##H5游戏接入文档（网页）
###游戏参数
   
    游戏：海盗传说
    GameId = 18
    GameKey = jat7e8e3w04lh2g213ie4f8ro4mhpx4n
    channelId = 28
    callbackKey = taani1t80g33dm3o600tyum8fm6um623
    
###流程描述:
1. 游戏开发者在游戏主页引入libZySdk_v2.js类库.
2. 引入类库后 调用类库中的初始化方法.
3. 在初始化完成的回调中,调用登录方法,从登录方法的回调中获取用户uid和token.
4. 将js端取到的uid和token存储并调用相应接口，上报数据。
5. 参数必有 √ 字段和值都要有，反之 只需字段 值为空即可。

###接入流程

#####1. 引用JS类库.
````
  js文件地址：https://zyh5games.com/sdk/libZySdk_v2.js 
````  
注意: 游戏应原样引入此JS,不能随意变更协议为https或在后面附加时间戳。    

#####2. 初始化ZySDK

######2.1 sdk初始化
    加载游戏登录地址时，使用ZySDK后台分配给游戏的参数，调用ZySDK的init接口。  
    
######2.2 传入参数：
|字段|类型|说明|必选|
|:-----:|:-----:|:-----:|:-----:|
|GameId      |  string  |   平台游戏id（后台自动分配 ）    | √
|GameKey     |  string  |   游戏渠道id，手动分配          | √
|channelId  |  string  |    平台渠道id（后台自动分配 ）    | √

######2.3.返回值:

|字段|	类型|	说明|必有
|:-----:|:-----:|:-----:|:-----:|
|status |  bool  |   初始化结果 ,true成功,false失败  |√

######2.4 js示例：
````
    ZhiYueSDK.init(GameId, GameKey, channelId, function (status) {
        if (!status) {
            console.error("ZhiYueSDK init fail");
            return;
        }
        console.log("ZhiYueSDK init succ");
    });
````

#####3.渠道用户信息初始化
######3.1 
    玩家渠道账号登录完成后，调用此接口，完成登录校验。
    渠道会带参数访问游戏地址，cp不需要处理这些参数
    获取回调信息中的uid和token发回游戏服务器,cp游戏服务器按 #4 调用用户验证接口.
######3.2 返回参数：

|字段|	类型|	说明|必有
|:-----|:-----:|:-----:|:-----:
|   status              |   bool    |   初始化结果,true成功,false失败 |√
|   message             |   string  |   提示内容|√
|   data                |   json    |   玩家信息|√
|	    data.uid        |   string  |	渠道uid
|       data.username   |	string  |   渠道username
|       data.token      |	string  |   游戏服务器，需通过 webGame2/checkUserInfo 接口(参见服务器接口文档)验证token和UID的正确性
|       data.isLogin    |   bool  	|   是否游客,登录后此值为true
|       data.time       |   string	|   时间
|       data.channelId  |   string	|   渠道ID
````
{
    "data": {
        "uid": "1000175",
        "isLogin": true,
        "time": 1589813405,
        "zhiyueUid": "",
        "channelId": 0,
        "username": "",
        "token": "g73zosyo8vfmu08s6v3l4gt6oi7t7omw"
    },
    "status": true,
    "message": ""
}
````
######3.3 js示例： 
````
ZhiYueSDK.login(function (callbackData) {
    if (callbackData.status) {
        console.log('GameDemo:ZhiYueSDK登录成功: uid=>' + callbackData.data.uid);
        //模拟cp服务器进行登录校验
        checkUserInfo(callbackData);
    } else {
        console.log('GameDemo:ZhiYueSDK登录失败:' + callbackData.message);
    }
});
````

#####4.验证用户信息接口

######4.1 接口说明
1. 该接口为游戏服务器对SDK服务器发起的接口。
2. 该接口的功能主要为:游戏服务器通过token向SDK服务器验证用户信息。
3. token值会由SDK客户端告知给游戏客户端。
4. 成功返回1。
5. 同渠道UID绝对唯一，不同渠道UID可能重复，游戏服务器必须 使用渠道ID + 渠道UID 方能确保游戏角色唯一。

