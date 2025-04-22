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
    public ResultDTO chatController(@RequestBody LRequest lRequest) {
        log.info(lRequest.toString());

        LResponse lResponse = agentService.chatService(lRequest);
        return ResultDTO.success(lResponse);
    }

    @GetMapping("/{id}")
    public ResultDTO getHistoriesByChatId(@PathVariable("id") String chatId) {
        List<HistoryMessage> result = historyService.getHistoriesByChatId(chatId);
        return ResultDTO.success(result);
    }

}
