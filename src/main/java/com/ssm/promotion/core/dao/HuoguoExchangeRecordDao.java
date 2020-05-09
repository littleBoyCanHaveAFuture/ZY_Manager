package com.ssm.promotion.core.dao;

import com.ssm.promotion.core.entity.HuoguoExchangeRecord;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author 1034683568@qq.com
 * @project_name perfect-ssm
 * @date 2017-3-1
 */
@Repository
public interface HuoguoExchangeRecordDao {

    List<HuoguoExchangeRecord> getRecord(Map<String, Object> map);

    int addRecord(HuoguoExchangeRecord record);

    int updateRecord(HuoguoExchangeRecord record);

    HuoguoExchangeRecord getRecordById(@Param("id") Integer id);

    Long getCount(@Param("openid") String openid);
}
