package com.mylearn.agentgate.controller;

import com.mylearn.agentgate.core.entity.HistoryMessage;
import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import com.mylearn.agentgate.dto.ResultDTO;
import com.mylearn.agentgate.service.AgentService;
import com.mylearn.agentgate.service.HistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {
    @Autowired
    private AgentService agentService;

    @Autowired
    private HistoryService historyService;

    @PostMapping
    public ResultDTO syncNonStreamChatController(@RequestBody LRequest lRequest) {
        log.info(lRequest.toString());

        LResponse lResponse = agentService.syncNonStreamChatService(lRequest);
        return ResultDTO.success(lResponse);
    }

    @GetMapping("/{id}")
    public ResultDTO getHistoriesByChatId(@PathVariable("id") String chatId) {
        List<HistoryMessage> result = historyService.getHistoriesByChatId(chatId);
        System.out.println("yes");
        for (HistoryMessage historyMessage : result) {
            System.out.println(historyMessage.getContext());
        }
        return ResultDTO.success(result);
    }

    @PostMapping("/syncStream")
    public Flux<LResponse> syncStreamChatController(@RequestBody LRequest lRequest) throws IOException {
        Flux<LResponse> result = agentService.syncStreamChatService(lRequest);
        return result;
    }

}
