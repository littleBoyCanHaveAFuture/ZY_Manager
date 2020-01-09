package com.ssm.promotion.core.entity;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.util.RandomUtil;
import lombok.Data;

/**
 * 游戏的渠道信息
 *
 * @author Administrator
 */
@Data
public class GameSp {
    /**
     * 主键id
     */
    private Integer id;
    /**
     * 游戏id
     */
    private Integer gameId;
    /**
     * 渠道id
     */
    private Integer spId;
    /**
     * 创建者uid
     */
    private Integer uid;

    //以下是渠道自己的配置 每个渠道可能都不一样
    /**
     * 状态
     * 0 未配置
     * 1 已配置
     */
    private Integer status;
    /**
     * 渠道游戏id
     */
    private Integer appId;
    /**
     * 渠道游戏名称
     */
    private String appName;
    /**
     * 支付回调地址
     */
    private String paybackUrl;
    /**
     * 登录地址
     */
    private String loginUrl;
    /**
     * 登录地址
     */
    private String loginKey;
    /**
     * 登录地址
     */
    private String payKey;
    /**
     * 登录地址
     */
    private String sendKey;

    public GameSp() {
        RandomUtil.rndWord();

    }

    /**
     * 随机32位字符串
     */
    public void initKey() {
        this.loginKey = RandomUtil.rndSecertKey();
        this.payKey = RandomUtil.rndSecertKey();
        this.sendKey = RandomUtil.rndSecertKey();
    }


    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("gameId", this.gameId);
        json.put("spId", this.spId);
        json.put("uid", this.uid);
        json.put("appId", this.appId);
        json.put("appName", this.appName);
        json.put("paybackUrl", this.paybackUrl);
        json.put("loginUrl", this.loginUrl);
        json.put("loginKey", this.loginKey);
        json.put("payKey", this.payKey);
        json.put("sendKey", this.sendKey);
        return json.toString();
    }

}
