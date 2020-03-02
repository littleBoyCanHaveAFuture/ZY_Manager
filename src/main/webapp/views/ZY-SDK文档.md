##H5游戏接入文档（网页）

###流程描述:
1. 游戏开发者在游戏主页引入libZySdk_v1.js类库.
2. 引入类库后 调用类库中的初始化方法.
3. 在初始化完成的回调中,调用登录方法,从登录方法的回调中获取用户uid和token.
4. 将js端取到的uid和token存储并调用相应接口，上报数据。

###接入流程

####1. 引用JS类库.
````
  js文件地址：http://zy.hysdgame.cn/sdk/common/libZySdk_v1.js  
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
function init() {
    let appId = '11';   
    let channelId = '0';
    let GameKey = "l44i45326jixrlaio9c0025g974125y6";    
    ZySDK.init(appId, GameKey, channelId, function (state) {
        if (state === true) {
            console.info("init ok");
        } else {
            console.error("init fail");
        }
    });
}
````
返回值:

|字段|	类型|	说明|必有
|:-----:|:-----:|:-----:|:-----:|
|state |  bool  |   初始化结果 ,true成功,false失败  |√


#####2.渠道用户信息初始化
玩家渠道账号登录完成后，获取到渠道用户uid。调用此接口，完成渠道用户初始化。  

传入参数：

|字段|	类型|	说明|必选
|:-----:|:-----:|:-----:|:-----:|
|channelUid |  string  |   渠道用户uid(纯数字id)   |√
|channelUName |  string  |   渠道用户账号 |
````
function SpInit() {
    let channelUid = "";
    let channelUName = "";
    ZySDK.initUser(channelUid, channelUName, function (data) {
        if (data.state === false) {
            console.error(data.message);
        } else {
            console.info(data.message);
        }
    });
}
````
返回参数：

|字段|	类型|	说明|必有
|:-----:|:-----:|:-----:|:-----:
|state |  bool  |   初始化结果,true成功,false失败  |√
|message |  string  |   初始化详细内容  |√

#####3.指悦平台账号登录
前面初始化完成后，调用ZySDK的login方法。  
1. 登录获取指悦平台uid。
传入参数：

|字段|	类型|	说明|必选
|:-----|:-----:|:-----:|:-----:
| loginInfo|Object|
| loginInfo.isAuto     |boolean   | 是否渠道自动注册。true:channelUid(渠道账号id) 必选     |√
| loginInfo.GameId     |number    | 游戏id             |√
| loginInfo.channelId  |number    | 渠道id            |√
| loginInfo.channelUid |string    | 渠道账号id         |
| loginInfo.username   |string    | 指悦账号           |
| loginInfo.password   |string    | 指悦账号密码       |
| loginInfo.timestamp  |number    | 时间戳            |√
| loginInfo.sign       |string    | 签名              |√

1.渠道uid登录的情况下，只需要如下所示。设置isAuto即可。  
2.返回值，无此账号情况下，可以调用zy_Register()函数自动注册。  

注：实际调用方法如下，出现的参数需赋值，未出现的无需赋值。
````
function zy_Login() {
    let loginInfo = {};
    loginInfo.isAuto = $("#isChannel").val() === "true";

    ZySDK.zyLogin(loginInfo, function (callbackLoginData) {
        if (callbackLoginData.state === false) {
            console.error(callbackLoginData.message);
            if (callbackLoginData.message === "无此渠道用户") {
                zy_Register();
            }
        } else {
            console.info(JSON.stringify(callbackLoginData));
        }
    });
}
````
返回参数：

|字段|	类型|	说明|必有
|:-----|:-----:|:-----:|:-----:
| rspObject|Object|
| rspObject.message         |string     | 提示内容                                 |√
| rspObject.state           |boolean    | 返回：true成功,false失败 (无下列返回值)    |√
| rspObject.GameId          |number     | 游戏id
| rspObject.channelId       |number     | 渠道id
| rspObject.channelUid      |string     | 渠道账号id
| rspObject.channelToken    |string     | 渠道登录token
| rspObject.zyUid           |number     | 平台uid
| rspObject.username        |string     | 平台账号
| rspObject.password        |string     | 平台密码
| rspObject.loginUrl        |string     | 后台填写的登录地址
| rspObject.paybackUrl      |string     | 后台填写的渠道回调地址


#####2.注册平台账号
1. 推荐 auto为true用法。  
2. auto为true时，channelUid必须有值，其他需有字段，赋值值为空即可。  

传入参数：

|字段|	类型|	说明|必选
|:-----|:-----:|:-----:|:-----:
| regInfo               |Object    | 注册信息               
| regInfo.auto          |boolean   | 是否无需账号密码注册        |√
| regInfo.appId         |number    | 游戏id                    |√
| regInfo.channelId     |number    | 渠道id                    |√
| regInfo.channelUid    |string    | 渠道账号id                
| regInfo.channelUname  |string    | 渠道账号名称
| regInfo.channelUnick  |string    | 渠道账号昵称
| regInfo.username      |string    | 指悦账号
| regInfo.password      |string    | 指悦账号密码
| regInfo.phone         |string    | 手机号                    |√
| regInfo.deviceCode    |string    | deviceCode               |√
| regInfo.imei          |string    | 时间戳                    |√
| regInfo.addparm       |string    | 额外参数                  |√

注：实际调用方法如下，出现的参数需赋值，未出现的无需赋值，方法里已处理。
````
function zy_Register() {
    let regInfo = {};
    regInfo.auto = $("#auto").val() === "true";
    regInfo.channelUid;
    regInfo.username = "";
    regInfo.password = "";
    regInfo.phone = "";
    regInfo.deviceCode = "";
    regInfo.imei = "";
    regInfo.addparm = "";

    ZySDK.zyRegister(regInfo, function (callbackData) {
        if (callbackData.state === false) {
             console.error(callbackData.message);
        } else {
            console.info(callbackData.message);
            console.info(JSON.stringify(callbackData));
        }
    });
}
````
返回参数：

|字段|	类型|	说明|必有
|:-----|:-----:|:-----:|:-----:
| rspObject|Object|
| rspObject.message     |string     | 提示内容                                  |√
| rspObject.state       |boolean    | 返回：true成功,false失败 (无下列返回值)     |√
| rspObject.uid         |number     | 平台uid
| rspObject.account     |string     | 平台账号
| rspObject.password    |string     | 平台密码
| rspObject.channelUid  |string     | 渠道账号id

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



