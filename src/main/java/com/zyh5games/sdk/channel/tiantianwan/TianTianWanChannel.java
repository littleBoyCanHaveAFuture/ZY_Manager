package com.zyh5games.sdk.channel.tiantianwan;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.entity.Account;
import com.zyh5games.sdk.GameRoleWorker;
import com.zyh5games.sdk.channel.BaseChannel;
import com.zyh5games.sdk.channel.ChannelId;
import com.zyh5games.service.AccountService;
import com.zyh5games.util.FeeUtils;
import com.zyh5games.util.MD5Util;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 模板
 *
 * @author song minghua
 * @date 2020/5/21
 */
@Component("32")
public class TianTianWanChannel extends BaseChannel {
    private static final Logger log = Logger.getLogger(TianTianWanChannel.class);
    @Resource
    GameRoleWorker gameRoleWorker;
    @Resource
    private AccountService accountService;

    TianTianWanChannel() {
        channelId = ChannelId.H5_TianTianWan;
        channelName = "天天玩";
        configMap = new ConcurrentHashMap<>();
    }

    @Override
    public JSONArray commonLib() {
        JSONArray libUrl = super.commonLib();
//        libUrl.add("https://dayplay.pagecp.com/js/d2f_loader.js" + "?" + System.currentTimeMillis());
        libUrl.add("https://h5sdk-cdn.pagecp.com/js/jssdk/d2f.js" + "?" + System.currentTimeMillis());
        return libUrl;
    }

    /**
     * 1.渠道初始化 加载渠道js文件
     *
     * @return boolean
     */
    @Override
    public JSONObject channelLib(Integer appId) {
        JSONObject channelData = new JSONObject();
        channelData.put("name", "TianTianWanH5");
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
        String result = "false";
        // 玩家是否首次进该游戏
        if (map.containsKey("openId") && map.get("openId").length > 0) {
            String openId = map.get("openId")[0];
            String appId = map.get("GameId")[0];
            log.info("openId =" + openId);
            log.info("appId =" + appId);
            Account account = accountService.findUserBychannelUid(String.valueOf(channelId), openId);
            if (account != null) {
                String accountId = String.valueOf(account.getId());
                log.info("accountId =" + accountId);

                result = gameRoleWorker.existRole(accountId, appId) ? "false" : "true";
            } else {
                return "true";
            }
        }
        log.info("result =" + result);

        return result;
    }

    /**
     * 3.渠道登录<p>
     * 3.1 向渠道校验 获取用户数据 <p>
     * 3.2 设置token<p>
     *
     * @param map      渠道传入参数
     *                 openId           用户唯一标识
     *                 openKey          辅助标识
     *                 noice            时间戳  需要与服务器当前时间做比对 5分钟内有效
     *                 appId            游戏渠道id （将用于js的init使用）
     *                 sign             签名
     *                 serverId         服务器id
     * @param userData 渠道用户数据
     * @return boolean
     */
    @Override
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        String[] mustKey = {"openId", "openKey", "noice", "appId", "serverId"};
        if (!super.channelMustParam(mustKey, map)) {
            return false;
        }
        int appId = Integer.parseInt(map.get("GameId")[0]);
        int channelId = Integer.parseInt(map.get("ChannelCode")[0]);

        String loginKey = configMap.get(appId).getString(TianTianWanConfig.COMMON_KEY);

        String openId = map.get("openId")[0];
        String noice = map.get("noice")[0];
        long curr = System.currentTimeMillis() / 1000;
        if (curr - Long.parseLong(noice) > 5 * 60) {
            log.info("登录5分钟内有效 已超时 curr = " + curr);
            return false;
        }
        // 加密串
        String param = openId + noice + loginKey;

        log.info("param = " + param);

        // 签名验证
        String sign = map.get("sign")[0];
        String serverSign = MD5Util.md5(param);

        log.info("channelLogin serverSign = " + serverSign);
        log.info("channelLogin sign       = " + sign);

