package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.entity.RechargeSummary;
import com.ssm.promotion.core.jedis.JedisRechargeCache;
import com.ssm.promotion.core.jedis.RedisGeneratorKey;
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
        /**
         *        map.put("type", type);
         *         map.put("gameId", gameId);
         *         map.put("serverId", serverId);
         *         map.put("spId", spId);
         *         map.put("startTime", startTime);
         *         map.put("endTime", endTime);
         * */
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
                    RechargeSummary rs = new RechargeSummary();
                    //新增创号-<yyMMdd,账号数目>
                    Map<String, Integer> timeCAMap = cache.getDaySignUp(gameId, serverId, spid, timeList);
                    //新增创角-<yyMMdd,账号数目>
                    Map<String, Integer> timeCRMap = cache.getDayNewAddCreateRole(gameId, serverId, spid, timeList);
                    //--所有账号
                    BitSet allAccount = cache.getGameAccount(gameId, serverId, spid);
                    //--所有账号的数目
                    int allAccountSize = cache.getSizeByBitmap(RedisGeneratorKey.getKeySignUp(gameId, serverId, null, 2, "-1"));
                    //新增创角去除滚服
                    Map<String, Integer> timeCRROMap = cache.getDayNewAddCreateRoleRemoveOld(gameId, serverId, spid, timeList);
                    //创角率
                    Map<String, Double> timeCreateAccountRateMap = this.generatorAccountRate(timeCRMap, allAccountSize);
                    //活跃玩家
                    BitSet activeAccount = cache.getGameActiveAccount(gameId, serverId, spid);
                    //活跃玩家 的账号数目
                    int activeAccountSize = cache.getSizeByBitmap(RedisGeneratorKey.getKeyEnterGame(gameId, serverId, spid, "-1"));
                    //充值次数
                    Map<String, Integer> timeRechargeTimesMap = cache.getDayRechargeSortedSet(gameId, serverId, spid, 1, 1, timeList);
                    //充值人数
                    Map<String, Integer> timeRechargeAccountsMap = cache.getDayRechargeSortedSet(gameId, serverId, spid, 1, 2, timeList);
                    //充值金额
                    Map<String, Integer> timeRechargeAmountsMap = cache.getDayRechargeSortedSet(gameId, serverId, spid, 1, 3, timeList);
                    //活跃付费率
                    Map<String, Double> activePayRateMap = this.generatoractivePayRate(timeRechargeAccountsMap, activeAccountSize);
                    //付费ARPU
                    Map<String, Double> activeARPUMap = this.generatorpaidARPU(timeRechargeAmountsMap, timeRechargeAccountsMap);
                    //当日首次付费金额
                    Map<String, Integer> timeRechargeNOFPayersMap = cache.getDayRechargeSortedSet(gameId, serverId, spid, 1, 4, timeList);
                    //当日首次付费人数

                    //注册付费人数

                    //注册付费金额

                    //注册付费ARPU

                    //累计充值金额
                    Map<String, Integer> timeTotalPaymentMap = cache.getDayRechargeSortedSet(gameId, serverId, spid, 2, 6, timeList);
                    //累计充值人数
                    Map<String, Integer> timeTotalRechargeNumsMap = cache.getDayRechargeSortedSet(gameId, serverId, spid, 2, 7, timeList);
                    //总付费率
                    Double totalRechargeRates;

                    //渠道id
                    String tmpSpId = spid;
                    //注收比
                    Double zhushoubi;
                    //新增注收比
                    Double addzhushoubi;
                }
            }
            break;
            default:
                break;
        }
        return null;
    }

    /**
     * 创角率
     * 新增创角/所有账号的数目
     */
    public Map<String, Double> generatorAccountRate(Map<String, Integer> timeCRMap, int allAccountSize) {
        Map<String, Double> map = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> e : timeCRMap.entrySet()) {
            String times = e.getKey();
            int nums = e.getValue();
            map.put(times, (double) (nums / allAccountSize * 100));
        }
        return map;
    }

    /**
     * 活跃付费率
     * 充值人数/活跃玩家
     */
    public Map<String, Double> generatoractivePayRate(Map<String, Integer> timeRechargeAccountsMap, int activeAccountSize) {
        Map<String, Double> map = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> e : timeRechargeAccountsMap.entrySet()) {
            String times = e.getKey();
            int nums = e.getValue();
            map.put(times, (double) (nums / activeAccountSize * 100));
        }
        return map;
    }

    /**
     * 付费ARPU
     * 充值金额/充值人数
     */
    public Map<String, Double> generatorpaidARPU(Map<String, Integer> timeRechargeAmountsMap, Map<String, Integer> timeRechargeAccountsMap) {
        Map<String, Double> map = new LinkedHashMap<>();

        for (String times : timeRechargeAccountsMap.keySet()) {
            Integer paidAmounts = timeRechargeAmountsMap.get(times);
            Integer paidAccounts = timeRechargeAccountsMap.get(times);
            map.put(times, (double) (paidAmounts / paidAccounts * 100));
        }
        return map;
    }
}


