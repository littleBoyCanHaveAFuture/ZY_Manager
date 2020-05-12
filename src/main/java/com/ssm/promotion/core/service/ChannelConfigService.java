package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.ChannelConfig;
import com.ssm.promotion.core.entity.GameNew;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface ChannelConfigService {
    int insertConfig(ChannelConfig config,Integer userId);

    int updateConfig(ChannelConfig config,Integer userId);

    ChannelConfig selectConfig(Integer appId, Integer channelId,Integer userId);

    int deleteConfig(Integer appId, Integer channelId,Integer userId);
}
