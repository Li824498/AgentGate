package com.mylearn.agentgate.controller;

import com.mylearn.agentgate.dto.ModelAndApiDTO;
import com.mylearn.agentgate.dto.ResultDTO;
import com.mylearn.agentgate.service.ModelApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/model")
public class ModelApiController {

    @Autowired
    private ModelApiService modelService;

    @PostMapping("/save")
    public ResultDTO saveModel(@RequestBody ModelAndApiDTO modelAndApiDTO) {
        String model = modelAndApiDTO.getModel();
        String api = modelAndApiDTO.getApi();
        modelService.insertModelAndApi(modelAndApiDTO.getModel(), modelAndApiDTO.getApi());

        return ResultDTO.successWithEmpty();
    }

    @GetMapping("/query")
    public ResultDTO queryModels() {
        List<String> result = modelService.selectAllModels();
        return ResultDTO.success(result);
    }
}
