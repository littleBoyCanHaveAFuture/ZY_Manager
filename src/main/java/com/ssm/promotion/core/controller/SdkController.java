package com.ssm.promotion.core.controller;

import com.ssm.promotion.core.jedis.JedisRechargeCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
    public void sdk_login(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        http://47.101.44.31:8080/hysdk/zyLogin?productCode=47260927223169296554508046219263&channelCode=4136
        response.sendRedirect("http://lh5ds.yy66game.com/index.php/SANG/Login/sgyx?");

    }
}
