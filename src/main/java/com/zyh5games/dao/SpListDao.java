package com.zyh5games.dao;


import com.zyh5games.entity.Sp;
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
     * @param map spIdList 渠道map
     * @return List<Sp>
     */
    List<Sp> selectSpByIds(Map<String, Object> map);

    /**
     * 批量查询渠道
     *
     * @param map spIdList 排除的渠道di
     * @return List<Sp>
     */
    List<Sp> selectSpNoByIds(Map<String, Object> map);

    /**
     * 通过渠道id 查询渠道信息
     *
     * @return List<Sp>
     */
    List<Sp> getSpById(Map<String, Object> map);

    Sp getSp(@Param("spId") int spId);

    List<Sp> getAllSpByPage(Map<String, Object> map);

    Long getTotalSp();

    int deleteSp(@Param("spId") int spId);

    int updateSp(Map<String, Object> map);

    int addSp(Map<String, Object> map);


}
