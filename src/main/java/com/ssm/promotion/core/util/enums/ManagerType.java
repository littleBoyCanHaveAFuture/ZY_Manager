package com.ssm.promotion.core.util.enums;

/**
 * @author tgzwmkkkk
 */

public enum ManagerType {
    //
    SuperManager("超级管理员", 1000),
    SpManager("渠道管理员", 500),
    SpMember("渠道成员", 100),
    CommonMember("普通成员", 0);


    private String name;
    private int id;


    ManagerType(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    //获取对应 id 的 ManagerType
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
}
