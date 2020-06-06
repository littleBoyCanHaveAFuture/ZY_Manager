window.DSSDK = (function (w, d) {
    var PARTNER_SDK = h5Sdk();
    var ua = navigator.userAgent;

    var PARTNER_CALLS = [];
    w.DKMGetParameter = function(name){
        var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
        var r = window.location.search.substr(1).match(reg);
        if(r != null){
            return decodeURIComponent(r[2]);
        }else{
            return '';
        }
    };

    w.DKM_LOADING_SCRIPT = {};
    w.DKM_SCRIPT_LOAD_CALLBACKS = {};
    w.DKM_GAME_INFO = {};


    function run(method, data, callback) {
        (method in PARTNER_SDK) && PARTNER_SDK[method](data, callback);
    }


    var dsSdk = {
        //调用登录／注册界面
        login: function (callback) {
            run('login', '', callback);
        },

        //用户登出
        logout: function(callback){
            run('logout', '', callback);
        },

        //支付
        pay: function(data, callback){
            //console.log("cp发起支付请求");
            //console.log(data);
            run('pay', data, callback);
        },

        //打开客服窗口
        openCsCenter: function () {
            run('openCsCenter');
        },


        //角色创建LOG
        logCreateRole: function (serverId, serverName, roleId, roleName, roleLevel) {
            var data = {
                server_id: serverId,
                server_name: serverName,
                role_id: roleId,
                role_name: roleName,
                role_lev: roleLevel
            };
            run('logCreateRole', data);
        },

        //进入游戏LOG
        logEnterGame: function (serverId, serverName, roleId, roleName, roleLevel) {
            var data = {
                server_id: serverId,
                server_name: serverName,
                role_id: roleId,
                role_name: roleName,
                role_lev: roleLevel
            };

            run('logEnterGame', data);
        },

        //角色升级LOG
        logRoleUpLevel: function (serverId, serverName, roleId, roleName, roleLevel) {
            var data = {
                server_id: serverId,
                server_name: serverName,
                role_id: roleId,
                role_name: roleName,
                role_lev: roleLevel
            };
            run('logRoleUpLevel', data);
        },

        //处理退出事件
        onLogout: function(callback){
            run('onLogout', '', callback);
        },

        getSdkPartnerId: function () {
            return run('getSdkPartnerId');
        },
        logLoadingFinish:function(callback){
            run('logLoadingFinish','',callback);
        },
        openIdCheck:function(){
            run('openIdCheck');
        },
        verify:function(){
            run('verify');
        },
        inputLeave:function () {
            run('inputLeave');
        },
        bindPhone :function (callback) {
            run('bindPhone', '', callback);
        },
        realName : function (callback) {
            run('realName', '', callback);
        },
        share : function(data,callback){
            run('share',data,callback);
        },
        follow : function (data,callback) {
            run('follow',data,callback);
        }

    };


    //官方的H5
    function h5Sdk() {
        var callbacks = {};
        window.addEventListener("message", function (event) {
            //console.log('收到数据：');
            //console.log(event.data);

            //alert('收到数据：' + JSON.stringify(event));
            var action = event && event.data && event.data.action ? event.data.action : false;
            var result = event && event.data && event.data.data ? event.data.data : {};

            if(!action){
                return false;
            }
            switch(action){
                case 'login':
                    if(result.state){
                        callbacks['login'] && callbacks['login'](0, result.data);
                    }else{
                        callbacks['login'] && callbacks['login'](1);
                    }
                    break;
                case 'logout':
                    // if(result.state){
                    //     callbacks['logout'] && callbacks['logout'](0);
                    // }else{
                    //     callbacks['logout'] && callbacks['logout'](1);
                    // }
                    callbacks['logout'] && callbacks['logout'](result);
                    break;
                case 'pay':
                    // if(result.state){
                    //     callbacks['pay'] && callbacks['pay'](0, result.data);
                    // }else{
                    //     callbacks['pay'] && callbacks['pay'](1);
                    // }
                    callbacks['pay'] && callbacks['pay'](result);
                    break;
                case 'on_logout':
                    callbacks['on_logout'] && callbacks['on_logout']();
                    break;
                case 'createRole':
                    callbacks['logCreateRole'] && callbacks['logCreateRole'](0,result);
                    break;
                case 'enter':
                    callbacks['logEnterGame'] && callbacks['logEnterGame'](0,result);
                    break;
                case 'loadingFinish':
                    callbacks['logLoadingFinish'] && callbacks['logLoadingFinish'](0,result);
                    break;
                case 'openIdCheck' :
                    callbacks['openIdCheck'] && callbacks['openIdCheck'](0);
                    break;
                case 'verify' :
                    callbacks['verify'] && callbacks['verify'](0);
                    break;
                case 'inputLeave':
                    callbacks['inputLeave'] && callbacks['inputLeave']();
                    break;
                case 'realName':
                    callbacks['realName'] && callbacks['realName'](result);
                    break;
                case 'bindPhone':
                    callbacks['bindPhone'] && callbacks['bindPhone'](result);
                    break;
                case 'share':
                    if(result != '1'){
                        result = 0;
                    }
                    callbacks['share'] && callbacks['share'](result);
                    break;
                case 'follow':
                    if(result != '1'){
                        result = 0;
                    }
                    callbacks['follow'] && callbacks['follow'](result);
                    break;
            }
        }, false);
        return {
            //调用登录／注册界面
            login: function (data, callback) {
                var h5_uid = DKMGetParameter('h5_uid');
                var h5_token = DKMGetParameter('h5_token');
                var h5_token_timeout = DKMGetParameter('h5_token_timeout');

                var is_token_timeout = false;
                if(h5_token_timeout && (new Date()).getTime() >=  h5_token_timeout * 1000){
                    is_token_timeout = true;
                }

                if(h5_uid && h5_token && !is_token_timeout){//SDK先登录
                    callback({data:{user_id: h5_uid, account: h5_uid, token: h5_token}, code: 0, message: ''});
                }else{
                    callbacks['login'] = typeof callback == 'function' ? callback : null;
                    this.postTopMessage('login');
                }
            },

            //用户登出
            logout: function(data, callback){
                callbacks['logout'] = typeof callback == 'function' ? callback : null;
                this.postTopMessage('logout');
            },

            //支付
            pay: function(data, callback){
                callbacks['pay'] = typeof callback == 'function' ? callback : null;
                this.postTopMessage('pay', data);
            },


            //角色创建LOG
            logCreateRole: function (data) {
                this.postTopMessage('createRole', data);
            },

            //进入游戏LOG
            logEnterGame: function (data) {
                this.postTopMessage('enter', data);
            },

            //角色升级LOG
            logRoleUpLevel: function (data) {
                this.postTopMessage('levelup', data);
            },
            //首次登录成功到创角页面
            logLoadingFinish:function(data){
                this.postTopMessage('loadingFinish',data);
            },
            //首次登录成功到创角页面
            openIdCheck:function(data){
                this.postTopMessage('openIdCheck',data);
            },
            verify:function (data) {
                this.postTopMessage('verify',data);
            },
            //处理退出事件
            onLogout: function(data, callback){
                callbacks['on_logout'] = typeof callback == 'function' ? callback : null;
            },

            getSdkPartnerId: function () {
                return DKMGetParameter('dkm_partner_id') || DKMGetParameter('pf');
            },

            share: function (data,callback) {
                callbacks['share'] = typeof callback == 'function' ? callback : null;
                this.postTopMessage('share', data);
            },

            follow: function (data,callback) {
                callbacks['follow'] = typeof callback == 'function' ? callback : null;
                this.postTopMessage('follow', data);
            },
            download: function (data) {
                this.postTopMessage('download', data);
            },
            inputLeave : function (data) {
                this.postTopMessage('inputLeave',data);
            },
            realName : function (data, callback) {
                callbacks['realName'] = typeof callback == 'function' ? callback : null;
                this.postTopMessage('realName',data);
            },
            bindPhone : function (data,callback) {
                callbacks['bindPhone'] = typeof callback == 'function' ? callback : null;
                this.postTopMessage('bindPhone',data);
            },
            postTopMessage: function(action, args) {
                args = args || {};
                var data = {action: action, data: args};
                //console.log('发送数据：');
                //console.log(data);
                //alert(JSON.stringify(data));
                //log(JSON.stringify(data));
                parent.postMessage(data, '*');
            }
        };
    }

    return dsSdk;
})(window, document);
