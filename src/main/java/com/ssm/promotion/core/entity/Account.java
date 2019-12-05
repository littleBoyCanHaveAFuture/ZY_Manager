package com.ssm.promotion.core.entity;

import lombok.Data;

/**
 * 指悦账号信息
 * 1.官方注册
 * 2.渠道账号自动注册并绑定
 * mysql：zy_account
 *
 * @author song minghua
 * @date 2019/12/4
 */
@Data
public class Account {
    /**
     * 账号唯一id
     */
    Integer id;
    /**
     * 账户名称
     */
    String name;
    /**
     * 账号密码
     */
    String pwd;
    /**
     * 绑定手机
     */
    String phone;

    /**
     * 创建账号的
     * ip
     */
    String createIp;
    /**
     * 创建账号的
     * 时间
     */
    String createTime;
    /**
     * 创建账号的
     * 设备码
     */
    String createDevice;
    /**
     * 最后一次登录
     * 设备码
     */
    String deviceCode;

    /**
     * 创建账号的
     * 渠道id
     */
    String channelId;
    /**
     * 创建账号的
     * 渠道用户账号id
     */
    String channelUserId;
    /**
     * 创建账号的
     * 渠道账号名称
     */
    String channelUserName;
    /**
     * 创建账号的
     * 渠道用户昵称
     */
    String channelUserNick;

    /**
     * 最后一次登录
     * 时间
     */
    Long lastLoginTime;
    /**
     * 额外参数
     */
    String token;
    String addParam;

    @Override
    public String toString() {
        String objectS = "id:" + id + "\n" + "\t" +
                "name:" + name + "\n" + "\t" +
                "pwd:" + pwd + "\n" + "\t" +
                "phone:" + phone + "\n" + "\t" +
                "createIp:" + createIp + "\n" + "\t" +

                "createTime:" + createTime + "\n" + "\t" +
                "createDevice:" + createDevice + "\n" + "\t" +
                "deviceCode:" + deviceCode + "\n" + "\t" +
                "channelId:" + channelId + "\n" + "\t" +
                "channelUserId:" + channelUserId + "\n" + "\t" +

                "channelUserName:" + channelUserName + "\n" + "\t" +
                "channelUserNick:" + channelUserNick + "\n" + "\t" +
                "lastLoginTime:" + lastLoginTime + "\n" + "\t" +
                "token:" + token + "\n" + "\t" +
                "addParam:" + addParam + "\n";
        return objectS;
    }
//    String username,
//    String pwd,
//    String spId,
//    String phone,
//    String createIp,

//    String createTime
//    String deviceCode,
//    String channelId,
//    String channelUserId,

//    String channelUserName,
//    String channelUserNick,
//    String lastLoginTime
//    String token
//    String addparm
}
