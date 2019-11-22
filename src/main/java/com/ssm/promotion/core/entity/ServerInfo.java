package com.ssm.promotion.core.entity;

import lombok.Data;

import java.util.ArrayList;

@Data
public class ServerInfo {

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
    private String spId;
    /**
     * 渠道id
     */
    private ArrayList<Integer> spIdList;
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

    public ServerInfo() {

    }

    public ServerInfo(Integer id, Integer gameId, Integer serverId, String spId, Integer regState, Integer loginState, String loginUrl) {
        super();
        this.id = id;
        this.gameId = gameId;
        this.serverId = serverId;
        this.spId = spId;
        this.regState = regState;
        this.loginState = loginState;
        this.loginUrl = loginUrl;
        String[] spIdA = spId.split("\\,");
        if (spIdList == null) {
            spIdList = new ArrayList<>();
        }
        for (String ignored : spIdA) {
            spIdList.add(Integer.parseInt(spId));
        }
    }

    @Override
    public String toString() {
        return "ServerListVo [id=" + id + "," +
                "gameId = " + gameId + ", " +
                "serverId =" + serverId + ", " +
                "spId =" + spId + ", " +
                "regState = " + regState + ", " +
                "loginState = " + loginState + ", " +
                "loginUrl = " + loginUrl + ", " +
                "]";
    }
}
