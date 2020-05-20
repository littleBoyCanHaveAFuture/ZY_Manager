package com.ssm.promotion.core.sdk;

import com.alibaba.fastjson.JSONObject;
import com.ssm.promotion.core.service.ServerListService;
import com.ssm.promotion.core.util.MD5Util;
import com.ssm.promotion.core.util.RandomUtil;
import com.ssm.promotion.core.util.enums.ServiceType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/12/4
 */
@Component
public class LoginWorker {
    private static final Logger log = Logger.getLogger(LoginWorker.class);
    /**
     * 0关闭白名单，所有玩家可进。
     * 1开启白名单， 仅白名单列表玩家可进
     */
    public static int whiteListState = 0;
    @Resource
    ServerListService service;
    @Resource
    ChannelLogin channelLogin;

    /**
     * 登陆校验
     * ：白名单玩家
     * 是否白名单状态，且玩家是否在白名单中
     */
    public boolean isWhiteCanLogin(int accountId, String channelUid) {
        if (LoginWorker.whiteListState == 1) {
/*            List<CacheWhiteList.Builder> list = this.whiteListTao.select(param);
            if (!list.isEmpty() && list.get(NumberUtil.ZERO).getState() == 0) {
                return false;
            } else {
                return true;
            }*/
        }
        return true;
    }

    /**
     * 某游戏-某渠道
     * 用户是否可登录
     */
    public boolean isSpCanLogin(int appId, int channelId) {
        if (channelId == -1) {
            return false;
        }
        Map<String, Object> map = new HashMap<>();
        return service.isSpCanLogin(map, null);
    }

    public String getGameInfo(int accountId, int appId) {
        //生成token
        return LoginToken.getToken(accountId, appId, ServiceType.LOGIN);
    }

    /**
     * 渠道游戏登录 设置参数
     */
    public String loadLoginUrl(String loginUrl, Integer accountId, Integer appId, Integer serverId) {
        StringBuilder param = new StringBuilder();

        String key = String.valueOf(System.currentTimeMillis());
        switch (appId) {
            case AppId.cisha:
            case AppId.julongzhange:
                //指悦刺沙
                //巨龙战歌
                param.append("qid=").append(accountId);
                param.append("&server_id=").append(serverId);
                break;
            case 9999:
                param.append("qid=").append(accountId);
            default:
                break;
        }
        param.append("&time=").append(key);

        String sign = "";
        switch (appId) {
            case AppId.cisha:
                sign = MD5Util.md5(param.toString());
                break;
            case AppId.julongzhange:
                //指悦
                sign = MD5Util.md5(param.toString() + key);
                break;
            case 9999:
                sign = MD5Util.md5(param.toString());
            default:
                break;
        }

        param.append("&sign=").append(sign);

        return loginUrl + param.toString();
    }

    public void getLoginParams(Map<String, String[]> map) {
        //遍历
        for (Map.Entry<String, String[]> stringEntry : map.entrySet()) {
            //key值
            Object strKey = stringEntry.getKey();
            //value,数组形式
            String[] value = stringEntry.getValue();

            System.out.println(strKey.toString() + "=" + value[0]);
        }
    }


}
