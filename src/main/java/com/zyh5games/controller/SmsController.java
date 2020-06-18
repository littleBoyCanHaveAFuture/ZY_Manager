package com.zyh5games.controller;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.entity.Account;
import com.zyh5games.entity.GameNew;
import com.zyh5games.jedis.JedisRechargeCache;
import com.zyh5games.jedis.RedisKey_Gen;
import com.zyh5games.sdk.AccountWorker;
import com.zyh5games.sdk.GameWorker;
import com.zyh5games.sdk.channel.HttpService;
import com.zyh5games.service.AccountService;
import com.zyh5games.service.GameNewService;
import com.zyh5games.util.RandomUtil;
import com.zyh5games.util.ResponseUtil;
import com.zyh5games.util.StringUtils;
import com.zyh5games.util.UtilG;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author song minghua
 * @date 2019/11/27
 */
@Controller
@RequestMapping("/zhiYueSms")
public class SmsController {
    private static final Logger log = Logger.getLogger(SmsController.class);
    /**
     * 判断手机号
     */
    public static Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
    /**
     * 注册码cd 60s
     */
    public static Integer regExpireTime = 60 * 1000;
    /**
     * 登录码cd 60s
     */
    public static Integer loginExpireTime = 60 * 1000;
    /**
     * 注册码 有效时间 10分钟
     */
    public static Integer regTime = 10 * 60;
    /**
     * 登录码 有效时间 10分钟
     */
    public static Integer loginTime = 10 * 60;

    /**
     * sms 注册验证码
     */
    public static String regSmsUrl = "https://www.zyh5games.com/sms/s5oE7p4JuIqnn5jB";
    /**
     * sms 登录验证码
     */
    public static String loginSmsUrl = "https://www.zyh5games.com/sms/459wf5sls15n5opd";

    @Autowired
    JedisRechargeCache cache;
    @Autowired
    HttpService httpService;
    @Autowired
    GameWorker gameWorker;
    @Resource
    AccountWorker accountWorker;
    @Autowired
    private HttpServletRequest request;
    @Resource
    private AccountService accountService;
    @Resource
    private GameNewService gameNewService;

    /**
     * 判断是否合法手机号
     */
    public static boolean isMobileNumber(String mobiles) {
        Matcher m = p.matcher(mobiles);
        System.out.println(m.matches() + "---" + mobiles);
        return m.matches();
    }

    public static boolean checkPhone(String phone, JSONObject rspJson) {
        if (!isMobileNumber(phone)) {
            rspJson.put("status", false);
            rspJson.put("message", "请输入合法的手机号");
            return false;
        }
        return true;
    }

    /**
     * 注册、登录 cd 60s 才能请求一次
     */
    public boolean checkTime(String key, JSONObject rspJson, Integer type) {
        String lastTime = cache.getSmsString(key);
        long time = 0L;
        int expireTime = 0;
        if (type == 1) {
            time = regExpireTime;
            expireTime = regExpireTime / 1000;
        } else if (type == 2) {
            time = loginExpireTime;
            expireTime = loginExpireTime / 1000;
        }
        //是否cd中
        if (!lastTime.isEmpty()) {
            long lt = Long.parseLong(lastTime);
            long curr = System.currentTimeMillis();
            if (curr - lt < time) {
                long left = time - (curr - lt);
                rspJson.put("status", false);
                rspJson.put("message", "处于cd 请等待" + left / 1000 + "s 再试");
                return false;
            }
        } else {
            //无cd
            cache.setSmsKey(key, expireTime, String.valueOf(System.currentTimeMillis()));
        }
        return true;
    }

