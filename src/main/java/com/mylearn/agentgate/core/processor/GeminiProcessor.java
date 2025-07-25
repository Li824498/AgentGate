package com.mylearn.agentgate.core.processor;

import com.alibaba.fastjson.JSON;
import com.mylearn.agentgate.annoation.ModelType;
import com.mylearn.agentgate.core.domain.history.GeminiHistoryManager;
import com.mylearn.agentgate.core.domain.historyRendered.HistoryRenderedManager;
import com.mylearn.agentgate.core.domain.prompt.PromptManager;
import com.mylearn.agentgate.core.domain.render.RenderManager;
import com.mylearn.agentgate.core.domain.roleCard.RoleCardManager;
import com.mylearn.agentgate.core.domain.worldBook.WorldBookManager;
import com.mylearn.agentgate.core.entity.*;
import com.mylearn.agentgate.core.listener.HistoryMqMessage;
import com.mylearn.agentgate.exception.AgentException;
import com.mylearn.agentgate.service.ModelApiService;
import com.mylearn.agentgate.utils.HistoryIdUtils;
import com.mylearn.agentgate.utils.UserIdUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
@ModelType(value = "gemini")
public class GeminiProcessor extends AbstractChatProcessor {

    @Autowired
    private GeminiHistoryManager history;

    @Autowired
    private WorldBookManager worldBookManager;

    @Autowired
    private PromptManager promptManager;

    // todo 出bug了再改， 不出bug就不改，准备设计降级
    @Autowired
    private RoleCardManager roleCardManager;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private RenderManager renderManager;

    @Autowired
    private HistoryRenderedManager historyRenderedManager;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ModelApiService modelApiService;

    // todo 扛不住一点并发啊
    private static final ThreadPoolExecutor renderThreadPool = new ThreadPoolExecutor(
            16,
            32,
            10,
            TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(100),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );


    public static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:";
    public static final String NON_STREAM_PREFIX = "generateContent?key=";
    public static final List<String> GEMINI_VERSION_LIST = new ArrayList<>();

    static {
        GEMINI_VERSION_LIST.add("gemini-1.5-flash");
    }



    @Override
    List<HistoryMessage> historyBefore(LRequest lRequest) {
        List<HistoryMessage> historyMessages = history.processBefore(lRequest);
        return historyMessages;
    }


    @Override
    Prompt prompt(LRequest lRequest) {
        Prompt prompt = promptManager.process(lRequest);
        return prompt;
    }

    @Override
    RoleCard roleCard(LRequest lRequest) {
        return roleCardManager.process(lRequest);
    }

    @Override
    List<String> worldBook(LRequest lRequest) {
        return worldBookManager.process(lRequest);
    }

    @Override
    void historyAfter(LResponse lResponse) {
        history.processAfter(lResponse);
    }

    @Override
    void chatMetaBefore(LRequest lRequest) {
        history.chatMetaProcessBefore(lRequest);
    }

    @Override
    void chatMetaAfter(LRequest lRequest, LResponse lResponse) {
        history.chatMetaProcessAfter(lRequest, lResponse);
    }

