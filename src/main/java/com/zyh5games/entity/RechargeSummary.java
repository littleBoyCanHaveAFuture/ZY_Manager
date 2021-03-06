package com.zyh5games.entity;

import com.zyh5games.util.UtilG;
import lombok.Data;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

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
    private String date;

    /**
     * 新增创号
     * :当日新注册账号的账号数目，且进入游戏
     * 条件：
     * 1.注册官方账号
     * 2.渠道账号首次登录任意游戏
     */
    private int newAddCreateAccount;

    /**
     * 新增创角
     * :角色数：当天创建的
     */
    private int newAddCreateRole;

    /**
     * 新增创角去除滚服
     * 角色数：当天创建的，且是账号首个角色
     */
    private int newAddCreateRoleRemoveOld;

    /**
     * 创角率
     * :当天新增创角/当天新增创号
     */
    private double createAccountRate;


    /**
     * 创角转化率：该渠道：新增创角之和/新增创号之和
     */
    private double createAccountTransRate;

    //通用
    /**
     * 活跃玩家
     * :当日上线的不同角色
     * 时间段：一个角色只算一次
     */
    private int activePlayer;
    /**
     * 充值次数
     * :当日充值成功的订单数目
     */
    private int rechargeTimes;
    /**
     * 充值人数
     * :当日充值成功的角色数目(不同的角色数目)
     */
    private int rechargeNumber;
    /**
     * 充值金额
     * :当日充值成功的金额之和
     */
    private int rechargePayment;
    /**
     * 活跃付费率
     * :当日充值的角色数目/当日上线的角色数目
     */
    private double activePayRate;
    /**
     * 付费ARPU
     * :当日充值总额/当日充值的角色数目
     */
    private double paidARPU;
    /**
     * 活跃ARPU
     * :当日充值总额/当日上线的角色数目
     */
    private double activeARPU;
    /**
     * 当日首次付费人数
     * 角色数目：注册时间超过一天的，首次充值的
     */
    private int nofPayers;
    /**
     * 当日首次付费金额
     * 当天充值总金额：注册时间超过一天的，首次充值的角色
     */
    private int nofPayment;
    /**
     * 注册付费人数
     * : 当日注册并且充值的账号数目
     */
    private int registeredPayers;
    /**
     * 注册付费金额
     * : 当日注册并且充值的角色支付金额数目
     */
    private int registeredPayment;
    /**
     * 注册付费ARPU
     * : 当日注册并且充值的玩家支付金额数目/当日注册并且充值的玩家账号数目
     */
    private double registeredPaymentARPU;


//分服2
    /**
     * 服务器id
     */
    private int serverId;
    /**
     * 开服天数
     */
    private int openDay;

    /**
     * 新增创号
     */
    private int newaddplayer;


//分服概况\分渠道 =2/3
    /**
     * 累计充值
     * 开服起：每天充值金额之和
     */
    private int totalPayment;

    /**
     * 累计创角
     * 开服起：每天新增创角之和
     */
    private int totalCreateRole;
    /**
     * 累计充值人数
     * 开服起：不同充值角色数目之和（每个角色只计算一次）
     */
    private int totalRechargeNums;
    /**
     * 总付费率
     * 付费角色数目/所有的角色数目
     */
    private double totalRechargeRates;
    /**
     * 该渠道该游戏所有的账号数目
     */
    private double totalAccounts;

//    分渠道概况3
    /**
     * 渠道id
     */
    private int spId;
    /**
     * 注收比
     * 该服务器充值总金额/总创角数
     */
    private double zhushoubi;
    /**
     * 无用
     * 新增注收比
     */
    private double addzhushoubi;

    public RechargeSummary() {
    }

    public static void main(String[] args) {
//        RechargeSummary s = new RechargeSummary();
//        s.date = "11111";
//        String json = JSONObject.toJSONString(s);
//        RechargeSummary ss = JSONObject.parseObject(json, RechargeSummary.class);
//        System.out.println(json);


        Set<String> set = new HashSet<>();

        set.add("20200303");
        set.add("20180103");
        set.add("20180104");
        set.add("20180105");
        set.add("20180101");
        set.stream().sorted(Comparator.reverseOrder());
        System.out.println(set.toString());
    }

    public void addDayInfo(RechargeSummary rs) {
        this.newAddCreateAccount += rs.getNewAddCreateAccount();
        this.newAddCreateRole += rs.getNewAddCreateRole();
        this.newAddCreateRoleRemoveOld += rs.getNewAddCreateRoleRemoveOld();
        this.activePlayer += rs.getActivePlayer();
        this.rechargeTimes += rs.getRechargeTimes();
        this.rechargeNumber += rs.getRechargeNumber();
        this.rechargePayment += rs.getRechargePayment();
        this.registeredPayers += rs.getRegisteredPayers();
        this.registeredPayment += rs.getRegisteredPayment();

        this.totalPayment += rs.getTotalPayment();
//        this.totalRechargeTimes += rs.getTotalRechargeTimes();
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
            this.setPaidARPU(UtilG.format2point((double) this.rechargePayment / this.rechargeNumber));
        }
        //活跃ARPU
        if (this.activePlayer == 0) {
            this.setActiveARPU(0);
        } else {
            this.setActiveARPU((UtilG.format2point((double) this.rechargePayment / this.activePlayer)));
        }
        //注册付费ARPU
        if (this.registeredPayers == 0) {
            this.setRegisteredPaymentARPU(0);
        } else {
            this.setRegisteredPaymentARPU(UtilG.format2point((double) this.registeredPayment / this.registeredPayers));
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
            if (this.totalCreateRole == 0) {
                this.setTotalRechargeRates(0);
            } else {
                this.setTotalRechargeRates(UtilG.format2point((double) this.totalRechargeNums / this.totalCreateRole * 100));
            }
            if (type == 3) {
                //创号转化率
                if (this.totalAccounts == 0) {
                    this.setCreateAccountTransRate(0);
                } else {
//                    this.setCreateAccountTransRate(UtilG.format2point((double) this.totalCreateRole / this.totalAccounts * 100));
                }

                if (this.totalCreateRole == 0) {
                    //注收比
                    this.setZhushoubi(0);
                } else {
                    this.setZhushoubi(UtilG.format2point((double) this.totalPayment / this.totalCreateRole * 100));
                }
                //新增注收比
                this.setAddzhushoubi(0);
            }
        }
    }

    public void add(RechargeSummary add) {
        //1/3
        newAddCreateAccount += add.newAddCreateAccount;
        newAddCreateRole += add.newAddCreateRole;
        newAddCreateRoleRemoveOld += add.newAddCreateRoleRemoveOld;

        //通用
        rechargeTimes += add.rechargeTimes;
        rechargeNumber += add.rechargeNumber;
        rechargePayment += add.rechargePayment;
        nofPayers += add.nofPayers;
        nofPayment += add.nofPayment;
        registeredPayers += add.registeredPayers;
        registeredPayment += add.registeredPayment;
        newaddplayer += add.newaddplayer;

    }

}
