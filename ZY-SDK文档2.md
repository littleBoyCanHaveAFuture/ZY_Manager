##H5游戏接入文档（网页）

###流程描述:
1. 游戏开发者在游戏主页引入libZySdk_v1.js类库.
2. 引入类库后 调用类库中的初始化方法.
3. 在初始化完成的回调中,调用登录方法,从登录方法的回调中获取用户uid和token.
4. 将js端取到的uid和token存储并调用相应接口，上报数据。

###接入流程

####1. 引用JS类库.
````
  js文件地址：http://zy.hysdgame.cn/sdk/common/libZySdk_v2.js  
````  
注意: 游戏应原样引入此JS,不能随意变更协议为http或在后面附加时间戳。    
````        
  游戏测试地址：http://zy.hysdgame.cn:8080/views/test.jsp
 ````
  请通过浏览器调试工具查看，按键F12。详细数据请看js文件，函数参数返回值均有jsdoc。


####2. 初始化ZySDK
#####1.sdk初始化
加载游戏登录地址时，使用ZySDK后台分配给游戏的参数，调用ZySDK的init接口。  

传入参数：

|字段|类型|说明|必选|
|:-----:|:-----:|:-----:|:-----:|
|appId      |  number  |   游戏id，ZySDK后台自动分配    | √
|channelId  |  number  |   游戏渠道id，手动分配         |√
|GameKey    |  string  |   游戏秘钥，ZySDK后台自动分配  |√
````
function sdkInit(appId, GameKey) {
    ZySDK.init(appId, GameKey, function (status) {
        if (!status) {
            console.error("zySDK init fail");
            return;
        }
        console.log("zySDK init succ");
    });
}
````
返回值:

|字段|	类型|	说明|必有
|:-----:|:-----:|:-----:|:-----:|
|state |  bool  |   初始化结果 ,true成功,false失败  |√


#####2.渠道用户信息初始化
玩家渠道账号登录完成后，调用此接口，完成游戏登陆。  
````
function sdkLogin(appId, GameKey) {
    ZySDK.login(function (callbackData) {
        if (callbackData.status) {
            console.log('GameDemo:zySDK登录成功: uid=>' + callbackData.data.uid);
        } else {
            console.log('GameDemo:zySDK登录失败:' + callbackData.message);
        }
    });
}
````
返回参数：

|字段|	类型|	说明|必有
|:-----:|:-----:|:-----:|:-----:
|state  |  bool  |   初始化结果,true成功,false失败  |√
| data  | json   |玩家信息|√
|	data.uid        | string    |	渠道uid
|   data.username   |	string  | 渠道username
|   data.token      |	string  | 游戏服务器，需通过webGame2/checkUserInfo接口(参见服务器接口文档)验证token和UID的正确性
|   data.isLogin    |   bool  	| 是否游客,登录后此值为true
|   data.channelId  |   string	| 渠道ID
| message           |   string  | 提示内容
message	status为false时,此字段为Failed表示登录失败,为cancel表示玩家取消登录
````
{"status":true,"data":{"uid":"123","username":"quicksdk","token":"","isLogin":true,"channelId":8},"message":""}
````
获取回调信息中的uid和token发回游戏服务器,游戏服务器按 #3 调用用户验证接口.

#####3.验证用户信息接口
1.1接口说明
1. 该接口为游戏服务器对SDK服务器发起的接口。
2. 该接口的功能主要为:游戏服务器通过token向SDK服务器验证用户信息。
3. token值会由SDK客户端告知给游戏客户端。
4. 成功返回1。
5. 同渠道UID绝对唯一，不同渠道UID可能重复，游戏服务器必须 使用渠道ID + 渠道UID 方能确保游戏角色唯一。
6. 游戏最终发放道具金额应以amount为准

