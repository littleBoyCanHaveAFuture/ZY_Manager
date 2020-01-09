package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.dao.ServerListDao;
import com.ssm.promotion.core.dao.SpListDao;
import com.ssm.promotion.core.entity.ServerInfo;
import com.ssm.promotion.core.entity.Sp;
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
        return serverListdao.selectServers(map);
    }

    @Override
    public int addServer(ServerInfo server, Integer userId) throws Exception {
        return serverListdao.insertServer(server);
    }

    @Override
    public int updateServer(ServerInfo server, Integer userId) {
        if (server.getGameId() == null || server.getServerId() == null || server.getSpId() == null) {
            return 0;
        }
        return serverListdao.updateServer(server);
    }

    @Override
    public int delServer(Integer id, Integer userId) {
        if (id < 0) {
            return -1;
        }
        return serverListdao.delServer(id);
    }

    @Override
    public Long getTotalServers(Map<String, Object> map, Integer userId) {
        return serverListdao.getTotalServers(map);
    }

    /**
     * @param type 同游戏
     *             1 不同渠道id
     *             2 不同游戏id
     *             3.不同区服id
     * @return id合集
     */
    @Override
    public List<Integer> getDistinctServerInfo(Map<String, Object> map, Integer type, Integer userId) {
        switch (type) {
            case 1:
                return serverListdao.selectDistinctSpId(map);
            case 2:
                return serverListdao.selectDistinctGameId(map);
            case 3:
                return serverListdao.selectDistinctServerId(map);
            default:
                return null;
        }
    }


    @Override
    public boolean isSpCanReg(Map<String, Object> map, Integer userId) {
        String spId = map.get("spId").toString();
        if ("-1".equals(spId)) {
            return false;
        }
//        serverListdao.selectRegStatus(map);
        return true;
    }

    @Override
    public boolean isSpCanLogin(Map<String, Object> map, Integer userId) {
//        serverListdao.selectLoginStatus(map);
        return true;
    }

    @Override
    public boolean existSGS(Map<String, Object> map, Integer userId) {
        return serverListdao.exist(map) > 0;
    }

    @Override
    public String getOpenday(Map<String, Object> map, Integer userId) {
        return serverListdao.selectOpenday(map);
    }

    @Override
    public String selectPrivateKey(Map<String, Object> map, Integer userId) {
        return serverListdao.selectPrivateKey(map);
    }

    @Override
    public String selectPublicKey(Map<String, Object> map, Integer userId) {
        return serverListdao.selectPublicKey(map);
    }

    @Override
    public String selectLoginUrl(Map<String, Object> map, Integer userId) {
        return serverListdao.selectLoginUrl(map);
    }

    @Override
    public String selectSecertKey(Map<String, Object> map, Integer userId) {
        return serverListdao.selectPublicKey(map);
    }



}