    @Override
    LResponse transferAi(LRequest lRequest, List<HistoryMessage> history, Prompt prompt, RoleCard roleCard, List<String> worldBookMessages) {
        // todo 负载均衡设计 可能采用配置方式解决
//        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyAMwBIWE63VgdEmhu1FcDR4bCMUa2w7u0E";
        String apiUrl = GEMINI_URL + NON_STREAM_PREFIX + modelApiService.getChatApi("gemini", GEMINI_VERSION_LIST.getFirst());

        HttpEntity<Map<String, Object>> entity = null;

        entity = sendGemini(lRequest, history, prompt, roleCard, worldBookMessages);

        ResponseEntity<Map> response = null;
        try {
            response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
            if (response.getStatusCode().isError()) {
                throw new AgentException("模型调用失败，状态码：" + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new AgentException("调用AI服务异常", e);
        }

//        if (response.getStatusCode().isError()) throw new RuntimeException("模型调用失败");


        String text = getGeminiText(response);

        // todo 建造者模式优化
        LResponse lResponse = new LResponse();
        lResponse.setInContext(text);
        lResponse.setUserId(lRequest.getUserId());
        lResponse.setChatId(lRequest.getChatId());
        lResponse.setMsgIndex(lRequest.getMsgIndex() + 1);

        return lResponse;
    }

    @Override
    Flux<LResponse> transferAiStream(LRequest lRequest, List<HistoryMessage> history, Prompt prompt, RoleCard roleCard, List<String> worldBookMessages, String bucketName) throws IOException {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:streamGenerateContent?key=AIzaSyAMwBIWE63VgdEmhu1FcDR4bCMUa2w7u0E";

        RequestBody requestBody = sendGeminiStream(lRequest, history, prompt, roleCard, worldBookMessages);


        Request request = new Request.Builder()
                .url(apiUrl)
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        ResponseBody responseBody = response.body();


        Flux<LResponse> lResponseFlux = Flux.create(new Consumer<FluxSink<LResponse>>() {
            @Override
            public void accept(FluxSink<LResponse> fluxSink) {
                BufferedSource bufferedSource = responseBody.source();
                Mono.delay(Duration.ZERO)
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) {
                                    while (true) {
                                        try {
                                            if (bufferedSource.exhausted()) break;
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                        String line = null;
                                        try {
                                            line = bufferedSource.readUtf8Line();
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }


                                        if (line.startsWith("            \"text\": ")) {
                                            String substring = line.substring(21, line.length() - 1);

                                            redisTemplate.opsForValue().append(bucketName, substring);

                                            log.info(substring);


                                            LResponse lResponse = new LResponse();
                                            lResponse.setInContext(substring);
                                            lResponse.setUserId(UserIdUtils.getUserId());
                                            lResponse.setChatId(lRequest.getChatId());
                                            lResponse.setMsgIndex(lRequest.getMsgIndex() + 1);

                                            fluxSink.next(lResponse);
                                        }
                                    }

                                HistoryMqMessage historyMqMessage = new HistoryMqMessage();
                                historyMqMessage.setBucketName(bucketName);
                                historyMqMessage.setLRequest(lRequest);
                                log.info("发送了消息！" + historyMqMessage.toString());
                                rabbitTemplate.convertAndSend("history.stream.exchange", "historyStream", historyMqMessage);

                                fluxSink.complete();
                            }
                        });
            }
        });
        return lResponseFlux;
    }

    @Override
    public List<Render> renders(LRequest lRequest, String inContext) {
        List<Render> renders = renderManager.process(lRequest, inContext);
        return renders;
    }

