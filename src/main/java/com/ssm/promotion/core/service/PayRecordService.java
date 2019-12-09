package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.PayRecord;

import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/12/9
 */
public interface PayRecordService {
    List<PayRecord> getPayOrderList(Map<String, Object> map, Integer userId);

    public Long getTotalPayRecords(Map<String, Object> map, Integer userId);
}
