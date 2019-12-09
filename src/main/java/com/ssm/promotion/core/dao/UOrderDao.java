package com.ssm.promotion.core.dao;

import com.ssm.promotion.core.entity.UOrder;

import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/12/9
 */
public interface UOrderDao {
    public List<UOrder> getUOrderList(Map<String, Object> param);

    List<Long> getTotalUorders(Map<String, Object> map);

    public UOrder get(long orderID);

    void save(UOrder order);

    void delete(UOrder order);


}
