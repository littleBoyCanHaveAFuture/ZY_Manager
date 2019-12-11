package com.ssm.promotion.core.sdk;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.dao.UOrderDao;
import com.ssm.promotion.core.entity.GameRole;
import com.ssm.promotion.core.entity.UOrder;
import com.ssm.promotion.core.util.EncryptUtils;
import com.ssm.promotion.core.util.StringUtils;
import com.ssm.promotion.core.util.enums.OrderState;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service("orderManager")
public class UOrderManager {
    private static final Logger log = Logger.getLogger(UOrderManager.class);
    @Resource
    LoginIdGenerator loginIdGenerator;
    @Resource
    private UOrderDao orderDao;

    /**
     * @param appId          游戏id
     * @param channelID      渠道id
     * @param channelOrderID 渠道订单id
     */
    public UOrder getOrder(String appId, String channelID, String channelOrderID) {
        List<UOrder> orderList = orderDao.get(appId, channelID, channelOrderID);
        if (orderList.size() >= 1) {
            return orderList.get(0);
        }
        return null;
    }

    /**
     * 只能更新
     * 1.realMoney
     * 2.state
     * 3.completeTime
     */
    public void updateOrder(UOrder order) {
        orderDao.update(order);
    }

    public void deleteOrder(UOrder order) {
        orderDao.delete(order);
    }

    public List<UOrder> getUOrderList(Map<String, Object> map) {
        return orderDao.getUOrderList(map);
    }

    public Long getTotalUorders(Map<String, Object> map) {
        return orderDao.getTotalUorders(map);
    }

    /**
     * 保存
     * 生成订单(首先得没有该订单）
     */
    public UOrder generateOrder(GameRole role,
                                String channelOrderID,
                                String extension, int money, String notifyUrl,
                                String productDesc, String productID, String productName,
                                String serverID, String serverName, Integer status,
                                String roleID, String roleName) throws Exception {
        if (status < OrderState.STATE_OPEN_SHOP || status > OrderState.STATE_PAY_SUPPLEMENT) {
            return null;
        }
        Integer realMoney = null;
        Long completeTime = null;
        Long sdkOrderTime;
        if (extension == null) {
            System.out.println("extension is null");
            return null;
        }
        extension = StringUtils.urlcodeToStr(extension);
        JSONObject object = JSONObject.parseObject(extension);

        sdkOrderTime = object.containsKey("sdkOrderTime") ? object.getLong("sdkOrderTime") : null;

        if (status == OrderState.STATE_PAY_SUCCESS || status == OrderState.STATE_PAY_FINISHED || status == OrderState.STATE_PAY_SUPPLEMENT) {
            realMoney = object.containsKey("realMoney") ? object.getInteger("realMoney") : null;

            if (status != OrderState.STATE_PAY_SUCCESS) {
                completeTime = object.containsKey("completeTime") ? object.getLong("completeTime") : null;
            }
        }

        UOrder order = new UOrder();

        order.setOrderID(loginIdGenerator.getRandomId());
        order.setAppID(Integer.parseInt(role.getGameId()));
        order.setChannelID(Integer.parseInt(role.getChannelId()));
        order.setChannelOrderID(channelOrderID);
        order.setCompleteTime(completeTime != null ? new Date(completeTime) : null);


        order.setCreatedTime(new Date());
        order.setCurrency("RMB");
        order.setExtension(extension);
        order.setMoney(money);
        order.setNotifyUrl(notifyUrl);

        order.setProductDesc(productDesc);
        order.setProductID(productID);
        order.setProductName(productName);
        order.setRealMoney(realMoney);
        order.setSdkOrderTime(sdkOrderTime != null ? new Date(sdkOrderTime) : null);

        order.setServerID(serverID);
        order.setServerName(serverName);
        order.setState(status);
        order.setUserID(role.getAccountId());
        order.setUsername("");

        order.setRoleID(roleID);
        order.setRoleName(roleName);

        int res = orderDao.save(order);
        System.out.println("saveOrder:" + res);
        return order;
    }

    //    public UOrder generateOrder(UChannelMaster master, String userID, int money, String productID, String productName,
//                                String productDesc, String roleID, String roleName, String serverID, String serverName,
//                                String extension, String notifyUrl) {
//
//        UOrder order = new UOrder();
//        order.setOrderID(IDGenerator.getInstance().nextOrderID());
//        order.setAppID(0);
//        order.setChannelID(master.getMasterID());
//        order.setMoney(money);
//        order.setProductID(productID);
//        order.setProductName(productName);
//        order.setProductDesc(productDesc);
//        order.setCurrency("RMB");
//        order.setUserID(0);
//        order.setUsername(userID);
//        order.setExtension(extension);
//        order.setState(PayState.STATE_PAYING);
//        order.setChannelOrderID("");
//        order.setRoleID(roleID);
//        order.setRoleName(roleName);
//        order.setServerID(serverID);
//        order.setServerName(serverName);
//        order.setCreatedTime(new Date());
//        order.setNotifyUrl(notifyUrl);
//
//        orderDao.save(order);
//
//        return order;
//    }
    public boolean isSignOK(int accountID,
                            String channelOrderID,
                            String productID,
                            String productName,
                            String productDesc,
                            int money,
                            String roleID,
                            String roleName,
                            String roleLevel,
                            String serverID,
                            String serverName,
                            String extension,
                            Integer status,
                            String notifyUrl,
                            String signType,
                            String sign) throws UnsupportedEncodingException {

        StringBuilder sb = new StringBuilder();
        sb.append("accountID=").append(accountID).append("&")
                .append("channelOrderID=").append(channelOrderID == null ? "" : channelOrderID).append("&")
                .append("productID=").append(productID == null ? "" : productID).append("&")
                .append("productName=").append(productName == null ? "" : productName).append("&")
                .append("productDesc=").append(productDesc == null ? "" : productDesc).append("&")
                .append("money=").append(money).append("&")
                .append("roleID=").append(roleID == null ? "" : roleID).append("&")
                .append("roleName=").append(roleName == null ? "" : roleName).append("&")
                .append("roleLevel=").append(roleLevel == null ? "" : roleLevel).append("&")
                .append("serverID=").append(serverID == null ? "" : serverID).append("&")
                .append("serverName=").append(serverName == null ? "" : serverName).append("&")
                .append("extension=").append(extension == null ? "" : extension)
                .append("status=").append(status == null ? "" : status);

        if (!StringUtils.isEmpty(notifyUrl)) {
            sb.append("&notifyUrl=").append(notifyUrl);
        }

//        if ("rsa".equalsIgnoreCase(signType)) {
//            String encoded = URLEncoder.encode(sb.toString(), "UTF-8");
//
//            log.debug("The encoded getOrderID sign is " + encoded);
//            log.debug("The getOrderID sign is " + sign);
//            String publickey = "";
//            return RSAUtils.verify(encoded, sign, publickey, "UTF-8", "SHA1withRSA");
//        }
        String appkey = "";
        //md5 sign
        sb.append(appkey);

        log.debug("the appkey:" + appkey);

        String encoded = URLEncoder.encode(sb.toString(), "UTF-8");

        log.debug("The encoded getOrderID sign is " + encoded);
        log.debug("The getOrderID sign is " + sign);
        String newSign = EncryptUtils.md5(encoded);

        log.debug("the sign now is md5; newSign:" + newSign);
        return newSign.toLowerCase().equals(sign);
    }
}
