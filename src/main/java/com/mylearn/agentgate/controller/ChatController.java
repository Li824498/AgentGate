package com.mylearn.agentgate.controller;

import com.mylearn.agentgate.core.entity.HistoryMessage;
import com.mylearn.agentgate.core.entity.HistoryRendered;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import com.mylearn.agentgate.dto.HistoryDTO;
import com.mylearn.agentgate.dto.ResultDTO;
import com.mylearn.agentgate.service.AgentService;
import com.mylearn.agentgate.service.HistoryRenderedService;
import com.mylearn.agentgate.service.HistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {
    @Autowired
    private AgentService agentService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private HistoryRenderedService historyRenderedService;

    @PostMapping
    public ResultDTO syncNonStreamChatController(@RequestBody LRequest lRequest) {
        log.info(lRequest.toString());

        LResponse lResponse = agentService.syncNonStreamChatService(lRequest);
        return ResultDTO.success(lResponse);
    }

    @GetMapping("/{id}")
    public ResultDTO<List<HistoryDTO>> getHistoriesByChatId(@PathVariable("id") String chatId) {
        // todo 先写死，以后再改，啊啊啊啊啊
        LocalDateTime dateTime = LocalDateTime.of(2025, 5, 4, 18, 30, 33);
        List<HistoryMessage> historyMessageList = historyService.getHistoriesByChatId(chatId, dateTime);

        System.out.println("yes");
        for (HistoryMessage historyMessage : historyMessageList) {
            System.out.println(historyMessage.getContext());
        }
        List<Long> historyIds = historyMessageList.stream().map(historyMessage -> historyMessage.getId()).collect(Collectors.toList());

        List<HistoryRendered> historyRenderedList = historyRenderedService.batchGetByHistoryIds(historyIds);
        Map<Long, List<HistoryRendered>> hrListMap = historyRenderedList.stream().collect(Collectors.groupingBy(historyRendered -> historyRendered.getHistoryId()));

        List<HistoryDTO> result = historyMessageList.stream().map(historyMessage -> {
            HistoryDTO historyDTO = new HistoryDTO();
            historyDTO.setHistoryMessage(historyMessage);
            historyDTO.setHistoryRenderedList(hrListMap.get(historyMessage.getId()));
            return historyDTO;
        }).collect(Collectors.toList());

        return ResultDTO.success(result);
    }

    @PostMapping("/syncStream")
    public Flux<LResponse> syncStreamChatController(@RequestBody LRequest lRequest) throws IOException {
        Flux<LResponse> result = agentService.syncStreamChatService(lRequest);
        return result;
    }


}
