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

//    public PayRecord getPayOrder(Integer orderId);
//
//    public int getDataSum(Map<String, Object> paramter);
//
//    public int getSumPayMoney(Map<String, Object> paramter);
//
//    public int updataPayRecord(Map<String, Object> param);

    /**
     * 玩家充值排行榜查询
     */
//    public List<PayRecord> selectPlayerPayOrder(Map<String, Object> param);

    /**
     * 玩家充值排行榜总记录数
     */
//    public int selectPlayerPayOrderTotalCount(Map<String, Object> param);
}
