package com.mylearn.agentgate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mylearn.agentgate.core.entity.RoleCard;
import com.mylearn.agentgate.dto.ResultDTO;
import com.mylearn.agentgate.dto.RoleCardDTO;
import com.mylearn.agentgate.service.RoleCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/roleCard")
public class RoleCardController {
    @Autowired
    private RoleCardService roleCardService;

    @GetMapping
    public ResultDTO getAllRoleCards() {
        List<RoleCard> result = roleCardService.selectAllRoleCards();
        return ResultDTO.success(result);
    }

    @PostMapping
    public ResultDTO createRoleCard(@RequestBody RoleCardDTO roleCardDTO) {
        roleCardService.insertRoleCard(roleCardDTO.getName(),
                roleCardDTO.getUserId(),
                roleCardDTO.getDescription(),
                roleCardDTO.getAvatarUrl(),
                roleCardDTO.getStartText(),
                roleCardDTO.getSettingText());

        return ResultDTO.successWithEmpty();
    }

    @PostMapping("/upload/file")
    public ResultDTO uploadFile(@RequestParam("file") MultipartFile file) throws Exception {
        String json = new String(file.getBytes(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        RoleCardDTO roleCardDTO = objectMapper.readValue(json, RoleCardDTO.class);

        roleCardService.insertRoleCard(roleCardDTO.getName(),
                roleCardDTO.getUserId(),
                roleCardDTO.getDescription(),
                roleCardDTO.getAvatarUrl(),
                roleCardDTO.getStartText(),
                roleCardDTO.getSettingText());

        return ResultDTO.successWithEmpty();
    }
}
