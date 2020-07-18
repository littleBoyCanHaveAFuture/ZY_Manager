package com.zyh5games.sdk.channel.zhiyue3;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.entity.Account;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.sdk.channel.baijia.BaiJiaConfig;
import com.zyh5games.service.AccountService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 指悦
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("37")
public class ZhiYue3BaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(ZhiYue3BaseChannel.class);
    @Resource
    private AccountService accountService;

    ZhiYue3BaseChannel() {
        channelId = ChannelId.H5_ZhiYue3;
        channelName = "指悦分渠道3";
    }

    /**
     * 1.渠道初始化 加载渠道js文件
     *
     * @return boolean
     */
    @Override
    public JSONObject channelLib(Integer appId) {
        JSONObject channelData = new JSONObject();
        channelData.put("name", "ZhiYueH5");
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
        return null;
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
        String channelId = String.valueOf(map.get("ChannelCode")[0]);
        String channelUid = String.valueOf(map.get("ChannelUid")[0]);

        Account account = accountService.findUserBychannelUid(channelId, channelUid);
        if (account != null) {
            setUserData(userData, channelUid, "", channelId, "");
            return true;
        } else {
            setUserData(userData, "", "", channelId, "");
            return false;
        }
    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     *                       channelId          必传     QuickSDK后台自动分配的渠道参数
     *                       gameKey	        必传	    QuickSDK后台自动分配的游戏参数
     *                       uid	            必传	    渠道UID
     *                       username	        必传	    渠道username
     *                       userRoleId	        必传	    游戏内角色ID
     *                       userRoleName	    必传	    游戏角色
     *                       serverId	        必传	    角色所在区服ID
     *                       userServer	        必传	    角色所在区服
     *                       userLevel	        必传	    角色等级
     *                       cpOrderNo	        必传	    游戏内的订单,服务器通知中会回传
     *                       amount	            必传	    购买金额（元）
     *                       count	            必传	    购买商品个数
     *                       quantifier	        必传	    购买商品单位，如，个
     *                       subject	        必传	    道具名称
     *                       desc	            必传	    道具描述
     *                       callbackUrl	    选传	    服务器通知地址
     *                       extrasParams	    选传	    透传参数,服务器通知中原样回传
     *                       goodsId	        必传	    商品IDF
     * @param channelOrderNo 渠道订单返回参数
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");

        JSONObject data = new JSONObject();
        data.put("orderId", orderData.getString("zhiyueOrderId"));
        data.put("body", orderData.getString("desc"));
        data.put("subject", orderData.getString("subject"));
        data.put("totalAmount", orderData.getString("amount"));
        data.put("productId", orderData.getString("goodsId"));
        data.put("passBackParams", orderData.getString("extrasParams"));
        channelOrderNo.put("data", data);
        channelOrderNo.put("spId", orderData.getString("channelId"));
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
        String channelGameId = configMap.get(appId).getString(BaiJiaConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(BaiJiaConfig.PAY_KEY);

        return true;
    }
}
