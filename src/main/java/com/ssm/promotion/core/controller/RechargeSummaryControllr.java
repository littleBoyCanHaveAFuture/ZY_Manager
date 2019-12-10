package com.ssm.promotion.core.controller;

import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.entity.RechargeSummary;
import com.ssm.promotion.core.entity.ServerInfo;
import com.ssm.promotion.core.service.RechargeSummaryService;
import com.ssm.promotion.core.service.ServerListService;
import com.ssm.promotion.core.util.DateUtil;
import com.ssm.promotion.core.util.ResponseUtil;
import com.ssm.promotion.core.util.ServerInfoUtil;
import com.ssm.promotion.core.util.StringUtil;
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
import java.text.DecimalFormat;
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

    /**
     * 获取当前用户 session
     */
    private Integer getUserId() {
        HttpSession session = request.getSession();
        return (Integer) session.getAttribute("userId");
    }

    /**
     * @param type      1/2/3 全服-时间排序/全区服-区服排序/全渠道-渠道排序
     * @param gameId    游戏id 不为空
     * @param serverId  区服id 查询对应游戏的区服 可为空,则查询数据库该游戏所有区服
     * @param spId      渠道id 查询对应游戏渠道 可为空
     * @param startTime 开始时间 jsp内格式：yyyy-MM-dd HH:mm
     * @param endTime   结束时间 jsp内格式：yyyy-MM-dd HH:mm
     */
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
        //检测数据
        if (type == null || type > 3 || type < 1) {
            return;
        }
        if (gameId == null || gameId == -1) {
            return;
        }
        if (serverId == null) {
            return;
        }
        if (spId == null) {
            return;
        }
        if (startTime.isEmpty()) {
            startTime = DateUtil.getCurrentDateStr();
        }
        if (endTime.isEmpty()) {
            endTime = DateUtil.getCurrentDateStr();
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

        List<RechargeSummary> list = this.getGameRechargeSummary(map, userId);

        long e = System.currentTimeMillis();
        System.out.println("end:" + e);
        System.out.println("use:" + (e - s));

        JSONObject result = new JSONObject();
        JSONArray jsonArray = JSONArray.fromObject(list);
        result.put("rows", jsonArray);
        result.put("total", list.size());
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        result.put("time", new DecimalFormat("0.00").format((double) (e - s) / 1000));
        ResponseUtil.write(response, result);

        System.out.println("request: rechargeSummary/searchRechargeSummary , map: " + result.toString());
        log.info("request: rechargeSummary/searchRechargeSummary , map: " + map.toString());

    }

    /**
     * 根据type获取不同的数据
     */
    public List<RechargeSummary> getGameRechargeSummary(Map<String, Object> map, Integer userId) throws Exception {
        int type = Integer.parseInt(map.get("type").toString());
        int gameId = Integer.parseInt(map.get("gameId").toString());
        int serverId = Integer.parseInt(map.get("serverId").toString());
        String spId = map.get("spId").toString();
        switch (type) {
            case 1:
            case 2: {
                Map<String, Object> searchMap = new HashMap<>(1);
                searchMap.put("gameId", gameId);
                //1.查询对应区服
                //游戏-所有区服列表
                List<Integer> serverIdList = this.getServerList(searchMap, userId);
                if (serverIdList.size() == 0) {
                    return null;
                }
                //查询的区服是否存在
                //查询所有的区服
                if (serverId != -1) {
                    if (!serverIdList.contains(serverId)) {
                        return null;
                    }
                    serverIdList.clear();
                    serverIdList.add(serverId);
                }
                //2.查询区服对应渠道
                Map<Integer, List<String>> serverSpIdMap = getServerSpIdList(searchMap, serverIdList, userId);
                //3.计算结果
                return this.rechargeSummaryServices.getRechargeSummary(map, serverSpIdMap, null, userId);
            }
            case 3: {
                //游戏分渠道查看数据
                //需要参数 gameId serverId
                if (serverId == -1) {
                    break;
                }
                List<String> spidStrList = this.getSpIdList(map, userId);
                if (spidStrList.size() == 0) {
                    break;
                }
                if (!StringUtil.isInteger(spId) || spId.equals("-1")) {
                    //不能转数字 或者 -1 则查询所有区服
                } else if (spidStrList.contains(spId)) {
                    spidStrList.clear();
                    spidStrList.add(spId);
                }
                //查询redis
                return this.rechargeSummaryServices.getRechargeSummary(map, null, spidStrList, userId);
            }
            default:
                break;
        }
        return null;
    }

    /**
     * 查询该游戏所有区服id
     */
    public List<Integer> getServerList(Map<String, Object> map, Integer userId) {
        List<String> serverInfos = serverService.getDistinctServerInfo(map, 1, userId);
        List<Integer> serverIntList = new LinkedList<>();

        for (String serverIdStr : serverInfos) {
            Integer serverId = Integer.parseInt(serverIdStr);
            if (!serverIntList.contains(serverId)) {
                serverIntList.add(serverId);
            }
        }
        return serverIntList;
    }

    /**
     * case = 3
     * 查询该游戏所有渠道id
     */
    public List<String> getSpIdList(Map<String, Object> map, Integer userId) {
        List<String> serverInfos = serverService.getDistinctServerInfo(map, 2, userId);
        return ServerInfoUtil.spiltStrList(serverInfos);
    }

    /**
     * @param searchMap
     * @param serverIdList 查询的区服列表
     * @param userId
     * @return map(serverId, List < Spid >)
     */
    public Map<Integer, List<String>> getServerSpIdList(Map<String, Object> searchMap, List<Integer> serverIdList, Integer userId) {
        //对应渠道List 需要排重
        Map<Integer, List<String>> serverIdMap = this.getServerSpMap(searchMap, serverIdList, userId);
        if (serverIdMap.isEmpty()) {
            return null;
        }
        for (Integer serverId : serverIdMap.keySet()) {
            List<String> value = serverIdMap.get(serverId);
            serverIdMap.replace(serverId, ServerInfoUtil.spiltStrList(value));
        }
        return serverIdMap;
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
