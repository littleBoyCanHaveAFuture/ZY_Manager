package com.zyh5games.sdk.channel;

import com.alibaba.fastjson.JSONObject;
import com.zyh5games.entity.UOrder;
import com.zyh5games.util.RandomUtil;
import lombok.Data;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;

/**
 * @author song minghua
 * @date 2020/5/21
 */
@Data
public abstract class BaseChannel {
    private static final Logger log = Logger.getLogger(BaseChannel.class);
    /**
     * 渠道的游戏配置 <p>
     * 指悦游戏id-该渠道的配置
     * </p>
     */
    public Map<Integer, JSONObject> configMap;
    /**
     * 渠道id
     */
    public Integer channelId;
    /**
     * 是否开启
     */
    boolean isOpen;
    /**
     * 渠道名称
     */
    String channelName;

    /**
     * 从数据库加载配置
     */
    public void loadChannelConfig() {

    }

    /**
     * 1.渠道初始化 加载渠道js文件
     *
     * @return JSONArray
     */
    public JSONArray commonLib() {
        JSONArray libUrl = new JSONArray();
        libUrl.add("https://zyh5games.com/sdk/common/md5.js");
        libUrl.add("https://zyh5games.com/sdk/common/jquery-3.4.1.min.js");
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
    public JSONObject channelLib(Integer appId) {
        return null;
    }

    /**
     * 2.渠道初始化 设置渠道参数token
     *
     * @param map 渠道传入参数
     * @return boolean
     */
    public String channelToken(Map<String, String[]> map) {
        return "";
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
    public boolean channelLogin(Map<String, String[]> map, JSONObject userData) {
        return false;
    }

    /**
     * 4. 渠道调起支付 订单信息
     *
     * @param orderData      渠道订单请求参数
     * @param channelOrderNo 渠道订单返回参数
     * @return boolean
     */
    public boolean channelPayInfo(JSONObject orderData, JSONObject channelOrderNo) throws UnsupportedEncodingException {
        return false;
    }

    /**
     * 5.渠道支付订单校验
     *
     * @param appId          指悦游戏id
     * @param parameterMap   渠道回调参数
     * @param channelOrderNo 渠道回调校验成功后，设置向cp请求发货的数据格式
     */
    public boolean channelPayCallback(Integer appId, Map<String, String> parameterMap, JSONObject channelOrderNo) {
        return false;
    }

    /**
     * 返还给H5客户端的渠道用户数据
     */
    public void setUserData(JSONObject userData, String channelUid, String username, String channelId, String openid) {
        String token = RandomUtil.rndSecertKey();
        // 渠道uid
        userData.put("uid", channelUid);
        // 	渠道username
        userData.put("username", username);
        // 是否游客,登录后此值为true
        userData.put("isLogin", false);
        // 当前时间戳 单位：秒
        userData.put("time", System.currentTimeMillis() / 1000);
        // 	token 游戏服务器需通过v2/checkUserInfo接口(参见服务器接口文档)验证token和UID的正确性
        userData.put("token", token);
        // 渠道ID
        userData.put("channelId", channelId);
        // 渠道用户标识
        userData.put("openid", openid);
    }

    public void setChannelOrder(JSONObject channelOrderNo, String zyUid, String cpOrderId, String channelOrderId, String price) {
        channelOrderNo.put("zy_uid", zyUid);
        channelOrderNo.put("price", price);
        channelOrderNo.put("channelOrderId", channelOrderId);
        channelOrderNo.put("cpOrderId", cpOrderId);
    }

    public JSONObject ajaxGetSignature(Integer appId, JSONObject requestInfo, JSONObject result) {
        return null;
    }


    public boolean channelLoginCheck(JSONObject data, String clientToken, String serverToken) {
        boolean res = false;
        if (!clientToken.isEmpty() && clientToken.equals(serverToken)) {
            res = true;
        } else {
        }
        return res;
    }

    /**
     * 检查 key 是否存在
     */
    public boolean channelMustParam(String[] mustKey, Map<String, String[]> map) {
        for (String key : mustKey) {
            if (!map.containsKey(key)) {
                log.info("channelPayCallback 缺少key：" + key);
                return false;
            }
        }
        return true;
    }

    /**
     * 检查 key 是否存在
     */
    public boolean channelMustParamS(String[] mustKey, Map<String, String> map) {
        for (String key : mustKey) {
            if (!map.containsKey(key)) {
                System.out.println("channelPayCallback 缺少key：" + key);
                return false;
            }
        }
        return true;
    }

    /**
     * 检查 key 是否存在
     */
    public boolean channelMustParamJson(String[] mustKey, JSONObject jsonObject) {
        for (String key : mustKey) {
            if (!jsonObject.containsKey(key)) {
                System.out.println("channelPayCallback 缺少key：" + key);
                return false;
            }
        }
        return true;
    }

    public StringBuilder signMap(String[] signKey, Map<String, String> parameterMap) {
        StringBuilder param = new StringBuilder();
        Arrays.sort(signKey);

        boolean first = false;
        for (String key : signKey) {
            String value = parameterMap.get(key);
            if (!first) {
                addParam(param, key, value);
                first = true;
            } else {
                addParamAnd(param, key, value);
            }
        }
        log.info("param = " + param);
        return param;
    }

    public StringBuilder signMapNoKey(String[] signKey, Map<String, String> parameterMap) {
        StringBuilder param = new StringBuilder();
        Arrays.sort(signKey);

        boolean first = false;
        for (String key : signKey) {
            String value = parameterMap.get(key);
            param.append(value);
        }
        log.info("param = " + param);
        return param;
    }

    public void signJson(StringBuilder param, String[] signKey, JSONObject requestInfo) {
        Arrays.sort(signKey);

        boolean first = false;
        for (String key : signKey) {
            String value = requestInfo.getString(key);
            if (!first) {
                addParam(param, key, value);
                first = true;
            } else {
                addParamAnd(param, key, value);
            }
        }
        log.info("param = " + param);
    }

    public StringBuilder signJsonNoKey(String[] signKey, JSONObject requestInfo) {
        StringBuilder param = new StringBuilder();
        Arrays.sort(signKey);

        for (String key : signKey) {
            String value = requestInfo.getString(key);
            param.append(value);
        }
        log.info("param = " + param);
        return param;
    }

    /**
     * 检查 订单金额
     * 有折扣的怎么处理
     */
    public boolean checkOrderMoney(String channelMoney, UOrder order) {
        return true;
    }

    public void addParam(StringBuilder param, String key, String value) {
        param.append(key).append("=").append(value);
    }

    public void addParamAnd(StringBuilder param, String key, String value) {
        param.append("&").append(key).append("=").append(value);
    }

}
