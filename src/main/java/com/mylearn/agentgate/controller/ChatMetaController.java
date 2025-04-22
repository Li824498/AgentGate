package com.mylearn.agentgate.controller;

import com.mylearn.agentgate.core.entity.ChatMeta;
import com.mylearn.agentgate.dto.ResultDTO;
import com.mylearn.agentgate.service.ChatMetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chatMeta")
public class ChatMetaController {

    @Autowired
    private ChatMetaService chatMetaService;

    @GetMapping
    public ResultDTO getAllChatMetas() {
        List<ChatMeta> result = chatMetaService.selectAllChatMetas();
        return ResultDTO.success(result);
    }


}
