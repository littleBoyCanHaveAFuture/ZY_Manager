package com.ssm.promotion.core.controller;

import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.entity.RechargeSummary;
import com.ssm.promotion.core.entity.ServerInfo;
import com.ssm.promotion.core.service.RechargeSummaryService;
import com.ssm.promotion.core.service.ServerListService;
import com.ssm.promotion.core.util.ResponseUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * @author song minghua
 * @date 2019/11/26
 */
@Controller
@RequestMapping("/rechargeSummary")
public class RechargeSummaryControllr {
    private static final Logger log = Logger.getLogger(RechargeSummaryControllr.class);
    @Resource
    private RechargeSummaryService rechargeSummaryServices;
    @Resource
    private ServerListService serverService;
    @Autowired
    private HttpServletRequest request;

    private Integer getUserId() {
        HttpSession session = request.getSession();
        return (Integer) session.getAttribute("userId");
    }


    @RequestMapping(value = "/searchRechargeSummary", method = RequestMethod.POST)
    @ResponseBody
    public void searchRechargeSummary(Integer type,
                                      Integer gameId, Integer serverId, String spId,
                                      String startTime, String endTime,
                                      HttpServletResponse response) throws Exception {
        System.out.println("searchRS:");
        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }

        Map<String, Object> map = new HashMap<>(6);
        map.put("type", type);
        map.put("gameId", gameId);
        map.put("serverId", serverId);
        map.put("spId", spId);
        map.put("startTime", startTime);
        map.put("endTime", endTime);

        long s = System.currentTimeMillis();
        System.out.println("start:" + s);
        List<RechargeSummary> list = null;
        switch (type) {
            case 1:
                break;
            case 2:
                Map<String, Object> searchMap = new HashMap<>(1);
                searchMap.put("gameId", gameId);
                //游戏区服列表
                List<Integer> serverIdList = this.getServerList(searchMap, 1, userId);
                if (serverIdList.size() == 0) {
                    break;
                }
                Map<Integer, List<String>> serverIdMap = this.getServerSpMap(searchMap, serverIdList, userId);
                list = this.rechargeSummaryServices.getRechargeSummary(map, serverIdMap, null, userId);
                break;
            case 3: {
                //游戏分渠道查看数据
                //需要参数 gameId serverId
                //查询渠道
                if (gameId == null || serverId == null || gameId == -1 || serverId == -1) {
                    //                ResponseUtil.
                    break;
                }
                List<String> spidStrList = this.getSpIdList(map, 3, userId);
                if (spidStrList.size() == 0) {
                    break;
                }
                //查询redis
                list = this.rechargeSummaryServices.getRechargeSummary(map, null, spidStrList, userId);
            }
            break;
            default:
                break;
        }

        JSONObject result = new JSONObject();
        JSONArray jsonArray = JSONArray.fromObject(list);
        result.put("rows", jsonArray);
        result.put("total", list == null ? 0 : list.size());
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, result);

        System.out.println("request: rechargeSummary/searchRechargeSummary , map: " + result.toString());
        log.info("request: rechargeSummary/searchRechargeSummary , map: " + map.toString());
        long e = System.currentTimeMillis();
        System.out.println("end:" + e);
        System.out.println("use:" + (e - s));
    }

    /**
     * 查询该游戏所有渠道id
     */
    public List<String> getSpIdList(Map<String, Object> map, Integer type, Integer userId) {
        List<String> spIdStrList = new LinkedList<>();
        List<Integer> spIdIntList = new LinkedList<>();
        List<String> serverInfos = serverService.getDistinctServerInfo(map, type, userId);
        for (String spIdList : serverInfos) {
            if (spIdList.contains(",")) {
                String[] spilt = spIdList.split(",");
                for (String spId : spilt) {
                    if (!spIdIntList.contains(spId)) {
                        spIdIntList.add(Integer.parseInt(spId));
                    }
                    spIdStrList.add(spId);
                }
            } else {
                Integer spId = Integer.parseInt(spIdList);
                if (!spIdIntList.contains(spId)) {
                    spIdIntList.add(spId);
                }
                spIdStrList.add(spIdList);
            }
        }
        return spIdStrList;
    }

    /**
     * 查询该游戏所有区服id
     */
    public List<Integer> getServerList(Map<String, Object> map, Integer type, Integer userId) {
        List<Integer> serverIntList = new LinkedList<>();
        List<String> serverInfos = serverService.getDistinctServerInfo(map, type, userId);
        for (String serverIdStr : serverInfos) {
            Integer serverId = Integer.parseInt(serverIdStr);
            if (!serverIntList.contains(serverId)) {
                serverIntList.add(serverId);
            }
        }
        return serverIntList;
    }

    /**
     * mysql
     * 获取区服对应的渠道列表
     *
     * @param serverIdList 游戏服务器列表
     */
    public Map<Integer, List<String>> getServerSpMap(Map<String, Object> map, List<Integer> serverIdList, Integer userId) {
        Map<Integer, List<String>> maplist = new LinkedHashMap<>(serverIdList.size());

        //游戏区服对应渠道列表
        List<ServerInfo> serverInfos = serverService.getServerList(map, userId);
        for (int serverId : serverIdList) {
            List<String> serverSpList = new LinkedList<>();
            for (ServerInfo info : serverInfos) {
                if (info.getServerId() == serverId) {
                    serverSpList.add(info.getSpId());
                }
            }
            maplist.put(serverId, serverSpList);
        }

        return maplist;
    }
}