    @Override
    public List<HistoryRendered> transferRenderAis(LRequest lRequest, LResponse lResponse, List<Render> renders) {
        String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=AIzaSyAMwBIWE63VgdEmhu1FcDR4bCMUa2w7u0E";

        Map<Future<ResponseEntity<Map>>, String> lfutureMap = new HashMap<>();

        for (Render render : renders) {
            Future<ResponseEntity<Map>> entityFuture = renderThreadPool.submit(new Callable<ResponseEntity<Map>>() {
                @Override
                public ResponseEntity<Map> call() throws Exception {
                    HttpEntity<Map<String, Object>> entity = sendGeminiRender(lResponse.getInContext(), render);

                    ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
                    return response;
                }
            });

            lfutureMap.put(entityFuture, render.getName());
        }

        List<HistoryRendered> historyRendereds = lfutureMap.keySet().stream().map(new Function<Future<ResponseEntity<Map>>, HistoryRendered>() {
            @Override
            public HistoryRendered apply(Future<ResponseEntity<Map>> responseEntityFuture) {
                ResponseEntity<Map> response = null;
                try {
                    response = responseEntityFuture.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                String text = getGeminiText(response);
                HistoryRendered historyRendered = new HistoryRendered();
                historyRendered.setMsgIndex(lRequest.getMsgIndex() + 1);
                historyRendered.setRenderType(lfutureMap.get(responseEntityFuture));
                historyRendered.setOutContext(getGeminiText(response));
                // todo 使用线程池
                historyRendered.setHistoryId(HistoryIdUtils.get());
                historyRendered.setCreateTime(LocalDateTime.now());
                historyRendered.setUserId(UserIdUtils.getUserId());
                return historyRendered;
            }
        }).collect(Collectors.toList());

        HistoryIdUtils.remove();

        return historyRendereds;



/*        lfutureList.stream().map(new Function<Future<ResponseEntity<Map>>, LResponse>() {
            @Override
            public LResponse apply(Future<ResponseEntity<Map>> responseEntityFuture) {
                ResponseEntity<Map> response = null;
                try {
                    response = responseEntityFuture.get();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                String text = getGeminiText(response);
                HistoryRendered historyRendered = new HistoryRendered();
                historyRendered.setMsgIndex(lRequest.getMsgIndex());
                historyRendered.setRenderType();
                return text;
            }
        }).collect()*/



/*        new Callable<ResponseEntity<Map>>() {
            @Override
            public ResponseEntity<Map> call() throws Exception {
                return null;
            }
        }*/

/*        // todo 线程池处理？排队？
        HttpEntity<Map<String, Object>> entity = sendGeminiRender(inContext, render);

        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);*/

//        String text = getGeminiText(response);
    }

    @Override
    public void historyRendered(LRequest lRequest, List<HistoryRendered> historyRenderedList) {
        historyRenderedManager.processAfters(lRequest, historyRenderedList);
    }

    private HttpEntity<Map<String, Object>> sendGeminiRender(String inContext, Render render) {
        String prompt = "请帮我把以下剧情映射为对应的格式，只输出对应的格式即可，多余的不要输出：";

        String text = prompt + "\n" + "剧情状态：\n" + inContext + "\n\n\n" + "格式：\n" + render.getText();

        Map<String, Object> part = Map.of("text", text);

        Map<String, Object> message = Map.of("parts", List.of(part));

        Map<String, Object> body = Map.of("contents", List.of(message));


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        return entity;
    }

    private RequestBody sendGeminiStream(LRequest lRequest, List<HistoryMessage> history, Prompt prompt, RoleCard roleCard, List<String> worldBookMessages) {
        // 1.user:prompt
        List<Map> contents = new ArrayList<>();

//        List<Map<String, String>> partsPrompt = List.of(Map.of("text", prompt == null ? "" : prompt.getText()));
        List<Map<String, String>> partsPrompt = List.of(Map.of("text", Optional.ofNullable(prompt).map(Prompt::getText).orElse("")));
        Map<String, Object> mapPrompt = new LinkedHashMap<>();
        mapPrompt.put("role", "user");
        mapPrompt.put("parts", partsPrompt);

        contents.add(mapPrompt);
        //2.model:角色卡开头
//        List<Map<String, String>> partsStartText = List.of(Map.of("test", roleCard == null ? "" : roleCard.getStartText()));
        List<Map<String, String>> partsStartText = List.of(Map.of("text", Optional.ofNullable(roleCard).map(RoleCard::getStartText).orElse("")));
        Map<String, Object> mapStartText = new LinkedHashMap<>();
        mapStartText.put("role", "model");
        mapStartText.put("parts", partsStartText);

        contents.add(mapStartText);
        //3.user-model:history
        for (HistoryMessage historyMessage : history) {
//            List<Map<String, String>> partsHistory = List.of(Map.of("text", historyMessage == null ? "" : historyMessage.getInContext()));
            List<Map<String, String>> partsHistory = List.of(Map.of("text", historyMessage.getContext()));

            Map<String, Object> mapHistory = new LinkedHashMap<>();
            mapHistory.put("role", historyMessage.getRole());
            mapHistory.put("parts", partsHistory);

            contents.add(mapHistory);
        }
        //4.user:
        StringBuffer textLastText = new StringBuffer();
        //4.1键入消息
        textLastText.append(lRequest.getContext());
        //4.2角色卡设定
        textLastText.append(Optional.ofNullable(roleCard).map(RoleCard::getSettingText).orElse(""));
        //4.3worldBook
        textLastText.append(worldBookMessages);

        List<Map<String, String>> partsLastText = List.of(Map.of("text", textLastText.toString()));
        Map<String, Object> mapLastText = new LinkedHashMap<>();
        mapLastText.put("role", "user");
        mapLastText.put("parts", partsLastText);

        contents.add(mapLastText);
        //5稳定输出
        //5.1model
        String textPlusModel = "推荐以下面的指令&剧情继续：\n" + lRequest.getContext();
        List<Map<String, String>> partsPlusModel = List.of(Map.of("text", textPlusModel));
        Map<String, Object> mapPlusModel = new LinkedHashMap<>();
        mapPlusModel.put("role", "model");
        mapPlusModel.put("parts", partsPlusModel);

        contents.add(mapPlusModel);
        //5.2user
        String textPlusUser = "继续";
        List<Map<String, String>> partsPlusUser = List.of(Map.of("text", textPlusUser));
        Map<String, Object> mapPlusUser = new LinkedHashMap<>();
        mapPlusUser.put("role", "model");
        mapPlusUser.put("parts", partsPlusUser);

        contents.add(mapPlusUser);


        Map<String, Object> body = Map.of("contents", contents);

        String jsonString = JSON.toJSONString(body);
        okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8");

        RequestBody requestBody = RequestBody.create(jsonString, mediaType);
        return requestBody;
    }

    private HttpEntity<Map<String, Object>> sendGemini(LRequest lRequest, List<HistoryMessage> history, Prompt prompt, RoleCard roleCard, List<String> worldBookMessages) {
        // 1.user:prompt
        List<Map> contents = new ArrayList<>();

//        List<Map<String, String>> partsPrompt = List.of(Map.of("text", prompt == null ? "" : prompt.getText()));
        List<Map<String, String>> partsPrompt = List.of(Map.of("text", Optional.ofNullable(prompt).map(Prompt::getText).orElse("")));
        Map<String, Object> mapPrompt = new LinkedHashMap<>();
        mapPrompt.put("role", "user");
        mapPrompt.put("parts", partsPrompt);

        contents.add(mapPrompt);
        //2.model:角色卡开头
//        List<Map<String, String>> partsStartText = List.of(Map.of("test", roleCard == null ? "" : roleCard.getStartText()));
        List<Map<String, String>> partsStartText = List.of(Map.of("text", Optional.ofNullable(roleCard).map(RoleCard::getStartText).orElse("")));
        Map<String, Object> mapStartText = new LinkedHashMap<>();
        mapStartText.put("role", "model");
        mapStartText.put("parts", partsStartText);

        contents.add(mapStartText);
        //3.user-model:history
        for (HistoryMessage historyMessage : history) {
//            List<Map<String, String>> partsHistory = List.of(Map.of("text", historyMessage == null ? "" : historyMessage.getInContext()));
            List<Map<String, String>> partsHistory = List.of(Map.of("text", historyMessage.getContext()));

            Map<String, Object> mapHistory = new LinkedHashMap<>();
            mapHistory.put("role", historyMessage.getRole());
            mapHistory.put("parts", partsHistory);

            contents.add(mapHistory);
        }
        //4.user:
        StringBuffer textLastText = new StringBuffer();
        //4.1键入消息
        textLastText.append(lRequest.getContext());
        //4.2角色卡设定
        textLastText.append(Optional.ofNullable(roleCard).map(RoleCard::getSettingText).orElse(""));
        //4.3worldBook
        textLastText.append(worldBookMessages);

        List<Map<String, String>> partsLastText = List.of(Map.of("text", textLastText.toString()));
        Map<String, Object> mapLastText = new LinkedHashMap<>();
        mapLastText.put("role", "user");
        mapLastText.put("parts", partsLastText);

        contents.add(mapLastText);
        //5稳定输出
        //5.1model
        String textPlusModel = "推荐以下面的指令&剧情继续：\n" + lRequest.getContext();
        List<Map<String, String>> partsPlusModel = List.of(Map.of("text", textPlusModel));
        Map<String, Object> mapPlusModel = new LinkedHashMap<>();
        mapPlusModel.put("role", "model");
        mapPlusModel.put("parts", partsPlusModel);

        contents.add(mapPlusModel);
        //5.2user
        String textPlusUser = "继续";
        List<Map<String, String>> partsPlusUser = List.of(Map.of("text", textPlusUser));
        Map<String, Object> mapPlusUser = new LinkedHashMap<>();
        mapPlusUser.put("role", "user");
        mapPlusUser.put("parts", partsPlusUser);

        contents.add(mapPlusUser);


        Map<String, Object> body = Map.of("contents", contents);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        return entity;
    }

    private HttpEntity<Map<String, Object>> sendGeminiTextWithHistoryAndPrompt(LRequest lRequest, List<HistoryMessage> history, Prompt prompt) {
        List<Map> contents = new ArrayList<>();

        List<Map<String, String>> parts1 = List.of(Map.of("text", prompt.getText()));
        Map<String, Object> map1 = new HashMap<>();
        map1.put("role", "user");
        map1.put("parts", parts1);

        contents.add(map1);

        for (HistoryMessage historyMessage : history) {
            List<Map<String, String>> parts2 = List.of(Map.of("text", historyMessage.getContext()));

            Map<String, Object> map2 = new LinkedHashMap<>();
            map2.put("role", historyMessage.getRole());
            map2.put("parts", parts2);

            contents.add(map2);
        }

        List<Map<String, String>> partsNow = new ArrayList<>();
        partsNow.add(Map.of("text", lRequest.getContext()));

        Map<String, Object> mapNow = new LinkedHashMap<>();
        mapNow.put("role", "user");
        mapNow.put("parts", partsNow);

        contents.add(mapNow);

        Map<String, Object> body = Map.of("contents", contents);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        return entity;


    }

    private HttpEntity<Map<String, Object>> sendGeminiTextWithHistory(LRequest lRequest, List<HistoryMessage> history) {
        List<Map> contents = new ArrayList<>();
        // todo Map有上限，修掉这个bug
        for (HistoryMessage historyMessage : history) {
            List<Map<String, String>> parts = List.of(Map.of("text", historyMessage.getContext()));

            Map<String, Object> map = new LinkedHashMap<>();
            map.put("role", historyMessage.getRole());
            map.put("parts", parts);

            contents.add(map);
        }

        List<Map<String, String>> partsNow = new ArrayList<>();
        partsNow.add(Map.of("text", lRequest.getContext()));

        Map<String, Object> mapNow = new LinkedHashMap<>();
        mapNow.put("role", "user");
        mapNow.put("parts", partsNow);

        contents.add(mapNow);

        Map<String, Object> body = Map.of("contents", contents);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        return entity;
    }


    public static String getGeminiText(ResponseEntity<Map> response) {
        Map<String, List> responseBody = response.getBody();
        List<Map> candidates = responseBody.get("candidates");
        Map<String, Map> firstCandidate = candidates.get(0);
        Map<String, List> contents = firstCandidate.get("content");
        List<Map> parts = contents.get("parts");
        Map<String, String> firstPart = parts.get(0);
        String text = firstPart.get("text");

        return text;
    }

    // todo 重新设计兼容三大模块
    public static HttpEntity<Map<String, Object>> sendGeminiText(LRequest lRequest) {
        Map<String, Object> part = Map.of("text", lRequest.getContext());

        Map<String, Object> message = Map.of("parts", List.of(part));

        Map<String, Object> body = Map.of("contents", List.of(message));


        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, httpHeaders);

        return entity;
    }
}
