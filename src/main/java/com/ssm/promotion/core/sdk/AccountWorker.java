package com.ssm.promotion.core.sdk;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.entity.Account;
import com.ssm.promotion.core.service.AccountService;
import com.ssm.promotion.core.service.ServerListService;
import com.ssm.promotion.core.util.DateUtil;
import com.ssm.promotion.core.util.NumberUtil;
import com.ssm.promotion.core.util.RandomUtil;
import com.ssm.promotion.core.util.StringUtil;
import lombok.Data;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
    public JSONObject reqRegister(Map<String, String> map) throws Exception {
        JSONObject reply = new JSONObject();
        do {
            //全部数据
            String username = map.get("username");
            String pwd = map.get("pwd");
            String phone = map.get("phone");
            String ip = map.get("ip");
            String deviceCode = map.get("deviceCode");

            String imei = map.get("imei");
            String channelId = map.get("channelId");
            String channelUserId = map.get("channelUid");
            String channelUserName = map.get("channelUname");
            String channelUserNick = map.get("channelUnick");

            String addparm = map.get("addparm");
            String gameId = map.get("appId");
            String auto = map.get("auto");


            Map<String, Object> tmp = new HashMap<>(6);
            tmp.put("gameId", gameId);
            tmp.put("spId", channelId);

            //某游戏 是否开放注册
            if (!serverService.isSpCanReg(tmp, -1)) {
                //返回结果
                reply.put("err", "未开放注册");
                break;
            }

            int deviceSize = this.getDeviceCreateAccount(deviceCode, Integer.parseInt(channelId));
            if (deviceSize > 0) {
                if (deviceSize == 10) {
                    reply.put("err", "设备码非法");
                    break;
                } else if (deviceSize == 20) {
                    reply.put("err", "已到达设备创建账号最大数量");
                    break;
                }
            }
            //封禁ip
            if (TemplateWorker.hasBanIp(ip)) {
                TemplateWorker.addBanIp(ip);
                reply.put("err", "玩家ip已被封禁");
                break;
            }
            if (!auto.equals("1")) {
                if (username.length() < AccountWorker.UserInfoLenMin || username.length() > AccountWorker.UserInfoLenMax) {
                    reply.put("err", "用户名长度不对！");
                    break;
                }
                //名称合法
                if (!StringUtil.isValidUsername(username)) {
                    //Todo
                    reply.put("err", "用户名格式不合法！");
                    break;
                }
                // 能包含敏感词
                if (TemplateWorker.hasBad(username)) {
                    reply.put("err", "用户名包含敏感词！");
                    break;
                }
                if (pwd.length() < AccountWorker.UserInfoLenMin || pwd.length() > AccountWorker.UserInfoLenMax) {
                    reply.put("err", "密码长度不对");
                    break;
                }
            }

            //检查渠道id和渠道用户id是否存在

            //创建账号
            Account account = this.createAccount(map);
            if (account == null) {
                reply.put("err", "注册失败");
                break;
            }
            if (account.getId() < AccountWorker.USERID_BEGIN) {
                if (account.getId() == -2) {
                    reply.put("err", "账号名重复");
                    break;
                }
            }
            map.put("accountId", account.getId().toString());
            reply.put("message", "注册成功");
            reply.put("status", 1);
            //注册成功 相关数据存入redis

        } while (false);


        return reply;
    }

    /**
     * 注册账号
     */
    public JSONObject channelAutoRegister(Map<String, String> map) throws Exception {
        JSONObject reply = new JSONObject();
        do {
            //全部数据
            String channelId = map.get("channelId");
            String gameId = map.get("appId");
            String ip = map.get("ip");

            Map<String, Object> tmp = new HashMap<>(6);
            tmp.put("gameId", gameId);
            tmp.put("spId", channelId);

            //某游戏 是否开放注册
            if (!serverService.isSpCanReg(tmp, -1)) {
                //返回结果
                reply.put("err", "未开放注册");
                break;
            }

            //封禁ip
            if (TemplateWorker.hasBanIp(ip)) {
                TemplateWorker.addBanIp(ip);
                reply.put("err", "玩家ip已被封禁");
                break;
            }

            //检查渠道id和渠道用户id是否存在

            //创建账号
            Account account = this.createAccount(map);
            if (account == null) {
                reply.put("err", "注册失败");
                break;
            }
            if (account.getId() < AccountWorker.USERID_BEGIN) {
                if (account.getId() == -2) {
                    reply.put("err", "账号名重复");
                    break;
                }
            }

            map.put("accountId", account.getId().toString());
            reply.put("message", "注册成功");
            reply.put("status", 1);
            //注册成功 相关数据存入redis

        } while (false);


        return reply;
    }

    /**
     * 此设备号的用户 是否可以创建账号
     *
     * @return int:0,可以创建|>0,不能
     */
    public int getDeviceCreateAccount(String deviceCode, int spId) {
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
    public Account createAccount(Map<String, String> map) throws Exception {
        String username = map.get("username");
        String pwd = map.get("pwd");
        String phone = map.get("phone");
        String ip = map.get("ip");
        String deviceCode = map.get("deviceCode");

        String imei = map.get("imei");
        String channelId = map.get("channelId");
        String channelUserId = map.get("channelUid");
        String channelUserName = map.get("channelUname");
        String channelUserNick = map.get("channelUnick");

        String addparm = map.get("addparm");
        String gameId = map.get("gameId");
        String auto = map.get("auto");

        Account account = new Account();
        if (auto.equals("1")) {
            account.setName(RandomUtil.rndStr(10, true));
            account.setPwd(RandomUtil.rndStr(6, false));
        } else {
            account.setName(username);
            account.setPwd(pwd);
        }

        account.setPhone(phone);
        account.setCreateIp(ip);
        account.setCreateTime(DateUtil.getCurrentDateStr());
        account.setCreateDevice(deviceCode);
        account.setDeviceCode(deviceCode);
        account.setChannelId(channelId);
        account.setChannelUserId(channelUserId);
        account.setChannelUserName(channelUserName);
        account.setChannelUserNick(channelUserNick);
        account.setLastLoginTime(0L);
        account.setToken("");
        account.setAddParam(addparm);

        accountService.createAccount(account);
        return account;

    }

    private void AccountWorker() {
        try {
            AccountWorker.lastUserId = this.getLastUserId();
            System.out.println("init lastUserId:" + AccountWorker.lastUserId);
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
     */
    public Account getAccount(Map<String, String> map) {
        boolean isChannel = Boolean.parseBoolean(map.get("isChannel"));
        if (isChannel) {
            if (map.get("channelId").isEmpty() || map.get("channelUid").isEmpty()) {
                return null;
            }
        } else {
            if (map.get("name").isEmpty() || map.get("pwd").isEmpty()) {
                return null;
            }
        }
        List<Account> list = accountService.findUser(map);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(NumberUtil.ZERO);
    }
}
