package com.zyh5games.util.enums;

/**
 * @author tgzwmkkkk
 */

public enum FunctionType {

    //
    NONE("无权限", 0),
    GameDetail("游戏概况", 10000),
    LiveData("实时数据", 20000),
    PlayerInfo("玩家信息", 30000),
    DataAnalysis("数据分析", 40000),
    GMFunction("GM功能", 50000),
    ServerManagement("服务器管理", 60000),
    AccountManagement("账号管理", 70000);

    private String name;
    private Integer id;


    FunctionType(String name, Integer id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Integer getId() {
        return id;
    }
}
