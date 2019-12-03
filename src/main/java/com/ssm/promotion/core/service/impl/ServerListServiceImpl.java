package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.dao.ServerListDao;
import com.ssm.promotion.core.entity.ServerInfo;
import com.ssm.promotion.core.service.ServerListService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;


/**
 * @author Administrator
 */
@Service("serverList")
public class ServerListServiceImpl implements ServerListService {

    @Resource
    private ServerListDao serverListdao;

    @Override
    public List<ServerInfo> getServerList(Map<String, Object> map, Integer userId) {
        //  map.entrySet().forEach((k) -> System.out.println("getServerList--->" + k.getKey() + ":" + k.getValue()));

        return serverListdao.selectServers(map);
    }

    @Override
    public int addServer(ServerInfo server, Integer userid) {
        if (server.getGameId() == null || server.getServerId() == null || server.getSpId() == null) {
            return 0;
        }
        return serverListdao.insertServer(server);
    }

    @Override
    public int updateServer(ServerInfo server, Integer userid) {
        if (server.getGameId() == null || server.getServerId() == null || server.getSpId() == null) {
            return 0;
        }
        return serverListdao.updateServer(server);
    }

    @Override
    public int delServer(Integer id, Integer userid) {
        if (id < 0) {
            return -1;
        }
        return serverListdao.delServer(id);
    }

    @Override
    public Long getTotalServers(Map<String, Object> map, Integer userid) {
        return serverListdao.getTotalServers(map);
    }

    /**
     * @param type 同游戏
     *             1 不同区服
     *             2 不同渠道
     */
    @Override
    public List<String> getDistinctServerInfo(Map<String, Object> map, Integer type, Integer userid) {
        List<String> res;
        if (type == 1) {
            res = serverListdao.selectDistinctServerId(map);
        } else {
            res = serverListdao.selectDistinctSpId(map);
        }
        return res;
    }
}
