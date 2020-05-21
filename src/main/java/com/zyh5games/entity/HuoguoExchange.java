package com.zyh5games.entity;

import lombok.Data;

/**
 * @author song minghua
 * @date 2020/5/8
 */
@Data
public class HuoguoExchange {
    /**
     * 主键id
     */
    Integer id;
    /**
     * 商品名称
     */
    String name;
    /**
     * 图片名称
     */
    String photoName;
    /**
     * 商品剩余数量
     */
    Integer num;
    /**
     * 商品兑换价格
     */
    Integer price;

}
