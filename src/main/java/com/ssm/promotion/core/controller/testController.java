package com.ssm.promotion.core.controller;

import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author song minghua
 * @date 2020/5/14
 */
//
@Controller
@RequestMapping("/test")
public class testController {
    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getGameInfo() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "test");
        return jsonObject;
    }
}
