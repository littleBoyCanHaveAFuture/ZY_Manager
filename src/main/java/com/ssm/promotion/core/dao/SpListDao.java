package com.ssm.promotion.core.dao;


import com.ssm.promotion.core.entity.Sp;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * 2019/11/15
 */
public interface SpListDao {
    /**
     * 查询所有的sp
     *
     * @return List<Sp>
     */
    List<Sp> getAllSp();

    /**
     * 批量查询渠道
     *
     * @param map item 渠道map
     * @return List<Sp>
     */
    List<Sp> selectSpByIds(Map<String, Object> map);

    /**
     * 通过渠道id 查询渠道信息
     *
     * @return List<Sp>
     */
    List<Sp> getSpById(Map<String, Object> map);

    List<Sp> getAllSpByPage(Map<String, Object> map);

    Long getTotalSp();

    int deleteSp(@Param("spId") int spId);

    int updateSp(Map<String, Object> map);

    int addSp(Map<String, Object> map);
}