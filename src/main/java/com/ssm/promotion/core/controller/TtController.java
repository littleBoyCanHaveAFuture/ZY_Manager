package com.ssm.promotion.core.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.common.Result;
import com.ssm.promotion.core.common.ResultGenerator;
import com.ssm.promotion.core.entity.Account;
import com.ssm.promotion.core.jedis.jedisRechargeCache;
import com.ssm.promotion.core.sdk.*;
import com.ssm.promotion.core.service.ServerListService;
import com.ssm.promotion.core.util.*;
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
import java.util.*;

/**
 * 用来做假数据
 * 1.注册
 * 2.登录
 * 3.支付
 *
 * @author song minghua
 * @version 0.1
 * @date 2019/12/3
 */
@Controller
@RequestMapping("/ttt")
public class TtController {
    private static final Logger log = Logger.getLogger(TtController.class);
    @Autowired
    jedisRechargeCache cache;
    @Resource
    LoginWorker loginWorker;
    @Resource
    AccountWorker accountWorker;
    @Resource
    GameRoleWorker gameRoleWorker;
    @Resource
    private ServerListService serverService;
    @Autowired
    private HttpServletRequest request;
    @Resource
    private UOrderManager orderManager;

    private void setResponseAccess(HttpServletResponse response) {
/*
        # 服务端允许访问的域名
        Access-Control-Allow-Origin=https://idss-uat.jiuyescm.com
        # 服务端允许访问Http Method
        Access-Control-Allow-Methods=GET, POST, PUT, DELETE, PATCH, OPTIONS
        # 服务端接受跨域带过来的Cookie,当为true时,origin必须是明确的域名不能使用*
        Access-Control-Allow-Credentials=true
        # Access-Control-Allow-Headers 表明它允许跨域请求包含content-type头，我们这里不设置，有需要的可以设置
        #Access-Control-Allow-Headers=Content-Type,Accept
        # 跨域请求中预检请求(Http Method为Option)的有效期,20天,单位秒
        Access-Control-Max-Age=1728000
*/
        String[] allowDomain = {"http://127.0.0.1:8080", "http://lh5ds.yy66game.com/", "http://47.101.44.31:8080"};
        Set<String> allowedOrigins = new HashSet<>(Arrays.asList(allowDomain));

        log.info("servername = " + request.getServerName());
        response.addHeader("Access-Control-Allow-Origin", "http://lh5ds.yy66game.com/");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.addHeader("Access-Control-Max-Age", "3600");
        response.addHeader("Access-Control-Allow-Headers", "X-Custom-Header,accept,content-type");
        //允许客户端发送cookies true表示接收，false不接受 默认为false？
        response.addHeader("Access-Control-Allow-Credentials", "true");
    }

    /**
     * 官方一键登录
     */
    @RequestMapping(value = "/autoGame", method = RequestMethod.GET)
    public void autoGame(String accountId, String appId, String serverId, HttpServletResponse response) throws Exception {
        //登录地址
        String url = loginWorker.getLoginParam(accountId, appId, serverId);

        log.info("autoGame\t" + url);

        JSONObject reply = new JSONObject();
        reply.put("url", url);
        reply.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        setResponseAccess(response);
        ResponseUtil.write(response, reply);
    }

    /**
     * 官方一键登录
     */
    @RequestMapping(value = "/autoGameSp", method = RequestMethod.GET)
    public void autoGameSp(String accountId, String appId, String serverId, HttpServletResponse response) throws Exception {
        //登录地址
        Map<String, Object> map = new HashMap<>();
        map.put("spId", "0");
        map.put("gameId", appId);
        map.put("serverId", serverId);
        String loginUrl = serverService.selectLoginUrl(map, 0);


        JSONObject reply = new JSONObject();
//        reply.put("url", loginUrl + param);
        reply.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        setResponseAccess(response);
        ResponseUtil.write(response, reply);
    }

    /**
     * 一键注册
     */
    @RequestMapping(value = "/autoReg", method = RequestMethod.GET)
    public void autoReg(String auto,
                        String appid, HttpServletResponse response) throws Exception {
        //注册账号
        JSONObject reply = new JSONObject();
        Account acc = accountWorker.autoRegister(reply, UtilG.getIpAddress(request));
        if (acc != null && reply.getInteger("status") == 1) {
            //注册成功 相关数据存入redis
            Map<String, Object> map = new HashMap<>();
            map.put("auto", auto);
            map.put("appId", appid);
            map.put("accountId", acc.getId().toString());
//            cache.register(auto,appid,acc.getId());
        }
        reply.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        setResponseAccess(response);
        ResponseUtil.write(response, reply);
    }

