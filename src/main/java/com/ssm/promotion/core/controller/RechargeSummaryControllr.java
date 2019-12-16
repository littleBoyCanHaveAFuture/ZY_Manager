package com.ssm.promotion.core.controller;

import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.entity.RechargeSummary;
import com.ssm.promotion.core.entity.ServerInfo;
import com.ssm.promotion.core.service.RechargeSummaryService;
import com.ssm.promotion.core.service.ServerListService;
import com.ssm.promotion.core.util.DateUtil;
import com.ssm.promotion.core.util.ResponseUtil;
import com.ssm.promotion.core.util.enums.RSType;
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


    private void removeSpMap(Map<Integer, Map<Integer, List<Integer>>> tmpMap, int spId) {
        Iterator<Map.Entry<Integer, Map<Integer, List<Integer>>>> it = tmpMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Map<Integer, List<Integer>>> entry = it.next();
            if (entry.getKey() != spId) {
                it.remove();//使用迭代器的remove()方法删除元素
            }
        }
    }

    private void removeGameMap(Map<Integer, List<Integer>> tmpMap, int gameId) {
        Iterator<Map.Entry<Integer, List<Integer>>> it = tmpMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<Integer>> entry = it.next();
            if (entry.getKey() != gameId) {
                it.remove();//使用迭代器的remove()方法删除元素
            }
        }
    }

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
    public void searchRechargeSmummary(Integer type,
                                       Integer spId, Integer gameId, Integer serverId,
                                       String startTime, String endTime,
                                       HttpServletResponse response) throws Exception {
        System.out.println("searchRS:");
        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }

        long s = System.currentTimeMillis();

        JSONObject result = new JSONObject();
        do {
            //参数校验
            if (type == null || type > RSType.ORDERBY_SPID || type < RSType.ORDERBY_DAY) {
                result.put("status", Constants.ERR_RSTYPE);
                break;
            }
            if (gameId == null) {
                result.put("status", Constants.ERR_GAMEID);
                break;
            }
            if (serverId == null) {
                result.put("status", Constants.ERR_SERVERID);
                break;
            }
            if (spId == null) {
                result.put("status", Constants.ERR_SPID);
                break;
            }
            String currTimes = DateUtil.getCurrentDateStr();
            if (startTime == null || startTime.isEmpty()) {
                startTime = currTimes;
            }
            if (endTime == null || endTime.isEmpty()) {
                endTime = currTimes;
            }

            Map<String, Object> map = new HashMap<>(6);
            map.put("type", type);
            map.put("gameId", gameId);
            map.put("serverId", serverId);
            map.put("spId", spId);
            map.put("startTime", startTime);
            map.put("endTime", endTime);


            List<RechargeSummary> list = this.getGameRechargeSummary(map, userId);

            list.forEach(rs -> System.out.println(rs.toJSONString()));

            result.put("rows", JSONArray.fromObject(list));
            result.put("total", list.size());

        } while (false);

        String usetime = new DecimalFormat("0.00").format((double) (System.currentTimeMillis() - s) / 1000);

        System.out.println("use:" + usetime);

        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        result.put("time", usetime);

        ResponseUtil.write(response, result);
    }

    /**
     * 根据type获取不同的数据
     *
     * @param map    type
     *               gameId
     *               serverId
     *               spId
     * @param userId 账号id
     */
    public List<RechargeSummary> getGameRechargeSummary(Map<String, Object> map, Integer userId) throws Exception {
        int type = Integer.parseInt(map.get("type").toString());
        int gameId = Integer.parseInt(map.get("gameId").toString());
        int serverId = Integer.parseInt(map.get("serverId").toString());
        int spId = Integer.parseInt(map.get("spId").toString());
        String startTimes = map.get("startTime").toString();
        String endTimes = map.get("endTime").toString();

        //1.查询渠道-游戏-区服
        //<渠道,<游戏，区服>>
        Map<Integer, Map<Integer, List<Integer>>> sgsMap = new HashMap<>();
        getAllSGS(sgsMap, spId, gameId, serverId, type, userId);

        //2.时间转化
        List<String> timeList = DateUtil.transTimes(startTimes, endTimes, DateUtil.FORMAT_YYYY_MMDD_HHmm);

        //3.查询redis
        return this.rechargeSummaryServices.getRechargeSummary(sgsMap, timeList, type, userId);
    }

    public void getAllSGS(Map<Integer, Map<Integer, List<Integer>>> sgsMap, int spId, int gameId, int serverId, int type, int userId) {
        //查询数据结果
        Map<Integer, Map<Integer, List<Integer>>> tmpMap = new HashMap<>();
        //所有渠道-游戏-区服
        List<ServerInfo> serverInfoList = serverService.getServerList(new HashMap<>(), userId);

        for (ServerInfo serverInfo : serverInfoList) {
            Integer sSpId = serverInfo.getSpId();
            Integer sGameId = serverInfo.getGameId();
            Integer sServerId = serverInfo.getServerId();

            if (sSpId == null || sGameId == null || sServerId == null) {
                continue;
            }

            if (!tmpMap.containsKey(sSpId)) {
                Map<Integer, List<Integer>> gsMap = new HashMap<>();
                List<Integer> serverIdList = new ArrayList<>();

                serverIdList.add(sServerId);
                gsMap.put(sGameId, serverIdList);
                tmpMap.put(sSpId, gsMap);
            } else {
                if (!tmpMap.get(sSpId).containsKey(sGameId)) {
                    List<Integer> serverIdList = new ArrayList<>();
                    serverIdList.add(sServerId);
                    tmpMap.get(sSpId).put(gameId, serverIdList);
                } else {
                    List<Integer> serverIdList = tmpMap.get(sSpId).get(sGameId);
                    serverIdList.add(sServerId);
                }
            }
        }
        //2.筛选
        //某些条件下不允许查询所有
        //查询所有的
        if (type == 2) {
            //需要指定 渠道和游戏id 不能为 -1
            if (spId == -1 || gameId == -1) {
                return;
            }
        } else if (type == 3) {
            if (gameId == -1) {
                return;
            }
        }

        //排除不需要的渠道
        if (spId != -1) {
            removeSpMap(tmpMap, spId);
        }
        //排除不需要的游戏
        if (gameId != -1) {
            for (Integer sp : tmpMap.keySet()) {
                Map<Integer, List<Integer>> gameMap = tmpMap.get(sp);
                removeGameMap(gameMap, gameId);
            }
        }
        //排除不需要的区服
        if (serverId != -1) {
            for (Integer sp : tmpMap.keySet()) {
                Map<Integer, List<Integer>> gameMap = tmpMap.get(sp);
                for (Integer game : gameMap.keySet()) {
                    List<Integer> serverList = gameMap.get(game);
                    //需要的区服存在 添加进去
                    if (serverList.contains(serverId)) {
                        serverList.clear();
                        serverList.add(serverId);
                    }
                }
            }
        }
        sgsMap.putAll(tmpMap);

        for (Integer sp : sgsMap.keySet()) {
            System.out.println("sgsMap----->sp:" + sp + ":" + sgsMap.get(sp).toString());
        }
    }

}

