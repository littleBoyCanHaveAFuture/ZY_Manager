package com.ssm.promotion.core.dao;

import com.ssm.promotion.core.entity.GameName;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public interface GameNameDao {
    List<GameName> selectAllGame();

    List<GameName> selectGame(Map<String, Object> map);

    int deleteGame(Integer gameid);

    int updateGame(GameName gamename);

    int insertGame(GameName gamename);

}