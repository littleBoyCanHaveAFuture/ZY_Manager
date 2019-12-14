package com.ssm.promotion.core.entity;

import com.ssm.promotion.core.util.UtilG;
import lombok.Data;
import net.sf.json.JSONObject;

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
     * 条件：
     * 1.注册官方账号
     * 2.渠道账号首次登录任意游戏
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
    public double createAccountRate;


    /**
     * 创角转化率：新增创角/新增创号
     */
    public double createAccountTransRate;

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
     * :当日充值的玩家账号数目/当日上线的玩家账号数目·
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
    public double totalRechargeRates;


//    分渠道概况3
    /**
     * 渠道id
     */
    public int spId;
    /**
     * 注收比
     * 总付费/总新增人数（创角）
     */
    public double zhushoubi;
    /**
     * 新增注收比
     * 每天收入/每天新增创角
     */
    public double addzhushoubi;

    public RechargeSummary() {
    }

    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("date", date);
        json.put("newAddCreateAccount", newAddCreateAccount);


        json.put("newAddCreateRole", newAddCreateRole);
        json.put("newAddCreateRoleRemoveOld", newAddCreateRoleRemoveOld);

        json.put("createAccountRate", createAccountRate);
        json.put("createAccountTransRate", createAccountTransRate);
        json.put("activePlayer", activePlayer);
        json.put("rechargeTimes", rechargeTimes);
        json.put("rechargeNumber", rechargeNumber);
        json.put("rechargePayment", rechargePayment);
        json.put("activePayRate", activePayRate);
        json.put("paidARPU", paidARPU);
        json.put("activeARPU", activeARPU);
        json.put("nofPayers", nofPayers);
        json.put("nofPayment", nofPayment);
        json.put("registeredPayers", registeredPayers);
        json.put("registeredPayment", registeredPayment);
        json.put("registeredPaymentARPU", registeredPaymentARPU);
        json.put("serverId", serverId);
        json.put("openDay", openDay);
        json.put("newaddplayer", newaddplayer);
        json.put("totalPayment", totalPayment);
        json.put("totalCreateRole", totalCreateRole);
        json.put("totalRechargeNums", totalRechargeNums);
        json.put("totalRechargeRates", totalRechargeRates);
        json.put("spId", spId);
        json.put("zhushoubi", zhushoubi);
        json.put("addzhushoubi", addzhushoubi);
        return json.toString();
    }

    @Override
    public String toString() {
        String objectS = "date:" + "\t" + date + "\n" + "\t" +

                "newAddCreateAccount:" + "\t" + newAddCreateAccount + "\n" + "\t" +
                "newAddCreateRole:" + "\t" + newAddCreateRole + "\n" + "\t" +
                "newAddCreateRoleRemoveOld:" + "\t" + newAddCreateRoleRemoveOld + "\n" + "\t" +
                "createAccountRate:" + "\t" + createAccountRate + "\n" + "\t" +

                "createAccountTransRate:" + "\t" + createAccountTransRate + "\n" + "\t" +

                "activePlayer:" + "\t" + activePlayer + "\n" + "\t" +
                "rechargeTimes:" + "\t" + rechargeTimes + "\n" + "\t" +
                "rechargeNumber:" + "\t" + rechargeNumber + "\n" + "\t" +
                "rechargePayment:" + "\t" + rechargePayment + "\n" + "\t" +
                "activePayRate:" + "\t" + activePayRate + "\n" + "\t" +
                "paidARPU:" + "\t" + paidARPU + "\n" + "\t" +
                "activeARPU:" + "\t" + activeARPU + "\n" + "\t" +
                "nofPayers:" + "\t" + nofPayers + "\n" + "\t" +
                "nofPayment:" + "\t" + nofPayment + "\n" + "\t" +
                "registeredPayers:" + "\t" + registeredPayers + "\n" + "\t" +
                "registeredPayment:" + "\t" + registeredPayment + "\n" + "\t" +
                "registeredPaymentARPU" + "\t" + registeredPaymentARPU + "\n" + "\t" +

                "serverId:" + "\t" + serverId + "\n" + "\t" +
                "openDay:" + "\t" + openDay + "\n" + "\t" +
                "newaddplayer:" + "\t" + newaddplayer + "\n" + "\t" +

                "totalPayment:" + "\t" + totalPayment + "\n" + "\t" +
                "totalCreateRole:" + "\t" + totalCreateRole + "\n" + "\t" +
                "totalRechargeNums:" + "\t" + totalRechargeNums + "\n" + "\t" +
                "totalRechargeRates:" + "\t" + totalRechargeRates + "\n" + "\t" +

                "spId:" + "\t" + spId + "\n" + "\t" +
                "zhushoubi:" + "\t" + zhushoubi + "\n" + "\t" +
                "addzhushoubi:" + "\t" + addzhushoubi + "\n";
        return objectS;
    }

    /**
     * 计算各种
     * 1.比率
     * 2.ARPU
     */
    public void calculate(Integer type) {
        this.createAccountRate = 0D;
        this.createAccountTransRate = 0D;
        this.activePayRate = 0D;
        this.paidARPU = 0D;
        this.activeARPU = 0D;
        this.registeredPaymentARPU = 0D;
        this.totalRechargeRates = 0D;
        this.zhushoubi = 0D;
        this.addzhushoubi = 0D;

        //活跃付费率
        if (this.activePlayer == 0) {
            this.setActivePayRate(0);
        } else {
            this.setActivePayRate(UtilG.format2point((double) this.rechargeNumber / this.activePlayer * 100));
        }
        //付费ARPU
        if (this.rechargeNumber == 0) {
            this.setPaidARPU(0);
        } else {
            this.setPaidARPU((double) this.rechargePayment / this.rechargeNumber);
        }
        //活跃ARPU
        if (this.activePlayer == 0) {
            this.setActiveARPU(0);
        } else {
            this.setActiveARPU((double) this.rechargePayment / this.activePlayer);
        }
        //注册付费ARPU
        if (this.registeredPayers == 0) {
            this.setRegisteredPaymentARPU(0);
        } else {
            this.setRegisteredPaymentARPU((double) this.rechargePayment / this.registeredPayers);
        }


        if (type == 1) {
            //创角率
            if (this.newAddCreateAccount == 0) {
                this.setCreateAccountRate(0);
            } else {
                this.setCreateAccountRate(UtilG.format2point((double) this.newAddCreateRole / this.newAddCreateAccount * 100));
            }
        } else {
            //总付费率
            if (this.totalRechargeRates == 0) {
                this.setTotalRechargeRates(0);
            } else {
//                this.setTotalRechargeRates(UtilG.format2point((double) this.newAddCreateRole / this.newAddCreateAccount * 100));
            }
            if (type == 3) {
                //创号转化率
                if (this.createAccountTransRate == 0) {
                    this.setCreateAccountTransRate(0);
                } else {
//                    this.setCreateAccountTransRate(UtilG.format2point((double) this.newAddCreateRole / this.newAddCreateAccount * 100));
                }
                //注收比
                if (this.zhushoubi == 0) {
                    this.setZhushoubi(0);
                } else {
//                    this.setZhushoubi(UtilG.format2point((double) this.newAddCreateRole / this.newAddCreateAccount * 100));
                }
                //新增注收比
                if (this.addzhushoubi == 0) {
                    this.setAddzhushoubi(0);
                } else {
//                    this.setAddzhushoubi(UtilG.format2point((double) this.newAddCreateRole / this.newAddCreateAccount * 100));
                }
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
