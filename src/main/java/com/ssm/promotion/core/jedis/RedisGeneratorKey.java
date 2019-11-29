package com.ssm.promotion.core.jedis;


import com.ssm.promotion.core.util.DateUtil;

import static com.ssm.promotion.core.jedis.RedisKeyBody.generatorBody;
import static com.ssm.promotion.core.util.StringUtil.COLON;

/**
 * @author song minghua
 * @date 2019/11/27
 */
public class RedisGeneratorKey {
    /**
     * 玩家信息
     * 1.注册账号
     * --1.新增创号
     * --2.所有账号
     * 2.创建角色
     * --1.新增创角
     * --2.累计创角
     * --3.不同区服创角账号数目
     * 3.
     */

    /**
     * 注册新账号
     * 1.新增创号:当日新注册账号且进入游戏的账号数目，    SETBIT "UserInfo:gid:{gid}:sid:{sid}:spid:{spid}:date:{yyyyMMdd}#NA_CA" {account_Id}
     * 2.所有账号:所有账号:该游戏内的所有账号数目        SETBIT "UserInfo:gid:{gid}#totalAccount" {account_Id}
     */
    public static String getKeySignUp(Integer gameId, Integer serverId, String spId, Integer type, String ftTimes) throws Exception {
        String day = DateUtil.getCurrentDayStr();
        StringBuilder key = new StringBuilder();
        if (type == 1) {
            //新增创号 = 当日新注册账号且进入游戏的账号数目，

            key.append(RedisKeyHeader.USER_INFO).append(COLON);
            key.append(generatorBody(gameId, serverId, spId, 3, ftTimes));
            key.append(RedisKeyTail.NEW_ADD_CREATE_ACCOUNT);
        } else if (type == 2) {
            // 所有账号:该游戏内的所有账号数目

            key.append(RedisKeyHeader.USER_INFO).append(COLON);
            key.append(generatorBody(gameId, null, null, 1, "-1"));
            key.append(RedisKeyTail.GAME_ACCOUNT_ALL_NUMS);
        }
        System.out.println("getkeySignUp key:" + key);
        return key.toString();
    }

    /**
     * //假设一个区只能一个角色
     * 玩家使用账号并登录游戏
     * 1.新增创角:            SETBIT "UserInfo:gid:{gid}:sid:{sid}:spid:{spid}:date:{yyyyMMdd}#NA_CR {account_Id}
     * 2.创建过角色的账号:     SETBIT "UserInfo:gid:{gid}#SA_SRole"            {account_id}
     * 3.创建过多个角色的账号: SETBIT "UserInfo:gid:{gid}#SA_MRole"             {account_id}
     * 4.累计创角:该游戏创角角色的次数:   ZADD "UserInfo:gid:{gid}:sid:{sid}:spid:{spid}#AccountInfo" {value} "ACC_CR"
     */
    public static String getKeyLoginIn(Integer gameId, Integer serverId, String spId, Integer type, String ftTimes) throws Exception {
        String day = DateUtil.getCurrentDayStr();
        StringBuilder key = new StringBuilder();
        if (type == 1) {
            //新增创角 = 当日创建新角色的账号数目
            key.append(RedisKeyHeader.USER_INFO).append(COLON);
            key.append(generatorBody(gameId, serverId, spId, 3, ftTimes));
            key.append(RedisKeyTail.NEW_ADD_CREATE_ROLE);
        } else if (type == 2) {
            //同游戏所有区服:
            key.append(RedisKeyHeader.USER_INFO).append(COLON);
            key.append(generatorBody(gameId, null, null, 1, "-1"));
            key.append(RedisKeyTail.GAME_ACCOUNT_HAS_ROLE);
        } else if (type == 3) {
            //同游戏所有区服: 创建第2个或更多角色时候加入(type=2已有账号)
            key.append(RedisKeyHeader.USER_INFO).append(COLON);
            key.append(generatorBody(gameId, null, null, 1, "-1"));
            key.append(RedisKeyTail.GAME_ACCOUNT_MULTIPLE_ROLE);
        } else if (type == 4) {
            //累计创角:该游戏创角角色的次数
            key.append(RedisKeyHeader.USER_INFO).append(COLON);
            key.append(generatorBody(gameId, serverId, spId, 3, "-1"));
            key.append(RedisKeyTail.ACCOUNT_INFO);
        }
        System.out.println("getkeyLoginIn key:" + key);
        return key.toString();
    }

    /**
     * 玩家进入游戏
     * 1.活跃玩家: 当日上线的玩家账号数目
     * SETBIT "UserInfo:gid:{gid}:sid:{sid}:spid:{spid}:date:{yyyyMMdd}#activePlayers" {account_id}
     */
    public static String getKeyEnterGame(Integer gameId, Integer serverId, String spId, String ftTimes) throws Exception {
        String day = DateUtil.getCurrentDayStr();
        StringBuilder key = new StringBuilder();

        key.append(RedisKeyHeader.USER_INFO).append(COLON);
        key.append(generatorBody(gameId, serverId, spId, 3, ftTimes));
        key.append(RedisKeyTail.ACTIVE_PLAYERS);

        System.out.println("getkeyEnterGame key:" + key);
        return key.toString();
    }