    /**
     * 注册账号
     * SDK 登录接口
     *
     * @param jsonData boolean     auto          自动注册(限渠道账号,无需username,pwd)<p>
     *                 int         appId         游戏id*<p>
     *                 int         channelId     渠道id*<p>
     *                 string      channelUid    渠道账号id*<p>
     *                 string      channelUname  渠道账号登录名*<p>
     *                 string      channelUnick  渠道账号昵称*<p>
     *                 string      username      指悦账户名(为空即可)<p>
     *                 string      pwd           指悦账号密码(为空即可)<p>
     *                 string      phone         手机号*<p>
     *                 string      deviceCode    硬件设备号*<p>
     *                 string      imei          国际移动设备识别码<p>
     *                 string      addparm       额外参数(为空即可)<p>
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Result sdkRegister(@RequestBody String jsonData) throws Exception {
        log.info("register:" + jsonData);
        JSONObject jsonObject = JSON.parseObject(jsonData);

        boolean auto = jsonObject.getBoolean("auto");
        //参数校验
        if (auto) {
            for (String key : jsonObject.keySet()) {
                if (StringUtils.isBlank(jsonObject.get(key))) {
                    if ("appId".equals(key) || "channelId".equals(key) || "channelUid".equals(key)) {
                        JSONObject result = new JSONObject();
                        result.put("message", "参数非法:" + key + "为空");
                        result.put("state", false);
                        return ResultGenerator.genSuccessResult(result);
                    }
                }
            }
        } else {
            for (String key : jsonObject.keySet()) {
                if (StringUtils.isBlank(jsonObject.get(key))) {
                    if ("appId".equals(key) || "username".equals(key) || "pwd".equals(key)) {
                        JSONObject result = new JSONObject();
                        result.put("message", "参数非法:" + key + "为空");
                        result.put("state", false);
                        return ResultGenerator.genSuccessResult(result);
                    }
                }
            }
        }
        //获取ip
        jsonObject.put("ip", UtilG.getIpAddress(request));
        JSONObject result = accountWorker.reqRegister(jsonObject);
        return ResultGenerator.genSuccessResult(result);
    }

    /**
     * 指悦账号登录
     * 客户端先请求
     * SDK 登录接口
     *
     * @param map isChannel         是否渠道登录*             渠道登录可以不输入(name pwd),非渠道登录必须输入(name pwd)
     *            appId             指悦平台创建的游戏ID*
     *            channelId         平台标示的渠道SDKID
     *            channelUid        渠道SDK标示的用户ID
     *            name              指悦账号名称
     *            pwd               指悦账号密码
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public void sdkLogin(@RequestBody Map<String, Object> map,
                         HttpServletResponse response) throws Exception {
        log.info("request: ttt/login , map: " + map.toString());

        int appId = Integer.parseInt(map.get("appId").toString());
        int channelId = Integer.parseInt(map.get("channelId").toString());

        JSONObject result = new JSONObject();
        do {
            Account account = accountWorker.getAccount(map);
            if (account == null) {
                result.put("reason", "指悦账号不存在，请前往注册！");
                result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                break;
            }
            int accountId = account.getId();
            if (!loginWorker.isWhiteCanLogin(accountId, "")) {

            }

            if (!loginWorker.isSpCanLogin(appId, channelId)) {

            }
            //获取账号成功 发送token
            String token = loginWorker.getGameInfo(accountId, appId);
            String sign = MD5Util.md5(appId + token + accountId);

            result.put("appid", appId);
            result.put("uid", accountId);
            result.put("token", token);
            result.put("sign", sign);

            result.put("reason", "获取token成功");
            result.put("message", ResultGenerator.DEFAULT_SUCCESS_MESSAGE);
            //数据库
            //设置账号登录时间
            Map<String, Object> tmap = new HashMap<>();
            tmap.put("id", accountId);
            tmap.put("lastLoginTime", DateUtil.getCurrentDateStr());
            accountWorker.updateLoginTime(tmap);

        } while (false);

        log.info("request: ttt/login , map: " + result.toString());
        ResponseUtil.write(response, result);
    }

    /**
     * 指悦账号登录验证
     * 验证账号信息
     *
     * @param appId 指悦平台创建的游戏ID，appId
     * @param token 随机字符串
     * @param uid   玩家指悦账号id
     * @param sign  签名数据：md5 (appId+token+uid)
     * @return 接口返回：表示用户已登录，其他表示未登陆。
     * 0    验证通过
     * 1    token错误
     * 2    签名错误
     */
    @RequestMapping(value = "/check", method = RequestMethod.GET)
    @ResponseBody
    public Result sdkLoginCheck(int appId,
                                int uid,
                                String token,
                                String sign,
                                HttpServletResponse response) {
        log.info("request: ttt/check , " + "appId: " + appId + "\tuid:" + uid + "\ttoken:" + token + "\tsign:" + sign);

        JSONObject result = new JSONObject();
        do {
            if (token == null || sign == null) {
                result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                result.put("reason", "token 或 sign 为空");
                break;
            }

            if (!LoginToken.check(uid, token)) {
                result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                result.put("reason", "token非法");
                break;
            }
            String tmpSign = MD5Util.md5(appId + token + uid);
            if (tmpSign == null || !tmpSign.equals(sign)) {
                result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                result.put("reason", "签名不一致非法");
                break;
            }

            Account account = accountWorker.getAccountById(uid);
            if (account != null) {
                result.put("accountId", uid);
                result.put("channelUid", account.getChannelUserId());
            }
            result.put("message", ResultGenerator.DEFAULT_SUCCESS_MESSAGE);
            result.put("reason", "登录成功");

        } while (false);

        setResponseAccess(response);
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        log.info("request: ttt/check , result\t" + result.toString());
        return ResultGenerator.genSuccessResult(result);
    }
}
