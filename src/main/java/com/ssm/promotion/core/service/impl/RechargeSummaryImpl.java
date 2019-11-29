package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.entity.RechargeSummary;
import com.ssm.promotion.core.jedis.*;
import com.ssm.promotion.core.service.RechargeSummaryService;
import com.ssm.promotion.core.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/11/26
 */
@Service("RechargeSummaryService")
public class RechargeSummaryImpl implements RechargeSummaryService {
    @Autowired
    JedisRechargeCache cache;

    @Override
    public List<RechargeSummary> getRechargeSummary(Map<String, Object> map, List<String> spIdList, Integer userId) throws Exception {
        int type = Integer.parseInt(map.get("type").toString());
        Integer gameId = Integer.parseInt(map.get("gameId").toString());
        Integer serverId = Integer.parseInt(map.get("serverId").toString());
        String startTimes = map.get("serverId").toString();
        String endTime = map.get("serverId").toString();
        //时间转化
        List<String> timeList = DateUtil.getDateStr(startTimes, endTime);
        timeList.forEach(day -> {
            if (day != null && !day.isEmpty()) {
                System.out.println("day:" + day);
            }
        });
        //分游戏、区服、渠道查询
        //查询游戏自己的数据库
        switch (type) {
            case 1: {
                //全服概况

            }
            break;
            case 2: {
                //分服概况

            }
            break;
            case 3: {
                //渠道概况
                for (String spid : spIdList) {
                    //gid:{gid}:sid:{sid}:spid:{spid}
                    String gsskey = RedisKeyBody.genBody(3, gameId, serverId, spid).toString();
                    //gid:{gid}:sid:{sid}
                    String gskey = RedisKeyBody.genBody(2, gameId, serverId, spid).toString();
                    //gid:{gid}
                    String gkey = RedisKeyBody.genBody(1, gameId, serverId, spid).toString();
                    //精确到游戏-区服-渠道
                    String usergsskey = RedisGeneratorKey.genKeyTail(RedisKeyHeader.USER_INFO, gsskey);
                    String activegsskey = RedisGeneratorKey.genKeyTail(RedisKeyHeader.ACTIVE_PLAYERS_INFO, gsskey);
                    //精确到游戏
                    String key1 = RedisGeneratorKey.genKeyTail(RedisKeyHeader.USER_INFO, gkey);

                    //新增创号-<yyMMdd,账号数目>
                    Map<String, Double> timecaMap = cache.getDayBitmapCount(usergsskey, RedisKeyTail.NEW_ADD_CREATE_ACCOUNT, timeList);
                    //新增创角-<yyMMdd,账号数目>
                    Map<String, Double> timecrMap = cache.getDayBitmapCount(usergsskey, RedisKeyTail.NEW_ADD_CREATE_ROLE, timeList);
                    //--所有账号
                    BitSet allAccount = cache.getBitSet(key1, RedisKeyTail.GAME_ACCOUNT_ALL_NUMS);
                    //--所有账号的数目
                    Long allAccountSize = cache.getBitSetCount(key1, RedisKeyTail.GAME_ACCOUNT_ALL_NUMS);
                    //新增创角去除滚服
                    Map<String, Double> timecrroMap = cache.getDayBitopAnd(usergsskey, RedisKeyTail.NEW_ADD_CREATE_ROLE, usergsskey, RedisKeyTail.GAME_ACCOUNT_MULTIPLE_ROLE, timeList);
                    //创角率
                    Map<String, Double> timeCreateAccountRateMap = this.generatorAccountRate(timecrMap, (int) (long) allAccountSize);
                    //活跃玩家
                    BitSet activeAccount = cache.getBitSet(key1, RedisKeyTail.ACTIVE_PLAYERS);
                    //活跃玩家 的账号数目
                    Long activeAccountSize = cache.getBitSetCount(key1, RedisKeyTail.ACTIVE_PLAYERS);
                    //充值次数
                    Map<String, Double> timeRechargeTimesMap = cache.getDayZScore(activegsskey, RedisKeyTail.RECHARGE_INFO, RedisKey.RECHARGE_TIMES, timeList);
                    //充值人数
                    Map<String, Double> timeRechargeAccountsMap = cache.getDayZScore(activegsskey, RedisKeyTail.RECHARGE_INFO, RedisKey.RECHARGE_PLAYERS, timeList);
                    //充值金额
                    Map<String, Double> timeRechargeAmountsMap = cache.getDayZScore(activegsskey, RedisKeyTail.RECHARGE_INFO, RedisKey.RECHARGE_AMOUNTS, timeList);
                    //活跃付费率
                    Map<String, Double> timeactivePayRateMap = this.generatoractivePayRate(timeRechargeAccountsMap, (int) (long) activeAccountSize);
                    //付费ARPU
                    Map<String, Double> timeactivearpuMap = this.generatorpaidARPU(timeRechargeAmountsMap, timeRechargeAccountsMap);
                    //当日首次付费金额
                    Map<String, Double> timeRechargeFirstPayersMap = cache.getDayZScore(activegsskey, RedisKeyTail.RECHARGE_INFO, RedisKey.RECHARGE_FIRST_AMOUNTS, timeList);
                    //当日首次付费人数
                    Map<String, Double> timefraMap = cache.getDayBitmapCount(activegsskey, RedisKeyTail.RECHARGE_ACCOUNT, timeList);
                    //注册付费人数
                    Map<String, Double> timeRegisteredPayersAccountMap = cache.getDayBitmapCount(activegsskey, RedisKeyTail.RECHARGE_ACCOUNT_NA_CA, timeList);
                    //注册付费金额
                    Map<String, Double> timeRegisteredPaymentMap = cache.getDayZScore(activegsskey, RedisKeyTail.RECHARGE_INFO, RedisKey.RECHARGE_AMOUNTS_NA_CA, timeList);
                    //注册付费ARPU
                    Map<String, Double> timeRegisterdPaidarpuMap = this.generatorRegisteredPaidARPU(timeRegisteredPaymentMap, timeRegisteredPayersAccountMap);
                    //累计充值金额
                    Double timeTotalPayment = cache.getZscore(activegsskey, RedisKeyTail.RECHARGE_INFO, RedisKey.GAME_ACCUMULATION_RECHARGE_AMOUNTS);
                    //累计充值人数
                    Double timeTotalRechargeNums = cache.getZscore(activegsskey, RedisKeyTail.RECHARGE_INFO, RedisKey.GAME_ACCUMULATION_RECHARGE_ACCOUNTS);
                    //总付费率
                    Double totalRechargeRates;
                    //渠道id
                    String tmpspId = spid;
                    //注收比
                    double zhushoubi = 0L;
                    //新增注收比
                    double addzhushoubi = 0L;


                    RechargeSummary rs = new RechargeSummary();
                    //新增创号-<yyMMdd,账号数目>
                    rs.setNewAddCreateAccount(this.mapAddInt(timecaMap));
                    //新增创角-<yyMMdd,账号数目>
                    rs.setNewAddCreateRole(this.mapAddInt(timecrMap));
                    //--所有账号

                    //--所有账号的数目
                    //新增创角去除滚服
                    rs.setNewAddCreateRoleRemoveOld(this.mapAddInt(timecrroMap));
                    //创角率
                    rs.setCreateAccountRate(this.mapAddDouble(timeCreateAccountRateMap));
                    //活跃玩家
                    rs.setActivePlayer((int) (long) activeAccountSize);
                    //活跃玩家 的账号数目
                    //充值次数
                    rs.setRechargeTimes(this.mapAddInt(timeRechargeTimesMap));
                    //充值人数
                    rs.setRechargeNumber(this.mapAddInt(timeRechargeAccountsMap));
                    //充值金额
                    rs.setRechargePayment(this.mapAddInt(timeRechargeAmountsMap));
                    //活跃付费率
                    rs.setActivePayRate(this.mapAddDouble(timeactivePayRateMap));
                    //付费ARPU
                    rs.setPaidARPU(this.mapAddDouble(timeactivearpuMap));
                    //当日首次付费金额
                    rs.setNofPayment(this.mapAddInt(timeRechargeFirstPayersMap));
                    //当日首次付费人数
                    rs.setNofPayers(this.mapAddInt(timefraMap));
                    //注册付费人数
                    rs.setRegisteredPayers(this.mapAddInt(timeRegisteredPayersAccountMap));
                    //注册付费金额
                    rs.setRechargePayment(this.mapAddInt(timeRegisteredPaymentMap));
                    //注册付费ARPU
                    rs.setPaidARPU(this.mapAddDouble(timeRegisterdPaidarpuMap));
                    //累计充值金额
                    rs.setTotalPayment(timeTotalPayment);
                    //累计充值人数
                    rs.setTotalRechargeNums(timeTotalRechargeNums);
                    //总付费率
                    rs.setTotalRechargeRates((double) (timeTotalPayment / timeTotalRechargeNums));
                    //渠道id
                    rs.setSpId(Integer.parseInt(spid));
                    //注收比
                    rs.setZhushoubi(zhushoubi);
                    //新增注收比
                    rs.setAddzhushoubi(addzhushoubi);
                }
            }
            break;
            default:
                break;
        }
        return null;
    }

