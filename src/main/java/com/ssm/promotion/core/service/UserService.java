package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.User;

import java.util.List;
import java.util.Map;

/**
 * @author 1034683568@qq.com
 * @project_name perfect-ssm
 * @date 2017-3-1
 */
public interface UserService {

    /**
     * @param user
     * @return
     */
    public User login(User user);

    /**
     * 查找用户
     *
     * @param map
     * @param userId 当前操作的玩家id
     * @return
     */
    public List<User> findUser(Map<String, Object> map, Integer userId);

    /**
     * 判断是否有该用户
     *
     * @param map
     * @param userId 当前操作的玩家id
     * @return
     */
    public Long getTotalSameUser(Map<String, Object> map, Integer userId);


    /**
     * @param map
     * @param userId 当前操作的玩家id
     * @return
     */
    public Long getTotalUser(Map<String, Object> map, Integer userId);

    /**
     * @param user
     * @param userId 当前操作的玩家id
     * @return
     */
    public int updateUser(User user, Integer userId);

    /**
     * @param user
     * @param userId 当前操作的玩家id
     * @return
     */
    public int addUser(User user, Integer userId);

    /**
     * @param id
     * @param userId 当前操作的玩家id
     * @return
     */
    public int deleteUser(Integer id, Integer userId);
}
