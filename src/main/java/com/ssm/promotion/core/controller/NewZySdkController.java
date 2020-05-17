package com.ssm.promotion.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.entity.ChannelConfig;
import com.ssm.promotion.core.entity.GameNew;
import com.ssm.promotion.core.entity.Sp;
import com.ssm.promotion.core.jedis.jedisRechargeCache;
import com.ssm.promotion.core.sdk.*;
import com.ssm.promotion.core.service.AccountService;
import com.ssm.promotion.core.service.ChannelConfigService;
import com.ssm.promotion.core.service.GameNewService;
import com.ssm.promotion.core.service.SpService;
import com.ssm.promotion.core.util.ResponseUtil;
import com.ssm.promotion.core.util.StringUtils;
import com.ssm.promotion.core.util.UtilG;
import net.sf.json.JSONArray;
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
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/11/27
 */
@Controller
@RequestMapping("/webGame2")
public class NewZySdkController {
    private static final Logger log = Logger.getLogger(NewZySdkController.class);
    public static String[] keys = {"createRole", "levelUp", "enterGame", "exitGame"};
    public static String[] mustKeysValue = {
            "appId", "channelId", "channelUid",
            "roleId", "roleName", "roleLevel",
            "zoneId", "zoneName", "balance", "vip",
            "partyName"};
    @Autowired
    jedisRechargeCache cache;
    @Resource
    LoginWorker loginWorker;
    @Resource
    AccountWorker accountWorker;
    @Resource
    GameRoleWorker gameRoleWorker;
    @Resource
    ChannelLib channelLib;
    @Autowired
    private HttpServletRequest request;
    @Resource
    private UOrderManager orderManager;
    @Resource
    private AccountService accountService;
    @Resource
    private GameNewService gameNewService;
    @Resource
    private SpService spService;
    @Resource
    private ChannelConfigService configService;

    @RequestMapping(value = "/getId", method = RequestMethod.GET)
    @ResponseBody
    public Integer getNextId() throws Exception {
        return AccountWorker.getNextId();
    }

    /**
     * 渠道自动注册
     * SDK 登录接口
     *
     * @param jsonData int         appId         游戏id*<p>
     *                 int         channelId     渠道id*<p>
     *                 string      channelUid    渠道账号id*<p>
     *                 string      channelUname  渠道账号登录名*<p>
     *                 string      channelUnick  渠道账号昵称*<p>
     *                 string      phone         手机号*<p>
     *                 string      deviceCode    硬件设备号*<p>
     *                 string      imei          国际移动设备识别码<p>
     */
    @RequestMapping(value = "/autoReg", method = RequestMethod.POST)
    @ResponseBody
    public void sdkRegister(@RequestBody String jsonData,
                            HttpServletResponse response) throws Exception {
        log.info("start: /webGame2/register\t" + jsonData);

        JSONObject reqJson = JSONObject.parseObject(jsonData);
        JSONObject rspJson = new JSONObject();

        String[] mustKey = {"appId", "channelId", "channelUid"};
        for (String key : mustKey) {
            if (!reqJson.containsKey(key) || StringUtils.isBlank(reqJson.get(key))) {
                rspJson.put("message", "参数非法:" + key + "为空");
                rspJson.put("state", false);
                ResponseUtil.write(response, rspJson);
            }
        }

        reqJson.put("ip", UtilG.getIpAddress(request));
        rspJson = accountWorker.channelRegister(reqJson);

        ResponseUtil.write(response, rspJson);
        log.info("end: /webGame2/register\t" + rspJson.toString());
    }

