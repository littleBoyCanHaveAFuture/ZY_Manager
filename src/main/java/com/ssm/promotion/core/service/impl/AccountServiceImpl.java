package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.dao.AccountDao;
import com.ssm.promotion.core.entity.Account;
import com.ssm.promotion.core.sdk.AccountWorker;
import com.ssm.promotion.core.service.AccountService;
import com.ssm.promotion.core.util.MysqlUtil;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author song minghua
 * @date 2019/12/4
 */
@Service("AccountService")
public class AccountServiceImpl implements AccountService {
    private static final Logger log = Logger.getLogger(AccountServiceImpl.class);

    @Resource
    private AccountDao accountDao;

    @Override
    public void createAccount(Account account) {
        int id = AccountWorker.lastUserId.get();
        do {
            //若有多个login服，则lastUserId的值异步；主键循环增长，直至插入成功为止
            account.setId(++id);

            try {
                int res = accountDao.create(account);
                log.info("createAccount success:" + res);
                break;
            } catch (DataAccessException e) {
                String err = e.getMessage();
                //仅使主键重复异常被忽略
                if (err.contains(MysqlUtil.excep_sql) && err.contains(MysqlUtil.excep_pri)) {
                    log.info("err1");
                    continue;
                } else if (err.contains(MysqlUtil.excep_uni)) {
                    log.info("err2");
                    account.setId(-2);
                    return;
                } else {
                    account.setId(-1);
                    log.info("err3:" + err);
                    return;
                }
            } catch (Exception e) {
                account.setId(-3);
                log.info("err4:" + e.getMessage());
                return;
            }
        } while (true);

        AccountWorker.lastUserId.set(id);

        log.info(AccountWorker.lastUserId.get());
    }

    @Override
    public int getTotalSameDeviceCode(String deviceCode, Integer channelId) {
        return accountDao.getTotalSameDeviceCode(deviceCode, channelId);
    }

    @Override
    public Integer readMaxAccountId(int maxSpid) {
        Integer count = accountDao.readMaxAccountId(maxSpid);
        if (count == null) {
            count = 0;
        }
        return count;
    }

    @Override
    public List<Account> findUser(Map<String, Object> map) {
        boolean isChannel = Boolean.parseBoolean(map.get("isChannel").toString());
        if (isChannel) {
            String channelId = map.get("channelId").toString();
            String channelUid = map.get("channelUid").toString();

            if (channelId.isEmpty() || channelUid.isEmpty()) {
                return null;
            }
            return accountDao.findAccountSp(channelId, channelUid);
        } else {
            String name = map.get("name").toString();
            String pwd = map.get("pwd").toString();

            if (name.isEmpty() || pwd.isEmpty()) {
                return null;
            }

            Account account = accountDao.findAccountByname(name);
            if (account != null) {
                List<Account> list = new ArrayList<>();
                list.add(account);
                return list;
            } else {
                return null;
            }
        }
    }

    @Override
    public Account findUserBychannelUid(String channelId, String channelUid) {
        List<Account> accountList = accountDao.findAccountSp(channelId, channelUid);
        if (accountList == null || accountList.size() != 1) {
            return null;
        } else {
            return accountList.get(0);
        }
    }

    @Override
    public Account findUser(String channelId, String openId) {
        return accountDao.findAccountByOpenId(channelId, openId);
    }

    @Override
    public Account findAccountByname(String username) {
        return accountDao.findAccountByname(username);
    }

    @Override
    public Account findAccountById(int id) {
        return accountDao.findAccountById(id);
    }

    @Override
    public void updateAccount(Map<String, Object> map) {
        accountDao.updateAccount(map);
    }

    @Override
    public int exist(Map<String, Object> map) {
        return accountDao.exist(map).size();
    }

    @Override
    public void updateAccountUid(Map<String, Object> map) {
        accountDao.updateAccountUid(map);
    }
}
