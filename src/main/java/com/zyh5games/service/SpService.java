package com.zyh5games.service;

import com.zyh5games.entity.Sp;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface SpService {

    List<Sp> getAllSp(Integer userId);

    List<Sp> selectSpByIds(boolean notIn, Map<String, Object> map, Integer userId);

    List<Sp> getSpById(Map<String, Object> map, Integer userId);

    List<Sp> getAllSpByPage(Map<String, Object> map, Integer userId);

    Long getTotalSp(Integer userId);

    Sp getSp(Integer spId,Integer userId);
    /**
     * 删除渠道信息
     */
    public int delSp(Integer id, Integer userId);

    /**
     * 更新渠道信息
     */
    int updateSp(Map<String, Object> map, Integer userId);

    /**
     * 添加渠道信息
     */
    int addSp(Map<String, Object> map, Integer userId);
}
