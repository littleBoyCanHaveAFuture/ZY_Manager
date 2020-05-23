package com.zyh5games.service.impl;

import com.zyh5games.dao.ChannelConfigDao;
import com.zyh5games.entity.ChannelConfig;
import com.zyh5games.service.ChannelConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ChannelConfigService")
public class ChannelConfigServiceImpl implements ChannelConfigService {
    @Autowired
    ChannelConfigDao dao;

    @Override
    public int insertConfig(ChannelConfig config, Integer userId) {
        return dao.insertConfig(config);
    }

    @Override
    public int updateConfig(ChannelConfig config, Integer userId) {
        return dao.updateConfig(config);
    }

    @Override
    public ChannelConfig selectConfig(Integer appId, Integer channelId, Integer userId) {
        ChannelConfig c = dao.selectConfig(appId, channelId);
        return c;
    }

    @Override
    public int deleteConfig(Integer appId, Integer channelId, Integer userId) {
        return dao.deleteConfig(appId, channelId);
    }

    @Override
    public List<Integer> selectGameConfig(Integer appId, Integer userId) {
        return dao.selectGameConfig(appId);
    }

    @Override
    public List<ChannelConfig> selectAll(Integer userId) {
        return dao.selectAll();
    }

    @Override
    public List<ChannelConfig> selectChannelConfig(Integer channelId, Integer userId) {
        return dao.selectChannelConfig(channelId);
    }
}


