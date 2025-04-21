package com.mylearn.agentgate.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mylearn.agentgate.dto.RoleCardDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
class RoleCardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllRoleCards() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/roleCard")
                .header("token", "testUserToken"))
                .andDo(print());
    }

    @Test
    void createRoleCard() throws Exception {
        RoleCardDTO roleCardDTO = new RoleCardDTO();
        roleCardDTO.setName("测试卡2");
        roleCardDTO.setSettingText("你是一个测试卡");
        roleCardDTO.setDescription("这就只是一张测试卡而已");
        roleCardDTO.setStartText("你好，我是一张测试卡，我怎么帮你？");
        roleCardDTO.setUserId("测试用户2号");
        roleCardDTO.setAvatarUrl("头像地址");

        ObjectMapper objectMapper = new ObjectMapper();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/roleCard")
                .header("token", "testUserToken")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(roleCardDTO))).andDo(print());
    }
}