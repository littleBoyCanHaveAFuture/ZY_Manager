package com.zyh5games.sdk;

import com.zyh5games.service.ServerListService;
import com.zyh5games.util.MD5Util;
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

    /**
     * 渠道游戏登录 设置参数
     * 旧版
     */
    public String loadLoginUrl(String loginUrl, Integer accountId, Integer appId, Integer serverId) {
        StringBuilder param = new StringBuilder();

        //加密字符串
        String key = String.valueOf(System.currentTimeMillis());
        switch (appId) {
            //指悦刺沙
            case AppId.H5_CISHA:
                param.append("qid=").append(accountId);
                param.append("&server_id=").append(serverId);
                break;
            //巨龙战歌
            case AppId.H5_JULONGZHANGE:
                break;
            case 9999:
                param.append("qid=").append(accountId);
                break;
            default:
                break;
        }
        param.append("&time=").append(key);

        // 字符串md5 加密
        String data = param.toString();

        //指悦
        if (appId == AppId.H5_JULONGZHANGE) {
            data += key;
        }

        String sign = MD5Util.md5(data);

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
