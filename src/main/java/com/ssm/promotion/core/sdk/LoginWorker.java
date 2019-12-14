package com.ssm.promotion.core.sdk;

import com.ssm.promotion.core.service.ServerListService;
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

    /**
     * 登陆校验
     * ：白名单玩家
     * 是否白名单状态，且玩家是否在白名单中
     */
    public boolean isWhiteCanLogin(int accountId, String channelUid) {
        if (LoginWorker.whiteListState == 1) {
//            List<CacheWhiteList.Builder> list = this.whiteListTao.select(param);
//            if (!list.isEmpty() && list.get(NumberUtil.ZERO).getState() == 0) {
//                return false;
//            } else {
//                return true;
//            }
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

}
