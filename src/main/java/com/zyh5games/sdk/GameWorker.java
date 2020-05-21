package com.zyh5games.sdk;


import com.zyh5games.entity.Game;
import com.zyh5games.entity.ServerInfo;
import com.zyh5games.entity.Sp;
import com.zyh5games.service.GameService;
import com.zyh5games.service.ServerListService;
import com.zyh5games.service.SpService;
import lombok.Data;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 启动后存储渠道-游戏-区服信息
 *
 * @author song minghua
 * @date 2019/12/5
 */
@Data
public class GameWorker {
    private static final Logger log = Logger.getLogger(GameWorker.class);
    /**
     * 渠道信息
     * <p>
     * key：gmaeid
     * <p>
     * value：Sp
     */
    public static Map<Integer, Game> gameMap;
    /**
     * 渠道信息
     * <p>
     * key：渠道id
     * <p>
     * value：Sp
     */
    public static Map<Integer, Sp> spMap;
    /**
     * 服务器信息
     * <p>
     * key：渠道id
     * <p>
     * value：Map-
     * <p>
     * key:   gameid
     * <p>
     * value: ServerInfo
     */
    public static Map<Integer, Map<Integer, List<ServerInfo>>> serverMap;

    static {
        gameMap = new ConcurrentHashMap<>();
        spMap = new ConcurrentHashMap<>();
        serverMap = new ConcurrentHashMap<>();
    }

    @Resource
    private ServerListService serverService;
    @Resource
    private GameService gameService;
    @Resource
    private SpService spService;

    public void init() {
        log.info("------------------GameWorker init start-------------------");
        Map<String, Object> map = new HashMap<>();
        //1.查询  zy_servername
        List<Game> gameList = gameService.getGameList(map, -1);
        //2.查询  zy_sp
        List<Sp> spList = spService.getAllSp(-1);
        //3.查询  zy_serverlist
        List<ServerInfo> serverList = serverService.getServerList(map, -1);

        for (Game game : gameList) {
            gameMap.put(game.getId(), game);
        }
        for (Sp sp : spList) {
            spMap.put(sp.getSpId(), sp);
        }
        for (ServerInfo serverInfo : serverList) {
            int spId = serverInfo.getSpId();
            int gameId = serverInfo.getGameId();
            int serverId = serverInfo.getServerId();
            if (!serverMap.containsKey(spId)) {
                serverMap.put(spId, new HashMap<>());
            }
            //渠道-游戏
            Map<Integer, List<ServerInfo>> serverInfoMap = serverMap.get(spId);
            if (!serverInfoMap.containsKey(gameId)) {
                serverInfoMap.put(gameId, new ArrayList<>());
            }
            //游戏-区服
            List<ServerInfo> infoList = serverInfoMap.get(gameId);
            Iterator<ServerInfo> it = infoList.iterator();
            while (it.hasNext()) {
                ServerInfo sInfo = it.next();
                if (sInfo.getServerId() == serverId) {
                    infoList.remove(sInfo);
                }
            }
            infoList.add(serverInfo);
        }
        log.info("------------------GameWorker init finished-------------------");
//        System.out.println(gameMap.toString());
//        System.out.println(spMap.toString());
//        System.out.println(serverMap.toString());
    }
}
