package com.ssm.promotion.core.controller;

import com.ssm.promotion.core.common.Constants;
import com.ssm.promotion.core.common.Result;
import com.ssm.promotion.core.common.ResultGenerator;
import com.ssm.promotion.core.entity.PageBean;
import com.ssm.promotion.core.entity.User;
import com.ssm.promotion.core.entity.UserFunc;
import com.ssm.promotion.core.service.UserFuncService;
import com.ssm.promotion.core.service.UserService;
import com.ssm.promotion.core.service.impl.UserServiceImpl;
import com.ssm.promotion.core.util.MD5Util;
import com.ssm.promotion.core.util.ResponseUtil;
import com.ssm.promotion.core.util.StringUtil;
import com.ssm.promotion.core.util.enums.ManagerType;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 1034683568@qq.com
 * @project_name perfect-ssm
 * @date 2017-3-1
 */
@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger log = Logger.getLogger(UserController.class);
    @Resource
    private UserService userService;
    @Resource
    private UserFuncService userFuncService;
    /**
     * 自动注入request
     */
    @Autowired
    private HttpServletRequest request;

    private Integer getUserId() {
        HttpSession session = request.getSession();
        Integer managerId = (Integer) session.getAttribute("userId");
        return managerId;
    }

    /**
     * 登录
     *
     * @param user    User
     * @param request HttpServletRequest
     * @return Result
     */
    @RequestMapping(value = "/cookie", method = RequestMethod.POST)
    @ResponseBody
    public Result login(User user, HttpServletRequest request) {
        try {
            String mD5pwd = MD5Util.md5Encode(user.getPassword(), "UTF-8");
            user.setPassword(mD5pwd);
        } catch (Exception e) {
            user.setPassword("");
        }
        User resultUser = userService.login(user);
        log.info("request: user/login , user: " + user.toString());
        if (resultUser == null) {
            return ResultGenerator.genFailResult("请认真核对账号、密码！");
        } else {
            resultUser.setPassword("PASSWORD");

            Map data = new HashMap();
            data.put("currentUser", resultUser);

            HttpSession session = request.getSession();
            session.setAttribute("userId", resultUser.getId());
            session.setAttribute("allfuncs", resultUser.getFunc());

            return ResultGenerator.genSuccessResult(data);
        }
    }


    /**
     * 查询用户
     *
     * @param page
     * @param rows
     * @param user
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/datagrid", method = RequestMethod.POST)
    public void list(@RequestParam(value = "page", required = false) String page,
                     @RequestParam(value = "rows", required = false) String rows,
                     User user, HttpServletResponse response) throws Exception {

        Integer userId = getUserId();
        if (userId == null) {
            ResponseUtil.writeRelogin(response);
            return;
        }
        Map<String, Object> map = new HashMap<>();
        if (page != null && rows != null) {
            PageBean pageBean = new PageBean(Integer.parseInt(page),
                    Integer.parseInt(rows));
            map.put("start", pageBean.getStart());
            map.put("size", pageBean.getPageSize());
        }

        map.put("userName", StringUtil.formatLike(user.getUserName()));
        //帮助查询
        User currUser = UserServiceImpl.getUser(userId);
        map.put("currUserId", currUser.getId());
        map.put("managerLv", currUser.getManagerLv());
        map.put("agents", currUser.getAgents());
        if (currUser.getAgents() >= 0 && currUser.getManagerLv() == 500) {
            //渠道管理员 查询自己渠道所有数据
            map.put("self", 1);
        } else if (currUser.getAgents() == 0) {
            map.put("self", 0);
        }

        List<User> userList = userService.findUser(map, userId);
        for (User user1 : userList) {
            user1.setPassword("Password");
        }
        Long total = userService.getTotalUser(map, userId);

        JSONObject result = new JSONObject();
        JSONArray jsonArray = JSONArray.fromObject(userList);
        result.put("rows", jsonArray);
        result.put("total", total);
        result.put("resultCode", Constants.RESULT_CODE_SUCCESS);

        log.info("request: user/list , map: " + map.toString());

        ResponseUtil.write(response, result);
    }

    /**
     * 添加或修改管理员
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestBody User user) throws Exception {
        log.info("addUser");
        Integer userId = getUserId();
        if (userId == null) {
            return ResultGenerator.genRelogin();
        }
        User currUser = UserServiceImpl.getUser(userId);

        //参数校验
        if (user.getUserName() == null) {
            return ResultGenerator.genFailResults("用户名为空");
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userName", user.getUserName());
        if (userService.getTotalSameUser(map, userId) > 0) {
            return ResultGenerator.genFailResults("用户名重复");
        }
        //用户权限
        ManagerType currType = ManagerType.pareseTo(currUser.getManagerLv());
        ManagerType addype = ManagerType.pareseTo(user.getManagerLv());

        int resultTotal = 0;
        String mD5pwd = MD5Util.md5Encode(user.getPassword(), "UTF-8");
        user.setPassword(mD5pwd);
        user.setRoleName(currType.getName());
        User.setClientSpid(user);

        if (!ManagerType.canAddMember(currType, addype)) {
            return ResultGenerator.genFailResult("FAIL");
        }

        //添加用户
        resultTotal = userService.addUser(user, userId);
        if (resultTotal > 0) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("FAIL");
        }
    }

    /**
     * 修改
     *
     * @param user
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/updateUser", method = RequestMethod.PUT)
    @ResponseBody
    public Result update(@RequestBody User user) throws Exception {
        Integer userId = getUserId();
        if (userId == null) {
            return ResultGenerator.genRelogin();
        }
        User currUser = UserServiceImpl.getUser(userId);
        if (currUser == null) {
            return ResultGenerator.genRelogin();
        }
        String MD5pwd = null;
        if (user.getPassword() != null && !"******".equals(user.getPassword())) {
            MD5pwd = MD5Util.md5Encode(user.getPassword(), "UTF-8");
        }
        if (user.getId() == null) {
            return ResultGenerator.genFailResult("FAIL");
        }
        if (user.getManagerLv() == null) {
            return ResultGenerator.genFailResult("FAIL");
        }
        if (!ManagerType.canAddMemberInteger(currUser.getManagerLv(), user.getManagerLv())) {
            return ResultGenerator.genFailResults("权限错误，无法添加");
        }
        user.setPassword(MD5pwd);
        user.setRoleName(ManagerType.pareseTo(user.getManagerLv()).getName());

        log.info("user:" + user.toString());

        int resultTotal = userService.updateUser(user, userId);
        log.info("request: user/update , user: " + user.toString());

        if (resultTotal > 0) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("FAIL");
        }
    }

    /**
     * 删除管理员
     *
     * @param ids
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/{ids}", method = RequestMethod.DELETE)
    @ResponseBody
    public Result delete(@PathVariable(value = "ids") String ids) throws Exception {
        Integer userId = getUserId();
        if (userId == null) {
            return ResultGenerator.genRelogin();
        }
        if (ids.length() > 20) {
            return ResultGenerator.genFailResult("ERROR");
        }
        String[] idsStr = ids.split(",");
        for (int i = 0; i < idsStr.length; i++) {
            userService.deleteUser(Integer.valueOf(idsStr[i]), userId);
        }
        log.info("request: article/delete , ids: " + ids);
        return ResultGenerator.genSuccessResult();
    }

    /**
     * 修改
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getFuncList", method = RequestMethod.POST)
    @ResponseBody
    public List<UserFunc> getFuncList() throws Exception {
        Integer userId = getUserId();
//        if (userId == null) {
//            return ResultGenerator.genRelogin();
//        }

        List<UserFunc> userFuncList = userFuncService.getFuncList(userId);

        log.info("request: user/getFuncList , user: " + userId);

        return userFuncList;

    }
}
