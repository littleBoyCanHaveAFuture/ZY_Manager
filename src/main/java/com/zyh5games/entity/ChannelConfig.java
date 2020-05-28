package com.zyh5games.entity;

import lombok.Data;

/**
 * @author song minghua
 */
@Data
public class ChannelConfig {
    /**
     * id
     */
    private Integer id;
    /**
     * 游戏id
     */
    private Integer appId;
    /**
     * 渠道id
     */
    private Integer channelId;
    /**
     * 支付回调地址
     */
    private String channelCallbackUrl;
    /**
     * 渠道json参数
     */
    private String configKey;
    /**
     * 登录地址
     */
    private String h5Url;
//非 zy_channel的参数

    /**
     * 渠道sdk名称
     */
    private String channelSdkName;
    /**
     * 渠道简写,支付地址
     */
    private String channelSdkCode;
    /**
     * 渠道json参数
     */
    private String channelConfigKey;

}
