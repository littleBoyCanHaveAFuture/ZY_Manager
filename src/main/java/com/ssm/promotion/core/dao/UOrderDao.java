package com.ssm.promotion.core.dao;

import com.ssm.promotion.core.entity.UOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/12/9
 */
public interface UOrderDao {
    List<UOrder> getUOrderList(Map<String, Object> param);

    UOrder getOrderById(@Param("orderID") String orderID);

    Long getTotalUorders(Map<String, Object> map);

    List<UOrder> get(@Param("appId") String appId,
                     @Param("channelID") String channelID,
                     @Param("channelOrderID") String channelOrderID);

    List<UOrder> getCpOrder(@Param("appId") String appId,
                            @Param("channelID") String channelID,
                            @Param("cpOrderID") String cpOrderID);

    int save(UOrder order);

    void delete(UOrder order);

    void update(UOrder order);

}
