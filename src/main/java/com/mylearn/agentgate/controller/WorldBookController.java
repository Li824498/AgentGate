package com.mylearn.agentgate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mylearn.agentgate.dto.ResultDTO;
import com.mylearn.agentgate.dto.WorldBookDTO;
import com.mylearn.agentgate.service.WorldBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/worldBook")
public class WorldBookController {
    @Autowired
    private WorldBookService worldBookService;

    @PostMapping("/upload/file")
    public ResultDTO uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        worldBookService.uploadFile(file);
        return ResultDTO.successWithEmpty();
    }

    @GetMapping
    public ResultDTO getAllWorldBooks() {
        List<WorldBookDTO> result = worldBookService.selectAllWorldBooks();
        return ResultDTO.success(result);
    }
}