######4.2 API地址
````
地址: http://zy.hysdgame.cn:8080/webGame2/checkUserInfo
````
######4.3 请求方法
    
    GET/POST
    
######4.4 请求参数

|字段|	类型|	说明|必有
|:-----:|:-----:|:-----:|:-----:
|token          |string     |从游戏客户端从SDK客户端中获取的token值,原样传递无需解密,此值长度范围小于512,CP需预留足够长度|√
|gameKey        |string     |ZhiYue 后台,查看游戏信息里可获取此值|√
|uid            |string     |从客户端接口获取到的渠道原始uid,无需任何加工如拼接渠道ID等|√
|channelId	    |string     |传入此值将校验uid和token是否与 channelId 一致|√

######4.5 返回参数

     返回字符串：成功返回1。

#####5.上传角色信息接口
|字段|	类型|	说明|必选
|:-----|:-----:|:-----:|:-----:
|    datatype        |number|     1.选择服务器 2.创建角色 3.进入游戏 4.等级提升 5.退出游戏"|√
|    roleCreateTime  |string|      角色创建时间 时间戳 单位 秒|√
|    uid             |string|      渠道UID|√
|    username        |string|      渠道账号昵称|√
|    serverId        |number|      区服ID|√
|    serverName      |string|      区服名称|√
|    userRoleName    |string|      游戏内角色ID|√
|    userRoleId      |string|      游戏角色|√
|    userRoleBalance |number|      角色游戏内货币余额|√
|    vipLevel        |number|      角色VIP等级|√
|    userRoleLevel   |number|      角色等级|√
|    partyId         |string|      公会/社团ID|√
|    partyName       |string|      公会/社团名称|√
|    gameRoleGender  |string|      角色性别
|    gameRolePower   |string|      角色战力
|    partyRoleId     |string|      角色在帮派中的ID
|    partyRoleName   |string|      角色在帮派中的名称
|    professionId    |string|      角色职业ID
|    profession      |string|      角色职业名称
|    friendlist      |string|      角色好友列表

````
    let roleInfo = {};
    roleInfo.datatype = type;
    roleInfo.roleCreateTime = Date.parse(new Date()) / 1000;
    roleInfo.uid = uid;
    roleInfo.username = 'username_' + uid;
    roleInfo.serverId = 1;
    roleInfo.serverName = '内测1区';
    roleInfo.userRoleName = 'username_' + uid;
    roleInfo.userRoleId = roleId;
    roleInfo.userRoleBalance = 1000;
    roleInfo.vipLevel = 1;
    roleInfo.userRoleLevel = 1;
    roleInfo.partyId = 1;
    roleInfo.partyName = '行会名称';
    roleInfo.gameRoleGender = '男';
    roleInfo.gameRolePower = 100;
    roleInfo.partyRoleId = 1;
    roleInfo.partyRoleName = '会长';
    roleInfo.professionId = '1';
    roleInfo.profession = '武士';
    roleInfo.friendlist = '';
    ZhiYueSDK.uploadGameRoleInfo(roleInfo, function (response) {
        if (response.status) {
            console.log('提交信息成功');
        } else {
            console.log(response.message);
        }
    });
````
返回参数：

|字段|	类型|	说明|必有
|:-----|:-----:|:-----:|:-----:
| rspObject                 |Object     |
| rspObject.message         |string     | 提示内容                      |√ 
| rspObject.status          |boolean    | 返回：true成功,false失败       |√ 
| rspObject.data            |object     | 

#####6.调起支付
######6.1 
    调起渠道支付地址
