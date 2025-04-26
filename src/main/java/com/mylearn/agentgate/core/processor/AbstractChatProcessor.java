package com.mylearn.agentgate.core.processor;

import com.mylearn.agentgate.core.entity.*;
import com.mylearn.agentgate.utils.UserIdUtils;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 核心会话实体
 */
@Component
public abstract class AbstractChatProcessor {

    private final static ThreadPoolExecutor streamPool = new ThreadPoolExecutor(
            16,
            32,
            10,
            TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(64),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );
    private static final Logger log = LoggerFactory.getLogger(AbstractChatProcessor.class);


    @Autowired
    private StringRedisTemplate redisTemplate;





    /**
     * 同步非流式传输模式
     * 新建聊天之后
     * @param lRequest
     * @return
     */
    // todo 事务bug
    @Transactional
    public LResponse syncNonStreamChatProcess(LRequest lRequest) {
        // todo 什么设计模式？？？？

        List<HistoryMessage> history = historyBefore(lRequest);
        chatMetaBefore(lRequest);
        Prompt prompt = prompt(lRequest);
        RoleCard roleCard = roleCard(lRequest);
        List<String> worldBookMessages = worldBook(lRequest);

        // todo 以后要一步架构怎么搞？
        LResponse lResponse = transferAi(lRequest, history, prompt, roleCard, worldBookMessages);

        historyAfter(lResponse);
        chatMetaAfter(lRequest, lResponse);

        return lResponse;
    }

    abstract List<HistoryMessage> historyBefore(LRequest lRequest);
    abstract void historyAfter(LResponse lResponse);
    abstract void chatMetaBefore(LRequest lRequest);
    abstract void chatMetaAfter(LRequest lRequest, LResponse lResponse);
    abstract Prompt prompt(LRequest lRequest);
    abstract RoleCard roleCard(LRequest lRequest);
    abstract List<String> worldBook(LRequest lRequest);
    abstract LResponse transferAi(LRequest lRequest, List<HistoryMessage> history, Prompt prompt, RoleCard roleCard, List<String> worldBookMessages);

    public Flux<LResponse> syncStreamChatProcess(LRequest lRequest) throws IOException {
        List<HistoryMessage> history = historyBefore(lRequest);
        chatMetaBefore(lRequest);
        Prompt prompt = prompt(lRequest);
        RoleCard roleCard = roleCard(lRequest);
        List<String> worldBookMessages = worldBook(lRequest);

        String bucketName = "syncStream:bucket:" + UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(bucketName, "");
        redisTemplate.opsForValue().set(bucketName + "Sync", "SYNC");
        Flux<LResponse> lResponseFlux = transferAiStream(lRequest, history, prompt, roleCard, worldBookMessages, bucketName);

        streamPool.submit(new Runnable() {
            @Override
            public void run() {
                log.info("子线程启动！");
                while (true) {
                    if (redisTemplate.opsForValue().setIfAbsent(bucketName + "Sync", "SYNC")) {
                        log.info("枷锁陈工！");
                        break;
                    }
                    try {
                        log.info("失败重试！");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

                String context = redisTemplate.opsForValue().getAndDelete(bucketName);
                System.out.println(context);
                LResponse lResponse = new LResponse();
                lResponse.setContext(context);
                lResponse.setUserId(lRequest.getUserId());
                lResponse.setChatId(lRequest.getChatId());
                lResponse.setMsgIndex(lRequest.getMsgIndex() + 1);

                historyAfter(lResponse);

                redisTemplate.opsForValue().getAndDelete(bucketName + "Sync");
            }
        });

        // todo 来个适配器模式兄弟
        LResponse lResponseSum = new LResponse();
        lResponseSum.setContext("等待更新中");
        lResponseSum.setUserId(UserIdUtils.getUserId());
        lResponseSum.setChatId(lRequest.getChatId());
        lResponseSum.setMsgIndex(lRequest.getMsgIndex() + 1);

        chatMetaAfter(lRequest, lResponseSum);

        return lResponseFlux;
    }

    abstract Flux<LResponse> transferAiStream(LRequest lRequest, List<HistoryMessage> history, Prompt prompt, RoleCard roleCard, List<String> worldBookMessages, String bucketName) throws IOException;

}
