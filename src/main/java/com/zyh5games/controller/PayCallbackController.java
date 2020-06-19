package com.zyh5games.controller;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.entity.Account;
import com.zyh5games.entity.GameNew;
import com.zyh5games.entity.UOrder;
import com.zyh5games.jedis.JedisRechargeCache;
import com.zyh5games.sdk.UOrderManager;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelHandler;
import com.zyh5games.sdk.channel.HttpService;
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
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
     * 检查订单并发货 通用方法
     *
     * @param appId          指悦游戏id
     * @param channelId      指悦渠道id
     * @param channelOrderId 渠道订单id
     * @param cpOrderId      cp订单id
     * @param channelOrder   给cp的参数-函数内赋值
     * @param money          金额 元
     */
    public boolean checkOrder(Integer appId, Integer channelId,
                              Map<String, String> parameterMap, JSONObject channelOrder,
                              String cpOrderId, String channelOrderId, String money) throws Exception {
        boolean result = true;
        do {
            BaseChannel channelSerivce = channelHandler.getChannel(channelId);

            boolean channelCheck = channelSerivce.channelPayCallback(appId, parameterMap, channelOrder);
            if (!channelCheck) {
                result = false;
                break;
            }

            if (cpOrderId == null || cpOrderId.isEmpty()) {
                cpOrderId = channelOrder.getString("cpOrderId");
            }
            if (channelOrderId == null || channelOrderId.isEmpty()) {
                channelOrderId = channelOrder.getString("channelOrderId");
            }

            UOrder order = orderManager.getCpOrder(String.valueOf(appId), String.valueOf(channelId), cpOrderId);
            if (order == null) {
                log.info("订单为空");
                result = false;
                break;
            }
            //某些清况 检查金额 todo
            if (!channelSerivce.checkOrderMoney(money, order)) {
                return false;
            }
            //检查uid
            Integer zhiyueUid = order.getUserID();
            channelOrder.replace("zy_uid", zhiyueUid);

            log.info("start OrderState = " + order.getState());
            boolean first = false;
            boolean isReturn = false;
            switch (order.getState()) {
                case OrderState.STATE_OPEN_PAY: {
                    // 首次回调 已完成支付 但未发货
                    order.setState(OrderState.STATE_PAY_SUCCESS);
                    order.setChannelOrderID(channelOrderId);
                    order.setRealMoney(Integer.parseInt(FeeUtils.yuanToFen(money)));
                    order.setSdkOrderTime(DateUtil.formatDate(System.currentTimeMillis(), DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
                    orderManager.updateCpOrder(order);
                    first = true;
                }
                break;
                case OrderState.STATE_PAY_SUCCESS: {
                    // 多次回调 已完成支付 申请发货未发货
                    log.info("checkOrder 支付成功待发货 order = " + order.getOrderID());
                }
                break;
                case OrderState.STATE_PAY_FINISHED:
                default:
                    isReturn = true;
                    break;
            }
            if (isReturn) {
                return result;
            }

            // cp请求发货
            GameNew gameNew = gameNewService.selectGame(appId, -1);
            if (gameNew == null) {
                result = false;
                break;
            }

            //订单金额 非实际支付
            String orderMoney = FeeUtils.fenToYuan(order.getMoney());
            result = notifyToCp(first, gameNew, order, orderMoney, cpOrderId, channelId);

            log.info(" end OrderState = " + order.getState());
        } while (false);
        log.info(" end result = " + result);
        return result;
    }

    /**
     * 渠道订单回调地址
     * 支付成功-核对订单 渠道->sdk回调->cp->通知sdk->渠道
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
            param.append("amount").append("=").append(price);//金额 元
            param.append("&").append("appId").append("=").append(appId);
            param.append("&").append("cpOrderId").append("=").append(cpOrderId);
            param.append("&").append("orderId").append("=").append(orderId);
            param.append("&").append("productId").append("=").append(order.getProductID());
            param.append("&").append("roleId").append("=").append(order.getRoleID());

            String sdkSign = MD5Util.md5(param.toString() + md5Key);
            param.append("&").append("sign").append("=").append(sdkSign);

            String url = notifyUrl + param.toString();

            JSONObject cpData = httpService.httpGetJson(url);
            log.info("cp支付回调：" + cpData);
            if (cpData.containsKey("code") && cpData.getInteger("code") == 1) {
                order.setState(OrderState.STATE_PAY_FINISHED);
                order.setCompleteTime(DateUtil.formatDate(System.currentTimeMillis(), DateUtil.FORMAT_YYYY_MMDD_HHmmSS));
                orderManager.updateCpOrder(order);
            } else {
                res = false;
                break;
            }
            log.info("first：" + first);
            //支付成功 更新订单 redis-mysql
            if (first) {
                Account account = accountService.findAccountById(zhiyueUid);
                log.info("account：" + (account != null));
                if (account != null) {
                    cache.reqPay(String.valueOf(appId), order.getServerID(), String.valueOf(channelId), zhiyueUid, order.getRoleID(), order.getRealMoney(), account.getCreateTime());
                }
            }
        } while (false);

        return res;
    }


    /**
     * 模板
     */
    @RequestMapping(value = "/callbackPayInfo/h5_example/{channelId}/{appId}", method = RequestMethod.GET)
    @ResponseBody
    public void h5_example(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                           @RequestParam("amount") String amount,
                           @RequestParam("cpOrderId") String cpOrderId,
                           @RequestParam("channelOrderId") String channelOrderId,
                           @RequestBody Map<String, Object> param,
                           HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("amount", amount);
        parameterMap.put("cpOrderId", cpOrderId);
        parameterMap.put("channelOrderId", channelOrderId);

        JSONObject channelOrder = new JSONObject();

        boolean result = checkOrder(appId, channelId, parameterMap, channelOrder, cpOrderId, channelOrderId, amount);


        ResponseUtil.write(response, result ? "success" : "fail");
        log.info("h5_ziwan end " + result);
    }

    /**
     * 紫菀
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
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("openid", openid);
        parameterMap.put("price", price);
        parameterMap.put("other", other);
        parameterMap.put("item_id", item_id);
        parameterMap.put("orderid", orderid);
        parameterMap.put("sign", sign);
        log.info("parameterMap =" + parameterMap.toString());

        JSONObject channelOrder = new JSONObject();


        String money = price;
        String cpOrderId = orderid;

        boolean result = checkOrder(appId, channelId, parameterMap, channelOrder, cpOrderId, "", money);


        ResponseUtil.write(response, result ? "success" : "fail");
        log.info("h5_ziwan end " + result);
    }

    /**
     * 百家
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
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

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

        log.info("h5_baijia parameterMap =" + parameterMap.toString());

        JSONObject channelOrder = new JSONObject();
        boolean result = checkOrder(appId, channelId, parameterMap, channelOrder, cpOrderId, "", money);

        ResponseUtil.write(response, result ? "success" : "fail");
    }

    /**
     * 5144玩
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
     * time	        int	是	是	    当前时间unix时间戳
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
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

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

        log.info("parameterMap =" + parameterMap.toString());

        JSONObject channelOrder = new JSONObject();
        boolean result = checkOrder(appId, channelId, parameterMap, channelOrder, cpOrderId, "", money);

        ResponseUtil.write(response, result ? "success" : "fail");
    }

    /**
     * 顺网
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
    @RequestMapping(value = "/callbackPayInfo/h5_shunwang/{channelId}/{appId}", method = RequestMethod.GET)
    @ResponseBody
    public void h5_shunwang(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                            @RequestParam("order_no") String order_no,
                            @RequestParam("guid") String guid,
                            @RequestParam(value = "server_code", required = false) String server_code,//字符串
                            @RequestParam("money") String money,
                            @RequestParam(value = "coin", required = false) String coin,
                            @RequestParam(value = "role_id", required = false) String role_id,
                            @RequestParam(value = "out_order_no", required = false) String out_order_no,
                            @RequestParam(value = "other_data", required = false) String other_data,
                            @RequestParam("platform") String platform,
                            @RequestParam("time") String time,
                            @RequestParam("sign") String sign,

                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

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

        log.info("parameterMap =" + parameterMap.toString());

        JSONObject rsp = new JSONObject();
        JSONObject channelOrder = new JSONObject();

        boolean result = checkOrder(appId, channelId, parameterMap, channelOrder, out_order_no, order_no, money);
        /*
            {"code": 1,"msg": "ok"}	json	其中 code 为 int 类型，msg 为 code 的说明类型,两个属性值都不 能少
            code=1		订单充值成功
            code=2		订单号重复,顺网当成充值成功来处理
            code=3		充值异常,顺网会重复发起请求
            code=4		明确充值失败,顺网发起退款流
            还没返回或其它值		顺网会重复发起请求
        */
        if (!result) {
            rsp.put("code", 3);
            rsp.put("msg", "发货失败");
        } else {
            rsp.put("code", 1);
            rsp.put("msg", "订单充值成功");
        }

        ResponseUtil.write(response, rsp);
    }


    /**
     * 鱼马
     * 参数名            类型    是否必传    说明
     * nt_data	        string	是       通知数据解码后为xml格式 ,具体见2.1.1
     * sign	            string	是       签名串,具体见第三章
     * md5Sign	        string	否       分区服的游戏必传,游戏方的区服编号,如s1,s2
     */
    @RequestMapping(value = "/callbackPayInfo/h5_yuma/{channelId}/{appId}", method = RequestMethod.POST)
    @ResponseBody
    public void h5_yuma(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                        @RequestParam("nt_data") String nt_data,
                        @RequestParam("sign") String sign,
                        @RequestParam("md5Sign") String md5Sign,
                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("nt_data", nt_data);
        parameterMap.put("sign", sign);
        parameterMap.put("md5Sign", md5Sign);


        log.info("parameterMap =" + parameterMap.toString());

        JSONObject rsp = new JSONObject();
        JSONObject channelOrder = new JSONObject();
        boolean result = false;
        do {
            result = checkOrder(appId, channelId, parameterMap, channelOrder, "", "", "");
            if (!result) {
                rsp.put("code", 3);
                rsp.put("msg", "发货失败");
            } else {
                rsp.put("code", 1);
                rsp.put("msg", "订单充值成功");
            }

        } while (false);

        ResponseUtil.write(response, rsp);
    }

    /**
     * 欢聚
     * <p>
     * 接收参数(CGI)	类型	        必选	参于加密	说明
     * status		    String	    是	是	    订单状态。“success”为支付成功
     * cpOrderId		String	    是	是	    cp游戏订单号。
     * orderId		    String	    是	是	    欢聚游微游戏订单号
     * uid		        string	    是	是	    欢聚游微游戏用户的uid
     * userName		    string	    是	是	    欢聚游微游戏的用户名
     * money		    decimal	    是	是	    支付钱数(元),保留2位小数
     * gameId		    String	    是	是	    游戏的id
     * goodsId		    String	    是	是	    商品ID
     * goodsName		String	    是	是	    商品名
     * server		    String	    是	是	    支付的游戏服
     * role		        String	    是	是	    支付时角色信息,
     * time		        int	        是  是	    当前时间unix时间戳
     * ext		        String(200)	否	否	    额外透传参数(原样返回)
     * sign		        string	    是	否	    加密串
     * signType		    string	    是	否	    固定md5
     */
    @RequestMapping(value = "/callbackPayInfo/h5_huanju/{channelId}/{appId}")
    @ResponseBody
    public String h5_huanju(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
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
                            @RequestParam(value = "ext", required = false) String ext,
                            @RequestParam("signType") String signType,
                            @RequestParam("sign") String sign,
                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

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

        log.info("parameterMap =" + parameterMap.toString());

        JSONObject channelOrder = new JSONObject();

        boolean result = checkOrder(appId, channelId, parameterMap, channelOrder, cpOrderId, orderId, money);
//        ResponseUtil.write(response, result ? "success" : "fail");
        return result ? "success" : "fail";
    }

    /**
     * 引力
     * 回调body已经过url encode，使用时要使用url decode进行解密等到json
     * <p>
     * uid                  KUKU平台用户ID
     * orderNo              KUKU平台订单唯一编号
     * productId            游戏方支付时传入的商品ID
     * gameOrderNo          游戏方支付时传入的游戏方订单编号
     * gameKey              KUKU平台分配给游戏方的游戏标识
     * payCost              用户真实支付的金额，单位分
     * ext1                 游戏方支付时传入的扩展参数2，原样返回
     * ext2                 游戏方支付时传入的扩展参数1，原样返回
     * sign                 签名
     */
    @RequestMapping(value = "/callbackPayInfo/h5_yinli/{channelId}/{appId}")
    @ResponseBody
    public void h5_yinli(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        int length = request.getContentLength();
        ServletInputStream input = request.getInputStream();
        byte[] buffer = new byte[length];
        input.read(buffer, 0, length);

        String urlData = new String(buffer);


        log.info("callbackPayInfo:" + urlData);
        String jsonData = URLDecoder.decode(urlData, String.valueOf(StandardCharsets.UTF_8));
        log.info("h5_yinli JsonData = " + jsonData);

        JSONObject data = JSONObject.parseObject(jsonData);

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("uid", data.getString("uid"));
        parameterMap.put("orderNo", data.getString("orderNo"));
        parameterMap.put("productId", data.getString("productId"));
        parameterMap.put("gameOrderNo", data.getString("gameOrderNo"));
        parameterMap.put("gameKey", data.getString("gameKey"));
        parameterMap.put("payCost", data.getString("payCost"));
        parameterMap.put("ext1", data.getString("ext1"));
        parameterMap.put("ext2", data.getString("ext2"));
        parameterMap.put("sign", data.getString("sign"));

        log.info("parameterMap =" + parameterMap.toString());

        String money = FeeUtils.fenToYuan(data.getString("payCost"));

        boolean result = checkOrder(appId, channelId, parameterMap, data, data.getString("gameOrderNo"), data.getString("orderNo"), money);

        ResponseUtil.write(response, result ? "ok" : "fail");
    }

    /**
     * 悦游
     * 回调body已经过url encode，使用时要使用url decode进行解密等到json
     * <p>
     *
     * @param amount         金额，单位为分
     * @param channel_source 数据来源
     * @param game_appid     游戏编号----运营方为游戏分配的唯一编号
     * @param out_trade_no   渠道方订单号
     * @param payplatform2cp 用于 CP 要求平台特别传输其他参数，默认是访问 ip
     * @param trade_no       游戏透传参数（默认为游戏订单号，回调时候原样返回）
     * @param sign           按照上方签名机制进行签名
     */
    @RequestMapping(value = "/callbackPayInfo/h5_yueyou/{channelId}/{appId}", method = RequestMethod.GET)
    @ResponseBody
    public void h5_yueyou(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                          @RequestParam("amount") String amount,
                          @RequestParam("channel_source") String channel_source,
                          @RequestParam("game_appid") String game_appid,
                          @RequestParam("out_trade_no") String out_trade_no,
                          @RequestParam("payplatform2cp") String payplatform2cp,
                          @RequestParam("trade_no") String trade_no,
                          @RequestParam("sign") String sign,
                          HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        JSONObject data = new JSONObject();

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("amount", amount);
        parameterMap.put("channel_source", channel_source);
        parameterMap.put("game_appid", game_appid);
        parameterMap.put("out_trade_no", out_trade_no);
        parameterMap.put("payplatform2cp", payplatform2cp);
        parameterMap.put("trade_no", trade_no);
        parameterMap.put("sign", sign);

        log.info("parameterMap =" + parameterMap.toString());

        String money = FeeUtils.fenToYuan(amount);
        boolean result = checkOrder(appId, channelId, parameterMap, data, trade_no, out_trade_no, money);

        JSONObject rsp = new JSONObject();
        rsp.put("status", result ? "success" : "fail");
        ResponseUtil.write(response, rsp);
    }

    /**
     * 掌盟  soeasy
     * 参数名            类型        是否必传        说明
     *
     * @param appid     string      Y           同APPID
     * @param sdkindx   string      Y           平台定义
     * @param uid       string      Y           用户的唯一标示
     * @param feeid     string      N           计费点ID
     * @param feemoney  string      Y           实际扣费金额（分）
     * @param orderid   string      Y           支付在速易服务器上订单号
     * @param extradata string      N           Cp自定义参数，响应时透传返回（如游戏服务的订单号）
     * @param paytime   string      Y           下单时间
     * @param prover    string      Y           协议版本号初始为1
     * @param paystatus string      Y           支付状态1为成功，2沙盒测试，其他均为失败
     * @param sign      string      Y
     */
    @RequestMapping(value = "/callbackPayInfo/h5_zhangmeng/{channelId}/{appId}", method = RequestMethod.GET)
    @ResponseBody
    public void h5_zhangmeng(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                             @RequestParam("appid") String appid,
                             @RequestParam("sdkindx") String sdkindx,
                             @RequestParam("uid") String uid,
                             @RequestParam(value = "feeid", required = false) String feeid,
                             @RequestParam("feemoney") String feemoney,
                             @RequestParam("orderid") String orderid,
                             @RequestParam(value = "extradata", required = false) String extradata,
                             @RequestParam("paytime") String paytime,
                             @RequestParam("prover") String prover,
                             @RequestParam("paystatus") String paystatus,
                             @RequestParam("sign") String sign,
                             HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("appid", appid);
        parameterMap.put("sdkindx", sdkindx);
        parameterMap.put("uid", uid);
        parameterMap.put("feeid", feeid);
        parameterMap.put("feemoney", feemoney);
        parameterMap.put("orderid", orderid);
        parameterMap.put("extradata", extradata);
        parameterMap.put("paytime", paytime);
        parameterMap.put("prover", prover);
        parameterMap.put("paystatus", paystatus);
        parameterMap.put("sign", sign);

        log.info("parameterMap =" + parameterMap.toString());

        JSONObject rsp = new JSONObject();
        JSONObject channelOrder = new JSONObject();
        boolean result = false;
        do {
            String cpOrderId = channelOrder.getString("extradata");
            String money = FeeUtils.fenToYuan(parameterMap.get("feemoney"));

            result = checkOrder(appId, channelId, parameterMap, channelOrder, cpOrderId, "", money);

        } while (false);

        rsp.put("status", result ? "ok" : "fail");
        ResponseUtil.write(response, rsp);
        log.info("callbackPayInfo rsp " + rsp.toString());
    }

    /**
     * 悦游
     * 回调body已经过url encode，使用时要使用url decode进行解密等到json
     * <p>
     *
     * @param amount         金额，单位为分
     * @param channel_source 数据来源
     * @param game_appid     游戏编号----运营方为游戏分配的唯一编号
     * @param out_trade_no   渠道方订单号
     * @param payplatform2cp 用于 CP 要求平台特别传输其他参数，默认是访问 ip
     * @param trade_no       游戏透传参数（默认为游戏订单号，回调时候原样返回）
     * @param sign           按照上方签名机制进行签名
     */
    @RequestMapping(value = "/callbackPayInfo/h5_yiniu/{channelId}/{appId}", method = RequestMethod.GET)
    @ResponseBody
    public void h5_yiniu(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                         @RequestParam("amount") String amount,
                         @RequestParam("channel_source") String channel_source,
                         @RequestParam("game_appid") String game_appid,
                         @RequestParam("out_trade_no") String out_trade_no,
                         @RequestParam("payplatform2cp") String payplatform2cp,
                         @RequestParam("trade_no") String trade_no,
                         @RequestParam("sign") String sign,
                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        JSONObject data = new JSONObject();

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("amount", amount);
        parameterMap.put("channel_source", channel_source);
        parameterMap.put("game_appid", game_appid);
        parameterMap.put("out_trade_no", out_trade_no);
        parameterMap.put("payplatform2cp", payplatform2cp);
        parameterMap.put("trade_no", trade_no);
        parameterMap.put("sign", sign);

        log.info("parameterMap =" + parameterMap.toString());

        String money = FeeUtils.fenToYuan(amount);
        boolean result = checkOrder(appId, channelId, parameterMap, data, trade_no, out_trade_no, money);

        JSONObject rsp = new JSONObject();
        rsp.put("status", result ? "success" : "fail");
        ResponseUtil.write(response, rsp);
    }

    /**
     * 1758
     * 参数字段	           类型	    说明	                                是否可空	是否参与签名
     *
     * @param params appKey      string	游戏的appKey	                            否	是
     *               gid         string	用户的gid	                            否	是
     *               orderId     string	1758订单号	                            否	是
     *               txId        string	cp订单号	                                否	是
     *               productDesc string	商品描述	                                否	是
     *               totalFee    int	    支付金额，单位分	                        否	是
     *               status      int	    订单状态	                                否	是
     *               state       string	cp自定义参数	                            是	是
     *               ext         string	订单其他信息，json结构，预留参数，目前未启用	是	否
     *               sign        string	参数签名	                                否	签名方法 , 签名工具
     */
    @RequestMapping(value = "/callbackPayInfo/h5_1758/{channelId}/{appId}", method = RequestMethod.POST)
    @ResponseBody
    public void h5_1758(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                        @RequestParam Map<String, String> params,
                        HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        log.info("parameterMap =" + params.toString());

        JSONObject data = new JSONObject();

        String totalFee = params.get("totalFee");
        String txId = params.get("txId");
        String orderId = params.get("orderId");
        String money = FeeUtils.fenToYuan(totalFee);
        boolean result = checkOrder(appId, channelId, params, data, txId, orderId, money);

        JSONObject rsp = new JSONObject();
        rsp.put("status", result ? "success" : "fail");
        ResponseUtil.write(response, rsp);
    }

    /**
     * 小y
     * 回调body已经过url encode，使用时要使用url decode进行解密等到json
     * <p>
     *
     * @param amount         金额，单位为分
     * @param channel_source 数据来源
     * @param game_appid     游戏编号----运营方为游戏分配的唯一编号
     * @param out_trade_no   渠道方订单号
     * @param payplatform2cp 用于 CP 要求平台特别传输其他参数，默认是访问 ip
     * @param trade_no       游戏透传参数（默认为游戏订单号，回调时候原样返回）
     * @param sign           按照上方签名机制进行签名
     */
    @RequestMapping(value = "/callbackPayInfo/h5_xiaoy/{channelId}/{appId}", method = RequestMethod.GET)
    @ResponseBody
    public void h5_xiaoy(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                         @RequestParam("amount") String amount,
                         @RequestParam("channel_source") String channel_source,
                         @RequestParam("game_appid") String game_appid,
                         @RequestParam("out_trade_no") String out_trade_no,
                         @RequestParam("payplatform2cp") String payplatform2cp,
                         @RequestParam("trade_no") String trade_no,
                         @RequestParam("sign") String sign,
                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        JSONObject data = new JSONObject();

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("amount", amount);
        parameterMap.put("channel_source", channel_source);
        parameterMap.put("game_appid", game_appid);
        parameterMap.put("out_trade_no", out_trade_no);
        parameterMap.put("payplatform2cp", payplatform2cp);
        parameterMap.put("trade_no", trade_no);
        parameterMap.put("sign", sign);

        log.info("parameterMap =" + parameterMap.toString());

        String money = FeeUtils.fenToYuan(amount);
        boolean result = checkOrder(appId, channelId, parameterMap, data, trade_no, out_trade_no, money);

        JSONObject rsp = new JSONObject();
        rsp.put("status", result ? "success" : "fail");
        ResponseUtil.write(response, rsp);
    }


    /**
     * 三唐
     * <p>
     * 参数名              备注                      说明
     * pf               平台名                         固定值:3tang
     * sid              游戏区服                       根据玩家所在区服传值
     * openid           玩家唯一标志三唐 openid
     * billDate         玩家下单时间                   Unixtime 格式标准时间
     * st_trade_no      三唐平台订单号
     * cp_trade_no      CP 方订单号                   跟调起充值接口传入的 CP 方,订单号是一致的
     * cash             玩家充值金额                  单位是元
     * sign             签名（md5 加密 小写）          md5(pf&sid&openid&billDate&st_trade_no&cp_trade_no&cash& APPKEY )注意：&是变量连接符,不要放到加密里APPKEY 由三唐平台分配或双方协定
     */
    @RequestMapping(value = "/callbackPayInfo/h5_santang/{channelId}/{appId}")
    @ResponseBody
    public void h5_santang(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                           @RequestParam("pf") String pf,
                           @RequestParam("sid") String sid,
                           @RequestParam("openid") String openid,
                           @RequestParam("billDate") String billDate,
                           @RequestParam("st_trade_no") String st_trade_no,
                           @RequestParam("cp_trade_no") String cp_trade_no,
                           @RequestParam("cash") String cash,
                           @RequestParam("sign") String sign,
                           HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        JSONObject data = new JSONObject();

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("pf", pf);
        parameterMap.put("sid", sid);
        parameterMap.put("openid", openid);
        parameterMap.put("billDate", billDate);
        parameterMap.put("st_trade_no", st_trade_no);
        parameterMap.put("cp_trade_no", cp_trade_no);
        parameterMap.put("cash", cash);
        parameterMap.put("sign", sign);

        log.info("parameterMap =" + parameterMap.toString());

        boolean result = checkOrder(appId, channelId, parameterMap, data, cp_trade_no, st_trade_no, cash);

        JSONObject rsp = new JSONObject();
        rsp.put("result", result ? 1 : 0);
        ResponseUtil.write(response, rsp);
    }

    /**
     * 羊羔
     * <p>
     *
     * @param amount         金额，单位为分
     * @param channel_source 数据来源
     * @param game_appid     游戏编号----运营方为游戏分配的唯一编号
     * @param out_trade_no   渠道方订单号
     * @param payplatform2cp 用于 CP 要求平台特别传输其他参数，默认是访问 ip
     * @param trade_no       游戏透传参数（默认为游戏订单号，回调时候原样返回）
     * @param sign           按照上方签名机制进行签名
     */
    @RequestMapping(value = "/callbackPayInfo/h5_yanggao/{channelId}/{appId}", method = RequestMethod.GET)
    @ResponseBody
    public void h5_yanggao(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                           @RequestParam("amount") String amount,
                           @RequestParam("channel_source") String channel_source,
                           @RequestParam("game_appid") String game_appid,
                           @RequestParam("out_trade_no") String out_trade_no,
                           @RequestParam("payplatform2cp") String payplatform2cp,
                           @RequestParam("trade_no") String trade_no,
                           @RequestParam("sign") String sign,
                           HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        JSONObject data = new JSONObject();

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("amount", amount);
        parameterMap.put("channel_source", channel_source);
        parameterMap.put("game_appid", game_appid);
        parameterMap.put("out_trade_no", out_trade_no);
        parameterMap.put("payplatform2cp", payplatform2cp);
        parameterMap.put("trade_no", trade_no);
        parameterMap.put("sign", sign);

        log.info("parameterMap =" + parameterMap.toString());

        String money = FeeUtils.fenToYuan(amount);
        boolean result = checkOrder(appId, channelId, parameterMap, data, trade_no, out_trade_no, money);

        JSONObject rsp = new JSONObject();
        rsp.put("status", result ? "success" : "fail");
        ResponseUtil.write(response, rsp);
    }

    /**
     * 奇游
     * <p>
     *
     * @param order_no   平台支付订单	                            是	201912181137114223
     * @param cp_order   游戏订单号	                            是
     * @param user_id    平台用户ID，和登录注册时返回的user_id一致	是
     * @param product_id 游戏商品ID	                            是
     * @param price      充值金额（元）	                        是
     * @param role_id    角色ID	                                是
     * @param server_id  服务器ID	                            是
     * @param ext        扩展参数，透传下单时提供的extension	    否	默认空字符串
     * @param time       Unix时间戳	                            是
     * @param sign       加密串	                                是	详见下面sign生成算法
     */
    @RequestMapping(value = "/callbackPayInfo/h5_qiyou/{channelId}/{appId}", method = RequestMethod.GET)
    @ResponseBody
    public void h5_qiyou(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                         @RequestParam("order_no") String order_no,
                         @RequestParam("cp_order") String cp_order,
                         @RequestParam("user_id") String user_id,
                         @RequestParam("product_id") String product_id,
                         @RequestParam("price") String price,
                         @RequestParam("role_id") String role_id,
                         @RequestParam("server_id") String server_id,
                         @RequestParam("ext") String ext,
                         @RequestParam("time") String time,
                         @RequestParam("sign") String sign,
                         HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        JSONObject data = new JSONObject();

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("order_no", order_no);
        parameterMap.put("cp_order", cp_order);
        parameterMap.put("user_id", user_id);
        parameterMap.put("product_id", product_id);
        parameterMap.put("price", price);
        parameterMap.put("role_id", role_id);
        parameterMap.put("server_id", server_id);
        parameterMap.put("ext", ext);
        parameterMap.put("time", time);
        parameterMap.put("sign", sign);

        log.info("parameterMap =" + parameterMap.toString());

        boolean result = checkOrder(appId, channelId, parameterMap, data, cp_order, order_no, price);

        ResponseUtil.write(response, result ? "0" : "-1");
    }

    /**
     * 77
     *
     * @param qqes_order String	    是   SDK订单号
     * @param cp_order   String	    是   cp订单号
     * @param fee        float	    是   订单金额（元）
     * @param cpgameid   Integer	是   cp在我们平台的游戏id
     * @param timestamp  String	    是   请求时间戳，Unix时间戳，10位
     * @param sign       String	    否   签名字符串，算法： 除非字段标注为不需要参与签名，否则请求参数都参与签名，按参数值自然升序，然后拼接成字符串(比如a=1&b=2&c=3&签名秘钥)，然后md5生成签名字符串
     */
    @RequestMapping(value = "/callbackPayInfo/h5_77/{channelId}/{appId}", method = RequestMethod.POST)
    @ResponseBody
    public void h5_77(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                      @RequestParam("qqes_order") String qqes_order,
                      @RequestParam("cp_order") String cp_order,
                      @RequestParam("fee") String fee,
                      @RequestParam("cpgameid") String cpgameid,
                      @RequestParam("timestamp") String timestamp,
                      @RequestParam("sign") String sign,
                      HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        JSONObject data = new JSONObject();

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("qqes_order", qqes_order);
        parameterMap.put("cp_order", cp_order);
        parameterMap.put("fee", fee);
        parameterMap.put("cpgameid", cpgameid);
        parameterMap.put("timestamp", timestamp);
        parameterMap.put("sign", sign);

        log.info("parameterMap =" + parameterMap.toString());

        boolean result = checkOrder(appId, channelId, parameterMap, data, cp_order, qqes_order, fee);

        ResponseUtil.write(response, result ? "success" : "fail");
    }

    /**
     * so游记
     * <p>
     *
     * @param uid       用户在汇米网络的用户ID # length <= 20
     * @param nonce     随机字符串，可为空 # length <= 64
     * @param time      操作发生时的UNIX时间戳，精确到秒 # length = 10
     * @param money     订单金额，币种人民币，单位分，此值必须为正整数 # length <= 20
     * @param propsname 游戏商品名 # length <= 128
     * @param order     由游戏开发商产生的唯一订单号 # length <= 128
     * @param token     校验token # length <= 128
     * @param pay_code  #
     *                  0 # 支付失败
     *                  1 # 支付成功
     * @param sign      #
     *                  --- # 签名，用于请求合法性校验
     */
    @RequestMapping(value = "/callbackPayInfo/h5_SOyouji/{channelId}/{appId}", method = RequestMethod.GET)
    @ResponseBody
    public void h5_SOyouji(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                           @RequestParam("uid") String uid,
                           @RequestParam("nonce") String nonce,
                           @RequestParam("time") String time,
                           @RequestParam("money") String money,
                           @RequestParam("propsname") String propsname,
                           @RequestParam("order") String order,
                           @RequestParam("token") String token,
                           @RequestParam("pay_code") String pay_code,
                           @RequestParam("sign") String sign,
                           HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        JSONObject data = new JSONObject();

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("uid", uid);
        parameterMap.put("nonce", nonce);
        parameterMap.put("time", time);
        parameterMap.put("money", money);
        parameterMap.put("propsname", propsname);
        parameterMap.put("order", order);
        parameterMap.put("token", token);
        parameterMap.put("pay_code", pay_code);
        parameterMap.put("sign", sign);

        log.info("parameterMap =" + parameterMap.toString());

        boolean result = checkOrder(appId, channelId, parameterMap, data, order, "", FeeUtils.fenToYuan(money));

        ResponseUtil.write(response, result ? "1" : "0");
    }

    /**
     * 找手游
     *
     * @param user_id   Int	        是	付费用户id
     * @param username  String	    是	付费用户名
     * @param to_uid    Int	        是	充入用户id
     * @param to_user   String	    是	充入用户名
     * @param pay_id    string	    是	支付id
     * @param money     Smallint	是	充值金额
     * @param game_id   Int	        是	游戏id
     * @param server_id Int	        是	区/服 id
     * @param cp_order  String	    是	游戏充值(订单号)
     * @param time      Int	        是	充值发起时间
     * @param extra     String	    否	额外信息good_id
     * @param sign      string	    是	签名
     */
    @RequestMapping(value = "/callbackPayInfo/h5_zhaoshouyou/{channelId}/{appId}")
    @ResponseBody
    public void h5_zhaoshouyou(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                               @RequestParam("user_id") String user_id,
                               @RequestParam("username") String username,
                               @RequestParam("to_uid") String to_uid,
                               @RequestParam("to_user") String to_user,
                               @RequestParam("pay_id") String pay_id,
                               @RequestParam("money") String money,
                               @RequestParam("game_id") String game_id,
                               @RequestParam("server_id") String server_id,
                               @RequestParam("cp_order") String cp_order,
                               @RequestParam("time") String time,
                               @RequestParam("extra") String extra,
                               @RequestParam("sign") String sign,
                               HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        JSONObject data = new JSONObject();

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("user_id", user_id);
        parameterMap.put("username", username);
        parameterMap.put("to_uid", to_uid);
        parameterMap.put("to_user", to_user);
        parameterMap.put("pay_id", pay_id);
        parameterMap.put("money", money);
        parameterMap.put("game_id", game_id);
        parameterMap.put("server_id", server_id);
        parameterMap.put("cp_order", cp_order);
        parameterMap.put("time", time);
        parameterMap.put("extra", extra);
        parameterMap.put("sign", sign);
        log.info("parameterMap =" + parameterMap.toString());

        boolean result = checkOrder(appId, channelId, parameterMap, data, cp_order, "", money);

        ResponseUtil.write(response, result ? "success" : "fail");
    }

    /**
     * 找手游
     * <p>
     * 必选  类型及范围  说明
     *
     * @param trade_no     true  string  果盘唯一订单号
     * @param serialNumber true  string  游戏方订单序列号
     * @param money        true  string  消费金额。单位是元，精确到分，如10.00。请务必校验金额与玩家下单的商品价值是否一致
     * @param status       true  string  状态；0=失败；1=成功；2=失败，原因是余额不足。
     * @param t            true  string  时间戳(果盘服务器发起通知的北京时间)
     * @param sign         true  string  加密串 sign=md5(serialNumber+money+status+t+SERVER_KEY) 是五个变量值拼接后经 md5 后的值，其中SERVER_KEY 在果盘开放平台上获得。
     * @param appid        false  string
     * @param item_id      false  string
     * @param item_price   false  string
     * @param item_count   false  string
     * @param reserved     false  string  扩展参数，SDK 发起支付时有传递，则这里会回传。
     * @param game_uin     true  string  玩家游戏 uid；请务必校验该 game_uin 和下单时对应的角色 game_uin 是否一致，防止“任意充”
     */
    @RequestMapping(value = "/callbackPayInfo/h5_guopan/{channelId}/{appId}", method = RequestMethod.POST)
    @ResponseBody
    public void h5_guopan(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                          @RequestParam("trade_no") String trade_no,
                          @RequestParam("serialNumber") String serialNumber,
                          @RequestParam("money") String money,
                          @RequestParam("status") String status,
                          @RequestParam("t") String t,
                          @RequestParam("sign") String sign,
                          @RequestParam(value = "appid", required = false) String appid,
                          @RequestParam(value = "item_id", required = false) String item_id,
                          @RequestParam(value = "item_price", required = false) String item_price,
                          @RequestParam(value = "item_count", required = false) String item_count,
                          @RequestParam(value = "reserved", required = false) String reserved,
                          @RequestParam(value = "game_uin", required = false) String game_uin,
                          HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        JSONObject data = new JSONObject();

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("trade_no", trade_no);
        parameterMap.put("serialNumber", serialNumber);
        parameterMap.put("money", money);
        parameterMap.put("status", status);
        parameterMap.put("t", t);
        parameterMap.put("sign", sign);
        parameterMap.put("appid", appid);
        parameterMap.put("item_id", item_id);
        parameterMap.put("item_price", item_price);
        parameterMap.put("item_count", item_count);
        parameterMap.put("reserved", reserved);
        parameterMap.put("game_uin", game_uin);

        log.info("parameterMap =" + parameterMap.toString());

        boolean result = checkOrder(appId, channelId, parameterMap, data, serialNumber, trade_no, money);

        ResponseUtil.write(response, result ? "success" : "fail");
    }

    /**
     * 游戏fan
     * <p>
     */
    @RequestMapping(value = "/callbackPayInfo/h5_youxifun/{channelId}/{appId}", method = RequestMethod.POST)
    @ResponseBody
    public void h5_youxifun(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                            @RequestBody String datas,
                            HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);


        String[] body = URLDecoder.decode(datas, String.valueOf(StandardCharsets.UTF_8)).split("&");

        JSONObject data = new JSONObject();
        Map<String, String> parameterMap = new HashMap<>();

        Integer len = body.length;
        StringBuilder param = new StringBuilder();
        for (int index = 0; index < len; index++) {
            String s = body[index];
            String[] p = s.split("=");
            String key = p[0];
            String value = p.length == 2 ? p[1] : "";
            System.out.println("key = " + key + "[" + value + "]");
            parameterMap.put(key, value);
            data.put(key, value);
            if (key.equals("userId") || key.equals("sign")) {
            } else {
                if (index > 1) {
                    param.append("&");
                }
                param.append(s);
            }
        }

        parameterMap.put("param", param.toString());

        String cpOrderNo = data.getString("attach");
        String channelOrder = data.getString("orderid");
        String amount = data.getString("amount");

        boolean result = checkOrder(appId, channelId, parameterMap, data, cpOrderNo, channelOrder, amount);

        ResponseUtil.write(response, result ? "success" : "error");
    }

    /**
     * 大秦
     * <p>
     * 接收参数(CGI)	类型	        必选	参于加密	说明
     * status		    String	    是	是	    订单状态。“success”为支付成功
     * cpOrderId		String	    是	是	    cp游戏订单号。
     * orderId		    String	    是	是	    欢聚游微游戏订单号
     * uid		        string	    是	是	    欢聚游微游戏用户的uid
     * userName		    string	    是	是	    欢聚游微游戏的用户名
     * money		    decimal	    是	是	    支付钱数(元),保留2位小数
     * gameId		    String	    是	是	    游戏的id
     * goodsId		    String	    是	是	    商品ID
     * goodsName		String	    是	是	    商品名
     * server		    String	    是	是	    支付的游戏服
     * role		        String	    是	是	    支付时角色信息,
     * time		        int	        是  是	    当前时间unix时间戳
     * ext		        String(200)	否	否	    额外透传参数(原样返回)
     * sign		        string	    是	否	    加密串
     * signType		    string	    是	否	    固定md5
     */
    @RequestMapping(value = "/callbackPayInfo/h5_daqin/{channelId}/{appId}")
    @ResponseBody
    public String h5_daqin(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
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
                           @RequestParam(value = "ext", required = false) String ext,
                           @RequestParam("signType") String signType,
                           @RequestParam("sign") String sign,
                           HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

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

        log.info("parameterMap =" + parameterMap.toString());

        JSONObject channelOrder = new JSONObject();

        boolean result = checkOrder(appId, channelId, parameterMap, channelOrder, cpOrderId, orderId, money);
//        ResponseUtil.write(response, result ? "success" : "fail");
        return result ? "success" : "fail";
    }

    /**
     * 盛娱
     *
     * @param datas datas.origin_order_no 原始订单号
     *              datas.order_id        平台订单号
     *              datas.type            充值类型（direct 平台直充 order 订单充值）
     *              datas.money           充值金额(单位分)
     *              datas.game_id         游戏ID
     *              datas.game_area       区服ID
     *              datas.pay_channel     支付渠道     wechat     alipay    coin
     *              datas.status          订单状态 1 表示成功，只有成功才会发通知
     *              datas.complete_at     成功时间戳
     *              datas.extra_data      透传参数
     *              datas.sign            签名字符串
     */
    @RequestMapping(value = "/callbackPayInfo/h5_shengyu/{channelId}/{appId}")
    @ResponseBody
    public void h5_shengyu(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                           @RequestBody String datas,
                           HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        JSONObject data = JSONObject.parseObject(datas);

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("origin_order_no", data.getString("origin_order_no"));
        parameterMap.put("order_id", data.getString("order_id"));
        parameterMap.put("type", data.getString("type"));
        parameterMap.put("money", data.getString("money"));
        parameterMap.put("game_id", data.getString("game_id"));
        parameterMap.put("game_area", data.getString("game_area"));
        parameterMap.put("pay_channel", data.getString("pay_channel"));
        parameterMap.put("status", data.getString("status"));
        parameterMap.put("complete_at", data.getString("complete_at"));
        parameterMap.put("extra_data", data.getString("extra_data"));
        parameterMap.put("sign", data.getString("sign"));

        log.info("parameterMap =" + parameterMap.toString());
        String yuan = FeeUtils.fenToYuan(data.getString("money"));
        boolean result = checkOrder(appId, channelId, parameterMap, data, data.getString("origin_order_no"), data.getString("order_id"), yuan);

        JSONObject rsp = new JSONObject();
        rsp.put("status", result ? "ok" : "fail");
        ResponseUtil.write(response, rsp);
    }

    /**
     * 游心
     * 回调body已经过url encode，使用时要使用url decode进行解密等到json
     * <p>
     *
     * @param amount         金额，单位为分
     * @param channel_source 数据来源
     * @param game_appid     游戏编号----运营方为游戏分配的唯一编号
     * @param out_trade_no   渠道方订单号
     * @param payplatform2cp 用于 CP 要求平台特别传输其他参数，默认是访问 ip
     * @param trade_no       游戏透传参数（默认为游戏订单号，回调时候原样返回）
     * @param sign           按照上方签名机制进行签名
     */
    @RequestMapping(value = "/callbackPayInfo/h5_youxin/{channelId}/{appId}", method = RequestMethod.GET)
    @ResponseBody
    public void h5_youxin(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                          @RequestParam("amount") String amount,
                          @RequestParam("channel_source") String channel_source,
                          @RequestParam("game_appid") String game_appid,
                          @RequestParam("out_trade_no") String out_trade_no,
                          @RequestParam("payplatform2cp") String payplatform2cp,
                          @RequestParam("trade_no") String trade_no,
                          @RequestParam("sign") String sign,
                          HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        JSONObject data = new JSONObject();

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("amount", amount);
        parameterMap.put("channel_source", channel_source);
        parameterMap.put("game_appid", game_appid);
        parameterMap.put("out_trade_no", out_trade_no);
        parameterMap.put("payplatform2cp", payplatform2cp);
        parameterMap.put("trade_no", trade_no);
        parameterMap.put("sign", sign);

        log.info("parameterMap =" + parameterMap.toString());

        String money = FeeUtils.fenToYuan(amount);
        boolean result = checkOrder(appId, channelId, parameterMap, data, trade_no, out_trade_no, money);

        JSONObject rsp = new JSONObject();
        rsp.put("status", result ? "success" : "fail");
        ResponseUtil.write(response, rsp);
    }


    /**
     * 天天玩
     * 回调body已经过url encode，使用时要使用url decode进行解密等到json
     * <p>
     *
     * @param actor_id
     * @param app_id       分配给游戏里的gameId
     * @param app_user_id  游戏内的userid，允许自定义。不做校验，下单值原样返回
     * @param real_amount  充值金额
     * @param app_order_id 传入的订单号，允许自定义。不做校验，下单值原样返回
     * @param order_id     平台的订单号，在充值未返回success时，会在后续进行补单，游戏需防止重复发放奖励
     * @param payment_time 充值时间
     * @param ext          玩家所在区服，用于区分统计
     * @param sign         平台计算的签名
     */
    @RequestMapping(value = "/callbackPayInfo/h5_tiantianwan/{channelId}/{appId}", method = RequestMethod.GET)
    @ResponseBody
    public void h5_tiantianwan(@PathVariable("channelId") Integer channelId, @PathVariable("appId") Integer appId,
                               @RequestParam("actor_id") String actor_id,
                               @RequestParam("app_id") String app_id,
                               @RequestParam("app_user_id") String app_user_id,
                               @RequestParam("real_amount") String real_amount,
                               @RequestParam("app_order_id") String app_order_id,
                               @RequestParam("order_id") String order_id,
                               @RequestParam("payment_time") String payment_time,
                               @RequestParam("ext") String ext,
                               @RequestParam("sign") String sign,
                               HttpServletRequest request, HttpServletResponse response) throws Exception {
        log.info("callbackPayInfo:" + channelId);
        log.info("callbackPayInfo:" + appId);

        JSONObject data = new JSONObject();

        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("actor_id", actor_id);
        parameterMap.put("app_id", app_id);
        parameterMap.put("app_user_id", app_user_id);
        parameterMap.put("real_amount", real_amount);
        parameterMap.put("app_order_id", app_order_id);
        parameterMap.put("order_id", order_id);
        parameterMap.put("payment_time", payment_time);
        parameterMap.put("ext", ext);
        parameterMap.put("sign", sign);

        log.info("parameterMap =" + parameterMap.toString());

//        String money = FeeUtils.fenToYuan(amount);
        boolean result = checkOrder(appId, channelId, parameterMap, data, app_order_id, order_id, real_amount);

        JSONObject rsp = new JSONObject();
        rsp.put("status", result ? "success" : "fail");
        ResponseUtil.write(response, rsp);
    }


}
