package com.zyh5games.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author Administrator
 */
@Data
public class WebGameSp {
    /**
     * 渠道id
     */
    private Integer appid;
    /**
     * icon
     */
    private String icon;
    /**
     * 名称
     */
    private String name;
    /**
     * 渠道id
     */
    private Integer channelid;
    /**
     * 版本
     */
    private String version;
    /**
     * 状态
     * 0 未添加
     * 1 已添加
     */
    private Integer status;
    /**
     * 创建者id
     */
    private Integer uid;
    /**
     * 状态
     * 0 未配置
     * 1 已配置
     */
    private Integer configStatus;

    /**
     * status = 1
     * zy_game_sp 的主键id
     */
    private Integer id;

    public WebGameSp() {

    }


    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("appid", this.appid);
        json.put("icon", this.icon);
        json.put("name", this.name);
        json.put("channelid", this.channelid);
        json.put("version", this.version);
        json.put("status", this.status);
        json.put("uid", this.uid);
        return json.toString();
    }
}
