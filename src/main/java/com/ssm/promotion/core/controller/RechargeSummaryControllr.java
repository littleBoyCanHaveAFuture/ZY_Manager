package com.ssm.promotion.core.controller;

import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.entity.RechargeSummary;
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
                                      Integer gameId, Integer serverId, Integer spId,
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

        System.out.println("start:" + System.currentTimeMillis());
        List<RechargeSummary> list = null;
        switch (type) {
            case 1:
                break;
            case 2:
                break;
            case 3: {
                //游戏分渠道查看数据
                //需要参数 gameId serverId
                //查询渠道
                if (gameId == null || serverId == null || gameId == -1 || serverId == -1) {
//                ResponseUtil.
                    break;
                }
                List<String> spidStrList = new LinkedList<>();
                List<Integer> spidIntList = new LinkedList<>();
                List<String> serverInfos = serverService.getDistinctServerInfo(map, type, userId);
                for (String spidList : serverInfos) {
                    if (spidList.contains(",")) {
                        String[] spilt = spidList.split(",");
                        for (String spid : spilt) {
                            if (!spidIntList.contains(spid)) {
                                spidIntList.add(Integer.parseInt(spid));
                            }
                            spidStrList.add(spid);
                        }
                    } else {
                        Integer spid = Integer.parseInt(spidList);
                        if (!spidIntList.contains(spid)) {
                            spidIntList.add(spid);
                        }
                        spidStrList.add(spidList);
                    }
                }
                if (spidStrList.size() == 0) {
                    break;
                }

                //查询redis
                list = this.rechargeSummaryServices.getRechargeSummary(map, spidStrList, userId);
            }
            break;
            default:
                break;
        }
        System.out.println("end:" + System.currentTimeMillis());
        JSONObject result = new JSONObject();
        JSONArray jsonArray = JSONArray.fromObject(list);
        result.put("rows", jsonArray);
        result.put("total", list == null ? 0 : list.size());
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);
        ResponseUtil.write(response, result);

        System.out.println("request: rechargeSummary/searchRechargeSummary , map: " + result.toString());
        log.info("request: rechargeSummary/searchRechargeSummary , map: " + map.toString());

    }
}