    /**
     * 1.初始化游戏
     * 1.获取 需要的js文件
     * 2.获取 渠道秘钥
     */
    @RequestMapping(value = "/initApi", method = RequestMethod.GET)
    @ResponseBody
    public void initApi(HttpServletRequest request,
                        HttpServletResponse response) throws Exception {
        log.info("start: /webGame2/initApi\tstart");

        Map<String, String[]> parameterMap = request.getParameterMap();
        loginWorker.getLoginParams(parameterMap);
        JSONObject result = new JSONObject();

        String[] mustKey = {"GameId", "GameKey", "channelId"};
        for (String key : mustKey) {
            if (!parameterMap.containsKey(key) || StringUtils.isBlank(parameterMap.get(key))) {
                result.put("status", false);
                result.put("message", "SDK init:参数非法:" + key + "为空");
                ResponseUtil.write(response, result);
                return;
            }
        }

        do {

            int appId = Integer.parseInt(parameterMap.get("GameId")[0]);
            String appKey = parameterMap.get("GameKey")[0];
            int channelId = Integer.parseInt(parameterMap.get("channelId")[0]);


            //检查游戏秘钥
            GameNew gameNew = gameNewService.selectGame(appId, -1);
            if (gameNew == null) {
                log.error("游戏不存在 appId=" + appId);
                result.put("status", false);
                result.put("message", "SDK init:游戏不存在！");
                break;
            }

            if (!gameNew.getSecertKey().equals(appKey)) {
                log.error("游戏秘钥错误 appId=" + appId);
                log.error("游戏秘钥错误 正确  key=" + gameNew.getSecertKey());
                log.error("游戏秘钥错误 收到  key=" + appKey);
                result.put("status", false);
                result.put("message", "SDK init:游戏秘钥错误！");
                break;
            }
            //检查游戏渠道
            ChannelConfig config = configService.selectConfig(appId, channelId, -1);
            if (config == null) {
                log.error("游戏渠道不存在 appId=" + appId + " channelId=" + channelId);
                result.put("status", false);
                result.put("message", "SDK init:游戏渠道不存在！");
                break;
            }

            JSONObject channelParams = new JSONObject();
            channelParams.put("login_key", "");
            channelParams.put("pay_key", "");
            channelParams.put("send_key", "");

            JSONArray libUrl = channelLib.loadChannelLib(channelId);

            JSONObject channelPlatform = new JSONObject();
            channelPlatform.put("libUrl", libUrl);
            channelPlatform.put("playUrl", "");

            result.put("channelToken", "");
            result.put("channelParams", channelParams);
            result.put("channelPlatform", channelPlatform);
            result.put("status", true);
            result.put("message", "SDK init:初始化成功！");
        } while (false);

        ResponseUtil.write(response, result);
        log.info("end: /webGame/initApi\tend\t" + result.toString());
    }

    /**
     * 2.sdk登录
     */
    @RequestMapping(value = "/loginApi", method = RequestMethod.GET)
    @ResponseBody
    public void loginApi(HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        log.info("start: /webGame2/loginApi\tstart");

        Map<String, String[]> parameterMap = request.getParameterMap();
        loginWorker.getLoginParams(parameterMap);

        int appId = Integer.parseInt(parameterMap.get("GameId")[0]);
        String appKey = parameterMap.get("GameKey")[0];
        int channelId = Integer.parseInt(parameterMap.get("channelId")[0]);

        JSONObject result = new JSONObject();
        JSONObject userData = new JSONObject();
        JSONObject channelData = new JSONObject();

        channelData.put("channel_id", channelId);
        channelData.put("channel_name", "");

        do {
            //检查游戏秘钥
            GameNew gameNew = gameNewService.selectGame(appId, -1);
            if (gameNew == null) {
                log.error("游戏不存在 appId=" + appId);
                result.put("message", "游戏不存在！");
                userData.put("channelId", "");
                break;
            }

            if (!gameNew.getSecertKey().equals(appKey)) {
                log.error("游戏秘钥错误 appId=" + appId);
                log.error("游戏秘钥错误 正确  key=" + gameNew.getSecertKey());
                log.error("游戏秘钥错误 收到  key=" + appKey);
                result.put("message", "游戏秘钥错误！");
                break;
            }
            Sp sp = spService.getSp(channelId, -1);

            if (sp != null) {
                channelData.replace("channel_name", sp.getCode());
            }

            //检查游戏渠道
            ChannelConfig config = configService.selectConfig(appId, channelId, -1);
            if (config == null) {
                log.error("游戏渠道不存在 appId=" + appId + " channelId=" + channelId);
                result.put("message", "游戏渠道不存在！");
                break;
            }

            // 不同渠道 账号校验
            if (!loginWorker.checkLoginParams(parameterMap, userData)) {
                log.error("游戏渠道 参数校验失败 appId=" + appId + " channelId=" + channelId);
                result.put("message", "游戏渠道 参数校验失败！");
                break;
            }

        } while (false);

        result.put("userData", userData);
        result.put("channelData", channelData);

        ResponseUtil.write(response, result);

        log.info("end: /webGame2/initApi\tend\t" + result.toString());
    }

    /**
     * 2.5登陆校验
     *
     * @return 1为成功, 其他值为失败。
     */
    @RequestMapping(value = "/checkUserInfo")
    @ResponseBody
    public String checkUserInfo(HttpServletRequest request, HttpServletResponse response,
                                String token,
                                String product_code,
                                String uid,
                                String channel_code) throws Exception {
        //从redis读取 [appId-channelId-uid]-token todo
        return "1";
    }


}
