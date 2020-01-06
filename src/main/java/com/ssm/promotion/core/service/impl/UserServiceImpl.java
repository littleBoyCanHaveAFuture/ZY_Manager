package com.ssm.promotion.core.service.impl;

import com.ssm.promotion.core.dao.UserDao;
import com.ssm.promotion.core.entity.User;
import com.ssm.promotion.core.service.UserService;
import com.ssm.promotion.core.util.AntiXssUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 此路径和 aop 的路径分开
 *
 * @author Administrator
 */

@Service("userService")
public class UserServiceImpl implements UserService {
    private static Map<Integer, User> managerMap = new ConcurrentHashMap<>();
    @Resource
    private UserDao userDao;

    public static User getUser(Integer userId) {
        return managerMap.get(userId);
    }

    public static String getUserName(int userId) {
        return managerMap.get(userId).getRoleName();
    }

    public void removeManager(Integer userId) {
        managerMap.remove(userId);
    }

    @Override
    public User login(User user) {
        User resultUser = userDao.login(user);
        if (resultUser != null) {
            managerMap.put(resultUser.getId(), resultUser);
        }
        return resultUser;
    }

    @Override
    public List<User> findUser(Map<String, Object> map, Integer userId) {
        List<User> result = userDao.findUsers(map);
        return result;
    }

    @Override
    public Long getTotalSameUser(Map<String, Object> map, Integer userId) {
        Long res = userDao.getTotalSameUser(map);
        return res;
    }

    @Override
    public int updateUser(User user, Integer userId) {
        //防止有人胡乱修改导致其他人无法正常登陆
        if ("admin".equals(user.getUserName())) {
            return 0;
        }
        user.setUserName(AntiXssUtil.replaceHtmlCode(user.getUserName()));
        return userDao.updateUser(user);
    }

    @Override
    public Long getTotalUser(Map<String, Object> map, Integer userId) {
        return userDao.getTotalUser(map);
    }

    @Override
    public int addUser(User user, Integer userId) {
        if (user.getUserName() == null || user.getPassword() == null || user.getManagerLv() == null) {
            return 0;
        }
        user.setUserName(AntiXssUtil.replaceHtmlCode(user.getUserName()));
        return userDao.addUser(user);
    }

    @Override
    public int deleteUser(Integer id, Integer userId) {
        //防止有人胡乱修改导致其他人无法正常登陆
        if (2 == id) {
            return 0;
        }
        return userDao.deleteUser(id);
    }

}
