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
    private static final int DB_INDEX = 2;
    private jedisManager jedisManager;

    private boolean isLog = true;

//键值参考：账号id 一定要唯一
/*  zscore
    充值次数:         zincrby "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_INFO" {value} "RECHARGE_TIMES"
    充值人数:         zincrby "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_INFO" {value} "RECHARGE_PLAYERS"
    充值金额:         zincrby "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_INFO" {value} "RECHARGE_AMOUNTS"
    当日首次付费金额： zincrby "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_INFO" {amounts} "RECHARGE_FIRST_AMOUNTS"
    注册付费金额：    zincrby "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_INFO" {amounts} "RECHARGE_AMOUNTS_NA_CA"
    累计充值金额：    zincrby "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}#RechargeTotalInfo" {value} "GAME_ACCUMULATION_RECHARGE_AMOUNTS"
    累计充值人数:     zincrby "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}#RechargeTotalInfo" {value} "GAME_ACCUMULATION_RECHARGE_ACCOUNTS"

    累计创角:        zincrby "UserInfo:spid:{spid}:gid:{gid}:sid:{sid}#AccountInfo" {value} "GAME_ACCUMULATION_CREATE_ROLE"

    bitcount
    新增创号                  SETBIT "UserInfo:spid:{spid}:gid:{gid}:date:{yyyyMMdd}#NEW_ADD_CREATE_ACCOUNT" {account_Id}
    新增创角                  SETBIT "UserInfo:spid:{spid}:gid:{gid}:date:{yyyyMMdd}#NA_CR {account_Id}
    所有账号的数目:            SETBIT "UserInfo:spid:{spid}:gid:{gid}#GAME_ACCOUNT_ALL_NUMS" {account_Id}
    活跃玩家 的账号数目:       SETBIT "UserInfo:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#activePlayers" {account_id}
    当日首次付费人数:         SETBIT "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_ACCOUNT" {account_id}
    当日多次付费人数:         SETBIT "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_ACCOUNT_M" {account_id}
    注册付费人数:            SETBIT "activePlayers:gid:{gid}:sid:{sid}:spid:{spid}:date:{yyyyMMdd}#RECHARGE_ACCOUNT_NA_CA" {account_id}

    创建过角色的账号          SETBIT "UserInfo:spid:{spid}:gid:{gid}#GAME_ACCOUNT_HAS_ROLE" {account_id}
    新增创角去除滚服           SETBIT "UserInfo:spid:{spid}:gid:{gid}:date:{yyyyMMdd}#NA_CR_RM_OLD {account_Id}
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
        String key1 = RedisKeyHeader.USER_INFO + ":spid:*:gid:*:sid:*:date:" + currDay + "#" + RedisKeyTail.ONLINE_PLAYERS;
        String key2 = RedisKeyHeader.REALTIMEDATA + ":spid:*:gid:*:sid:*:date:" + currDayMin + "#" + RedisKeyTail.REALTIME_ONLINE_ACCOUNTS;
        //查询的键
        String patternKey = key1;

        ScanParams scanParams = new ScanParams();
        scanParams.match(patternKey);// 匹配以 {header}:spid:*:gid:*:sid:*:date:*#{tail} 为前缀的 key
        scanParams.count(500);

        //当天的实时在线玩家数据-有序集合键值
        List<String> targetKeyList = new ArrayList<>();
        Pipeline pipeline = jedis.pipelined();

        do {
            //使用scan命令获取500条数据，使用cursor游标记录位置，下次循环使用
            ScanResult<String> scanResult = jedis.scan(cursor, scanParams);

            cursor = scanResult.getCursor();
            List<String> list = scanResult.getResult();

            long t1 = System.currentTimeMillis();

            //每分钟的在线玩家
            //亦或|或 都可以 反正 key2 此刻不存在 均为0
            for (String mapEntry : list) {
                String[] keys = mapEntry.split(":");
                StringBuilder targetbody = new StringBuilder();
                // :spid:*:gid:*:sid:*:date:
                for (int i = 1; i <= 7; i++) {
                    targetbody.append(keys[i]).append(":");
                }
                String target = RedisKeyHeader.REALTIMEDATA + ":" + targetbody + currDay + "#" + RedisKeyTail.REALTIME_ONLINE_ACCOUNTS;
                targetKeyList.add(target);
                if (isLog) {
                    System.out.println("src key------>" + mapEntry);
                    System.out.println("target key--->" + target);
                }
                pipeline.bitcount(mapEntry);
            }
            //给当前时间实际在线添加数值
            List<Object> res = pipeline.syncAndReturnAll();
            for (int i = 0; i < res.size(); i++) {
                double num = Double.parseDouble(res.get(i).toString());
                String targetKey = targetKeyList.get(i);

                pipeline.zincrby(targetKey, num, currDayMin);
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

    /**
     * 每天12点更新活跃玩家实时数据
     * todo
     */
    public void setOfflineData() {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

//            this.getListKey(jds);
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
            //当前分钟
            String currDayMin = DateUtil.getCurrentMinuteStr();

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
            //活跃账号 渠道-游戏-区服-日期
            String key1 = userSGSKey + ":date:" + currDay + "#" + RedisKeyTail.ACTIVE_PLAYERS;
            //在线账号 渠道-游戏-区服-日期
            String key2 = userSGSKey + ":date:" + currDay + "#" + RedisKeyTail.ONLINE_PLAYERS;
            //实时在线
            String key3 = realtimeSGSKey + ":date:" + currDayMin + "#" + RedisKeyTail.REALTIME_ONLINE_ACCOUNTS;

            pipeline.setbit(key1, accountId, true);
            pipeline.setbit(key2, accountId, true);
//            pipeline.expireAt(key2, DateUtil.getEndTimestamp());
            pipeline.setbit(key3, accountId, true);
//            pipeline.expire(key3, (int) ((int) DateUtil.MONTH_MILLIS / DateUtil.SECOND_MILLIS));

            List<Object> res = pipeline.syncAndReturnAll();
            if (isLog) {
                System.out.println("enterGame key1:" + key1 + "\taccountId:" + accountId + "\tresult:[" + res.get(0).toString() + "]");
                System.out.println("enterGame key2:" + key2 + "\taccountId:" + accountId + "\tresult:[" + res.get(1).toString() + "]");
            }
            log.info("enterGame key1:" + key1 + "\taccountId:" + accountId + "\tresult:[" + res.get(0).toString() + "]");
            log.info("enterGame key2:" + key2 + "\taccountId:" + accountId + "\tresult:[" + res.get(1).toString() + "]");
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * redis 管道
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
     * @param accountId 角色id
     */
    public void createRole(String appId,
                           String serverId,
                           String channelId,
                           long accountId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            //当前分钟
            String currDayMin = DateUtil.getCurrentMinuteStr();
            String currDay = DateUtil.getCurrentDayStr();

            //渠道-游戏
            String userSGKey = String.format(RedisKey.FORMAT_SG_SS, RedisKeyHeader.USER_INFO, channelId, appId);
            //渠道-游戏-区服
            String userSGSKey = String.format(RedisKey.FORMAT_SGS_SSS, RedisKeyHeader.USER_INFO, channelId, appId, serverId);
            //渠道-游戏-区服
            String realtimeSGSKey = String.format("%s:spid:%s:gid:%s:sid:%s:date:", RedisKeyHeader.REALTIMEDATA, channelId, appId, serverId);
            //渠道-游戏 有角色的账号
            String key2 = userSGKey + "#" + RedisKeyTail.GAME_ACCOUNT_HAS_ROLE;

            //是否滚服用户
            boolean isMutiple = jds.getbit(key2, accountId);
            System.out.println("isMutiple:" + isMutiple);

            //新增创角 渠道-游戏-区服-日期
            String key1 = userSGSKey + ":date:" + currDay + "#" + RedisKeyTail.NEW_ADD_CREATE_ROLE;
            //新增创角去除滚服 渠道-游戏-区服-日期
            String key3 = userSGSKey + ":date:" + currDay + "#" + RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD;
            // 累计创角 渠道-游戏-区服
            String key5 = userSGSKey + "#" + RedisKeyTail.ACCOUNT_INFO;

            String key11 = realtimeSGSKey + currDay + "#" + RedisKeyTail.REALTIME_ADD_ROLES;

            boolean res1 = jds.setbit(key1, accountId, true);
            boolean res2 = jds.setbit(key3, accountId, !isMutiple);
            double res3 = jds.zincrby(key5, 1, RedisKey.GAME_ACCUMULATION_CREATE_ROLE);
            if (!isMutiple) {
                jds.setbit(key2, accountId, true);
            }

            jds.zincrby(key11, 1, currDayMin);
            if (isLog) {
                System.out.println("createRole key1:" + key1 + "\taccountId:" + accountId + "\tresult:[" + res1 + "]");
                System.out.println("createRole key3:" + key3 + "\taccountId:" + accountId + "\tresult:[" + res2 + "]");
                System.out.println("createRole key5:" + key5 + "\taccountId:" + accountId + "\tresult:[" + res3 + "]");
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

    /**
     * redis 管道
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
     * @param appId     游戏id
     * @param serverId  区服id
     * @param channelId 渠道id
     * @param accountId 角色id
     */
    public void reqpay(String appId, String serverId, String channelId, long accountId, long payamounts) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            //当前分钟
            String currDayMin = DateUtil.getCurrentMinuteStr();
            //当天时间
            String currDay = DateUtil.getCurrentDayStr();

            String userSGKey = String.format(RedisKey.FORMAT_SG_SS, RedisKeyHeader.USER_INFO, channelId, appId);
            String activeSGSKey = String.format(RedisKey.FORMAT_SGS_SSS, RedisKeyHeader.ACTIVE_PLAYERS_INFO, channelId, appId, serverId);
            String realtimeSGSKey = String.format(RedisKey.FORMAT_SGS_SSS, RedisKeyHeader.REALTIMEDATA, channelId, appId, serverId);
            //渠道-游戏-区服-日期
            /**
             * 1.充值次数
             * 2.充值人数
             * 3.充值金额
             * 4.当日首次付费金额
             * 5.注册付费金额
             */
            String key1 = activeSGSKey + ":date:" + currDay + "#" + RedisKeyTail.RECHARGE_INFO;
            /**
             * 渠道-游戏-区服
             * 6.累计充值金额
             * 7.累计充值人数
             */
            String key2 = activeSGSKey + "#" + RedisKeyTail.RECHARGE_TOTAL_INFO;
            //付费玩家 渠道-游戏-区服-日期
            String key3 = activeSGSKey + ":date:" + currDay + "#" + RedisKeyTail.RECHARGE_ACCOUNT;
            //多次付费玩家 渠道-游戏-区服-日期
            String key31 = activeSGSKey + ":date:" + currDay + "#" + RedisKeyTail.RECHARGE_ACCOUNT_M;
            //注册付费人数
            String key9 = activeSGSKey + ":date:" + currDay + "#" + RedisKeyTail.RECHARGE_ACCOUNT_NA_CA;
            //新增创号(注册人数)
            String key10 = userSGKey + ":date:" + currDay + "#" + RedisKeyTail.NEW_ADD_CREATE_ACCOUNT;
            //当前时间段的收入
            String key11 = realtimeSGSKey + ":date:" + currDay + "#" + RedisKeyTail.REALTIME_RECHARGE_AMOUNTS;
            //所有充值玩家（累积）
            String key71 = userSGKey + "#" + RedisKeyTail.GAME_ACCOUNT_ALL_NUMS;
            //是否当日已付费
            boolean isPaid = jds.getbit(key3, accountId);
            //是否当日多次付费
            boolean isPaidMutiple = jds.getbit(key31, accountId);
            //是否当日注册玩家
            boolean isRegister = jds.getbit(key10, accountId);
            if (isLog) {
                System.out.println("key1:" + key1);
                System.out.println("key2:" + key2);
                System.out.println("key3:" + key3);
                System.out.println("key31:" + key31);
                System.out.println("key10:" + key10);
                System.out.println("key11:" + key11);

                System.out.println("isPaid:" + isPaid);
                System.out.println("isPaidMutiple:" + isPaidMutiple);
                System.out.println("isRegister:" + isRegister);
            }
            //累计充值 游戏-区服
            jds.zincrby(key2, payamounts, RedisKey.GAME_ACCUMULATION_RECHARGE_AMOUNTS);

            //充值次数
            jds.zincrby(key1, 1, RedisKey.RECHARGE_TIMES);
            //充值人数
            jds.zincrby(key1, 1, RedisKey.RECHARGE_PLAYERS);
            //充值金额
            jds.zincrby(key1, payamounts, RedisKey.RECHARGE_AMOUNTS);
            //充值金额-当前分钟
            jds.zincrby(key11, payamounts, currDayMin);
//            jds.expire(key11, (int) (DateUtil.MONTH_MILLIS / DateUtil.SECOND_MILLIS));

            if (!isPaid) {
                //当日首次付费金额
                jds.zincrby(key1, payamounts, RedisKey.RECHARGE_FIRST_AMOUNTS);
                //当日首次付费人数
                jds.setbit(key3, accountId, true);
            } else {
                //多次付费
                jds.setbit(key31, accountId, true);
            }
            //注册付费玩家
            if (isRegister) {
                //注册付费人数
                jds.setbit(key9, accountId, true);
                //注册付费金额
                jds.zincrby(key1, payamounts, RedisKey.RECHARGE_AMOUNTS_NA_CA);
            }
            //计算总付费率=付费玩家/所有玩家

//            long allAccounts = jds.bitcount(key71);
//            jds.zadd(RedisKey.RECHARGE_TOTAL_RATE, , currDay);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }


    public Double getZscore(String keyBody, String keyTail, String member) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = this.jedisManager.getJedis();
            jds.select(DB_INDEX);

            String key = keyBody + "#" + keyTail;

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
                                    Double re;
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
