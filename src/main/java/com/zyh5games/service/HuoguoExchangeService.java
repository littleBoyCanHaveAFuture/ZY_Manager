package com.zyh5games.service;

import com.zyh5games.entity.HuoguoExchange;
import com.zyh5games.entity.HuoguoExchangeRecord;

import java.util.List;
import java.util.Map;

/**
 * @author 1034683568@qq.com
 * @project_name perfect-ssm
 * @date 2017-3-1
 */
public interface HuoguoExchangeService {

    List<HuoguoExchange> getAll(Integer id);

    HuoguoExchange getById(Integer itemId, Integer id);

    List<HuoguoExchangeRecord> getRecord(Map<String, Object> map, Integer userId);

    long getCount(String openid, Integer userId);

    HuoguoExchangeRecord getRecord(Integer recordId, Integer userId);

    int addRecord(HuoguoExchangeRecord record, Integer userId);

    int updateRecord(HuoguoExchangeRecord record, Integer userId);
}
