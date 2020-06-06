/**
 * 厦门舜邦网络发行sdk
 * @author zhoushen
 * @since 2016/09/09
 */

(function($, window){
    SbPulSdk = function(option){

    }

    /**
     * version history
     * 1.0 初始版本
     * 2.0 添加初始化函数回调
     * @type {String}
     */
    SbPulSdk.version       = '2.0';
    SbPulSdk.sourceRoot    = '//pulsdk.7724.com/channelsdk/';
    SbPulSdk.payApi        = '//pulsdk.7724.com/sdk/cppay';
    SbPulSdk.createRoleApi = '//pulsdk.7724.com/sdk/createRole';
    SbPulSdk.loginRoleApi  = '//pulsdk.7724.com/sdk/loginRole';
    SbPulSdk.canPay        = 1; //1可以支付 2不可以支付

    /**
     * sdk初始话，登录回调后，cp调用
     * @param  json loginParams 登录回调参数
     * @param  func channelLoadedCall 渠道js加载完毕初始化回调
     * @param  func LoadedCall 初始化回调
     */
    SbPulSdk.init = function(loginParams, channelLoadedCallBack, loadedCallBack){
        
        //载入渠道业务逻辑
        var js = SbPulSdk.sourceRoot + 'channel' + loginParams.channelid + 'pay.js?v=20191017';

        SbPulSdk.loadJsSync( js, function(){ 
            SbPulSdk.debug('渠道js加载完毕' + SbPulSdkChannel);
            //渠道sdk初始化
            SbPulSdkChannel.init(loginParams, channelLoadedCallBack); 
            //初始化回调
            if(loadedCallBack){
                loadedCallBack();
            }
            
        } );
    }

    /**
     * cp发起支付
     * @param  json cpPayParams cp支付参数
     */
    SbPulSdk.pay  = function(cpPayParams){ 

        if( SbPulSdk.canPay == 2 ){
            SbPulSdk.debug(cpPayParams, '支付锁定，请等待上一次支付发起结束');
            return false;
        } 

        SbPulSdk.canPay = 2;

        SbPulSdk.debug(cpPayParams, 'cp请求支付参数');

        //TODO:增加角色信息
        //{'roleName':'屠龙','serverId':'游戏区服id','level':'角色等级','ext':'其他透传信息'}

        //获取支付参数
        $.ajax(  
            {  
                type:'get',  
                url : SbPulSdk.payApi,  
                data: cpPayParams,
                dataType : 'jsonp',  
                jsonp:"jsoncallback",  

                complete : function(){ 
                    SbPulSdk.canPay = 1;
                },

                success : function(respon) {  

                    SbPulSdk.debug(respon, 'sdk返回请求支付参数');
                    if(respon.code  == -1){
                        alert(respon.msg);
                        return false;
                    }
                    SbPulSdkChannel.pay(respon.payParams);
                },  
                error : function() {  
                    alert('发起渠道支付错误');
                }  
            }  
        );   
    }

    /**
     * 创建角色接口
     * @param  json roleInfo 角色json信息 
     * 数据格式:
     * {'roleName':'屠龙','serverId':'游戏区服id','level':'角色等级','ext':'其他透传信息'}
     */
    SbPulSdk.createRole = function(roleInfo){
        SbPulSdk.debug(roleInfo, 'cp请求创建角色接口');
        //获取支付参数 
        $.ajax(  
            {  
                type:'get',  
                url : SbPulSdk.createRoleApi,  
                data: roleInfo,
                dataType : 'jsonp',  
                jsonp:"jsoncallback",  
                success : function(respon) {  
                    if(respon.code  == -1){
                        console.log(respon.msg);
                        return false;
                    }

                    //TODO：
                    if(SbPulSdkChannel.createRole != undefined){
                        SbPulSdkChannel.createRole(respon.roleParams);
                    }
                },  
                error : function() {
                    console.log('上送角色信息失败');
                }  
            }  
        );   
    }

    /**
     * 上报登入角色信息接口
     * @param  json roleData 角色json信息
     * 数据格式:
     * {'rolename':'屠龙','serverid':'游戏区服id','level':'角色等级','ext':'其他透传信息'}
     */
    SbPulSdk.loginRole = function(roleData){
        SbPulSdk.debug(roleData, 'cp请求上报登入角色信息接口');
        //获取支付参数
        $.ajax(
            {
                type:'get',
                url : SbPulSdk.loginRoleApi,
                data: roleData,
                dataType : 'jsonp',
                jsonp:"jsoncallback",
                success : function(respon) {
                    if(respon.code  == -1){
                        console.log(respon.msg);
                        return false;
                    }

                    //TODO：
                    if(SbPulSdkChannel.loginRole != undefined){
                        SbPulSdkChannel.loginRole(respon.roleParams);
                    }
                },
                error : function() {
                    console.log('上报登入角色信息失败');
                }
            }
        );
    }
	
	//渠道是否开启分享
	SbPulSdk.isCanShareble = function(){
		if(SbPulSdkChannel.isCanShareble == true)
		{
			return true;
		}
		else
		{
			return false;
		}
	};
	//通用化分享初始化，cp回调方法和cp自定义参数
	SbPulSdk.shareConfig = function(shareCallback,cpCustomerParams){
		if(SbPulSdkChannel.shareConfig != undefined)
		{
			SbPulSdkChannel.shareConfig(shareCallback,cpCustomerParams);
		}

	};
	//通用化分享玩家点击分享按钮
	SbPulSdk.share = function (){
		if(SbPulSdkChannel.share != undefined)
		{
			SbPulSdkChannel.share();
		}
	};

    //渠道是否接入广告
    SbPulSdk.advertOpen = function () {
        if (SbPulSdkChannel.advertOpen == true) {
            return true;
        } else {
            return false;
        }
    }

    //广告回调方法用于cp给用户发放奖励
    SbPulSdk.advertCallback = function(advertCallback) {
        if(SbPulSdkChannel.advertCallback != undefined) {
            SbPulSdkChannel.advertCallback(advertCallback);
        }
    }

    //字符串转json
    SbPulSdk.parseJson = function(string){
        return $.parseJSON(string);
    }


    //调试
    SbPulSdk.debug = function(params, msg){
        // return true;
        msg && console.log(msg);
        params && console.log(params);
    };

    //同步加载js
    SbPulSdk.loadJsSync = function loadJS(url, success) {
        var domScript = document.createElement('script');
        domScript.src = url;
        success = success || function () {};
        domScript.onload = domScript.onreadystatechange = function () {
            if (!this.readyState || 'loaded' === this.readyState || 'complete' === this.readyState) {
                success();
                this.onload = this.onreadystatechange = null;
                this.parentNode.removeChild(this);
            }
        }
        document.getElementsByTagName('head')[0].appendChild(domScript);
    }

    window.SbPulSdk = SbPulSdk;

})(jQuery, window);