    public int mapAddInt(Map<String, Double> map) {
        double total = 0L;
        for (Double d : map.values()) {
            total += (double) d;
        }
        return (int) total;
    }

    public Double mapAddDouble(Map<String, Double> map) {
        double total = 0L;
        for (Double d : map.values()) {
            total += (double) d;
        }
        return total;
    }

    /**
     * 创角率
     * 新增创角/所有账号的数目
     */
    public Map<String, Double> generatorAccountRate(Map<String, Double> timeCRMap, int allAccountSize) {
        Map<String, Double> map = new LinkedHashMap<>();
        for (Map.Entry<String, Double> e : timeCRMap.entrySet()) {
            String times = e.getKey();
            Double nums = e.getValue();
            map.put(times, (double) (nums / allAccountSize * 100));
        }
        return map;
    }

    /**
     * 活跃付费率
     * 充值人数/活跃玩家
     */
    public Map<String, Double> generatoractivePayRate(Map<String, Double> timeRechargeAccountsMap, int activeAccountSize) {
        Map<String, Double> map = new LinkedHashMap<>();
        for (Map.Entry<String, Double> e : timeRechargeAccountsMap.entrySet()) {
            String times = e.getKey();
            Double nums = e.getValue();
            map.put(times, (double) (nums / activeAccountSize * 100));
        }
        return map;
    }

    /**
     * 付费ARPU
     * 充值金额/充值人数
     */
    public Map<String, Double> generatorpaidARPU(Map<String, Double> timeRechargeAmountsMap, Map<String, Double> timeRechargeAccountsMap) {
        Map<String, Double> map = new LinkedHashMap<>();

        for (String times : timeRechargeAccountsMap.keySet()) {
            Double paidAmounts = timeRechargeAmountsMap.get(times);
            Double paidAccounts = timeRechargeAccountsMap.get(times);
            map.put(times, (double) (paidAmounts / paidAccounts * 100));
        }
        return map;
    }

    /**
     * 注册付费ARPU
     * 注册付费金额/注册付费人数
     */
    public Map<String, Double> generatorRegisteredPaidARPU(Map<String, Double> timeTotalRechargeNumsMap, Map<String, Double> timeRegisteredPayersAccountMap) {
        Map<String, Double> map = new LinkedHashMap<>();

        for (String times : timeTotalRechargeNumsMap.keySet()) {
            Double paidAmounts = timeTotalRechargeNumsMap.get(times);
            Double paidAccounts = timeRegisteredPayersAccountMap.get(times);
            map.put(times, (double) (paidAmounts / paidAccounts * 100));
        }
        return map;
    }
}


