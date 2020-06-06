package com.zyh5games.controller;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.entity.Account;
import com.zyh5games.entity.GameNew;
import com.zyh5games.jedis.JedisRechargeCache;
import com.zyh5games.sdk.AccountWorker;
import com.zyh5games.sdk.LoginWorker;
import com.zyh5games.service.AccountService;
import com.zyh5games.service.GameNewService;
import com.zyh5games.util.ResponseUtil;
import com.zyh5games.util.StringUtils;
import com.zyh5games.util.UtilG;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/zhiyue")
public class ZyChannelController {
    private static final Logger log = Logger.getLogger(ZyChannelController.class);
    @Autowired
    JedisRechargeCache cache;
    @Resource
    AccountWorker accountWorker;
    @Autowired
    private HttpServletRequest request;
    @Resource
    private GameNewService gameNewService;
    @Resource
    private AccountService accountService;

    /**
     * 指悦账号一键注册
     *
     * @param jsonData int         appId         游戏id*<p>
     *                 int         channelId     渠道id*<p>
     *                 string      appKey        游戏秘钥*<p>
     *                 string      addParam      注释*<p>
     */
    @RequestMapping(value = "/autoReg", method = RequestMethod.POST)
    @ResponseBody
    public void autoReg(@RequestBody String jsonData, HttpServletResponse response) throws Exception {
        log.info("start: /zhiyue/autoRegister\t" + jsonData);

        JSONObject reqJson = JSONObject.parseObject(jsonData);
        JSONObject result = new JSONObject();

        do {
            String[] mustKey = {"appId", "channelId", "appKey"};
            for (String key : mustKey) {
                if (!reqJson.containsKey(key) || StringUtils.isBlank(reqJson.get(key))) {
                    result.put("message", "参数非法:" + key + "为空");
                    result.put("status", false);
                    break;
                }
            }
            int appId = reqJson.getInteger("appId");
            int channelId = reqJson.getInteger("channelId");
            String appKey = reqJson.getString("appKey");

            GameNew gameNew = gameNewService.selectGame(appId, -1);
            if (gameNew == null) {
                result.put("message", "游戏不存在");
                result.put("status", false);
                break;
            }
            if (!gameNew.getSecertKey().equals(appKey)) {
                result.put("message", "秘钥不存在");
                result.put("status", false);
                break;
            }

            reqJson.put("ip", UtilG.getIpAddress(request));

            Account account = accountWorker.zhiyueRegister(reqJson);

            if (account.getId() > 0) {
                result.put("status", true);
                result.put("message", "注册成功");

                result.put("zhiyueUid", account.getId());
                result.put("account", account.getName());
                result.put("password", account.getPwd());
                result.put("channelUid", account.getChannelUserId());
            } else {
                result.put("status", false);
                result.put("message", "注册失败");
            }
        } while (false);


        ResponseUtil.write(response, result);
        log.info("end: /zhiyue/autoRegister\t" + result.toString());
    }

    /**
     * 指悦账号一键注册
     *
     * @param jsonData int         appId         游戏id*<p>
     *                 int         channelId     渠道id*<p>
     *                 string      appKey        游戏秘钥*<p>
     *                 string      username      用户名*<p>
     *                 string      password      用户密码*<p>
     */
    @RequestMapping(value = "/autoLogin", method = RequestMethod.POST)
    @ResponseBody
    public void autoLogin(@RequestBody String jsonData, HttpServletResponse response) throws Exception {
        log.info("start: /zhiyue/autoLogin\t" + jsonData);

        JSONObject reqJson = JSONObject.parseObject(jsonData);
        JSONObject result = new JSONObject();

        do {
            String[] mustKey = {"appId", "channelId", "appKey", "username", "password"};
            for (String key : mustKey) {
                if (!reqJson.containsKey(key) || StringUtils.isBlank(reqJson.get(key))) {
                    result.put("message", "参数非法:" + key + "为空");
                    result.put("status", false);
                    break;
                }
            }
            int appId = reqJson.getInteger("appId");
            int channelId = reqJson.getInteger("channelId");
            String appKey = reqJson.getString("appKey");
            String username = reqJson.getString("username");
            String password = reqJson.getString("password");

            GameNew gameNew = gameNewService.selectGame(appId, -1);
            if (gameNew == null) {
                result.put("message", "游戏不存在");
                result.put("status", false);
                break;
            }
            if (!gameNew.getSecertKey().equals(appKey)) {
                result.put("message", "秘钥不存在");
                result.put("status", false);
                break;
            }

            Account account = accountService.findAccountByname(username);
            if (account == null) {
                result.put("status", false);
                result.put("message", "登录失败，账号不存在");
                break;
            }
            if (!account.getPwd().equals(password)) {
                result.put("status", false);
                result.put("message", "登录失败，密码错误");
                break;
            }
            String channelUid = account.getChannelUserId();
            // http://localhost:8080/login/jlzg.html?GameId=14&GameKey=u6d3047qbltix34a9l0g2bvs5e8q82ol
            StringBuilder loginUrl = new StringBuilder(gameNew.getLoginUrl());
            loginUrl.append("GameId").append("=").append(appId);
            loginUrl.append("&").append("GameKey").append("=").append(appKey);
            loginUrl.append("&").append("ChannelCode").append("=").append(channelId);
            loginUrl.append("&").append("ChannelUid").append("=").append(channelUid);
            result.put("status", true);
            result.put("message", "登录成功");
            result.put("channelUid", channelUid);
            result.put("loginUrl", loginUrl);
        } while (false);


        ResponseUtil.write(response, result);
        log.info("end: /zhiyue/autoLogin\t" + result.toString());
    }


}
