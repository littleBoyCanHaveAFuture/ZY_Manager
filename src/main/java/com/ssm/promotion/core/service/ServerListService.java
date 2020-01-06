package com.ssm.promotion.core.service;

import com.ssm.promotion.core.entity.ServerInfo;
import com.ssm.promotion.core.entity.Sp;

import java.util.List;
import java.util.Map;
/**
 * @author song minghua
 */
public interface ServerListService {

    /**
     * 获取 服务器列表
     */

    public List<ServerInfo> getServerList(Map<String, Object> map, Integer userId);

    /**
     * 添加服务器
     */
    public int addServer(ServerInfo server, Integer userId) throws Exception;

    /**
     * 修改服务器
     */
    public int updateServer(ServerInfo server, Integer userId);

    /**
     * 删除服务器
     */
    public int delServer(Integer id, Integer userId);

    /**
     * 服务器总数
     */
    Long getTotalServers(Map<String, Object> map, Integer userId);

    /**
     * 不同的区服渠道
     *
     * @param type 1 返回渠道
     *             2 返回游戏
     *             3 返回区服
     */
    List<Integer> getDistinctServerInfo(Map<String, Object> map, Integer type, Integer userId);


    /**
     * 某产品某运营商旗下
     * 用户是否可注册
     */
    boolean isSpCanReg(Map<String, Object> map, Integer userId);

    /**
     * 某产品某运营商旗下
     * 用户是否可登录
     */
    boolean isSpCanLogin(Map<String, Object> map, Integer userId);

    boolean existSGS(Map<String, Object> map, Integer userId);

    String getOpenday(Map<String, Object> map, Integer userId);

    /**
     * 私钥
     */
    String selectPrivateKey(Map<String, Object> map, Integer userId);

    /**
     * 公钥
     */
    String selectPublicKey(Map<String, Object> map, Integer userId);

    /**
     * 公钥
     */
    String selectLoginUrl(Map<String, Object> map, Integer userId);

    /**
     * md5秘钥
     */
    String selectSecertKey(Map<String, Object> map, Integer userId);

    List<Sp> getAllSp(Integer userId);

    List<Sp> selectSpByIds(Map<String, Object> map, Integer userId);

    List<Sp> getSpById(Map<String, Object> map, Integer userId);

    List<Sp> getAllSpByPage(Map<String, Object> map, Integer userId);

    Long getTotalSp(Integer userId);

    /**
     * 删除渠道信息
     */
    public int delSp(Integer id, Integer userId);

    /**
     * 更新渠道信息
     */
    int updateSp(Map<String, Object> map, Integer userId);

    /**
     * 添加渠道信息
     */
    int addSp(Map<String, Object> map, Integer userId);
}
