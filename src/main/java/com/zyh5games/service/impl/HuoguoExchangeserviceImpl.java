package com.zyh5games.service.impl;

import com.zyh5games.dao.HuoguoExchangeRecordDao;
import com.zyh5games.dao.Huoguo_ExchangeDao;
import com.zyh5games.entity.HuoguoExchange;
import com.zyh5games.entity.HuoguoExchangeRecord;
import com.zyh5games.service.HuoguoExchangeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 此路径和 aop 的路径分开
 *
 * @author Administrator
 */

@Service("huoguouExchangeService")
public class HuoguoExchangeserviceImpl implements HuoguoExchangeService {
    @Resource
    private Huoguo_ExchangeDao exchangeDao;

    @Resource
    private HuoguoExchangeRecordDao exchangeRecordDao;

    @Override
    public List<HuoguoExchange> getAll(Integer id) {
        return exchangeDao.getAll();
    }

    @Override
    public HuoguoExchange getById(Integer itemId, Integer id) {
        return exchangeDao.getById(itemId);
    }

    @Override
    public List<HuoguoExchangeRecord> getRecord(Map<String, Object> map, Integer id) {
        return exchangeRecordDao.getRecord(map);
    }

    @Override
    public long getCount(String openid, Integer userId) {
        return exchangeRecordDao.getCount(openid);
    }

    @Override
    public HuoguoExchangeRecord getRecord(Integer recordId, Integer userId) {
        return exchangeRecordDao.getRecordById(recordId);
    }

    @Override
    public int addRecord(HuoguoExchangeRecord record, Integer id) {
        return exchangeRecordDao.addRecord(record);
    }

    @Override
    public int updateRecord(HuoguoExchangeRecord record, Integer id) {
        return exchangeRecordDao.updateRecord(record);
    }
}
