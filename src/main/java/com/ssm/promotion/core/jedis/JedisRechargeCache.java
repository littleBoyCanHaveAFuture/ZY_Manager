package com.ssm.promotion.core.jedis;


import com.ssm.promotion.core.entity.RechargeSummary;
import com.ssm.promotion.core.util.DateUtil;
import com.ssm.promotion.core.util.SerializeUtil;
import org.apache.log4j.Logger;
import redis.clients.jedis.*;

import java.util.*;

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

    private boolean test = false;


    //账号id 一定要唯一
    //zscore
    //充值次数:         ZADD "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_INFO" {value} "RECHARGE_TIMES"
    //充值人数:         ZADD "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_INFO" {value} "RECHARGE_PLAYERS"
    //充值金额:         ZADD "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_INFO" {value} "RECHARGE_AMOUNTS"
    //当日首次付费金额： ZADD "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_INFO" {amounts} "RECHARGE_FIRST_AMOUNTS"
    //注册付费金额：    ZADD "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_INFO" {amounts} "RECHARGE_AMOUNTS_NA_CA"
    //累计充值金额：    ZADD "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}#RechargeTotalInfo" {value} "GAME_ACCUMULATION_RECHARGE_AMOUNTS"
    //累计充值人数:     ZADD "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}#RechargeTotalInfo" {value} "GAME_ACCUMULATION_RECHARGE_ACCOUNTS"

    //累计创角:        ZADD "UserInfo:spid:{spid}:gid:{gid}:sid:{sid}#AccountInfo" {value} "GAME_ACCUMULATION_CREATE_ROLE"

    //bitcount
    //新增创号                  SETBIT "UserInfo:spid:{spid}:gid:{gid}:date:{yyyyMMdd}#NEW_ADD_CREATE_ACCOUNT" {account_Id}
    //新增创角                  SETBIT "UserInfo:spid:{spid}:gid:{gid}:date:{yyyyMMdd}#NA_CR {account_Id}
    //所有账号的数目:            SETBIT "UserInfo:spid:{spid}:gid:{gid}#GAME_ACCOUNT_ALL_NUMS" {account_Id}
    //活跃玩家 的账号数目:       SETBIT "UserInfo:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#activePlayers" {account_id}
    //当日首次付费人数:         SETBIT "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_ACCOUNT" {account_id}
    //当日多次付费人数:         SETBIT "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RECHARGE_ACCOUNT_M" {account_id}
    //注册付费人数:            SETBIT "activePlayers:gid:{gid}:sid:{sid}:spid:{spid}:date:{yyyyMMdd}#RECHARGE_ACCOUNT_NA_CA" {account_id}

    //创建过角色的账号          SETBIT "UserInfo:spid:{spid}:gid:{gid}#GAME_ACCOUNT_HAS_ROLE" {account_id}
    //新增创角去除滚服           SETBIT "UserInfo:spid:{spid}:gid:{gid}:date:{yyyyMMdd}#NA_CR_RM_OLD {account_Id}

    public jedisManager getJedisManager() {
        return jedisManager;
    }

    public void setJedisManager(jedisManager jedisManager) {
        this.jedisManager = jedisManager;
    }

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
     * 实时数据
     */
    public void setRealtimeData() {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            this.getListKey(jds, 1);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 实时数据
     * 12点下线上线
     */
    public void setOfflineData() {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            this.getListKey(jds, 1);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 实时数据
     *
     * @param timeList yyyyMMddmm 时间
     * @return
     */
    public void getRealtimeData(String spId, Integer gameId, Integer serverId, String currday, List<String> timeList,
                                List<Integer> newadd, List<Integer> online, List<Double> money) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);
            String realtimeSGSKey = String.format("%s:spid:%s:gid:%s:sid:%s:date:",
                    RedisKeyHeader.REALTIMEDATA, spId, gameId, serverId);

            Pipeline pipeline = jds.pipelined();
            List<Object> res;
            //在线
            for (String times : timeList) {
                String key11 = realtimeSGSKey + times + "#" + RedisKeyTail.REALTIME_ONLINE_ACCOUNTS;
                System.out.println("getRealtimeData key:" + key11);
                pipeline.bitcount(key11);
            }
            res = pipeline.syncAndReturnAll();
            for (int i = 0; i < timeList.size(); i++) {
                String times = timeList.get(i);
                Object num = res.get(i);
                online.add(Integer.parseInt(num.toString()));
            }
            Map<String, Object> map = new HashMap<>();
            // 游标初始值为0
            String cursor = ScanParams.SCAN_POINTER_START;
            //收入
            String key2 = realtimeSGSKey + currday + "#" + RedisKeyTail.REALTIME_RECHARGE_AMOUNTS;
            //当前时间段的收入
            System.out.println("getRealtimeData key:" + key2);

            do {
                //使用scan命令获取500条数据，使用cursor游标记录位置，下次循环使用
                ScanResult<Tuple> scanResult = jds.zscan(key2, cursor);
                cursor = scanResult.getCursor();
                List<Tuple> list = scanResult.getResult();
                for (Tuple tuple : list) {
                    String member = tuple.getElement();
                    double score = tuple.getScore();
                    if (timeList.contains(member)) {
                        map.put(member, score);
                    }
                }
            } while (!"0".equals(cursor));
            for (String times : timeList) {
                money.add(Double.parseDouble(map.getOrDefault(times, 0D).toString()));
            }


            //新增角色
            cursor = ScanParams.SCAN_POINTER_START;
            //收入
            String key3 = realtimeSGSKey + currday + "#" + RedisKeyTail.REALTIME_ADD_Roles;
            //当前时间段的收入
            System.out.println("getRealtimeData key:" + key3);
            map.clear();
            do {
                //使用scan命令获取500条数据，使用cursor游标记录位置，下次循环使用
                ScanResult<Tuple> scanResult = jds.zscan(key3, cursor);
                cursor = scanResult.getCursor();
                List<Tuple> list = scanResult.getResult();
                for (Tuple tuple : list) {
                    String member = tuple.getElement();
                    double score = tuple.getScore();
                    if (timeList.contains(member)) {
                        map.put(member, (int) score);
                    }
                }
            } while (!"0".equals(cursor));

            for (String times : timeList) {
                newadd.add(Integer.parseInt(map.getOrDefault(times, 0).toString()));
            }

        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }

    }

    /**
     * 每分钟设置实时数据
     */
    public void getListKey(Jedis jedis, int type) throws Exception {
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
        String patternKey = null;
        if (type == 1) {
            patternKey = key1;
        }
        if (patternKey == null) {
            return;
        }
        ScanParams scanParams = new ScanParams();
        scanParams.match(patternKey);// 匹配以 {header}:spid:*:gid:*:sid:*:date:*#{tail} 为前缀的 key
        scanParams.count(500);

        Pipeline pipeline = jedis.pipelined();
        do {
            //使用scan命令获取500条数据，使用cursor游标记录位置，下次循环使用
            ScanResult<String> scanResult = jedis.scan(cursor, scanParams);

            cursor = scanResult.getCursor();
            List<String> list = scanResult.getResult();

            long t1 = System.currentTimeMillis();

            //亦或|或 都可以 反正 key2 此刻不存在 均为0
            for (String mapEntry : list) {
                System.out.println("key--->" + mapEntry);

                String[] keys = mapEntry.split(":");
                String targetbody = "";
                // :spid:*:gid:*:sid:*:date:
                for (int i = 1; i <= 7; i++) {
                    targetbody += keys[i] + ":";
                }

                String target = RedisKeyHeader.REALTIMEDATA + ":" + targetbody + currDayMin +
                        "#" + RedisKeyTail.REALTIME_ONLINE_ACCOUNTS;

                System.out.println("target key--->" + target);
                System.out.println("src key--->" + mapEntry);

                pipeline.bitop(BitOP.OR, target, mapEntry);
                pipeline.expire(target, (int) (DateUtil.MONTH_MILLIS / DateUtil.SECOND_MILLIS));
            }
            pipeline.sync();

            long t2 = System.currentTimeMillis();
            System.out.println("find " + list.size() + " key,use: " + (t2 - t1) + " ms,cursor:" + cursor);
        } while (!"0".equals(cursor));
        pipeline.close();
    }

    /**
     * redis 管道
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
                String userSGKey = String.format("%s:spid:%s:gid:%d", RedisKeyHeader.USER_INFO, spId, gameId);
                //新增创号：渠道-游戏-日期
                key1 = userSGKey + ":date:" + currday + "#" + RedisKeyTail.NEW_ADD_CREATE_ACCOUNT;
                //渠道-该游戏所有账号 渠道-游戏
                key2 = userSGKey + "#" + RedisKeyTail.GAME_ACCOUNT_ALL_NUMS;
            } else {
                //渠道-游戏
                String userOffcialKey = String.format("%s:%s:gid:%d", RedisKeyHeader.USER_INFO, RedisKeyBody.OFFICIAL, gameId);
                //新增创号-官方 官方-游戏
                key1 = userOffcialKey + ":date:" + currday + "#" + RedisKeyTail.NEW_ADD_CREATE_ACCOUNT;
                //官方-该游戏所有账号 官方-游戏
                key2 = userOffcialKey + "#" + RedisKeyTail.GAME_ACCOUNT_ALL_NUMS;
            }

            Pipeline pipeline = jds.pipelined();
            pipeline.setbit(key1, accountId, true);
            pipeline.setbit(key2, accountId, true);

            List<Object> res = pipeline.syncAndReturnAll();

            System.out.println("register key:" + key1 + "\taccountId:" + accountId + "\tresult:[" + res.get(0).toString() + "]");
            System.out.println("register key:" + key2 + "\taccountId:" + accountId + "\tresult:[" + res.get(1).toString() + "]");

            log.info("register key:" + key1 + "\taccountId:" + accountId + "\tresult:[" + res.get(0).toString() + "]");
            log.info("register key:" + key2 + "\taccountId:" + accountId + "\tresult:[" + res.get(1).toString() + "]");
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }


    /**
     * redis 管道
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

            String userSGSKey = String.format("%s:spid:%s:gid:%s:sid:%s", RedisKeyHeader.USER_INFO, channelId, appId, serverId);
            String realtimeSGSKey = String.format("%s:spid:%s:gid:%s:sid:%s", RedisKeyHeader.REALTIMEDATA, channelId, appId, serverId);

            Pipeline pipeline = jds.pipelined();
            //活跃账号 渠道-游戏-区服-日期
            String key1 = userSGSKey + ":date:" + currDay + "#" + RedisKeyTail.ACTIVE_PLAYERS;
            //在线账号 渠道-游戏-区服-日期
            String key2 = userSGSKey + ":date:" + currDay + "#" + RedisKeyTail.ONLINE_PLAYERS;
            //实时在线
            String key3 = realtimeSGSKey + ":date:" + currDayMin + "#" + RedisKeyTail.REALTIME_ONLINE_ACCOUNTS;

            pipeline.setbit(key1, accountId, true);
            pipeline.setbit(key2, accountId, true);
            pipeline.expireAt(key2, DateUtil.getEndTimestamp());
            pipeline.setbit(key3, accountId, true);
            pipeline.expire(key3, (int) ((int) DateUtil.MONTH_MILLIS / DateUtil.SECOND_MILLIS));

            List<Object> res = pipeline.syncAndReturnAll();

            System.out.println("enterGame key1:" + key1 + "\taccountId:" + accountId + "\tresult:[" + res.get(0).toString() + "]");
            System.out.println("enterGame key2:" + key2 + "\taccountId:" + accountId + "\tresult:[" + res.get(1).toString() + "]");

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
            String userSGKey = String.format("%s:spid:%s:gid:%s", RedisKeyHeader.USER_INFO, channelId, appId);
            //渠道-游戏-区服
            String userSGSKey = String.format("%s:spid:%s:gid:%s:sid:%s", RedisKeyHeader.USER_INFO, channelId, appId, serverId);

            String realtimeSGSKey = String.format("%s:spid:%s:gid:%s:sid:%s:date:", RedisKeyHeader.REALTIMEDATA, channelId, appId, serverId);
            //渠道-游戏 有角色的账号
            String key2 = userSGKey + "#" + RedisKeyTail.GAME_ACCOUNT_HAS_ROLE;
            //是否滚服用户
            boolean isMutiple = jds.getbit(key2, accountId);
            System.out.println("isMutiple:" + isMutiple);


            //新增创角 渠道-游戏-区服-日期
            String key1 = userSGSKey + ":date:" + currDay + "#" + RedisKeyTail.NEW_ADD_CREATE_ROLE;
            //新增创角去除滚服 渠道-游戏-区服-日期
            String key3 = userSGKey + ":date:" + currDay + "#" + RedisKeyTail.NEW_ADD_CREATE_ROLE_RM_OLD;
            // 累计创角 渠道-游戏-区服
            String key5 = userSGSKey + "#" + RedisKeyTail.ACCOUNT_INFO;

            String key11 = realtimeSGSKey + currDay + "#" + RedisKeyTail.REALTIME_ADD_Roles;

            boolean res1 = jds.setbit(key1, accountId, true);
            boolean res2 = jds.setbit(key3, accountId, !isMutiple);
            long res3 = jds.zadd(key5, 1, RedisKey.GAME_ACCUMULATION_CREATE_ROLE);
            if (!isMutiple) {
                jds.setbit(key2, accountId, true);
            }

            jds.zadd(key11, 1, currDayMin);

            System.out.println("createRole key1:" + key1 + "\taccountId:" + accountId + "\tresult:[" + res1 + "]");
            System.out.println("createRole key3:" + key3 + "\taccountId:" + accountId + "\tresult:[" + res2 + "]");
            System.out.println("createRole key5:" + key5 + "\taccountId:" + accountId + "\tresult:[" + res3 + "]");

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
            String userSGSKey = String.format("%s:spid:%s:gid:%s:sid:%s", RedisKeyHeader.USER_INFO, channelId, appId, serverId);
            //活跃账号 渠道-游戏-区服-日期
            String key1 = userSGSKey + ":date:" + currDay + "#" + RedisKeyTail.ACTIVE_PLAYERS;
            //在线账号 渠道-游戏-区服-日期
            String key2 = userSGSKey + ":date:" + currDay + "#" + RedisKeyTail.ONLINE_PLAYERS;


            boolean res1 = jds.getbit(key1, accountId);
            System.out.println("exit key:" + res1);

            if (!res1) {
                boolean res2 = jds.setbit(key1, accountId, true);
                System.out.println("enterGame key1:" + key1 + "\taccountId:" + accountId + "\tresult:[" + res2 + "]");
            } else {
                boolean res3 = jds.setbit(key2, accountId, false);
                jds.expireAt(key2, DateUtil.getEndTimestamp());
                System.out.println("enterGame key2:" + key2 + "\taccountId:" + accountId + "\tresult:[" + res3 + "]");
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

            String activeSGSKey = String.format("%s:spid:%s:gid:%s:sid:%s", RedisKeyHeader.ACTIVE_PLAYERS_INFO, channelId, appId, serverId);
            String realtimeSGSKey = String.format("%s:spid:%s:gid:%s:sid:%s:date:", RedisKeyHeader.REALTIMEDATA, channelId, appId, serverId);
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
            String key10 = activeSGSKey + ":date:" + currDay + "#" + RedisKeyTail.NEW_ADD_CREATE_ACCOUNT;
            //当前时间段的收入
            String key11 = realtimeSGSKey + ":date:" + currDay + "#" + RedisKeyTail.REALTIME_RECHARGE_AMOUNTS;

            //是否当日已付费
            boolean isPaid = jds.getbit(key3, accountId);
            //是否当日多次付费
            boolean isPaidMutiple = jds.getbit(key31, accountId);
            //是否当日注册玩家
            boolean isRegister = jds.getbit(key10, accountId);

            System.out.println("isPaid:" + isPaid);
            System.out.println("isPaidMutiple:" + isPaidMutiple);
            System.out.println("isRegister:" + isRegister);

            jds.zadd(key2, payamounts, RedisKey.GAME_ACCUMULATION_RECHARGE_AMOUNTS);
            jds.zadd(key2, 1, RedisKey.GAME_ACCUMULATION_RECHARGE_ACCOUNTS);

            jds.zadd(key1, 1, RedisKey.RECHARGE_TIMES);
            jds.zadd(key1, 1, RedisKey.RECHARGE_PLAYERS);
            jds.zadd(key1, payamounts, RedisKey.RECHARGE_AMOUNTS);
            jds.zadd(key1, payamounts, RedisKey.RECHARGE_AMOUNTS_NA_CA);
            jds.zadd(key11, payamounts, currDayMin);
            jds.expire(key11, (int) (DateUtil.MONTH_MILLIS / DateUtil.SECOND_MILLIS));
            if (!isPaid) {
                jds.zadd(key1, 1, RedisKey.RECHARGE_FIRST_AMOUNTS);
                jds.setbit(key3, accountId, true);
            } else {
                //多次付费
                jds.setbit(key31, accountId, true);
            }
            //注册付费玩家
            if (isRegister) {
                jds.setbit(key9, accountId, true);
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
     * 查询每一天的分数
     *
     * @param timeList 时间列表-yyyyMMdd
     * @return 返回值 map(yyyyMMdd, score)
     */
    public Map<String, Double> getDayZScore(String keyBody, String keyTail, String member, List<String> timeList) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            Pipeline pipeline = jds.pipelined();
            if (test) {
                for (String times : timeList) {
                    String key = RedisKeyBody.appendBodyTimes(keyBody, times) + keyTail;
                    pipeline.zadd(key, 0, member);
                }
                pipeline.sync();
            }

            //时间遍历
            for (String times : timeList) {
                String key = RedisKeyBody.appendBodyTimes(keyBody, times) + keyTail;
                System.out.println("getDayZScore key:member\t" + key + "  " + member);
                //不存在返回 null
                pipeline.zscore(key, member);
            }

            List<Object> res = pipeline.syncAndReturnAll();

            Map<String, Double> map = new LinkedHashMap<>();
            for (int i = 0; i < timeList.size(); i++) {
                String times = timeList.get(i);
                Object num = res.get(i);
                if (num != null) {
                    Double nums = Double.parseDouble(num.toString());
                    map.put(times, nums);
                }
            }
            return map;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }

    public Double getZscore(String keyBody, String keyTail, String member) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = this.jedisManager.getJedis();
            jds.select(DB_INDEX);

            String key = RedisKeyBody.appendBodyTail(keyBody, keyTail);

            System.out.println("getZscore key:member\t" + key + " " + member);

            if (test) {
                jds.zadd(key, 0D, member);
            }

            //            byte[] skey = SerializeUtil.serialize(key);
            //            byte[] smember = SerializeUtil.serialize(member);


            return jds.zscore(key, member);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }

    /**
     * redis 管道
     * 获取 bitmap 数目
     * 格式--->   {type}:spid:{spid}:gid:{gid}:sid:{sid}:date:{times}#{keyTail}
     *
     * @param keyBody
     * @param keyTail
     * @param timeList 时间列表
     */
    public Map<String, Double> getDayBitmapCount(String keyBody, String keyTail, List<String> timeList) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            Pipeline pipeline = jds.pipelined();

            if (test) {
                List<String> tmpTimeList = new ArrayList<>();
                for (String times : timeList) {
                    String key = keyBody + ":date:" + times + "#" + keyTail;
                    pipeline.exists(key);
                }
                List<Object> res = pipeline.syncAndReturnAll();
                for (int i = 0; i < timeList.size(); i++) {
                    String times = timeList.get(i);
                    String nums = res.get(i).toString();
                    if (nums.equals("0")) {
                        tmpTimeList.add(times);
                    }
                }
//                pipeline.clear();
                for (String times : tmpTimeList) {
                    String key = keyBody + ":date:" + times + "#" + keyTail;
                    pipeline.setbit(key, 10L, true);
                }

                pipeline.sync();
//                pipeline.clear();
            }


            //时间遍历
            for (String times : timeList) {
                String key = keyBody + ":date:" + times + "#" + keyTail;

                System.out.println("bitcount:\t" + key);

                pipeline.bitcount(key);
            }

            List<Object> res = pipeline.syncAndReturnAll();

            Map<String, Double> map = new LinkedHashMap<>();
            for (int i = 0; i < timeList.size(); i++) {
                String times = timeList.get(i);
                Double nums = Double.parseDouble(res.get(i).toString());
                map.put(times, nums);
            }
            return map;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }

    /**
     * redis 管道
     * 获取 bitset
     * 可能为空
     */
    public BitSet getBitSet(String keyBody, String keyTail) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            BitSet all = new BitSet();
            String key = RedisKeyBody.appendBodyTail(keyBody, keyTail);
            byte[] skey = SerializeUtil.serialize(key);

            System.out.println("getBitSet key\t" + key);

            if (test) {
                if (!jds.exists(skey)) {
                    jds.setbit(skey, 10L, true);
                }
            }

            byte[] value = jds.get(skey);

            if (value != null && value.length > 0) {
                BitSet users = BitSet.valueOf(value);
                all.or(users);
                return all;
            }
            return null;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }

    /**
     * redis 管道
     * 获取 bitset
     */
    public Long getBitSetCount(String keyBody, String keyTail) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            BitSet all = new BitSet();

            String key = RedisKeyBody.appendBodyTail(keyBody, keyTail);
            byte[] skey = SerializeUtil.serialize(key);

            System.out.println("getBitSetCount key\t" + key);

            if (test) {
                if (!jds.exists(key)) {
                    jds.setbit(key, 10L, true);
                }
            }

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
     * redis 管道
     * 获取
     * 查询 2个 bitmap 相同 数据的数量
     */
    public Map<String, Integer> getDayBitopAnd(String destKey, String srcKey1, String srcKey2,
                                               String destBodyTail,
                                               String src1BodyTail, String src2BodyTail,
                                               List<String> timeList) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);
            Pipeline pipeline = jds.pipelined();

            if (test) {
                List<String> tmpTimeList1 = new ArrayList<>();
                List<String> tmpTimeList2 = new ArrayList<>();
                for (String times : timeList) {
                    String destkey = destKey + ":date:" + times + "#" + destBodyTail;
                    String srckey1 = srcKey1 + ":date:" + times + "#" + src1BodyTail;
                    String srckey2 = srcKey2 + ":date:" + times + "#" + src2BodyTail;
                    pipeline.exists(srckey1);
                    pipeline.exists(srckey2);
                }
                List<Object> res = pipeline.syncAndReturnAll();

                int tag = 0;
                for (int i = 0; i < timeList.size(); i++) {
                    String times = timeList.get(i);
                    String nums1 = res.get(tag).toString();
                    String nums2 = res.get(tag + 1).toString();
                    tag += 2;
                    if (nums1.equals("0")) {
                        tmpTimeList1.add(times);
                    }
                    if (nums2.equals("0")) {
                        tmpTimeList2.add(times);
                    }
                }
//                pipeline.clear();

                for (String times : tmpTimeList1) {
                    String srckey1 = srcKey1 + "date:" + times + "#" + src1BodyTail;
                    pipeline.setbit(srckey1, 10L, true);
                }
                for (String times : tmpTimeList2) {
                    String srckey2 = srcKey2 + "date:" + times + "#" + src2BodyTail;
                    pipeline.setbit(srckey2, 10L, true);
                }
                pipeline.sync();
//                pipeline.clear();
            }

            for (String times : timeList) {
                String destkey = destKey + "date:" + times + "#" + destBodyTail;
                String srckey1 = srcKey1 + "date:" + times + "#" + src1BodyTail;
                String srckey2 = srcKey2 + "date:" + times + "#" + src2BodyTail;

                System.out.println("getDayBitopAnd destkey\t" + destkey);
                System.out.println("getDayBitopAnd srckey1\t" + srckey1);
                System.out.println("getDayBitopAnd srckey2\t" + srckey2);

                //每天的 新增创角去除滚服 账号数目
                pipeline.bitop(BitOP.AND, destkey, srckey1, srckey2);
                pipeline.bitcount(destkey);
            }

            List<Object> res = pipeline.syncAndReturnAll();
            Map<String, Integer> map = new LinkedHashMap<>();

            int resSize = res.size();
            int tag = resSize > 0 ? 1 : 0;
            if (tag == 0) {
                return null;
            }
            for (int i = 0; i < timeList.size(); i++) {
                String times = timeList.get(i);
                Integer nums = Integer.parseInt(res.get(tag).toString());
                map.put(times, nums);
                tag += 2;
                System.out.println("getDayBitopAnd key:value\t" + times + ":" + nums);
            }
            return map;
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
            System.out.println("setbit key:" + key + "\tmember:" + member + "\tvalue" + value + "\tresult:[" + res + "]");
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * sorted set
     * 添加一个新的键值对
     *
     * @param key    键值
     * @param score  分数
     * @param member 成员键值
     */
    public void zadd(Object key, double score, Object member) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);
            byte[] skey = SerializeUtil.serialize(key);
            byte[] svalue = SerializeUtil.serialize(member);
            jds.zadd(skey, score, svalue);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * sorted set
     * 获取键值对的分数
     *
     * @param key    键值
     * @param member 成员键值
     * @return
     */
    public Double zscore(String key, String member) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = this.jedisManager.getJedis();
            jds.select(DB_INDEX);
            byte[] skey = SerializeUtil.serialize(key);
            byte[] smember = SerializeUtil.serialize(member);
            return jds.zscore(skey, smember);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
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
     * 过期时间的
     *
     * @param key
     * @param value
     * @param timer （秒）
     */
    public void setex(Object key, Object value, int timer) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[] skey = SerializeUtil.serialize(key);
            byte[] svalue = SerializeUtil.serialize(value);
            jds.setex(skey, timer, svalue);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }

    }

    /**
     * @param <T>
     * @param mapkey       map
     * @param key          map里的key
     * @param requiredType value的泛型类型
     * @return
     */
    public <T> T getVByMap(String mapkey, String key, Class<T> requiredType) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[] mkey = SerializeUtil.serialize(mapkey);
            byte[] skey = SerializeUtil.serialize(key);
            List<byte[]> result = jds.hmget(mkey, skey);
            if (null != result && result.size() > 0) {
                byte[] x = result.get(0);
                T resultObj = SerializeUtil.deserialize(x, requiredType);
                return resultObj;
            }

        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }

    /**
     * @param mapkey map
     * @param key    map里的key
     * @param value  map里的value
     */
    public void setVByMap(String mapkey, String key, Object value) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[] mkey = SerializeUtil.serialize(mapkey);
            byte[] skey = SerializeUtil.serialize(key);
            byte[] svalue = SerializeUtil.serialize(value);
            jds.hset(mkey, skey, svalue);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }

    }

    /**
     * 删除Map里的值
     *
     * @param mapKey
     * @param dkey
     * @return
     */
    public Object delByMapKey(String mapKey, String... dkey) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[][] dx = new byte[dkey.length][];
            for (int i = 0; i < dkey.length; i++) {
                dx[i] = SerializeUtil.serialize(dkey[i]);
            }
            byte[] mkey = SerializeUtil.serialize(mapKey);
            Long result = jds.hdel(mkey, dx);
            return result;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return new Long(0);
    }

    /**
     * 往redis里取set整个集合
     */
    public <T> Set<T> getVByList(String setKey, Class<T> requiredType) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[] lkey = SerializeUtil.serialize(setKey);
            Set<T> set = new TreeSet<T>();
            Set<byte[]> xx = jds.smembers(lkey);
            for (byte[] bs : xx) {
                T t = SerializeUtil.deserialize(bs, requiredType);
                set.add(t);
            }
            return set;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }

    /**
     * 获取Set长度
     *
     * @param setKey
     * @return
     */
    public Long getLenBySet(String setKey) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            Long result = jds.scard(setKey);
            return result;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }

    /**
     * 删除Set
     *
     * @param dkey
     * @return
     */
    public Long delSetByKey(String key, String... dkey) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            Long result = 0L;
            if (null == dkey) {
                result = jds.srem(key);
            } else {
                result = jds.del(key);
            }
            return result;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return new Long(0);
    }

    /**
     * 随机 Set 中的一个值
     *
     * @param key
     * @return
     */
    public String srandmember(String key) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            String result = jds.srandmember(key);
            return result;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }

    /**
     * 往redis里存Set
     *
     * @param setKey
     * @param value
     */
    public void setVBySet(String setKey, String value) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            jds.sadd(setKey, value);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 取set
     *
     * @param key
     * @return
     */
    public Set<String> getSetByKey(String key) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            Set<String> result = jds.smembers(key);
            return result;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;

    }

    /**
     * 往redis里存List
     *
     * @param listKey
     * @param value
     */
    public void setVByList(String listKey, Object value) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[] lkey = SerializeUtil.serialize(listKey);
            byte[] svalue = SerializeUtil.serialize(value);
            jds.rpush(lkey, svalue);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 往redis里取list
     *
     * @param <T>
     * @param listKey
     * @param start
     * @param end
     * @param requiredType
     * @return
     */
    public <T> List<T> getVByList(String listKey, int start, int end, Class<T> requiredType) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[] lkey = SerializeUtil.serialize(listKey);
            List<T> list = new ArrayList<T>();
            List<byte[]> xx = jds.lrange(lkey, start, end);
            for (byte[] bs : xx) {
                T t = SerializeUtil.deserialize(bs, requiredType);
                list.add(t);
            }
            return list;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }

    /**
     * 获取list长度
     *
     * @param listKey
     * @return
     */
    public Long getLenByList(String listKey) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[] lkey = SerializeUtil.serialize(listKey);
            Long result = jds.llen(lkey);
            return result;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }

    /**
     * 删除
     *
     * @param dkey
     * @return
     */
    public Long delByKey(String... dkey) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(0);
            byte[][] dx = new byte[dkey.length][];
            for (int i = 0; i < dkey.length; i++) {
                dx[i] = SerializeUtil.serialize(dkey[i]);
            }
            Long result = jds.del(dx);
            return result;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return new Long(0);
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
//        if (isBroken)
//            jedisManager.getJedisPool().returnBrokenResource(jedis);
//        else
//        	jedisManager.getJedisPool().returnResource(jedis);
//        版本问题

        //注意这里不是关闭连接，在JedisPool模式下，Jedis会被归还给资源池。
        jedis.close();
    }
}
