package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.ServerInfo;

import java.util.List;
import java.util.Map;

public interface ServerListService {

    /**
     * 获取 服务器列表
     */

    public List<ServerInfo> getServerList(Map<String, Object> map,Integer userid);

    /**
     * 添加服务器
     */
    public int addServer(ServerInfo server,Integer userid);

    /**
     * 修改服务器
     */
    public int updateServer(ServerInfo server,Integer userid);

    /**
     * 删除服务器
     */
    public int delServer(Integer id,Integer userid);

    /**
     * 服务器总数
     */
    Long getTotalServers(Map<String, Object> map,Integer userid);
}
