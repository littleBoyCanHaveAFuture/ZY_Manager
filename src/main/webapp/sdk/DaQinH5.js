let cySdk = window.CY_GAME_SDK;
let hasInit = false;
let initParams = {
    gameId: "游戏的ID ", //游戏的ID
    pay: {
        success: function (e) {
            if (e === 'success') {
                /* 支付成功回调方法（仅针对于快捷支付方式有效，该方法不做回调处理，游戏发货请以服务端回调为准）*/
                console.log("game tell pay success");//该方法仅供参考
            } else if (e === 'payClose') {
                //用户关闭支付窗口
                console.log("game tell pay close");//该方法仅供参考
            }
        }
    },
    share: {
        success: function (e) {
            /* 分享成功回调方法*/
            console.log("game tell share success");//该方法仅供参考
        }
    },
    uploadGameRole: {
        success: function (e) {
            /* 上报角色信息回调方法*/
            console.log("game tell uploadGameRole success");//该方法仅供参考
        }
    }
};

//用户点击分享
function share() {
    cySdk.showShare();
}


//登录被顶
function repeatLogin() {
    cySdk.repeatLogin();
}

function getQueryString(url, key) {
    let list = url.split("&");
    for (let i in list) {
        let arr = list[i].split("=");
        if (arr[0] === key) {
            return arr[1];
        }
    }
}

// 获取欢聚sdk游戏id
function zyCallChannelInit(params) {
    let gameId = 0;
    if (typeof (params) == 'object') {
        gameId = params.gameId;
    } else {
        gameId = getQueryString(params, 'gameId');
    }
    console.log(params);
    console.log(gameId);
    initParams.gameId = gameId;
    if (!hasInit) {
        cySdk = window.CY_GAME_SDK;
        if (cySdk !== null && cySdk !== undefined) {
            cySdk.config(initParams);//初始化
            hasInit = true;
        }
    }
}

//用户点击商品下单
function zyCallChannelPay(order) {
    if (!hasInit) {
        cySdk = window.CY_GAME_SDK;
        cySdk.config(initParams);//初始化
        hasInit = true;
    }
    console.log("zyCallChannelPay = " + JSON.stringify(order));

    let trade = JSON.parse(order.zhiyueOrder.channelOrder);
    let payData = JSON.parse(trade.data)

    console.log("zyCallChannelPay payData= " + trade.data);

    cySdk.pay(payData);
}

//角色-上报数据
function zyCallUploadRole(roleInfo) {
    console.log(roleInfo);
    if (!hasInit) {
        cySdk = window.CY_GAME_SDK
        cySdk.config(initParams);//初始化
        hasInit = true;
    }
    if (roleInfo.datatype === 5) {
        return;
    }
    //对应关系
    let type = roleInfo.datatype;
    if (type === 4) {
        type = 3;
    } else if (type === 1) {
        type = 4;
    } else if (type === 3) {
        type = 1;
    }

    /*
       接收参数(CGI)		    类型	            必选	    说明
       loginType		    Int	            是	    1.开始游戏 2.创建角色 3.角色升级 4.选择区服
       gameId		        Int	            是	    游戏id
       uid		            string	        是	    用户id
       serverId		        Int	            是	    区服ID
       serverName		    string	        是	    区服名称
       userRoleId		    Int	            是	    游戏内角色ID
       userRoleName		    String	        是	    游戏角色名
       vipLevel		        String	        是	    角色VIP等级,不是vip则传入0
       userRoleLevel		String	        是	    角色等级
       rebornLevel		    String	        是	    角色转生等级.无转生则不传
       gameRoleMoney		decimal(11,2)	否	    角色已充值金额
       gameRoleGender		enum	        否	    角色性别[no 未设置 male 男 famale 女]
       gameRolePower		Int	            是	    角色战力
       gameRoleOnline		Int	            否	    角色在线时长，精确到秒
   */
    let roleParam = {};
    roleParam = {
        "loginType ": type,
        "uid": roleInfo.uid,
        "gameId": initParams.gameId,
        "serverId": roleInfo.serverId,
        "serverName": roleInfo.serverName,
        "userRoleId": roleInfo.userRoleId,
        "userRoleName": roleInfo.userRoleName,
        "vipLevel": roleInfo.vipLevel,
        "userRoleLevel": roleInfo.userRoleLevel,
        // "rebornLevel": "",
        "gameRoleGender": "no",
        "gameRolePower": roleInfo.gameRolePower,
        // "gameRoleMoney": 10.00,
        // "gameRoleOnline": 10,
    };
    cySdk.uploadGameRole(roleParam);
}

