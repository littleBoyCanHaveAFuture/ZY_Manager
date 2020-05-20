package com.ssm.promotion.core.entity;

import lombok.Data;

/**
 * @author song minghua
 */
@Data
public class GameNew {
    /**
     * id
     */
    private Integer id;
    /**
     * 创建者uid
     */
    private Integer ownerId;
    /**
     * 游戏id
     */
    private Integer appId;
    /**
     * 游戏名称
     */
    private String appName;
    /**
     * 游戏类型：
     * 1.h5游戏；
     * 2.单机游戏；
     * 3.网络游戏；
     * 4.H5转Android游戏；
     */
    private Integer type;

    /**
     * 游戏类别：
     * 0:未知；
     * 1:动作游戏；
     * 2.探险游戏；
     * 3.街机游戏；
     * 4.桌面游戏；
     * 5.扑克牌游戏；
     * 6.娱乐场游戏；
     * 7.骰子游戏；
     * 8.教育游戏；
     * 9.家庭游戏；
     * 10.儿童游戏；
     * 11.音乐游戏；
     * 12.智力游戏；
     * 13.赛车游戏；
     * 14.角色扮演游戏；
     * 15.模拟游戏；
     * 16.体育游戏；
     * 17.策略游戏；
     * 18.小游戏；
     * 19.文字游戏；
     */
    private Integer genres;

    /**
     * 题材类别：
     * 0.其他；
     * 1.修仙；
     * 2.武侠；
     * 3.玄幻；
     * 4.三国演义；
     * 5.西游记；
     * 6.封神榜；
     * 7.日漫；
     * 8.宫斗官斗；
     * 9.现代都市；
     * 10.女性向；
     * 11.西方玄幻；
     */
    private Integer theme;

    /**
     * 核心玩法(多选):使用位运算太麻烦，还是直接存字符串吧,‘|’连接
     * MMO 卡牌 休闲游戏 RPG ARPG 类传奇
     * 轻竞技/io SLG 解谜 模拟经营 换装 文字冒险
     * 战棋 沙盒 moba 竞速 传统武侠 其他
     */
    private String keyConcept;
    /**
     * ip类型：
     * 0.电影ip；
     * 1.小说ip；
     * 2.国漫ip；
     * 3.日漫ip；
     * 4.游戏ip；
     * 5.电视剧ip；
     * 6.其他ip；
     */
    private Integer ipType;
    /**
     * login_url
     */
    private String loginUrl;
    /**
     * 支付回调地址
     */
    private String paybackUrl;
    /**
     * 横屏竖屏：0.横屏；1.竖屏；
     */
    private Integer direction;
    /**
     * 游戏描述
     */
    private String description;
    /**
     * 秘钥 32位小写字母和数字
     */
    private String secertKey;
    /**
     * 秘钥 32位小写字母和数字
     */
    private String callbacKey;

    public GameNew() {

    }

}
