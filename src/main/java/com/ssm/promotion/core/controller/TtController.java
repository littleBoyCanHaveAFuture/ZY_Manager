package com.ssm.promotion.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.common.Result;
import com.ssm.promotion.core.common.ResultGenerator;
import com.ssm.promotion.core.entity.Account;
import com.ssm.promotion.core.entity.GameRole;
import com.ssm.promotion.core.entity.UOrder;
import com.ssm.promotion.core.jedis.JedisRechargeCache;
import com.ssm.promotion.core.jedis.RedisKey;
import com.ssm.promotion.core.jedis.RedisKeyHeader;
import com.ssm.promotion.core.jedis.RedisKeyTail;
import com.ssm.promotion.core.sdk.*;
import com.ssm.promotion.core.service.ServerListService;
import com.ssm.promotion.core.util.*;
import com.ssm.promotion.core.util.enums.OrderState;
import com.ssm.promotion.core.util.enums.StateCode;
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
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用来做假数据
 * 1.注册
 * 2.登录
 * 3.支付
 *
 * @author song minghua
 * @date 2019/12/3
 */
@Controller
@RequestMapping("/ttt")
public class TtController {
    private static final Logger log = Logger.getLogger(TtController.class);
    @Autowired
    JedisRechargeCache cache;
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

