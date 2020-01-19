package com.ssm.promotion.core.sdk;

import com.ssm.promotion.core.dao.UOrderDao;
import com.ssm.promotion.core.entity.UOrder;
import com.ssm.promotion.core.util.DateUtil;
import com.ssm.promotion.core.util.EncryptUtils;
import com.ssm.promotion.core.util.MD5Util;
import com.ssm.promotion.core.util.enums.OrderState;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 */
@Service("orderManager")
public class UOrderManager {
    private static final Logger log = Logger.getLogger(UOrderManager.class);
    @Resource
    LoginIdGenerator loginIdGenerator;
    @Resource
    private UOrderDao orderDao;

    public static boolean isSignOK(int accountId, int channelId, String channelUid, int appID, String channelOrderID,
                                   String productID, String productName, String productDesc, int money,
                                   String roleID, String roleName, String roleLevel,
                                   int serverID, String serverName,
                                   int realMoney, String completeTime, String sdkOrderTime,
                                   int status, String notifyUrl,
                                   String signType,
                                   String sign, String gameKey) throws UnsupportedEncodingException {
        if (!signType.equals(MD5Util.KEY_ALGORITHM)) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("accountID=").append(accountId).append("&")
                .append("channelID=").append(channelId).append("&")
                .append("channelUid=").append(channelUid == null ? "" : channelUid).append("&")
                .append("appID=").append(appID).append("&")
                .append("channelOrderID=").append(channelOrderID == null ? "" : channelOrderID).append("&")

                .append("productID=").append(productID).append("&")
                .append("productName=").append(productName).append("&")
                .append("productDesc=").append(productDesc).append("&")
                .append("money=").append(money).append("&")

                .append("roleID=").append(roleID).append("&")
                .append("roleName=").append(roleName == null ? "" : roleName).append("&")
                .append("roleLevel=").append(roleLevel == null ? "" : roleLevel).append("&")

                .append("serverID=").append(serverID).append("&")
                .append("serverName=").append(serverName == null ? "" : serverName).append("&")

                .append("realMoney=").append(realMoney).append("&")
                .append("completeTime=").append(completeTime).append("&")
                .append("sdkOrderTime=").append(sdkOrderTime == null ? "" : sdkOrderTime).append("&")

                .append("status=").append(status).append("&")
                .append("notifyUrl=").append(notifyUrl == null ? "" : notifyUrl)
                .append("&").append(gameKey);
        System.out.println("sign\n" + sb.toString());

        String encoded = URLEncoder.encode(sb.toString(), "UTF-8");
        String newSign = EncryptUtils.md5(encoded).toLowerCase();

        System.out.println("Md5 sign recv  \n:" + sign);
        System.out.println("Md5 sign server\n:" + newSign);

        return newSign.equals(sign);
    }

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

    public List<UOrder> getUOrderById(String id) {
        return orderDao.getOrderById(id);
    }

    public Long getTotalUorders(Map<String, Object> map) {
        return orderDao.getTotalUorders(map);
    }

    /**
     * 保存
     * 生成订单(首先得没有该订单）
     */
    public UOrder generateOrder(int accountId, int channelId, String channelUid, int appID, String channelOrderID,
                                String productID, String productName, String productDesc, int money,
                                String roleID, String roleName, String roleLevel,
                                int serverID, String serverName,
                                int realMoney, String completeTime, String sdkOrderTime,
                                int status, String notifyUrl) throws Exception {
        if (status < OrderState.STATE_OPEN_SHOP || status > OrderState.STATE_PAY_SUPPLEMENT) {
            return null;
        }
        String completetiems = "0";
        if (status == OrderState.STATE_PAY_SUCCESS || status == OrderState.STATE_PAY_FINISHED || status == OrderState.STATE_PAY_SUPPLEMENT) {
            if (status != OrderState.STATE_PAY_SUCCESS) {
                completetiems = DateUtil.formatDate(Long.parseLong(completeTime), DateUtil.FORMAT_YYYY_MMDD_HHmmSS);
            }
        }

        UOrder order = new UOrder();

        order.setOrderID(loginIdGenerator.getRandomId());
        order.setAppID(appID);
        order.setChannelID(channelId);
        order.setChannelOrderID(channelOrderID);

//        order.setCompleteTime(completetiems);

        order.setCreatedTime(DateUtil.formatDate(System.currentTimeMillis(), DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
        order.setCurrency("RMB");
        order.setExtension("");
        order.setMoney(money);
        order.setNotifyUrl(notifyUrl);

        order.setProductDesc(productDesc);
        order.setProductID(productID);
        order.setProductName(productName);
        order.setRealMoney(realMoney);
        order.setSdkOrderTime(DateUtil.formatDate(Long.parseLong(sdkOrderTime), DateUtil.FORMAT_YYYY_MMDD_HHmmSS));

        order.setServerID(String.valueOf(serverID));
        order.setServerName(serverName);
        order.setState(status);
        order.setUserID(accountId);
        order.setUsername("");

        order.setRoleID(roleID);
        order.setRoleName(roleName);

        System.out.println("Order:\n" + order.toJSON());

        int res = orderDao.save(order);
        System.out.println("saveOrder:" + res);
        return order;
    }
}
