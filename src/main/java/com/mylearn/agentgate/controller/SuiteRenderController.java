package com.mylearn.agentgate.controller;

import com.mylearn.agentgate.dto.ResultDTO;
import com.mylearn.agentgate.dto.SuiteRenderDTO;
import com.mylearn.agentgate.service.SuiteRenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 渲染套件
 */
@RestController
@RequestMapping("/api/suiteRender")
public class SuiteRenderController {
    @Autowired
    private SuiteRenderService suiteRenderService;

    /**
     * 查询所有渲染套件
     * @return
     */
    @GetMapping
    public ResultDTO getAllSuiteRenders() {
        List<SuiteRenderDTO> suiteRenderDTOList = suiteRenderService.getAllSuiteRenders();
        return ResultDTO.success(suiteRenderDTOList);
    }

    /**
     * 新增新的渲染套件
     * @param suiteRenderDTO
     * @return
     */
    @PostMapping
    public ResultDTO saveSuiteRender(@RequestBody SuiteRenderDTO suiteRenderDTO) {
        suiteRenderService.save(suiteRenderDTO);
        return ResultDTO.successWithEmpty();
    }
}
