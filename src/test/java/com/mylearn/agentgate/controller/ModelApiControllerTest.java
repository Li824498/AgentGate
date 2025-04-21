package com.mylearn.agentgate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mylearn.agentgate.dto.ModelAndApiDTO;
import com.mylearn.agentgate.dto.ResultDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(ModelApiController.class)
@SpringBootTest
@AutoConfigureMockMvc
class ModelApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // todo?????

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSaveModel() throws Exception {
        ModelAndApiDTO modelAndApiDTO = new ModelAndApiDTO();
        modelAndApiDTO.setApi("test2");
        modelAndApiDTO.setModel("claude");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/model/save")
                        .header("token", "testUserToken")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(modelAndApiDTO)))
                .andExpect(status().isOk());
    }

    @Test
    public void testQueryModels() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/model/query")
                .header("token", "testUserToken")).andDo(print());

    }


}