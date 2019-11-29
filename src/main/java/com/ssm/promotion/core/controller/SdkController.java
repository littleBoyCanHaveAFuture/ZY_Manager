package com.ssm.promotion.core.controller;

import com.ssm.promotion.core.jedis.JedisRechargeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * @author song minghua
 * @date 2019/11/27
 */
@Controller
@RequestMapping("/hysdk")
public class SdkController {
    @Autowired
    JedisRechargeCache cache;

    /**
     * SDK 登录接口
     */
    @RequestMapping(value = "zyLogin", method = RequestMethod.GET)
    public void sdk_login(HttpServletRequest request) {


    }
}
