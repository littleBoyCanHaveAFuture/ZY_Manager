package com.zyh5games.entity;

import com.zyh5games.util.enums.ManagerType;
import lombok.Data;

/**
 * 每个账号有几种权限
 * 1.账号权限：最高管理员、渠道管理员、渠道成员、普通成员。可以查看修改自己和他人账号权限；
 * 2.模块权限：25个模块灵活开关，显示可以使用的功能。
 * 3.数据权限：在某些功能下，限制查询对应渠道的数据。
 *
 * @author Administrator
 */
@Data
public class User {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 用户姓名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 用户名
     */
    private String roleName;
    /**
     * 用户权限
     */
    private String func;

    /**
     * 管理员权限 参考
     * ManagerType
     */
    private Integer managerLv;
    /**
     * 可以查看的渠道
     * 超级管理员：所有
     * 渠道：该渠道
     */
    private String spId;
    /**
     * 所属渠道: 1=壹帆，2=神之域
     */
    private int agents;

    public User() {
    }

    public User(String userName, String password, Integer managerLv) {
        this.userName = userName;
        this.password = password;
        this.managerLv = managerLv;
    }

    /**
     * 查询用户数据时 设置 用户可查看渠道
     */
    public static void setClientSpid(User user) {
        if (user == null || user.getManagerLv() == null) {
            return;
        }
        ManagerType currType = ManagerType.pareseTo(user.getManagerLv());
        switch (currType) {
            case SuperManager:
                //超级管理员 不需要筛选
                user.setSpId("所有渠道");
                break;
            case SpManager:
                //筛选渠道
                break;
            case SpMember:
                user.setRoleName("渠道成员");
                break;
            case CommonMember:
                user.setSpId("无");
                break;
            default:
                break;
        }
    }

    @Override
    public String toString() {
        return "User [" + "id=" + id +
                ", userName=" + userName +
                ", password=" + password +
                ", roleName=" + roleName +
                ", func=" + func +
                ", manager_lv=" + managerLv +
                ", spId=" + spId +
                ", agents=" + agents +
                "]";
    }
}
