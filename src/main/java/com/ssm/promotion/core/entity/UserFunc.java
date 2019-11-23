package com.ssm.promotion.core.entity;

import lombok.Data;

@Data
public class UserFunc {
    /**
     * 主键
     */
    private String id;
    /**
     * 父节点
     */
    private Integer parent;
    /**
     * 名称
     */
    private String name;
    /**
     * 页面id
     */
    private String webId;
    /**
     * 页面url
     */
    private String url;

    @Override
    public String toString() {
        return "User [" + "id=" + id +
                ", parent=" + parent +
                ", name=" + name +
                ", webId=" + webId +
                ", url=" + url +
                "]";
    }
}