    /**
     * 手机请求注册验证码
     */
    @RequestMapping(value = "/PhoneCodeReg", method = RequestMethod.GET)
    @ResponseBody
    public void sdkPhoneRegister(@RequestParam("phone") String phone,
                                 @RequestParam("appId") String appId,
                                 HttpServletResponse response) throws Exception {
        log.info("start: /webGame2/PhoneCodeReg\t");
        JSONObject rspJson = new JSONObject();
        do {
            if (!checkPhone(phone, rspJson)) {
                break;
            }
            String key = RedisKey_Gen.get_SmsRegisterTimeKey(appId, phone);
            if (!checkTime(key, rspJson, 1)) {
                break;
            }

            if (accountWorker.existAccount(phone)) {
                rspJson.put("message", "账号已存在");
                rspJson.put("status", false);
                break;
            }

            int code = RandomUtil.rndInt(1000, 9999);
            String keyCode = RedisKey_Gen.get_SmsRegisterKey(appId, phone);
            cache.setSmsKey(keyCode, regTime, String.valueOf(code));
            String url = regSmsUrl + "?phone=" + phone + "&code=" + code;

            JSONObject rsp = httpService.httpGetJsonNo(url);
            if (rsp.containsKey("data")) {
                String data = rsp.getString("data");
                JSONObject jsData = JSONObject.parseObject(data);
                if (jsData.containsKey("Message") && "OK".equals(jsData.getString("Message"))) {
                    rspJson.put("status", true);
                    rspJson.put("message", "获取验证码成功");
                    break;
                }
            }
            rspJson.put("status", false);
            rspJson.put("message", "获取验证码失败");
        } while (false);


        ResponseUtil.write(response, rspJson);
        log.info("end: /webGame2/register\t" + rspJson.toString());
    }

    /**
     * 手机请求登录验证码
     */
    @RequestMapping(value = "/PhoneCodeLogin", method = RequestMethod.GET)
    @ResponseBody
    public void sdkPhoneLogin(@RequestParam("phone") String phone,
                              @RequestParam("appId") String appId,
                              HttpServletResponse response) throws Exception {
        log.info("start: /zhiYueSms/PhoneCodeLogin\t");
        JSONObject rspJson = new JSONObject();
        do {
            if (!checkPhone(phone, rspJson)) {
                break;
            }
            String key = RedisKey_Gen.get_SmsLoginTimeKey(appId, phone);
            if (!checkTime(key, rspJson, 2)) {
                break;
            }

            int code = RandomUtil.rndInt(1000, 9999);
            String key1 = RedisKey_Gen.get_SmsLoginKey(appId, phone);
            cache.setSmsKey(key1, loginTime, String.valueOf(code));
            String url = loginSmsUrl + "?phone=" + phone + "&code=" + code;

            JSONObject rsp = httpService.httpGetJsonNo(url);
            if (rsp.containsKey("data")) {
                String data = rsp.getString("data");
                JSONObject jsData = JSONObject.parseObject(data);

                if (jsData.containsKey("Message") && "OK".equals(jsData.getString("Message"))) {
                    rspJson.put("status", true);
                    rspJson.put("message", "获取验证码成功");
                    break;
                }
            }
            rspJson.put("status", false);
            rspJson.put("message", "获取验证码失败");
        } while (false);


        ResponseUtil.write(response, rspJson);
        log.info("end: /zhiYueSms/PhoneCodeLogin\t" + rspJson.toString());
    }

