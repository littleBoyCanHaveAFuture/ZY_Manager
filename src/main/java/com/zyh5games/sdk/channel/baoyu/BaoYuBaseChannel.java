package com.zyh5games.sdk.channel.baoyu;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.sdk.channel.HttpService;
import com.zyh5games.service.AccountService;
import com.zyh5games.util.IOSDesUtil;
import com.zyh5games.util.MD5Util;
import com.zyh5games.util.XmlUtils;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 暴雨-quick聚合
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("39")
public class BaoYuBaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(BaoYuBaseChannel.class);
    @Autowired
    HttpService httpService;
    @Autowired
    AccountService accountService;

    BaoYuBaseChannel() {
        configMap = new ConcurrentHashMap<>();
        channelId = ChannelId.H5_BaoYu;
        channelName = "暴雨";
    }

    @Override
    public JSONArray commonLib() {
        JSONArray libUrl = super.commonLib();
        libUrl.add("https://staticjs.quickapi.net/static/lib/libQuickSDK_v2.js");
        return libUrl;
    }

    /**
     * 1.渠道初始化 加载渠道js文件
     *
     * @return channelData 渠道数据
     * <p>
     * channelData.name 指悦对应渠道js文件
     * <p>
     * channelData.xxx 其他参数自定义
     */
    @Override
    public JSONObject channelLib(Integer appId) {
        JSONObject channelData = new JSONObject();
        channelData.put("name", "BaoYuH5");

        JSONObject config = configMap.get(appId);
        if (config != null && !config.isEmpty()) {
            JSONObject c = new JSONObject();
            c.put("ProductCode", config.getString("ProductCode"));
            c.put("ProductKey", config.getString("ProductKey"));
            channelData.put("config", c.toJSONString());
        }

        return channelData;
    }

    /**
     * 2.渠道初始化 设置渠道参数token
     *
     * @param map 渠道传入参数
     * @return boolean
     */
    @Override
    public String channelToken(Map<String, String[]> map) {
        if (map.containsKey("channelToken")) {
            return map.get("channelToken")[0];
        } else {
            return null;
        }
    }

    /**
     * 3.渠道登录<p>
     * 3.1 向渠道校验 获取用户数据 <p>
     * 3.2 设置token<p>
     *
     * @param map      渠道传入参数
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);
        String loginUrl = BaoYuConfig.LOGIN_URL;

        StringBuilder param = new StringBuilder();

        for (String key : map.keySet()) {
            super.addParamAnd(param, key, map.get(key)[0]);
        }

        String url = loginUrl + param.toString();

        JSONObject rsp = httpService.httpGetJson(url);
        if (rsp != null && rsp.containsKey("userData")) {
            JSONObject quickUserData = rsp.getJSONObject("userData");

            log.info("quickUserData = " + quickUserData);
            String channelUid = quickUserData.getString("uid");
            String username = quickUserData.containsKey("username") ? quickUserData.getString("username") : "";
            String token = quickUserData.containsKey("token") ? quickUserData.getString("token") : "";
            String isLogin = quickUserData.containsKey("isLogin") ? quickUserData.getString("isLogin") : "";
            String message = quickUserData.containsKey("message") ? quickUserData.getString("message") : "";

            setUserData(userData, channelUid, username, String.valueOf(channelId), "");
            // 渠道特殊数据
            userData.put("quickData", rsp);
            // 渠道token 需要校验
            userData.replace("token", token);

            return true;
        } else {
            setUserData(userData, "", "", String.valueOf(channelId), "");
            return false;
        }
    }

    /**
     * 4. 渠道调起支付 订单信息
     * 交给quick js
     *
     * @param orderData      渠道订单请求参数
     * @param channelOrderNo 渠道订单返回参数
     *                       orderInfo.productCode      = “05425578266356246482673853629430”;
     *                       orderInfo.uid              = 'uid';
     *                       orderInfo.username         = 'username';
     *                       orderInfo.userRoleId       = 'roleId1';
     *                       orderInfo.userRoleName     = '小朋友';
     *                       orderInfo.serverId         = 1;
     *                       orderInfo.userServer       = '内测1区';
     *                       orderInfo.userLevel        = 1;
     *                       orderInfo.cpOrderNo        = 'cpOrderNo000001';
     *                       orderInfo.amount           = '0.01';
     *                       orderInfo.subject          = '大袋钻石';
     *                       orderInfo.desc             = '一大袋钻石60个';
     *                       orderInfo.callbackUrl      = '';
     *                       orderInfo.extrasParams     = '';
     *                       orderInfo.goodsId          = 'goods';
     *                       orderInfo.count            = 60;
     *                       orderInfo.quantifier       = '个';
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String productCode = configMap.get(appId).getString(BaoYuConfig.PRODUCT_CODE);

        String channelId = orderData.getString("channelId");
        String gameKey = orderData.getString("gameKey");
        String channelUid = orderData.getString("uid");
        String username = orderData.getString("username");
        String userRoleId = orderData.getString("userRoleId");
        String userRoleName = orderData.getString("userRoleName");

        String serverId = orderData.getString("serverId");
        String userServer = orderData.getString("userServer");
        String userLevel = orderData.getString("userLevel");
        String cpOrderNo = orderData.getString("cpOrderNo");
        String amount = orderData.getString("amount");

        String count = orderData.getString("count");
        String quantifier = orderData.getString("quantifier");
        String subject = orderData.getString("subject");
        String desc = orderData.getString("desc");
        String callbackUrl = orderData.getString("callbackUrl");

        String extrasParams = orderData.getString("extrasParams");
        String goodsId = orderData.getString("goodsId");
        String channelToken = orderData.getString("channelToken");

        //js调用支付，这里给正确格式即可
        JSONObject reqData = new JSONObject();
        reqData.put("productCode", productCode);
        reqData.put("uid", channelUid);
        reqData.put("username", username);
        reqData.put("userRoleId", userRoleId);
        reqData.put("userRoleName", userRoleName);
        reqData.put("serverId", serverId);
        reqData.put("userServer", userServer);
        reqData.put("userLevel", userLevel);
        reqData.put("cpOrderNo", cpOrderNo);
        reqData.put("amount", amount);
        reqData.put("subject", subject);
        reqData.put("desc", desc);
        reqData.put("callbackUrl", callbackUrl);
        reqData.put("extrasParams", extrasParams);
        reqData.put("goodsId", goodsId);
        reqData.put("count", count);
        reqData.put("quantifier", quantifier);

        channelOrderNo.put("reqData", reqData.toJSONString());

        return true;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId          指悦游戏id
     * @param parameterMap   渠道回调参数
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        //quick xml 解密
        String md5Key = configMap.get(appId).getString(BaoYuConfig.MD5_KEY);
        String callBackKey = configMap.get(appId).getString(BaoYuConfig.PAY_KEY);
        String nt_data = parameterMap.get("nt_data");
        String sign = parameterMap.get("sign");
        String md5Sign = parameterMap.get("md5Sign");

        String param = nt_data + sign + md5Key;
        String serverMd5Sign = MD5Util.md5(param);

        log.info("channelPayCallback sign: " + serverMd5Sign);
        log.info("channelPayCallback sign: " + md5Sign);

        if (!md5Sign.equals(serverMd5Sign)) {
            return false;
        }

        /*
         * 解析 nt_data
         *  is_test	        string	必有	    是否为测试订单 1为测试 0为线上正式订单，游戏应根据情况确定上线后是否向测试订单发放道具。
         *  channel	        string	必有	    渠道标示ID 注意:游戏可根据实情,确定发放道具时是否校验充值来源渠道是否与该角色注册渠道相符
         *  channel_uid	    string	必有	    渠道用户唯一标示,该值从客户端GetUserId()中可获取
         *  game_order	    string	必有	    游戏在调用QuickSDK发起支付时传递的游戏方订单,这里会原样传回
         *  order_no	    string	必有	    QuickSDK唯一订单号
         *  pay_time	    string	必有	    支付时间 2015-01-01 23:00:00
         *  amount	        string	必有	    成交金额，单位元，游戏最终发放道具金额应以此为准
         *  status	        string	必有	    充值状态:0成功, 1失败(为1时 应返回FAILED失败)
         *  extras_params	string	必有	    可为空,充值状态游戏客户端调用SDK发起支付时填写的透传参数.没有则为空
         * */
        String orderData = IOSDesUtil.decode(nt_data, callBackKey);
        log.info("orderData = " + orderData);
        JSONObject quickOrder = XmlUtils.getQuickOrderXml(orderData);
        if (quickOrder == null || quickOrder.isEmpty()) {
            return false;
        }

        String cpOrderId = quickOrder.getString("game_order");
        String channelOrderId = quickOrder.getString("order_no");
        String money = quickOrder.getString("amount");

        setChannelOrder(channelOrderNo, "", cpOrderId, channelOrderId, money);
        return true;
    }

    @Override
    public boolean channelLoginCheck(JSONObject data, String clientToken, String serverToken) {
        Integer appId = data.getInteger("appId");
        String channelUid = data.getString("channelUid");
        String channelId = data.getString("channelId");

        boolean res = false;
        String loginCheckUrl = BaoYuConfig.LOGIN_CHECK_URL;
        String productCode = configMap.get(appId).getString(BaoYuConfig.PRODUCT_CODE);


        /*
         * token          必须      游戏客户端从SDK客户端中获取的token值,原样传递无需解密,此值长度范围小于512,CP需预留足够长度
         * product_code   必须      Quick后台查看游戏信息里可获取此值
         * uid            必须      从客户端接口获取到的渠道原始uid,无需任何加工如拼接渠道ID等
         * channel_code	  选传	   传入此值将校验uid和token是否与channel_code一致,
         *                          若游戏运营过程有发生玩家渠道间帐号转移,应确保此值正确 （该接口为客户端文档中的渠道类型接口）
         * */
        StringBuilder param = new StringBuilder();
        super.addParamAnd(param, "token", clientToken);
        super.addParamAnd(param, "product_code", productCode);
        super.addParamAnd(param, "uid", channelUid);

        String url = loginCheckUrl + param.toString();

        String rsp = httpService.httpGetString(url);
        if ("1".equals(rsp)) {
            res = true;
        }
        return res;
    }
}
