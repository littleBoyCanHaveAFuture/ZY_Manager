package com.zyh5games.sdk;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.entity.ChannelConfig;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.service.ChannelConfigService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2020/5/21
 */
public class ChannelHandler {
    private static final Logger log = Logger.getLogger(ChannelHandler.class);
    Map<Integer, Map<Integer, JSONObject>> appConfigMap = new HashMap<>();
    /**
     * 关键功能 Spring 会自动将 EntStrategy 接口的类注入到这个Map中
     */
    @Autowired
    private Map<String, BaseChannel> channelMap;

    @Resource
    private ChannelConfigService configService;

    public BaseChannel getBy(String entNum) {
        return channelMap.get(entNum);
    }

    public void print() {
        System.out.println("===== BaseChannel extends Map =====");
        channelMap.forEach((name, impl) -> {
            System.out.println(name + "->channelData" + ":" + impl.channelLib());
        });
    }

    /**
     * 加载渠道的游戏id
     */
    public void loadChannelApp() {
        List<ChannelConfig> gameList = configService.selectAll(-1);
        for (ChannelConfig channelConfig : gameList) {
            Integer appId = channelConfig.getAppId();
            Integer channelId = channelConfig.getChannelId();
            String config = channelConfig.getConfigKey();
            if (config == null || config.isEmpty()) {
                continue;
            }
            JSONObject jsonObject = JSONObject.parseObject(config);

            if (!appConfigMap.containsKey(channelId)) {
                Map<Integer, JSONObject> configMap = new HashMap<>();
                configMap.put(appId, jsonObject);
                appConfigMap.put(channelId, configMap);
            } else {
                Map<Integer, JSONObject> configMap = appConfigMap.get(channelId);
                if (!configMap.containsKey(appId)) {
                    configMap.put(appId, jsonObject);
                }
            }


        }
        System.out.println(configService.toString());
    }

    @PostConstruct
    public void init() {
        loadChannelApp();
        print();
    }


    public BaseChannel getChannel(Integer channelId) {
        BaseChannel channel = this.channelMap.get(String.valueOf(channelId));
        if (channel.getConfigMap() == null || channel.getConfigMap().size() == 0) {
            channel.setConfigMap(appConfigMap.get(channelId));
        } else {
            List<ChannelConfig> channelConfigList = configService.selectChannelConfig(channelId, -1);
            for (ChannelConfig config : channelConfigList) {
                Integer appId = config.getAppId();
                String configKey = config.getConfigKey();
                if (configKey != null && !configKey.isEmpty()) {
                    JSONObject jsonObject = JSONObject.parseObject(configKey);
                    if (channel.getConfigMap().containsKey(appId)) {
                        channel.getConfigMap().replace(appId, jsonObject);
                    } else {
                        channel.getConfigMap().put(appId, jsonObject);
                    }
                    System.out.println("[" + channelId + "]getChannel[" + appId + "] = " + jsonObject);
                }
            }
        }
        return channel;
    }

}
