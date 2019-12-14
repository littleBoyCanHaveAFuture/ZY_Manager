package com.ssm.promotion.core.entity;

import lombok.Data;
import net.sf.json.JSONObject;

/**
 * 订单对象
 */
@Data
public class UOrder {
    private Long orderID;           //订单号
    private Integer appID;          //当前所属游戏ID
    private Integer channelID;      //当前所属渠道ID
    private Integer userID;         //指悦这边对应的用户账号ID
    private String username;        //指悦这边生成的用户名
    private String productID;       //游戏中商品ID
    private String productName;     //游戏中商品名称
    private String productDesc;     //游戏中商品描述
    private Integer money;          //单位 分, 下单时收到的金额，实际充值的金额以这个为准
    private Integer realMoney;      //单位 分，渠道SDK支付回调通知返回的金额，记录，留作查账
    private String currency;        //币种
    private String roleID;          //游戏中角色ID
    private String roleName;        //游戏中角色名称
    private String serverID;        //服务器ID
    private String serverName;      //服务器名称
    private Integer state;          //订单状态
    private String channelOrderID;  //渠道SDK对应的订单号
    private String extension;       //扩展数据
    private String createdTime;       //订单创建时间戳-本地
    private String sdkOrderTime;      //订单交易时间戳-渠道SDK那边
    private String completeTime;      //订单完成时间戳-渠道SDK那边
    private String notifyUrl;       //游戏下单的时候，可以携带notifyUrl过来，作为渠道支付回调时，通知到游戏服务器的地址，没有设置的话，默认走后台游戏管理中配置的固定通知回调地址

    /**
     * 检查参数 是否相符
     */
    public boolean checkParam(String productID, String productName, String productDesc,
                              int money,
                              String roleID, String roleName,
                              String serverID, String serverName, Integer status) {
        boolean res = false;
        do {
            if (!this.productID.equals(productID)) {
                break;
            }
            if (!this.productName.equals(productName)) {
                break;
            }
            if (!this.productDesc.equals(productDesc)) {
                break;
            }

            if (this.money != money) {
                break;
            }

            if (!this.roleID.equals(roleID)) {
                break;
            }
            if (!this.roleName.equals(roleName)) {
                break;
            }

            if (!this.serverID.equals(serverID)) {
                break;
            }
            if (!this.serverName.equals(serverName)) {
                break;
            }
            res = true;
        } while (false);

        return res;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("orderID", orderID + "");
        json.put("appID", appID);


        json.put("appName", "");
        json.put("channelID", channelID);

        json.put("channelName", "");
        json.put("userID", userID);
        json.put("username", username);
        json.put("productID", productID);
        json.put("productName", productName);
        json.put("productDesc", productDesc);
        json.put("money", money);
        json.put("realMoney", money);
        json.put("currency", currency);
        json.put("roleID", roleID);
        json.put("roleName", roleName);
        json.put("serverID", serverID);
        json.put("serverName", serverName);
        json.put("state", state);
        json.put("channelOrderID", channelOrderID);
//        json.put("extension", extension);
        json.put("createdTime", createdTime);
        json.put("sdkOrderTime", sdkOrderTime);
        json.put("completeTime", completeTime);
        json.put("notifyUrl", notifyUrl);
        json.put("platID", channelID);

        return json;
    }
}

