package com.mylearn.agentgate.controller;

import com.mylearn.agentgate.dto.LoginDTO;
import com.mylearn.agentgate.dto.ResultDTO;
import com.mylearn.agentgate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 登录接口（仅仅身份隔离）
     * @param loginDTO
     * @return
     */

    // todo 登录优化，怎么他妈的六秒
    @PostMapping("/login")
    public ResultDTO<String> login(@RequestBody LoginDTO loginDTO) {
        String userId = loginDTO.getUserId();
        String password = loginDTO.getPassword();
        String token = userService.login(userId, password);
//        return ResultDTO.success(token);
        if (token == null) {
            return ResultDTO.fail("登录失败");
        } else {
            return ResultDTO.success(token);
        }
    }

    @GetMapping("/testInterceptor")
    public ResultDTO<String> testInterceptor() {
        return ResultDTO.success("ok");
    }

}
