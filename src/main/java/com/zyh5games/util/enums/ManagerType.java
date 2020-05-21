package com.zyh5games.util.enums;

/**
 * 管理员权限
 * 1.最高管理员 可以查看修改所有成员      1000
 * 2.渠道管理员 可以查看修改所有同渠道的  500
 * 3.渠道普通成员 只能查看自己           100
 * 4.普通成员                          0
 *
 * @author tgzwmkkkk
 */

public enum ManagerType {
    //
//    SuperManager("SuperManager", 1000),
//    SpManager("SpManager", 500),
//    SpMember("SpMember", 100),
//    CommonMember("CommonMember", 0);
    SuperManager("超级管理员", 1000),
    SpManager("渠道管理员", 500),
    SpMember("渠道成员", 100),
    CommonMember("普通成员", 0),
    NONE("无", -1);
    private String name;
    private int id;


    ManagerType(String name, int id) {
        this.name = name;
        this.id = id;
    }

    /**
     * 获取对应 id 的 ManagerType
     */
    public static ManagerType pareseTo(Integer id) {
        if (id == null) {
            return CommonMember;
        }
        for (ManagerType p : ManagerType.values()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return CommonMember;
    }

    /**
     * 通过权限判断 能否添加子用户
     */
    public static boolean canAddMember(ManagerType currType, ManagerType addType) {
        boolean canAdd = false;
        if (currType.getId() < addType.getId()) {
            return canAdd;
        }
        //选择能够添加的情况
        /*1.超级管理员随便加
         * 2.渠道管理员只能加渠道成员
         * 3.渠道成员不能添加
         * 4.普通成员不能添加
         * */
        switch (currType) {
            case SuperManager:
                canAdd = true;
                break;
            case SpManager:
                if (addType == SpMember) {
                    canAdd = true;
                }
                break;
            default:
                break;
        }
        return canAdd;
    }

    /**
     * 通过权限判断 能否添加子用户
     */
    public static boolean canAddMemberInteger(Integer currTypeInt, Integer addTypeInt) {
        boolean canAdd = false;
        if (currTypeInt < addTypeInt) {
            return canAdd;
        }

        ManagerType currType = pareseTo(currTypeInt);
        ManagerType addType = pareseTo(addTypeInt);
        if (currType == NONE || addType == NONE) {
            return false;
        }
        //选择能够添加的情况
        /*1.超级管理员随便加
         * 2.渠道管理员只能加渠道成员
         * 3.渠道成员不能添加
         * 4.普通成员不能添加
         * */
        switch (currType) {
            case SuperManager:
                canAdd = true;
                break;
            case SpManager:
                if (addType == SpMember) {
                    canAdd = true;
                }
                break;
            default:
                break;
        }
        return canAdd;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
