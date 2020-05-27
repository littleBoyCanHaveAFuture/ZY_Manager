package com.zyh5games.controller;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.entity.Account;
import com.zyh5games.entity.GameNew;
import com.zyh5games.entity.UOrder;
import com.zyh5games.jedis.JedisRechargeCache;
import com.zyh5games.sdk.ChannelHandler;
import com.zyh5games.sdk.HttpService;
import com.zyh5games.sdk.UOrderManager;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.service.AccountService;
import com.zyh5games.service.GameNewService;
import com.zyh5games.util.DateUtil;
import com.zyh5games.util.FeeUtils;
import com.zyh5games.util.MD5Util;
import com.zyh5games.util.ResponseUtil;
import com.zyh5games.util.enums.OrderState;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
@RequestMapping("/sdkPay")
public class PayCallbackController {
    private static final Logger log = Logger.getLogger(PayCallbackController.class);
    public static String[] keys = {"createRole", "levelUp", "enterGame", "exitGame"};
    @Autowired
    JedisRechargeCache cache;
    @Resource
    ChannelHandler channelHandler;
    @Resource
    private UOrderManager orderManager;
    @Resource
    private AccountService accountService;
    @Resource
    private GameNewService gameNewService;
    @Resource
    private HttpService httpService;

    /**
     * 通知cp发货
     * 1.指悦支付 在 ijpay--master里面发货 这里不处理
     * 2.渠道在这里处理
     */
    public boolean notifyToCp(boolean first, GameNew gameNew, UOrder order,
                              String price, String cpOrderId, Integer channelId) throws Exception {
        log.info("notifyToCp");
        boolean res = true;
        do {
            // cp验证订单并发货
            String notifyUrl = gameNew.getPaybackUrl();
            String md5Key = gameNew.getCallbacKey();

            Integer appId = gameNew.getAppId();
            Integer zhiyueUid = order.getUserID();
            long orderId = order.getOrderID();

            StringBuilder param = new StringBuilder();
            param.append("amount").append("=").append(price);
            param.append("&").append("appId").append("=").append(appId);
            param.append("&").append("cpOrderId").append("=").append(cpOrderId);
            param.append("&").append("orderId").append("=").append(orderId);
            param.append("&").append("productId").append("=").append(order.getProductID());
            param.append("&").append("roleId").append("=").append(order.getRoleID());

            String sdkSign = MD5Util.md5(param.toString() + md5Key);
            param.append("&").append("sign").append("=").append(sdkSign);

            String url = notifyUrl + param.toString();

            JSONObject cpData = httpService.httpGet(url);
            log.info("cp支付回调：" + cpData);
            if (cpData.containsKey("code") && cpData.getInteger("code") == 1) {
                order.setState(OrderState.STATE_PAY_FINISHED);
                order.setCompleteTime(DateUtil.formatDate(System.currentTimeMillis(), DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
                orderManager.updateOrder(order);
            } else {
                res = false;
                break;
            }

            //支付成功 更新订单 redis-mysql
            if (first) {
                Account account = accountService.findAccountById(zhiyueUid);
                if (account != null) {
                    cache.reqPay(String.valueOf(appId), order.getServerID(), String.valueOf(channelId), zhiyueUid, order.getRoleID(), order.getRealMoney(), account.getCreateTime());
                }
            }
        } while (false);

        return res;
    }

    /**
     * 渠道订单回调地址
     * 支付成功-核对订单 渠道->sdk回调->cp->通知sdk->渠道
     *
     * @param openid  渠道玩家唯一标示
     * @param price   商品金额
     * @param other   其他信息
     * @param item_id 商品id
     * @param orderid 订单id
     * @param sign    签名
     */
    @RequestMapping(value = "/callbackPayInfo/h5_ziwan/{channelId}/{appId}")
    @ResponseBody
    public void h5_ziwan(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                         @RequestParam("openid") String openid,
                         @RequestParam("price") String price,
                         @RequestParam("other") String other,
                         @RequestParam("item_id") String item_id,
                         @RequestParam("orderid") String orderid,
                         @RequestParam("sign") String sign,
                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("h5_ziwan start " + "channelId=" + channelId + " appId=" + appId);
        System.out.println("callbackPayInfo:" + channelId);
        System.out.println("callbackPayInfo:" + appId);

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("openid", openid);
        parameterMap.put("price", price);
        parameterMap.put("other", other);
        parameterMap.put("item_id", item_id);
        parameterMap.put("orderid", orderid);
        parameterMap.put("sign", sign);
        System.out.println("parameterMap =" + parameterMap.toString());

        JSONObject channelOrder = new JSONObject();
        boolean result = true;
        do {
            BaseChannel channelSerivce = channelHandler.getChannel(channelId);

            boolean checkOrder = channelSerivce.channelPayCallback(appId, parameterMap, channelOrder);
            if (!checkOrder) {
                result = false;
                break;
            }
            String channelOrderId = channelOrder.getString("channelOrderId");
            String cpOrderId = channelOrder.getString("cpOrderId");

            UOrder order = orderManager.getCpOrder(String.valueOf(appId), String.valueOf(channelId), cpOrderId);
            if (order == null) {
                log.info("订单为空");
                result = false;
                break;
            }
            long orderId = order.getOrderID();
            Integer zhiyueUid = order.getUserID();

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
                break;
            }

            // cp请求发货

            GameNew gameNew = gameNewService.selectGame(appId, -1);
            if (gameNew == null) {
                result = false;
                break;
            }

            result = notifyToCp(first, gameNew, order, price, cpOrderId, channelId);

        } while (false);
        ResponseUtil.write(response, result ? "success" : "fail");
        log.info("h5_ziwan end " + result);
    }

    /**
     * 渠道订单回调地址
     * 支付成功-核对订单 渠道->sdk回调->cp->通知sdk->渠道
     * status	    String	是	是	订单状态。“success”为支付成功
     * cpOrderId	String	是	是	游戏方（cp）游戏订单号
     * orderId	    String	是	是	我方订单ID
     * uid	        string	是	是	用户UID (唯一) 我方用户的UID
     * userName	    string	是	是	用户名
     * money		        是	是	商品价格(元) 例：1.00
     * gameId	    String	是	是	游戏的id
     * goodsId	    string	是	是	商品ID 没有的话。可以写1
     * goodsName	string	是	是	商品名,如：游戏币
     * server	    string	是	是	支付时的游戏服
     * role	        string	是	是	支付时角色信息,支付完后回传
     * time	        int	是	是	当前时间unix时间戳
     * ext	        string	否	否	额外透传参数(原样返回)
     * signType	    string	是	否	固定值 md5
     * sign	        string	是	否	加密串
     */
    @RequestMapping(value = "/callbackPayInfo/h5_baijia/{channelId}/{appId}")
    @ResponseBody
    public void h5_baijia(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                          @RequestParam("status") String status,
                          @RequestParam("cpOrderId") String cpOrderId,
                          @RequestParam("orderId") String orderId,
                          @RequestParam("uid") String uid,
                          @RequestParam("userName") String userName,
                          @RequestParam("money") String money,
                          @RequestParam("gameId") String gameId,
                          @RequestParam("goodsId") String goodsId,
                          @RequestParam("goodsName") String goodsName,
                          @RequestParam("server") String server,
                          @RequestParam("role") String role,
                          @RequestParam("time") String time,
                          @RequestParam("ext") String ext,
                          @RequestParam("signType") String signType,
                          @RequestParam("sign") String sign,

                          HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("callbackPayInfo:" + channelId);
        System.out.println("callbackPayInfo:" + appId);

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("status", status);
        parameterMap.put("cpOrderId", cpOrderId);
        parameterMap.put("orderId", orderId);
        parameterMap.put("uid", uid);
        parameterMap.put("userName", userName);
        parameterMap.put("money", money);

        parameterMap.put("gameId", gameId);
        parameterMap.put("goodsId", goodsId);
        parameterMap.put("goodsName", goodsName);
        parameterMap.put("server", server);
        parameterMap.put("role", role);
        parameterMap.put("time", time);
        parameterMap.put("ext", ext);
        parameterMap.put("signType", signType);

        parameterMap.put("sign", sign);

        System.out.println("h5_baijia parameterMap =" + parameterMap.toString());

        JSONObject channelOrder = new JSONObject();
        boolean result = true;
        do {
            BaseChannel channelSerivce = channelHandler.getChannel(channelId);

            boolean checkOrder = channelSerivce.channelPayCallback(appId, parameterMap, channelOrder);
            if (!checkOrder) {
                log.info("h5_baijia checkOrder false");
                result = false;
                break;
            }

            UOrder order = orderManager.getCpOrder(String.valueOf(appId), String.valueOf(channelId), cpOrderId);
            if (order == null) {
                log.info("订单为空");
                result = false;
                break;
            }

            long zhiyueOrderId = order.getOrderID();
            Integer zhiyueUid = order.getUserID();
            channelOrder.replace("zy_uid", zhiyueUid);
            boolean first = false;
            if (order.getState() == OrderState.STATE_OPEN_PAY) {
                // 首次回调 已完成支付 但未发货
                order.setState(OrderState.STATE_PAY_SUCCESS);
                order.setChannelOrderID("");
                order.setRealMoney(Integer.parseInt(FeeUtils.yuanToFen(money)));
                order.setSdkOrderTime(DateUtil.formatDate(System.currentTimeMillis(), DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
                orderManager.updateOrder(order);
                first = true;
            } else if (order.getState() == OrderState.STATE_PAY_SUCCESS) {
                // 多次回调 已完成支付 申请发货未发货
            } else {
                break;
            }

            // cp请求发货
            GameNew gameNew = gameNewService.selectGame(appId, -1);
            if (gameNew == null) {
                result = false;
                break;
            }

            result = notifyToCp(first, gameNew, order, money, cpOrderId, channelId);

        } while (false);
        ResponseUtil.write(response, result ? "success" : "fail");
    }

    /**
     * 渠道订单回调地址
     * 支付成功-核对订单 渠道->sdk回调->cp->通知sdk->渠道
     * status	    String	是	是	订单状态。“success”为支付成功
     * cpOrderId	String	是	是	游戏方（cp）游戏订单号
     * orderId	    String	是	是	我方订单ID
     * uid	        string	是	是	用户UID (唯一) 我方用户的UID
     * userName	    string	是	是	用户名
     * money		        是	是	商品价格(元) 例：1.00
     * gameId	    String	是	是	游戏的id
     * goodsId	    string	是	是	商品ID 没有的话。可以写1
     * goodsName	string	是	是	商品名,如：游戏币
     * server	    string	是	是	支付时的游戏服
     * role	        string	是	是	支付时角色信息,支付完后回传
     * time	        int	是	是	当前时间unix时间戳
     * ext	        string	否	否	额外透传参数(原样返回)
     * signType	    string	是	否	固定值 md5
     * sign	        string	是	否	加密串
     */
    @RequestMapping(value = "/callbackPayInfo/h5_5144wan/{channelId}/{appId}")
    @ResponseBody
    public void h5_5144wan(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                           @RequestParam("status") String status,
                           @RequestParam("cpOrderId") String cpOrderId,
                           @RequestParam("orderId") String orderId,
                           @RequestParam("uid") String uid,
                           @RequestParam("userName") String userName,
                           @RequestParam("money") String money,
                           @RequestParam("gameId") String gameId,
                           @RequestParam("goodsId") String goodsId,
                           @RequestParam("goodsName") String goodsName,
                           @RequestParam("server") String server,
                           @RequestParam("role") String role,
                           @RequestParam("time") String time,
                           @RequestParam("ext") String ext,
                           @RequestParam("signType") String signType,
                           @RequestParam("sign") String sign,

                           HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("callbackPayInfo:" + channelId);
        System.out.println("callbackPayInfo:" + appId);

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("status", status);
        parameterMap.put("cpOrderId", cpOrderId);
        parameterMap.put("orderId", orderId);
        parameterMap.put("uid", uid);
        parameterMap.put("userName", userName);
        parameterMap.put("money", money);

        parameterMap.put("gameId", gameId);
        parameterMap.put("goodsId", goodsId);
        parameterMap.put("goodsName", goodsName);
        parameterMap.put("server", server);
        parameterMap.put("role", role);
        parameterMap.put("time", time);
        parameterMap.put("ext", ext);
        parameterMap.put("signType", signType);

        parameterMap.put("sign", sign);

        System.out.println("parameterMap =" + parameterMap.toString());

        JSONObject channelOrder = new JSONObject();
        boolean result = true;
        do {
            BaseChannel channelSerivce = channelHandler.getChannel(channelId);

            boolean checkOrder = channelSerivce.channelPayCallback(appId, parameterMap, channelOrder);
            if (!checkOrder) {
                result = false;
                break;
            }

            UOrder order = orderManager.getCpOrder(String.valueOf(appId), String.valueOf(channelId), cpOrderId);
            if (order == null) {
                log.info("订单为空");
                result = false;
                break;
            }

            long zhiyueOrderId = order.getOrderID();
            Integer zhiyueUid = order.getUserID();
            channelOrder.replace("zy_uid", zhiyueUid);
            boolean first = false;
            if (order.getState() == OrderState.STATE_OPEN_PAY) {
                // 首次回调 已完成支付 但未发货
                order.setState(OrderState.STATE_PAY_SUCCESS);
                order.setChannelOrderID("");
                order.setRealMoney(Integer.parseInt(FeeUtils.yuanToFen(money)));
                order.setSdkOrderTime(DateUtil.formatDate(System.currentTimeMillis(), DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
                orderManager.updateOrder(order);
                first = true;
            } else if (order.getState() == OrderState.STATE_PAY_SUCCESS) {
                // 多次回调 已完成支付 申请发货未发货
            } else {
                break;
            }

            // cp请求发货

            GameNew gameNew = gameNewService.selectGame(appId, -1);
            if (gameNew == null) {
                result = false;
                break;
            }

            result = notifyToCp(first, gameNew, order, money, cpOrderId, channelId);
        } while (false);
        ResponseUtil.write(response, result ? "success" : "fail");
    }

    /**
     * 渠道订单回调地址
     * 支付成功-核对订单 渠道->sdk回调->cp->通知sdk->渠道
     * 参数名            类型    是否必传    说明
     * order_no	        string	是       顺网平台订单号
     * guid	            string	是       顺网平台游戏帐号
     * server_code	    string	否       分区服的游戏必传,游戏方的区服编号,如s1,s2
     * money	        string	是       充值金额,人民币(单位元)
     * coin	            string	否       游戏币数量,页游必传
     * role_id	        string	否       角色ID(充值到角色的游戏必传)
     * out_order_no	    string	否       游戏方订单号(H5,端游游戏必传)
     * other_data	    string	否       其他数据(透传，原样返回)
     * platform	        string	是       固定值:swjoy
     * time	            int	    是	     unix时间戳
     * sign	            string	是	     签名规则
     */
    @RequestMapping(value = "/callbackPayInfo/h5_shunwang/{channelId}/{appId}")
    @ResponseBody
    public void h5_shunwang(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                            @RequestParam("order_no") String order_no,
                            @RequestParam("guid") String guid,
                            @RequestParam("server_code") String server_code,//字符串
                            @RequestParam("money") String money,
                            @RequestParam("coin") String coin,
                            @RequestParam("role_id") String role_id,
                            @RequestParam("out_order_no") String out_order_no,
                            @RequestParam("other_data") String other_data,
                            @RequestParam("platform") String platform,
                            @RequestParam("time") String time,
                            @RequestParam("sign") String sign,

                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("callbackPayInfo:" + channelId);
        System.out.println("callbackPayInfo:" + appId);

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("order_no", order_no);
        parameterMap.put("guid", guid);
        parameterMap.put("server_code", server_code);
        parameterMap.put("money", money);
        parameterMap.put("coin", coin);
        parameterMap.put("role_id", role_id);

        parameterMap.put("out_order_no", out_order_no);
        parameterMap.put("other_data", other_data);
        parameterMap.put("platform", platform);
        parameterMap.put("time", time);
        parameterMap.put("sign", sign);

        System.out.println("parameterMap =" + parameterMap.toString());

        JSONObject rsp = new JSONObject();
        JSONObject channelOrder = new JSONObject();
        boolean result = false;
        do {
            BaseChannel channelSerivce = channelHandler.getChannel(channelId);

            boolean checkOrder = channelSerivce.channelPayCallback(appId, parameterMap, channelOrder);
            if (!checkOrder) {
                result = false;
                break;
            }

            UOrder order = orderManager.getCpOrder(String.valueOf(appId), String.valueOf(channelId), out_order_no);
            if (order == null) {
                log.info("订单为空");
                result = false;
                break;
            }

            long zhiyueOrderId = order.getOrderID();
            Integer zhiyueUid = order.getUserID();
            channelOrder.replace("zy_uid", zhiyueUid);
            boolean first = false;
            if (order.getState() == OrderState.STATE_OPEN_PAY) {
                // 首次回调 已完成支付 但未发货
                order.setState(OrderState.STATE_PAY_SUCCESS);
                order.setChannelOrderID("");
                order.setRealMoney(Integer.parseInt(FeeUtils.yuanToFen(money)));
                order.setSdkOrderTime(DateUtil.formatDate(System.currentTimeMillis(), DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
                orderManager.updateOrder(order);
                first = true;
            } else if (order.getState() == OrderState.STATE_PAY_SUCCESS) {
                // 多次回调 已完成支付 申请发货未发货
                rsp.put("code", 2);
                rsp.put("msg", "订单已支付成功");
            } else {
                break;
            }

            // cp请求发货
            GameNew gameNew = gameNewService.selectGame(appId, -1);
            if (gameNew == null) {
                result = false;
                break;
            }

            result = notifyToCp(first, gameNew, order, money, out_order_no, channelId);
            if (!result) {
                rsp.put("code", 3);
                rsp.put("msg", "发货失败");
            } else {
                rsp.put("code", 1);
                rsp.put("msg", "订单充值成功");
            }

        } while (false);
        //        {"code": 1,"msg": "ok"}	json	其中 code 为 int 类型，msg 为 code 的说明类型,两个属性值都不 能少
        //code=1		订单充值成功
        //code=2		订单号重复,顺网当成充值成功来处理
        //code=3		充值异常,顺网会重复发起请求
        //code=4		明确充值失败,顺网发起退款流
        //还没返回或其它值		顺网会重复发起请求
        if (!rsp.containsKey("code")) {
            rsp.put("code", 4);
            rsp.put("msg", "订单充值失败");
        }
        ResponseUtil.write(response, rsp);
    }
}
