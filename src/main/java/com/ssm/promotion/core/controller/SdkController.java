package com.ssm.promotion.core.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.entity.*;
import com.ssm.promotion.core.jedis.JedisRechargeCache;
import com.ssm.promotion.core.jedis.RedisKey;
import com.ssm.promotion.core.jedis.RedisKeyHeader;
import com.ssm.promotion.core.jedis.RedisKeyTail;
import com.ssm.promotion.core.sdk.AccountWorker;
import com.ssm.promotion.core.sdk.GameRoleWorker;
import com.ssm.promotion.core.sdk.LoginWorker;
import com.ssm.promotion.core.sdk.UOrderManager;
import com.ssm.promotion.core.service.*;
import com.ssm.promotion.core.util.DateUtil;
import com.ssm.promotion.core.util.ResponseUtil;
import com.ssm.promotion.core.util.StringUtils;
import com.ssm.promotion.core.util.UtilG;
import com.ssm.promotion.core.util.enums.OrderState;
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
 * @author song minghua
 * @date 2019/11/27
 */
@Controller
@RequestMapping("/webGame")
public class SdkController {
    private static final Logger log = Logger.getLogger(SdkController.class);
    public static String[] keys = {"createRole", "levelUp", "enterGame", "exitGame"};
    public static String[] mustKeysValue = {
            "appId", "channelId", "channelUid",
            "roleId", "roleName", "roleLevel",
            "zoneId", "zoneName", "balance", "vip",
            "partyName"};
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
    @Resource
    private GameService gameService;
    @Resource
    private GameSpService gameSpService;
    @Resource
    private AccountService accountService;
    @Resource
    private SpService spService;
    @Resource
    private UserService userService;
    @Autowired
    private HttpServletRequest request;
    @Resource
    private UOrderManager orderManager;

