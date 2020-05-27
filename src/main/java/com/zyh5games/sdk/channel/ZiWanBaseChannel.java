package com.zyh5games.sdk.channel;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.entity.Account;
import com.zyh5games.sdk.ChannelId;
import com.zyh5games.sdk.HttpService;
import com.zyh5games.service.AccountService;
import com.zyh5games.util.MD5Util;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 紫菀/骆驼
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("8")
public class ZiWanBaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(ZiWanBaseChannel.class);
    @Autowired
    HttpService httpService;
    @Autowired
    AccountService accountService;

    ZiWanBaseChannel() {
        configMap = new HashMap<>();
        channelId = ChannelId.H5_ZIWAN;
    }

    /**
     * 1.渠道初始化 加载渠道js文件
     */
    @Override
    public JSONObject channelLib() {
        JSONObject channelData = new JSONObject();
        channelData.put("name", "LuoTuoH5");
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
        return map.get("userToken")[0];
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

        if (!map.containsKey("channel_id") || !map.containsKey("userToken") || !map.containsKey("other")) {
            return false;
        }
        String ziwanChannelId = map.get("channel_id")[0];
        String userToken = map.get("userToken")[0];
        String other = map.get("other")[0];

        String loginKey = configMap.get(appId).getString(ZiWanConfig.KEY);

        //升序排列
        StringBuilder param = new StringBuilder();
        param.append("channel_id").append("=").append(ziwanChannelId);
        param.append("&").append("userToken").append("=").append(userToken);

        String sign = MD5Util.md5(param.toString() + loginKey);
        param.append("&").append("sign").append("=").append(sign);

        String url = ZiWanConfig.LOGIN_URL + "&" + param.toString();
        log.info("channelLogin = " + url);

        JSONObject jsonObject = httpService.httpGet(url);
        // userinfo (获取到的用户信息，status为1001时有，包含wechaname，用户名称；portrait，用户头像；sex，性别；city，城市；province 省会;openid 用户标识，uid 用户ID)
        if (jsonObject.containsKey("status") && jsonObject.getInteger("status") == 1001) {
            log.info("紫菀平台 登录校验成功");

            JSONObject userinfo = jsonObject.getJSONObject("userinfo");

            String wechaname = userinfo.getString("wechaname");
            String portrait = userinfo.getString("portrait");
            String sex = userinfo.getString("sex");
            String city = userinfo.getString("city");
            String province = userinfo.getString("province");
            String openid = userinfo.getString("openid");
            String uid = userinfo.getString("uid");

            setUserData(userData, uid, wechaname, String.valueOf(channelId), openid);
            return true;
        } else {
            setUserData(userData, "", "", String.valueOf(channelId), "");
            return false;
        }
    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     * @param channelOrderNo 渠道订单返回参数
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String channelToken = orderData.getString("channelToken");
        String channel_Id = configMap.get(appId).getString(ZiWanConfig.CHANNEL_ID);
        String secretKey = configMap.get(appId).getString(ZiWanConfig.KEY);

        StringBuilder param = new StringBuilder();
        //升序排列-参数赋值 并签名
        param.append("channel_id").append("=").append(channel_Id);
        param.append("&").append("item_id").append("=").append(orderData.get("goodsId"));
        param.append("&").append("orderid").append("=").append(orderData.get("cpOrderNo"));
        param.append("&").append("other").append("=").append(orderData.getString("other"));
        param.append("&").append("price").append("=").append(orderData.get("amount"));
        param.append("&").append("userToken").append("=").append(channelToken);


        String sign = MD5Util.md5(param.toString() + secretKey);
        param.append("&").append("sign").append("=").append(sign);

        String url = ZiWanConfig.PAY_URL + "&" + param;
        log.info("channelPayInfo = " + url);

        JSONObject rsp = httpService.httpGet(url);
        if (rsp.containsKey("status") && rsp.getInteger("status") == 1001) {
            String info = rsp.getString("info");
            String domain = rsp.getString("domain");

            JSONObject data = rsp.getJSONObject("data");

            channelOrderNo.put("domain", domain);
            channelOrderNo.put("data", data.toJSONString());
            return true;
        }

        return false;
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

        String[] mustKey = {"openid", "price", "other", "item_id", "orderid", "sign"};
        for (String key : mustKey) {
            if (!parameterMap.containsKey(key)) {
                System.out.println("channelPayCallback 缺少key：" + key);
                return false;
            }
        }

        String channel_Id = configMap.get(appId).getString(ZiWanConfig.CHANNEL_ID);
        String secretKey = configMap.get(appId).getString(ZiWanConfig.KEY);

        String orderId = parameterMap.get("orderid");
        String price = parameterMap.get("price");

        //升序排列
        StringBuilder param = new StringBuilder();
        param.append("channel_id").append("=").append(channel_Id);
        param.append("&").append("orderid").append("=").append(orderId);

        //参数赋值 并签名
        String sign = MD5Util.md5(param.toString() + secretKey);
        param.append("&").append("sign").append("=").append(sign);

        String url = ZiWanConfig.CHECK_ORDER_URL + "&" + param;
        log.info("channelPayCallback = " + url);

        JSONObject rsp = httpService.httpGet(url);

        if (rsp.containsKey("status")) {
            if (rsp.getInteger("status") == 1001) {
                String openid = rsp.getString("openid");
                String time = rsp.getString("time");
                String item_id = rsp.getString("item_id");
                String orderid = rsp.getString("orderid");

                log.info("channelPayCallback 支付成功");

                // openid 可能重复 需要处理 todo
                Account account = accountService.findUser(String.valueOf(ChannelId.H5_ZIWAN), openid);
                int zyUid = account == null ? 0 : account.getId();
                if (zyUid == 0) {
                    return false;
                }

                setChannelOrder(channelOrderNo, String.valueOf(zyUid), orderid, "", price);

                return true;
            } else if (rsp.getInteger("status") == 4001 || rsp.getInteger("status") == 4002) {
                log.info("channelPayCallback 参数缺少：");
            } else if (rsp.getInteger("status") == 1002) {
                log.info("channelPayCallback 订单未支付：");
            }
        }
        return false;
    }

    @Override
    public JSONObject ajaxGetSignature(Integer appId,JSONObject requestInfo) {
        String secretKey = configMap.get(appId).getString(ZiWanConfig.KEY);

        StringBuilder param = new StringBuilder();
        //升序排列-参数赋值 并签名
        param.append("area").append("=").append(requestInfo.getString("area"));
        param.append("&").append("channel_id").append("=").append(requestInfo.get("channel_id"));
        param.append("&").append("money").append("=").append(requestInfo.get("money"));
        param.append("&").append("new_role").append("=").append(requestInfo.getString("new_role"));
        param.append("&").append("rank").append("=").append(requestInfo.get("rank"));
        param.append("&").append("role_name").append("=").append(requestInfo.get("role_name"));
        param.append("&").append("userToken").append("=").append(requestInfo.get("userToken"));

        String sign = MD5Util.md5(param.toString() + secretKey);
        param.append("&").append("sign").append("=").append(sign);
        JSONObject rsp = new JSONObject();
        rsp.put("content", requestInfo);
        rsp.put("sign", sign);
        return rsp;
    }
}
