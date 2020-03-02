package com.ssm.promotion.core.controller;

import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.entity.GameInfo;
import com.ssm.promotion.core.entity.RechargeSummary;
import com.ssm.promotion.core.entity.ServerInfo;
import com.ssm.promotion.core.jedis.jedisRechargeCache;
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
import java.util.stream.Collectors;

/**
 * @author song minghua
 * @date 2019/11/26
 */
@Controller
@RequestMapping("/rechargeSummary")
public class RechargeSummaryControllr {
    private static final Logger log = Logger.getLogger(RechargeSummaryControllr.class);
    @Autowired
    jedisRechargeCache cache;
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
        log.info("searchRS:");
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

        log.info("use:" + usetime);

        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        result.put("time", usetime);

        ResponseUtil.write(response, result);
    }

    /**
     * 根据type获取不同的汇总数据
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
        getAllGame(sgsMap, spId, gameId, serverId, type);
        if (sgsMap.size() == 0) {
            return null;
        }
        //2.时间转化
        List<String> timeList = DateUtil.transTimes(startTimes, endTimes, DateUtil.FORMAT_YYYY_MMDD_HHmm);

        //3.查询redis
        //todo 查询数据 区服/渠道概况 必须指定 渠道和游戏
        List<RechargeSummary> rs = rechargeSummaryServices.getRechargeSummary(sgsMap, timeList, type, userId);
        //处理开服天数
//        if (type == 2) {
//            for (RechargeSummary rechargeSummary : rs) {
//                for (ServerInfo info : serverInfoList) {
//                    if (info.getServerId() == rechargeSummary.getServerId()) {
//                        rechargeSummary.setOpenDay(DateUtil.transTimes(info.getOpenday(), DateUtil.getCurrentDateStr(), DateUtil.FORMAT_YYYY_MMDD_HHmmSS).size());
//                        break;
//                    }
//                }
//            }
//        }
        return rs;
    }

    public List<ServerInfo> getAllSGS(Map<Integer, Map<Integer, List<Integer>>> sgsMap, int spId, int gameId, int serverId, int type, int userId) {
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
                return null;
            }
        } else if (type == 3) {
            if (gameId == -1) {
                return null;
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
            log.info("sgsMap----->sp:" + sp + ":" + sgsMap.get(sp).toString());
        }
        Iterator<ServerInfo> it = serverInfoList.iterator();
        while (it.hasNext()) {
            ServerInfo next = it.next();
            if (sgsMap.containsKey(next.getSpId())) {
                Map<Integer, List<Integer>> gameMap = sgsMap.get(next.getSpId());
                if (gameMap.containsKey(next.getGameId())) {
                    List<Integer> serverList = gameMap.get(next.getGameId());
                    if (serverList.contains(next.getServerId())) {
                        continue;
                    }
                }
            }
            it.remove();
        }

        return serverInfoList;
    }

    //筛选游戏渠道区服信息

    /**
     * @param sgsMap 游戏id-渠道id-
     *               {渠道区服1
     *               -渠道区服2
     *               -渠道区服3
     *               -渠道区服4
     *               ...}
     */
    public Map<String, GameInfo> getAllGame(Map<Integer, Map<Integer, List<Integer>>> sgsMap, int spId, int gameId, int serverId, int type) {
        Map<String, GameInfo> gameInfoMap = new HashMap<>();

        //1.筛选
        //某些条件下不允许查询所有
        switch (type) {
            case 1:
            case 2:
                //需要指定 渠道和游戏id 不能为 -1
                if (spId == -1 || gameId == -1) {
                    log.error("需要指定 渠道和游戏id 不能为 -1");
                    return null;
                }
                break;
            case 3: {
                if (gameId == -1) {
                    log.error("需要指定 游戏id 不能为 -1");
                    return null;
                }
            }
            break;
            default:
                break;
        }

        //2.满足条件的游戏渠道区服
        Set<String> gameIdInfo;
        if (gameId == -1) {
            gameIdInfo = cache.getGAMEIDInfo(String.valueOf(gameId));
        } else {
            gameIdInfo = new HashSet<>();
            gameIdInfo.add(String.valueOf(gameId));
        }

        for (String gid : gameIdInfo) {
            Set<String> spIdInfo;
            if (spId == -1) {
                spIdInfo = cache.getSPIDInfo(gid);
            } else {
                spIdInfo = new HashSet<>();
                spIdInfo.add(String.valueOf(spId));
            }
            GameInfo gameInfo = new GameInfo(gid);
            gameInfo.setSpIdSet(spIdInfo);

            for (String spid : spIdInfo) {
                Set<String> serverIdInfo;
                if (serverId == -1) {
                    serverIdInfo = cache.getServerInfo(gid, spid);
                    gameInfo.addServerInfo(spid, serverIdInfo);
                } else {
                    serverIdInfo = new HashSet<>();
                    serverIdInfo.add(String.valueOf(serverId));
                    gameInfo.addServerInfo(spid, serverIdInfo);
                }
            }
            gameInfoMap.put(gid, gameInfo);
        }


        for (GameInfo gameInfo : gameInfoMap.values()) {
            log.info("RS集合：" + gameInfo.toString());

            String gid = gameInfo.getGameId();
            //渠道id-渠道区服
            Map<String, Set<String>> spInfo = gameInfo.getSpInfo();
            Map<Integer, List<Integer>> stringListMap = new HashMap<>();

            for (Map.Entry<String, Set<String>> entry : spInfo.entrySet()) {
                List<String> stringList = new ArrayList<>(entry.getValue());
                List<Integer> integerList = stringList.stream().map(Integer::parseInt).collect(Collectors.toList());
                stringListMap.put(Integer.valueOf(entry.getKey()), integerList);
            }

            sgsMap.put(Integer.parseInt(gid), stringListMap);
        }
        return gameInfoMap;
    }
}

