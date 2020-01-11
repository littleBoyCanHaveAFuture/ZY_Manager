package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.dao.SpListDao;
import com.ssm.promotion.core.entity.Sp;
import com.ssm.promotion.core.service.SpService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/11/26
 */
@Service("SpService")
public class SpServiceImpl implements SpService {
    @Resource
    private SpListDao spDao;

    @Override
    public List<Sp> getAllSp(Integer userId) {
        return spDao.getAllSp();
    }

    @Override
    public List<Sp> selectSpByIds(boolean notIn, Map<String, Object> map, Integer userId) {
        if (notIn) {
            return spDao.selectSpNoByIds(map);
        } else {
            return spDao.selectSpByIds(map);
        }

    }

    @Override
    public List<Sp> getSpById(Map<String, Object> map, Integer userId) {
        return spDao.getSpById(map);
    }

    @Override
    public List<Sp> getAllSpByPage(Map<String, Object> map, Integer userId) {
        return spDao.getAllSpByPage(map);
    }

    @Override
    public Long getTotalSp(Integer userId) {
        return spDao.getTotalSp();
    }

    @Override
    public int delSp(Integer id, Integer userId) {
        return spDao.deleteSp(id);
    }

    @Override
    public int updateSp(Map<String, Object> map, Integer userId) {
        return spDao.updateSp(map);
    }

    @Override
    public int addSp(Map<String, Object> map, Integer userId) {
        return spDao.addSp(map);
    }
}