package com.zyh5games.sdk.channel.zhangmeng;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.sdk.channel.HttpService;
import com.zyh5games.sdk.channel.yuma.YuMaConfig;
import com.zyh5games.service.AccountService;
import com.zyh5games.util.IOSDesUtil;
import com.zyh5games.util.MD5Util;
import com.zyh5games.util.XmlUtils;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 顺网
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("11")
public class ZhangMengBaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(ZhangMengBaseChannel.class);
    @Autowired
    HttpService httpService;
    @Autowired
    AccountService accountService;

    ZhangMengBaseChannel() {
        configMap = new HashMap<>();
        channelId = ChannelId.H5_YUMA;
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
        channelData.put("name", "YuMaH5");

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
        String loginUrl = YuMaConfig.LOGIN_URL;

        StringBuilder param = new StringBuilder();

        for (String key : map.keySet()) {
            param.append("&").append(key).append("=").append(map.get(key)[0]);
        }

        String url = loginUrl + param.toString();
        System.out.println("channelLogin url = " + url);


        JSONObject rsp = httpService.httpGetJson(url);
        if (rsp != null && rsp.containsKey("userData")) {
            JSONObject quickUserData = rsp.getJSONObject("userData");

            System.out.println("quickUserData = " + quickUserData);
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
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String productCode = configMap.get(appId).getString(YuMaConfig.PRODUCT_CODE);

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

        /*向quick 下单参数
            orderInfo = new Object();
            orderInfo.productCode = “05425578266356246482673853629430”;
            orderInfo.uid = 'uid';
            orderInfo.username = 'username';
            orderInfo.userRoleId = 'roleId1';
            orderInfo.userRoleName = '小朋友';
            orderInfo.serverId= 1;
            orderInfo.userServer = '内测1区';
            orderInfo.userLevel = 1;
            orderInfo.cpOrderNo = 'cpOrderNo000001';
            orderInfo.amount = '0.01';
            orderInfo.subject = '大袋钻石';
            orderInfo.desc = '一大袋钻石60个';
            orderInfo.callbackUrl = '';
            orderInfo.extrasParams = '';
            orderInfo.goodsId = 'goods';
            orderInfo.count = 60;
            orderInfo.quantifier = '个';
        */
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
        String md5Key = configMap.get(appId).getString(YuMaConfig.MD5_KEY);
        String callBackKey = configMap.get(appId).getString(YuMaConfig.PAY_KEY);
        String nt_data = parameterMap.get("nt_data");
        String sign = parameterMap.get("sign");
        String md5Sign = parameterMap.get("md5Sign");

        String param = nt_data + sign + md5Key;
        String serverMd5Sign = MD5Util.md5(param);

        System.out.println("channelPayCallback sign: " + serverMd5Sign);
        System.out.println("channelPayCallback sign: " + md5Sign);

        if (!md5Sign.equals(serverMd5Sign)) {
            return false;
        }
        // 解析 nt_data
        String orderData = IOSDesUtil.decode(nt_data, callBackKey);
        System.out.println("orderData = " + orderData);
        JSONObject quickOrder = XmlUtils.getQuickOrderXml(orderData);
        if (quickOrder == null || quickOrder.isEmpty()) {
            return false;
        }
        parameterMap.put("quickOrder", quickOrder.toJSONString());
        return true;
    }

    @Override
    public boolean channelLoginCheck(JSONObject data, String clientToken, String serverToken) {
        Integer appId = data.getInteger("appId");
        String channelUid = data.getString("channelUid");
        String channelId = data.getString("channelId");

        boolean res = false;
        String loginCheckUrl = YuMaConfig.LOGIN_CHECK_URL;
        String productCode = configMap.get(appId).getString(YuMaConfig.PRODUCT_CODE);


        /*
         *
         * token          必须      游戏客户端从SDK客户端中获取的token值,原样传递无需解密,此值长度范围小于512,CP需预留足够长度
         * product_code   必须      Quick后台查看游戏信息里可获取此值
         * uid            必须      从客户端接口获取到的渠道原始uid,无需任何加工如拼接渠道ID等
         * channel_code	  选传	   传入此值将校验uid和token是否与channel_code一致,若游戏运营过程有发生玩家渠道间帐号转移,应确保此值正确 （该接口为客户端文档中的渠道类型接口）
         * */
        StringBuilder param = new StringBuilder();
        param.append("&").append("token").append("=").append(clientToken);
        param.append("&").append("product_code").append("=").append(productCode);
        param.append("&").append("uid").append("=").append(channelUid);

        String url = loginCheckUrl + param.toString();

        String rsp = httpService.httpGetString(url);
        if ("1".equals(rsp)) {
            res = true;
        }
        return res;
    }
}