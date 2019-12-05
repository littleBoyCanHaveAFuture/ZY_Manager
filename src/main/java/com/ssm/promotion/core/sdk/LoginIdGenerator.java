package com.ssm.promotion.core.sdk;

/**
 * @author song minghua
 * @date 2019/12/5
 */

import lombok.Data;
import xyz.downgoon.snowflake.Snowflake;
import xyz.downgoon.snowflake.util.BinHexUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class LoginIdGenerator {
    private static int idAmout = 1000;
    private static final List<Long> idPool = Collections.synchronizedList(new ArrayList<>(idAmout + 10));

    /**
     * 机房
     */
    private long datacenterId = 0L;
    /**
     * 机器
     */
    private long workerId = 0L;

    /**
     * 快速生成1千个ID，基本在1毫秒内就能
     */
    public static void main(String[] args) {
        // 构造方法设置机器码：第1个机房的第1台机器
        Snowflake snowflake = new Snowflake(0, 0);
        final int idAmout = 1;
        List<Long> idPool = new ArrayList<>(idAmout);
        for (int i = 0; i < idAmout; i++) {
            long id = snowflake.nextId();
            idPool.add(id);
        }

        for (Long id : idPool) {
            System.out.println(String.format("%s => id: %d, hex: %s, bin: %s",
                    snowflake.formatId(id), id,
                    BinHexUtil.hex(id), BinHexUtil.bin(id)));
        }
    }

    public long getRandomId() {
        synchronized (idPool) {
            if (idPool.size() < 10) {
                // 构造方法设置机器码：第1个机房的第1台机器
                Snowflake snowflake = new Snowflake(datacenterId, workerId);
                for (int i = 0; i < idAmout; i++) {
                    long genId = snowflake.nextId();
                    idPool.add(genId);
                }
            }
            return idPool.remove(idPool.size() - 1);
        }

    }

    public void init() {
        synchronized (idPool) {
            if (idPool.size() != 0) {
                return;
            }
            // 构造方法设置机器码：第1个机房的第1台机器
            Snowflake snowflake = new Snowflake(datacenterId, workerId);
            for (int i = 0; i < idAmout; i++) {
                long id = snowflake.nextId();
                idPool.add(id);
            }
        }
    }

}