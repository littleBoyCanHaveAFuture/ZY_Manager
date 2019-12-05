package com.ssm.promotion.core.entity;

import lombok.Data;

/**
 * 角色信息
 * 渠道-游戏-区服 会影响角色
 * mysql：zy_role
 *
 * @author song minghua
 * @date 2019/12/4
 */
@Data
public class GameRole {
    /**
     * 数据库id
     */
    Integer id;
    /**
     * 账号唯一id
     */
    Integer accountId;
    /**
     * 角色唯一id
     * 暂定
     * index 该区第几个角色
     * String = id + Spid + gameId + ServerId + index  ;
     */
    Integer roleId;
    /**
     * 渠道id
     */
    String channelId;
    /**
     * 游戏id
     */
    String gameId;
    /**
     * 游戏区服id
     */
    Integer serverId;

    /**
     * 最后一次登录时间
     */
    String lastLoginTime;
    /**
     * 自定义参数
     */
    String param;
}
