package com.ssm.promotion.core.controller;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.entity.Account;
import com.ssm.promotion.core.entity.GameNew;
import com.ssm.promotion.core.entity.GameRole;
import com.ssm.promotion.core.entity.UOrder;
import com.ssm.promotion.core.jedis.jedisRechargeCache;
import com.ssm.promotion.core.sdk.ChannelPayCallback;
import com.ssm.promotion.core.sdk.GameRoleWorker;
import com.ssm.promotion.core.sdk.UOrderManager;
import com.ssm.promotion.core.service.AccountService;
import com.ssm.promotion.core.service.GameNewService;
import com.ssm.promotion.core.util.DateUtil;
import com.ssm.promotion.core.util.FeeUtils;
import com.ssm.promotion.core.util.MD5Util;
import com.ssm.promotion.core.util.ResponseUtil;
import com.ssm.promotion.core.util.enums.OrderState;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/11/27
 */
@Controller
@RequestMapping("/sdkpay")
public class PayCallbackController {
    private static final Logger log = Logger.getLogger(PayCallbackController.class);
    public static String[] keys = {"createRole", "levelUp", "enterGame", "exitGame"};
    @Autowired
    jedisRechargeCache cache;
    @Resource
    GameRoleWorker gameRoleWorker;
    @Resource
    ChannelPayCallback channelPayCallback;
    @Resource
    private UOrderManager orderManager;
    @Resource
    private AccountService accountService;
    @Resource
    private GameNewService gameNewService;

    /**
     * 渠道订单回调地址
     * 支付成功-核对订单 渠道->sdk回调->cp->通知sdk->渠道
     */
    @RequestMapping(value = "/callbackPayInfo/h5_ziwan/{channelId}/{appId}")
    @ResponseBody
    public void callbackPayInfo(@PathVariable("channelId") Integer channelId,
                                @PathVariable("appId") Integer appId,
                                @RequestParam("openid") String openid,
                                @RequestParam("price") String price,
                                @RequestParam("other") String other,
                                @RequestParam("item_id") String item_id,
                                @RequestParam("orderid") String orderid,
                                @RequestParam("sign") String sign,
                                HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("callbackPayInfo:" + channelId);
        System.out.println("callbackPayInfo:" + appId);
        boolean res;

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("openid", openid);
        parameterMap.put("price", price);
        parameterMap.put("other", other);
        parameterMap.put("item_id", item_id);
        parameterMap.put("orderid", orderid);
        parameterMap.put("sign", sign);

        JSONObject channelOrder = new JSONObject();
        JSONObject result = new JSONObject();
        do {
            res = channelPayCallback.loadPayCallback(appId, channelId, parameterMap, channelOrder);
            if (!res) {
                result.put("message", "失败");
                result.put("status", false);
                break;
            }
            String channelOrderId = channelOrder.getString("channelOrderId");
            String cpOrderId = channelOrder.getString("cpOrderId");

            UOrder order = orderManager.getCpOrder(String.valueOf(appId), String.valueOf(channelId), cpOrderId);
            if (order == null) {
                result.put("message", "订单不存在");
                result.put("status", true);
                break;
            }
            long orderId = order.getOrderID();
            Integer zy_Uid = order.getUserID();

            boolean first = false;
            if (order.getState() == OrderState.STATE_OPEN_PAY) {
                // 首次回调 已完成支付 但未发货
                order.setState(OrderState.STATE_PAY_SUCCESS);
                order.setChannelOrderID("");
                order.setRealMoney(Integer.parseInt(FeeUtils.yuanToFen(price)));
                order.setSdkOrderTime(DateUtil.formatDate(System.currentTimeMillis(), DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
                orderManager.updateOrder(order);
                first = true;
            } else if (order.getState() == OrderState.STATE_PAY_SUCCESS) {
                // 多次回调 已完成支付 申请发货未发货
            } else {
                result.put("status", true);
                result.put("message", "订单已完成");
                break;
            }

            // cp请求发货

            GameNew gameNew = gameNewService.selectGame(appId, channelId);
            if (gameNew == null) {
                result.put("status", true);
                result.put("message", "游戏不存在");
                break;
            }

            // cp验证订单并发货
            String notifyUrl = gameNew.getPaybackUrl();

            String md5Key = "";
            StringBuilder param = new StringBuilder();
            param.append("amount").append("=").append(price);
            param.append("&").append("appId").append("=").append(appId);
            param.append("&").append("cpOrderId").append("=").append(cpOrderId);
            param.append("&").append("orderId").append("=").append(orderId);
            param.append("&").append("productId").append("=").append(order.getProductID());
            param.append("&").append("roleId").append("=").append(order.getRoleID());
            param.append("&").append("key").append("=").append(md5Key);

            String sdkSign = MD5Util.md5(param.toString());
            param.append("&").append("sign").append("=").append(sdkSign);

            String url = notifyUrl + param.toString();

            JSONObject cpData = channelPayCallback.httpGet(url);
            log.info("cp支付回调：" + cpData);
            if (cpData.containsKey("code") && cpData.getInteger("code") == 1) {
                order.setState(OrderState.STATE_PAY_FINISHED);
                order.setCompleteTime(DateUtil.formatDate(System.currentTimeMillis(), DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
                orderManager.updateOrder(order);
            }

            //支付成功 更新订单 redis-mysql
            if (first) {
                Account account = accountService.findAccountById(zy_Uid);
                if (account != null) {
                    cache.reqPay(String.valueOf(appId), order.getServerID(), String.valueOf(channelId), zy_Uid, order.getRoleID(), order.getRealMoney(), account.getCreateTime());
                }
            }

        } while (false);
        ResponseUtil.write(response, result);
    }

}
