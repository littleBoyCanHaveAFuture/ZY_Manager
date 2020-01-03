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
        HttpServletResponse rsp = (HttpServletResponse) response;
        String[] allowDomain = {"http://127.0.0.1:8080", "http://lh5ds.yy66game.com/", "http://47.101.44.31:8080"};
        Set<String> allowedOrigins = new HashSet<>(Arrays.asList(allowDomain));
//        String originHeader = ((HttpServletRequest) rsp).getHeader("Origin");
//        log.info("originHeader:" + originHeader);
//        log.info("allowedOrigins:" + allowedOrigins);
//        if (allowedOrigins.contains(originHeader)) {
        response.addHeader("Access-Control-Allow-Origin", "http://lh5ds.yy66game.com/");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.addHeader("Access-Control-Max-Age", "3600");
        response.addHeader("Access-Control-Allow-Headers", "X-Custom-Header,accept,content-type");
        //允许客户端发送cookies true表示接收，false不接受 默认为false？
        response.addHeader("Access-Control-Allow-Credentials", "true");
//        }
    }

    /**
     * 官方一键登录
     */
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
        if (appId.equals("2")) {
            param += "&server_id=" + serverId;
        }
        param += "&time=" + System.currentTimeMillis();

        String sign = MD5Util.md5(param);
        param += "&sign=" + sign;

        JSONObject reply = new JSONObject();
        reply.put("url", loginUrl + param);
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
            Map<String, String> map = new HashMap<>();
            map.put("auto", auto);
            map.put("appId", appid);
            map.put("accountId", acc.getId().toString());
            cache.register(map);
        }
        reply.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        setResponseAccess(response);
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
        log.info("register:" + map.toString());

        boolean auto = Boolean.parseBoolean(map.get("auto"));
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
        if (result.getString("message").equals(ResultGenerator.DEFAULT_SUCCESS_MESSAGE)) {
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
                                HttpServletResponse response) throws Exception {
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


    /**
     * 设置角色基本数据
     * 1.创角打点
     * 2.新手指引打点
     *
     * @param map <p>-------key------<p>
     *            createrole    创建新角色时调用<p>
     *            levelup       玩家升级角色时调用<p>
     *            enterServer   选择服务器进入时调用<p>
     *            ------value-----<p>
     *            channelId       玩家渠道id<p>
     *            channelUid      玩家渠道账号id<p>
     *            appId           游戏id<p>
     *            roleId          当前登录的玩家角色ID，必须为数字<p>
     *            roleName        当前登录的玩家角色名，不能为空，不能为null<p>
     *            roleLevel       当前登录的玩家角色等级，必须为数字，且不能为0，若无，传入1<p>
     *            zoneId          当前登录的游戏区服ID，必须为数字，且不能为0，若无，传入1<p>
     *            zoneName        当前登录的游戏区服名称，不能为空，不能为null<p>
     *            balance         用户游戏币余额，必须为数字，若无，传入0<p>
     *            vip             当前用户VIP等级，必须为数字，若无，传入1<p>
     *            partyName       当前角色所属帮派，不能为空，不能为null，若无，传入“无帮派”<p>
     *            roleCTime       单位为毫秒，创建角色的时间<p>
     *            roleLevelMTime  单位为毫秒，角色等级变化时间<p>
     */
    @RequestMapping(value = "/setdata", method = RequestMethod.POST)
    @ResponseBody
    public void sdkSetData(@RequestBody Map<String, String> map,
                           HttpServletResponse response) throws Exception {
        log.info("request: /ttt/setdata ,map: " + map.toString());

        String key = map.get("key");
        String value = map.get("value");

        log.info("key\t" + key);
        log.info("value\t" + value);

        JSONObject result = new JSONObject();


        JSONObject roleInfo = JSONObject.parseObject(value);

        long roleId = roleInfo.getLong("roleId");
        String channelId = roleInfo.getString("channelId");
        String channelUid = roleInfo.getString("channelUid");
        String gameId = roleInfo.getString("appId");
        Integer serverId = roleInfo.getInteger("zoneId");
        long roleCTime = roleInfo.getLongValue("roleCTime");
        String roleName = roleInfo.getString("roleName");
        BigInteger balance = roleInfo.getBigInteger("balance");

        if (StringUtils.isBlank(roleId, channelId, channelUid, gameId, serverId)) {
            log.info("数据为空");

            result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
            result.put("reason", "数据为空");
            ResponseUtil.write(response, result);
            return;
        }

        Map<String, String> map1 = new HashMap<>();
        map1.put("isChannel", "true");
        map1.put("channelId", channelId);
        map1.put("channelUid", channelUid);

        Account account = accountWorker.getAccount(map1);
        if (account == null) {
            log.info("account is null\t" + map1.toString());
            result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
            result.put("reason", "账号不存在");
            ResponseUtil.write(response, result);
            return;
        }
        log.info("key\t" + key);
        if ("createRole".equals(key)) {

            //role 同渠道游戏区服不能重复
            //创建角色
            GameRole gameRole = new GameRole();
            gameRole.setAccountId(account.getId());
            gameRole.setRoleId(roleId);
            gameRole.setChannelId(channelId);
            gameRole.setChannelUid(channelUid);
            gameRole.setGameId(gameId);
            gameRole.setServerId(serverId);
            gameRole.setCreateTime(DateUtil.formatDate(roleCTime, DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
            gameRole.setLastLoginTime(DateUtil.formatDate(System.currentTimeMillis(), DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
            gameRole.setName(roleName);
            //插入mysql
            boolean res = gameRoleWorker.createGameRole(gameRole);
            if (!res) {
                result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                result.put("reason", "角色已存在");
                ResponseUtil.write(response, result);
                return;
            }
            //redis
            cache.createRole(gameId, serverId.toString(), channelId, account.getId(), roleId);
        } else if ("levelUp".equals(key)) {
            Map<String, Object> lmap = new HashMap<>();
            lmap.put("roleId", roleId);
            lmap.put("channelId", channelId);
            lmap.put("gameId", gameId);
            lmap.put("serverId", serverId);
            lmap.put("name", roleName);
            lmap.put("balance", balance);
            //更新mysql
            gameRoleWorker.updateGameRole(lmap);

        } else if ("enterServer".equals(key)) {
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
        } else {
            result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
            result.put("reason", "key 错误");
            ResponseUtil.write(response, result);
            return;
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("channelId", channelId);
        jsonObject.put("appId", gameId);
        jsonObject.put("zoneId", serverId);
        jsonObject.put("roleId", roleId);
        jsonObject.put("balance", balance);

        result.put("message", ResultGenerator.DEFAULT_SUCCESS_MESSAGE);
        result.put("reason", "上报成功");
        result.put("data", jsonObject.toJSONString());
        ResponseUtil.write(response, result);

        log.info("request: ttt/setdata , result\t" + result.toString());

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
        log.info("request: ttt/enter , appId: " + appId + "\tserverId:" + serverId +
                "\tchannelId:" + channelId + "\tchannelUid:" + channelUid + "\troleId:" + roleId);

        JSONObject result = new JSONObject();
        do {
            if (appId.isEmpty() || serverId.isEmpty() || channelId.isEmpty() || channelUid.isEmpty()) {
                result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                result.put("reason", "参数为空");
                break;
            }
            //查询redis
            //查找角色的指悦账号
            Map<String, String> map = new HashMap<>(3);
            map.put("isChannel", "true");
            map.put("channelId", channelId);
            map.put("channelUid", channelUid);

            Account account = accountWorker.getAccount(map);
            if (account == null) {
                result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                result.put("reason", "该账号不存在");
                break;
            }
            long accountId = account.getId().longValue();
            //角色是否存在
            Map<String, Object> maps = new HashMap<>(5);
            maps.put("accountId", accountId);
            maps.put("channelId", channelId);
            maps.put("channelUid", channelUid);
            maps.put("serverId", serverId);
            maps.put("roleId", roleId);


            //1.判断用户存不存在 userId
            List<GameRole> roleList = gameRoleWorker.findGamerole(maps);
            if (roleList.size() == 0) {
                result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                result.put("reason", "该角色不存在");
                break;
            }
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
            result.put("reason", "进入成功");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("channelId", channelId);
            jsonObject.put("appId", appId);
            jsonObject.put("serverId", serverId);
            jsonObject.put("roleId", roleId);

            result.put("data", jsonObject.toString());
        } while (false);

        setResponseAccess(response);
        ResponseUtil.write(response, result);

        log.info("request: ttt/enter , result\t" + result.toString());
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
        log.info("request: ttt/exit , appId: " + appId + "\tserverId:" + serverId +
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
                result.put("reason", "该账号不存在");
                break;
            }
            long accountId = account.getId().longValue();
            //角色是否存在
            Map<String, Object> maps = new HashMap<>(5);
            maps.put("accountId", accountId);
            maps.put("channelId", channelId);
            maps.put("channelUid", channelUid);
            maps.put("serverId", serverId);
            maps.put("roleId", roleId);

            //1.判断用户存不存在 userId
            List<GameRole> roleList = gameRoleWorker.findGamerole(maps);
            if (roleList.size() == 0) {
                result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                result.put("reason", "该角色不存在");
                break;
            }
            //查询redis
            //移除在线玩家
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

            log.info("logintime:" + logintime);

            //计算在线时间
            //todo

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("channelId", channelId);
            jsonObject.put("appId", appId);
            jsonObject.put("serverId", serverId);
            jsonObject.put("roleId", roleId);
            jsonObject.put("lastLoginTime", DateUtil.getCurrentDateStr());

            result.put("data", jsonObject.toString());
            result.put("message", ResultGenerator.DEFAULT_SUCCESS_MESSAGE);
        } while (false);

        setResponseAccess(response);
        ResponseUtil.write(response, result);

        log.info("request: ttt/exit , result\t" + result.toString());
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
        JSONObject request = JSONObject.parseObject(jsonData);
        log.info("request: ttt/payInfo " + request.toString());

        int accountId = request.getInteger("accountID");
        int channelId = request.getInteger("channelID");
        String channelUid = request.getString("channelUid");
        int appId = request.getInteger("appID");
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
                result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                result.put("state", StateCode.CODE_PARAM_ERROR);
                break;
            }
            //1.判断账号是否存在
            Map<String, String> mapstr = new HashMap<>();
            mapstr.put("isChannel", "true");
            mapstr.put("channelId", String.valueOf(channelId));
            mapstr.put("channelUid", channelUid);
            Account account = accountWorker.getAccount(mapstr);
            if (account == null) {
                result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                result.put("state", StateCode.CODE_ACCOUNT_NONE);
                break;
            }

            //2.判断角色存不存在
            Map<String, Object> map = new HashMap<>();
            map.put("accountId", accountId);
            map.put("serverId", serverID);
            map.put("roleId", roleID);
            map.put("channelId", channelId);
            map.put("channelUid", channelUid);

            List<GameRole> roleList = gameRoleWorker.findGamerole(map);
            if (roleList.size() == 0 || !roleList.get(0).getGameId().equals(String.valueOf(appId))) {
                result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                result.put("state", StateCode.CODE_USER_NONE);
                break;
            }
            //角色信息 可以获取 游戏id、渠道id
            GameRole role = roleList.get(0);

            //2.金额合法性
            if (money < 0) {
                log.info("the money is not valid. money:" + money);
                result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                result.put("state", StateCode.CODE_MONEY_ERROR);
                break;
            }

            //3.验签
            if (!UOrderManager.isSignOK(accountId, channelId, channelUid, appId,
                    channelOrderID, productID, productName, productDesc, money,
                    roleID, roleName, roleLevel,
                    serverID, serverName, realMoney,
                    completeTime, sdkOrderTime, status, notifyUrl, signType, sign)) {

                log.info("the sign is not valid. sign:" + sign);
                result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                result.put("state", StateCode.CODE_SIGN_ERROR);
                break;
            }

            //订单是否已经存在
            boolean isPaySuccess = false;
            UOrder order = orderManager.getOrder(String.valueOf(appId), String.valueOf(channelId), channelOrderID);
            if (order == null) {
                log.info("order is not exist. generateOrder");
                order = orderManager.generateOrder(accountId, channelId, channelUid, appId,
                        channelOrderID, productID, productName, productDesc, money,
                        roleID, roleName, roleLevel,
                        serverID, serverName, realMoney,
                        completeTime, sdkOrderTime, status, notifyUrl);

                if (order == null) {
                    result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                    result.put("state", StateCode.CODE_ORDER_ERROR);
                    break;
                }
            } else {
                //参数合法 能否更新订单
                boolean is_right = order.checkParam(productID, productName, productDesc, money,
                        roleID, roleName, serverID, serverName, status, sdkOrderTime);
                if (!is_right) {
                    result.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                    result.put("state", StateCode.CODE_PARAM_DIFF);
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
                cache.reqpay(role.getGameId(), String.valueOf(serverID), role.getChannelId(), accountId, roleID, money, account.getCreateTime());
            }

            result.put("message", ResultGenerator.DEFAULT_SUCCESS_MESSAGE);
            result.put("orderId", order.getOrderID());
            result.put("state", StateCode.CODE_SUCCESS);

            log.info("status:" + status);
        } while (false);

        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        setResponseAccess(response);
        ResponseUtil.write(response, result);

        log.info("request: ttt/payInfo , result\t" + result.toString());
    }
}