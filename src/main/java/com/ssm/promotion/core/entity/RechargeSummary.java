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
     * :当天新增创角/当天新增创号
     */
    public Double createAccountRate;


    /**
     * 创号转化率
     * :忠诚用户数量：本周登陆3次以上（当天重复登陆算1次），最高角色等级超过15级，在线时长超过14小时的帐号
     * :转化率：上周登录的用户在本周转化为忠诚用户的比例
     */
    public Double createAccountTransRate;

    //通用
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
    public double activePayRate;
    /**
     * 付费ARPU
     * :当日充值总额/当日充值的玩家账号数目
     */
    public double paidARPU;
    /**
     * 活跃ARPU
     * :当日充值总额/当日上线的玩家账号数目
     */
    public double activeARPU;
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
    public double registeredPaymentARPU;


//分服2
    /**
     * 服务器id
     */
    public int serverId;
    /**
     * 开服天数
     */
    public int openDay;

    /**
     * 新增玩家
     */
    public int newaddplayer;


//分服概况\分渠道 =2/3
    /**
     * 累计充值
     */
    public double totalPayment;

    /**
     * 累计创角
     */
    public double totalCreateRole;
    /**
     * 累计充值人数
     */
    public double totalRechargeNums;
    /**
     * 总付费率
     */
    public double totalRechargeRates;


//    分渠道概况3
    /**
     * 渠道id
     */
    public int spId;
    /**
     * 注收比
     */
    public double zhushoubi;
    /**
     * 新增注收比
     */
    public double addzhushoubi;

    public RechargeSummary() {
    }

    @Override
    public String toString() {
        String objectS = "date:" + date + "\n" +

                "newAddCreateAccount:" + newAddCreateAccount + "\n" +
                "newAddCreateRole:" + newAddCreateRole + "\n" +
                "newAddCreateRoleRemoveOld:" + newAddCreateRoleRemoveOld + "\n" +
                "createAccountRate:" + createAccountRate + "\n" +

                "createAccountTransRate:" + createAccountTransRate +

                "activePlayer:" + activePlayer + "\n" +
                "rechargeTimes:" + rechargeTimes + "\n" +
                "rechargeNumber:" + rechargeNumber + "\n" +
                "rechargePayment:" + rechargePayment + "\n" +
                "activePayRate:" + activePayRate + "\n" +
                "paidARPU:" + paidARPU + "\n" +
                "activeARPU:" + activeARPU + "\n" +
                "nofPayers:" + nofPayers + "\n" +
                "nofPayment:" + nofPayment + "\n" +
                "registeredPayers:" + registeredPayers + "\n" +
                "registeredPayment:" + registeredPayment + "\n" +
                "registeredPaymentARPU" + registeredPaymentARPU + "\n" +

                "serverId:" + serverId + "\n" +
                "openDay" + openDay + "\n" +
                "newaddplayer" + newaddplayer + "\n" +

                "totalPayment:" + totalPayment + "\n" +
                "totalCreateRole:" + totalCreateRole + "\n" +
                "totalRechargeNums:" + totalRechargeNums + "\n" +
                "totalRechargeRates:" + totalRechargeRates + "\n" +

                "spId:" + spId + "\n" +
                "zhushoubi:" + zhushoubi + "\n" +
                "addzhushoubi:" + addzhushoubi + "\n";
        return objectS;
    }

    /**
     * 计算各种
     * 1.比率
     * 2.ARPU
     */
    public void calculate(Integer type) {
        //活跃付费率
        this.setActivePayRate((double) this.rechargeNumber / this.activePlayer * 100);
        //付费ARPU
        this.setPaidARPU((double) this.rechargePayment / this.rechargeNumber * 100);
        //活跃ARPU
        this.setActiveARPU((double) this.rechargePayment / this.activePlayer * 100);

        //注册付费ARPU
        this.setRegisteredPaymentARPU((double) this.rechargePayment / this.registeredPayers);

        if (type == 1) {
            //创角率
            this.setCreateAccountRate((double) this.newAddCreateRole / this.newAddCreateAccount * 100);
        } else {
            //总付费率

            if (type == 3) {
                //创号转化率
                //注收比
                //新增注收比
            }
        }
    }

    /**
     * 同类数据相加
     * 部分属性需要重新计算
     */
    public void add(RechargeSummary add) {
        //1
        date = add.date;
        //1/3
        newAddCreateAccount += add.newAddCreateAccount;
        newAddCreateRole += add.newAddCreateRole;
        newAddCreateRoleRemoveOld += add.newAddCreateRoleRemoveOld;

        //1
//        createAccountRate += add.createAccountRate;
        //3
//        createAccountTransRate += add.createAccountTransRate;

        //通用
        activePlayer += add.activePlayer;
        rechargeTimes += add.rechargeTimes;
        rechargeNumber += add.rechargeNumber;
        rechargePayment += add.rechargePayment;
//        activePayRate += add.activePayRate;
//        paidARPU += add.paidARPU;
//        activeARPU += add.activeARPU;
        nofPayers += add.nofPayers;
        nofPayment += add.nofPayment;
        registeredPayers += add.registeredPayers;
        registeredPayment += add.registeredPayment;
//        registeredPaymentARPU+=add.registeredPaymentARPU;

        //2
//        serverId += add.serverId;
//        openDay+=openDay;
//        newaddplayer+=newaddplayer;

        //2/3
        totalPayment += add.totalPayment;
        totalCreateRole += add.totalCreateRole;
        totalRechargeNums += add.totalRechargeNums;
//        totalRechargeRates += add.totalRechargeRates;

        //3
//        spId += add.spId;
//        zhushoubi += add.zhushoubi;
//        addzhushoubi += add.addzhushoubi;
    }
}
