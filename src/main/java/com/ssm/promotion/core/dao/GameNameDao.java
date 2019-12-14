package com.ssm.promotion.core.dao;

import com.ssm.promotion.core.entity.GameName;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface GameNameDao {
    List<GameName> selectAll();

    List<GameName> select(Map<String, Object> map);

    int delete(Integer gameid);

    int update(GameName gamename);

    int insert(GameName gamename);

}