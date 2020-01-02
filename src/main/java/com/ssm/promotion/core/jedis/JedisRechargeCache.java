package com.ssm.promotion.core.jedis;


import com.ssm.promotion.core.entity.RechargeSummary;
import com.ssm.promotion.core.util.DateUtil;
import com.ssm.promotion.core.util.SerializeUtil;
import org.apache.log4j.Logger;
import redis.clients.jedis.*;

import java.util.*;
import java.util.stream.Collectors;

import static com.ssm.promotion.core.jedis.RedisKeyHeader.RS_INFO;

public class JedisRechargeCache {
    private static final Logger log = Logger.getLogger(JedisRechargeCache.class);
    /**
     * 为了不和其他的缓存混淆，采用追加前缀方式以作区分
     */
    private static final String REDIS_RECHARGE_CACHE = "recharge-cache:";
    /**
     * Redis 分片(分区)，也可以在配置文件中配置
     */
    private static final int DB_INDEX = 3;
    private jedisManager jedisManager;

    private boolean isLog = true;

//键值参考：账号id 一定要唯一
/*
    新增创号    BitMap
    UserInfo:spid:{spid}:gid:{gid}:date:{yyyyMMdd}#NEW_ADD_CREATE_ACCOUNT {account_Id}

    该游戏所有账号 BitMap
    UserInfo:spid:{spid}:gid:{gid}#GAME_ACCOUNT_ALL_NUMS {account_Id}

    创建过角色的账号    BitMap
    UserInfo:spid:{spid}:gid:{gid}#GAME_ACCOUNT_HAS_ROLE {account_Id}

    活跃玩家    Sorted Set
    UserInfo:spid:{spid}:gid:{gid}:sid:{sid}#ACTIVE_PLAYERS score {yyyyMMdd}

    在线玩家    Sorted Set
    UserInfo:spid:{spid}:gid:{gid}:sid:{sid}#ONLINE_PLAYERS score {yyyyMMdd}

    新增创角    Sorted Set
    UserInfo:spid:{spid}:gid:{gid}:sid:{sid}#NEW_ADD_CREATE_ROLE    score   {yyyyMMdd}

    新增创角去除滚服
    UserInfo:spid:{spid}:gid:{gid}:sid:{sid}#NEW_ADD_CREATE_ROLE_RM_OLD    score   {yyyyMMdd}

    累计创角    Sorted Set
    UserInfo:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#ACCOUNT_INFO    score   {yyyyMMdd}

    当日付费角色  Set
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_ROLES" {role_id}

    历史付费角色  Set
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}#RECHARGE_ACCOUNT" {role_id}

    充值次数    Sorted Set
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_INFO {score} RECHARGE_TIMES
    充值人数    Sorted Set
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_INFO {score} RECHARGE_PLAYERS
    充值金额    Sorted Set
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_INFO {score} RECHARGE_AMOUNTS

    累计充值金额    Sorted Set
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}#RECHARGE_INFO {score} GAME_ACCUMULATION_RECHARGE_AMOUNTS
    累计充值人数    Sorted Set
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}#RECHARGE_INFO {score} GAME_ACCUMULATION_RECHARGE_ACCOUNTS

    注册付费人数    Sorted Set
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}RECHARGE_ROLES_NA_CR {score} {yyyyMMdd}
    注册付费金额
    ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}RECHARGE_AMOUNTS_NA_CR {score} {yyyyMMdd}

    当日首次付费人数    Sorted Set
     ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}RECHARGE_FIRST_PAY_ROLES {score} {yyyyMMdd}
    当日首次付费金额    Sorted Set
     ACTIVE_PLAYERS_INFO:spid:{spid}:gid:{gid}:sid:{sid}RECHARGE_FIRST_PAY_AMOUNTS {score} {yyyyMMdd}
 */