    /**
     * 指悦账号手机注册
     */
    @RequestMapping(value = "/PhoneReg", method = RequestMethod.POST)
    @ResponseBody
    public void autoReg(@RequestBody String jsonData, HttpServletResponse response) throws Exception {
        log.info("start: /zhiYueSms/PhoneReg\t" + jsonData);

        JSONObject reqJson = JSONObject.parseObject(jsonData);
        JSONObject result = new JSONObject();

        do {
            String[] mustKey = {"appId", "channelId", "appKey", "code", "phone", "password"};
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
            String phone = reqJson.getString("phone");
            String code = reqJson.getString("code");

            GameNew gameNew = gameWorker.getGameNew(appId);
            if (gameNew == null || !gameNew.getSecertKey().equals(appKey)) {
                result.put("message", "游戏 或 秘钥 不存在");
                result.put("status", false);
                break;
            }
            //检查code
            String key = RedisKey_Gen.get_SmsRegisterKey(String.valueOf(appId), phone);
            String redisCode = cache.getSmsString(key);
            if (!redisCode.equals(code)) {
                result.put("message", "验证码错误 redisCode=" + redisCode + " code=" + code);
                result.put("status", false);
                break;
            }

            reqJson.put("ip", UtilG.getIpAddress(request));
            Account account = accountWorker.phoneReg(reqJson);
            if (account.getId() > 0) {
                result.put("status", true);
                result.put("message", "注册成功");

                result.put("zhiyueUid", account.getId());
                result.put("account", account.getName());
                result.put("password", account.getPwd());
                result.put("channelUid", account.getChannelUserId());
            } else {
                if (account.getId() == -2) {
                    result.put("status", false);
                    result.put("message", "该手机已注册");
                } else {
                    result.put("status", false);
                    result.put("message", "注册失败");
                }
            }
        } while (false);
        ResponseUtil.write(response, result);
        log.info("end: /zhiYueSms/PhoneReg\t" + result.toString());
    }

    /**
     * 指悦账号手机登录
     */
    @RequestMapping(value = "/PhoneLogin", method = RequestMethod.POST)
    @ResponseBody
    public void autoLogin(@RequestBody String jsonData, HttpServletResponse response) throws Exception {
        log.info("start: /zhiYueSms/PhoneLogin\t" + jsonData);
        JSONObject reqJson = JSONObject.parseObject(jsonData);
        JSONObject rspJson = new JSONObject();

        do {
            String[] mustKey = {"appId", "channelId", "appKey", "phone", "code"};
            for (String key : mustKey) {
                if (!reqJson.containsKey(key) || StringUtils.isBlank(reqJson.get(key))) {
                    rspJson.put("message", "参数非法:" + key + "为空");
                    rspJson.put("status", false);
                    break;
                }
            }
            String appId = reqJson.getString("appId");
            String channelId = reqJson.getString("channelId");
            String appKey = reqJson.getString("appKey");
            String phone = reqJson.getString("phone");
            String code = reqJson.getString("code");
            if (!checkPhone(phone, rspJson)) {
                break;
            }

            String key = RedisKey_Gen.get_SmsLoginKey(appId, phone);
            String redisCode = cache.getSmsString(key);
            if (!redisCode.equals(code)) {
                rspJson.put("message", "验证码错误 redisCode=" + redisCode + " code=" + code);
                rspJson.put("status", false);
                break;
            }
            GameNew gameNew = gameWorker.getGameNew(Integer.parseInt(appId));
            if (gameNew == null || !gameNew.getSecertKey().equals(appKey)) {
                rspJson.put("message", "游戏 或 秘钥 不存在");
                rspJson.put("status", false);
                break;
            }
            Account account = accountService.findAccountByname(phone);
            if (account == null) {
                rspJson.put("message", "账号不存在");
                rspJson.put("status", false);
                break;
            }

            String channelUid = account.getChannelUserId();

            StringBuilder loginUrl = new StringBuilder(gameNew.getLoginUrl());
            loginUrl.append("GameId").append("=").append(appId);
            loginUrl.append("&").append("GameKey").append("=").append(appKey);
            loginUrl.append("&").append("ChannelCode").append("=").append(channelId);
            loginUrl.append("&").append("ChannelUid").append("=").append(channelUid);

            rspJson.put("status", true);
            rspJson.put("message", "登录成功");
            rspJson.put("channelUid", channelUid);
            rspJson.put("loginUrl", loginUrl);
            cache.setSmsExpire(key, 10);
        } while (false);

        ResponseUtil.write(response, rspJson);
        log.info("end: /zhiYueSms/PhoneLogin\t" + rspJson.toString());
    }
}