    @RequestMapping(value = "/autoGame", method = RequestMethod.GET)
    public void autoGame(String accountId, String appId, String serverId, HttpServletResponse response) throws Exception {
        //登录地址
        Map<String, Object> map = new HashMap<>();
        map.put("spId", "0");
        map.put("gameId", appId);
        map.put("serverId", serverId);
        String loginUrl = serverService.selectLoginUrl(map, 0);
        String param = "";
        param += "qid=" + accountId;
        param += "&server_id=" + serverId;
        param += "&time=" + System.currentTimeMillis();

        String sign = MD5Util.md5(param);
        param += "&sign=" + sign;

        JSONObject reply = new JSONObject();
        reply.put("url", loginUrl + param);
        reply.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, reply);
    }

    @RequestMapping(value = "/autoReg", method = RequestMethod.GET)
    public void autoReg(String auto,
                        String appid, HttpServletResponse response) throws Exception {

        //注册账号
        JSONObject reply = new JSONObject();
        Account acc = accountWorker.autoRegister(reply, UtilG.getIpAddress(request));
        if (reply.getInteger("status") == 1) {
            //注册成功 相关数据存入redis
            Map<String, String> map = new HashMap<>();
            map.put("auto", auto);
            map.put("appId", appid);
            map.put("accountId", acc.getId().toString());
            cache.register(map);
        }
        reply.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, reply);
    }

    /**
     * 注册账号
     * SDK 登录接口
     *
     * @param map boolean     auto          自动注册(限渠道账号,无需username,pwd)
     *            int         appId         游戏id*
     *            int         channelId     渠道id*
     *            string      channelUid    渠道账号id*
     *            string      channelUname  渠道账号登录名*
     *            string      channelUnick  渠道账号昵称*
     *            string      username      指悦账户名(为空即可)
     *            string      pwd           指悦账号密码(为空即可)
     *            string      phone         手机号*
     *            string      deviceCode    硬件设备号*
     *            string      imei          国际移动设备识别码
     *            string      addparm       额外参数(为空即可)
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Result sdkRegister(@RequestBody Map<String, String> map) throws Exception {
        System.out.println("register:" + map.toString());

        boolean auto = Boolean.parseBoolean(map.get("auto"));
        int appId = Integer.parseInt(map.get("appId"));
        int channelId = Integer.parseInt(map.get("channelId"));
        String channelUid = map.get("channelUid");
        String channelUname = map.get("channelUname");
        String channelUnick = map.get("channelUnick");
        String username = map.get("username");
        String pwd = map.get("pwd");
        String phone = map.get("phone");
        String deviceCode = map.get("deviceCode");
        String imei = map.get("imei");
        String addparm = map.get("addparm");

        //参数校验
        if (auto) {
            for (String key : map.keySet()) {
                if (StringUtils.isBlank(map.get(key))) {
                    if (key.equals("appId") || key.equals("channelId") || key.equals("channelUid")) {
                        JSONObject result = new JSONObject();
                        result.put("err", "参数非法:" + key + "为空");
                        return ResultGenerator.genSuccessResult(result);
                    }
                }
            }
        } else {
            for (String key : map.keySet()) {
                if (StringUtils.isBlank(map.get(key))) {
                    if (key.equals("appId") || key.equals("username") || key.equals("pwd")) {
                        JSONObject result = new JSONObject();
                        result.put("err", "参数非法:" + key + "为空");
                        return ResultGenerator.genSuccessResult(result);
                    }
                }
            }
        }
        //获取ip
        map.put("ip", UtilG.getIpAddress(request));
        //注册账号
        JSONObject result = accountWorker.reqRegister(map);
        if (result.getInteger("status") == 1) {
            //注册成功 相关数据存入redis
            cache.register(map);
        }
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
    public void sdkLogin(@RequestBody Map<String, String> map,
                         HttpServletResponse response) throws Exception {
        log.info("request: ttt/login , map: " + map.toString());

        int appId = Integer.parseInt(map.get("appId"));
        int channelId = Integer.parseInt(map.get("channelId"));

        JSONObject result = new JSONObject();

        do {
            Account account = accountWorker.getAccount(map);
            if (account == null) {
                result.put("err", "指悦账号不存在，请前往注册！");
                result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
                break;
            }
            int accountId = account.getId();
            if (!loginWorker.isWhiteCanLogin(accountId, "")) {

            }

            if (!loginWorker.isSpCanLogin(appId, channelId)) {

            }
            //获取账号成功 发送token
            String loginToken = loginWorker.getGameInfo(accountId, appId);
            String sign = StringUtils.getBASE64(appId + loginToken + accountId);

            result.put("appid", appId);
            result.put("token", loginToken);
            result.put("uid", accountId);
            result.put("sign", sign);
            result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

            //数据库
            //设置账号登录时间
            Map<String, Object> tmap = new HashMap<>();
            tmap.put("id", accountId);
            tmap.put("lastLoginTime", DateUtil.getCurrentDateStr());
            accountWorker.updateLoginTime(tmap);

        } while (false);

        ResponseUtil.write(response, result);

        System.out.println("request: ttt/login , map: " + result.toString());
        log.info("request: ttt/login , map: " + result.toString());
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
    public void sdkLoginCheck(String appId,
                              String token,
                              String uid,
                              String sign,
                              HttpServletResponse response) throws Exception {
        log.info("request: ttt/check , appId: " + appId + "\ttoken:" + token + "\tsign:" + sign);
        System.out.println("request: ttt/check , appId: " + appId + "\ttoken:" + token + "\tsign:" + sign);
        JSONObject result = new JSONObject();
        do {
            System.out.println(appId);
            System.out.println(token);
            System.out.println(uid);
            System.out.println(sign);


            if (appId == null || token == null || uid == null || sign == null) {
                result.put("status", Constants.SDK_PARAM);
                break;
            }
            int accountId = Integer.parseInt(uid);

            if (!LoginToken.check(accountId, token)) {
                //token 非法
                result.put("status", Constants.SDK_LOGIN_FAIL_TOKEN);
                break;
            }
            String tmpSign = StringUtils.getBASE64(appId + token + accountId);
            if (!tmpSign.equals(sign)) {
                result.put("status", Constants.SDK_LOGIN_FAIL_SIGN);
                break;
            }
            result.put("status", Constants.SDK_LOGIN_SUCCESS);
            result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        } while (false);

        ResponseUtil.write(response, result);

        log.info("request: ttt/check , result\t" + result.toString());
        System.out.println("request: ttt/check , result\t" + result.toString());
    }


    /**
     * 设置角色基本数据
     * 1.创角打点
     * 2.新手指引打点
     *
     * @param map -------key------
     *            createrole    创建新角色时调用
     *            levelup       玩家升级角色时调用
     *            enterServer   选择服务器进入时调用
     *            ------value-----
     *            channelId       玩家渠道id
     *            channelUid      玩家渠道账号id
     *            appId           游戏id
     *            roleId          当前登录的玩家角色ID，必须为数字
     *            roleName        当前登录的玩家角色名，不能为空，不能为null
     *            roleLevel       当前登录的玩家角色等级，必须为数字，且不能为0，若无，传入1 redis
     *            zoneId          当前登录的游戏区服ID，必须为数字，且不能为0，若无，传入1
     *            zoneName        当前登录的游戏区服名称，不能为空，不能为null
     *            balance         用户游戏币余额，必须为数字，若无，传入0
     *            vip             当前用户VIP等级，必须为数字，若无，传入1
     *            partyName       当前角色所属帮派，不能为空，不能为null，若无，传入“无帮派”
     *            roleCTime       单位为秒，创建角色的时间
     *            roleLevelMTime  单位为秒，角色等级变化时间
     */
    @RequestMapping(value = "/setdata", method = RequestMethod.POST)
    @ResponseBody
    public void sdkSetData(@RequestBody Map<String, String> map,
                           HttpServletResponse response) throws Exception {
        log.info("request: ttt/setdata ,map: " + map.toString());
        System.out.println("request: ttt/setdata ,map: " + map.toString());

        String key = map.get("key");
        String value = map.get("value");

        JSONObject result = new JSONObject();

        do {
            JSONObject roleInfo = JSONObject.parseObject(value);

            System.out.println(roleInfo.toJSONString());

            Integer roleId = roleInfo.getInteger("roleId");
            String channelId = roleInfo.getString("channelId");
            String channelUid = roleInfo.getString("channelUid");
            String gameId = roleInfo.getString("appId");
            Integer serverId = roleInfo.getInteger("zoneId");
            Integer roleCTime = roleInfo.getInteger("roleCTime");
            String roleName = roleInfo.getString("roleName");
            BigInteger balance = roleInfo.getBigInteger("balance");

            if (StringUtils.isBlank(roleId, channelId, channelUid, gameId, serverId)) {
                System.out.println("数据为空");
                System.out.println("roleId:" + roleId);
                System.out.println("channelId:" + channelId);
                System.out.println("channelUid:" + channelUid);
                System.out.println("gameId:" + gameId);
                System.out.println("serverId:" + serverId);
                result.put("messgae", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                break;
            }

            Map<String, String> map1 = new HashMap<>();
            map1.put("isChannel", "true");
            map1.put("channelId", channelId);
            map1.put("channelUid", channelUid);
            Account account = accountWorker.getAccount(map1);
            if (account == null) {
                log.error("account is null\t" + map1.toString());
                result.put("messgae", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                break;
            }
            if (key.equals("createrole")) {
                //role 同渠道游戏区服不能重复
                //创建角色
                GameRole gameRole = new GameRole();
                gameRole.setAccountId(account.getId());
                gameRole.setRoleId(roleId);
                gameRole.setChannelId(channelId);
                gameRole.setChannelUid(channelUid);
                gameRole.setGameId(gameId);
                gameRole.setServerId(serverId);
                gameRole.setCreateTime((long) roleCTime);
                gameRole.setLastLoginTime(0L);
                gameRole.setName(roleName);
                //插入mysql
                gameRoleWorker.createGameRole(gameRole);
                //redis
                cache.createRole(gameId, serverId.toString(), channelId, account.getId(), roleId);
            } else if (key.equals("levelup")) {
                Map<String, Object> lmap = new HashMap<>();
                lmap.put("roleId", roleId);
                lmap.put("channelId", channelId);
                lmap.put("gameId", gameId);
                lmap.put("serverId", serverId);
                lmap.put("name", roleName);
                lmap.put("balance", balance);
                //更新mysql
                gameRoleWorker.updateGameRole(lmap);

            } else if (key.equals("enterServer")) {
                Map<String, Object> tmap = new HashMap<>();
                tmap.put("roleId", roleId);
                tmap.put("channelId", channelId);
                tmap.put("gameId", gameId);
                tmap.put("serverId", serverId);
                tmap.put("lastLoginTime", DateUtil.getCurrentDateStr());
                tmap.put("name", roleName);
                tmap.put("balance", balance);
                //更新mysql
                gameRoleWorker.updateGameRole(tmap);

            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("channelId", channelId);
            jsonObject.put("appId", gameId);
            jsonObject.put("zoneId", serverId);
            jsonObject.put("roleId", roleId);
            jsonObject.put("balance", balance);

            result.put("messgae", ResultGenerator.DEFAULT_SUCCESS_MESSAGE);
            result.put("data", jsonObject.toJSONString());
        } while (false);

        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, result);

        log.info("request: ttt/setdata , result\t" + result.toString());
        System.out.println("request: ttt/setdata , result\t" + result.toString());
    }

    /**
     * 进入游戏
     * 若有创建角色的游戏，则选完角色进入游戏内发送
     *
     * @param appId      游戏id
     * @param serverId   区服id
     * @param channelId  渠道id
     * @param channelUid 渠道用户id
     * @param roleId     角色id
     */
    @RequestMapping(value = "/enter", method = RequestMethod.GET)
    public void sdkEnterGame(String appId,
                             String serverId,
                             String channelId,
                             String channelUid,
                             String roleId, HttpServletResponse response) throws Exception {
        log.info("request: ttt/enter , appId: " + appId + "\tserverId:" + serverId +
                "\tchannelId:" + channelId + "\tchannelUid:" + channelUid + "\troleId:" + roleId);
        System.out.println("request: ttt/enter , appId: " + appId + "\tserverId:" + serverId +
                "\tchannelId:" + channelId + "\tchannelUid:" + channelUid + "\troleId:" + roleId);

        JSONObject result = new JSONObject();
        do {
            //查询redis
            //查找角色的指悦账号
            Map<String, String> map = new HashMap<>(3);
            map.put("isChannel", "true");
            map.put("channelId", channelId);
            map.put("channelUid", channelUid);

            Account account = accountWorker.getAccount(map);
            if (account == null) {
                result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                break;
            }
            long accountId = account.getId().longValue();
            //设置活跃玩家、在线玩家
            cache.enterGame(appId, serverId, channelId, accountId);

            //查询mysql
            //设置角色登录时间
            Map<String, Object> tmap = new HashMap<>();
            tmap.put("roleId", roleId);
            tmap.put("channelId", channelId);
            tmap.put("gameId", appId);
            tmap.put("serverId", serverId);
            tmap.put("lastLoginTime", DateUtil.getCurrentDateStr());

            gameRoleWorker.updateGameRole(tmap);
            result.put("message", ResultGenerator.DEFAULT_SUCCESS_MESSAGE);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("channelId", channelId);
            jsonObject.put("appId", appId);
            jsonObject.put("serverId", serverId);
            jsonObject.put("roleId", roleId);
            jsonObject.put("lastLoginTime", DateUtil.getCurrentDateStr());

            result.put("data", jsonObject.toString());
        } while (false);


        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, result);

        log.info("request: ttt/enter , result\t" + result.toString());
        System.out.println("request: ttt/enter , result\t" + result.toString());
    }

    /**
     * 退出游戏
     * 指悦账号
     * ：退出游戏
     *
     * @param appId      游戏id
     * @param serverId   区服id
     * @param channelId  渠道id
     * @param channelUid 渠道用户id
     * @param roleId     角色id
     */
    @RequestMapping(value = "/exit", method = RequestMethod.GET)
    public void sdkExitGame(String appId,
                            String serverId,
                            String channelId,
                            String channelUid,
                            String roleId, HttpServletResponse response) throws Exception {
        log.info("request: ttt/exit , appId: " + appId + "\tserverId:" + serverId +
                "\tchannelId:" + channelId + "\tchannelUid:" + channelUid + "\troleId:" + roleId);
        System.out.println("request: ttt/exit , appId: " + appId + "\tserverId:" + serverId +
                "\tchannelId:" + channelId + "\tchannelUid:" + channelUid + "\troleId:" + roleId);

        //查找角色的指悦账号
        Map<String, String> map = new HashMap<>(3);
        map.put("isChannel", "true");
        map.put("channelId", channelId);
        map.put("channelUid", channelUid);

        JSONObject result = new JSONObject();
        do {
            Account account = accountWorker.getAccount(map);
            if (account == null) {
                result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                break;
            }
            long accountId = account.getId().longValue();

            //查询redis
            //移除在线玩家
            //todo 凌晨在线玩家重新赋值
            String currDay = DateUtil.getCurrentDayStr();
            String userSGSKey = String.format(RedisKey.FORMAT_SGS_SSS, RedisKeyHeader.USER_INFO, channelId, appId, serverId);

            cache.zincrby(userSGSKey + "#" + RedisKeyTail.ACTIVE_PLAYERS, -1, currDay);

            //查询mysql
            //统计玩家在线时间并存储到redis
            Map<String, Object> tmap = new HashMap<>();
            tmap.put("roleId", roleId);
            tmap.put("channelId", channelId);
            tmap.put("gameId", appId);
            tmap.put("serverId", serverId);
            String logintime = gameRoleWorker.getLastLoginTime(tmap);

            System.out.println("logintime:" + logintime);
            //计算在线时间
            //todo

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("channelId", channelId);
            jsonObject.put("appId", appId);
            jsonObject.put("serverId", serverId);
            jsonObject.put("roleId", roleId);
            jsonObject.put("lastLoginTime", DateUtil.getCurrentDateStr());

            result.put("data", jsonObject.toString());

        } while (false);


        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, result);

        log.info("request: ttt/exit , result\t" + result.toString());
        System.out.println("request: ttt/exit , result\t" + result.toString());
    }


    /**
     * 上报充值数据
     *
     * @param accountID      指悦账号id
     * @param channelOrderID 渠道订单号
     * @param productID      当前商品ID
     * @param productName    商品名称
     * @param productDesc    商品描述
     * @param money          单位 分
     * @param roleID         玩家在游戏服中的角色ID
     * @param roleName       玩家在游戏服中的角色名称
     * @param roleLevel      玩家等级
     * @param serverID       玩家所在的服务器ID
     * @param serverName     玩家所在的服务器名称
     * @param extension      额外参数 json
     *                       realMoney      //单位 分，渠道SDK支付成功通知返回的金额，记录，留作查账
     *                       completeTime   //订单完成时间戳(毫秒，13位)-渠道SDK
     *                       sdkOrderTime   //订单交易时间戳(毫秒，13位)-渠道SDK
     * @param status         订单状态
     * @param notifyUrl      支付回调通知的游戏服地址
     * @param signType       签名算法， RSA|MD5
     * @param sign           RSA签名
     */
    @RequestMapping(value = "/payInfo", method = RequestMethod.GET)
    @ResponseBody
    public void sdkPayInfo(int accountID,
                           String channelOrderID,
                           String productID,
                           String productName,
                           String productDesc,
                           int money,
                           String roleID,
                           String roleName,
                           String roleLevel,
                           String serverID,
                           String serverName,
                           String extension,
                           Integer status,
                           String notifyUrl,
                           String signType,
                           String sign,
                           HttpServletResponse response) throws Exception {
//        log.info("request: ttt/payInfo , accountID: " + accountID + "\tchannelOrderID:" + channelOrderID +
//                "\tproductID:" + productID + "\tproductName:" + productName + "\tproductDesc:" + productDesc +
//                "\tmoney:" + money +
//                "\troleID:" + roleID + "\troleName:" + roleName + "\troleLevel:" + roleLevel +
//                "\tserverID:" + serverID + "\tserverName:" + serverName +
//                "\textension:" + extension +
//                "\tstatus:" + status +
//                "\tnotifyUrl:" + notifyUrl +
//                "\tsignType:" + signType +
//                "\tsign:" + sign);
        System.out.println("request: ttt/payInfo , accountID: " + accountID + "\tchannelOrderID:" + channelOrderID +
                "\tproductID:" + productID + "\tproductName:" + productName + "\tproductDesc:" + productDesc +
                "\tmoney:" + money +
                "\troleID:" + roleID + "\troleName:" + roleName + "\troleLevel:" + roleLevel +
                "\tserverID:" + serverID + "\tserverName:" + serverName +
                "\textension:" + extension +
                "\tstatus:" + status +
                "\tnotifyUrl:" + notifyUrl +
                "\tsignType:" + signType +
                "\tsign:" + sign);


        JSONObject result = new JSONObject();
        JSONObject data = new JSONObject();
        do {
            if (channelOrderID == null) {
                data.put("state", StateCode.CODE_PARAM_ERROR);
                break;
            }


            Map<String, Object> map = new HashMap<>();
            map.put("accountId", accountID);
            map.put("roleId", roleID);


            //1.判断用户存不存在 userId
            List<GameRole> roleList = gameRoleWorker.findGamerole(map);
            if (roleList.size() == 0) {
                result.put("message", ResultGenerator.DEFAULT_SUCCESS_MESSAGE);
                data.put("state", StateCode.CODE_USER_NONE);
                break;
            }
            //角色信息 可以获取 游戏id、渠道id
            GameRole role = roleList.get(0);
            System.out.println("role:accountID --->" + role.getAccountId());

            Map<String, String> mapstr = new HashMap<>();
            mapstr.put("isChannel", "true");
            mapstr.put("channelId", role.getChannelId());
            mapstr.put("channelUid", role.getChannelUid());
            Account account = accountWorker.getAccount(mapstr);

            //2.金额合法性
            if (money < 0) {
                log.error("the money is not valid. money:" + money);
                data.put("state", StateCode.CODE_MONEY_ERROR);
                break;
            }

//            //3.验签
//            if (!orderManager.isSignOK(accountID, channelOrderID, productID, productName, productDesc, money,
//                    roleID, roleName, roleLevel, serverID, serverName, extension, status, notifyUrl, signType, sign)) {
//                log.error("the sign is not valid. sign:" + sign);
//
//                result.put("state", StateCode.CODE_SIGN_ERROR);
//                break;
//            }

            //订单是否已经存在
            boolean isPaySuccess = false;
            UOrder order = orderManager.getOrder(role.getGameId(), role.getChannelId(), channelOrderID);
            if (order == null) {
                System.out.println("order is not exist. generateOrder");
                order = orderManager.generateOrder(role, channelOrderID, extension, money, notifyUrl,
                        productDesc, productID, productName, serverID, serverName, status, roleID, roleName);
                if (order == null) {

                    data.put("state", StateCode.CODE_ORDER_ERROR);
                    break;
                }
            } else {
                boolean is_right = order.checkParam(productID, productName, productDesc, money,
                        roleID, roleName, serverID, serverName, status);
                if (!is_right) {

                    data.put("state", StateCode.CODE_PARAM_DIFF);
                    break;
                }
                isPaySuccess = (order.getState() == OrderState.STATE_PAY_SUCCESS);

                System.out.println("Order:\n" + order.toJSON());
                //更新订单
                orderManager.updateOrder(order);
            }

            /*
             * redis
             * 一个订单-只插入一次-充值成功的时候
             * 1.订单存在 更新: STATE_PAY_SUCCESS--->STATE_PAY_FINISHED/STATE_PAY_SUPPLEMENT
             * 2.订单不存在 STATE_PAY_SUCCESS/TATE_PAY_FINISHED/STATE_PAY_SUPPLEMENT
             */
            boolean updateRedis = false;
            if (isPaySuccess && (status == OrderState.STATE_PAY_FINISHED || status == OrderState.STATE_PAY_SUPPLEMENT)) {
                updateRedis = true;
            } else {
                if (status == OrderState.STATE_PAY_SUCCESS || status == OrderState.STATE_PAY_FINISHED || status == OrderState.STATE_PAY_SUPPLEMENT) {
                    updateRedis = true;
                }
            }
            if (updateRedis) {
                cache.reqpay(role.getGameId(), serverID, role.getChannelId(), accountID, roleID, money, account.getCreateTime());
            }

            data.put("orderid", order.getOrderID());
            data.put("state", StateCode.CODE_SUCCESS);
            result.put("message", ResultGenerator.DEFAULT_SUCCESS_MESSAGE);
            System.out.println("status:" + status);
        } while (false);

        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        result.put("data", data.toJSONString());
        ResponseUtil.write(response, result);

        log.info("request: ttt/payInfo , result\t" + result.toString());
        System.out.println("request: ttt/payInfo , result\t" + result.toString());
    }
}