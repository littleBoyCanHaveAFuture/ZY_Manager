package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.RechargeSummary;

import java.util.List;
import java.util.Map;


/**
 * @author Administrator
 */
public interface RechargeSummaryService {

    //    public void getRechargeSummary(HttpServletRequest request, Integer managerId);

    /**
     * 充值汇总
     *
     * @param map          http参数
     * @param serverIdMap 区服查询 区服-渠道列表
     * @param spIdList     分渠道查询 游戏-渠道列表
     * @param userId       当前操作者id
     */
    public List<RechargeSummary> getRechargeSummary(Map<String, Object> map, Map<Integer, List<String>> serverIdMap, List<String> spIdList, Integer userId) throws Exception;
//
//    public List<RechargeSummaryVo> getPutInData(String spIds, String startTime, String endTime, Integer managerId);
//
//    public List<RechargeSummaryVo> getPutInDataByDay(String spIds, String date, Integer managerId);

}
