package com.zyh5games.sdk;


import com.zyh5games.entity.GameNew;
import com.zyh5games.entity.Sp;
import com.zyh5games.service.GameNewService;
import com.zyh5games.service.SpService;
import lombok.Data;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * 所有游戏信息
     */
    public static Map<Integer, GameNew> gameMap;
    /**
     *
     */
    public static Map<Integer, Sp> spMap;

    static {
        gameMap = new ConcurrentHashMap<>();
        spMap = new ConcurrentHashMap<>();
    }

    @Resource
    private GameNewService gameNewService;
    @Resource
    private SpService spService;


    public void init() {
        log.info("------------------GameWorker init start-------------------");
        Map<String, Object> map = new HashMap<>();
        List<GameNew> gameList = gameNewService.getGameList(map, -1);
        for (GameNew gameNew : gameList) {
            Integer appId = gameNew.getAppId();
            gameMap.put(appId, gameNew);
        }

        log.info("------------------GameWorker init finished-------------------");

    }
}