    public static void main(String[] args) {
        List<String> list = new ArrayList<String>();
        list.add("JavaWeb编程词典");  //向列表中添加数据
        list.add("Java编程词典");  //向列表中添加数据
        list.add("C#编程词典");  //向列表中添加数据
        list.add("ASP.NET编程词典");  //向列表中添加数据
        list.add("VC编程词典");  //向列表中添加数据
        list.add("SQL编程词典");  //向列表中添加数据

        Iterator<String> its = list.iterator();  //获取集合迭代器
        System.out.println("\n集合中所有元素对象:");

        while (its.hasNext()) {  //循环遍历集合
            System.out.println(its.next() + "");  //输出集合内容
        }
        List<String> subList = list.subList(0, 3);  //获取子列表
        System.out.println("\n截取集合中部分元素:");

        Iterator it = subList.iterator();
        while (it.hasNext()) {
            System.out.println(it.next() + "");
        }

        System.out.println("\n:");
        list.forEach(v -> System.out.println(v));
    }

    public jedisManager getJedisManager() {
        return jedisManager;
    }

    /**
     * bean
     */
    public void setJedismanager(jedisManager jedismanager) {
        this.jedisManager = jedismanager;
    }

    /**
     * 存储查询结果
     */
    public void setRechargeSummary(List<RechargeSummary> rsList, List<String> timelist, Integer type) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            Pipeline pipeline = jds.pipelined();
            if (type == 1) {
                for (RechargeSummary rs : rsList) {
                    Double yyyyMMdd = 0D;
                    String yyyyMM = rs.getDate();
                    String key = RS_INFO + ":date:" + yyyyMM + "#" + RedisKeyTail.RECHARGE_SUMMARY;
                    pipeline.zadd(key, yyyyMMdd, rs.toString());
                }
            } else if (type == 2) {

            } else if (type == 3) {

            }


        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 每分钟存储实时数据
     */
    public void setRealtimeData() {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            this.setMinData(jds);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 每分钟设置实时数据
     * 1.查询实时数据键值
     * 2.更新数据
     */
    public void setMinData(Jedis jedis) throws Exception {
        // 游标初始值为0
        String cursor = ScanParams.SCAN_POINTER_START;
        //当天时间
        String currDay = DateUtil.getCurrentDayStr();
        //当前分钟
        String currDayMin = DateUtil.getCurrentMinuteStr();
        //下一分钟
        String nextMin = DateUtil.getCurrentMinuteStr(1);

        //实时在线玩家数据
        String key1 = RedisKeyHeader.USER_INFO + ":spid:*:gid:*:sid:*" + "#" + RedisKeyTail.ONLINE_PLAYERS;
        String key2 = RedisKeyHeader.REALTIMEDATA + ":spid:*:gid:*:sid:*:date:" + currDayMin + "#" + RedisKeyTail.REALTIME_ONLINE_ACCOUNTS;
        //查询的键
        String patternKey = key1;

        ScanParams scanParams = new ScanParams();
        // 匹配以 {header}:spid:*:gid:*:sid:*:date:*#{tail} 为前缀的 key
        scanParams.match(patternKey);
        scanParams.count(500);

        //当天的实时在线玩家数据-有序集合键值
        List<String> targetKeyList = new ArrayList<>();
        Pipeline pipeline = jedis.pipelined();

        do {
            long t1 = System.currentTimeMillis();
            //使用scan命令获取500条数据，使用cursor游标记录位置，下次循环使用
            ScanResult<String> scanResult = jedis.scan(cursor, scanParams);

            cursor = scanResult.getCursor();
            List<String> list = scanResult.getResult();

            //每分钟的在线玩家
            //亦或|或 都可以 反正 key2 此刻不存在 均为0
            for (String mapEntry : list) {
                String[] keys = mapEntry.split(":");
                StringBuilder targetbody = new StringBuilder();
                // :spid:*:gid:*:sid:*#
                for (int i = 1; i <= 5; i++) {
                    targetbody.append(keys[i]).append(":");
                }
                targetbody.append(keys[6].split("#")[0]).append(":");
                String target = RedisKeyHeader.REALTIMEDATA + ":" + targetbody + "date:" + currDay + "#" + RedisKeyTail.REALTIME_ONLINE_ACCOUNTS;
                targetKeyList.add(target);
                if (isLog) {
                    System.out.println("src key------>" + mapEntry);
                    System.out.println("target key--->" + target);
                }
                pipeline.zscore(mapEntry, currDay);
            }
            //给当前时间实际在线添加数值
            List<Object> res = pipeline.syncAndReturnAll();
            for (int i = 0; i < res.size(); i++) {
                String targetKey = targetKeyList.get(i);
                double num = 0D;
                if (res.get(i) != null) {
                    num = Double.parseDouble(res.get(i).toString());
                }
                pipeline.zadd(targetKey, num, currDayMin);
                if (isLog) {
                    System.out.println("target key:" + targetKey + "\tmember:" + currDayMin + "\t" + num);
                }
            }
            targetKeyList.clear();
            pipeline.sync();

            long t2 = System.currentTimeMillis();
            if (isLog) {
                System.out.println("find " + list.size() + " key,use: " + (t2 - t1) + " ms,cursor:" + cursor);
            }
        } while (!"0".equals(cursor));


        pipeline.close();
    }

    public void setOfflineData(Jedis jedis) throws Exception {
        // 游标初始值为0
        String cursor = ScanParams.SCAN_POINTER_START;
        //当天时间
        String currDay = DateUtil.getCurrentDayStr();
        String nextDay = DateUtil.formatDate(DateUtil.getEndTimestamp() * 1000, DateUtil.FORMAT_YYMMDD);
        //当前分钟
        String currDayMin = DateUtil.getCurrentMinuteStr();
        //下一分钟 大概是第二天 0:00:50
        String nextMin = DateUtil.getCurrentMinuteStr(1);

        //实时在线玩家数据
        String key1 = RedisKeyHeader.USER_INFO + ":spid:*:gid:*:sid:*" + "#" + RedisKeyTail.ONLINE_PLAYERS;
        String patternKey = key1;
        ScanParams scanParams = new ScanParams();
        // 匹配以 {header}:spid:*:gid:*:sid:*:date:*#{tail} 为前缀的 key
        scanParams.match(patternKey);

        scanParams.count(500);

        //当天的实时在线玩家数据-有序集合键值
        Pipeline pipeline = jedis.pipelined();
        do {
            long t1 = System.currentTimeMillis();
            //使用scan命令获取500条数据，使用cursor游标记录位置，下次循环使用
            ScanResult<String> scanResult = jedis.scan(cursor, scanParams);

            cursor = scanResult.getCursor();
            List<String> list = scanResult.getResult();

            for (String mapEntry : list) {
                pipeline.zscore(mapEntry, currDay);
                if (isLog) {
                    System.out.println("src key------>" + mapEntry);
                }
            }

            List<Object> res = pipeline.syncAndReturnAll();
            int i = 0;
            for (String mapEntry : list) {
                double ss = 0D;
                if (res.get(i) != null) {
                    ss = Double.parseDouble(res.get(i).toString());
                }
                pipeline.zadd(mapEntry, ss, nextDay);
                i++;
            }
            pipeline.sync();
            if (isLog) {
                System.out.println("find " + list.size() + " key,use: " + (System.currentTimeMillis() - t1) + " ms,cursor:" + cursor);
            }
        } while (!"0".equals(cursor));
    }

    /**
     * 每天12点更新活跃玩家实时数据
     * todo
     */
    public void updateNextDayOnlineData() {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

//            this.setOfflineData(jds);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 查询有序集合键值
     */
    public void zscan(Map<String, Object> map, String key, List<String> timeList, String cursor, Jedis jds, boolean isInt) {
        do {
            //使用scan命令获取500条数据，使用cursor游标记录位置，下次循环使用
            ScanResult<Tuple> scanResult = jds.zscan(key, cursor);
            cursor = scanResult.getCursor();
            List<Tuple> list = scanResult.getResult();
            for (Tuple tuple : list) {
                String member = tuple.getElement();
                double score = tuple.getScore();

                if (timeList.contains(member)) {
                    if (isInt) {
                        int scorei = (int) score;
                        map.put(member, scorei);
                    } else {
                        map.put(member, score);
                    }

                }
            }
        } while (!"0".equals(cursor));
    }

    /**
     * 获取实时数据
     *
     * @param currday  查询的日期-yyyyMMdd
     * @param timeList 查询的日期-具体到小时分钟-yyyyMMddHHmm 时间
     *                 与上面是同一天
     * @return
     */
    public void getRealtimeData(String spId, Integer gameId, Integer serverId, String currday, List<String> timeList,
                                List<Integer> newadd, List<Integer> online, List<Double> money) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            Pipeline pipeline = jds.pipelined();

            Map<String, Object> map = new HashMap<>();

            String realtimeSGSKey = String.format("%s:spid:%s:gid:%s:sid:%s:date:", RedisKeyHeader.REALTIMEDATA, spId, gameId, serverId);


            String key1 = realtimeSGSKey + currday + "#" + RedisKeyTail.REALTIME_ONLINE_ACCOUNTS;
            String key2 = realtimeSGSKey + currday + "#" + RedisKeyTail.REALTIME_RECHARGE_AMOUNTS;
            String key3 = realtimeSGSKey + currday + "#" + RedisKeyTail.REALTIME_ADD_ROLES;
            if (isLog) {
                System.out.println("getRealtimeData key:" + key1);
                System.out.println("getRealtimeData key:" + key2);
                System.out.println("getRealtimeData key:" + key3);
            }


            // 游标初始值为0
            String cursor = ScanParams.SCAN_POINTER_START;
            //在线
            this.zscan(map, key1, timeList, cursor, jds, true);
            for (String times : timeList) {
                online.add(Integer.parseInt(map.getOrDefault(times, 0).toString()));
            }
            map.clear();

            //收入
            cursor = ScanParams.SCAN_POINTER_START;
            this.zscan(map, key2, timeList, cursor, jds, false);
            for (String times : timeList) {
                money.add(Double.parseDouble(map.getOrDefault(times, 0D).toString()));
            }
            map.clear();

            //新增角色
            cursor = ScanParams.SCAN_POINTER_START;
            this.zscan(map, key3, timeList, cursor, jds, true);
            for (String times : timeList) {
                newadd.add(Integer.parseInt(map.getOrDefault(times, 0).toString()));
            }
            map.clear();
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 注册账号
     * 1.新增创号
     * 2.该游戏所有账号
     */
    public void register(Map<String, String> map) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            boolean auto = Boolean.parseBoolean(map.get("auto"));
            Integer gameId = Integer.parseInt(map.get("appId"));
            long accountId = Long.parseLong(map.get("accountId"));
            String currday = DateUtil.getCurrentDayStr();

            String key1;
            String key2;
            if (auto) {
                String spId = map.get("channelId");
                //渠道-游戏
                String userSGKey = String.format(RedisKey.FORMAT_SG, RedisKeyHeader.USER_INFO, spId, gameId);
                //新增创号：渠道-游戏-日期
                key1 = userSGKey + ":date:" + currday + "#" + RedisKeyTail.NEW_ADD_CREATE_ACCOUNT;
                //渠道-该游戏所有账号 渠道-游戏
                key2 = userSGKey + "#" + RedisKeyTail.GAME_ACCOUNT_ALL_NUMS;
            } else {
                //官方-游戏
                String userOffcialKey = String.format("%s:%s:gid:%d", RedisKeyHeader.USER_INFO, RedisKeyBody.OFFICIAL, gameId);
                //新增创号-官方 官方-游戏
                key1 = userOffcialKey + ":date:" + currday + "#" + RedisKeyTail.NEW_ADD_CREATE_ACCOUNT;
                //官方-该游戏所有账号 官方-游戏
                key2 = userOffcialKey + "#" + RedisKeyTail.GAME_ACCOUNT_ALL_NUMS;
            }

            Pipeline pipeline = jds.pipelined();
            pipeline.setbit(key1, accountId, true);
            pipeline.setbit(key2, accountId, true);

            pipeline.sync();

        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 进入游戏
     * 1.活跃玩家
     * 2.在线玩家
     * 3.在线时间-在线玩家
     *
     * @param appId     游戏id
     * @param serverId  区服id
     * @param channelId 渠道id
     * @param accountId 角色id
     */
    public void enterGame(String appId,
                          String serverId,
                          String channelId,
                          long accountId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String currDay = DateUtil.getCurrentDayStr();
            String currDayMin = DateUtil.getCurrentMinuteStr();

            String userSGSKey = String.format(RedisKey.FORMAT_SGS_SSS, RedisKeyHeader.USER_INFO, channelId, appId, serverId);
            String realtimeSGSKey = String.format(RedisKey.FORMAT_SGS_SSS, RedisKeyHeader.REALTIMEDATA, channelId, appId, serverId);

            Pipeline pipeline = jds.pipelined();

            //新版
            //活跃玩家
            pipeline.zincrby(RedisKeyNew.getKeyRolesActiveDay(channelId, appId, serverId), 1, currDay);
            //在线玩家
            pipeline.zincrby(RedisKeyNew.getKeyRolesOnlineDay(channelId, appId, serverId), 1, currDay);
            //实时在线
            pipeline.zincrby(RedisKeyNew.getKeyRolesOnlineMin(channelId, appId, serverId, currDay), 1, currDayMin);
            //设置过期时间
            //todo
            pipeline.sync();


//            //活跃账号 渠道-游戏-区服-日期
//            String key1 = userSGSKey + ":date:" + currDay + "#" + RedisKeyTail.ACTIVE_PLAYERS;
//            //在线账号 渠道-游戏-区服-日期
//            String key2 = userSGSKey + ":date:" + currDay + "#" + RedisKeyTail.ONLINE_PLAYERS;
//            //实时在线
//            String key3 = realtimeSGSKey + ":date:" + currDayMin + "#" + RedisKeyTail.REALTIME_ONLINE_ACCOUNTS;
//
//            pipeline.setbit(key1, accountId, true);
//            pipeline.setbit(key2, accountId, true);
////            pipeline.expireAt(key2, DateUtil.getEndTimestamp());
//            pipeline.setbit(key3, accountId, true);
////            pipeline.expire(key3, (int) ((int) DateUtil.MONTH_MILLIS / DateUtil.SECOND_MILLIS));
//
//            List<Object> res = pipeline.syncAndReturnAll();
//            if (isLog) {
//                System.out.println("enterGame key1:" + key1 + "\taccountId:" + accountId + "\tresult:[" + res.get(0).toString() + "]");
//                System.out.println("enterGame key2:" + key2 + "\taccountId:" + accountId + "\tresult:[" + res.get(1).toString() + "]");
//            }
//            log.info("enterGame key1:" + key1 + "\taccountId:" + accountId + "\tresult:[" + res.get(0).toString() + "]");
//            log.info("enterGame key2:" + key2 + "\taccountId:" + accountId + "\tresult:[" + res.get(1).toString() + "]");


        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 创建角色
     * 1.新增创角
     * 2.创建过角色的账号
     * 3.新增创角去除滚服
     * 4.创角率
     * 5.累计创角
     *
     * @param appId     游戏id
     * @param serverId  区服id
     * @param channelId 渠道id
     * @param accountId 指悦账号id
     * @param accountId 角色id
     */
    public void createRole(String appId,
                           String serverId,
                           String channelId,
                           long accountId, long roleId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            //当前分钟
            String currDayMin = DateUtil.getCurrentMinuteStr();
            String currDay = DateUtil.getCurrentDayStr();

            //是否滚服用户
            boolean hasRole = jds.getbit(RedisKeyNew.getKeyAccountCreateRoles(channelId, appId), accountId);
            System.out.println("hasRole:" + hasRole);
            log.info("hasRole:" + hasRole);

            //新版
            //新增创角
            jds.zincrby(RedisKeyNew.getKeyRolesCreateDay(channelId, appId, serverId), 1, currDay);
            if (!hasRole) {
                //新增创角去除滚服
                jds.zincrby(RedisKeyNew.getKeyRolesCreateFirst(channelId, appId, serverId), 1, currDay);
                //创建过角色的账号
                jds.setbit(RedisKeyNew.getKeyAccountCreateRoles(channelId, appId), accountId, true);
            }
            //实时创角
            jds.zincrby(RedisKeyNew.getKeyRolesCreateMin(channelId, appId, serverId, currDay), 1, currDayMin);
            //累计创角数目(所有角色)
            jds.zincrby(RedisKeyNew.getKeyRolesCreateServer(channelId, appId, serverId), 1, RedisKey.GAME_ACCUMULATION_CREATE_ROLE);

        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 充值
     * 1.充值次数
     * 2.充值人数
     * 3.充值金额
     * 4.当日首次付费金额
     * 5.注册付费金额
     * 6.累计充值金额
     * 7.累计充值人数
     * 8.当日首次付费人数
     * 9.注册付费人数
     *
     * @param appId      游戏id
     * @param serverId   区服id
     * @param channelId  渠道id
     * @param accountId  账号id
     * @param roleId     角色id
     * @param payamounts 支付金额
     * @param createTime 账号注册时间
     */
    public void reqpay(String appId, String serverId, String channelId, long accountId, String roleId, long payamounts,
                       String createTime) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            //当前分钟
            String currDayMin = DateUtil.getCurrentMinuteStr();
            //当天时间
            String currDay = DateUtil.getCurrentDayStr();


            //账号创建时间 + 一天
            long currtimes = System.currentTimeMillis();
            //当天凌晨 24:00的时间戳
            long endtime = DateUtil.getEndTimestamp() * 1000;
            long createtime = DateUtil.formatString(createTime, DateUtil.FORMAT_YYYY_MMDD_HHmmSS).getTime();
            //新版
            //今日充值过的角色
            boolean hasPaidCurrDay = jds.sismember(RedisKeyNew.getKeyRolesPaidDay(channelId, appId, serverId, currDay), roleId);
            //历史付费
            boolean hasPaid = jds.sismember(RedisKeyNew.getKeyRolesPaidServer(channelId, appId, serverId), roleId);
            //当日新增创号的角色
            boolean hasRegCurrday = (createtime < endtime) && (createtime > endtime - DateUtil.DAY_MILLIS);
            //创号超过一天的角色 首次付费
            boolean hasRegYesterday = (currtimes - createtime >= DateUtil.DAY_MILLIS);

            //当天充值
            //充值次数
            jds.zincrby(RedisKeyNew.getKeyRolesPayInfoDay(channelId, appId, serverId, currDay), 1, RedisKey.RECHARGE_TIMES);
            //充值金额
            jds.zincrby(RedisKeyNew.getKeyRolesPayInfoDay(channelId, appId, serverId, currDay), payamounts, RedisKey.RECHARGE_AMOUNTS);
            if (!hasPaidCurrDay) {
                //充值人数
                jds.zincrby(RedisKeyNew.getKeyRolesPayInfoDay(channelId, appId, serverId, currDay), 1, RedisKey.RECHARGE_PLAYERS);
                //今日充值过的角色
                jds.sadd(RedisKeyNew.getKeyRolesPaidDay(channelId, appId, serverId, currDay), roleId);
            }

            //实时充值
            //充值金额-当前分钟
            jds.zincrby(RedisKeyNew.getKeyRolesPaidMin(channelId, appId, serverId, currDay), payamounts, currDayMin);


            //累计充值
            //充值金额
            jds.zincrby(RedisKeyNew.getKeyRolesPayInfoServer(channelId, appId, serverId), payamounts, RedisKey.GAME_ACCUMULATION_RECHARGE_AMOUNTS);
            if (!hasPaid) {
                //充值人数 也阔以通过计算下面的size 获得
                jds.zincrby(RedisKeyNew.getKeyRolesPayInfoServer(channelId, appId, serverId), 1, RedisKey.GAME_ACCUMULATION_RECHARGE_ACCOUNTS);
                //历史付费角色
                jds.sadd(RedisKeyNew.getKeyRolesPaidServer(channelId, appId, serverId), roleId);
            }

            //当日注册付费
            if (hasRegCurrday) {
                if (!hasPaidCurrDay) {
                    //注册付费人数
                    jds.zincrby(RedisKeyNew.getKeyRegisterPaidRoles(channelId, appId, serverId), 1, currDay);
                }
                //注册付费金额
                jds.zincrby(RedisKeyNew.getKeyRegisterPaidAmounts(channelId, appId, serverId), payamounts, currDay);
            }

            //当日首次付费
            if (hasRegYesterday) {
                if (!hasPaid) {
                    //当日首次付费人数
                    jds.zincrby(RedisKeyNew.getKeyFirstPaidRoles(channelId, appId, serverId), 1, currDay);
                    //当日首次付费金额
                    jds.zincrby(RedisKeyNew.getKeyFirstPaidRolesAmounts(channelId, appId, serverId), payamounts, currDay);
                }
            }

        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * redis 管道
     * 退出游戏
     * 1.在线玩家
     *
     * @param appId     游戏id
     * @param serverId  区服id
     * @param channelId 渠道id
     * @param accountId 角色id
     */
    public void exitGame(String appId,
                         String serverId,
                         String channelId,
                         long accountId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String currDay = DateUtil.getCurrentDayStr();
            String userSGSKey = String.format(RedisKey.FORMAT_SGS_SSS, RedisKeyHeader.USER_INFO, channelId, appId, serverId);
            //活跃账号 渠道-游戏-区服-日期
            String key1 = userSGSKey + ":date:" + currDay + "#" + RedisKeyTail.ACTIVE_PLAYERS;
            //在线账号 渠道-游戏-区服-日期
            String key2 = userSGSKey + ":date:" + currDay + "#" + RedisKeyTail.ONLINE_PLAYERS;


            boolean res1 = jds.getbit(key1, accountId);
            System.out.println("exit key:" + res1);

            if (!res1) {
                boolean res2 = jds.setbit(key1, accountId, true);
                if (isLog) {
                    System.out.println("enterGame key1:" + key1 + "\taccountId:" + accountId + "\tresult:[" + res2 + "]");
                }
            } else {
                boolean res3 = jds.setbit(key2, accountId, false);
//                jds.expireAt(key2, DateUtil.getEndTimestamp());
                if (isLog) {
                    System.out.println("enterGame key2:" + key2 + "\taccountId:" + accountId + "\tresult:[" + res3 + "]");
                }
            }


        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    public void zincrby(String key, long score, String member) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = this.jedisManager.getJedis();
            jds.select(DB_INDEX);

            jds.zincrby(key, score, member);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }

    }

    public Double getZscore(String key, String member) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = this.jedisManager.getJedis();
            jds.select(DB_INDEX);

            return jds.zscore(key, member);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }

    public void getDayBitCount(String keyBody, List<String> timeList, List<String> keyTailList,
                               Map<String, Map<String, Double>> resultList) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            Pipeline pipeline = jds.pipelined();

            int tlSize = timeList.size();
            int keySize = keyTailList.size();

            //时间遍历
            for (String keytail : keyTailList) {
                for (String times : timeList) {
                    pipeline.bitcount(keyBody + ":date:" + times + "#" + keytail);
                }
            }

            List<Object> res = pipeline.syncAndReturnAll();
            if (res == null || res.size() == 0) {
                return;
            }

            for (int i = 0; i < keySize; i++) {
                int start = i * tlSize;
                int end = (i + 1) * tlSize;
                List<Double> codesDouble = res.subList(start, end).
                        stream().
                        map(e -> Double.parseDouble(e.toString())).
                        collect(Collectors.toList());

                Map<String, Double> resultmap = new HashMap<>();
                int f = 0;
                for (String times : timeList) {
                    resultmap.put(times, codesDouble.get(f));
                    f++;
                }
                resultList.put(keyTailList.get(i), resultmap);
            }

        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }

    }


