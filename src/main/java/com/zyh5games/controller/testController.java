package com.zyh5games.controller;

import com.zyh5games.sdk.AccountWorker;
import com.zyh5games.sdk.LoginIdGenerator;
import com.zyh5games.sdk.UOrderManager;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * @author song minghua
 * @date 2020/5/14
 */
//
@Controller
@RequestMapping("/test")
public class testController {
    private static final Logger log = Logger.getLogger(testController.class);
    @Resource
    AccountWorker accountWorker;
    @Resource
    LoginIdGenerator loginIdGenerator;
    @Resource
    private UOrderManager orderManager;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    @ResponseBody
    public JSONObject getGameInfo() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "test");
        return jsonObject;
    }

    @RequestMapping(value = "/getId", method = RequestMethod.GET)
    @ResponseBody
    public Integer getNextId() throws Exception {
        return AccountWorker.getNextId();
    }

    @RequestMapping(value = "/genOrder", method = RequestMethod.GET)
    @ResponseBody
    public String getCpOrderId() throws Exception {
        return String.valueOf(loginIdGenerator.getRandomId());
    }

    @RequestMapping("/{id}")
    public String testPathVariable(@PathVariable("id") Integer id) {
        System.out.println("testPathVariable:" + id);
        return "SUCCESS";
    }

}
