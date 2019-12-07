package com.ssm.promotion.core.handler;


import com.ssm.promotion.core.entity.User;
import com.ssm.promotion.core.service.UserService;
import com.ssm.promotion.core.service.impl.UserServiceImpl;
import com.ssm.promotion.core.util.StringUtil;
import com.ssm.promotion.core.util.enums.FunctionType;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author tgzwmkkkk
 */
public class AopSurvey {
    private static final Logger log = Logger.getLogger(AopSurvey.class);
    private static List<String> allJumpFuncsList = new ArrayList<>();
    private static List<String> allJumpFuncMethodList = new ArrayList<>();

    static {
        allJumpFuncsList.add("com.ssm.promotion.core.service.impl.PictureServiceImpl");
        allJumpFuncsList.add("com.ssm.promotion.core.service.impl.AccountServiceImpl");
        allJumpFuncsList.add("com.ssm.promotion.core.service.impl.GameRoleServiceImpl");
        // UserServiceImpl
        allJumpFuncMethodList.add("login");
        //ServerListServiceImpl
        allJumpFuncMethodList.add("isSpCanReg");
        allJumpFuncMethodList.add("isSpCanLogin");
    }

    @Resource
    UserService service;

    /**
     * 转换int列表
     */
    public static List<Integer> convertIntList(String str) {
        String[] strs = str.split(StringUtil.COMMA);
        if (strs.length > 0) {
            List<Integer> intList = new ArrayList<>();
            for (String string : strs) {
                int func = Integer.parseInt(string);
                if (intList.contains(func)) {
                    continue;
                }
                intList.add(func);
            }
            return intList;
        }
        return null;
    }

    private void verifyMethod(JoinPoint pjp) throws Exception {
        //参数
        Object[] obj = pjp.getArgs();
        //方法名
        String signature = pjp.getSignature().getName();

        System.out.println("");
        System.out.println("---------checkSecurity()------------");
        System.out.println("len:" + obj.length);
        for (Object o : obj) {
            System.out.println("args:" + o);
        }
        //得到方法名
        System.out.println("function:" + signature);

        if (this.jumpInterface(pjp)) {
            return;
        }
        if (this.jumpMethod(signature)) {
            return;
        }

        int len;
        if (obj.length >= 1) {
            len = obj.length - 1;
        } else {
            System.out.println("登陆超时,请重新登陆!");
            throw new RuntimeException("登陆超时,请重新登陆!");
        }

        Integer userId = (Integer) obj[len];
        User user = UserServiceImpl.getUser(userId);
        log.info("AopSurvey:  employeeId=" + userId + " operation >>>> " + signature);
        if (user == null) {
            throw new RuntimeException("登陆超时,请重新登陆!!");
        }
        List<Integer> allFuncsList = new ArrayList<>();
        String addFunStr = user.getFunc();
        if (addFunStr != null && !"".equals(addFunStr)) {
            allFuncsList = convertIntList(addFunStr);
        }
        boolean isFuncs = false;
        switch (checkType(signature, allFuncsList)) {
            case NONE:
                break;
            case GameDetail:
                isFuncs = containGameDetail(signature, allFuncsList);
                break;
            case PlayerInfo:
                break;
            case DataAnalysis:
                break;
            case LiveData:
                break;
            case GMFunction:
                break;
            case ServerManagement:
                isFuncs = containServerList(signature, allFuncsList);
                break;
            case AccountManagement:
                isFuncs = containAccount(signature, allFuncsList);
                break;
            default:
                break;
        }

        log.info("AopSurvey:  employeeId=" + userId + " operation >>>> " + signature);
        this.funcThrowRuntimeException(isFuncs, signature);
    }

    /**
     * 没有此权限抛异常
     *
     * @param comtainsFun 是否有次权限
     * @param signature   操作的方法名
     */
    private void funcThrowRuntimeException(boolean comtainsFun, String signature) {
        if (!comtainsFun) {
            log.error("操作失败：无此权限! >>> " + signature);
            throw new RuntimeException("操作失败：无此权限!");
        }
    }

    /**
     * 判断功能模块
     */
    private FunctionType checkType(String signature, List<Integer> allFuncsList) {
        FunctionType type = FunctionType.NONE;

        switch (signature) {
            case "containGameDetail":
                type = FunctionType.GameDetail;
            case "getServerList":
            case "addServer":
            case "updateServer":
            case "delServer":
            case "getTotalServers":

            case "getRechargeSummary":
            case "getGameList":
            case "getDistinctServerInfo":
            case "isSpCanReg":

                //文章
            case "findArticle":
            case "getTotalArticle":
            case "addArticle":
            case "updateArticle":
            case "deleteArticle":
            case "findById":
                type = FunctionType.ServerManagement;
                break;
            case "login":
            case "findUser":
            case "updateUser":
            case "getTotalUser":
            case "addUser":
            case "deleteUser":
            case "getFuncList":
            case "getFuncById":
            case "getTotalSameUser":
                type = FunctionType.AccountManagement;
                break;
            default:
                break;
        }
        log.info("AopSurvey:  checkType = " + type + " operation >>>> " + signature);
        return type;
    }

    /**
     * 账号管理权限
     */
    private boolean containGameDetail(String signature, List<Integer> allFuncsList) {
        if (allFuncsList.contains(FunctionType.GameDetail.getId())) {
            return true;
        }
        boolean result = false;
        //详细权限
        switch (signature) {
            case "getRechargeSummary":
                result = true;
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 账号管理权限
     */
    private boolean containAccount(String signature, List<Integer> allFuncsList) {
        if (allFuncsList.contains(FunctionType.AccountManagement.getId())) {
            return true;
        }
        boolean result = false;
        //详细权限
        switch (signature) {
            case "login":
                result = true;
                break;
            case "findUser":
            case "updateUser":
            case "getTotalUser":
            case "addUser":
            case "deleteUser":
            case "getFuncList":
            case "getFuncById":
            case "getTotalSameUser":
                result = true;
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 服务器管理权限
     */
    private boolean containServerList(String signature, List<Integer> allFuncsList) {
        if (allFuncsList.contains(FunctionType.ServerManagement.getId())) {
            return true;
        }
        boolean result = false;
        //详细权限
        switch (signature) {
            case "getServerList":
            case "addServer":
            case "updateServer":
            case "delServer":
            case "getTotalServers":
            case "getRechargeSummary":
            case "getGameList":
            case "getDistinctServerInfo":
            case "isSpCanReg":
                //文章
            case "findArticle":
            case "getTotalArticle":
            case "addArticle":
            case "updateArticle":
            case "deleteArticle":
            case "findById":
                result = true;
                break;
            default:
                break;
        }
        return result;
    }

    /**
     * 过滤 impl 接口
     */
    private boolean jumpInterface(JoinPoint joinPoint) {
        boolean result = false;
        String name = joinPoint.getTarget().getClass().getName();
        if (allJumpFuncsList.contains(name)) {
            result = true;
        }
        return result;
    }

    /**
     * 过滤 impl 接口
     */
    private boolean jumpMethod(String sign) {
        boolean result = false;
        if (allJumpFuncMethodList.contains(sign)) {
            result = true;
        }
        return result;
    }
}
