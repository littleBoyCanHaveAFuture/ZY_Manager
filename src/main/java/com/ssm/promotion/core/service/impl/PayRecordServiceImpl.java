package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.dao.PayRecordDao;
import com.ssm.promotion.core.entity.PayRecord;
import com.ssm.promotion.core.service.PayRecordService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/12/9
 */
@Service("PayRecordService")
public class PayRecordServiceImpl implements PayRecordService {
    @Resource
    PayRecordDao payRecordDao;

    @Override
    public List<PayRecord> getPayOrderList(Map<String, Object> map, Integer userId) {
        return payRecordDao.getPayOrderList(map);
    }

    @Override
    public Long getTotalPayRecords(Map<String, Object> map, Integer userId) {
        return payRecordDao.getTotalServers(map);
    }

}
