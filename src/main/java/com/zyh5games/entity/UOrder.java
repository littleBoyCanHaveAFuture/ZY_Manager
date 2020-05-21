package com.zyh5games.entity;

import lombok.Data;
import net.sf.json.JSONObject;

/**
 * 订单对象
 *
 * @author Administrator
 */
@Data
public class UOrder {
    /**
     * 订单号
     */
    private Long orderID;
    /**
     * 当前所属游戏ID
     */
    private Integer appID;
    /**
     * 当前所属渠道ID
     */
    private Integer channelID;
    /**
     * 指悦这边对应的用户账号ID
     */
    private Integer userID;
    /**
     * 指悦这边生成的用户名
     */
    private String username;
    /**
     * 游戏中商品ID
     */
    private String productID;
    /**
     * 游戏中商品名称
     */
    private String productName;
    /**
     * 游戏中商品描述
     */
    private String productDesc;
    /**
     * 单位 分, 下单时收到的金额，实际充值的金额以这个为准
     */
    private Integer money;
    /**
     * 单位 分，渠道SDK支付回调通知返回的金额，记录，留作查账
     */
    private Integer realMoney;
    /**
     * 币种
     */
    private String currency;
    /**
     * 游戏中角色ID
     */
    private String roleID;
    /**
     * 游戏中角色名称
     */
    private String roleName;
    /**
     * 服务器ID
     */
    private String serverID;
    /**
     * 服务器名称
     */
    private String serverName;
    /**
     * 订单状态
     */
    private Integer state;
    /**
     * 渠道SDK对应的订单号
     */
    private String channelOrderID;
    /**
     * 扩展数据
     */
    private String extension;
    /**
     * 订单创建时间戳-本地
     */
    private String createdTime;
    /**
     * 订单交易时间戳-渠道SDK那边
     */
    private String sdkOrderTime;
    /**
     * 订单完成时间戳-渠道SDK那边
     */
    private String completeTime;
    /**
     * 游戏下单的时候，可以携带notifyUrl过来，作为渠道支付回调时，通知到游戏服务器的地址，没有设置的话，默认走后台游戏管理中配置的固定通知回调地址
     */
    private String notifyUrl;
    //给js显示
    private String JsOrderId;
    //cp订
    private String cpOrderId;

    public void setJsOrder() {
        JsOrderId = String.valueOf(orderID);
    }

    /**
     * 检查参数 是否相符 或者合法
     */
    public boolean checkParam(String productID, String productName, String productDesc,
                              int money,
                              String roleID, String roleName,
                              int serverID, String serverName, int status, String sdkOrderTime) {
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

            if (!this.serverID.equals(String.valueOf(serverID))) {
                break;
            }
            if (!this.serverName.equals(serverName)) {
                break;
            }
            if (!this.sdkOrderTime.equals(sdkOrderTime)) {
                break;
            }
            if (this.state >= status) {
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

