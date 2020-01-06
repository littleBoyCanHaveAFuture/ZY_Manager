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
     * @param account
     * @return
     */
    int create(Account account);

    /**
     * 查询相同设备码账号条数
     */
    int getTotalSameDeviceCode(@Param("deviceCode") String deviceCode, @Param("channelId") int channelId);

    /**
     * 查询最大id
     */
    Integer readMaxAccountId(@Param("maxSpid") int maxSpid);

    /**
     * 查找用户列表
     *
     * @param channelId  渠道id
     * @param channelUid 渠道uid
     * @return
     */
    List<Account> findAccountSp(@Param("channelId") String channelId, @Param("channelUid") String channelUid);

    /**
     * 实体修改
     *
     * @param map
     * @return
     */
    int updateAccount(Map<String, Object> map);

    List<Integer> exist(Map<String, Object> map);

    List<Account> findAccountByname(@Param("name") String name);

    int updateAccountUid(Map<String, Object> map);

    Account findAccountById(@Param("id") int id);
}
