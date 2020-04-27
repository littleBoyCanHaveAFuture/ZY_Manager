package com.ssm.promotion.core.sdk;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.common.ResultGenerator;
import com.ssm.promotion.core.entity.Account;
import com.ssm.promotion.core.jedis.jedisRechargeCache;
import com.ssm.promotion.core.service.AccountService;
import com.ssm.promotion.core.service.ServerListService;
import com.ssm.promotion.core.util.*;
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
    private static final Logger log = Logger.getLogger(AccountWorker.class);
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
    @Autowired
    jedisRechargeCache cache;
    @Resource
    private AccountService accountService;
    @Resource
    private ServerListService serverService;

    /**
     * 返回某运营商用户编号起始值
     */
    public static int getSpUserIdBegin(int spId) {
        return USERID_BEGIN + USERID_SP_INTERVAL * (spId - 1);
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
     * 此设备号的用户 是否可以创建账号
     *
     * @return int:0,可以创建|>0,不能
     */
    public int getDeviceCreateAccount(String deviceCode, Integer spId) {
        if (!StringUtil.isValid(deviceCode)) {
            return 10;
        }
        //根据渠道和设备码获取账户列表

        int size = accountService.getTotalSameDeviceCode(deviceCode, spId);
        if (size >= AccountWorker.DeviceAccountMax) {
            return 20;
        }
        return 0;
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

    /**
     * 注册账号
     */
    public Account autoRegister(JSONObject reply, String ip) throws Exception {
        //创建账号
        Account account = new Account();

        account.setPhone("");
        account.setCreateIp(ip);
        account.setCreateTime(DateUtil.getCurrentDateStr());
        account.setCreateDevice("");
        account.setDeviceCode("");
        account.setChannelId("0");
        account.setChannelUserId("");
        account.setChannelUserName("Official");
        account.setChannelUserNick("Official");
        account.setLastLoginTime(0L);
        account.setToken("");
        account.setAddParam("");

        accountService.createAccount(account);
        if (account.getId() == -1 || account.getId() == -2 || account.getId() == -3) {
            return null;
        }
        //官方
        account.setChannelUserId(account.getId().toString());
        //更新uid
        Map<String, Object> map = new HashMap<>();
        map.put("id", account.getId());
        map.put("channelUid", account.getId());
        accountService.updateAccountUid(map);

        reply.put("message", "注册成功");
        reply.put("accountId", account.getId());
        reply.put("account", account.getName());
        reply.put("pwd", account.getPwd());
        reply.put("status", 1);

        return account;
    }

    private void AccountWorker() {
        try {
            AccountWorker.lastUserId = this.getLastUserId();
            log.info("init lastUserId:" + AccountWorker.lastUserId);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public Account getAccountById(int id) {
        Account account = accountService.findAccountById(id);

        return account;
    }

    /**
     * 账号登录时间
     */
    public void updateLoginTime(Map<String, Object> map) {
        accountService.updateAccount(map);
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
}
