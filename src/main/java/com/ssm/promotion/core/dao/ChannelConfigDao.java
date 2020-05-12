package com.ssm.promotion.core.dao;

import com.ssm.promotion.core.entity.ChannelConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Administrator
 */
public interface ChannelConfigDao {
    int insertConfig(ChannelConfig config);

    int updateConfig(ChannelConfig config);

    ChannelConfig selectConfig(@Param("appId") Integer appId, @Param("channelId") Integer channelId);

    int deleteConfig(@Param("appId") Integer appId, @Param("channelId") Integer channelId);

    List<Integer> selectGameConfig(@Param("appId") Integer appId);
}
