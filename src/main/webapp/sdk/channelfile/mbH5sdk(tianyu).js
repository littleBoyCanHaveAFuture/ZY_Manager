var manbah5sdk = function () {
    this.debug = false
};
window.addEventListener('message',function(e){
    if(e.data.hasOwnProperty('eventType')===true){
        var data = e.data.data || {};
        switch(e.data.eventType){
            case 'MbShaerCall':
                if("undefined" == typeof _manbah5sdk){
                    var _manbah5sdk = new manbah5sdk();
                }
                _manbah5sdk.MbShaerCall();
                break;
            case 'MbPayorder':
                if("undefined" == typeof _manbah5sdk){
                    var _manbah5sdk = new manbah5sdk();
                }
                _manbah5sdk.MbPayorder(data.result,data.string);
                break;
        }
    }
});
manbah5sdk.prototype = {
    MbShaerCall:function(){},
    MbPayorder:function(){},
    //========== 支付调用 ==========
    pay: function (object) {
        if (this.debug) {
            console.info(' >>CP调用曼巴SDK支付：', object)
        }
        var newData = {
            eventType: 'MbopenPay',
            data: object
        };
        parent.postMessage(newData,'*');
    },
    //========== 等级上报 ==========
    gradeReport: function (object) {
        if (this.debug) {
            console.info(' >> CP调用曼巴SDK等级上报：', object)
        }
        if (typeof object !== 'object'){
            return
        }
        var newData = {
            eventType: 'MbgradeReport',
            data: object
        };
        parent.postMessage(newData, '*');
    },
    //=========分享点击==========
    MbShaer:function(object){
        if (this.debug) {
            console.info(' >> CP调用曼巴分享点击：', object)
        }
        if (typeof object !== 'object'){
            return
        }
        var newData = {
            eventType: 'MbShare',
            data: object
        };
        parent.postMessage(newData, '*');
    },
    //========== 获取用户 ==========
    init: function () {
        if (this.debug) {
            console.info(' >> CP调用曼巴SDK获取用户')
        }
        var mbGameId = new RegExp("(^|&)mbGameId=([^&]*)(&|$)");
        var mbUserId = new RegExp("(^|&)mbUserId=([^&]*)(&|$)");
        var mbToken = new RegExp("(^|&)mbToken=([^&]*)(&|$)");
        try {
            var uid = window.location.search.substr(1).match(mbGameId);
            var sid = window.location.search.substr(1).match(mbUserId);
            var tid = window.location.search.substr(1).match(mbToken);
            if (uid !== null && sid !== null) {
                return {
                    'mbGameId': uid[2],
                    'mbUserId': sid[2],
                    'mbToken': tid[2]
                }
            }
        } catch (e) {
            console.info(' >> 捕获到曼巴SDK获取用户错误信息：', e)
        }
        return null
    },
    //========== 登录 ==========
    login: function () {
        if (this.debug) {
            console.info(' >> CP调用曼巴SDK登录')
        }
        var data = {
            eventType: 'Mblogin'
        };
        parent.postMessage(data, '*');
    },
    //========== 注销 ==========
    logout: function () {
        if (this.debug) {
            console.info(' >> CP调用曼巴SDK注销')
        }
        var data = {
            eventType: 'Mblogout'
        };
        parent.postMessage(data, '*');
    }
};