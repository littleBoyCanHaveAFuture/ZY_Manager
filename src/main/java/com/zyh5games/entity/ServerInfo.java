package com.zyh5games.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author song minghua
 */
@Data
public class ServerInfo {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 游戏id
     */
    private Integer gameId;
    /**
     * 服务器id
     */
    private Integer serverId;
    /**
     * 渠道id
     */
    private Integer spId;
    /**
     * 注册开关
     */
    private Integer regState;
    /**
     * 登录开关
     */
    private Integer loginState;
    /**
     * 登陆地址
     */
    private String loginUrl;
    /**
     * 游戏名称
     */
    private String gamename;
    /**
     * 开服天数
     */
    private String openday;

    public ServerInfo() {

    }

    public ServerInfo(Integer id, Integer gameId, Integer serverId, Integer spId, Integer regState, Integer loginState,
                      String loginUrl, String openday) {
        this.id = id;
        this.gameId = gameId;
        this.serverId = serverId;
        this.spId = spId;
        this.regState = regState;
        this.loginState = loginState;
        this.loginUrl = loginUrl;
        this.openday = openday;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        json.put("id", this.id);
        json.put("gameId", this.gameId);
        json.put("serverId", this.serverId);
        json.put("spId", this.spId);
        json.put("regState", this.regState);
        json.put("loginState", this.loginState);
        json.put("loginUrl", this.loginUrl);
        json.put("openday", this.openday);

        return json.toString();

    }
}
