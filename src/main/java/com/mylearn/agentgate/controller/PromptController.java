package com.mylearn.agentgate.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mylearn.agentgate.core.entity.Prompt;
import com.mylearn.agentgate.dto.PromptDTO;
import com.mylearn.agentgate.dto.ResultDTO;
import com.mylearn.agentgate.service.PromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/prompts")
public class PromptController {

    @Autowired
    private PromptService promptService;

    @PostMapping
    public ResultDTO createPrompt(@RequestBody PromptDTO promptDTO) {
        promptService.save(promptDTO.getName(), promptDTO.getUserId(), promptDTO.getPrompt());
        return ResultDTO.successWithEmpty();
    }

    @GetMapping
    public ResultDTO getAllPrompts() {
        List<Prompt> result = promptService.selectAllPrompts();
        return ResultDTO.success(result);
    }

    @PostMapping("/upload/file")
    public ResultDTO uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        String json = new String(file.getBytes(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        PromptDTO promptDTO = objectMapper.readValue(json, PromptDTO.class);

        promptService.save(promptDTO.getName(), promptDTO.getUserId(), promptDTO.getPrompt());
        return ResultDTO.successWithEmpty();
    }
}