    public void getDayZScore(String keyBody, List<String> timeList, String keyTail, List<String> memberList,
                             Map<String, Map<String, Double>> resultList) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            Pipeline pipeline = jds.pipelined();

            int tlSize = timeList.size();
            int memberSize = memberList.size();

            //时间遍历
            for (String times : timeList) {
                String key = keyBody + ":date:" + times + "#" + keyTail;
                for (String member : memberList) {
                    pipeline.zscore(key, member);
                }
            }

            List<Object> res = pipeline.syncAndReturnAll();
            if (res == null || res.size() == 0) {
                return;
            }

            for (int i = 0; i < memberSize; i++) {
                int start = i * tlSize;
                int end = (i + 1) * tlSize;
                List<Double> codesDouble = res.subList(start, end).
                        stream().
                        map(
                                e -> {
                                    double re;
                                    if (e != null) {
                                        re = Double.parseDouble(e.toString());
                                    } else {
                                        re = 0D;
                                    }
                                    return re;
                                }
                        ).
                        collect(Collectors.toList());

                Map<String, Double> resultmap = new HashMap<>();
                int f = 0;
                for (String times : timeList) {
                    resultmap.put(times, codesDouble.get(f));
                    f++;
                }
                resultList.put(memberList.get(i), resultmap);
            }
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }


    /**
     * 获取Sorted Set 分数
     * <p>
     *
     * @param keyListMap 1.每天：活跃玩家
     *                   2.每天：在线玩家
     *                   3.每天：新增创角
     *                   4.每天：新增创角去除滚服
     *                   5.每天：注册付费金额
     *                   7.每天：注册付费人数
     *                   8.每天：当日首次付费人数
     *                   9.每天：当日首次付费金额
     * @param timeList   时间字符串 yyyyMMdd
     * @param resultList 查询结果
     *                   key keytail
     *                   value （times，score）
     */
    public void getDayZscore(Map<String, List<String>> keyListMap, List<String> timeList, Map<String, Map<String, Double>> resultList) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            Pipeline pipeline = jds.pipelined();

            int tlSize = timeList.size();

            for (Map.Entry<String, List<String>> entry : keyListMap.entrySet()) {
                String type = entry.getKey();
                //同一渠道 同一游戏 不同区服
                List<String> keyList = entry.getValue();
                // 按照时间顺序 获取分数
                for (String key : keyList) {
                    for (String times : timeList) {
                        pipeline.zscore(key, times);
                    }
                }
            }
            //res 大小为：键类型数*区服数*天数
            // [key][serverId][day]
            List<Object> res = pipeline.syncAndReturnAll();
            if (res == null || res.size() == 0) {
                return;
            }

            int i = 0;
            for (Map.Entry<String, List<String>> entry : keyListMap.entrySet()) {
                List<String> keyList = entry.getValue();
                for (String key : keyList) {
                    Map<String, Double> resultmap = new HashMap<>();
                    for (String times : timeList) {
                        if (res.get(i) == null) {
                            resultmap.put(times, 0D);
                        } else {
                            Double d = Double.parseDouble(res.get(i).toString());
                            resultmap.put(times, d);
                        }
                        i++;
                    }
                    resultList.put(entry.getKey(), resultmap);
                }
            }
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    public Long getbitcount(String key) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = this.jedisManager.getJedis();
            jds.select(DB_INDEX);

            return jds.bitcount(key);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }

    /**
     * 简单的setbit
     *
     * @param key
     * @param value
     */
    public void setbit(String key, long member, boolean value) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            boolean res = jds.setbit(key, member, value);
            if (isLog) {
                System.out.println("setbit key:" + key + "\tmember:" + member + "\tvalue" + value + "\tresult:[" + res + "]");
            }
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 简单的Get
     *
     * @param <T>
     * @param key
     * @param requiredType
     * @return
     */
    public <T> T get(String key, Class<T>... requiredType) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = this.jedisManager.getJedis();
            jds.select(0);
            byte[] skey = SerializeUtil.serialize(key);
            return SerializeUtil.deserialize(jds.get(skey), requiredType);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }

    /**
     * 简单的set
     *
     * @param key
     * @param value
     */
    public void set(Object key, Object value) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[] skey = SerializeUtil.serialize(key);
            byte[] svalue = SerializeUtil.serialize(value);
            jds.set(skey, svalue);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }


    /**
     * 判断是否存在
     *
     * @param existskey
     * @return
     */
    public boolean exists(String existskey) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[] lkey = SerializeUtil.serialize(existskey);
            return jds.exists(lkey);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return false;
    }

    /**
     * 释放
     *
     * @param jedis
     * @param isBroken
     */
    public void returnResource(Jedis jedis, boolean isBroken) {
        if (jedis == null) {
            return;
        }
//        if (isBroken) {
//            jedisManager.getJedisPool().returnBrokenResource(jedis);
//        } else {
//            jedisManager.getJedisPool().returnResource(jedis);
//        }
//        版本问题

        //注意这里不是关闭连接，在JedisPool模式下，Jedis会被归还给资源池。
        jedis.close();
    }


}
