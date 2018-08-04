package com.imooc.miaosha.controller;

import com.imooc.miaosha.result.Result;
import com.imooc.miaosha.service.MiaoshaUserService;
import com.imooc.miaosha.vo.LoginVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RequestMapping("/login")
@RestController
@Slf4j
public class LoginController {

    @Autowired
    private MiaoshaUserService userService;

    @RequestMapping("/do_login")
    public Result<Boolean> doLogin(@Valid LoginVo loginVo, HttpServletResponse response){
        userService.login(response,loginVo);
        return Result.success(true);
    }
}
