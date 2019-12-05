package com.ssm.promotion.core.util.enums;

import com.ssm.promotion.core.util.StringUtil;

/**
 * @author song minghua
 * @date 2019/12/4
 */
public enum ServiceType {
    //支付token
    PAY(2, (short) 768),
    //后台token
    MANAGE(3, (short) 1024),
    //登陆token
    LOGIN(11, (short) 4864);

    public static final short PAY_BEGIN = 768;
    public static final short MANAGE_BEGIN = 1024;
    public static final short LOGIN_BEGIN = 4864;

    private int id;
    private String name;

    ServiceType(int id, short opcodeStart) {
        this.id = id;
        this.name = StringUtil.firstChangeCase(this.toString(), true);

    }

    public static ServiceType valueOf(int id) {
        ServiceType[] var1 = values();
        int var2 = var1.length;

        for (int var3 = 0; var3 < var2; ++var3) {
            ServiceType type = var1[var3];
            if (type.id == id) {
                return type;
            }
        }

        return null;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }


}