1.2 API地址
````
地址: http://zy.hysdgame.cn:8080/webGame2/checkUserInfo
````
|字段|	类型|	说明|必有
|:-----:|:-----:|:-----:|:-----:
|token          |string     |从游戏客户端从SDK客户端中获取的token值,原样传递无需解密,此值长度范围小于512,CP需预留足够长度|√
|gameKey        |string     |Quick后台查看游戏信息里可获取此值|√
|uid            |string     |从客户端接口获取到的渠道原始uid,无需任何加工如拼接渠道ID等|√
|channelId	    |string     |传入此值将校验uid和token是否与 channelId 一致|√

#####4.上传角色信息接口
1. 进入游戏，若未创建角色，则在创建后调用此函数,key = "createRole"。  
2. 选择完角色后，进入游戏场景内调用。key ="enterGame"。  
3. 退出游戏时调用。key = "exitGame"。  
4. 角色升级时调用。key = "levelUp"  


|字段|	类型|	说明|必选
|:-----|:-----:|:-----:|:-----:
| key                       |string     |       createRole,enterGame,exitGame,levelUp                     |√
| roleInfo                  |Object     |       角色信息                                                   |√ 
| roleInfo.appId            |number     |       游戏id                                                     |√
| roleInfo.channelId        |number     |       玩家渠道id                                                 |√
| roleInfo.channelUid       |number     |       玩家渠道账号id                                              |√
| roleInfo.roleId           |number     |       当前登录的玩家角色ID，必须为数字                               |√
| roleInfo.roleName         |string     |       当前登录的玩家角色名，不能为空，不能为null                      |√ 
| roleInfo.roleLevel        |number     |       当前登录的玩家角色等级，必须为数字，且不能为0，若无，传入1         |√ 
| roleInfo.zoneId           |number     |       当前登录的游戏区服ID，必须为数字，且不能为0，若无，传入1          |√ 
| roleInfo.zoneName         |string     |       当前登录的游戏区服名称，不能为空，不能为null                    |√ 
| roleInfo.balance          |number     |       用户游戏币余额，必须为数字，若无，传入0                         |√ 
| roleInfo.vip              |number     |       当前用户VIP等级，必须为数字，若无，传入1                        |√ 
| roleInfo.partyName        |string     |       当前角色所属帮派，不能为空，不能为null，若无，传入“无帮派”        |√ 
| roleInfo.roleCTime        |number     |       单位为毫秒，创建角色的时间(创建必选)
| roleInfo.roleLevelMTime   |number     |       单位为毫秒，角色等级变化时间(升级必选)

注：实际调用方法如下，出现的参数需赋值，未出现的无需赋值，方法里已处理。
````
function zy_upload(type) {
    let roleInfo = {};
    roleInfo.roleId = "";
    roleInfo.roleName = "";
    roleInfo.roleLevel = 1;
    roleInfo.zoneId = ""
    roleInfo.zoneName = "";
    roleInfo.balance = 0;
    roleInfo.vip = 1;
    roleInfo.partyName = "无帮派";
    let key;
    if (type === 1) {
        key = "createRole";
        roleInfo.roleCTime = new Date().valueOf();
    } else if (type === 2) {
        key = "enterGame";
    } else if (type === 3) {
        key = "exitGame";
    } else {
        key = "levelUp";
        roleInfo.roleLevelMTime = new Date().valueOf();
    }
    let result = ZySDK.uploadGameRoleInfo(key, roleInfo, function (callbackData) {
        if (callbackData.state === false) {
            console.error(callbackData.message);
            console.error(JSON.stringify(roleInfo));
        } else {
            console.info(JSON.stringify(callbackData));
        }
    });
}
````
返回参数：

|字段|	类型|	说明|必有
|:-----|:-----:|:-----:|:-----:
| rspObject             |Object     |
| rspObject.message     |string     | 提示内容                                  |√ 
| rspObject.state       |boolean    | 返回：true成功,false失败 (无下列返回值)      |√ 
| rspObject.GameId      |number     | 平台游戏id
| rspObject.channelId   |number     | 平台渠道id
| rspObject.zoneId      |string     | 区服id
| rspObject.balance     |string     | 用户游戏币余额


