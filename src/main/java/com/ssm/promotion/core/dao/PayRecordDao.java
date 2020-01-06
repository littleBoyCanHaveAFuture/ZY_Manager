package com.ssm.promotion.core.dao;


import com.ssm.promotion.core.entity.PayRecord;

import java.util.List;
import java.util.Map;


/**
 * @author Administrator
 */
public interface PayRecordDao {

    public List<PayRecord> getPayOrderList(Map<String, Object> param);

    Long getTotalServers(Map<String, Object> map);

}