    /**
     * 1.初始化游戏
     * 1.获取 需要的js文件
     * 2.获取 渠道秘钥
     *
     * @param GameId    游戏id
     * @param GameKey   游戏秘钥
     * @param channelId 渠道id
     */
    @RequestMapping(value = "/initApi", method = RequestMethod.GET)
    @ResponseBody
    public void initApi(Integer GameId,
                        String GameKey,
                        Integer channelId,
                        HttpServletResponse response) throws Exception {
        log.info("/webGame/initApi\t" + "{" +
                "\tGameId=" + GameId +
                "\tGameKey=" + GameKey +
                "\tSpId=" + channelId +
                "}");
        JSONObject result = new JSONObject();

        Map<String, Object> map = new HashMap<>();
        map.put("gameId", GameId);
        map.put("spId", channelId);

        do {
            //检查游戏秘钥
            List<Game> gameList = gameService.getGameList(map, -1);
            if (gameList == null || gameList.size() != 1) {
                System.out.println("Game err");
                result.put("state", false);
                result.put("message", "游戏不存在！");
                break;
            }
            Game game = gameList.get(0);
            if (!game.getSecertKey().equals(GameKey)) {
                System.out.println("GameKey err");
                result.put("state", false);
                result.put("message", "游戏秘钥错误！");
                break;
            }
            //检查游戏渠道
            List<GameSp> gameSpList = gameSpService.selectGameSp(map, -1);
            if (gameSpList == null || gameSpList.size() != 1) {
                System.out.println("GameSp err");
                result.put("state", false);
                result.put("message", "游戏渠道不存在！");
                break;
            }

            GameSp gameSp = gameSpList.get(0);

            JSONObject channelParams = new JSONObject();
            JSONObject channelPlatform = new JSONObject();
            JSONArray libUrl = new JSONArray();

            channelParams.put("login_key", gameSp.getLoginKey());
            channelParams.put("pay_key", gameSp.getPayKey());
            channelParams.put("send_key", gameSp.getSendKey());

            libUrl.add("http://zy.hysdgame.cn/sdk/common/md5.js");
            libUrl.add("http://zy.hysdgame.cn/sdk/common/jquery-3.4.1.min.js");


            channelPlatform.put("libUrl", libUrl);
            channelPlatform.put("playUrl", "");

            result.put("channelToken", "");
            result.put("channelParams", channelParams);
            result.put("channelPlatform", channelPlatform);
            result.put("state", true);
            result.put("message", "初始化成功！");
        } while (false);

        ResponseUtil.write(response, result);
        log.info("/webGame/initApi\t" + result.toString());
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
    public void sdkRegister(@RequestBody String jsonData,
                            HttpServletResponse response) throws Exception {
        log.info("/webGame/register:" + jsonData);
        JSONObject jsonObject = JSONObject.parseObject(jsonData);
        JSONObject result = new JSONObject();
        do {
            boolean auto = jsonObject.getBoolean("auto");
            //参数校验
            if (auto) {
                for (String key : jsonObject.keySet()) {
                    if (StringUtils.isBlank(jsonObject.get(key))) {
                        if ("appId".equals(key) || "channelId".equals(key) || "channelUid".equals(key)) {
                            result.put("message", "参数非法:" + key + "为空");
                            result.put("state", false);
                            break;
                        }
                    }
                }
            } else {
                for (String key : jsonObject.keySet()) {
                    if (StringUtils.isBlank(jsonObject.get(key))) {
                        if ("appId".equals(key) || "username".equals(key) || "pwd".equals(key)) {
                            result.put("message", "参数非法:" + key + "为空");
                            result.put("state", false);
                            break;
                        }
                    }
                }
            }
            //获取ip
            jsonObject.put("ip", UtilG.getIpAddress(request));
            result = accountWorker.reqRegister(jsonObject);
        } while (false);

        ResponseUtil.write(response, result);
        log.info("/webGame/initApi\t" + result.toString());
    }

    /**
     * 2.指悦账号登录
     * 客户端先请求
     * SDK 登录接口
     *
     * @param isAuto     是否渠道自动登录*             渠道登录可以不输入(name pwd),非渠道登录必须输入(name pwd)
     * @param GameId     指悦平台创建的游戏ID
     * @param channelId  平台标示的渠道ID
     * @param channelUid 渠道SDK标示的用户ID
     * @param username   指悦账号名称，需要uri
     * @param password   指悦账号密码，需要uri
     * @param timestamp  时间戳
     * @param sign       签名数据，使用loginkey
     */
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    @ResponseBody
    public void sdkLogin(Boolean isAuto,
                         Integer GameId,
                         String channelId,
                         String channelUid,
                         String username,
                         String password,
                         String timestamp,
                         String sign,
                         HttpServletResponse response) throws Exception {
        log.info("start /webGame/login" + "\t" + "{" + "\t" +
                "isAuto = " + isAuto + "\t" + "GameId = " + GameId + "\t" + "SpId = " + channelId + "\t" +
                "channelUid = " + channelUid + "\t" +
                "username = " + username + "\t" + "password = " + password + "\t" + "}");

        JSONObject result = new JSONObject();
        do {
            if (isAuto == null || GameId == null) {
                result.put("message", "渠道uid登录，参数为空:" + "GameId GameId");
                result.put("state", false);
                break;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("gameId", GameId);
            map.put("spId", channelId);

            //检查游戏秘钥
            List<Game> gameList = gameService.getGameList(map, -1);
            if (gameList == null || gameList.size() != 1) {
                System.out.println("Game err");
                result.put("state", false);
                result.put("message", "游戏不存在！");
                break;
            }
            Game game = gameList.get(0);
            String gameKey = game.getSecertKey();

            List<GameSp> gameSpList = gameSpService.selectGameSp(map, -1);
            if (gameSpList == null || gameSpList.size() != 1) {
                result.put("message", "游戏渠道查询失败");
                result.put("state", false);
                break;
            }

            if (!accountWorker.checkSign(isAuto, GameId, channelId, channelUid, username, password, timestamp, gameKey, sign)) {
                result.put("message", "签名错误");
                result.put("state", false);
                break;
            }
            Account account;
            if (isAuto) {
                account = accountService.findUserBychannelUid(channelId, channelUid);
                if (account == null) {
                    result.put("message", "无此渠道用户");
                    result.put("state", false);
                    break;
                }
            } else {
                if (username.isEmpty() || password.isEmpty()) {
                    result.put("message", "账号密码登录，参数为空:" + "username password ");
                    result.put("state", false);
                    break;
                }
                List<Account> accountList = accountService.findAccountByname(username);
                if (accountList == null || accountList.size() != 1) {
                    result.put("message", "无此渠道用户");
                    result.put("state", false);
                    break;
                }
                account = accountList.get(0);
                if (!account.getPwd().equals(password)) {
                    result.put("message", "密码错误");
                    result.put("state", false);
                    break;
                }
            }
            //指悦uid
            int accountId = account.getId();
            //白名单
            if (!loginWorker.isWhiteCanLogin(accountId, "")) {

            }
            //渠道登录限制
            if (!loginWorker.isSpCanLogin(GameId, Integer.parseInt(channelId))) {

            }
            String tokenParam = cache.getToken(String.valueOf(GameId), Integer.parseInt(channelId), String.valueOf(channelUid));
            if (tokenParam == null || tokenParam.isEmpty()) {
                tokenParam = cache.saveToken(String.valueOf(GameId), Integer.parseInt(channelId), String.valueOf(channelUid));
            }
            String[] tokens = tokenParam.split("#");
            String token = tokens[0];

            result.put("message", "登陆成功");
            result.put("state", true);
            result.put("GameId", GameId);
            result.put("channelId", channelId);
            result.put("channelUid", channelUid);
            result.put("zyUid", accountId);
            result.put("channelToken", token);
            result.put("username", account.getName());
            result.put("password", account.getPwd());
            result.put("loginUrl", loginWorker.loadLoginUrl(game.getLoginUrl(), accountId, GameId, 1));
            result.put("paybackUrl", game.getPaycallbackUrl());
            //数据库-设置账号登录时间
            map.clear();
            map.put("id", accountId);
            map.put("lastLoginTime", DateUtil.getCurrentDateStr());
            accountWorker.updateLoginTime(map);
        } while (false);

        ResponseUtil.write(response, result);
        log.info("end /webGame/login" + result.toString());
    }

    /**
     * 设置角色基本数据
     * 1.创角打点
     * 2.新手指引打点
     *
     * @param JsonData json字符串<p>
     *                 key=<p>
     *                 createRole          创建新角色时调用<p>
     *                 enterGame           选择游戏内进入时调用<p>
     *                 levelUp             玩家升级角色时调用<p>
     *                 value=
     *                 appId                游戏id<p>
     *                 channelId            玩家渠道id<p>
     *                 channelUid           玩家渠道账号id<p>
     *                 roleId               当前登录的玩家角色ID，必须为数字<p>
     *                 roleName             当前登录的玩家角色名，不能为空，不能为null<p>
     *                 roleLevel            当前登录的玩家角色等级，必须为数字，且不能为0，若无，传入1<p>
     *                 zoneId               当前登录的游戏区服ID，必须为数字，且不能为0，若无，传入1<p>
     *                 zoneName             当前登录的游戏区服名称，不能为空，不能为null<p>
     *                 balance              用户游戏币余额，必须为数字，若无，传入0<p>
     *                 vip                  当前用户VIP等级，必须为数字，若无，传入1<p>
     *                 partyName            当前角色所属帮派，不能为空，不能为null，若无，传入“无帮派”<p>
     *                 roleCTime            单位为毫秒，创建角色的时间<p>
     *                 roleLevelMTime       单位为毫秒，角色等级变化时间<p>
     */
    @RequestMapping(value = "/setData", method = RequestMethod.POST)
    @ResponseBody
    public void sdkRoleData(@RequestBody String JsonData,
                            HttpServletResponse response) throws Exception {
        log.info("start /webGame/setData" + "\t" + JsonData);

        JSONObject jsonObject = JSONObject.parseObject(JsonData);
        JSONObject result = new JSONObject();

        do {
            String key = jsonObject.getString("key");
            JSONObject roleInfo = jsonObject.getJSONObject("value");
            boolean hasKey = false;
            for (String index : keys) {
                if (index.equals(key)) {
                    hasKey = true;
                    break;
                }
            }
            if (!hasKey) {
                result.put("message", "key值不正确");
                result.put("state", false);
                break;
            }

            for (String index : mustKeysValue) {
                if (!roleInfo.containsKey(index)) {
                    result.put("message", "缺失参数：" + index);
                    result.put("state", false);
                    break;
                }
            }
            String gameId = roleInfo.getString("appId");
            String channelId = roleInfo.getString("channelId");
            String channelUid = roleInfo.getString("channelUid");
            Integer zoneId = roleInfo.getInteger("zoneId");
            long roleId = roleInfo.getLong("roleId");
            String roleName = roleInfo.getString("roleName");
            BigInteger balance = roleInfo.getBigInteger("balance");

            Map<String, Object> map = new HashMap<>();
            map.put("isChannel", "true");
            map.put("channelId", channelId);
            map.put("channelUid", channelUid);

            Account account = accountService.findUserBychannelUid(channelId, channelUid);
            if (account == null) {
                result.put("message", "账号不存在");
                result.put("state", false);
                break;
            }
            switch (key) {
                case "createRole": {
                    long roleCTime = roleInfo.getLongValue("roleCTime");

                    //role 同渠道游戏区服不能重复-创建角色
                    GameRole gameRole = new GameRole();
                    gameRole.setAccountId(account.getId());
                    gameRole.setRoleId(roleId);
                    gameRole.setChannelId(channelId);
                    gameRole.setChannelUid(channelUid);
                    gameRole.setGameId(gameId);
                    gameRole.setServerId(zoneId);
                    gameRole.setCreateTime(DateUtil.formatDate(roleCTime, DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
                    gameRole.setLastLoginTime(DateUtil.formatDate(System.currentTimeMillis(), DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
                    gameRole.setName(roleName);
                    //插入mysql
                    boolean res = gameRoleWorker.createGameRole(gameRole);
                    if (!res) {
                        result.put("message", "角色已存在");
                        result.put("state", false);
                        break;
                    }
                    //redis
                    try {
                        cache.createRole(gameId, zoneId.toString(), channelId, account.getId(), roleId);
                        result.put("message", "创建角色 上报成功");
                        result.put("state", true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        result.put("message", "创建角色 上报失败");
                        result.put("state", false);
                    }
                }
                break;
                case "levelUp": {
                    map.clear();
                    map.put("gameId", gameId);
                    map.put("channelId", channelId);
                    map.put("serverId", zoneId);
                    map.put("roleId", roleId);
                    map.put("name", roleName);
                    map.put("balance", balance);
                    //更新mysql
                    gameRoleWorker.updateGameRole(map);
                    result.put("message", "角色升级 上报成功");
                    result.put("state", true);
                }
                break;
                case "enterGame":
                case "exitGame": {
                    map.clear();
                    map.put("gameId", gameId);
                    map.put("channelId", channelId);
                    map.put("serverId", zoneId);
                    map.put("channelUid", channelUid);
                    map.put("roleId", roleId);

                    GameRole gameRole;
                    //1.判断用户存不存在 userId
                    GameRole roleList = gameRoleWorker.findGameRole(map);
                    if (roleList == null) {
                        result.put("message", "角色信息不存在 ");
                        result.put("state", false);
                        break;
//                    } else if (roleList.size() > 1) {
//                        result.put("message", "角色信息异常 请联系平台");
//                        result.put("state", false);
//                        break;
//                    } else {
//                        gameRole = roleList.get(0);
                    }
                    gameRole = roleList;
                    if ("enterGame".equals(key)) {
                        //查询mysql-设置角色登录时间
                        map.clear();
                        map.put("roleId", roleId);
                        map.put("channelId", channelId);
                        map.put("gameId", gameId);
                        map.put("serverId", zoneId);
                        map.put("lastLoginTime", DateUtil.getCurrentDateStr());
                        gameRoleWorker.updateGameRole(map);
                        result.put("message", "进入游戏 上报成功");
                        result.put("state", true);

                        //设置活跃玩家、在线玩家
                        cache.enterGame(gameId, String.valueOf(zoneId), channelId, account.getId());
                        //设置区服信息
                        cache.setServerInfo(gameId, channelId, String.valueOf(zoneId));
                    } else {
                        //查询redis-移除在线玩家
                        String currDay = DateUtil.getCurrentDayStr();
                        String userSGSKey = String.format(RedisKey.FORMAT_SGS_SSS, RedisKeyHeader.USER_INFO, channelId, gameId, zoneId);

                        cache.zIncrBy(userSGSKey + "#" + RedisKeyTail.ACTIVE_PLAYERS, -1, currDay);
                        result.put("message", "退出游戏 上报成功");
                        result.put("state", true);
                    }
                }
                break;
                default: {
                    result.put("message", "key 错误");
                    result.put("state", false);
                }
                break;
            }
            if (!result.containsKey("state") || !result.getBoolean("state")) {
                break;
            }

            result.put("GameId", gameId);
            result.put("channelId", channelId);
            result.put("chanelUid", channelUid);
            result.put("zoneId", zoneId);
            result.put("roleId", roleId);
            result.put("balance", balance);
        } while (false);

        ResponseUtil.write(response, result);

        log.info("/webGame/setData\t" + result.toString());
    }

    /**
     * 上报充值数据
     *
     * @param jsonData json 字符串
     *                 accountID           指悦账号id
     *                 channelID           渠道id
     *                 channelUid          渠道账号id
     *                 appID               游戏id
     *                 channelOrderID      渠道订单号
     *                 productID           当前商品ID
     *                 productName         商品名称
     *                 productDesc         商品描述
     *                 money               商品价格,单位:分
     *                 roleID              玩家在游戏服中的角色ID
     *                 roleName            玩家在游戏服中的角色名称
     *                 roleLevel           玩家等级
     *                 serverID            玩家所在的服务器ID
     *                 serverName          玩家所在的服务器名称
     *                 realMoney           订单完成,实际支付金额,单位:分,未完成:-1
     *                 completeTime        订单完成时间戳(毫秒，13位),未完成为:-1
     *                 sdkOrderTime        订单创建时间戳(毫秒，13位)
     *                 status              订单状态 请看OrderStatus、OrderStatusDesc
     *                 notifyUrl           支付回调通知的游戏服地址
     *                 signType            签名算法,RSA|MD5,默认MD5
     *                 sign                签名
     */
    @RequestMapping(value = "/payInfo", method = RequestMethod.POST)
    @ResponseBody
    public void sdkPayInfo(@RequestBody String jsonData,
                           HttpServletResponse response) throws Exception {
        log.info("start /webGame/payInfo " + jsonData);

        JSONObject request = JSONObject.parseObject(jsonData);

        int accountId = request.getInteger("accountID");
        int channelId = request.getInteger("channelId");
        String channelUid = request.getString("channelUid");
        int appId = request.getInteger("appId");
        String channelOrderID = request.getString("channelOrderID");

        String productID = request.getString("productID");
        String productName = request.getString("productName");
        String productDesc = request.getString("productDesc");
        int money = request.getInteger("money");

        String roleID = request.getString("roleID");
        String roleName = request.getString("roleName");
        String roleLevel = request.getString("roleLevel");

        int serverID = request.getInteger("serverID");
        String serverName = request.getString("serverName");

        int realMoney = request.getIntValue("realMoney");
        String completeTime = request.getString("completeTime");
        String sdkOrderTime = request.getString("sdkOrderTime");
        int status = request.getInteger("status");
        String notifyUrl = request.getString("notifyUrl");
        String signType = request.getString("signType");

        String sign = request.getString("sign");

        JSONObject result = new JSONObject();
        do {
            if (channelOrderID == null) {
                result.put("message", "订单号为空");
                result.put("state", false);
                break;
            }
            //1.判断账号是否存在
            Map<String, Object> map = new HashMap<>();
            map.put("isChannel", "true");
            map.put("channelId", String.valueOf(channelId));
            map.put("channelUid", channelUid);
            Account account = accountWorker.getAccount(map);
            if (account == null) {
                result.put("message", "账号不存在");
                result.put("state", false);
                break;
            }

            //2.判断角色存不存在
            map.clear();
            map.put("accountId", accountId);
            map.put("serverId", serverID);
            map.put("roleId", roleID);
            map.put("channelId", channelId);
            map.put("channelUid", channelUid);

            List<GameRole> roleList = gameRoleWorker.findGamerole(map);
            if (roleList.size() == 0 || !roleList.get(0).getGameId().equals(String.valueOf(appId))) {
                result.put("message", "角色不存在");
                result.put("state", false);
                break;
            }
            //角色信息 可以获取 游戏id、渠道id
            GameRole role = roleList.get(0);

            //2.金额合法性
            if (money < 0) {
                log.info("the money is not valid. money:" + money);
                result.put("message", "金额错误");
                result.put("state", false);
                break;
            }

            map.clear();
            map.put("gameId", appId);
            map.put("spId", channelId);
            //检查游戏秘钥
            List<Game> gameList = gameService.getGameList(map, -1);
            if (gameList == null || gameList.size() != 1) {
                log.error("Game err : gameId=" + appId + "\tspId=" + channelId);
                result.put("state", false);
                result.put("message", "游戏不存在！");
                break;
            }
            Game game = gameList.get(0);
            String gameKey = game.getSecertKey();
            String notify = game.getPaycallbackUrl();
            //3.验签
            if (!UOrderManager.isSignOK(accountId, channelId, channelUid, appId,
                    channelOrderID, productID, productName, productDesc, money,
                    roleID, roleName, roleLevel,
                    serverID, serverName, realMoney,
                    completeTime, sdkOrderTime, status, notifyUrl, signType, sign, gameKey)) {
                log.error("the sign is not valid. sign:" + sign);
                result.put("message", "签名错误");
                result.put("state", false);
                break;
            }


            //订单是否已经存在
            boolean isPaySuccess = false;
            UOrder order = orderManager.getOrder(String.valueOf(appId), String.valueOf(channelId), channelOrderID);
            if (order == null) {
                log.error("order is not exist. generateOrder");
                order = orderManager.generateOrder(accountId, channelId, channelUid, appId,
                        channelOrderID, productID, productName, productDesc, money,
                        roleID, roleName, roleLevel,
                        serverID, serverName, realMoney,
                        completeTime, sdkOrderTime, status, notify);

                if (order == null) {
                    result.put("message", "订单不存在");
                    result.put("state", false);
                    break;
                }
            } else {
                //参数合法 能否更新订单
                boolean is_right = order.checkParam(productID, productName, productDesc, money,
                        roleID, roleName, serverID, serverName, status, sdkOrderTime);
                if (!is_right) {
                    result.put("message", "订单参数不一致");
                    result.put("state", false);
                    break;
                }
                isPaySuccess = (order.getState() == OrderState.STATE_PAY_SUCCESS);

                log.info("Order:\n" + order.toJSON());
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
                cache.reqPay(role.getGameId(), String.valueOf(serverID), role.getChannelId(), accountId, roleID, money, account.getCreateTime());
            }

            result.put("message", "上报订单数据成功");
            result.put("state", true);
            result.put("orderId", String.valueOf(order.getOrderID()));
        } while (false);

        ResponseUtil.write(response, result);

        log.info("end   /webGame/payInfo \t" + result.toString());
    }
}