#####5.调用支付上报接口
1. 玩家打开商品界面，并点击购买。此时请求游戏服务器并生成了游戏订单。客户端需要调用此接口上报订单数据。status = 1。  
2. 玩家点击支付宝/微信支付，跳转到支付界面，并调起支付。支付成功，status = 3，支付取消，status = 2。  
3. 支付成功后，收到商品，则status = 4。  
4. 支付成功后，未收到商品，但是补发了商品，则status = 5。  

传入参数：

|字段|	类型|	说明|必选
|:-----|:-----:|:-----:|:-----:
| orderInfo                     |Object     |充值信息
| orderInfo.accountID           |number     |指悦账号uid                |√ 
| orderInfo.channelId           |number     |渠道id                    |√ 
| orderInfo.channelUid          |number     |渠道账号uid                |√ 
| orderInfo.appId               |number     |游戏id                    |√ 
| orderInfo.channelOrderID      |string     |渠道订单号                 |√ 
| orderInfo.productID           |string     |当前商品ID                 |√ 
| orderInfo.productName         |string     |商品名称                   |√ 
| orderInfo.productDesc         |string     |商品描述                   |√ 
| orderInfo.money               |number     |商品价格,单位:分            |√ 
| orderInfo.roleID              |number     |玩家在游戏服中的角色ID       |√ 
| orderInfo.roleName            |string     |玩家在游戏服中的角色名称     |√ 
| orderInfo.roleLevel           |number     |玩家等级                   |√ 
| orderInfo.serverID            |number     |玩家所在的服务器ID          |√ 
| orderInfo.serverName          |string     |玩家所在的服务器名称         |√ 
| orderInfo.realMoney           |number     |订单完成,实际支付金额,单位:分,未完成:0  |√ 
| orderInfo.completeTime        |number     |订单完成时间戳(毫秒，13位),未完成为:-1
| orderInfo.sdkOrderTime        |number     |订单创建时间戳(毫秒，13位)下单必填
| orderInfo.status              |number     |订单状态 请看OrderStatus、OrderStatusDesc|√  
| orderInfo.notifyUrl           |string     |支付回调通知的游戏服地址               |√ 
| orderInfo.signType            |string     |签名算法,RSA,MD5,默认MD5             |√
| orderInfo.sign                |string     |签名                                 |√ 

````
 channelId=0 就是官方包 此时qid=accountid=channeluid

function zy_UploadPayInfo() {
        let orderInfo = {};
        orderInfo.accountID;
        orderInfo.channelOrderID;

        orderInfo.productID;
        orderInfo.productName;
        orderInfo.productDesc;
        orderInfo.money;

        orderInfo.roleID;
        orderInfo.roleName;
        orderInfo.roleLevel;

        orderInfo.serverID;
        orderInfo.serverName;

        orderInfo.realMoney;
        orderInfo.completeTime;
        orderInfo.sdkOrderTime = new Date().valueOf();

        orderInfo.status;
        orderInfo.notifyUrl;
        orderInfo.signType = "MD5";

        if (status >= OrderStatus[4]) {
            orderInfo.completeTime= new Date().valueOf();;
        }
        let result = ZySDK.pay(orderInfo, function (callbackData) {
            if (callbackData.state === false) {
                console.error(callbackData.message);
                console.error(JSON.stringify(orderInfo));
            } else {
                console.info(JSON.stringify(callbackData));
            }
        });
        console.info("订单当前状态 " + OrderStatusDesc[orderInfo.status]);
    }
````
返回参数：

|字段|	类型|	说明|必有
|:-----|:-----:|:-----:|:-----:
| rspObject             |Object     |
| rspObject.message     |string     | 提示内容|√ 
| rspObject.state       |boolean    | 返回：true成功,false失败 (无下列返回值)|√ 
| rspObject.orderId     |string     | 平台订单id

