package com.shop.controller;

import com.shop.common.BaseResponse;
import com.shop.common.ResultUtils;
import com.shop.model.vo.LoginUserVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class test {
    @GetMapping("/hello")
    public BaseResponse<LoginUserVO> hello() {
        LoginUserVO loginUserVO = new LoginUserVO();
        return ResultUtils.success(loginUserVO);
    }
}
