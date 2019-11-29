package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.RechargeSummary;

import java.util.List;
import java.util.Map;


/**
 * @author Administrator
 */
public interface RechargeSummaryService {

    //    public void getRechargeSummary(HttpServletRequest request, Integer managerId);

    public List<RechargeSummary> getRechargeSummary(Map<String, Object> map, List<String> spIdList, Integer userId) throws Exception;
//
//    public List<RechargeSummaryVo> getPutInData(String spIds, String startTime, String endTime, Integer managerId);
//
//    public List<RechargeSummaryVo> getPutInDataByDay(String spIds, String date, Integer managerId);

}
