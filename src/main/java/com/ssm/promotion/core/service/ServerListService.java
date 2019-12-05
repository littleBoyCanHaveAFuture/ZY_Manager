package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.ServerInfo;

import java.util.List;
import java.util.Map;

public interface ServerListService {

    /**
     * 获取 服务器列表
     */

    public List<ServerInfo> getServerList(Map<String, Object> map, Integer userId);

    /**
     * 添加服务器
     */
    public int addServer(ServerInfo server, Integer userId);

    /**
     * 修改服务器
     */
    public int updateServer(ServerInfo server, Integer userId);

    /**
     * 删除服务器
     */
    public int delServer(Integer id, Integer userId);

    /**
     * 服务器总数
     */
    Long getTotalServers(Map<String, Object> map, Integer userId);

    /**
     * 不同的区服渠道
     *
     * @param type 同游戏
     *             1 不同区服
     *             2 不同渠道
     */

    List<String> getDistinctServerInfo(Map<String, Object> map, Integer type, Integer userId);

    /**
     * 某产品某运营商旗下
     * 用户是否可注册
     */
    boolean isSpCanReg(Map<String, Object> map, Integer userId);

    /**
     * 某产品某运营商旗下
     * 用户是否可登录
     */
    boolean isSpCanLogin(Map<String, Object> map, Integer userId);
}
