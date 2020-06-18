package com.zyh5games.sdk.channel.santang;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.util.MD5Util;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 三唐
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("20")
public class SanTangBaseChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(SanTangBaseChannel.class);

    static Map<String, String> userIdMap;

    SanTangBaseChannel() {
        channelId = ChannelId.H5_SANTANG;
        channelName = "三唐";
        configMap = new ConcurrentHashMap<>();
        userIdMap = new ConcurrentHashMap<>();
    }


    /**
     * 1.渠道初始化 加载渠道js文件
     *
     * @return boolean
     */
    @Override
    public JSONObject channelLib(Integer appId) {
        JSONObject channelData = new JSONObject();
        channelData.put("name", "SanTangH5");
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
     *                 参数名              备注                             说明
     *                 pf                  平台名                         固定值 3tang
     *                 sid                 游戏区服                        玩家真实所在区服，没有区服区服则 sid=1
     *                 openid              三唐开放平台 ID                  玩家开放平台唯一标志
     *                 userid              三唐用户名                      三唐开发平台分配的
     *                 isAdult             是否成年人                      0 未成年，1 成年人根据国家法规如果是未成年，CP 需要提示防成谜经验减半，三唐验证防成谜连接：http://www.3tang.com/my/safe.asp
     *                 logintime           登录时间(UnixTime)
     *                 sign                签名（md5 加密）                md5(openid&userid&logintime&sid&isAdult& APPKEY)   注意：&是变量连接符,不要放到加密里。
     *                 APPKEY              由三唐平台分配或者双方协定
     *                 iconurl             三唐平台玩家头像地址            URLEncode 编码
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        String[] mustKey = {"pf", "sid", "openid", "userid", "isAdult", "logintime", "sign", "iconurl"};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String loginKey = configMap.get(appId).getString(SanTangConfig.COMMON_KEY);

        String openid = map.get("openid")[0];
        String userid = map.get("userid")[0];
        String logintime = map.get("logintime")[0];
        String sid = map.get("sid")[0];
        String isAdult = map.get("isAdult")[0];

        // 加密串md5(openid&userid&logintime&sid&isAdult& APPKEY)注意：&是变量连接符,不要放到加密里。
        StringBuilder param = new StringBuilder();
        param.append(openid);
        param.append(userid);
        param.append(logintime);
        param.append(sid);
        param.append(isAdult);
        param.append(loginKey);

        log.info("param = " + param.toString());

        // 签名验证
        String sign = map.get("sign")[0];
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelLogin serverSign = " + serverSign);
        log.info("channelLogin sign       = " + sign);

        if (!sign.equals(serverSign)) {
            setUserData(userData, "", "", String.valueOf(channelId), "");
            return false;
        }
        if (userIdMap.containsKey(openid)) {
            userIdMap.replace(openid, userid);
        } else {
            userIdMap.put(openid, userid);
        }
        setUserData(userData, openid, userid, String.valueOf(channelId), "");
        return true;
    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     * @param channelOrderNo 渠道订单返回参数
     *                       参数名        是否必须       备注                   说明
     *                       userid         是           三唐用户名               三唐开发平台分配的
     *                       gid            是           游戏                    APPID
     *                       sid            是           游戏区服                不分区服默认 1
     *                       money          是           用户充值金额             Money 单位是元，比如 6 元则传值 6
     *                       gamename       是           游戏名字                中文需要 URLEncode编码
     *                       cp_trade_no    是           CP 方订单编号
     *                       openid         是           三唐开放平台openid
     *                       method         是           消息类型固定值 pay       注意本参数不参与签名
     *                       item           是           购买物品                例：购买 60 元宝,中文需要 URLEncode编码
     *                       gamerate       否           游戏兑换比例             例如如果兑换比例是1 元 10 元宝，则gamerate=10
     *                       ybcn           否           游戏虚拟货币名称         例：元宝、龙晶,中文需要 URLEncode编码
     *                       roleid         是           玩家角色 ID
     *                       rolename       是           玩家游戏角色名          中文需要 URLEncode编码
     *                       sign           是           md5(gid&sid&openid&userid&money& APPKEY)注：&是变量连接符,不要放到加密里。APPKEY 由三唐平台分配
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String channelGameId = configMap.get(appId).getString(SanTangConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(SanTangConfig.COMMON_KEY);


        String channelUid = orderData.getString("uid");
        String userRoleId = orderData.getString("userRoleId");
        String serverId = orderData.getString("serverId");
        String cpOrderNo = orderData.getString("cpOrderNo");
        String amount = orderData.getString("amount");
        String subject = orderData.getString("subject");

        String userId = userIdMap.get(channelUid);
        // 加密串
        StringBuilder param = new StringBuilder();
        param.append(channelGameId);
        param.append(serverId);
        param.append(channelUid);
        param.append(userId);
        param.append(amount);
        param.append(payKey);

        log.info("param = " + param.toString());

        String serverSign = MD5Util.md5(param.toString());
        // 渠道订单数据
        JSONObject data = new JSONObject();
        data.put("userid", userId);
        data.put("gid", channelGameId);
        data.put("sid", serverId);
        data.put("money", amount);


        data.put("cp_trade_no", cpOrderNo);
        data.put("openid", channelUid);
        data.put("method", "pay");
        try {
            if (appId == 14) {
                data.put("gamename", URLEncoder.encode("巨龙战歌", String.valueOf(StandardCharsets.UTF_8)));
                data.put("item", URLEncoder.encode(subject, String.valueOf(StandardCharsets.UTF_8)));
            }

            data.put("rolename", URLEncoder.encode(userRoleId, String.valueOf(StandardCharsets.UTF_8)));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        data.put("gamerate", "100");
//        data.put("ybcn", "元宝");
        data.put("roleid", userRoleId);
        data.put("sign", serverSign);

        log.info("channelPayInfo data: " + data);
        channelOrderNo.put("data", data.toJSONString());

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
        String channelGameId = configMap.get(appId).getString(SanTangConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(SanTangConfig.COMMON_KEY);

        // 加密串
        StringBuilder param = new StringBuilder();
        param.append(parameterMap.get("pf"));
        param.append(parameterMap.get("sid"));
        param.append(parameterMap.get("openid"));
        param.append(parameterMap.get("billDate"));
        param.append(parameterMap.get("st_trade_no"));
        param.append(parameterMap.get("cp_trade_no"));
        param.append(parameterMap.get("cash"));
        param.append(payKey);
        log.info("param = " + param.toString());

        // 签名验证
        String sign = parameterMap.get("sign");
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelPayCallback serverSign = " + serverSign);
        log.info("channelPayCallback sign       = " + sign);

        if (!sign.equals(serverSign)) {
            return false;
        }
        // 渠道订单赋值
        setChannelOrder(channelOrderNo, "", parameterMap.get("cp_trade_no"), parameterMap.get("st_trade_no"), parameterMap.get("cash"));
        return true;
    }
}
