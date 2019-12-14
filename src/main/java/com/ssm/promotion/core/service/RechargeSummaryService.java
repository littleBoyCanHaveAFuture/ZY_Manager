package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.RechargeSummary;

import java.util.List;
import java.util.Map;


/**
 * @author Administrator
 */
public interface RechargeSummaryService {

    /**
     * 充值汇总
     *
     * @param sgsMap   渠道游戏区服
     * @param timeList 时间列表
     * @param type     查询类型
     * @param userId   当前操作者id
     */
    List<RechargeSummary> getRechargeSummary(Map<Integer, Map<Integer, List<Integer>>> sgsMap, List<String> timeList, Integer type, Integer userId) throws Exception;

}
