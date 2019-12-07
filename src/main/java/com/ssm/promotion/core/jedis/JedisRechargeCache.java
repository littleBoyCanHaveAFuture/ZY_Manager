package com.ssm.promotion.core.jedis;


import com.ssm.promotion.core.util.DateUtil;
import com.ssm.promotion.core.util.SerializeUtil;
import org.apache.log4j.Logger;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;

import java.util.*;

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

    public jedisManager getJedisManager() {
        return jedisManager;
    }

    public void setJedisManager(jedisManager jedisManager) {
        this.jedisManager = jedisManager;
    }

    //账号id 一定要唯一
    //zscore
    //充值次数:         ZADD "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RechargeInfo" {value} "paytimes"
    //充值人数:         ZADD "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RechargeInfo" {value} "accountnumbers"
    //充值金额:         ZADD "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RechargeInfo" {value} "payamounts"
    //当日首次付费金额： ZADD "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RechargeInfo" {amounts} "firstamounts"
    //注册付费金额：    ZADD "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RechargeInfo" {amounts} "amounts_NA_CA"
    //累计充值金额：    ZADD "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}#RechargeTotalInfo" {value} "ACC_amounts"
    //累计充值人数:     ZADD "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}#RechargeTotalInfo" {value} "ACC_playernumbers"

    //累计创角:        ZADD "UserInfo:spid:{spid}:gid:{gid}:sid:{sid}#AccountInfo" {value} "ACC_CR"

    //getbit

    //bittop and

    //bitcount
    //新增创号-<yyMMdd,账号数目>L SETBIT "UserInfo:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#NA_CA" {account_Id}
    //新增创角-<yyMMdd,账号数目>: SETBIT "UserInfo:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#NA_CR {account_Id}
    //所有账号的数目:            SETBIT "UserInfo:gid:{gid}#totalAccount" {account_Id}
    //活跃玩家 的账号数目:       SETBIT "UserInfo:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#activePlayers" {account_id}
    //当日首次付费人数:         SETBIT "activePlayers:spid:{spid}:gid:{gid}:sid:{sid}:date:{yyyyMMdd}#RechargeAccount" {account_id}
    //注册付费人数:            SETBIT "activePlayers:gid:{gid}:sid:{sid}:spid:{spid}:date:{yyyyMMdd}#RechargeAccount_NA_CA" {account_id}

    //get all bitmap
    //所有账号
    //活跃玩家

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
            Pipeline pipeline = jds.pipelined();
            boolean auto = Boolean.parseBoolean(map.get("auto"));
            Integer gameId = Integer.parseInt(map.get("gameId"));
            Integer serverId = Integer.parseInt(map.get("serverId"));
            long accountId = Long.parseLong(map.get("accountId"));

            String currday = DateUtil.getCurrentDayStr();
            String key1;
            String key2;
            if (auto) {
                String spId = map.get("channelId");
                //新增创号-渠道
                key1 = String.format("%s:spid:%s:gid:%d:sid:%d:date:%s#%s",
                        RedisKeyHeader.USER_INFO, spId, gameId, serverId,
                        currday, RedisKeyTail.NEW_ADD_CREATE_ACCOUNT);
                pipeline.setbit(key1, accountId, true);
                //渠道-该游戏所有账号
                key2 = String.format("%s:spid:%s:gid:%d#%s",
                        RedisKeyHeader.USER_INFO, spId, gameId, RedisKeyTail.GAME_ACCOUNT_ALL_NUMS);
                pipeline.setbit(key2, accountId, true);

            } else {
                //新增创号-官方
                key1 = String.format("%s:%s:gid:%d:sid:%d:date:%s#%s",
                        RedisKeyHeader.USER_INFO, RedisKeyBody.OFFICIAL, gameId, serverId,
                        currday, RedisKeyTail.NEW_ADD_CREATE_ACCOUNT);
                pipeline.setbit(key1, accountId, true);
                //官方-该游戏所有账号
                key2 = String.format("%s:gid:%d#%s",
                        RedisKeyHeader.USER_INFO, RedisKeyBody.OFFICIAL,
                        gameId, RedisKeyTail.GAME_ACCOUNT_ALL_NUMS);
                pipeline.setbit(key2, accountId, true);
            }

            List<Object> res = pipeline.syncAndReturnAll();

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
            Pipeline pipeline = jds.pipelined();
            //渠道-游戏-区服 活跃账号
            String key1 = String.format("%s:spid:%s:gid:%s:sid:%s:date:%s#%s",
                    RedisKeyHeader.USER_INFO, channelId, appId, serverId, currDay,
                    RedisKeyTail.ACTIVE_PLAYERS);

            //渠道-游戏-区服 在线账号
            String key2 = String.format("%s:spid:%s:gid:%s:sid:%s:date:%s#%s",
                    RedisKeyHeader.USER_INFO, channelId, appId, serverId, currDay,
                    RedisKeyTail.ONLINE_PLAYERS);
            pipeline.setbit(key1, accountId, true);
            pipeline.setbit(key2, accountId, true);
            List<Object> res = pipeline.syncAndReturnAll();

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
     * 进入游戏
     * 1.活跃玩家
     * 2.在线玩家
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

            String currDay = DateUtil.getCurrentDayStr();
            Pipeline pipeline = jds.pipelined();

            //渠道-游戏-区服 活跃账号
            String key1 = String.format("%s:spid:%s:gid:%s:sid:%s:date:%s#%s",
                    RedisKeyHeader.USER_INFO, channelId, appId, serverId, currDay,
                    RedisKeyTail.ACTIVE_PLAYERS);

            //渠道-游戏-区服 在线账号
            String key2 = String.format("%s:spid:%s:gid:%s:sid:%s:date:%s#%s",
                    RedisKeyHeader.USER_INFO, channelId, appId, serverId, currDay,
                    RedisKeyTail.ONLINE_PLAYERS);
            pipeline.setbit(key1, accountId, true);
            pipeline.setbit(key2, accountId, true);

            List<Object> res = pipeline.syncAndReturnAll();

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
                pipeline.syncAndReturnAll();
                pipeline.clear();
            }

            //时间遍历
            for (String times : timeList) {
                String key = RedisKeyBody.appendBodyTimes(keyBody, times) + keyTail;

                System.out.println("getDayZScore key:member\t" + key + "  " + member);

//                byte[] skey = SerializeUtil.serialize(key);
//                byte[] smember = SerializeUtil.serialize(member);

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
                pipeline.clear();
                for (String times : tmpTimeList) {
                    String key = keyBody + ":date:" + times + "#" + keyTail;
                    pipeline.setbit(key, 10L, true);
                }

                pipeline.sync();
                pipeline.clear();
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
                    String destkey = RedisKeyBody.appendBodyTimes(destKey, times) + destBodyTail;
                    String srckey1 = RedisKeyBody.appendBodyTimes(srcKey1, times) + src1BodyTail;
                    String srckey2 = RedisKeyBody.appendBodyTimes(srcKey2, times) + src2BodyTail;
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
                pipeline.clear();

                for (String times : tmpTimeList1) {
                    String srckey = RedisKeyBody.appendBodyTimes(srcKey1, times) + src1BodyTail;
                    pipeline.setbit(srckey, 10L, true);
                }
                for (String times : tmpTimeList2) {
                    String destkey = RedisKeyBody.appendBodyTimes(srcKey2, times) + src2BodyTail;
                    pipeline.setbit(destkey, 10L, true);
                }
                pipeline.sync();
                pipeline.clear();
            }

            for (String times : timeList) {
                String destkey = RedisKeyBody.appendBodyTimes(destKey, times) + destBodyTail;
                String srckey1 = RedisKeyBody.appendBodyTimes(srcKey1, times) + src1BodyTail;
                String srckey2 = RedisKeyBody.appendBodyTimes(srcKey2, times) + src2BodyTail;

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
     * 简单的set
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

            jds.setbit(key, member, value);
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