######6.2 传入参数：
|字段|	类型|	说明|必有
|:-----|:-----:|:-----:|:-----:
| gameKey       |   string      |    
| uid           |   string      |  渠道UID|√
| username      |   string      |  渠道username|√
| userRoleId    |   string      |  游戏内角色ID|√
| userRoleName  |   string      |  游戏角色|√
| serverId      |   string      |  角色所在区服ID|√
| userServer    |   string      |  角色所在区服|√
| userLevel     |   string      |  角色等级|√
| cpOrderNo     |   string      |  游戏内的订单,SDK服务器通知中会回传|√
| amount        |   string      |  购买金额（元）|√
| count         |   string      |  购买商品个数|√
| quantifier    |   string      |  购买商品单位，如，个|√
| subject       |   string      |  道具名称|√
| desc          |   string      |  道具描述|√
| callbackUrl   |   string      |  Cp服务器通知地址
| extrasParams  |   string      |  透传参数,服务器通知中原样回传
| goodsId       |   string      |  商品ID|√
######6.4 js示例：
````
    //1.生成Cp订单
    let cpOrderId = "";
    //2.调用渠道支付接口
    let orderInfo = {};
    orderInfo.gameKey = ZhiYueSDK.GameKey;          //
    orderInfo.uid = channelUid;                     //渠道UID
    orderInfo.username = channelUid;                //渠道username
    orderInfo.userRoleId = roleId;                  //游戏内角色ID
    orderInfo.userRoleName = "名_" + channelUid;    //游戏角色
    orderInfo.serverId = "1";                       //角色所在区服ID
    orderInfo.userServer = "测试1区";                //角色所在区服
    orderInfo.userLevel = "1";                      //角色等级
    orderInfo.cpOrderNo = cpOrderId;                //游戏内的订单,SDK服务器通知中会回传
    orderInfo.amount = 0.01;                        //购买金额（元）
    orderInfo.count = 1;                            //购买商品个数
    orderInfo.quantifier = "个";                    //购买商品单位，如，个
    orderInfo.subject = "测试道具1";                 //道具名称
    orderInfo.desc = "测试道具1";                    //道具描述
    orderInfo.callbackUrl = "";                     //Cp服务器通知地址
    orderInfo.extrasParams = "";                    //透传参数,服务器通知中原样回传
    orderInfo.goodsId = "1";                        //商品ID

    ZhiYueSDK.pay(orderInfo, function (payStatusObject) {
        console.log(payStatusObject);
        let status = payStatusObject.hasOwnProperty("status") ? payStatusObject.orderNo : false;
        let orderNo = payStatusObject.hasOwnProperty("orderNo") ? payStatusObject.orderNo : "";
        let channelOrder = payStatusObject.hasOwnProperty("channelOrder") ? payStatusObject.channelOrder : "";
        console.log("status=" + status);
        console.log("orderNo" + orderNo);
        console.log("channelOrder=" + channelOrder);
    });

    //3.SDK服务器收到渠道支付回调 处理订单

````
#####7. 支付回调
````
Cp回调地址
Get：http://testapi.dev.9zhouapp.com/v2/game/notify/******
 ````
传入参数

|字段|	类型|	说明|必填
|:-----|:-----:|:-----:|:-----:
| amount            |string     | 金额 元|√ 
| appId             |number     | zy平台游戏id|√ 
| cpOrderId         |string     | 游戏方订单|√ 
| orderId           |string     | zy平台订单id|√ 
| productId         |string     | 产品id|√ 
| roleId            |string     | 玩家角色id|√ 
| sign              |string     | 签名|√ 
回复参数

|字段|	类型|	说明|必有
|:-----|:-----:|:-----:|:-----:
| rspObject          |Object|
| rspObject.code     |number     | 1 成功，2 失败|√ 
| rspObject.msg      |string     | success 成功，error 失败。|√ 

签名方式

    加密字符串 = data="amount={amount}&appId={appId}&cpOrderId={cpOrderId}&orderId={orderId}&productId={productId}&roleId={roleId}"
    sign = md5(data + callbackKey)
    请求参数："?amount={amount}&appId={appId}&cpOrderId={cpOrderId}&orderId={orderId}&productId={productId}&roleId={roleId}&sign={sign}"






