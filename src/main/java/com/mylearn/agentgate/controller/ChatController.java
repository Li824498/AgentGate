package com.mylearn.agentgate.controller;

import com.mylearn.agentgate.entity.LRequest;
import com.mylearn.agentgate.entity.LResponse;
import com.mylearn.agentgate.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {
    @Autowired
    private AgentService agentService;

    @GetMapping
    public LResponse chatController(LRequest lRequest) {
        lRequest.setLMName("genimi");
        lRequest.setText("hello world!tell me, whats your name");
        lRequest.setUid_chat(1);
        lRequest.setUid_position(1);


        LResponse lResponse = agentService.chatService(lRequest);
        return lResponse;
    }

}
