package com.zyh5games.service;

import com.zyh5games.entity.ChannelConfig;

import java.util.List;

/**
 * @author Administrator
 */
public interface ChannelConfigService {
    int insertConfig(ChannelConfig config, Integer userId);

    int updateConfig(ChannelConfig config, Integer userId);

    ChannelConfig selectConfig(Integer appId, Integer channelId, Integer userId);

    int deleteConfig(Integer appId, Integer channelId, Integer userId);

    List<Integer> selectGameConfig(Integer appId, Integer userId);

    List<ChannelConfig> selectAll(Integer userId);

    List<ChannelConfig> selectChannelConfig(Integer channelId, Integer userId);
}
