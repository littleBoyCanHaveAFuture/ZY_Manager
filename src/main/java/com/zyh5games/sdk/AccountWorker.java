package com.zyh5games.sdk;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.entity.Account;
import com.zyh5games.jedis.JedisRechargeCache;
import com.zyh5games.service.AccountService;
import com.zyh5games.service.GameNewService;
import com.zyh5games.service.ServerListService;
import com.zyh5games.util.*;
import lombok.Data;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author song minghua
 */
@Component
@Data
public class AccountWorker {
    /**
     * 用户编号不同运营商的间隔值
     * ：一百万
     * ：1,000,000
     */
    public static final int USERID_SP_INTERVAL = NumberUtil.TEN_THOUSAND * NumberUtil.HUNDRED;
    /**
     * 真实用户编号起始值
     */
    public static final int USERID_BEGIN = USERID_SP_INTERVAL;
    /**
     * log
     */
    private static final Logger log = Logger.getLogger(AccountWorker.class);
    /**
     * 用户名和密码长度限制
     */
    public static int UserInfoLenMin = 6, UserInfoLenMax = 24;
    /**
     * 单个设备可创建账户数量
     */
    public static int DeviceAccountMax = 20;
    /**
     * 最大渠道编号
     */
    public static int maxSpid = 2147;
    /**
     * 已存在的最大用户编号，不含机器人，
     */
    public static AtomicInteger lastUserId;
    /**
     * 已存在的最大appid
     */
    public static AtomicInteger lastAppId;
    @Autowired
    JedisRechargeCache cache;
    @Resource
    private AccountService accountService;
    @Resource
    private ServerListService serverService;
    @Resource
    private GameNewService gameNewService;

    public static Integer getNextId() {
        int id = AccountWorker.lastUserId.get();
        return id + 1;
    }

    /**
     * 返回某运营商用户编号起始值
     */
    public static int getSpUserIdBegin(int spId) {
        return USERID_BEGIN + USERID_SP_INTERVAL * (spId - 1);
    }