        if (!sign.equals(serverSign)) {
            setUserData(userData, "", "", String.valueOf(channelId), "");
            return false;
        }

        setUserData(userData, openId, "", String.valueOf(channelId), "");
        return true;
    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     * @param channelOrderNo 渠道订单返回参数
     *                       orderdata={
     *                       openId:             用户id,
     *                       openKey:            验证key,
     *                       orderNo:            研发订单id,
     *                       ext:server id        //从1开始
     *                       actor_id:           角色id	//最长30位
     *                       cproleid:           游戏研发方唯一角色id     //最长30位 同actor_id
     *                       subject:            商品名
     *                       }
     *                       amount 金额
     * @return boolean
     */
    @Override
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) {
        Integer appId = orderData.getInteger("appId");
        String channelGameId = configMap.get(appId).getString(TianTianWanConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(TianTianWanConfig.COMMON_KEY);


        String channelUid = orderData.getString("uid");
        String userRoleId = orderData.getString("userRoleId");
        String cpOrderNo = orderData.getString("cpOrderNo");
        String amount = orderData.getString("amount");
        String subject = orderData.getString("subject");
        String extrasParams = orderData.getString("extrasParams");


        // 渠道订单数据
        JSONObject data = new JSONObject();
        data.put("openId", channelUid);
        data.put("openKey", payKey);
        data.put("orderNo", cpOrderNo);
        data.put("ext", extrasParams);
        data.put("actor_id", userRoleId);
        data.put("cproleid", userRoleId);
        data.put("subject", subject);

        JSONObject rsp = new JSONObject();
        rsp.put("orderData", data);
        rsp.put("amount", FeeUtils.yuanToFen(amount));

        log.info("channelPayInfo data: " + rsp);
        channelOrderNo.put("data", rsp.toJSONString());

        return true;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId          指悦游戏id
     * @param parameterMap   渠道回调参数
     *                       actor_id
     *                       app_id       分配给游戏里的gameId
     *                       app_user_id  游戏内的userid，允许自定义。不做校验，下单值原样返回
     *                       real_amount  充值金额
     *                       app_order_id 传入的订单号，允许自定义。不做校验，下单值原样返回
     *                       order_id     平台的订单号，在充值未返回success时，会在后续进行补单，游戏需防止重复发放奖励
     *                       payment_time 充值时间
     *                       ext          玩家所在区服，用于区分统计
     *                       sign         平台计算的签名
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    @Override
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        String channelGameId = configMap.get(appId).getString(TianTianWanConfig.GAME_ID);
        String payKey = configMap.get(appId).getString(TianTianWanConfig.COMMON_KEY);

        // 加密串
        Set<String> keySet = parameterMap.keySet();
        Set<String> sortSet = new TreeSet<>(Comparator.naturalOrder());
        sortSet.addAll(keySet);
        StringBuilder param = new StringBuilder();
        boolean first = false;
        for (String key : sortSet) {
            if (key.equals("sign")) {
                continue;
            }
            String value = parameterMap.get(key);
            if (!first) {
                super.addParam(param, key, value);
                first = true;
            } else {
                super.addParamAnd(param, key, value);
            }
        }
        super.addParamAnd(param, "key", payKey);

        log.info("param = " + param.toString());

        // 签名验证
        String sign = parameterMap.get("sign");
        String serverSign = MD5Util.md5(param.toString());

        log.info("channelPayCallback serverSign = " + serverSign);
        log.info("channelPayCallback sign       = " + sign);


        if (!sign.equals(serverSign)) {
            return false;
        }
        String cpOrderId = parameterMap.get("app_order_id");
        String channelOrderId = parameterMap.get("order_id");
        String money = parameterMap.get("real_amount");

        // 渠道订单赋值
        setChannelOrder(channelOrderNo, "", cpOrderId, channelOrderId, money);
        return true;
    }
}
