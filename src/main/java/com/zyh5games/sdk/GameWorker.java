package com.zyh5games.sdk;


import com.zyh5games.entity.ChannelConfig;
import com.zyh5games.entity.GameNew;
import com.zyh5games.entity.Sp;
import com.zyh5games.service.ChannelConfigService;
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
     * 所有渠道信息
     */
    private Map<Integer, Sp> spMap;
    /**
     * 所有游戏信息
     */
    private Map<Integer, GameNew> gameMap;
    /**
     * 游戏对应渠道信息
     */
    private Map<Integer, Map<Integer, ChannelConfig>> channelConfigMapMap;
    @Resource
    private SpService spService;
    @Resource
    private GameNewService gameNewService;
    @Resource
    private ChannelConfigService configService;

    public void init() {
        spMap = new ConcurrentHashMap<>();
        gameMap = new ConcurrentHashMap<>();
        channelConfigMapMap = new ConcurrentHashMap<>();

        log.info("------------------GameWorker init start-------------------");
//        List<Sp> spList = spService.getAllSp(-1);
//        for (Sp sp : spList) {
//            Integer spId = sp.getSpId();
//            spMap.put(spId, sp);
//        }
//
//        Map<String, Object> map = new HashMap<>();
//        List<GameNew> gameList = gameNewService.getGameList(map, -1);
//        for (GameNew gameNew : gameList) {
//            Integer appId = gameNew.getAppId();
//            gameMap.put(appId, gameNew);
//        }
//
//        List<ChannelConfig> configList = configService.selectAll(-1);
//        for (ChannelConfig channelConfig : configList) {
//            Integer appId = channelConfig.getAppId();
//            Integer channelId = channelConfig.getChannelId();
//            if (!channelConfigMapMap.containsKey(appId)) {
//                Map<Integer, ChannelConfig> channelConfigMap = new HashMap<>();
//
//                channelConfigMap.put(channelId, channelConfig);
//                channelConfigMapMap.put(appId, channelConfigMap);
//            } else {
//                Map<Integer, ChannelConfig> channelConfigMap = channelConfigMapMap.get(appId);
//                channelConfigMap.put(channelId, channelConfig);
//            }
//        }

        log.info("------------------GameWorker init finished-------------------");
//        for (Sp sp : spList) {
//            System.out.println("sp[" + sp.getSpId() + "] = " + sp.toString());
//        }
//        for (GameNew gameNew : gameList) {
//            Integer appId = gameNew.getAppId();
//            System.out.println("GameNew[" + appId + "] = " + gameNew.toString());
//        }
//        for (ChannelConfig channelConfig : configList) {
//            System.out.println("ChannelConfig[" + channelConfig.getAppId() + "][" + channelConfig.getChannelId() + "] = " + channelConfig.toString());
//        }
    }

    public Sp getSp(Integer channelId) {
        if (spMap.containsKey(channelId)) {
            return spMap.get(channelId);
        }
        Sp sp = spService.getSp(channelId, -1);
        if (sp != null) {
            spMap.put(channelId, sp);
            return sp;
        }
        return null;
    }

    public GameNew getGameNew(Integer appId) {
        if (gameMap.containsKey(appId)) {
            return gameMap.get(appId);
        }
        GameNew gameNew = gameNewService.selectGame(appId, -1);
        if (gameNew != null) {
            gameMap.put(appId, gameNew);
            return gameNew;
        }
        return null;
    }

    public ChannelConfig getChannelConfig(Integer appId, Integer channelId) {
        if (channelConfigMapMap.containsKey(appId)) {
            Map<Integer, ChannelConfig> channelConfigMap = channelConfigMapMap.get(appId);
            if (channelConfigMap.containsKey(channelId)) {
                return channelConfigMap.get(channelId);
            }
        }
        ChannelConfig channelConfig = configService.selectConfig(appId, channelId, -1);
        if (channelConfig != null) {
            if (!channelConfigMapMap.containsKey(appId)) {
                Map<Integer, ChannelConfig> channelConfigMap = new HashMap<>();

                channelConfigMap.put(channelId, channelConfig);
                channelConfigMapMap.put(appId, channelConfigMap);
            } else {
                Map<Integer, ChannelConfig> channelConfigMap = channelConfigMapMap.get(appId);
                channelConfigMap.put(channelId, channelConfig);
            }
            return channelConfig;
        }

        return null;
    }
}
