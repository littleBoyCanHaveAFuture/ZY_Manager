package com.ssm.promotion.core.jedis;


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
    private static final int DB_INDEX = 3;
    private jedisManager jedisManager;

    private boolean isLog = true;

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
                    String key = RedisKeyHeader.RS_INFO + RedisKey.FORMAT_DATE + yyyyMM + RedisKey.FORMAT_SHARP + RedisKeyTail.RECHARGE_SUMMARY;
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
        String key1 = RedisKeyHeader.USER_INFO + ":spid:*:gid:*:sid:*" + RedisKey.FORMAT_SHARP + RedisKeyTail.ONLINE_PLAYERS;
        String key2 = RedisKeyHeader.REALTIMEDATA + ":spid:*:gid:*:sid:*:date:" + currDayMin + RedisKey.FORMAT_SHARP + RedisKeyTail.REALTIME_ONLINE_ACCOUNTS;
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
                targetbody.append(keys[6].split(RedisKey.FORMAT_SHARP)[0]).append(":");
                String target = RedisKeyHeader.REALTIMEDATA + ":" + targetbody + "date:" + currDay + RedisKey.FORMAT_SHARP + RedisKeyTail.REALTIME_ONLINE_ACCOUNTS;
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
        String key1 = RedisKeyHeader.USER_INFO + ":spid:*:gid:*:sid:*" + RedisKey.FORMAT_SHARP + RedisKeyTail.ONLINE_PLAYERS;
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
                    log.info("src key------>" + mapEntry);
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
                log.info("find " + list.size() + " key,use: " + (System.currentTimeMillis() - t1) + " ms,cursor:" + cursor);
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
                    key = RedisKeyNew.getKeyRolesOnlineMin(spId, String.valueOf(gameId), String.valueOf(serverId), currDay);
                    break;
                case 2:
                    //收入
                    key = RedisKeyNew.getKeyRolesPaidMin(spId, String.valueOf(gameId), String.valueOf(serverId), currDay);
                    break;
                case 3:
                    //新增角色
                    key = RedisKeyNew.getKeyRolesCreateMin(spId, String.valueOf(gameId), String.valueOf(serverId), currDay);
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
                    key = RedisKeyNew.getKeyRolesOnlineMinSp(spId, String.valueOf(gameId), currDay);
                    break;
                case 2:
                    //收入
                    key = RedisKeyNew.getKeyRolesPaidMinSp(spId, String.valueOf(gameId), currDay);
                    break;
                case 3:
                    //新增角色
                    key = RedisKeyNew.getKeyRolesCreateMinSp(spId, String.valueOf(gameId), currDay);
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

            String tokenKey = String.format(RedisKey.FORMAT_SG, RedisKeyHeader.Token, appId, channelId);
            String member = ChannelUid;
            String value = RandomUtil.rndSecertKey() + RedisKey.FORMAT_SHARP + timestamp;
            jds.hset(tokenKey, member, value);
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
            long timestamp = System.currentTimeMillis();

            String tokenKey = String.format(RedisKey.FORMAT_SG, RedisKeyHeader.Token, appId, channelId);
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
     * 设置游戏
     */
    public void setGAMEIDInfo(String appId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String key = RedisKeyTail.GAMEINFO;
            jds.sadd(key, appId);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 获取游戏
     */
    public Set<String> getGAMEIDInfo(String appId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String key = RedisKeyTail.GAMEINFO;
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
    public void delGAMEIDInfo(String appId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String key = RedisKeyTail.GAMEINFO;
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

            String key = String.format(RedisKey.FORMAT_GAME, appId, RedisKeyTail.SPIDINFO);
            jds.sadd(key, channelId);
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    /**
     * 获取游戏渠道
     */
    public Set<String> getSPIDInfo(String appId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            String key = String.format(RedisKey.FORMAT_GAME, appId, RedisKeyTail.SPIDINFO);
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

            String key = String.format(RedisKey.FORMAT_GAME, appId, RedisKeyTail.SPIDINFO);
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

            String key = String.format(RedisKey.FORMAT_SG_SS, RedisKeyTail.SERVERINFO, appId, channelId);
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

            String key = String.format(RedisKey.FORMAT_SG_SS, RedisKeyTail.SERVERINFO, appId, channelId);
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

            String currday = DateUtil.getCurrentDayStr();

            String key1;
            String key2;
            if (auto) {
                //渠道-游戏
//                String userSGKey = String.format(RedisKey.FORMAT_SG, RedisKeyHeader.USER_INFO, channelId, gameId);
                //新增创号：渠道-游戏-日期
//                key1 = userSGKey + RedisKey.FORMAT_DATE + currDay + RedisKey.FORMAT_SHARP + RedisKeyTail.NEW_ADD_CREATE_ACCOUNT;
                //渠道-该游戏所有账号 渠道-游戏
//                key2 = userSGKey + RedisKey.FORMAT_SHARP + RedisKeyTail.GAME_ACCOUNT_ALL_NUMS;
                key1 = RedisKeyNew.getKeyAccountCreateDay(String.valueOf(channelId), String.valueOf(gameId), currday);
                key2 = RedisKeyNew.getKeyAccountAll(String.valueOf(channelId), String.valueOf(gameId));
            } else {
                //官方-游戏
                String userOfficialKey = String.format(RedisKey.FORMAT_SSGD, RedisKeyHeader.USER_INFO, RedisKeyBody.OFFICIAL, gameId);
                //新增创号-官方 官方-游戏
                key1 = userOfficialKey + RedisKey.FORMAT_DATE + currday + RedisKey.FORMAT_SHARP + RedisKeyTail.NEW_ADD_CREATE_ACCOUNT;
                //官方-该游戏所有账号 官方-游戏
                key2 = userOfficialKey + RedisKey.FORMAT_SHARP + RedisKeyTail.GAME_ACCOUNT_ALL_NUMS;
            }

            Pipeline pipeline = jds.pipelined();
            pipeline.setbit(key1, accountId, true);
            pipeline.setbit(key2, accountId, true);
            log.info("setbit " + key1 + "\t" + accountId + "\t" + true);
            log.info("setbit " + key2 + "\t" + accountId + "\t" + true);
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

            Pipeline pipeline = jds.pipelined();

            //新版
            //活跃玩家
            pipeline.zincrby(RedisKeyNew.getKeyRolesActiveDay(channelId, appId, serverId), 1, currDay);
            //在线玩家
            pipeline.zincrby(RedisKeyNew.getKeyRolesOnlineDay(channelId, appId, serverId), 1, currDay);
            //实时在线
            pipeline.zincrby(RedisKeyNew.getKeyRolesOnlineMin(channelId, appId, serverId, currDay), 1, currDayMin);
            pipeline.zincrby(RedisKeyNew.getKeyRolesOnlineMinSp(channelId, appId, currDay), 1, currDayMin);
            //设置过期时间
            //todo
            pipeline.sync();
        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    public void initGameSp(String appId,
                           String channelId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            //是否滚服用户
            String key = RedisKeyNew.getKeyAccountCreateRoles(channelId, appId);
            //若没有该bitmap 需要重新生成
            if (!jds.exists(key)) {
//                jds.setbit(key, 0, "0");
                log.info("NONE:" + key);
            }
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
                           long accountId, String roleId) {
        Jedis jds = null;
        boolean isBroken = false;
        try {
            jds = jedisManager.getJedis();
            jds.select(DB_INDEX);

            //当前分钟
            String currDayMin = DateUtil.getCurrentMinuteStr();
            String currDay = DateUtil.getCurrentDayStr();

            //是否滚服用户
            String key = RedisKeyNew.getKeyAccountCreateRoles(channelId, appId);

            boolean hasRole = jds.sismember(key, String.valueOf(accountId));

            Pipeline pipeline = jds.pipelined();
            //新版
            //新增创角
            pipeline.zincrby(RedisKeyNew.getKeyRolesCreateDay(channelId, appId, serverId), 1, currDay);
            if (!hasRole) {
                //新增创角去除滚服
                pipeline.zincrby(RedisKeyNew.getKeyRolesCreateFirst(channelId, appId, serverId), 1, currDay);
                //创建过角色的账号
                pipeline.sadd(RedisKeyNew.getKeyAccountCreateRoles(channelId, appId), String.valueOf(accountId));
            }
            //实时创角
            pipeline.zincrby(RedisKeyNew.getKeyRolesCreateMin(channelId, appId, serverId, currDay), 1, currDayMin);
            pipeline.zincrby(RedisKeyNew.getKeyRolesCreateMinSp(channelId, appId, currDay), 1, currDayMin);
            //累计创角数目(所有角色)
            pipeline.zincrby(RedisKeyNew.getKeyRolesCreateServer(channelId, appId, serverId), 1, RedisKey.GAME_ACCUMULATION_CREATE_ROLE);
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
            jds.zincrby(RedisKeyNew.getKeyRolesPaidMinSp(channelId, appId, currDay), payamounts, currDayMin);

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
            String key1 = userSGSKey + RedisKey.FORMAT_DATE + currDay + RedisKey.FORMAT_SHARP + RedisKeyTail.ACTIVE_PLAYERS;
            //在线账号 渠道-游戏-区服-日期
            String key2 = userSGSKey + RedisKey.FORMAT_DATE + currDay + RedisKey.FORMAT_SHARP + RedisKeyTail.ONLINE_PLAYERS;


            boolean res1 = jds.getbit(key1, accountId);
            log.info("exit key:" + res1);

            if (!res1) {
                boolean res2 = jds.setbit(key1, accountId, true);
                if (isLog) {
                    log.info("enterGame key1:" + key1 + "\taccountId:" + accountId + "\tresult:[" + res2 + "]");
                }
            } else {
                boolean res3 = jds.setbit(key2, accountId, false);
//                jds.expireAt(key2, DateUtil.getEndTimestamp());
                if (isLog) {
                    log.info("enterGame key2:" + key2 + "\taccountId:" + accountId + "\tresult:[" + res3 + "]");
                }
            }


        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }
    }

    public void zIncrBy(String key, long score, String member) {
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
            for (String keyTail : keyTailList) {
                for (String times : timeList) {
                    String key = keyBody + RedisKey.FORMAT_DATE + times + RedisKey.FORMAT_SHARP + keyTail;
                    pipeline.bitcount(key);
                    log.info(key);
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

                Map<String, Double> resultMap = new HashMap<>();
                int f = 0;
                for (String times : timeList) {
                    resultMap.put(times, codesDouble.get(f));
                    f++;
                }
                resultList.put(keyTailList.get(i), resultMap);
            }

        } catch (Exception e) {
            isBroken = true;
            e.printStackTrace();
        } finally {
            returnResource(jds, isBroken);
        }

    }

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
                String key = RedisKeyNew.getKeyAccountCreateDay(channelId, gameId, times);
                pipeline.bitcount(key);
                log.info(key);
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

            resultList.put(RedisKeyTail.NEW_ADD_CREATE_ACCOUNT, resultMap);

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
                String key = keyBody + RedisKey.FORMAT_DATE + times + RedisKey.FORMAT_SHARP + keyTail;
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
                        log.info(key + "\t" + times);
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
