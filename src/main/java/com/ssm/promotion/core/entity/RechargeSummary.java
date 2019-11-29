package com.ssm.promotion.core.entity;

import lombok.Data;

/**
 * @author song minghua
 * @date 2019/11/26
 * 生成一遍后 序列化丢到redis
 * 下次直接取
 */
@Data
public class RechargeSummary {
    /**
     * 每年天数
     */
    public int day;
    public int year;
    public int month;
    /**
     * 每月天数
     */
    public int mday;

    /**
     * 全服概况
     * yyyyMMdd
     */
    public String date;

    /**
     * 新增创号
     * :当日新注册账号的账号数目，且进入游戏
     */
    public int newAddCreateAccount;

    /**
     * 新增创角
     * :当日创建新角色的账号数目
     */
    public int newAddCreateRole;

    /**
     * 新增创角去除滚服
     * :新增创角 - 其中在其他区服已有角色的账号数目
     * :当日创建新角色的账号数目 - 其中在其他区服已有角色的账号数目
     */
    public int newAddCreateRoleRemoveOld;

    /**
     * 创角率
     * :当日创建新角色的账号数目/所有账号的数目
     */
    public Double createAccountRate;

    /**
     * 活跃玩家
     * :当日上线的玩家账号数目
     */
    public int activePlayer;

    /**
     * 充值次数
     * :当日充值的次数
     */
    public int rechargeTimes;

    /**
     * 充值人数
     * :当日充值的玩家账号数目
     */
    public int rechargeNumber;
    /**
     * 充值金额
     * :当日充值的金额数目
     */
    public int rechargePayment;

    /**
     * 活跃付费率
     * :当日充值的玩家账号数目/当日上线的玩家账号数目
     */
    public int activePayRate;

    /**
     * 付费ARPU
     * :当日充值总额/当日充值的玩家账号数目
     */
    public int paidARPU;

    /**
     * 活跃ARPU
     * :当日充值总额/当日上线的玩家账号数目
     */
    public int activeARPU;

    /**
     * 当日首次付费人数
     * : 当日充值1次的玩家账号数目
     */
    public int nofPayers;

    /**
     * 当日首次付费金额
     * : 当日充值1次的玩家支付金额数目
     */
    public int nofPayment;

    /**
     * 注册付费人数
     * : 当日注册并且充值的玩家账号数目
     */
    public int registeredPayers;

    /**
     * 注册付费金额
     * : 当日注册并且充值的玩家支付金额数目
     */
    public int registeredPayment;


    /**
     * 注册付费ARPU
     * : 当日注册并且充值的玩家支付金额数目/当日注册并且充值的玩家账号数目
     */
    public int registeredPaymentARPU;

//    分服概况\分渠道
    /**
     * 累计充值
     */
    public int totalPayment;

    /**
     * 累计创角
     */
    public int totalCreateRole;

    /**
     * 累计充值人数
     */
    public int totalRechargeNums;

    /**
     * 总付费率
     */
    public int totalRechargeRates;

//    分渠道概况
    /**
     * 渠道id
     */
    public int spId;
    /**
     * 注收比
     */
    public Double zhushoubi;

    /**
     * 新增注收比
     */
    public Double addzhushoubi;

    public RechargeSummary() {
    }
}
