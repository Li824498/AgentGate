package com.mylearn.agentgate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mylearn.agentgate.dto.RenderDTO;
import com.mylearn.agentgate.dto.ResultDTO;
import com.mylearn.agentgate.service.RenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 渲染组件
 */
@RestController
@RequestMapping("/api/render")
public class RenderController {
    @Autowired
    private RenderService renderService;

    @GetMapping
    public ResultDTO getAllRenders() {
        List<RenderDTO> renderDTOList = renderService.selectAllRenders();
        return ResultDTO.success(renderDTOList);
    }

    @PostMapping("/upload/file")
    public ResultDTO uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileString = new String(file.getBytes(), StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        RenderDTO renderDTO = objectMapper.readValue(fileString, RenderDTO.class);
        renderService.insert(renderDTO);
        return ResultDTO.successWithEmpty();
    }
}
