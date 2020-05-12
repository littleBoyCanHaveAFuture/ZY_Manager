package com.ssm.promotion.core.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author Administrator
 */
@Data
public class Sp {
    /**
     * 渠道id
     */
    private Integer spId;
    /**
     * 父渠道
     */
    private Integer parent;
    /**
     * 名称
     */
    private String name;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 分享链接
     */
    private String shareLinkUrl;
    /**
     * icon
     */
    private String iconUrl;
    /**
     * 版本
     */
    private String version;
    /**
     * 简写地址
     */
    private String code;
    /**
     * json 配置
     */
    private String config;

    public Sp() {

    }

    public Sp(Integer spId, Integer parent, String name, Integer state, String shareLinkUrl, String iconUrl, String version) {
        this.spId = spId;
        this.parent = parent;
        this.name = name;
        this.state = state;
        this.shareLinkUrl = shareLinkUrl;
        this.iconUrl = iconUrl;
        this.version = version;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("spId", this.spId);
        json.put("parent", this.parent);
        json.put("name", this.name);
        json.put("state", this.state);
        json.put("shareLinkUrl", this.shareLinkUrl);
        json.put("iconUrl", this.iconUrl);
        json.put("version", this.version);
        return json.toString();
    }

}
