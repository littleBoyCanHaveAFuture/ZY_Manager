package com.ssm.promotion.core.jedis;


import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.entity.RechargeSummary;
import com.ssm.promotion.core.util.DateUtil;
import com.ssm.promotion.core.util.RandomUtil;
import com.ssm.promotion.core.util.SerializeUtil;
import org.apache.log4j.Logger;
import redis.clients.jedis.*;

import java.util.*;
import java.util.stream.Collectors;

public class jedisRechargeCache {
    private static final Logger log = Logger.getLogger(jedisRechargeCache.class);
    /**
     * 为了不和其他的缓存混淆，采用追加前缀方式以作区分
     */
    private static final String REDIS_RECHARGE_CACHE = "recharge-cache:";
    /**
     * Redis 分片(分区)，也可以在配置文件中配置
     */
    private static final int DB_INDEX = 5;
    private jedisManager jedisManager;

    private boolean isLog = false;

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
        list.forEach(System.out::println);
    }

    public jedisManager getJedisManager() {
        return jedisManager;
    }

    /**
     * bean
     */
    public void setJedisManager(jedisManager jedismanager) {
        this.jedisManager = jedismanager;
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
            jds.select(DB_INDEX);
            return jds.exists(existskey);
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

    public void setChannelLoginToken(String gameId, String channelId, String channelUid, String token) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = this.jedisManager.getJedis();
            jds.select(DB_INDEX);
            String key = RedisKey_Gen.get_ChannelLoginToken(gameId, channelId, channelUid);
            jds.set(key, token);
            jds.expire(key, 60 * 10);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    public String getChannelLoginToken(String gameId, String channelId, String channelUid) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = this.jedisManager.getJedis();
            jds.select(DB_INDEX);
            String key = RedisKey_Gen.get_ChannelLoginToken(gameId, channelId, channelUid);
            return jds.get(key);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return "";
    }

    public String getString(String key) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = this.jedisManager.getJedis();
            jds.select(DB_INDEX);

            return jds.get(key);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }

    public void setString(String key, String value) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);
            jds.set(key, value);
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
            jds.select(DB_INDEX);
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
            jds.select(DB_INDEX);
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

    public Double getZScore(String key, String member) {
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
        String key1 = RedisKey_Header.USER_INFO + ":gid:*:spid:*:sid:*#" + RedisKey_Tail.ONLINE_PLAYERS;
        String key2 = RedisKey_Header.REALTIMEDATA + ":gid:*:spid:*:sid:*:date:" + currDayMin + "#" + RedisKey_Tail.REALTIME_ONLINE_ACCOUNTS;
        //查询的键
        String patternKey = key1;

        ScanParams scanParams = new ScanParams();
        // 匹配以 {header}:gid:*:spid:*:sid:*:date:*#{tail} 为前缀的 key
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
                // :gid:*:spid:*:sid:*#
                for (int i = 1; i <= 5; i++) {
                    targetbody.append(keys[i]).append(":");
                }
                targetbody.append(keys[6].split("#")[0]).append(":");
                String target = RedisKey_Header.REALTIMEDATA + ":" + targetbody + "date:" + currDay + "#" + RedisKey_Tail.REALTIME_ONLINE_ACCOUNTS;
                targetKeyList.add(target);
                if (isLog) {
                    log.info("src key------>" + mapEntry);
                    log.info("target key--->" + target);
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
                    log.info("target key:" + targetKey + "\tmember:" + currDayMin + "\t" + num);
                }
            }
            targetKeyList.clear();
            pipeline.sync();

            long t2 = System.currentTimeMillis();
            if (isLog) {
                log.info("find " + list.size() + " key,use: " + (t2 - t1) + " ms,cursor:" + cursor);
            }
        } while (!"0".equals(cursor));


        pipeline.close();
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
    public void zScan(Map<String, Object> map, String key, List<String> timeList, String cursor, Jedis jds, boolean isInt) {
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
     * @param currDay  查询的日期-yyyyMMdd
     * @param timeList 查询的日期-具体到小时分钟-yyyyMMddHHmm 时间
     *                 与上面是同一天
     * @return
     */
    public void getRealtimeData(Integer type,
                                String spId, Integer gameId, Integer serverId,
                                String currDay, List<String> timeList,
                                List<Integer> resultInt) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            // 游标初始值为0
            String cursor = ScanParams.SCAN_POINTER_START;
            Map<String, Object> map = new HashMap<>();
            String key;
            switch (type) {
                case 1:
                    //在线
                    key = RedisKey_Gen.get_RolesOnline_Min(spId, String.valueOf(gameId), String.valueOf(serverId), currDay);
                    break;
                case 2:
                    //收入
                    key = RedisKey_Gen.get_RolesPaid_Min(spId, String.valueOf(gameId), String.valueOf(serverId), currDay);
                    break;
                case 3:
                    //新增角色
                    key = RedisKey_Gen.get_RolesCreate_Min(spId, String.valueOf(gameId), String.valueOf(serverId), currDay);
                    break;
                default:
                    return;
            }
            this.zScan(map, key, timeList, cursor, jds, true);
            for (String times : timeList) {
                resultInt.add(Integer.parseInt(map.getOrDefault(times, 0).toString()));
            }
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 查询同游戏同渠道汇总在线
     */
    public void getRealtimeData(Integer type,
                                String spId, Integer gameId,
                                String currDay, List<String> timeList,
                                List<Integer> resultInt) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            // 游标初始值为0
            String cursor = ScanParams.SCAN_POINTER_START;
            Map<String, Object> map = new HashMap<>();
            String key;
            switch (type) {
                case 1:
                    //在线
                    key = RedisKey_Gen.get_RolesOnline_Min_Channel(spId, String.valueOf(gameId), currDay);
                    break;
                case 2:
                    //收入
                    key = RedisKey_Gen.get_RolesPaid_Min_Channel(spId, String.valueOf(gameId), currDay);
                    break;
                case 3:
                    //新增角色
                    key = RedisKey_Gen.get_RolesCreate_Min_Channel(spId, String.valueOf(gameId), currDay);
                    break;
                default:
                    return;
            }
            this.zScan(map, key, timeList, cursor, jds, true);
            for (String times : timeList) {
                resultInt.add(Integer.parseInt(map.getOrDefault(times, 0).toString()));
            }
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 渠道用户登录 设置token 有效期 2小时
     *
     * @param appId      游戏id
     * @param channelId  渠道id
     * @param ChannelUid 渠道uid
     */
    public String saveToken(String appId,
                            Integer channelId,
                            String ChannelUid) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);
            long timestamp = System.currentTimeMillis();

            String tokenKey = RedisKey_Gen.get_LoginToken(appId, String.valueOf(channelId));
            String value = RandomUtil.rndSecertKey() + "#" + timestamp;
            jds.hset(tokenKey, ChannelUid, value);
            return value;
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return "";
    }

    /**
     * 渠道用户登录 设置token 有效期 2小时
     *
     * @param appId      游戏id
     * @param channelId  渠道id
     * @param ChannelUid 渠道uid
     */
    public String getToken(String appId,
                           Integer channelId,
                           String ChannelUid) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String tokenKey = RedisKey_Gen.get_LoginToken(appId, String.valueOf(channelId));
            return jds.hget(tokenKey, ChannelUid);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return "";
    }

    /**
     * 获取游戏信息
     */
    public Set<String> getGAMEIDInfo() {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String key = RedisKey_Gen.get_GameInfo();
            return jds.smembers(key);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }

    /**
     * 设置游戏信息
     */
    public void setGAMEIDInfo(String appId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String key = RedisKey_Gen.get_GameInfo();
            jds.sadd(key, appId);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 删除游戏渠道信息
     */
    public void delGAMEIDInfo(String appId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String key = RedisKey_Tail.GAMEINFO;
            jds.srem(key, appId);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 设置游戏渠道信息
     */
    public void setSPIDInfo(String appId,
                            String channelId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String key = RedisKey_Gen.get_ChannelInfo(appId);
            jds.sadd(key, channelId);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 获取游戏的渠道id
     */
    public Set<String> getSPIDInfo(String appId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String key = RedisKey_Gen.get_ChannelInfo(appId);
            return jds.smembers(key);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }

    /**
     * 删除游戏渠道信息
     */
    public void delSPIDInfo(String appId,
                            String channelId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String key = RedisKey_Gen.get_ChannelInfo(appId);
            jds.srem(key, channelId);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 设置游戏渠道区服信息
     */
    public void setServerInfo(String appId,
                              String channelId,
                              String serverId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String key = RedisKey_Gen.get_ServerInfo(appId, channelId);
            jds.sadd(key, serverId);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 获取游戏渠道区服信息
     */
    public Set<String> getServerInfo(String appId,
                                     String channelId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String key = RedisKey_Gen.get_ServerInfo(appId, channelId);
            return jds.smembers(key);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
        return null;
    }
    /**SDK功能*/

    /**
     * 注册账号
     * 1.新增创号
     * 2.该游戏所有账号
     */
    public void register(boolean auto, int gameId, long accountId, int channelId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String currDay = DateUtil.getCurrentDayStr();

            //该渠道今日新建的账号id
            String key1 = RedisKey_Gen.get_AccountCreate_Day(String.valueOf(channelId), String.valueOf(gameId), currDay);
            //该渠道累积创建的账号id
            String key2 = RedisKey_Gen.get_AccountAll(String.valueOf(channelId), String.valueOf(gameId));

            Pipeline pipeline = jds.pipelined();
            pipeline.setbit(key1, accountId, true);
            pipeline.setbit(key2, accountId, true);

            pipeline.sync();

            if (isLog) {
                log.info("setbit " + key1 + "\t" + accountId + "\t" + true);
                log.info("setbit " + key2 + "\t" + accountId + "\t" + true);
            }
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
     * @param roleId    角色id
     */
    public void enterGame(String appId,
                          String serverId,
                          String channelId,
                          long roleId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String currDay = DateUtil.getCurrentDayStr();
            String currDayMin = DateUtil.getCurrentMinuteStr();

            Pipeline pipeline = jds.pipelined();

            //活跃玩家-精确到游戏
            pipeline.sadd(RedisKey_Gen.get_RolesActive_Day_Game(appId, currDay), String.valueOf(roleId));
            //活跃玩家-精确到游戏渠道
            pipeline.sadd(RedisKey_Gen.get_RolesActive_Day_Channel(appId, channelId, currDay), String.valueOf(roleId));
            //活跃玩家-精确到游戏区服
            pipeline.sadd(RedisKey_Gen.get_RolesActive_Day_Server(appId, serverId, currDay), String.valueOf(roleId));

            //在线玩家
            pipeline.zincrby(RedisKey_Gen.get_RolesOnline_Day(channelId, appId, serverId), 1, currDay);
            //实时在线
            pipeline.zincrby(RedisKey_Gen.get_RolesOnline_Min(channelId, appId, serverId, currDay), 1, currDayMin);
            pipeline.zincrby(RedisKey_Gen.get_RolesOnline_Min_Channel(channelId, appId, currDay), 1, currDayMin);


            pipeline.sync();
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 退出游戏
     * 1.在线玩家
     *
     * @param appId     游戏id
     * @param channelId 渠道id
     * @param serverId  区服id
     * @param roleId    角色id
     */
    public void exitGame(String appId,
                         String channelId,
                         String serverId,
                         long roleId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);
            String currDay = DateUtil.getCurrentDayStr();
            String currDayMin = DateUtil.getCurrentMinuteStr();

            Pipeline pipeline = jds.pipelined();
            //在线玩家
            pipeline.zincrby(RedisKey_Gen.get_RolesOnline_Day(channelId, appId, serverId), -1, currDay);
            //实时在线
            pipeline.zincrby(RedisKey_Gen.get_RolesOnline_Min(channelId, appId, serverId, currDay), -1, currDayMin);
            pipeline.zincrby(RedisKey_Gen.get_RolesOnline_Min_Channel(channelId, appId, currDay), -1, currDayMin);

            pipeline.sync();

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
                           long accountId, String roleId,
                           boolean exist) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            //当前分钟
            String currDayMin = DateUtil.getCurrentMinuteStr();
            String currDay = DateUtil.getCurrentDayStr();

            //是否滚服用户
            String key = RedisKey_Gen.get_Account_CreateRoles(channelId, appId);

            boolean hasRole = jds.sismember(key, String.valueOf(accountId));
            //今天累积创角关键字 是否存在
            boolean hasNoACCRCurrDay = jds.zrank(RedisKey_Gen.get_RolesCreate_Server_Day(channelId, appId, serverId), currDay) == null;
            //至今为止累计创角数目
            Double acc_CreateRoles = jds.zscore(RedisKey_Gen.get_RolesCreate_Server(channelId, appId, serverId), RedisKey_Member.GAME_ACCUMULATION_CREATE_ROLE);

            Pipeline pipeline = jds.pipelined();
            //新版
            //新增创角
            pipeline.zincrby(RedisKey_Gen.get_RolesCreate_Day(channelId, appId, serverId), 1, currDay);
            if (!hasRole) {
                //新增创角去除滚服
                pipeline.zincrby(RedisKey_Gen.get_RolesCreate_First(channelId, appId, serverId), 1, currDay);
                //创建过角色的账号
                pipeline.sadd(RedisKey_Gen.get_Account_CreateRoles(channelId, appId), String.valueOf(accountId));
            }
            //实时创角
            pipeline.zincrby(RedisKey_Gen.get_RolesCreate_Min(channelId, appId, serverId, currDay), 1, currDayMin);
            pipeline.zincrby(RedisKey_Gen.get_RolesCreate_Min_Channel(channelId, appId, currDay), 1, currDayMin);
            //累计创角数目(所有角色)
            pipeline.zincrby(RedisKey_Gen.get_RolesCreate_Server(channelId, appId, serverId), 1, RedisKey_Member.GAME_ACCUMULATION_CREATE_ROLE);
            //单日累计创角
            if (hasNoACCRCurrDay) {
                pipeline.zadd(RedisKey_Gen.get_RolesCreate_Server_Day(channelId, appId, serverId), (acc_CreateRoles == null ? 0 : acc_CreateRoles) + 1, currDay);
            } else {
                pipeline.zincrby(RedisKey_Gen.get_RolesCreate_Server_Day(channelId, appId, serverId), 1, currDay);
            }
            //玩家首次进入游戏-服务器新增账号
            if (!exist) {
                pipeline.setbit(RedisKey_Gen.get_AccountCreate_Server_Day(appId, serverId, currDay), accountId, true);
            }
            pipeline.sync();

        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 充值
     * 支付成功回调才调用
     * <p>
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
    public void reqPay(String appId, String serverId, String channelId, long accountId, String roleId, long payamounts,
                       String createTime) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            //当天时间
            String currDay = DateUtil.getCurrentDayStr();
            //当前分钟
            String currDayMin = DateUtil.getCurrentMinuteStr();
            //账号创建时间 + 一天
            long currentTimeMillis = System.currentTimeMillis();
            //当天凌晨 24:00的时间戳
            long dayEndTimeMillis = DateUtil.getEndTimestamp() * 1000;
            //账号创建时间戳
            long createTimeMillis = DateUtil.formatString(createTime, DateUtil.FORMAT_YYYY_MMDD_HHmmSS).getTime();

            //前提条件-今日充值过的账号
            boolean hasAccountPaidCurrDay = jds.sismember(RedisKey_Gen.get_AccountPaid_Day(channelId, appId, serverId, currDay), String.valueOf(accountId));
            //前提条件-今日充值过的角色
            boolean hasRolePaidCurrDay = jds.sismember(RedisKey_Gen.get_RolesPaid_Day(channelId, appId, serverId, currDay), roleId);
            //历前提条件-史付费
            boolean hasPaid = jds.sismember(RedisKey_Gen.get_RolesPaid_Server(channelId, appId, serverId), roleId);
            //前提条件-今天累积充值金额关键字 是否存在
            boolean hasNoACPayCurrDay = jds.zrank(RedisKey_Gen.get_RolesPayInfo_Server_Day(channelId, appId, serverId), currDay) == null;
            //前提条件-今天累积充值次数关键字 是否存在
            boolean hasNoACPayTimesCurrDay = jds.zrank(RedisKey_Gen.get_RolesPayTimes_Server_Day(channelId, appId, serverId), currDay) == null;

            //前提条件-当日新增创号的角色
            boolean hasRegCurrDay = (createTimeMillis < dayEndTimeMillis) && (createTimeMillis > dayEndTimeMillis - DateUtil.DAY_MILLIS);
            //前提条件-创号超过一天的角色 首次付费
            boolean hasRegOneMoreDay = (currentTimeMillis - createTimeMillis >= DateUtil.DAY_MILLIS);

            //前提条件-今日首次充值会进入此分支 此时充值金额为到今天凌晨为止 也就是开服-到昨天的累计充值金额
            Double acc_mounts = jds.zscore(RedisKey_Gen.get_RolesPayInfo_Server(channelId, appId, serverId), RedisKey_Member.GAME_ACCUMULATION_RECHARGE_AMOUNTS);
            //前提条件-今日首次充值会进入此分支 此时充值金额为到今天凌晨为止 也就是开服-到昨天的累计充值次数
            Double acc_times = jds.zscore(RedisKey_Gen.get_RolesPayInfo_Server(channelId, appId, serverId), RedisKey_Member.GAME_ACCUMULATION_RECHARGE_TIMES);

            Pipeline pipeline = jds.pipelined();

            //当天充值-充值次数
            pipeline.zincrby(RedisKey_Gen.get_RolesPayInfo_Day(channelId, appId, serverId, currDay), 1, RedisKey_Member.RECHARGE_TIMES);
            //当天充值-充值金额
            pipeline.zincrby(RedisKey_Gen.get_RolesPayInfo_Day(channelId, appId, serverId, currDay), payamounts, RedisKey_Member.RECHARGE_AMOUNTS);
            if (!hasRolePaidCurrDay) {
                //充值人数
                pipeline.zincrby(RedisKey_Gen.get_RolesPayInfo_Day(channelId, appId, serverId, currDay), 1, RedisKey_Member.RECHARGE_PLAYERS);
                //今日充值过的角色
                pipeline.sadd(RedisKey_Gen.get_RolesPaid_Day(channelId, appId, serverId, currDay), roleId);
            }
            //当天充值-付费账号
            if (!hasAccountPaidCurrDay) {
                pipeline.sadd(RedisKey_Gen.get_AccountPaid_Day(channelId, appId, serverId, currDay), String.valueOf(accountId));
            }

            //实时充值-充值金额-当前分钟
            pipeline.zincrby(RedisKey_Gen.get_RolesPaid_Min(channelId, appId, serverId, currDay), payamounts, currDayMin);
            pipeline.zincrby(RedisKey_Gen.get_RolesPaid_Min_Channel(channelId, appId, currDay), payamounts, currDayMin);

            //累计充值-当日累积充值金额
            if (hasNoACPayCurrDay) {
                pipeline.zadd(RedisKey_Gen.get_RolesPayInfo_Server_Day(channelId, appId, serverId), (acc_mounts == null ? 0 : acc_mounts) + payamounts, currDay);
            } else {
                pipeline.zincrby(RedisKey_Gen.get_RolesPayInfo_Server_Day(channelId, appId, serverId), payamounts, currDay);
            }
            //当日累积充值次数-必须是不同角色
            if (hasNoACPayTimesCurrDay) {
                if (!hasPaid) {
                    pipeline.zadd(RedisKey_Gen.get_RolesPayTimes_Server_Day(channelId, appId, serverId), (acc_times == null ? 0 : acc_times) + 1, currDay);
                }
            } else {
                pipeline.zincrby(RedisKey_Gen.get_RolesPayTimes_Server_Day(channelId, appId, serverId), acc_times, currDay);
            }

            //开服累积至今-充值金额
            pipeline.zincrby(RedisKey_Gen.get_RolesPayInfo_Server(channelId, appId, serverId), payamounts, RedisKey_Member.GAME_ACCUMULATION_RECHARGE_AMOUNTS);
            if (!hasPaid) {
                //充值人数 也阔以通过计算下面的size 获得
                pipeline.zincrby(RedisKey_Gen.get_RolesPayInfo_Server(channelId, appId, serverId), 1, RedisKey_Member.GAME_ACCUMULATION_RECHARGE_ACCOUNTS);
                //历史付费角色
                pipeline.sadd(RedisKey_Gen.get_RolesPaid_Server(channelId, appId, serverId), roleId);
            }
            //充值次数
            pipeline.zincrby(RedisKey_Gen.get_RolesPayInfo_Server(channelId, appId, serverId), 1, RedisKey_Member.GAME_ACCUMULATION_RECHARGE_TIMES);

            //当日注册付费
            if (hasRegCurrDay) {
                if (!hasAccountPaidCurrDay) {
                    //注册付费人数
                    pipeline.zincrby(RedisKey_Gen.get_RegisterPaid_Accounts(channelId, appId, serverId), 1, currDay);
                }
                //注册付费金额
                pipeline.zincrby(RedisKey_Gen.get_RegisterPaid_Amounts(channelId, appId, serverId), payamounts, currDay);
            }

            //当日首次付费
            if (hasRegOneMoreDay && !hasPaid) {
                //当日首次付费人数
                pipeline.zincrby(RedisKey_Gen.get_FirstPaid_Roles(channelId, appId, serverId), 1, currDay);
                //当日首次付费金额
                pipeline.zincrby(RedisKey_Gen.get_FirstPaid_Roles_Amounts(channelId, appId, serverId), payamounts, currDay);
            }
            pipeline.sync();

        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }


    /**
     * 新增创号
     */
    public void getDayNewAddAccount(String gameId, String channelId, List<String> timeList,
                                    Map<String, Map<String, Double>> resultList) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            Pipeline pipeline = jds.pipelined();

            //时间遍历
            for (String times : timeList) {
                String key = RedisKey_Gen.get_AccountCreate_Day(channelId, gameId, times);
                pipeline.bitcount(key);
                if (isLog) {
                    log.info(key);
                }
            }

            List<Object> res = pipeline.syncAndReturnAll();
            if (res == null || res.size() == 0) {
                return;
            }

            List<Double> codesDouble = res.stream()
                    .map(e -> Double.parseDouble(e.toString()))
                    .collect(Collectors.toList());

            Map<String, Double> resultMap = new HashMap<>();
            int timeIndex = 0;
            for (String times : timeList) {
                resultMap.put(times, codesDouble.get(timeIndex));
                timeIndex++;
            }
            log.info(resultMap);
            resultList.put(RedisKey_Tail.NEW_ADD_CREATE_ACCOUNT, resultMap);

        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }

    }

    public void getDayNewAddAccount_Server(String gameId, String serverId, List<String> timeList,
                                           Map<String, Map<String, Double>> resultList) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            Pipeline pipeline = jds.pipelined();

            //时间遍历
            for (String times : timeList) {
                String key = RedisKey_Gen.get_AccountCreate_Server_Day(gameId, serverId, times);
                pipeline.bitcount(key);
                if (isLog) {
                    log.info(key);
                }
            }

            List<Object> res = pipeline.syncAndReturnAll();
            if (res == null || res.size() == 0) {
                return;
            }

            List<Double> codesDouble = res.stream()
                    .map(e -> Double.parseDouble(e.toString()))
                    .collect(Collectors.toList());

            Map<String, Double> resultMap = new HashMap<>();
            int timeIndex = 0;
            for (String times : timeList) {
                resultMap.put(times, codesDouble.get(timeIndex));
                timeIndex++;
            }
            log.info(resultMap);
            resultList.put(RedisKey_Tail.NEW_ADD_FIRST_ACCOUNT, resultMap);

        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }

    }

    /**
     * 每日支付信息
     * 相同键值不同member取值
     */
    public void getDayPayInfo(String gameId, String channelId, String serverId,
                              List<String> timeList,
                              Map<String, Map<String, Double>> resultList) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            Pipeline pipeline = jds.pipelined();

            int tlSize = timeList.size();

            for (String times : timeList) {
                String key = RedisKey_Gen.get_RolesPayInfo_Day(channelId, gameId, serverId, times);
                pipeline.zscore(key, RedisKey_Member.RECHARGE_TIMES);
            }
            for (String times : timeList) {
                String key = RedisKey_Gen.get_RolesPayInfo_Day(channelId, gameId, serverId, times);
                pipeline.zscore(key, RedisKey_Member.RECHARGE_PLAYERS);
            }
            for (String times : timeList) {
                String key = RedisKey_Gen.get_RolesPayInfo_Day(channelId, gameId, serverId, times);
                pipeline.zscore(key, RedisKey_Member.RECHARGE_AMOUNTS);
            }

            List<Object> res = pipeline.syncAndReturnAll();
            if (res == null || res.size() == 0) {
                return;
            }
            //变成double
            List<Double> codesDouble = res.stream().map(
                    e -> {
                        double re;
                        if (e != null) {
                            re = Double.parseDouble(e.toString());
                        } else {
                            re = 0D;
                        }
                        return re;
                    }
            ).collect(Collectors.toList());

            Map<String, Double> resultMap1 = new HashMap<>();
            Map<String, Double> resultMap2 = new HashMap<>();
            Map<String, Double> resultMap3 = new HashMap<>();
            for (int i = 0; i < 3 * tlSize; i++) {
                if (i < tlSize) {
                    resultMap1.put(timeList.get(i), codesDouble.get(i));
                    resultList.put(RedisKey_Member.RECHARGE_TIMES, resultMap1);
                } else if (i < 2 * tlSize) {
                    resultMap2.put(timeList.get(i - tlSize), codesDouble.get(i));
                    resultList.put(RedisKey_Member.RECHARGE_PLAYERS, resultMap2);
                } else {
                    resultMap3.put(timeList.get(i - 2 * tlSize), codesDouble.get(i));
                    resultList.put(RedisKey_Member.RECHARGE_AMOUNTS, resultMap3);
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
    public void getDayZScore(Map<String, List<String>> keyListMap, List<String> timeList, Map<String, Map<String, Double>> resultList) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            Pipeline pipeline = jds.pipelined();

            for (Map.Entry<String, List<String>> entry : keyListMap.entrySet()) {
                String type = entry.getKey();
                //同一渠道 同一游戏 不同区服
                List<String> keyList = entry.getValue();
                // 按照时间顺序 获取分数
                for (String key : keyList) {
                    for (String times : timeList) {
                        pipeline.zscore(key, times);
                        if (isLog) {
                            log.info(key + "\t" + times);
                        }
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


    /**
     * 存储-分区概况-每日汇总数据
     */
    public void setRSByDay(String gameId, List<String> timeList, Map<String, RechargeSummary> rsMap) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            List<String> unCalDay = new ArrayList<>();
            String key = RedisKey_Gen.get_RechargeInfo_Game(gameId);
            Pipeline pipeline = jds.pipelined();
            for (String day : timeList) {
                pipeline.hexists(key, day);
            }
            List<Object> result = pipeline.syncAndReturnAll();
            int resultSize = result.size();
            for (int i = 0; i < resultSize; i++) {
                String res = result.get(i).toString();
                if (res.equals("false")) {
                    unCalDay.add(timeList.get(i));
                }
            }
            Map<String, String> map = new HashMap<>();
            for (Map.Entry<String, RechargeSummary> entry : rsMap.entrySet()) {
                if (unCalDay.contains(entry.getKey())) {
                    map.put(entry.getKey(), JSONObject.toJSONString(entry.getValue()));
                }
            }

            String today = DateUtil.getCurrentDayStr();
            map.remove(today);
            pipeline.hmset(key, map);
            pipeline.sync();

        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 获取-全服概况-每日汇总数据
     *
     * @param gameId   游戏id
     * @param timeList 时间区间 yyyyMMdd
     * @param unCalDay 未查询到的时间区间
     * @return
     */
    public void getRSByDay(String gameId, List<String> timeList, List<String> unCalDay, Map<String, String> rsMap) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);


            String key = RedisKey_Gen.get_RechargeInfo_Game(gameId);
            Pipeline pipeline = jds.pipelined();
            for (String day : timeList) {
                pipeline.hmget(key, day);
            }
            List<Object> result = pipeline.syncAndReturnAll();
            int resultSize = result.size();
            for (int i = 0; i < resultSize; i++) {
                String res = result.get(i).toString();
                String day = timeList.get(i);
                if (res.equals("[null]")) {
                    //未查询到结果
                    unCalDay.add(day);
                } else {
                    //已经查询到结果
                    rsMap.put(day, res);
                }
            }
            //排除当天
            String currDay = DateUtil.getCurrentDayStr();
            if (rsMap.containsKey(currDay)) {
                rsMap.remove(currDay);
                unCalDay.add(currDay);
            }
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 存储-分区概况-每日汇总数据
     *
     * @param gameId   游戏id
     * @param serverId 区服id
     * @param timeList 时间区间 yyyyMMdd
     * @param rsMap
     * @return
     */
    public void setRSByServer(String gameId, String serverId, List<String> timeList, Map<String, RechargeSummary> rsMap) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            List<String> unCalDay = new ArrayList<>();
            String key = RedisKey_Gen.get_RechargeInfo_Server(gameId, serverId);

            Pipeline pipeline = jds.pipelined();
            for (String day : timeList) {
                pipeline.hexists(key, day);
            }
            List<Object> result = pipeline.syncAndReturnAll();
            int resultSize = result.size();
            for (int i = 0; i < resultSize; i++) {
                String res = result.get(i).toString();
                if (res.equals("false")) {
                    unCalDay.add(timeList.get(i));
                }
            }
            Map<String, String> map = new HashMap<>();
            for (Map.Entry<String, RechargeSummary> entry : rsMap.entrySet()) {
                if (unCalDay.contains(entry.getKey())) {
                    map.put(entry.getKey(), JSONObject.toJSONString(entry.getValue()));
                }
            }
            //排除当天
            String today = DateUtil.getCurrentDayStr();
            map.remove(today);
            pipeline.hmset(key, map);
            pipeline.sync();
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 获取-分区概况-每日汇总数据
     *
     * @param gameId   游戏id
     * @param timeList 时间区间 yyyyMMdd
     * @param unCalDay 未查询到的时间区间
     * @return
     */
    public void getRSByServer(String gameId, String serverId, List<String> timeList, List<String> unCalDay, Map<String, String> rsMap) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String key = RedisKey_Gen.get_RechargeInfo_Server(gameId, serverId);

            Pipeline pipeline = jds.pipelined();
            for (String day : timeList) {
                pipeline.hmget(key, day);
            }
            List<Object> result = pipeline.syncAndReturnAll();
            int resultSize = result.size();
            for (int i = 0; i < resultSize; i++) {
                String res = result.get(i).toString();
                String day = timeList.get(i);
                if (res.equals("[null]")) {
                    //  未查询到结果
                    unCalDay.add(day);
                } else {
                    //  已经查询到结果
                    rsMap.put(day, res);
                }
            }
            //排除当天
            String currDay = DateUtil.getCurrentDayStr();
            if (rsMap.containsKey(currDay)) {
                rsMap.remove(currDay);
                unCalDay.add(currDay);
            }
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 存储-分渠道概况-每日汇总数据
     *
     * @param gameId    游戏id
     * @param channelId 渠道id
     * @param timeList  时间区间 yyyyMMdd
     * @param rsMap
     * @return
     */
    public void setRSByChannel(String gameId, String channelId, List<String> timeList, Map<String, RechargeSummary> rsMap) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            List<String> unCalDay = new ArrayList<>();
            String key = RedisKey_Gen.get_RechargeInfo_Channel(gameId, channelId);

            Pipeline pipeline = jds.pipelined();
            for (String day : timeList) {
                pipeline.hexists(key, day);
            }
            List<Object> result = pipeline.syncAndReturnAll();
            int resultSize = result.size();
            for (int i = 0; i < resultSize; i++) {
                String res = result.get(i).toString();
                if (res.equals("false")) {
                    unCalDay.add(timeList.get(i));
                }
            }
            Map<String, String> map = new HashMap<>();
            for (Map.Entry<String, RechargeSummary> entry : rsMap.entrySet()) {
                if (unCalDay.contains(entry.getKey())) {
                    map.put(entry.getKey(), JSONObject.toJSONString(entry.getValue()));
                }
            }
            //排除当天
            String today = DateUtil.getCurrentDayStr();
            map.remove(today);
            pipeline.hmset(key, map);
            pipeline.sync();
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 获取-分渠道概况-每日汇总数据
     *
     * @param gameId   游戏id
     * @param timeList 时间区间 yyyyMMdd
     * @param unCalDay 未查询到的时间区间
     * @return
     */
    public void getRSByChannel(String gameId, String channelId, List<String> timeList, List<String> unCalDay, Map<String, String> rsMap) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String key = RedisKey_Gen.get_RechargeInfo_Channel(gameId, channelId);

            Pipeline pipeline = jds.pipelined();
            for (String day : timeList) {
                pipeline.hmget(key, day);
            }
            List<Object> result = pipeline.syncAndReturnAll();
            int resultSize = result.size();
            for (int i = 0; i < resultSize; i++) {
                String res = result.get(i).toString();
                String day = timeList.get(i);
                if (res.equals("[null]")) {
                    //  未查询到结果
                    unCalDay.add(day);
                } else {
                    //  已经查询到结果
                    rsMap.put(day, res);
                }
            }
            //排除当天
            String currDay = DateUtil.getCurrentDayStr();
            if (rsMap.containsKey(currDay)) {
                rsMap.remove(currDay);
                unCalDay.add(currDay);
            }
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 游戏概况-活跃玩家
     *
     * @param type     1-全服/2-分渠道/3-分区服
     * @param timeList 时间段
     * @param rs       存储的对象
     */
    public void setRS_Active(String gameId, String channelId, String serverId, List<String> timeList, RechargeSummary rs, Integer type, String day) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = this.jedisManager.getJedis();
            jds.select(DB_INDEX);
            String[] setMember;
            if (type == 1) {
                setMember = new String[1];
                setMember[0] = RedisKey_Gen.get_RolesActive_Day_Game(gameId, day);
            } else {
                setMember = new String[timeList.size()];
                for (int i = 0; i < timeList.size(); i++) {
                    if (type == 2) {
                        setMember[i] = RedisKey_Gen.get_RolesActive_Day_Channel(gameId, channelId, timeList.get(i));
                    } else if (type == 3) {
                        setMember[i] = RedisKey_Gen.get_RolesActive_Day_Server(gameId, serverId, timeList.get(i));
                    } else {
                        return;
                    }
                }
            }

            //求交集
            Set<String> roleSet = jds.sunion(setMember);

            rs.setActivePlayer(roleSet.size());
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }
}
