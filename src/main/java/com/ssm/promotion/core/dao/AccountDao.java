package com.ssm.promotion.core.dao;

import com.ssm.promotion.core.entity.Account;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @author 1034683568@qq.com
 * @project_name perfect-ssm
 * @date 2017-3-1
 */
@Repository
public interface AccountDao {

    /**
     * 创建账号
     *
     * @param Account
     * @return
     */
    public int create(Account Account);

    /**
     * 查询相同设备码账号条数
     */
    int getTotalSameDeviceCode(@Param("deviceCode") String deviceCode, @Param("channelId") int channelId);

    /**
     * 查询最大id
     */
    int readMaxAccountId(@Param("maxSpid") int maxSpid);

    /**
     * 查找用户列表
     *
     * @param map
     * @return
     */
    List<Account> findAccount(Map<String, String> map);

    /**
     * 实体修改
     *
     * @param Account
     * @return
     */
    public int updateAccount(Map<String, Object> map);


//    /**
//     * @param map
//     * @return
//     */
//    public Long getTotalAccount(Map<String, Object> map);
//

//    /**
//     * 添加用户
//     *
//     * @param Account
//     * @return
//     */
//    public int addAccount(Account Account);
//
//    /**
//     * 删除用户
//     *
//     * @param id
//     * @return
//     */
//    public int deleteAccount(Integer id);
//

    List<Integer> exist(Map<String, String> map);
}
