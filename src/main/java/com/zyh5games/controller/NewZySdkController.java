package com.zyh5games.controller;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.entity.*;
import com.zyh5games.jedis.JedisRechargeCache;
import com.zyh5games.sdk.AccountWorker;
import com.zyh5games.sdk.GameRoleWorker;
import com.zyh5games.sdk.LoginWorker;
import com.zyh5games.sdk.UOrderManager;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelHandler;
import com.zyh5games.service.AccountService;
import com.zyh5games.service.ChannelConfigService;
import com.zyh5games.service.GameNewService;
import com.zyh5games.service.SpService;
import com.zyh5games.util.*;
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
import java.util.HashMap;
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
    @Autowired
    JedisRechargeCache cache;
    @Resource
    LoginWorker loginWorker;
    @Resource
    AccountWorker accountWorker;
    @Resource
    GameRoleWorker gameRoleWorker;
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
    @Resource
    private ChannelHandler channelHandler;

    public boolean checkSdk(Integer type, Integer appId, Integer channelId, String appKey, JSONObject result) {
        // 1.检查游戏秘钥
        GameNew gameNew = gameNewService.selectGame(appId, -1);
        if (gameNew == null) {
            log.error("游戏不存在 appId=" + appId);
            result.put("status", false);
            result.put("message", "SDK init:游戏不存在！");
            return false;
        }
        if (!gameNew.getSecertKey().equals(appKey)) {
            log.error("游戏秘钥错误 appId=" + appId);
            log.error("游戏秘钥错误 正确  key=" + gameNew.getSecertKey());
            log.error("游戏秘钥错误 收到  key=" + appKey);
            result.put("status", false);
            result.put("message", "SDK init:游戏秘钥错误！");
            return false;
        }

        // 2.渠道基本配置 和游戏无关
        Sp sp = spService.getSp(channelId, -1);
        if (sp == null) {
            result.put("status", false);
            result.put("message", "游戏渠道不存在！");
            return false;
        }
        if (type == 1) {
            String channelCode = sp.getCode();
            result.put("channelCode", channelCode);
        } else if (type == 2) {
            result.put("channel_name", sp.getCode());
        }

        // 3.检查游戏渠道
        ChannelConfig config = configService.selectConfig(appId, channelId, -1);
        if (config == null) {
            log.error("游戏渠道不存在 appId=" + appId + " channelId=" + channelId);
            result.put("status", false);
            result.put("message", "SDK init:游戏渠道不存在！");
            return false;
        }

        return true;
    }

    public boolean checkSdkRole(Integer type, String channelId, String appKey, String channelUid,
                                String serverId, String roleId, JSONObject result) {
        // 1.检查游戏秘钥
        GameNew gameNew = gameNewService.getGameByKey(appKey, -1);
        if (gameNew == null) {
            result.put("message", "游戏不存在");
            result.put("status", false);
            return false;
        }
        if (type == 3) {
            result.put("appId", gameNew.getAppId());
        }

        //2.判断账号是否存在
        Map<String, Object> map = new HashMap<>();
        map.put("isChannel", "true");
        map.put("channelId", channelId);
        map.put("channelUid", channelUid);

        Account account = accountWorker.getAccount(map);
        if (account == null) {
            result.put("message", "账号不存在");
            result.put("state", false);
            return false;
        }
        if (type == 3) {
            result.put("zhiyueUid", account.getId());
        }
        //3.判断角色存不存在-角色信息 可以获取 游戏id、渠道id
        GameRole gameRole = gameRoleWorker.findGameRole(String.valueOf(gameNew.getAppId()),
                channelId, serverId, channelUid, roleId);
        if (gameRole == null) {
            result.put("message", "角色不存在");
            result.put("status", false);
            return false;
        }
        return true;

    }

    public GameRole createRole(Integer zhiyueUid, String roleId, String channelId, String channelUid,
                               String appId, String serverId, long roleCreateTime, String userRoleName) throws Exception {
        //role 同渠道游戏区服不能重复-创建角色
        GameRole gameRole = new GameRole();
        gameRole.setAccountId(zhiyueUid);
        gameRole.setRoleId(roleId);
        gameRole.setChannelId(channelId);
        gameRole.setChannelUid(channelUid);
        gameRole.setGameId(appId);
        gameRole.setServerId(Integer.parseInt(serverId));
        gameRole.setCreateTime(DateUtil.formatDate(roleCreateTime * 1000, DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
        gameRole.setLastLoginTime(DateUtil.formatDate(System.currentTimeMillis(), DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
        gameRole.setName(userRoleName);
        boolean res = gameRoleWorker.existRole(String.valueOf(zhiyueUid));
        //插入mysql
        gameRoleWorker.createGameRole(gameRole);
        //redis

        cache.createRole(appId, serverId, channelId, zhiyueUid, roleId, res);
        log.info("createRole " + gameRole.toString());
        return gameRole;
    }

    /**
     * 0.渠道自动注册
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
    public void sdkRegister(@RequestBody String jsonData, HttpServletResponse response) throws Exception {
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
    public void initApi(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("start: /webGame2/initApi\tstart");

        Map<String, String[]> parameterMap = request.getParameterMap();
        loginWorker.getLoginParams(parameterMap);
        JSONObject result = new JSONObject();

        String[] mustKey = {"GameId", "GameKey", "ChannelCode"};
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
            int channelId = Integer.parseInt(parameterMap.get("ChannelCode")[0]);

            if (!this.checkSdk(1, appId, channelId, appKey, result)) {
                break;
            }

            BaseChannel channelService = channelHandler.getChannel(channelId);
            JSONObject channelData = channelService.channelLib(appId);
            JSONArray libUrl = channelService.commonLib();
            String channelToken = channelService.channelToken(parameterMap);


            JSONObject channelPlatform = new JSONObject();
            channelPlatform.put("libUrl", libUrl);
            channelPlatform.put("playUrl", "");

            result.put("channelToken", channelToken);
            result.put("channelPlatform", channelPlatform);
            result.put("channelData", channelData);

            result.put("status", true);
            result.put("message", "SDK init:初始化成功！");
        } while (false);

        ResponseUtil.write(response, result);
        log.info("end: /webGame2/initApi\tend\t" + result.toString());
    }

    /**
     * 2.sdk登录
     */
    @RequestMapping(value = "/loginApi", method = RequestMethod.GET)
    @ResponseBody
    public void loginApi(HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("start: /webGame2/loginApi\tstart");

        Map<String, String[]> parameterMap = request.getParameterMap();
        loginWorker.getLoginParams(parameterMap);

        JSONObject result = new JSONObject();

        String[] mustKey = {"GameId", "GameKey", "ChannelCode"};
        for (String key : mustKey) {
            if (!parameterMap.containsKey(key) || StringUtils.isBlank(parameterMap.get(key))) {
                result.put("status", false);
                result.put("message", "SDK init:参数非法:" + key + "为空");
                ResponseUtil.write(response, result);
                return;
            }
        }

        int appId = Integer.parseInt(parameterMap.get("GameId")[0]);
        String appKey = parameterMap.get("GameKey")[0];
        int channelId = Integer.parseInt(parameterMap.get("ChannelCode")[0]);


        JSONObject userData = new JSONObject();
        JSONObject channelData = new JSONObject();

        do {
            if (!this.checkSdk(2, appId, channelId, appKey, result)) {
                break;
            }

            BaseChannel channelService = channelHandler.getChannel(channelId);

            // 4.不同渠道 账号校验
            if (!channelService.channelLogin(parameterMap, userData)) {
                log.error("渠道登录 参数校验失败 appId=" + appId + " channelId=" + channelId);
                result.put("status", false);
                result.put("message", "游戏渠道 参数校验失败！");
                break;
            } else {
                //没有则注册指悦账号
                //1.判断账号是否存在
                String channelUid = userData.getString("uid");
                String token = userData.getString("token");
                String openid = userData.getString("openid");

                Account account = accountWorker.channelReg(appId, channelId, channelUid, openid);
                if (account != null) {
                    cache.setChannelLoginToken(String.valueOf(appId), String.valueOf(channelId), channelUid, token);
                } else {
                    result.put("status", false);
                    result.put("message", "游戏渠道 登录失败！");
                    break;
                }
            }


            channelData.put("channel_id", channelId);
            channelData.put("channel_name", result.getString("channel_name"));

            result.remove("channel_name");

            result.replace("status", true);
            result.put("message", "登陆成功");
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
    @RequestMapping(value = "/checkUserInfo", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String checkUserInfo(String token, String gameKey, String uid, String channelId) {
        boolean res = false;
        do {
            GameNew gameNew = gameNewService.getGameByKey(gameKey, -1);
            if (gameNew == null) {
                break;
            }
            JSONObject data = new JSONObject();
            data.put("appId", gameNew.getAppId());
            data.put("channelUid", uid);
            data.put("channelId", channelId);

            String channelToken = cache.getChannelLoginToken(String.valueOf(gameNew.getAppId()), channelId, uid);
            BaseChannel channelService = channelHandler.getChannel(Integer.parseInt(channelId));
            if (channelService != null) {
                res = channelService.channelLoginCheck(data, token, channelToken);
            }
        } while (false);
        return res ? "1" : "0";
    }

    /**
     * 3.获取支付信息->跳转地址网页支付
     * 1.生成订单
     * 2.跳转到渠道支付页面
     *
     * @param data json 字符串
     *             channelId        必传     QuickSDK后台自动分配的渠道参数
     *             gameKey	        必传	    QuickSDK后台自动分配的游戏参数
     *             uid	            必传	    渠道UID
     *             username	        必传	    渠道username
     *             userRoleId	    必传	    游戏内角色ID
     *             userRoleName	    必传	    游戏角色
     *             serverId	        必传	    角色所在区服ID
     *             userServer	    必传	    角色所在区服
     *             userLevel	    必传	    角色等级
     *             cpOrderNo	    必传	    游戏内的订单,服务器通知中会回传
     *             amount	        必传	    购买金额（元）
     *             count	        必传	    购买商品个数
     *             quantifier	    必传	    购买商品单位，如，个
     *             subject	        必传	    道具名称
     *             desc	            必传	    道具描述
     *             callbackUrl	    选传	    服务器通知地址
     *             extrasParams	    选传	    透传参数,服务器通知中原样回传
     *             goodsId	        必传	    商品ID
     */
    @RequestMapping(value = "/ajaxGetOrderNo", method = RequestMethod.POST)
    @ResponseBody
    public void ajaxGetOrderNo(@RequestBody String data, HttpServletResponse response) throws Exception {
        log.info("start /webGame2/ajaxGetOrderNo " + data);

        JSONObject result = new JSONObject();

        JSONObject orderData = JSONObject.parseObject(data);
        String[] mustKey = {"gameKey", "uid", "username", "userRoleId", "userRoleName",
                "serverId", "userServer", "userLevel", "cpOrderNo", "amount",
                "count", "quantifier", "subject", "desc", "callbackUrl", "extrasParams",
                "goodsId"};
        for (String key : mustKey) {
            if (!orderData.containsKey(key)) {
                result.put("status", false);
                result.put("message", "SDK pay:参数非法:" + key + "缺少");
                ResponseUtil.write(response, result);
                return;
            } else {
                if ("callbackUrl".equals(key) || "extrasParams".equals(key)) {

                } else {
                    if (StringUtils.isBlank(orderData.get(key))) {
                        result.put("status", false);
                        result.put("message", "SDK pay:参数非法:" + key + "为空");
                        ResponseUtil.write(response, result);
                        return;
                    }
                }
            }
        }
        String channelId = orderData.getString("channelId");
        String gameKey = orderData.getString("gameKey");
        String channelUid = orderData.getString("uid");
        String username = orderData.getString("username");
        String userRoleId = orderData.getString("userRoleId");
        String userRoleName = orderData.getString("userRoleName");

        String serverId = orderData.getString("serverId");
        String userServer = orderData.getString("userServer");
        String userLevel = orderData.getString("userLevel");
        String cpOrderNo = orderData.getString("cpOrderNo");
        String amount = orderData.getString("amount");

        String count = orderData.getString("count");
        String quantifier = orderData.getString("quantifier");
        String subject = orderData.getString("subject");
        String desc = orderData.getString("desc");
        String callbackUrl = orderData.getString("callbackUrl");

        String extrasParams = orderData.getString("extrasParams");
        String goodsId = orderData.getString("goodsId");
        String channelToken = orderData.getString("channelToken");

        do {
            if (!this.checkSdkRole(3, channelId, gameKey, channelUid, serverId, userRoleId, result)) {
                break;
            }
            Integer appId = result.getInteger("appId");
            Integer zhiyueUid = result.getInteger("zhiyueUid");
            result.remove("appId");
            result.remove("zhiyueUid");

            //2.金额合法性
            String moneyFen = FeeUtils.yuanToFen(amount);
            if (Integer.parseInt(moneyFen) < 0) {
                log.info("the money is not valid. money:" + moneyFen);
                result.put("message", "金额错误");
                result.put("status", false);
                break;
            }

            //3.订单是否已经存在
            UOrder order = orderManager.getCpOrder(String.valueOf(appId), String.valueOf(channelId), cpOrderNo);
            if (order != null) {
                result.put("message", "订单已存在");
                result.put("status", false);
                break;
            }
            log.info("order is not exist. generateOrder");
            order = orderManager.genOrder(zhiyueUid, appId, channelId, cpOrderNo, userRoleId, userRoleName, serverId, userServer, userLevel,
                    goodsId, subject, desc, moneyFen, count, quantifier, extrasParams, callbackUrl);

            //4.调起渠道支付接口 生成订单
            BaseChannel channelService = channelHandler.getChannel(Integer.parseInt(channelId));

            orderData.put("appId", appId);
            orderData.put("zhiyueOrderId", order.getOrderID());
            JSONObject channelOrderNo = new JSONObject();
            if (!channelService.channelPayInfo(orderData, channelOrderNo)) {
                result.put("message", "调起渠道支付接口 失败");
                result.put("status", false);
                break;
            }
            orderData.remove("zhiyueOrderId");
            orderData.remove("appId");

            JSONObject rspData = new JSONObject();
            rspData.put("orderNo", String.valueOf(order.getOrderID()));
            rspData.put("channelOrderNo", channelOrderNo.toJSONString());
            rspData.put("amount", amount);

            result.put("message", "上报订单数据成功");
            result.put("status", true);
            result.put("data", rspData);

        } while (false);

        ResponseUtil.write(response, result);

        log.info("end   /webGame2/payInfo \t" + result.toString());
    }

    /**
     * 4.设置角色基本数据
     * 1.创角打点
     * 2.新手指引打点
     *
     * @param jsonRoleInfo json字符串
     *                     datatype             1.选择服务器 2.创建角色 3.进入游戏 4.等级提升 5.退出游戏"
     *                     roleCreateTime       角色创建时间 时间戳 单位 秒
     *                     uid                  渠道UID
     *                     username             渠道账号昵称
     *                     serverId             区服ID
     *                     serverName           区服名称
     *                     userRoleName         游戏内角色名
     *                     userRoleId           游戏内角色ID
     *                     userRoleBalance      角色游戏内货币余额
     *                     vipLevel             角色VIP等级
     *                     userRoleLevel        角色等级
     *                     partyId              公会/社团ID
     *                     partyName            公会/社团名称
     *                     gameRoleGender       角色性别
     *                     gameRolePower        角色战力
     *                     partyRoleId          角色在帮派中的ID
     *                     partyRoleName        角色在帮派中的名称
     *                     professionId         角色职业ID
     *                     profession           角色职业名称
     *                     friendlist           角色好友列表
     */
    @RequestMapping(value = "/ajaxUploadGameRoleInfo", method = RequestMethod.POST)
    @ResponseBody
    public void ajaxUploadGameRoleInfo(@RequestBody String jsonRoleInfo, HttpServletResponse response) throws Exception {
        log.info("start /webGame2/ajaxUploadGameRoleInfo" + "\t" + jsonRoleInfo);

        JSONObject roleInfo = JSONObject.parseObject(jsonRoleInfo);
        JSONObject result = new JSONObject();

        do {
            String[] mustKeysValue = {
                    "GameId", "GameKey", "channelId",
                    "datatype", "roleCreateTime", "uid", "username",
                    "serverId", "serverName", "userRoleName", "userRoleId", "userRoleBalance",
                    "vipLevel", "userRoleLevel", "partyId", "partyName",
                    "gameRoleGender", "gameRolePower", "partyRoleId", "partyRoleName", "professionId", "profession",
                    "friendlist"
            };
            String[] notNullKeysValue = {
                    "GameId", "GameKey", "channelId",
                    "datatype", "roleCreateTime", "uid", "username",
                    "serverId", "serverName", "userRoleName", "userRoleId", "userRoleBalance",
                    "vipLevel", "userRoleLevel", "partyId", "partyName"
            };

            for (String index : mustKeysValue) {
                if (!roleInfo.containsKey(index)) {
                    result.put("message", "缺失参数：" + index);
                    result.put("status", false);
                    break;
                }
            }
            for (String index : notNullKeysValue) {
                if (roleInfo.getString(index).isEmpty()) {
                    result.put("message", "参数为空：" + index);
                    result.put("status", false);
                    break;
                }
            }

            String gameId = roleInfo.getString("GameId");
            String channelId = roleInfo.getString("channelId");
            String channelUid = roleInfo.getString("uid");
            String roleId = roleInfo.getString("userRoleId");
            String userRoleName = roleInfo.getString("userRoleName");
            String serverId = roleInfo.getString("serverId");
            long roleCreateTime = roleInfo.getLongValue("roleCreateTime");
            String userRoleBalance = roleInfo.getString("userRoleBalance");

            Account account = accountService.findUserBychannelUid(channelId, channelUid);
            if (account == null) {
                result.put("message", "账号不存在");
                result.put("state", false);
                break;
            }

            GameRole gameRole = gameRoleWorker.findGameRole(gameId, channelId, serverId, channelUid, roleId);

            Integer datatype = roleInfo.getInteger("datatype");
            switch (datatype) {
                //创建角色
                case 2: {
                    if (gameRole != null) {
                        result.put("message", "角色已存在");
                        result.put("state", false);
                        ResponseUtil.write(response, result);
                        return;
                    }
                    gameRole = this.createRole(account.getId(), roleId, channelId, channelUid, gameId, serverId, roleCreateTime, userRoleName);
                }
                break;
                // 进入游戏
                case 3: {
                    if (gameRole == null) {
                        gameRole = this.createRole(account.getId(), roleId, channelId, channelUid, gameId, serverId, roleCreateTime, userRoleName);
                    } else {
                        gameRoleWorker.updateGameRole(gameId, channelId, channelUid, serverId, DateUtil.getCurrentDateStr(), roleId, userRoleBalance, userRoleName, "");
                    }
                    result.put("message", "进入游戏 上报成功");
                    result.put("state", true);
                    //设置活跃玩家、在线玩家
                    cache.enterGame(gameId, String.valueOf(serverId), channelId, gameRole.getRoleId());
                    //设置区服信息
                    cache.setServerInfo(gameId, channelId, String.valueOf(serverId));
                }
                break;
                // 角色升级
                case 4: {
                    if (gameRole == null) {
                        gameRole = this.createRole(account.getId(), roleId, channelId, channelUid, gameId, serverId, roleCreateTime, userRoleName);
                    } else {
                        gameRoleWorker.updateGameRole(gameId, channelId, channelUid, serverId, "", roleId, userRoleBalance, userRoleName, "");
                    }
                    result.put("message", "角色升级 上报成功");
                    result.put("state", true);
                }
                break;
                // 退出游戏
                case 5: {
                    if (gameRole == null) {
                        gameRole = this.createRole(account.getId(), roleId, channelId, channelUid, gameId, serverId, roleCreateTime, userRoleName);
                    } else {
                        gameRoleWorker.updateGameRole(gameId, channelId, channelUid, serverId, "", roleId, userRoleBalance, userRoleName, "");
                    }
                    //查询redis-移除在线玩家 todo h5没法监控 数据不可靠
                    cache.exitGame(gameId, channelId, String.valueOf(serverId), account.getId());
                    result.put("message", "退出游戏 上报成功");
                    result.put("state", true);
                }
                break;
                default:
                    break;
            }

            result.put("data", "");
            result.put("status", true);
            result.put("message", "成功上报");
        }
        while (false);

        ResponseUtil.write(response, result);

        log.info("/webGame2/ajaxUploadGameRoleInfo\t" + result.toString());
    }

    /**
     * 5.某些渠道 上报角色数据 签名
     * todo
     *
     * @param jsonRoleInfo json         字符串
     *                     userToken    必填，接口1 回调获取到的userToken
     *                     channel_id   必填，渠道id
     *                     area         必填，区服名
     *                     role_name    必填，角色名
     *                     new_role     必填，创建新角色：0为角色升级，1为创建新角
     *                     rank         必填，等级
     *                     money        必填，元宝数
     *                     sign         必填，见附录
     */
    @RequestMapping(value = "/ajaxGetSignature", method = RequestMethod.POST)
    @ResponseBody
    public void ajaxGetSignature(@RequestBody String jsonRoleInfo, HttpServletResponse response) throws Exception {
        log.info("start /webGame2/ajaxGetSignature" + "\t" + jsonRoleInfo);
        JSONObject result = new JSONObject();
        JSONObject requestInfo = JSONObject.parseObject(jsonRoleInfo);
        do {
            Integer appId = requestInfo.getInteger("appId");
            Integer channelId = requestInfo.getInteger("channelId");
            if (appId == null || channelId == null) {
                result.put("message", "缺失参数 appId/channelId");
                result.put("status", false);
                break;
            }
            BaseChannel channelService = channelHandler.getChannel(channelId);
            JSONObject rsp = channelService.ajaxGetSignature(appId, requestInfo, result);
            if (rsp != null) {
                result.put("data", rsp);
                result.put("message", "签名成功");
                result.put("status", true);
            }
        }
        while (false);

        ResponseUtil.write(response, result);

        log.info("/webGame2/ajaxGetSignature\t" + result.toString());
    }
}
