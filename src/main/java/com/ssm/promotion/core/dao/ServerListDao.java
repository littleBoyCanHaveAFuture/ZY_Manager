package com.ssm.promotion.core.dao;


import com.ssm.promotion.core.entity.ServerInfo;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * 2019/11/15
 */
public interface ServerListDao {
    /**
     * 查询服务器列表
     */
    List<ServerInfo> selectServers(Map<String, Object> map);

    /**
     * 添加服务器
     */
    public int insertServer(ServerInfo server);

    /**
     * 修改服务器
     */
    public int updateServer(ServerInfo server);

    /**
     * 删除
     */
    public int delServer(Integer id);

    /**
     * 服务器总数
     * 有登录地址的
     */
    Long getTotalServers(Map<String, Object> map);

    /**
     * 不同的渠道id
     */
    List<Integer> selectDistinctSpId(Map<String, Object> map);

    /**
     * 不同的游戏id
     */
    List<Integer> selectDistinctGameId(Map<String, Object> map);

    /**
     * 不同的区服id
     */
    List<Integer> selectDistinctServerId(Map<String, Object> map);


    /**
     * 查询是否可以注册
     */
    int selectRegStatus(Map<String, Object> map);

    /**
     * 查询是否可以登录
     */
    int selectLoginStatus(Map<String, Object> map);

    int exist(Map<String, Object> map);

    /**
     * 开服日期
     */
    String selectOpenday(Map<String, Object> map);
}
