package com.ssm.promotion.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.common.Result;
import com.ssm.promotion.core.common.ResultGenerator;
import com.ssm.promotion.core.entity.Account;
import com.ssm.promotion.core.entity.GameRole;
import com.ssm.promotion.core.entity.UOrder;
import com.ssm.promotion.core.jedis.JedisRechargeCache;
import com.ssm.promotion.core.jedis.RedisKeyHeader;
import com.ssm.promotion.core.jedis.RedisKeyTail;
import com.ssm.promotion.core.sdk.*;
import com.ssm.promotion.core.util.DateUtil;
import com.ssm.promotion.core.util.ResponseUtil;
import com.ssm.promotion.core.util.StringUtils;
import com.ssm.promotion.core.util.UtilG;
import com.ssm.promotion.core.util.enums.PayState;
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
    @Autowired
    private HttpServletRequest request;
    @Resource
    private UOrderManager orderManager;

    /**
     * 注册账号
     * SDK 登录接口
     *
     * @param map auto          自动注册(限渠道账号,无需账号密码)*
     *            appId         游戏id*
     *            channelId     渠道id*
     *            channelUid    渠道账号id*
     *            channelUname  渠道账号名称*
     *            channelUnick  渠道账号昵称*
     *            username      指悦账户名
     *            pwd           指悦账号密码
     *            phone         手机号
     *            deviceCode
     *            imei
     *            addparm       额外参数
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Result sdkRegister(@RequestBody Map<String, String> map) throws Exception {
        System.out.println("register:" + map.toString());

        //参数校验
        if (Boolean.parseBoolean(map.get("auto"))) {
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
        if (result.getString("status").equals("1")) {
            //注册成功
            //存到redis
            cache.register(map);
        }
        return ResultGenerator.genSuccessResult(result);
    }

    /**
     * 指悦账号登录
     * 客户端先请求
     * SDK 登录接口
     *
     * @param map appId             指悦平台创建的游戏ID*      请使用URLEncoder编码
     *            isChannel         是否渠道登录*             渠道登录可以不输入(name pwd),非渠道登录必须输入(name pwd)
     *            channelId         平台标示的渠道SDKID       请使用URLEncoder编码
     *            channelUid        渠道SDK标示的用户ID       请使用URLEncoder编码
     *            name              指悦账号名称
     *            pwd               指悦账号密码
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public void sdkLogin(@RequestBody Map<String, String> map,
                         HttpServletResponse response) throws Exception {

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

    }

    /**
     * 指悦账号登录验证
     * 验证账号信息
     *
     * @param appId 指悦平台创建的游戏ID，appId
     *              请使用URLEncoder编码
     * @param token 随机字符串
     *              请使用URLEncoder编码
     * @param uid   玩家指悦账号id
     *              请使用URLEncoder编码
     * @param sign  签名数据：md5 (appId+token+uid)
     *              请使用URLEncoder编码
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
        System.out.println("setdata:" + map.toString());
        String key = map.get("key");
        String value = map.get("value");
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
            return;
        }

        Map<String, String> map1 = new HashMap<>();
        map1.put("isChannel", "true");
        map1.put("channelId", channelId);
        map1.put("channelUid", channelUid);
        Account account = accountWorker.getAccount(map1);
        if (account == null) {
            log.error("account is null\t" + map1.toString());
            return;
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
            cache.createRole(gameId, serverId.toString(), channelId, account.getId());
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
        JSONObject result = new JSONObject();
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        result.put("roleId", roleId);
        ResponseUtil.write(response, result);

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
        //查询redis
        //查找角色的指悦账号
        Map<String, String> map = new HashMap<>(3);
        map.put("isChannel", "true");
        map.put("channelId", channelId);
        map.put("channelUid", channelUid);
        Account account = accountWorker.getAccount(map);
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
        //todo
        JSONObject result = new JSONObject();
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, result);
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
        //查找角色的指悦账号
        Map<String, String> map = new HashMap<>(3);
        map.put("isChannel", "true");
        map.put("channelId", channelId);
        map.put("channelUid", channelUid);
        Account account = accountWorker.getAccount(map);
        long accountId = account.getId().longValue();

        //查询redis
        //移除在线玩家
        String currDay = DateUtil.getCurrentDayStr();
        String key = String.format("%s:spid:%s:gid:%s:sid:%s:date:%s#%s",
                RedisKeyHeader.USER_INFO, channelId, appId, serverId, currDay,
                RedisKeyTail.ONLINE_PLAYERS);
        cache.setbit(key, accountId, false);

        //查询mysql
        //统计玩家在线时间并存储到redis
        Map<String, Object> tmap = new HashMap<>();
        tmap.put("roleId", roleId);
        tmap.put("channelId", channelId);
        tmap.put("gameId", appId);
        tmap.put("serverId", serverId);
        String logintime = gameRoleWorker.getLastLoginTime(tmap);
        System.out.println("logintime:" + logintime);
        //计算时间
//        DateUtil
        //todo
        JSONObject result = new JSONObject();
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, result);
    }

    /**
     * 定额计费接口
     *
     * @param context           上下文Activity
     * @param unitPrice         游戏道具价格，单位为人民币分
     * @param itemName          虚拟货币名称(商品名称)
     *                          注意：虚拟币名称在游戏内一定要确保唯一性！！！！不能出现多个虚拟币名称相同。
     * @param count             用户选择购买道具界面的默认道具数量。（总价为 count*unitPrice）
     * @param callBackInfo      由游戏开发者定义传入的字符串，会与支付结果一同发送给游戏服务器，游戏服务器可通过该字段判断交易的详细内容（金额角色等）
     * @param callBackUrl       将支付结果通知给游戏服务器时的通知地址url，交易结束后，系统会向该url发送http请求，通知交易的结果金额callbackInfo等信息
     *                          注意：这里的回调地址可以填也可以为空字串，如果填了则以这里的回调地址为主，如果为空则以易接开发者中心设置的回调地址为准。
     * @param payResultListener 支付回调接口
     */
    @RequestMapping(value = "/pay", method = RequestMethod.GET)
    public void sdkPay(String context,
                       String unitPrice,
                       String itemName,
                       String count,
                       String callBackInfo,
                       String callBackUrl,
                       String payResultListener) {

    }

    /**
     * 充值校验
     */
    @RequestMapping(value = "/paycheck", method = RequestMethod.GET)
    public void sdkPayCheck(String context,
                            String unitPrice,
                            String itemName,
                            String count,
                            String callBackInfo,
                            String callBackUrl,
                            String payResultListener) {

    }

    /**
     * 上报充值数据
     *
     * @param userID         指悦账号id
     * @param channelOrderID 渠道订单号
     * @param productID      当前商品ID
     * @param productName    商品名称
     * @param productDesc    游戏版本
     * @param money          单位 分
     * @param roleID         玩家在游戏服中的角色ID
     * @param roleName       玩家在游戏服中的角色名称
     * @param roleLevel      玩家等级
     * @param serverID       玩家所在的服务器ID
     * @param serverName     玩家所在的服务器名称
     * @param extension      额外参数 json
     *                       realMoney //单位 分，渠道SDK支付回调通知返回的金额，记录，留作查账
     *                       completeTime //订单创建时间
     *                       sdkOrderTime //渠道SDK那边订单交易时间
     * @param status         订单状态
     * @param notifyUrl      支付回调通知的游戏服地址
     * @param signType       签名算法， RSA|MD5
     * @param sign           RSA签名
     */
    @RequestMapping(value = "/payInfo", method = RequestMethod.GET)
    @ResponseBody
    public void sdkPayInfo(int userID,
                           String channelOrderID,
                           String extension, int money, String notifyUrl,
                           String productDesc, String productID, String productName,
                           String serverID, String serverName, Integer status,
                           String roleID, String roleName,
                           String signType, String sign,
                           String roleLevel, HttpServletResponse response) throws Exception {
        net.sf.json.JSONObject result = new net.sf.json.JSONObject();

        Account account = new Account();
        Map<String, Object> map = new HashMap<>();
        map.put("accountId", userID);
        map.put("roleId", roleID);
        GameRole role = gameRoleWorker.findGamerole(map).get(0);

        //1.判断用户存不存在 userId
        if (role == null) {
            log.error("the money is not valid. money:" + money);
            result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
            result.put("state", StateCode.CODE_USER_NONE);
            ResponseUtil.write(response, result);
        }
        //2.金额合法性
        if (money < 0) {
            log.error("the money is not valid. money:" + money);
            result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
            result.put("state", StateCode.CODE_MONEY_ERROR);
            ResponseUtil.write(response, result);
            return;
        }

        //3.验签
//        if (!orderManager.isSignOK(userID, productID, productName, productDesc, money, roleID, roleName, roleLevel,
//                serverID, serverName, extension, notifyUrl, signType, sign, channelOrderID)) {
//
//            log.error("the sign is not valid. sign:" + sign);
//            result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
//            result.put("state", StateCode.CODE_SIGN_ERROR);
//            ResponseUtil.write(response, result);
//            return;
//        }

        final UOrder order = orderManager.generateOrder(role, channelOrderID, extension, money, notifyUrl,
                productDesc, productID, productName, serverID, serverName, status, roleID, roleName);

        if (order == null) {
            result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
            result.put("state", StateCode.CODE_ORDER_ERROR);
            ResponseUtil.write(response, result);
            return;
        }
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        result.put("state", StateCode.CODE_SUCCESS);
        ResponseUtil.write(response, result);

        System.out.println("status:" + status);
        //redis
        //充值成功
        if (status == PayState.STATE_SUCCESS || status == PayState.STATE_PAY_DONE) {
            cache.reqpay(role.getGameId(), serverID, role.getChannelId(), userID, money);
        }
    }
}