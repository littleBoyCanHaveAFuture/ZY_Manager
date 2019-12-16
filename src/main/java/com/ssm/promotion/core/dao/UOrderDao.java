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
    public List<UOrder> getUOrderList(Map<String, Object> param);

    Long getTotalUorders(Map<String, Object> map);

    public List<UOrder> get(@Param("appId") String appId,
                            @Param("channelID") String channelID,
                            @Param("channelOrderID") String channelOrderID);

    int save(UOrder order);

    void delete(UOrder order);

    void update(UOrder order);

}