#####6.支付sdk
1. 调起支付
````
接口地址：
1.http://zy.hysdgame.cn/pay/static/pay.html

参数拼接：
let param =
    "?orderId=" + orderId +
    "&body=" + productDesc +
    "&subject=" + productName +
    "&totalAmount=" + totalAmount +
    "&productId=" + productID +
    "&passBackParams=" + "passBackParams" +
    "&appId=" + ZySDK.GameId +
    "&spId=" + ZySDK.channelId;
window.open("http://zy.hysdgame.cn/pay/static/pay.html" + encodeURI(param));
````

````
支付地址：
1.支付宝：POST:http://www.zyh5games.com/pay/aliPay/wapPaySdk?
2.微信：POST:http://www.zyh5games.com/pay/wxPay/wapPaySdk?
````
支付宝和微信请求参数相同

|字段|	类型|	说明|必填
|:-----|:-----:|:-----:|:-----:
| appId             |number     | zy平台游戏id|√ 
| orderId           |string     | zy平台订单id|√ 
| body              |string     | 对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body|√ 
| subject           |string     | 商品的标题/交易标题/订单标题/订单关键字等。|√ 
| totalAmount       |string     | 订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]|√ 
| productId         |string     | 当前商品ID|√ 
| passBackParams    |string     | 公用回传参数，如果请求时传递了该参数，则返回给商户时会回传该参数。|√ 
|                   |           | 支付宝只会在同步返回（包括跳转回商户网站）和异步通知时将该参数原样返回。
|                   |           | 本参数必须进行UrlEncode之后才可以发送给支付宝。没有，则值为空。
| sign              |string     | RSA签名|√ 

````
Js示例
function pay(orderId, body, subject, totalAmount, productId, passBackParams) {
    let param =
        "appId=" + appId +
        "&orderId=" + orderId +
        "&body=" + body +
        "&subject=" + subject +
        "&totalAmount=" + totalAmount +
        "&productId=" + productId +
        "&passBackParams=" + passBackParams +
        "&" + ZySDK.GameKey;

    let sign_uri = encodeURIComponent(param);
    let hex_sign_uri = hex_md5(sign_uri);

    param += "&sign=" + hex_sign_uri;

    let url =  "http://www.zyh5games.com/pay/aliPay/wapPaySdk?";
    $.ajax({
        url: url + param,
        type: "post",
        success: function (result) {
            if (result.hasOwnProperty('message')) {
                alert(result.message);
            } else {
                $('#footer').html($(result));
            }
        },
    });
}
````
````
返回参数：
1.支付宝：下表格所示
2.微信：无返回参数，会直接跳转到新地址（先接支付宝）
````

|字段|	类型|	说明|必有
|:-----|:-----:|:-----:|:-----:
| rspObject.message     |string     | 提示内容 失败会有此提示
| rspObject             |string    | 支付宝js文本,以html执行，可以直接跳转到支付宝H5支付页面，调起支付宝APP



2. 支付回调
````
刺沙回调地址
Get：http://testapi.dev.9zhouapp.com/v2/game/notify/21696d0d19fa2194/zhiyuesdk/cdaf26a5936e29c4
 ````
传入参数

|字段|	类型|	说明|必填
|:-----|:-----:|:-----:|:-----:
| appId             |number     | zy平台游戏id|√ 
| orderId           |string     | zy平台订单id|√ 
| cpOrderId         |string     | 游戏方订单|√ 
| amount            |string     | 金额 元|√ 
| productId         |string     | 产品id|√ 
| roleId            |string     | 玩家角色id|√ 

回复参数

|字段|	类型|	说明|必有
|:-----|:-----:|:-----:|:-----:
| rspObject          |Object|
| rspObject.code     |number     | 1 成功，2 失败|√ 
| rspObject.msg      |string     | success 成功，error 失败。|√ 