    /**
     * @param type 1.充值次数
     *             2.充值人数
     *             3.充值金额
     *             4.当日首次付费金额
     *             5.注册付费金额
     *             7.累计充值金额
     *             8.累计充值人数
     */
    static String getKeyRechargeMember(Integer type) {
        switch (type) {
            case 1:
                return RedisKey.RECHARGE_TIMES;
            case 2:
                return RedisKey.RECHARGE_PLAYERS;
            case 3:
                return RedisKey.RECHARGE_AMOUNTS;
            case 4:
                return RedisKey.RECHARGE_FIRST_AMOUNTS;
            case 5:
                return RedisKey.RECHARGE_AMOUNTS_NA_CA;
            case 6:
                return RedisKey.GAME_ACCUMULATION_RECHARGE_AMOUNTS;
            case 7:
                return RedisKey.GAME_ACCUMULATION_RECHARGE_ACCOUNTS;
            default:
                return null;
        }
    }

    /**
     * 成功充值
     *
     * @param type 1.充值信息：充值次数、充值人数、充值金额、当日首次付费金额、注册付费金额 ：sort-set
     *             2.累计充值信息：累计充值金额、累计充值人数：bitmap
     *             3.当日首次付费人数：bitmap
     *             4.注册付费人数:
     */
    static String getKeyRecharge(Integer gameId, Integer serverId, String spId, Integer type, String ftTimes) throws Exception {
        String day = DateUtil.getCurrentDayStr();
        StringBuilder key = new StringBuilder();
        if (type == 1) {
            //充值次数 = 当日充值的次数
            //充值次数: ZADD "activePlayers:gid:{gid}:sid:{sid}:spid:{spid}:date:{yyyyMMdd}#RechargeInfo" {value} "paytimes"
            //充值人数 = 当日充值的玩家账号数目
            //充值人数: ZADD "activePlayers:gid:{gid}:sid:{sid}:spid:{spid}:date:{yyyyMMdd}#RechargeInfo" {value} "accountnumbers"
            //充值金额 = 当日充值的金额数目
            //充值金额: ZADD "activePlayers:gid:{gid}:sid:{sid}:spid:{spid}:date:{yyyyMMdd}#RechargeInfo" {value} "payamounts"
            //当日首次付费金额:
            //当日首次付费金额：ZADD "activePlayers:gid:{gid}:sid:{sid}:spid:{spid}:date:{yyyyMMdd}#RechargeInfo" {amounts} "firstamounts"
            //注册付费金额 = {新增创号}当天充值金额:
            //注册付费金额：   ZADD "activePlayers:gid:{gid}:sid:{sid}:spid:{spid}:date:{yyyyMMdd}#RechargeInfo" {amounts} "amounts_NA_CA"
            //取值: ZSCORE

            key.append(RedisKeyHeader.ACTIVE_PLAYERS_INFO).append(COLON);
            key.append(generatorBody(gameId, serverId, spId, 3, ftTimes));
            key.append(RedisKeyTail.RECHARGE_INFO);
        } else if (type == 2) {
            //累计充值金额：
            //1.渠道：ZADD "activePlayers:gid:{gid}:sid:{sid}:spid:{spid}:date:{yyyyMMdd}#RechargeTotalInfo" {value} "ACC_amounts"
            //2.区服:不同渠道加起来
            // 累计充值人数
            //1.渠道ZADD "activePlayers:gid:{gid}:sid:{sid}:spid:{spid}:date:{yyyyMMdd}#RechargeTotalInfo" {value} "ACC_playernumbers"
            //2.区服:不同渠道加起来

            key.append(RedisKeyHeader.ACTIVE_PLAYERS_INFO).append(COLON);
            key.append(generatorBody(gameId, serverId, spId, 3, ftTimes));
            key.append(RedisKeyTail.RECHARGE_TOTAL_INFO);
        } else if (type == 3) {
            //充值成功:{当天充值的账号}无记录，则往{当日每个账号首次充值金额记录}添加数据
            //当天首充的账号信息:
            //SETBIT "activePlayers:gid:{gid}:sid:{sid}:spid:{spid}:date:{yyyyMMdd}#RechargeAccount" {account_id}

            key.append(RedisKeyHeader.ACTIVE_PLAYERS_INFO).append(COLON);
            key.append(generatorBody(gameId, serverId, spId, 3, ftTimes));
            key.append(RedisKeyTail.RECHARGE_ACCOUNT);
        } else if (type == 4) {
            //注册付费人数 = {新增创号}当天充值的账号:
            //SETBIT "activePlayers:game:{gid}:server:{svrid}:sp:{spid}:data:{yyyyMMdd}#RechargeAccount_NA_CA" {account_id}

            key.append(RedisKeyHeader.ACTIVE_PLAYERS_INFO).append(COLON);
            key.append(generatorBody(gameId, serverId, spId, 3, ftTimes));
            key.append(RedisKeyTail.RECHARGE_ACCOUNT_NA_CA);
        }
        System.out.println("getkeyLoginIn key:" + key);
        return key.toString();
    }
}