    private void init() {
        try {
            AccountWorker.lastUserId = this.getLastUserId();
            log.info("init lastUserId:" + AccountWorker.lastUserId);

            AccountWorker.lastAppId = this.lastAppId();
            log.info("init lastAppId:" + AccountWorker.lastAppId);
            System.out.println("init lastUserId:" + AccountWorker.lastAppId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 注册账号
     */
    public JSONObject reqRegister(JSONObject jsonObject) throws Exception {
        JSONObject reply = new JSONObject();
        do {
            //全部数据
            boolean auto = jsonObject.getBoolean("auto");
            int appId = jsonObject.getInteger("appId");
            int channelId = jsonObject.getInteger("channelId");
            String channelUid = jsonObject.getString("channelUid");
            String channelUname = jsonObject.getString("channelUname");
            String channelUnick = jsonObject.getString("channelUnick");
            String username = jsonObject.getString("username");
            String pwd = jsonObject.getString("password");
            String phone = jsonObject.getString("phone");
            String deviceCode = jsonObject.getString("deviceCode");
            String imei = jsonObject.getString("imei");
            String addparm = jsonObject.getString("addparm");
            String ip = jsonObject.getString("ip");

            Map<String, Object> map = new HashMap<>(6);
            map.put("gameId", appId);
            map.put("spId", channelId);

            //某游戏 是否开放注册
            if (!serverService.isSpCanReg(map, -1)) {
                //返回结果
                log.error("未开放注册");
                reply.put("state", false);
                reply.put("message", "未开放注册");
                break;
            }

//            int deviceSize = this.getDeviceCreateAccount(deviceCode, channelId);
//            if (deviceSize > 0) {
//                if (deviceSize == 10) {
//                    reply.put("reason", "设备码非法");
//                    reply.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
//                    break;
//                } else if (deviceSize == 20) {
//                    reply.put("reason", "已到达设备创建账号最大数量");
//                    reply.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
//                    break;
//                }
//            }

            if (TemplateWorker.hasBanIp(ip)) {
                log.error("玩家ip已被封禁  ip=" + ip);
                //封禁ip
                TemplateWorker.addBanIp(ip);
                reply.put("state", false);
                reply.put("message", "玩家ip已被封禁");
                break;
            }
            //账号密码注册
            if (!auto) {
                if (username.length() < AccountWorker.UserInfoLenMin || username.length() > AccountWorker.UserInfoLenMax) {
                    log.error("用户名长度不对 username=" + username + " length=" + username.length());
                    reply.put("state", false);
                    reply.put("message", "用户名长度不对！");
                    break;
                }
                //名称合法
                if (!StringUtil.isValidUsername(username)) {
                    log.error("用户名格式不合法 username=" + username);
                    reply.put("state", false);
                    reply.put("message", "用户名格式不合法！");
                    break;
                }
                // 能包含敏感词
                if (TemplateWorker.hasBad(username)) {
                    log.error("用户名包含敏感词 username=" + username);
                    reply.put("state", false);
                    reply.put("message", "用户名包含敏感词！");
                    break;
                }
                if (pwd.length() < AccountWorker.UserInfoLenMin || pwd.length() > AccountWorker.UserInfoLenMax) {
                    log.error("密码长度不对 pwd=" + pwd + " length=" + pwd.length());
                    reply.put("state", false);
                    reply.put("message", "密码长度不对");
                    break;
                }
            }

            //检查渠道id和渠道用户id是否存在
            map.clear();
            map.put("channelId", channelId);
            map.put("channelUid", channelUid);
            if (channelId != 0 && accountService.exist(map) > 0) {
                log.error("渠道账号已经存在 channelUid=" + channelUid + " channelId=" + channelId);
                reply.put("state", false);
                reply.put("message", "渠道账号已经存在");
                break;
            }

            //创建账号
            Account account = this.createAccount(jsonObject);
            if (account == null) {
                log.error("注册失败");
                reply.put("state", false);
                reply.put("message", "注册失败");
                break;
            }
            if (account.getId() < 0) {
                if (account.getId() == -2) {
                    log.error("账号名重复");
                    reply.put("state", false);
                    reply.put("message", "账号名重复");
                    break;
                } else {
                    log.error("注册失败");
                    reply.put("state", false);
                    reply.put("message", "注册失败");
                    break;
                }
            }

            map.put("accountId", account.getId().toString());
            reply.put("state", true);
            reply.put("accountId", account.getId());
            reply.put("account", account.getName());
            reply.put("password", account.getPwd());
            reply.put("channelUid", account.getChannelUserId());
            reply.put("message", "注册成功");

            //注册成功 相关数据存入redis
            cache.register(auto, appId, account.getId(), channelId);

        } while (false);

        return reply;
    }


    /**
     * 渠道自动注册账号
     */
    public JSONObject channelRegister(JSONObject jsonObject) throws Exception {
        JSONObject reply = new JSONObject();
        commonReg(reply, jsonObject);
        return reply;
    }

    public Account commonReg(JSONObject reply, JSONObject jsonObject) throws Exception {
        Account account = new Account();
        do {
            int appId = jsonObject.getInteger("appId");
            int channelId = jsonObject.getInteger("channelId");
            String channelUid = jsonObject.getString("channelUid");

            String channelUname = jsonObject.containsKey("channelUname") ? jsonObject.getString("channelUname") : "";
            String channelUnick = jsonObject.containsKey("channelUname") ? jsonObject.getString("channelUnick") : "";
            String phone = jsonObject.containsKey("channelUname") ? jsonObject.getString("phone") : "";
            String deviceCode = jsonObject.containsKey("channelUname") ? jsonObject.getString("deviceCode") : "";
            String imei = jsonObject.containsKey("channelUname") ? jsonObject.getString("imei") : "";

            String ip = jsonObject.getString("ip");

            String openId = jsonObject.containsKey("openId") ? jsonObject.getString("openId") : "";

            Map<String, Object> map = new HashMap<>(6);
            //检查渠道id和渠道用户id是否存在
            map.put("channelId", channelId);
            map.put("channelUid", channelUid);

            if (TemplateWorker.hasBanIp(ip)) {
                log.error("玩家ip已被封禁  ip=" + ip);
                //封禁ip
                TemplateWorker.addBanIp(ip);
                reply.put("state", false);
                reply.put("message", "玩家ip已被封禁");
                break;
            }

            if (accountService.exist(map) > 0) {
                log.error("渠道账号已经存在 channelUid=" + channelUid + " channelId=" + channelId);
                reply.put("state", false);
                reply.put("message", "渠道账号已经存在");
                break;
            }


            account.setName(RandomUtil.rndStr(10, true));
            account.setPwd(RandomUtil.rndStr(6, false));
            account.setPhone(phone);
            account.setCreateIp(ip);
            account.setCreateTime(DateUtil.getCurrentDateStr());
            account.setCreateDevice(deviceCode);
            account.setDeviceCode(deviceCode);
            account.setChannelId(String.valueOf(channelId));
            account.setChannelUserId(channelUid);
            account.setChannelUserName(channelUname);
            account.setChannelUserNick(channelUnick);
            account.setLastLoginTime(0L);
            account.setToken(openId);
            account.setAddParam("");

            accountService.createAccount(account);
            if (account.getId() < 0) {
                if (account.getId() == -2) {
                    log.error("账号名重复");
                    reply.put("state", false);
                    reply.put("message", "账号名重复");
                    break;
                } else {
                    log.error("注册失败");
                    reply.put("state", false);
                    reply.put("message", "注册失败");
                    break;
                }
            }
            reply.put("state", true);
            reply.put("accountId", account.getId());
            reply.put("account", account.getName());
            reply.put("password", account.getPwd());
            reply.put("channelUid", account.getChannelUserId());
            reply.put("message", "注册成功");
            //注册成功 相关数据存入redis
            cache.register(true, appId, account.getId(), channelId);
        } while (false);
        return account;
    }

    /**
     * 创建用户
     */
    public Account createAccount(JSONObject jsonObject) throws Exception {
        boolean auto = jsonObject.getBoolean("auto");
        int appId = jsonObject.getInteger("appId");
        String channelId = jsonObject.getString("channelId");
        String channelUid = jsonObject.getString("channelUid");
        String channelUname = jsonObject.getString("channelUname");
        String channelUnick = jsonObject.getString("channelUnick");
        String username = jsonObject.getString("username");
        String pwd = jsonObject.getString("password");
        String phone = jsonObject.getString("phone");
        String deviceCode = jsonObject.getString("deviceCode");
        String imei = jsonObject.getString("imei");
        String addparm = jsonObject.getString("addparm");
        String ip = jsonObject.getString("ip");

        Account account = new Account();
        if (auto) {
            account.setName(RandomUtil.rndStr(10, true));
            account.setPwd(RandomUtil.rndStr(6, false));
        } else {
            account.setName(username);
            account.setPwd(pwd);
        }
        if ("0".equals(channelId)) {
            account.setPhone(phone);
            account.setCreateIp(ip);
            account.setCreateTime(DateUtil.getCurrentDateStr());
            account.setCreateDevice(deviceCode);
            account.setDeviceCode(deviceCode);
            account.setChannelId("0");
            account.setChannelUserId("");
            account.setChannelUserName("Official");
            account.setChannelUserNick("Official");
            account.setLastLoginTime(0L);
            account.setToken("");
            account.setAddParam(addparm);

            accountService.createAccount(account);
            if (account.getId() == -1 || account.getId() == -2 || account.getId() == -3) {
                log.error("创建账号失败 err id=" + account.getId());
                return account;
            }
            //官方
            account.setChannelUserId(account.getId().toString());

            //更新uid
            Map<String, Object> map = new HashMap<>();
            map.put("id", account.getId());
            map.put("channelUid", account.getId());
            accountService.updateAccountUid(map);
        } else {
            account.setPhone(phone);
            account.setCreateIp(ip);
            account.setCreateTime(DateUtil.getCurrentDateStr());
            account.setCreateDevice(deviceCode);
            account.setDeviceCode(deviceCode);
            account.setChannelId(channelId);
            account.setChannelUserId(channelUid);
            account.setChannelUserName(channelUname);
            account.setChannelUserNick(channelUnick);
            account.setLastLoginTime(0L);
            account.setToken("");
            account.setAddParam(addparm);

            accountService.createAccount(account);
        }

        return account;
    }


    private AtomicInteger lastAppId() {
        int lastUserId = 0;
        Integer maxAppId = gameNewService.getMaxAppid();
        if (maxAppId != null) {
            lastUserId = maxAppId;
        }

        return new AtomicInteger(lastUserId);
    }

    private AtomicInteger getLastUserId() {
        int maxSpidStart = AccountWorker.getSpUserIdBegin(maxSpid);
        int lastUserId = accountService.readMaxAccountId(maxSpidStart);
        if (lastUserId <= NumberUtil.THOUSAND) {
            lastUserId = USERID_BEGIN;
        }
        return new AtomicInteger(lastUserId);
    }

    /**
     * 根据渠道和玩家所在渠道编号获取user
     *
     * @param map isChannel "true"
     *            channelId channelId
     *            channelUid channelUid
     *            name
     *            pwd
     */
    public Account getAccount(Map<String, Object> map) {
        List<Account> list = accountService.findUser(map);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(NumberUtil.ZERO);
    }

    /**
     * 渠道登录检查签名
     */
    public boolean checkSign(Boolean isAuto,
                             Integer GameId,
                             String channelId,
                             String channelUid,
                             String username,
                             String password,
                             String timestamp,
                             String loginKey,
                             String sign) throws UnsupportedEncodingException {
        String sb = "isAuto" + "=" + isAuto + "&" +
                "GameId" + "=" + GameId + "&" +
                "channelId" + "=" + channelId + "&" +
                "channelUid" + "=" + channelUid + "&" +
                "username" + "=" + username + "&" +
                "password" + "=" + password + "&" +
                "timestamp" + "=" + timestamp + "&" +
                loginKey;
        log.info(sb);

        String encoded = URLEncoder.encode(sb, "UTF-8");
        String newSign = EncryptUtils.md5(encoded).toLowerCase();

        log.info(newSign);
        log.info(sign);

        return sign.equals(newSign);
    }

    public Account channelReg(Integer appId, Integer channelId, String channelUid, String openId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("isChannel", "true");
        map.put("channelId", channelId);
        map.put("channelUid", channelUid);

        Account account = this.getAccount(map);
        if (account != null) {
            return account;
        } else {
            JSONObject jsonObject = new JSONObject();
            JSONObject reply = new JSONObject();

            jsonObject.put("appId", appId);
            jsonObject.put("channelId", channelId);
            jsonObject.put("channelUid", channelUid);
            jsonObject.put("openId", openId);
            return this.commonReg(reply, jsonObject);
        }
    }

    /**
     * @param rsqData int         appId         游戏id<p>
     *                int         channelId     渠道id<p>
     *                string      appKey        游戏秘钥<p>
     *                string      addParam      注释*<p>
     *                string      ip            ip地址
     */
    public Account zhiyueRegister(JSONObject rsqData) throws Exception {
        JSONObject reply = new JSONObject();
        Account account = new Account();
        do {
            int appId = rsqData.getInteger("appId");
            int channelId = rsqData.getInteger("channelId");
            String appKey = rsqData.getString("appKey");
            String addParam = rsqData.getString("addParam");
            String ip = rsqData.getString("ip");

            account.setName(RandomUtil.rndStr(10, true));
            account.setPwd(RandomUtil.rndStr(6, false));
            account.setPhone("");
            account.setCreateIp(ip);
            account.setCreateTime(DateUtil.getCurrentDateStr());
            account.setCreateDevice("");
            account.setDeviceCode("");
            account.setChannelId("0");
            account.setChannelUserName("");
            account.setChannelUserNick("");
            account.setLastLoginTime(0L);
            account.setToken("");
            account.setAddParam(addParam);

            accountService.createAccount(account);
            if (account.getId() < 0) {
                if (account.getId() == -2) {
                    log.error("账号名重复");
                    reply.put("state", false);
                    reply.put("message", "账号名重复");
                    break;
                } else {
                    log.error("注册失败");
                    reply.put("state", false);
                    reply.put("message", "注册失败");
                    break;
                }
            }
            //官方
            account.setChannelUserId(account.getId().toString());

            //更新uid
            Map<String, Object> map = new HashMap<>();
            map.put("id", account.getId());
            map.put("channelUid", account.getId());
            accountService.updateAccountUid(map);

            //注册成功 相关数据存入redis
            cache.register(true, appId, account.getId(), channelId);
        } while (false);
        return account;
    }
}
