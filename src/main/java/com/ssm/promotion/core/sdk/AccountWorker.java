package com.ssm.promotion.core.sdk;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.common.ResultGenerator;
import com.ssm.promotion.core.entity.Account;
import com.ssm.promotion.core.service.AccountService;
import com.ssm.promotion.core.service.ServerListService;
import com.ssm.promotion.core.util.DateUtil;
import com.ssm.promotion.core.util.NumberUtil;
import com.ssm.promotion.core.util.RandomUtil;
import com.ssm.promotion.core.util.StringUtil;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
            String ip = map.get("ip");

            Map<String, Object> tmp = new HashMap<>(6);
            tmp.put("gameId", appId);
            tmp.put("spId", channelId);

            //某游戏 是否开放注册
            if (!serverService.isSpCanReg(tmp, -1)) {
                //返回结果
                reply.put("reason", "未开放注册");
                reply.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                break;
            }
/*            int deviceSize = this.getDeviceCreateAccount(deviceCode, channelId);
            if (deviceSize > 0) {
                if (deviceSize == 10) {
                    reply.put("reason", "设备码非法");
                    reply.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                    break;
                } else if (deviceSize == 20) {
                    reply.put("reason", "已到达设备创建账号最大数量");
                    reply.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                    break;
                }
            }*/
            //封禁ip
            if (TemplateWorker.hasBanIp(ip)) {
                TemplateWorker.addBanIp(ip);
                reply.put("reason", "玩家ip已被封禁");
                reply.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                break;
            }
            //账号密码注册
            if (!auto) {
                if (username.length() < AccountWorker.UserInfoLenMin || username.length() > AccountWorker.UserInfoLenMax) {
                    reply.put("reason", "用户名长度不对！");
                    reply.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                    break;
                }
                //名称合法
                if (!StringUtil.isValidUsername(username)) {
                    //Todo
                    reply.put("reason", "用户名格式不合法！");
                    reply.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                    break;
                }
                // 能包含敏感词
                if (TemplateWorker.hasBad(username)) {
                    reply.put("reason", "用户名包含敏感词！");
                    reply.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                    break;
                }
                if (pwd.length() < AccountWorker.UserInfoLenMin || pwd.length() > AccountWorker.UserInfoLenMax) {
                    reply.put("reason", "密码长度不对");
                    reply.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                    break;
                }
            }

            //检查渠道id和渠道用户id是否存在
            if (channelId != 0 && accountService.exist(map) > 0) {
                reply.put("reason", "渠道账号已经存在");
                reply.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                break;
            }
            //创建账号
            Account account = this.createAccount(map);

            if (account == null) {
                reply.put("reason", "注册失败");
                reply.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                break;
            }
            if (account.getId() < 0) {
                if (account.getId() == -2) {
                    reply.put("reason", "账号名重复");
                    reply.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                    break;
                } else {
                    reply.put("reason", "注册失败");
                    reply.put("message", ResultGenerator.DEFAULT_FAIL_MESSAGE);
                    break;
                }
            }

            map.put("accountId", account.getId().toString());

            reply.put("message", ResultGenerator.DEFAULT_SUCCESS_MESSAGE);
            reply.put("accountId", account.getId());
            reply.put("account", account.getName());
            reply.put("password", account.getPwd());
            reply.put("channelUid", account.getChannelUserId());
            reply.put("reason", "注册成功");

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
    public Account createAccount(Map<String, String> map) throws Exception {
        boolean auto = Boolean.parseBoolean(map.get("auto"));
        String gameId = map.get("gameId");

        String channelId = map.get("channelId");
        String channelUserId = map.get("channelUid");
        String channelUserName = map.get("channelUname");
        String channelUserNick = map.get("channelUnick");

        String username = map.get("username");
        String pwd = map.get("pwd");

        String phone = map.get("phone");
        String deviceCode = map.get("deviceCode");
        String imei = map.get("imei");

        String addparm = map.get("addparm");

        String ip = map.get("ip");

        Account account = new Account();
        if (auto) {
            account.setName(RandomUtil.rndStr(10, true));
            account.setPwd(RandomUtil.rndStr(6, false));
        } else {
            account.setName(username);
            account.setPwd(pwd);
        }
        if (channelId.equals("0")) {
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
                return null;
            }
            //官方
            account.setChannelUserId(account.getId().toString());

            //更新uid
            Map<String, Object> maps = new HashMap<>();
            maps.put("id", account.getId());
            maps.put("channelUid", account.getId());
            accountService.updateAccountUid(maps);

        } else {
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
}
