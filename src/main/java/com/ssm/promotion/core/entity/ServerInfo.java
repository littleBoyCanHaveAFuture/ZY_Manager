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
    private Integer spId;
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

    /**
     * 开服天数
     */
    private String openday;
    /**
     * RSA2 公钥
     */
    private String pubKey;
    /**
     * RSA2 私钥
     */
    private String priKey;
    /**
     * md5 秘钥
     */
    private String secertKey;

    public ServerInfo() {

    }

    public ServerInfo(Integer id, Integer gameId, Integer serverId, Integer spId, Integer regState, Integer loginState,
                      String loginUrl, String openday,
                      String pubKey, String priKey, String secertKey) {
        super();
        this.id = id;
        this.gameId = gameId;
        this.serverId = serverId;
        this.spId = spId;
        this.regState = regState;
        this.loginState = loginState;
        this.loginUrl = loginUrl;
        this.openday = openday;
        this.pubKey = pubKey;
        this.priKey = priKey;
        this.secertKey = secertKey;
        if (spIdList == null) {
            spIdList = new ArrayList<>();
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
                "openday = " + openday + ", " +
                "]";
    }
}
