package com.zyh5games.controller;

import com.zyh5games.common.Constants;
import com.zyh5games.entity.GameNew;
import com.zyh5games.entity.Sp;
import com.zyh5games.jedis.JedisRechargeCache;
import com.zyh5games.service.ChannelConfigService;
import com.zyh5games.service.GameNewService;
import com.zyh5games.service.SpService;
import com.zyh5games.util.ResponseUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Administrator
 */
@Controller
@RequestMapping("/channel")
public class ChannelController {
    private static final Logger log = Logger.getLogger(ChannelController.class);
    @Autowired
    JedisRechargeCache cache;
    @Autowired
    private HttpServletRequest request;
    @Resource
    private GameNewService gameNewService;
    @Resource
    private ChannelConfigService channelConfigService;
    @Resource
    private SpService spService;

    private Integer getUserId() {
        //可以设置缓存 redis 登录时设置过期时间 24小时 todo
        HttpSession session = request.getSession();
        return (Integer) session.getAttribute("userId");
    }

    @RequestMapping(value = "/getAllGame", method = RequestMethod.GET)
    public void getGameInfo(HttpServletResponse response) throws Exception {
        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }

        JSONObject result = new JSONObject();
        //游戏id
        List<GameNew> gameIdList = gameNewService.selectGameIdList(-1);

        JSONArray rows = JSONArray.fromObject(gameIdList);
        result.put("rows", rows.toString());
        result.put("total", gameIdList.size());
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        ResponseUtil.write(response, result);
    }

    @RequestMapping(value = "/getAllChannel", method = RequestMethod.GET)
    public void getAllChannel(Integer gameId,
                              HttpServletResponse response) throws Exception {
        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }

        JSONObject result = new JSONObject();
        //游戏id
        List<Integer> channelIdList = channelConfigService.selectGameConfig(gameId, -1);

        List<Integer> serverIdList = new ArrayList(channelIdList);
        // 根据 渠道id 查询渠道名称
        List<Sp> list = spService.getSpName(serverIdList, -1);

        JSONArray rows = JSONArray.fromObject(list);
        result.put("rows", rows.toString());
        result.put("total", channelIdList.size());
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, result);
    }

    //查询区服
    @RequestMapping(value = "/getAllServerId", method = RequestMethod.GET)
    public void getAllServerId(Integer gameId, Integer spId,
                               HttpServletResponse response) throws Exception {
        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }

        JSONObject result = new JSONObject();
//        Set<String> channelIdSet;
//        if (spId == -1) {
//            channelIdSet = cache.getSPIDInfo(String.valueOf(gameId));
//            //渠道id
//            for (String channelId : channelIdSet) {
//                Set<String> serverIdSet = cache.getServerInfo(String.valueOf(gameId), channelId);
//                rsServerIdSet.addAll(serverIdSet);
//            }
//        } else {
//            Set<String> serverIdSet = cache.getServerInfo(String.valueOf(gameId), String.valueOf(spId));
//            rsServerIdSet.addAll(serverIdSet);
//        }

        Set<String> rsServerIdSet= cache.getGameServerInfo(String.valueOf(gameId));
        result.put("rows", rsServerIdSet.toString());
        result.put("total", rsServerIdSet.size());
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        ResponseUtil.write(response, result);
    }
}
