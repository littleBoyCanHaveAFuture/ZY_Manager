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

    public Sp() {

    }

    public Sp(Integer spId, Integer parent, String name, Integer state, String shareLinkUrl) {
        this.spId = spId;
        this.parent = parent;
        this.name = name;
        this.state = state;
        this.shareLinkUrl = shareLinkUrl;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("spId", this.spId);
        json.put("parent", this.parent);
        json.put("name", this.name);
        json.put("state", this.state);
        json.put("shareLinkUrl", this.shareLinkUrl);
        return json.toString();
    }

}
