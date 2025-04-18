package com.mylearn.agentgate.controller;

import com.mylearn.agentgate.core.entity.LRequest;
import com.mylearn.agentgate.core.entity.LResponse;
import com.mylearn.agentgate.service.AgentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@Slf4j
public class ChatController {
    @Autowired
    private AgentService agentService;

    @PostMapping
    public LResponse chatController(@RequestBody LRequest lRequest) {
        log.info(lRequest.toString());

        LResponse lResponse = agentService.chatService(lRequest);
        return lResponse;
    }

}
