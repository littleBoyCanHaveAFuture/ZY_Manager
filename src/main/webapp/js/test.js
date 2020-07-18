~function (window, undefined) {

    if (window.IO) return;

    var IO = {}
    var toString = Object.prototype.toString

    // Iterator
    function forEach(obj, iterator, context) {
        if (!obj) return
        if (obj.length && obj.length === +obj.length) {
            for (var i = 0; i < obj.length; i++) {
                if (iterator.call(context, obj[i], i, obj) === true) return
            }
        } else {
            for (var k in obj) {
                if (iterator.call(context, obj[k], k, obj) === true) return
            }
        }
    }

    // IO.isArray, IO.isBoolean, ...
    forEach(['Array', 'Boolean', 'Function', 'Object', 'String', 'Number'], function (name) {
        IO['is' + name] = function (obj) {
            return toString.call(obj) === '[object ' + name + ']'
        }
    })

    // Object to queryString
    function serialize(obj) {
        var a = []
        forEach(obj, function (val, key) {
            if (IO.isArray(val)) {
                forEach(val, function (v, i) {
                    a.push(key + '=' + encodeURIComponent(v))
                })
            } else {
                a.push(key + '=' + encodeURIComponent(val))
            }
        })
        return a.join('&')
    }

    // Parse json string
    function parseJSON(str) {
        try {
            return JSON.parse(str)
        } catch (e) {
            try {
                return (new Function('return ' + str))()
            } catch (e) {
            }
        }
    }

    // Empty function
    function noop() {
    }


    /**
     *  Ajax API
     *     IO.ajax, IO.get, IO.post, IO.text, IO.json, IO.xml
     *
     */
    ~function (IO) {

        var createXHR = window.XMLHttpRequest ?
            function () {
                return new XMLHttpRequest()
            } :
            function () {
                return new window.ActiveXObject('Microsoft.XMLHTTP')
            }

        function ajax(url, options) {
            if (IO.isObject(url)) {
                options = url
                url = options.url
            }
            var xhr, isTimeout, timer, options = options || {}
            var async = options.async !== false,
                method = options.method || 'GET',
                type = options.type || 'text',
                encode = options.encode || 'UTF-8',
                timeout = options.timeout || 0,
                credential = options.credential,
                data = options.data,
                scope = options.scope,
                success = options.success || noop,
                failure = options.failure || noop

            // 大小写都行，但大写是匹配HTTP协议习惯
            method = method.toUpperCase()

            // 对象转换成字符串键值对
            if (IO.isObject(data)) {
                data = serialize(data)
            }
            if (method === 'GET' && data) {
                url += (url.indexOf('?') === -1 ? '?' : '&') + data
            }

            xhr = createXHR()
            if (!xhr) {
                return
            }

            isTimeout = false
            if (async && timeout > 0) {
                timer = setTimeout(function () {
                    // 先给isTimeout赋值，不能先调用abort
                    isTimeout = true
                    xhr.abort()
                }, timeout)
            }
            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    if (isTimeout) {
                        failure(xhr, 'request timeout')
                    } else {
                        onStateChange(xhr, type, success, failure, scope)
                        clearTimeout(timer)
                    }
                }
            }
            xhr.open(method, url, async)
            if (credential) {
                xhr.withCredentials = true
            }
            if (method == 'POST') {
                xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded;charset=' + encode)
            }
            xhr.send(data)
            return xhr
        }

        function onStateChange(xhr, type, success, failure, scope) {
            var s = xhr.status, result
            if (s >= 200 && s < 300) {
                switch (type) {
                    case 'text':
                        result = xhr.responseText
                        break
                    case 'json':
                        result = parseJSON(xhr.responseText)
                        break
                    case 'xml':
                        result = xhr.responseXML
                        break
                }
                // text, 返回空字符时执行success
                // json, 返回空对象{}时执行suceess，但解析json失败，函数没有返回值时默认返回undefined
                result !== undefined && success.call(scope, result, s, xhr)

            } else {
                failure(xhr, xhr.status)
            }
            xhr = null
        }

        // exports to IO
        var api = {
            method: ['get', 'post'],
            type: ['text', 'json', 'xml'],
            async: ['sync', 'async']
        }

        // Low-level Interface: IO.ajax
        IO.ajax = ajax

        // Shorthand Methods: IO.get, IO.post, IO.text, IO.json, IO.xml
        forEach(api, function (val, key) {
            forEach(val, function (item, index) {
                IO[item] = function (key, item) {
                    return function (url, opt, success) {
                        if (IO.isObject(url)) {
                            opt = url
                        }
                        if (IO.isFunction(opt)) {
                            opt = {success: opt}
                        }
                        if (IO.isFunction(success)) {
                            opt = {data: opt}
                            opt.success = success
                        }
                        if (key === 'async') {
                            item = item === 'async' ? true : false
                        }
                        opt = opt || {}
                        opt[key] = item
                        return ajax(url, opt)
                    }
                }(key, item)
            })
        })

    }(IO)

    /**
     *  JSONP API
     *  IO.jsonp
     *
     */
    ~function (IO) {

        var ie678 = !-[1,]
        var win = window
        var opera = win.opera
        var doc = win.document
        var head = doc.head || doc.getElementsByTagName('head')[0]
        var timeout = 3000
        var done = false

        // Thanks to Kevin Hakanson
        // http://stackoverflow.com/questions/105034/how-to-create-a-guid-uuid-in-javascript/873856#873856
        function generateRandomName() {
            var uuid = ''
            var s = []
            var i = 0
            var hexDigits = '0123456789ABCDEF'
            for (i = 0; i < 32; i++) {
                s[i] = hexDigits.substr(Math.floor(Math.random() * 0x10), 1)
            }
            // bits 12-15 of the time_hi_and_version field to 0010
            s[12] = '4'
            // bits 6-7 of the clock_seq_hi_and_reserved to 01
            s[16] = hexDigits.substr((s[16] & 0x3) | 0x8, 1)
            uuid = 'jsonp_' + s.join('')
            return uuid
        }

        function jsonp(url, options) {
            if (IO.isObject(url)) {
                options = url;
                url = options.url;
            }
            var options = options || {}
            var me = this
            var url = url.indexOf('?') === -1 ? (url + '?') : (url + '&')
            var data = options.data
            var charset = options.charset
            var success = options.success || noop
            var failure = options.failure || noop
            var scope = options.scope || win
            var timestamp = options.timestamp
            var jsonpName = options.jsonpName || 'callback'
            var callbackName = options.jsonpCallback || generateRandomName()

            if (IO.isObject(data)) {
                data = serialize(data)
            }
            var script = doc.createElement('script')

            function callback(isSucc) {
                if (isSucc) {
                    done = true
                } else {
                    failure.call(scope)
                }
                // Handle memory leak in IE
                script.onload = script.onerror = script.onreadystatechange = null
                if (head && script.parentNode) {
                    head.removeChild(script)
                    script = null
                    win[callbackName] = undefined
                }
            }

            function fixOnerror() {
                setTimeout(function () {
                    if (!done) {
                        callback()
                    }
                }, timeout)
            }

            if (ie678) {
                script.onreadystatechange = function () {
                    var readyState = this.readyState
                    if (!done && (readyState == 'loaded' || readyState == 'complete')) {
                        callback(true)
                    }
                };

            } else {
                script.onload = function () {
                    callback(true)
                }
                script.onerror = function () {
                    callback()
                }
                if (opera) {
                    fixOnerror()
                }
            }

            url += jsonpName + '=' + callbackName

            if (charset) {
                script.charset = charset
            }
            if (data) {
                url += '&' + data
            }
            if (timestamp) {
                url += '&ts='
                url += (new Date).getTime()
            }

            win[callbackName] = function (json) {
                success.call(scope, json)
            };

            script.src = url
            head.insertBefore(script, head.firstChild)
        }

        // exports to IO
        IO.jsonp = function (url, opt, success) {

            if (IO.isObject(url)) {
                opt = url
            }
            if (IO.isFunction(opt)) {
                opt = {success: opt}
            }
            if (IO.isFunction(success)) {
                opt = {data: opt}
                opt.success = success
            }

            return jsonp(url, opt)
        }

    }(IO)


    // Expose IO to the global object or as AMD module
    if (typeof define === 'function' && define.amd) {
        define('IO', [], function () {
            return IO
        })
    } else {
        window.IO = IO
    }
}(this);


if (!window.localStorage) {

    window.localStorage = {
        getItem: function (sKey) {
            if (!sKey || !this.hasOwnProperty(sKey)) {
                return null;
            }
            return unescape(document.cookie.replace(new RegExp("(?:^|.*;\\s*)" + escape(sKey).replace(/[\-\.\+\*]/g, "\\$&") + "\\s*\\=\\s*((?:[^;](?!;))*[^;]?).*"), "$1"));
        },
        key: function (nKeyId) {
            return unescape(document.cookie.replace(/\s*\=(?:.(?!;))*$/, "").split(/\s*\=(?:[^;](?!;))*[^;]?;\s*/)[nKeyId]);
        },
        setItem: function (sKey, sValue) {
            if (!sKey) {
                return;
            }
            document.cookie = escape(sKey) + "=" + escape(sValue) + "; expires=Tue, 19 Jan 2038 03:14:07 GMT; path=/";
            this.length = document.cookie.match(/\=/g).length;
        },
        length: 0,
        removeItem: function (sKey) {
            if (!sKey || !this.hasOwnProperty(sKey)) {
                return;
            }
            document.cookie = escape(sKey) + "=; expires=Thu, 01 Jan 1970 00:00:00 GMT; path=/";
            this.length--;
        },
        hasOwnProperty: function (sKey) {
            return (new RegExp("(?:^|;\\s*)" + escape(sKey).replace(/[\-\.\+\*]/g, "\\$&") + "\\s*\\=")).test(document.cookie);
        }
    };
    window.localStorage.length = (document.cookie.match(/\=/g) || window.localStorage).length;
}

var doc = document,

    h5_game_url_out = '',//点所有的开始游戏的链接

    ajaxurl = '//www.3tang.com/h/',
    baseurl = '//www.3tang.com/',
    isSW = false,
    isHoliday = false,
    isWordDay = false,
    user_timer = '',
    domain = window.location.host,
    tangcoin = 0,
    btangcoin = 0,
    gameenter_time = '',//玩家进入游戏时间
    jrdate_2020 = '2020-01-01|2020-01-24|2020-01-25|2020-01-26|2020-01-27|2020-01-28|2020-01-29|2020-01-30|2020-04-04|2020-04-05|2020-04-06|2020-05-01|2020-05-02|2020-05-03|2020-05-04|2020-05-05|2020-06-25|2020-06-26|2020-06-27|2020-10-01|2020-10-02|2020-10-03|2020-10-04|2020-10-05|2020-10-06|2020-10-07|2020-10-08',
    //ajaxurl = domain == 'www.3tang.com' ? ajaxurl : '//'+domain+'/',
    userimgsrc = '',
    isIOS = !!navigator.userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/),
    query = {


        $: function (selector, parent) {

            return (parent || document).querySelector(selector);
        },

        $$: function (selector, parent) {

            return (parent || document).querySelectorAll(selector);

        },

        addClass: function (node, classname) {

            if (node.classList) {

                node.classList.add(classname);

                return;

            }

            //node.className += ' ' + classname;

            if (node.className.indexOf(classname) == -1) node.className += ' ' + classname;

        },
        removeClass: function (node, classname) {

            if (node.classList) {

                node.classList.remove(classname);

                return;

            }

            var reg = eval("/" + classname + "/ig");

            node.className = node.className.replace(reg, '');

            //node.className = node.className.replace(classname,'');

        },

        toArray: function (arr) {


            var reduced = [];

            try {

                reduced = Array.prototype.slice.call(arr, 0);

            } catch (ex) {

                for (var i = 0, len = arr.length; i < len; i++) {

                    reduced[i] = arr[i];

                }
            }

            return reduced;
        },

        getTarget: function (event) {

            var e = query.getEvent(event);

            return e.target || e.srcElement;

        }
    },

    judgeBrowser = function () {//判断浏览器版本是否低于ie8

        if (!doc.body.addEventListener) alert('浏览器版本过低，请使用高级浏览器浏览本页');
    }(),

    getCookie = function (name) {

        var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");

        if (arr = document.cookie.match(reg))

            return unescape(arr[2]);

        else

            return null;
    },

    setCookie = function (name, value) {
        var Days = 30;
        var exp = new Date();
        exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
        document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
    },

    // reloadFun = function(){

    // 	if( IsPC() ){

    // 		if(!h5_game_url_out){

    // 			parent.location.reload(false);

    // 			return;

    // 		}

    // 		parent.location.href = h5_game_url_out;

    // 		return;


    // 	}

    // 	if(!h5_game_url_out){

    // 		window.location.reload(false);

    // 		return;

    // 	}

    // 	window.location.href = h5_game_url_out;

    // },

    popshow = function () {//错误弹框

        var popmask = doc.createElement('div'),

            popDisable = function (event) {

                var target = event.target;

                if (target.nodeName.toLowerCase() == 'i' || target.nodeName.toLowerCase() == 'span') {

                    popmask.innerHTML = '';

                    popmask.style.display = 'none';

                }

            },

            show = function (data) {

                popmask.style.display = 'block';

                popmask.innerHTML = '<div class="h5_pop" style="display:block"><i></i><h2>提示</h2><p>' + data + '</p><span>确定</span></div>';

            },

            creatPop = function () {

                popmask.className = 'h5_popmask';

                doc.body.appendChild(popmask);

            }();


        popmask.addEventListener('click', popDisable, false);


        return {

            show: show

        };

    }(),

    messageShow = function () {

        var ele = doc.createElement('div'),


            show = function (str) {

                ele.innerHTML = str;

                ele.style.display = 'block';

                ele.style.marginTop = -ele.offsetHeight / 2 + 'px';

                ele.style.marginLeft = -ele.offsetWidth / 2 + 'px';

                setTimeout(function () {

                    ele.style.display = 'none';

                }, 3000);


            },


            creatPop = function () {

                ele.className = 'wrongmessage';

                doc.body.appendChild(ele);

            }();


        return {

            show: show

        };

    }(),

    screenFun = function () {//屏幕自适应

        var htmlElement = doc.documentElement,

            screenFun = function () {

                var w = htmlElement.clientWidth,

                    h = htmlElement.clientHeight;

                if (h <= 320) {

                    h = 375;

                } else if (h >= 640) {

                    h = 640;

                }

                if (w <= 320) {

                    w = 375;

                } else if (w >= 640) {

                    w = 640;

                }

                if (w >= h) {//判断横竖屏
                    //横屏
                    htmlElement.style.fontSize = Math.floor(h * 10000 / 75) / 1000 * .85 + "px";

                } else {
                    //竖屏
                    htmlElement.style.fontSize = Math.floor(w * 10000 / 75) / 1000 + "px";

                }

            };

        screenFun();

        window.addEventListener('resize', screenFun, false);

    }(),

    posObj = function () {//获取地址栏地址

        var str = location.search,

            obj = {},

            ary = [];

        if (!str) return obj;

        ary = str.substr(1).split('&');

        ary.forEach(function (item) {

            var subary = item.split('=');

            obj[subary[0]] = subary[1];
        });

        return obj;
    }();


//是否有游戏信息
if (typeof pos == 'undefined') {

    var pos = 0;
}
if (typeof game_id == 'undefined') {

    var game_id = 0;
}
if (typeof invite_type == 'undefined') {

    var invite_type = 0;
}
if (typeof invite_by == 'undefined') {

    var invite_by = 0;
}
if (typeof invite_level == 'undefined') {

    var invite_level = 0;
}


var userData = {},

    h5_login_popmask = query.$('#h5-pop'),

    h5_wap = query.$('.h5_wap'),

    IsPC = function () {//判断是否是pc端

        var userAgentInfo = navigator.userAgent;

        var Agents = ["Android", "iPhone", "SymbianOS", "Windows Phone", "iPad", "iPod"];

        var flag = true;

        for (var v = 0; v < Agents.length; v++) {

            if (userAgentInfo.indexOf(Agents[v]) > 0) {

                flag = false;

                break;
            }
        }


        if (userAgentInfo.toLowerCase().match(/MicroMessenger/i) == 'micromessenger') {
            flag = false;
        }


        return flag;
    },


    isWeiXin = function () {//判断是否是微信

        var ua = window.navigator.userAgent.toLowerCase();

        if (ua.match(/MicroMessenger/i) == 'micromessenger') {

            return true;

        }

        return false;

    },

    isQQ = function () {

        if (navigator.userAgent.toLowerCase().indexOf("qq") > -1) {

            return true;

        }

        return false;
    },
    time_range = function (beginTime, endTime) {
        var myDate = new Date()
        // if(myDate.getDay()==0||myDate.getDay()==6){
        // 	return false;
        // }
        var strb = beginTime.split(":");
        if (strb.length != 2) {
            return false;
        }

        var stre = endTime.split(":");
        if (stre.length != 2) {
            return false;
        }

        var b = new Date();
        var e = new Date();
        var n = new Date();

        b.setHours(strb[0]);
        b.setMinutes(strb[1]);
        e.setHours(stre[0]);
        e.setMinutes(stre[1]);

        if (n.getTime() - b.getTime() > 0 && n.getTime() - e.getTime() < 0) {
            return true;
        } else {
            //不在该时间范围内
            return false;
        }
    },
    isMobile = function () {
        var sUserAgent = navigator.userAgent.toLowerCase(),
            bIsIpad = sUserAgent.match(/ipad/i) == "ipad",
            bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os",
            bIsMidp = sUserAgent.match(/midp/i) == "midp",
            bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4",
            bIsUc = sUserAgent.match(/ucweb/i) == "ucweb",
            bIsAndroid = sUserAgent.match(/android/i) == "android",
            bIsCE = sUserAgent.match(/windows ce/i) == "windows ce",
            bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";


        if (bIsIpad || bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM) {

            return true;

        } else {
            return false;
        }


    },
    clockon = function () {
        var thistime = new Date();
        //时间差
        var diff = new Date();
        diff.setTime(Math.abs(thistime.getTime() - gameenter_time.getTime()));
        timediff = diff.getTime();

        if (isHoliday && (timediff > 3600 * 1000 * 3)) {
            login.showRule("根据相关法规未成年节假日游戏时间不得超过3个小时,请下线!");
            clearTimeout(user_timer);
            return;
        }
        if (isWordDay && (timediff > 60 * 1000 * 90)) {
            login.showRule("根据相关法规未成年平日游戏时间不得超过90分钟,请下线!");
            clearTimeout(user_timer);
            return;
        }
        if (isSW && (timediff > 60 * 1000 * 60)) {

            login.showRule('根据相关法规游客体验模式不得超过1小时,请激活账号');
            login.testsign();//试玩激活
            clearTimeout(user_timer);
            return;
        }
        user_timer = setTimeout("clockon()", 200);
    },
    islogin = function (callback) {//判定用户的登录态
        IO.jsonp(ajaxurl + 'account1.asp?action=info', function (data) {

            if (data.code == 200) {

                userData = data.data;//放入用户信息

                userData.loginState = true;
                //玩家年龄
                user_age = parseInt(userData.user_age);

                if (!getCookie('gameenter_time' + userData.uid)) {
                    gameenter_time = new Date();//进入游戏时间
                    setCookie('gameenter_time' + userData.uid, gameenter_time);
                } else {
                    gameenter_time = getCookie('gameenter_time' + userData.uid);
                    gameenter_time = new Date(gameenter_time);
                    var temp_logintime = new Date();//进入游戏时间
                    var lastlogin_time = gameenter_time.getFullYear() + '-' + gameenter_time.getMonth() + '-' + gameenter_time.getDate();
                    var thislogin_time = temp_logintime.getFullYear() + '-' + temp_logintime.getMonth() + '-' + temp_logintime.getDate();
                    if (lastlogin_time != thislogin_time) {//判断是否同一天
                        gameenter_time = temp_logintime;
                        setCookie('gameenter_time' + userData.uid, temp_logintime);
                    }
                }
                var login_year = gameenter_time.getFullYear();
                var login_month = gameenter_time.getMonth();
                var login_date = gameenter_time.getDate();
                var login_day = gameenter_time.getDay();

                if (eval(login_month) < 10) {
                    login_month = "0" + login_month;
                }
                if (eval(login_date) < 10) {
                    login_date = "0" + login_date;
                }
                var login_time = login_year + '-' + login_month + '-' + login_date


                //实名认证,未经实名认证不得进入游戏
                if (!userData.id_card) {
                    if (userData.username.indexOf('@st') > -1) {//试玩
                        clockon();
                    } else if (userData.username.indexOf('@xh') > -1) {
                    } else {
                        login.verifyLogin();
                    }
                } else {
                    if (user_age < 18) {
                        //未成年人的游戏时长平日限制在90分钟，节假日限制在3个小时内
                        if (time_range("00:00", "08:00") || time_range("22:00", "23:59")) {
                            login.showRule('根据相关法规每日22：00~8：00不得为未成年人提供游戏服务！');
                        } else if (jrdate_2020.indexOf(login_time) > -1 || login_day == 0 || login_day == 6) {//节假日
                            //限制3个小时
                            isHoliday = true;
                            clockon();
                        } else {//平日
                            //限制90分钟
                            isWordDay = true;
                            clockon();
                        }
                    }
                }


            } else {

                userData.loginState = false;
            }

            if (typeof callback == 'function') callback(data);


        });

    },
    login = function () {//如果用户未登录

        var popMiddle = function () {//弹框居中

                h5_login_popmask.style.display = 'block';

                var ele = h5_login_popmask.firstElementChild;

                ele.style.marginTop = -ele.offsetHeight / 2 + 'px';


            },
            gameLogin = function () {//游戏登录

                var str = '<a class="loginmod_qq" href="' + baseurl + 'redirectToExtLogin.asp?extlogin_type=QQ&pos=' + pos + '&game_id=' + game_id + '&invite_type=' + invite_type + '&invite_by=' + invite_by + '&invite_level=' + invite_level + '">QQ登录</a>',

                    testStr = '<span class="loginmod_test">试玩</span>';


                if (IsPC()) {//判断是否是pc端

                    str = '<span class="loginmod_qq pc_qq">QQ登录</span><span class="loginmod_weixin pc_weixin">微信</span>';

                }

                if (isWeiXin()) {//判断是否是微信

                    str = '<a class="loginmod_weixin" href="' + baseurl + 'redirectToExtLogin.asp?extlogin_type=WEIXIN&pos=' + pos + '&game_id=' + game_id + '&invite_type=' + invite_type + '&invite_by=' + invite_by + '&invite_level=' + invite_level + '">微信</a>';

                }

//					 if(typeof miniClient != 'undefined'){ //miniClient 隐藏qq
//					 	str = '';
//					 }

                if (isWeiXin()) testStr = '';


                h5_login_popmask.innerHTML =

                    ['<div class="h5_login loginmod">',
                        '	<i class="lp_clo"></i>',
                        '	<div class="loginmod_tit">游戏登录</div>',
                        '	<a class="loginmod_mobile">手机</a>',
                        str,
                        testStr,
                        '<div class="loginmod">',
                        '	<i class="loginmod_singn_3tang">帐号注册</i>',
                        '	<i class="loginmod_3tang">帐号登录</i>',
                        '</div>',
                        '</div>'].join('');


                popMiddle();//弹出框居中

            },


            santangAccount = function (that) {//检验用户登录帐号
                var txt = that.value;
                txt = txt.replace(/(^\s*)|(\s*$)/g, '');
                if (!txt) {

                    messageShow.show('请填写用户名');

                    return true;
                }

                if (txt.length < 4 || txt.length > 15) {

                    messageShow.show('用户名有误');

                    return true;
                }

                return false;

            },
            XHNumber = function (that) {//检验小号数量


                var txt = that.value;
                var reg = /^[0-9]+$/;

                if (!reg.test(txt)) {

                    messageShow.show('不超过30的整数');

                    return true;
                }
                return false;

            },
            santangRealName = function (that) {//检验用户登录帐号
                var txt = that.value;
                txt = txt.replace(/(^\s*)|(\s*$)/g, '');
                if (!txt) {
                    messageShow.show('姓名不能为空');
                    return true;
                }
                var reg = /^[\u4e00-\u9fa5]{2,5}$/;
                if (!reg.test(txt)) {
                    messageShow.show('请输入真实姓名');
                    return true;
                }

                return false;

            },
            santangCardID = function (that) {//检验用户登录帐号
                var txt = that.value;
                txt = txt.replace(/(^\s*)|(\s*$)/g, '');
                if (!txt) {

                    messageShow.show('请填写本人身份证');

                    return true;
                }

                var reg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/;
                if (!reg.test(txt)) {
                    messageShow.show('请输入真实身份证号码');
                    return true;
                }

                return false;

            },
            santangPassword = function (that, str) {//检验用户密码

                var txt = that.value;

                txt = txt.replace(/(^\s*)|(\s*$)/g, '');

                str = str || '';

                if (!txt) {

                    messageShow.show('请填写' + str + '密码');

                    return true;

                }

                if (txt.length < 6 || txt.length > 20) {

                    messageShow.show('请填写正确的' + str + '密码');

                    return true;
                }


                return false;
            },

            phonetest = function (that) {//手机登录验证手机号码

                var txt = that.value;

                if (txt == '') {

                    messageShow.show('手机号码不能为空');

                    return true;
                }

                if (!(/^1[3456789]\d{9}$/.test(txt))) {

                    messageShow.show('请输入正确格式的手机号码');

                    return true;

                }
                return false;

            },

            santanglogin = function () {//三唐帐号登录

                var str = '<a class="lpl_qq" href ="' + baseurl + 'redirectToExtLogin.asp?extlogin_type=QQ&pos=' + pos + '&game_id=' + game_id + '&invite_type=' + invite_type + '&invite_by=' + invite_by + '&invite_level=' + invite_level + '">QQ登录</a>',
                    faststr = '',
                    testStr = '';


                if (IsPC()) {

                    str = '<span class="lpl_qq pc_qq">QQ登录</span><span class="lpl_weixin pc_weixin">微信</span>';

                }

                if (isWeiXin()) {

                    str = '<a class="lpl_weixin" href="' + baseurl + 'redirectToExtLogin.asp?extlogin_type=WEIXIN&pos=' + pos + '&game_id=' + game_id + '&invite_type=' + invite_type + '&invite_by=' + invite_by + '&invite_level=' + invite_level + '">微信</a>';

                }

//					 if(typeof miniClient != 'undefined'){ //miniClient 隐藏qq
//						str = '';
//					 }

                if (!userData.nickname) {

                    testStr = '<span class="lpl_test">试玩</span>';
                }


                if (isWeiXin()) testStr = '';

                if (isSW) {
                    faststr = '<a class="try_joinfast">立即激活</a>';
                } else {
                    faststr = '<a class="lact_joinfast">快速注册</a>';
                }

                h5_login_popmask.innerHTML =
                    ['<div class="login3tangaccountnumber loginmod">',
                        '	<i class="lp_clo"></i>',
                        '	<div class="lp_tit">帐号登录</div>',
                        '	<input class="lact_accountnumber" type="text" name="" placeholder="帐号">',
                        '	<input class="lact_password" type="password" name="" placeholder="密码">',
                        '	<span class="lact_login">立即登录</span>',
                        '	<div class="lact_fj"><a class="lact_forgetpassword">忘记密码?</a>',
                        faststr,
                        '</div><p class="lp_otherlogin">其他方式登录</p>',
                        '	<div class="lp_loginmod">',
                        '		<span class="loginmod_mobile">手机</span>',
                        str,
                        testStr,
                        '	</div>',
                        '</div>'].join('');

                popMiddle();//弹出框居中

                if (!IsPC()) return;
                doc.addEventListener('keydown', keydownFun, false);
            },
            verifyLogin = function () {//身份证实名认证

                h5_login_popmask.innerHTML = ['<div class="register loginmod">',
                    //'	<i class="lp_clo"></i>',
                    '	<div class="lp_tit">根据相关法规须实名认证</div>',
                    '	<input class="verify_realname" type="" name="" placeholder="本人姓名">',
                    '	<input class="verify_cardID" type="" name="" placeholder="本人身份证">',
                    '	<span class="verify_login">立即认证</span>',
                    '</div>'].join('');

                popMiddle();//弹出框居中

                if (!IsPC()) return;
                doc.addEventListener('keydown', keydownFun, false);
            },
            showRule = function (that) {//系统提示

                h5_login_popmask.innerHTML = ['<div class="register loginmod">',
                    //'	<i class="lp_clo"></i>',
                    '	<div class="lp_tit"><b>系统提示</b></div>',
                    '	<div class="lp_tit">' + that + '</div>',
                    '	<span class="exit_btn">退出游戏</span>',
                    '</div>'].join('');

                popMiddle();//弹出框居中

                if (!IsPC()) return;
                doc.addEventListener('keydown', keydownFun, false);
            },
            fastlogin = function () {//三唐帐号快速注册

                h5_login_popmask.innerHTML = ['<div class="register loginmod">',
                    '	<i class="lp_clo"></i>',
                    '	<div class="lp_tit">帐号快速注册</div>',
                    '	<input class="fast_account" type="" name="" placeholder="4-15字符（仅限数字，英文）">',
                    '	<input class="fast_password" type="password" name="" placeholder="6-20位密码">',
                    '	<input class="fastrepeat_password" type="password" name="" placeholder="确认密码">',
                    '	<input class="fast_realname" type="" name="" placeholder="本人姓名">',
                    '	<input class="fast_cardID" type="" name="" placeholder="本人身份证">',
                    '	<div class="register_fj"><a class="fast_joinfast">快速登录</a></div>',
                    '	<span class="fast_login">立即注册</span>',
                    '</div>'].join('');

                popMiddle();//弹出框居中


                if (!IsPC()) return;
                doc.addEventListener('keydown', keydownFun, false);
            },

            phonelogin = function () {//手机登录

                var str = '<a class="lpl_qq" href ="' + baseurl + 'redirectToExtLogin.asp?extlogin_type=QQ&pos=' + pos + '&game_id=' + game_id + '&invite_type=' + invite_type + '&invite_by=' + invite_by + '&invite_level=' + invite_level + '">QQ登录</a>',

                    testStr = '';

                if (IsPC()) {

                    str = '<span class="lpl_qq pc_qq">QQ登录</span><span class="lpl_weixin pc_weixin">微信</span>';

                }

                if (isWeiXin()) {

                    str = '<a class="lpl_qq" href ="' + baseurl + 'redirectToExtLogin.asp?extlogin_type=QQ&pos=' + pos + '&game_id=' + game_id + '&invite_type=' + invite_type + '&invite_by=' + invite_by + '&invite_level=' + invite_level + '">QQ登录</a><a class="lpl_weixin" href="' + baseurl + 'redirectToExtLogin.asp?extlogin_type=WEIXIN&pos=' + pos + '&game_id=' + game_id + '&invite_type=' + invite_type + '&invite_by=' + invite_by + '&invite_level=' + invite_level + '">微信</a>';

                }

//				   if(typeof miniClient != 'undefined'){ //miniClient 隐藏qq
//					  str = '';
//				   }

                if (!userData.nickname) {

                    testStr = '<span class="lpl_test">试玩</span>';
                }


                if (isWeiXin()) testStr = '';


                h5_login_popmask.innerHTML =
                    ['<div class="loginphone loginmod">',
                        '	<i class="lp_clo"></i>',
                        '	<div class="lp_tit">手机登录</div>',
                        '	<input class="lp_phonenumber" type="" name="" placeholder="手机号">',
                        '	<div class="lp_verification"><input class="lpv_codeinp" type="" name="" placeholder="验证码"><span class="lpv_code">获取验证码</span></div>',
                        '	<span class="lp_start">立即登录</span>',
                        '	<a class="lp_3tang">帐号登录</a>',
                        '	<p class="lp_otherlogin">其他方式登录</p>',
                        '	<div class="lp_loginmod">',
                        str,
                        testStr,
                        '	</div>',
                        '</div>'].join('');


                popMiddle();//弹出框居中


                if (!IsPC()) return;
                doc.addEventListener('keydown', keydownFun, false);
            },
            testlogin = function (target) {//试玩登录

                var cutpicture = function (data) {//提示用户截图

                        var str = '<p class="lcp_account">帐号：' + data.username + '</p><p class="lcp_password">密码：' + data.password + '</p><p class="lcp_stress">试玩提醒:平台为您提供了系统账号，建议<i></i>截图保存。</p>';


                        if (typeof data == 'string') {

                            if (typeof miniClient != 'undefined') {
                                str = '<img class = "canvas_img" src="' + data + '"><p class="lcp_stress">试玩提醒：截图本页面保存至相册，下次使用该帐号密码登录游戏，可找回游戏进度。</p>';
                            } else {
                                str = '<img class = "canvas_img" src="' + data + '"><p class="lcp_stress">试玩提醒：长按上方图片（或截图本页面）保存，下次使用该帐号密码登录游戏，可找回游戏进度。</p>';
                            }

                        }


                        h5_login_popmask.innerHTML =

                            ['<div class="logincutpic loginmod">',

                                '<i class="cut_lp_clo"></i>',

                                '<h2 class="lcp_title">试玩帐号</h2>',


                                str,

                                '<span class="lcp_iknow_test">立即试玩</span>',

                                '</div>'].join('');


                        popMiddle();//弹出框居中

                    },

                    drawImg = function (canvas, data) {


                        if (!canvas.getContext) {


                            cutpicture(data);

                            return;


                        }


                        var ctx = canvas.getContext("2d"),

                            img = new Image();


                        img.crossOrigin = '*';

                        img.src = baseurl + 'images/saveuserdata.png';

                        img.onload = function () {

                            ctx.drawImage(img, 0, 0, canvas.width, canvas.height);

                            ctx.font = "15px Microsoft YaHei";

                            //设置字体填充颜色

                            ctx.fillStyle = "#333";
                            //从坐标点(50,50)开始绘制文字

                            ctx.textAlign = 'left';

                            ctx.fillText('帐号：' + data.username, 80, 108);

                            ctx.fillText('密码：' + data.password, 80, 126);

                            cutpicture(canvas.toDataURL("image/png"));


                            // userimgsrc = canvas.toDataURL("image/png");

                        }


                    },

                    createCanvas = function (data) {

                        var canvas = doc.createElement('canvas');

                        canvas.className = 'passwordcanvas';

                        document.body.appendChild(canvas);

                        drawImg(canvas, data);

                    };


                if (target.state) return;

                target.state = true;

                IO.jsonp(ajaxurl + 'account1.asp?action=try', {
                    pos: pos,
                    game_id: game_id,
                    invite_type: invite_type,
                    invite_by: invite_by,
                    invite_level: invite_level

                }, function (data) {


                    target.state = false;


                    if (data.code != 200) {


                        messageShow.show(data.message);

                        return;


                    }

                    createCanvas(data.data);


                });
            },


            testsign = function () {//试玩帐号激活
                h5_login_popmask.innerHTML =

                    ['<div class="trial loginmod">',
                        //'	<i class="lp_clo"></i>',
                        '	<div class="lp_tit">试玩帐号激活</div>',
                        '	<input class="trial_account" type="" name="" placeholder="帐号：4-15字符（仅限数字，英文）">',
                        '	<input class="trial_password" type="password" name="" placeholder="6-20位密码">',
                        '	<input class="confirm_password" type="password" name="" placeholder="确认密码">',
                        '	<input class="trial_realname" type="" name="" placeholder="姓名">',
                        '	<input class="trial_cardID" type="" name="" placeholder="身份证">',
                        '	<div class="trial_fj"><a class="trial_joinfast">切换帐号</a></div>',
                        '	<span class="trial_login">立即登录</span>',
                        '</div>'].join('');


                popMiddle();//弹出框居中

            },
            keydownFun = function (e) {

                if (!e) e = window.event;

                if ((e.keyCode || e.which) == 13) {

                    if (query.$('.lp_start')) query.$('.lp_start').click();

                    if (query.$('.fast_login')) query.$('.fast_login').click();

                    if (query.$('.verify_login')) query.$('.verify_login').click();
                    if (query.$('.exit_btn')) query.$('.exit_btn').click();
                    if (query.$('.lact_login')) query.$('.lact_login').click();

                    if (query.$('.trial_login')) query.$('.trial_login').click();

                    if (query.$('.lact_create')) query.$('.lact_create').click();

                }

            },

            popmaskHidden = function () {


                if (h5_login_popmask) h5_login_popmask.style.display = 'none';

                if (!IsPC()) return;
                doc.removeEventListener('keydown', keydownFun, false);

            },


            toDoLogin = function () {

                var numbers = query.$('.lact_accountnumber'),

                    passwrod = query.$('.lact_password'),

                    callFun = arguments.callee;


                if (santangAccount(numbers)) return;

                if (santangPassword(passwrod)) return;

                if (callFun.state) return;

                callFun.state = true;


                // 注册POS
                // var pos = 0;
                // // 游戏ID
                // var game_id = 20062;
                // // 邀请类型
                // var invite_type = 0;
                // // 邀请人
                // var invite_by = 0;
                // // 邀请level
                // var invite_level = 0;

                IO.jsonp(ajaxurl + 'account1.asp?action=login', {

                    username: numbers.value,
                    password: passwrod.value,
                    pos: pos,
                    game_id: game_id,
                    invite_type: invite_type,
                    invite_by: invite_by,
                    invite_level: invite_level

                }, function (data) {//登录获取用户信息

                    callFun.state = false;

                    if (data.code == 200) {

                        h5Login.login();

                        return;
                    }

                    messageShow.show(data.message);


                });


            },
            toXHcreate = function () {

                var numbers = query.$('.xh_number');

                if (XHNumber(numbers)) return;


                IO.jsonp(ajaxurl + 'api/plxh.asp?action=create', {

                    amax: numbers.value,
                }, function (data) {//登录获取用户信息
                    if (data.code == 200) {
                        messageShow.show("小号批量创建成功！");
                        h5Login.login();

                        return;
                    }

                    messageShow.show(data.message);


                });


            },
            toXHLogin = function (target) {
                var ary = target.getAttribute('v');

                if (target.state) return;

                target.state = true;

                IO.jsonp(ajaxurl + 'api/plxh.asp', {userid: ary}, function (data) {

                    console.log(data);

                    var _data = data.data;

                    target.state = false;

                    if (data.code == 200) {

                        h5Login.login();

                        return;
                    }

                    popshow.show(data.message);

                });


            },
            trialLogin = function () {

                var wf = query.$('.trial_account'),

                    wfp = query.$('.trial_password'),

                    wfpss = query.$('.confirm_password'),
                    wfrealname = query.$('.trial_realname'),
                    wfcardID = query.$('.trial_cardID'),
                    callFun = arguments.callee;


                if (santangAccount(wf)) return;

                if (santangPassword(wfp)) return;


                if (santangPassword(wfpss)) return;
                if (santangRealName(wfrealname)) return;
                if (santangCardID(wfcardID)) return;

                if (!(wfp.value == wfpss.value && wfp.value != '')) {

                    messageShow.show('两次输入密码不一致请重新输入');

                    return;
                }


                if (callFun.state) return;

                callFun.state = true;


                IO.jsonp(ajaxurl + 'account1.asp?action=bind', {
                    username: wf.value,
                    password: wfp.value,
                    password_repeat: wfpss.value,
                    realname: wfrealname.value,
                    cardID: wfcardID.value,
                    pos: pos,
                    game_id: game_id,
                    invite_type: invite_type,
                    invite_by: invite_by,
                    invite_level: invite_level
                }, function (data) {

                    callFun.state = false;

                    if (data.code == 200) {


                        h5Login.login();

                        return;

                    }


                    messageShow.show(data.message);


                });

            },

            sendcode = function (target, codeEle) {//发送验证码

                var cd_wait = 60,

                    time_id = function (o) {

                        if (cd_wait == 0) {

                            codeEle.innerHTML = "重新发送";

                            cd_wait = 60;

                            target.state = false;

                            clearTimeout(time_id);

                            return;

                        }


                        codeEle.innerHTML = cd_wait + "s后重新发送";

                        cd_wait--;

                        setTimeout(time_id, 1000);

                    };


                if (!codeEle) return;

                time_id();

            },

            lpvCode = function (target) {

                var ele = query.$('.lp_phonenumber');


                if (phonetest(ele)) return;

                if (target.state) return;

                target.state = true;


                IO.jsonp(ajaxurl + 'api/sendmessage.asp', {mobile: ele.value}, function (data) {


                    if (data.code == 200) {

                        sendcode(target, query.$('.lpv_code'));

                        return;

                    }

                    target.state = false;

                    messageShow.show(data.message);


                });

            },

            testVfCode = function (that) {//检验验证码

                var txt = that.value,

                    pattern = /\d{6}/;

                if (txt == '') {


                    messageShow.show('验证码不能为空');

                    return true;

                }

                if (!pattern.test(txt)) {

                    messageShow.show('验证码有误');

                    return true;

                }

                return false;
            },

            moreaccountNumber = function (data) {//一个手机绑定了多个帐号的情况

                var str = '';

                data.forEach(function (item) {

                    str += '<a class="login_item" v=' + item.login_url + '><img src="' + item.avatar + '"><i>' + item.nickname + '</i></a>';

                });


                h5_login_popmask.innerHTML =
                    ['<div class="more_login loginmod">',
                        '	<i class="more_login_clo"></i>',
                        '	<h2>选择已绑定帐号登录</h2>',
                        '	<div class="more_login_overflow">',
                        '		<div class="more_login_box">',
                        str,
                        // '			<a href=""><img src="images/ewm.png"><i>nnn</i></a>',
                        // '			<a href=""><img src="images/ewm.png"><i>nnnnnnnnn</i></a>',
                        // '			<!-- <a href=""><img src="images/ewm.png"><i>nnnnnnnnn</i></a> -->',
                        '		</div>',
                        '	</div>',
                        '	<span class="ml_goback" href="">返回登录首页</span>',
                        '</div>'].join('');


                var num = h5_login_popmask.querySelectorAll('.more_login_box a').length,

                    w = h5_login_popmask.querySelector('.login_item').offsetWidth;


                h5_login_popmask.querySelector('.more_login_box').style.width = num * w + num * 30 + 'px';


                popMiddle();
            },

            toDoPhoneLogin = function () {

                var phoneNumber = query.$('.lp_phonenumber'),

                    captcha = query.$('.lpv_codeinp'),

                    callFun = arguments.callee;


                if (phonetest(phoneNumber)) return;


                if (testVfCode(captcha)) return;


                if (callFun.state) return;

                callFun.state = true;

                IO.jsonp(ajaxurl + 'api/mobilelogin.asp', {
                    mobile: phoneNumber.value,
                    captcha: captcha.value,
                    pos: pos,
                    game_id: game_id,
                    invite_type: invite_type,
                    invite_by: invite_by,
                    invite_level: invite_level

                }, function (data) {

                    callFun.state = false;

                    if (data.code == 200) {

                        if (data.data.length >= 2) {

                            moreaccountNumber(data.data);

                            return;

                        }


                        h5Login.login();

                        return;

                    }


                    messageShow.show(data.message);


                });
            },

            accountuserLogin = function (target) {

                var url = target.getAttribute('v');

                if (target.state) return;

                target.state = true;


                IO.jsonp(url, function (data) {

                    target.state = false;

                    if (data.code == 200) {

                        h5Login.login();

                        return;

                    }

                    messageShow.show(data.message);

                });
            },

            toDoFastLogin = function () {


                var wf = query.$('.fast_account'),

                    wfp = query.$('.fast_password'),

                    wfpss = query.$('.fastrepeat_password'),
                    wfrealname = query.$('.fast_realname'),
                    wfcardID = query.$('.fast_cardID'),
                    callFun = arguments.callee;


                if (santangAccount(wf)) return;

                if (santangPassword(wfp)) return;

                if (santangPassword(wfpss)) return;
                if (santangRealName(wfrealname)) return;
                if (santangCardID(wfcardID)) return;

                if (!(wfp.value == wfpss.value && wfp.value != '')) {

                    messageShow.show('两次输入密码不一致请重新输入');

                    return;
                }


                if (callFun.state) return;

                callFun.state = true;


                IO.jsonp(ajaxurl + 'account1.asp?action=register', {

                    username: wf.value,
                    password: wfp.value,
                    password_repeat: wfpss.value,
                    realname: wfrealname.value,
                    cardID: wfcardID.value,
                    pos: pos,
                    game_id: game_id,
                    invite_type: invite_type,
                    invite_by: invite_by,
                    invite_level: invite_level

                }, function (data) {

                    callFun.state = false;

                    if (data.code == 200) {


                        h5Login.login();

                        return;

                    }


                    messageShow.show(data.message);


                });
            },
            toDoVerifyLogin = function () {


                var wfrealname = query.$('.verify_realname'),
                    wfcardID = query.$('.verify_cardID'),
                    callFun = arguments.callee;

                if (santangRealName(wfrealname)) return;
                if (santangCardID(wfcardID)) return;

                if (callFun.state) return;

                callFun.state = true;


                IO.jsonp(ajaxurl + 'account1.asp?action=verify', {
                    realname: wfrealname.value,
                    cardID: wfcardID.value,
                }, function (data) {

                    callFun.state = false;

                    if (data.code == 200) {
                        userData.id_card = wfcardID;
                        login.popmaskHidden();
                        query.$('#h5_ifream').contentWindow.postMessage('verifyOK', '*');
                        messageShow.show("认证成功！");
                        return;

                    }


                    messageShow.show(data.message);


                });
            },
            forgetPassword = function (m) {//忘记密码


                var str = '	<input class="fw_account" type="text" placeholder="帐号">';

                // console.log(m);

                if (m != '') {
                    str = '	<input class="fw_account" type="text" placeholder="帐号" value="' + m + '">';
                }

                if (m == undefined) {
                    str = '	<input class="fw_account" type="text" placeholder="帐号">';
                }

                h5_login_popmask.innerHTML =

                    ['<div class="forgetpasswordbox">',
                        '	<i class="cloo"></i>',
                        '	<i class="return"></i>',
                        '	<h2>找回密码</h2>',
                        str,
                        '	<div class="fw_yzm"><input class="fw_erification"  placeholder="验证码"><img class="fw_code" src="' + baseurl + 'include/verifycode.asp"></div>',
                        '	<span class="fw_nexttype">下一步</span>',
                        '</div>'].join('');

                popMiddle();

            },

            getNewcode = function (target) {//点击获取图片验证码

//
//		  			if( target.state ) return;
//
//		  			target.state = true;
//
//		  			IO.jsonp(ajaxurl +'uc/site/refresh-captcha.asp',function(){
//
//		  				target.state = false;

                query.$('.fw_code').src = baseurl + 'include/verifycode.asp?' + Math.random() * 700 + 800 + '';

//		  			});
            },

            fwPhone = function () {

                // h5_login_popmask.innerHTML =

                // 		['<div class="forgetpasswordbox">',
                // 		'	<i class="clo"></i>',
                // 		'	<i class="return"></i>',
                // 		'	<h2>通过手机找回</h2>',
                // 		'	<input class="fw_phone" type="tel" placeholder="手机号">',
                // 		'	<div class="fw_phoneyzm"><input class="fw_erification"  placeholder="验证码"><span class="get_yzm">获取验证码</span></div>',
                // 		'	<span class="fw_nexttype">验证手机</span>',
                // 		'</div>'].join('');

                // popMiddle();

            },

            testpicCode = function (that) {//检验验证码

                var txt = that.value,

                    pattern = /^\d{4}$/;// /[a-zA-Z]{4}/;

                if (txt == '') {


                    messageShow.show('验证码不能为空');

                    return true;

                }

                if (!pattern.test(txt)) {

                    messageShow.show('验证码格式错误');

                    return true;

                }

                return false;
            },

            changePassword = function (target) {


                var userName = query.$('.fw_account'),

                    captcode = query.$('.fw_erification');

                target.state = false;

                if (santangAccount(userName)) return;

                if (testpicCode(captcode)) return;


                IO.jsonp(ajaxurl + 'api/checkusername.asp', {

                    username: userName.value,

                    captcha: captcode.value

                }, function (data) {

                    if (data.code == 200) {


                        if (data.data.mobile != null) {

                            h5_login_popmask.innerHTML = //通过短信验证码找回


                                ['<div class="forgetpasswordbox">',
                                    '	<i class="cloo"></i>',
                                    '	<i class="returnc"></i>',
                                    '	<h2>通过手机找回</h2>',
                                    '	<input class="fw_phone" type="tel" value="' + data.data.mobile + '" readonly="readonly">',
                                    '	<div class="fw_phoneyzm"><input class="fw_erification"  placeholder="验证码"><span class="get_yzm">获取验证码</span></div>',
                                    '	<span class="fw_nexttypeh">确认</span>',
                                    '</div>'].join('');

                            popMiddle();

                        } else {
                            h5_login_popmask.innerHTML = //没绑定过手机号码

                                ['<div class="forgetpasswordbox">',
                                    '	<i class="cloo"></i>',
                                    '	<i class="returnb"></i>',
                                    '	<h2>通过手机找回</h2>',
                                    '   <p class="first">系统未检测到您的绑定手机号</p>',
                                    '   <p>请联系客服：1599610916</p>',
                                    '	<span class="fw_gobackn">返回</span>',
                                    '</div>'].join('');

                            popMiddle();
                        }


                    } else {

                        messageShow.show(data.message);

                    }


                });


            },

            getresetCode = function (target) {

                if (target.state) return;

                target.state = true;


                IO.jsonp(ajaxurl + 'api/sendmessage.asp', {}, function (data) {

                    // target.state = false;

                    if (data.code == 200) {

                        sendcode(target, query.$('.get_yzm'));

                        return;

                    }

                    target.state = false;

                    messageShow.show(data.message);

                });
            },

            codeisTrue = function (target) {

                var captcha = query.$('.fw_erification');

                if (testVfCode(captcha)) return;


                if (target.state) return;

                target.state = true;


                IO.jsonp(ajaxurl + 'api/checkmessage.asp', {

                    code: captcha.value

                }, function (data) {

                    target.state = false;

                    if (data.code == 200) {

                        h5_login_popmask.innerHTML = //修改密码
                            ['<div class="forgetpasswordbox">',
                                '	<i class="cloo"></i>',
                                '	<i class="returnbb"></i>',
                                '	<h2>修改密码</h2>',
                                '	<input class="fw_pasnew" type="password" placeholder="新密码">',
                                '	<input class="fw_pasnewt" type="password" placeholder="确认密码">',
                                '	<span class="fw_goback">确认</span>',
                                '</div>'].join('');
                        popMiddle();

                    } else {

                        target.state = false;
                        messageShow.show(data.message);
                    }

                });

            },
            changePasswordn = function (target) {

                var numbers = query.$('.fw_pasnew'),

                    passwrod = query.$('.fw_pasnewt');


                var passwordtrue = function (that, message, str) {

                    var txt = that.value;

                    txt = txt.replace(/(^\s*)|(\s*$)/g, '');

                    str = str || '';

                    if (!txt) {

                        messageShow.show(message);

                        return true;

                    }

                    if (str == '') return;

                    if (txt.length < 6 || txt.length > 20) {

                        messageShow.show(str + '格式有误');

                        return true;
                    }


                    return false;

                };

                if (passwordtrue(numbers, '新密码不能为空', '新密码')) return;


                if (passwordtrue(passwrod, '请再次确认密码')) return;


                if (numbers.value != passwrod.value) {

                    messageShow.show('两次密码输入不一致请,重新输入');

                    return;
                }


                if (target.state) return;

                target.state = true;

                IO.jsonp(ajaxurl + 'api/resetpassword.asp', {


                    password: numbers.value


                }, function (data) {

                    target.state = false;

                    messageShow.show(data.message);


                    var load = function () {

                        if (IsPC()) {
                            parent.location.reload(false);
                        }

                        window.location.reload();


                    };

                    if (data.code == 200) {

                        setTimeout(load, 3000);

                    }


                });

            },
            openXHlist = function () {

                IO.jsonp(ajaxurl + 'api/plxh.asp', function (data) {

                    if (data.code == 200) {

                        if (data.list.length != 0) {
                            var str = '';
                            var tempStr = '';
                            tempStr += ['<div class="xh_listbox">',
                                '<i class="lp_clo"></i>',
                                '<div class="lp_tit">小号管理</div>',
                                '<ul class="xh_ul">',
                                '<li class="xh_li">',
                                '<i class="zhuhao_txt">主号：</i>',
                                '<div class="xh_account">' + data.xh_father + '</div>'].join('');
                            if (data.xh_father == data.currentuser) {
                                tempStr += '<span class="xh_cur" v="' + data.xh_father + '">当前</span></li>';
                            } else {
                                tempStr += '<span class="enter_xh" v="' + data.xh_father + '">进入</span></li>';
                            }
                            data.list.forEach(function (item) {

                                if (item.name == data.currentuser) {
                                    str += ['<li class="xh_li">',
                                        '<i class="xiaohao_txt">小号：</i>',
                                        '<div class="xh_account">' + item.name + '</div>',
                                        '<span class="xh_cur" v="' + item.name + '">当前</span>',
                                        '</li>'].join('');
                                } else {
                                    str += ['<li class="xh_li">',
                                        '<i class="xiaohao_txt">小号：</i>',
                                        '<div class="xh_account">' + item.name + '</div>',
                                        '<span class="enter_xh" v="' + item.name + '">进入</span>',
                                        '</li>'].join('');
                                }


                            });


                            h5_login_popmask.innerHTML = tempStr + str + '</ul></div>';
                            popMiddle();

                        } else {
                            //提示创建小号
                            h5_login_popmask.innerHTML =
                                ['<div class="xh_plcj" style="margin-top: -293px;">',
                                    '<i class="lp_clo"></i>',
                                    '<div class="lp_tit">批量创建小号</div>',
                                    '<input class="xh_number" type="text" name="" placeholder="输入小号数量">',
                                    '<span class="lact_create">立即创建</span>',
                                    '</div>',
                                    '</div>'].join('');

                            popMiddle();
                        }


                    } else {

                        messageShow.show(data.message);

                    }


                });


            },
            clickEvent = function (event) {

                var target = event.target;

                if (target.className == 'lp_clo' || target.className == 'lcp_iknow' || target.className == 'more_login_clo') {//关闭弹窗

                    popmaskHidden();

                    return;
                }

                if (target.className == 'cloo') {

                    gameLogin();

                    return;
                }

                if (target.className == 'enter_xh') {//切换小号

                    toXHLogin(target);

                    return;
                }

                if (target.className == 'lact_login' || target.className == 'register_login') {//用户名密码登录


                    toDoLogin();

                    return;

                }
                if (target.className == 'lact_create') {//用户名密码登录


                    toXHcreate();

                    return;

                }


                if (target.className == 'lact_joinfast' || target.className == 'loginmod_singn_3tang') {//快速注册

                    fastlogin();

                    return;

                }


                if (target.className == 'ml_goback') {//切换回登录选择界面

                    gameLogin();

                    return;

                }
                if (target.className == 'try_joinfast') {//试玩用户绑定帐号


                    testsign();

                    return;

                }

                if (target.className == 'trial_login') {//试玩用户绑定帐号


                    trialLogin();

                    return;

                }


                if (target.className == 'trial_joinfast' || target.className == 'lp_3tang' || target.className == 'loginmod_3tang' || target.className == 'register_joinfast' || target.className == 'fast_joinfast') {//帐号登录

                    santanglogin();

                    return;
                }

                if (target.className == 'loginmod_mobile') {//手机登录标签插入

                    phonelogin();

                    return;
                }


                if (target.className == 'lpv_code') {//手机登录发送验证码


                    lpvCode(target);

                    return;

                }


                if (target.className == 'lp_start') {//手机立即登录


                    toDoPhoneLogin();


                    return;
                }


                if (target.className == 'loginmod_test' || target.className == 'lpl_test') {//试玩登录


                    testlogin(target);


                    return;
                }


                if (target.className.indexOf('pc_qq') > -1) {//pc端qq登录

                    window.open(baseurl + 'redirectToExtLogin.asp?extlogin_type=QQ&pos=' + pos + '&game_id=' + game_id + '&invite_type=' + invite_type + '&invite_by=' + invite_by + '&invite_level=' + invite_level + '', 'QQ登录', 'width=800,height=660,location=no,menubar=no,scrollbars=yes');

                    return;
                }


                if (target.className.indexOf('pc_weixin') > -1) {//pc端微信登录


                    window.open(baseurl + 'redirectToExtLogin.asp?extlogin_type=WEIXIN&pos=' + pos + '&game_id=' + game_id + '&invite_type=' + invite_type + '&invite_by=' + invite_by + '&invite_level=' + invite_level + '', '微信登录', 'width=800,height=660,location=no,menubar=no,scrollbars=yes');

                    return;

                }


                if (target.className == 'login_item') {//多个帐号选择登录哪一个


                    accountuserLogin(target);

                    return;

                }


                if ((target.nodeName.toLowerCase() == 'img' || target.nodeName.toLowerCase() == 'i') && target.parentNode.className == 'login_item') {


                    accountuserLogin(target.parentNode);


                    return;

                }


                if (target.className == 'fast_login') {//快速注册

                    toDoFastLogin();

                    return;

                }
                if (target.className == 'exit_btn') {//快速注册

                    window.location.href = ajaxurl;

                    return;

                }
                if (target.className == 'verify_login') {//快速注册

                    toDoVerifyLogin();

                    return;

                }

                if (target.className == 'fw_nexttype') {


                    if (target.state) return;

                    target.state = true;


                    changePassword(target);

                    return;
                }


                if (target.className == 'get_yzm') {


                    getresetCode(target);

                    return;

                }

                if (target.className == 'return') {

                    santanglogin();

                    return;

                }


                if (target.className == 'cut_lp_clo' || target.className == 'lcp_iknow_test') {

                    h5Login.login();

                    return;
                }


                if (target.className == 'lact_forgetpassword') {

                    var m = '';


                    if (query.$('.lact_forgetpassword').value != '') {

                        m = query.$('.lact_accountnumber').value;

                    }

                    forgetPassword(m);

                    getNewcode(target);


                    return;
                }


                if (target.className == 'fw_code') {

                    getNewcode(target);


                    return;

                }

                if (target.className == 'fw_nexttypeh') {


                    codeisTrue(target);

                    return;

                }

                if (target.className == 'fw_goback') {


                    changePasswordn(target);

                    return;
                }

                if (target.className == 'fw_gobackn' || target.className == 'returnb' || target.className == 'returnbb' || target.className == 'returnc') {

                    forgetPassword();

                    getNewcode(target);

                    return;

                }

            };

        h5_login_popmask.addEventListener('click', clickEvent, false);

        return {

            gameLogin: gameLogin,

            popmaskHidden: popmaskHidden,

            phonetest: phonetest,

            sendcode: sendcode,

            testVfCode: testVfCode,

            santangPassword: santangPassword,

            santanglogin: santanglogin,

            testsign: testsign,
            verifyLogin: verifyLogin,
            fastlogin: fastlogin,
            openXHlist: openXHlist,
            showRule: showRule

        };


    }(),

    bottomTap = function () {//底部提示栏

        if (IsPC() || isWeiXin() || isQQ()) return {};

        var createPop = function () {

                var bottom_pop = doc.createElement('div');

                bottom_pop.className = 'bottomtap';

                bottom_pop.innerHTML = '<i></i>' +
                    '<span class="bottom_clo"></span>' +
                    '<div class="rbox">' +
                    '	<p class="and_p">请点击收藏</p>' +
                    '	<p>或添加至书签</p>' +
                    '	<p>方便下次再玩</p>' +
                    '</div>';

                return doc.body.appendChild(bottom_pop);

            }(),

            hideFun = function () {

                createPop.style.display = 'none';

            },

            show = function () {

                createPop.style.display = 'block';
            },


            clickEvent = function (event) {

                var target = event.target;

                if (target.className == 'bottom_clo') {

                    hideFun();

                }

            };

        if (typeof gid == 'undefined' && typeof game_id == 'undefined') {

            if (getCookie('bottomTap')) return hideFun();

            setCookie('bottomTap', 1);

            show();

        } else {
            hideFun();
        }

        createPop.addEventListener('click', clickEvent, false);

        return {
            show: show,

            hideFun: hideFun
        }

    }(),

    clickBottom = function (event) {

        var target = event.target;

        // pbody = parent.document.getElementsByTagName('body')[0],
        // fullPage = pbody.getAttribute('class');
        // pbody.className = fullPage != null ? fullPage : '';

        if (target.className.indexOf('bottom_selected') > -1) {

            if (IsPC() && isWeiXin()) {

                event.preventDefault();

                parent.location.href = '//' + domain + 'h/index.asp';

                return;
            }

            if (IsPC()) {
                event.preventDefault();
                parent.location.href = '//' + domain + 'h/index.asp';
            }


        }

        if (target.className.indexOf('bottom_game') > -1) {

            if (IsPC() && isWeiXin()) {

                event.preventDefault();

                parent.location.href = '//' + domain + 'h/index.asp';


                return;
            }

            if (IsPC()) {
                event.preventDefault();
                parent.location.href = '//' + domain + 'h/index.asp';
            }


        }

        if (target.className.indexOf('bottom_gift') > -1) {

            if (IsPC()) {
                event.preventDefault();

                parent.location.href = '//' + domain + '' + 'h/index.asp';

            }
        }

        if (target.className.indexOf('bottom_pic') > -1) {

            if (IsPC()) {
                event.preventDefault();

                parent.location.href = '//' + domain + '' + 'h/user.asp';

            }

        }


        if (target.className.indexOf('bottom_community') > -1) {

            if (!IsPC()) {

                event.preventDefault();

                window.location.href = '//' + domain + 'h/ly.asp';

            } else {

            }

            return false;

        }

    };

islogin();


if (doc.querySelector('.bottom')) {

    if (isMobile()) {


    } else {
        if (isWeiXin()) {

            doc.querySelector('.bottom').className = 'bottom ispcweixin';
        }


    }

    doc.querySelector('.bottom').addEventListener('click', clickBottom, false);//登录点击事件

}

if (typeof h5Login == "undefined") {

    window.h5Login = {

        reloadFun: function () {

            if (IsPC()) {

                if (!h5_game_url_out) {

                    parent.location.reload(false);

                    return;

                }

                parent.location.href = h5_game_url_out;

                return;


            }

            if (!h5_game_url_out) {

                window.location.reload(false);

                return;

            }

            login.popmaskHidden();

            window.location.href = h5_game_url_out;

        },

        login: function () {
            for (key in h5Login) {

                if (typeof h5Login[key] == "function" && key != "login") {

                    h5Login[key]();

                }
            }
        }

    };
}

if (IsPC()) {//for pc scroll bar
    var style = document.createElement('style');
    style.innerHTML = '::-webkit-scrollbar{width:0;height:16px;background-color:#f1f1f1;}::-webkit-scrollbar:horizontal{width:5px;height:6px;background-color:#f1f1f1;}::-webkit-scrollbar-thumb{border-radius: 10px;background-color:#c9c9c9;}';
    document.getElementsByTagName('head')[0].appendChild(style);
}

/**
 * @Inertia.js
 * @author zhangxinxu
 * @version
 * @Created: 16-10-27
 * @description 拖动元素，并且具有惯性和边缘反弹效果
 */

(function (global, factory) {
    if (typeof define === 'function' && (define.amd || define.cmd)) {
        define(factory);
    } else {
        global.Inertia = factory();
    }
}(this, function () {
    'use strict';

    var popdisPlay = function (event) {

            var target = event.target;


            if (target.className == 'ball_jhmtc' || target.className == 'jhm' || (target.nodeName.toLowerCase() == 'b' && target.parentNode.className == 'ball_jhmtc')) {

                query.$('.ball_jhmtc').style.display = 'none';
            }

            query.addClass(doc.body, 'ballHidden');

        },
        Inertia = function (ele, options) {
            var defaults = {
                // 是否吸附边缘
                edge: true,
                startLeft: 0,
                startTop: 0
            };

            var params = {};
            options = options || {};
            for (var key in defaults) {
                if (typeof options[key] !== 'undefined') {
                    params[key] = options[key];
                } else {
                    params[key] = defaults[key];
                }
            }

            var data = {
                distanceX: 0,
                distanceY: 0
            };

            var win = window;

            // 浏览器窗体尺寸
            var winWidth = win.innerWidth;
            var winHeight = win.innerHeight;

            if (!ele) {
                return;
            }


            // 设置transform坐标等方法
            var fnTranslate = function (x, y) {
                x = Math.round(1000 * x) / 1000;
                y = Math.round(1000 * y) / 1000;

                ele.style.webkitTransform = 'translate(' + [x + 'px', y + 'px'].join(',') + ')';
                ele.style.transform = 'translate3d(' + [x + 'px', y + 'px', 0].join(',') + ')';
            };

            var strStoreDistance = '';
            // 居然有android机子不支持localStorage
            if (ele.id && win.localStorage && (strStoreDistance = localStorage['Inertia_' + ele.id])) {
                var arrStoreDistance = strStoreDistance.split(',');
                ele.distanceX = +arrStoreDistance[0];
                ele.distanceY = +arrStoreDistance[1];
                fnTranslate(ele.distanceX, ele.distanceY);
            }

            // 显示拖拽元素
            // ele.style.visibility = 'visible';


            if (ele.id && win.localStorage && (strStoreDistance = localStorage['Inertia_turn_' + ele.id])) {


                if (isWeiXin()) {

                    query.addClass(ele, 'ball_right');

                    ele.right = ele.offsetWidth / 2 + 'px';


                } else {

                    if (strStoreDistance == 'left') {

                        query.addClass(ele, 'ball_left');

                    }

                    if (strStoreDistance == 'right') {

                        query.addClass(ele, 'ball_right');

                    }

                }

            } else {

                query.addClass(ele, 'ball_right');

                ele.right = ele.offsetWidth / 2 + 'px';

            }

            // 如果元素在屏幕之外，位置使用初始值
            var initBound = ele.getBoundingClientRect();

            if (initBound.left < -0.5 * initBound.width ||
                initBound.top < -0.5 * initBound.height ||
                initBound.right > winWidth + 0.5 * initBound.width ||
                initBound.bottom > winHeight + 0.5 * initBound.height
            ) {
                ele.distanceX = 0;
                ele.distanceY = 0;
                fnTranslate(0, 0);
            }

            ele.addEventListener('touchstart', function (event) {

                var events = event.touches[0] || event;

                data.posX = events.pageX;
                data.posY = events.pageY;

                data.touching = true;

                if (ele.distanceX) {
                    data.distanceX = ele.distanceX;
                }
                if (ele.distanceY) {
                    data.distanceY = ele.distanceY;
                }

                // 元素的位置数据
                data.bound = ele.getBoundingClientRect();


                params.startLeft = data.bound.left;

                params.startTop = data.bound.top;

                data.timerready = true;
            });

            // easeOutBounce算法
            /*
            * t: current time（当前时间）；
             * b: beginning value（初始值）；
             * c: change in value（变化量）；
             * d: duration（持续时间）。
            **/
            var easeOutBounce = function (t, b, c, d) {
                if ((t /= d) < (1 / 2.75)) {
                    return c * (7.5625 * t * t) + b;
                } else if (t < (2 / 2.75)) {
                    return c * (7.5625 * (t -= (1.5 / 2.75)) * t + 0.75) + b;
                } else if (t < (2.5 / 2.75)) {
                    return c * (7.5625 * (t -= (2.25 / 2.75)) * t + 0.9375) + b;
                } else {
                    return c * (7.5625 * (t -= (2.625 / 2.75)) * t + 0.984375) + b;
                }
            };

            document.addEventListener('touchmove', function (event) {
                if (data.touching !== true) {
                    return;
                }

                // 当移动开始的时候开始记录时间
                if (data.timerready == true) {
                    data.timerstart = +new Date();
                    data.timerready = false;
                }

                event.preventDefault();

                var events = event.touches[0] || event;

                data.nowX = events.pageX;
                data.nowY = events.pageY;

                var distanceX = data.nowX - data.posX,
                    distanceY = data.nowY - data.posY;

                // 此时元素的位置
                var absLeft = data.bound.left + distanceX,
                    absTop = data.bound.top + distanceY,
                    absRight = absLeft + data.bound.width,
                    absBottom = absTop + data.bound.height;

                // 边缘检测
                if (absLeft < 0) {
                    distanceX = distanceX - absLeft;
                }
                if (absTop < 0) {
                    distanceY = distanceY - absTop;
                }
                if (absRight > winWidth) {
                    distanceX = distanceX - (absRight - winWidth);
                }
                if (absBottom > winHeight) {
                    distanceY = distanceY - (absBottom - winHeight);
                }

                // 元素位置跟随
                var x = data.distanceX + distanceX, y = data.distanceY + distanceY;
                fnTranslate(x, y);

                // 缓存移动位置
                ele.distanceX = x;
                ele.distanceY = y;

                // localStorage['Inertia_' + ele.id] = [ele.distanceX, ele.distanceY].join();
            });

            document.addEventListener('touchend', function (event) {


                var targetData = ele.getBoundingClientRect(),

                    topdatames = targetData.top - params.startTop,

                    leftdatames = params.startLeft - targetData.left;


                if (data.touching === false) {
                    // fix iOS fixed bug
                    return;
                }
                data.touching = false;

                // 计算速度
                data.timerend = +new Date();

                if (!data.nowX || !data.nowY) {
                    return;
                }

                // 移动的水平和垂直距离
                var distanceX = data.nowX - data.posX,
                    distanceY = data.nowY - data.posY;

                if (Math.abs(distanceX) < 5 && Math.abs(distanceY) < 5) {
                    return;
                }

                // 距离和时间
                var distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY),
                    time = data.timerend - data.timerstart;

                // 速度，每一个自然刷新此时移动的距离
                var speed = distance / time * 16.666;

                // 经测试，2~60多px不等
                // 设置衰减速率
                // 数值越小，衰减越快
                var rate = Math.min(10, speed);

                // 开始惯性缓动
                data.inertiaing = true;

                // 反弹的参数
                var reverseX = 1, reverseY = 1;

                // 速度计算法
                var step = function () {
                    if (data.touching == true) {
                        data.inertiaing = false;
                        return;
                    }
                    speed = speed - speed / rate;

                    // 根据运动角度，分配给x, y方向
                    var moveX = reverseX * speed * distanceX / distance,
                        moveY = reverseY * speed * distanceY / distance;

                    // 此时元素的各个数值
                    var bound = ele.getBoundingClientRect();

                    if (moveX < 0 && bound.left + moveX < 0) {
                        moveX = 0 - bound.left;
                        // 碰触边缘方向反转
                        reverseX = reverseX * -1;
                    } else if (moveX > 0 && bound.right + moveX > winWidth) {
                        moveX = winWidth - bound.right;
                        reverseX = reverseX * -1;
                    }

                    if (moveY < 0 && bound.top + moveY < 0) {
                        moveY = -1 * bound.top;
                        reverseY = -1 * reverseY;
                    } else if (moveY > 0 && bound.bottom + moveY > winHeight) {
                        moveY = winHeight - bound.bottom;
                        reverseY = -1 * reverseY;
                    }

                    var x = ele.distanceX + moveX, y = ele.distanceY + moveY;
                    // 位置变化
                    fnTranslate(x, y);

                    ele.distanceX = x;
                    ele.distanceY = y;

                    if (speed < 0.1) {
                        speed = 0;
                        if (params.edge == false) {
                            data.inertiaing = false;

                            if (win.localStorage) {
                                localStorage['Inertia_' + ele.id] = [x, y].join();
                            }
                        } else {
                            // 边缘吸附
                            edge();
                        }
                    } else {
                        requestAnimationFrame(step);
                    }
                };


                var turn = '';

                var edge = function () {
                    // 时间
                    var start = 0, during = 25;
                    // 初始值和变化量
                    var init = ele.distanceX, y = ele.distanceY, change = 0;
                    // 判断元素现在在哪个半区
                    var bound = ele.getBoundingClientRect();
                    // if (bound.left + bound.width / 2 < winWidth / 2) {
                    //     change = -1 * bound.left;
                    // } else {
                    //     change = winWidth - bound.right;
                    // }

                    // var run = function () {
                    //     // 如果用户触摸元素，停止继续动画
                    //     if (data.touching == true) {
                    //         data.inertiaing = false;
                    //         return;
                    //     }

                    //     start++;
                    //     var x = easeOutBounce(start, init, change, during);
                    //     fnTranslate(x, y);

                    //     if (start < during) {
                    //         requestAnimationFrame(run);
                    //     } else {
                    //         ele.distanceX = x;
                    //         ele.distanceY = y;

                    //         data.inertiaing = false;
                    //         if (win.localStorage) {
                    //            localStorage['Inertia_' + ele.id] = [x, y].join();
                    //        }
                    //     }
                    // };
                    //
                    //
                    //
                    if (isWeiXin()) {
                        if (bound.left + bound.width / 2 < winWidth / 2) {

                            turn = 'left';
                            change = winWidth - bound.left;

                        } else {

                            turn = 'right';
                            change = winWidth - bound.right;

                        }

                    } else {

                        if (bound.left + bound.width / 2 < winWidth / 2) {

                            turn = 'left';
                            change = -1 * bound.left;


                        } else {

                            turn = 'right';
                            change = winWidth - bound.right;

                        }
                    }


                    var ballwidth = ele.offsetWidth;


                    var run = function () {


                        // 如果用户触摸元素，停止继续动画

                        if (data.touching == true) {
                            data.inertiaing = false;
                            return;
                        }

                        start++;

                        var x = easeOutBounce(start, init, change, during);

                        if (isWeiXin()) {

                            query.removeClass(ele, 'ball_left');

                            query.addClass(ele, 'ball_right');

                            if (turn == 'left') {

                                fnTranslate(x - ballwidth / 1.5, y);


                            }

                            if (turn == 'right') {

                                fnTranslate(x + ballwidth / 3, y);


                            }

                        } else {

                            if (turn == 'left') {

                                query.removeClass(ele, 'ball_right');

                                query.addClass(ele, 'ball_left');

                                fnTranslate(x - ballwidth / 3, y);

                            }

                            if (turn == 'right') {

                                query.removeClass(ele, 'ball_left');

                                query.addClass(ele, 'ball_right');

                                fnTranslate(x + ballwidth / 3, y);

                            }


                        }


                        if (start < during) {

                            requestAnimationFrame(run);

                        } else {

                            ele.distanceX = x;
                            ele.distanceY = y;

                            data.inertiaing = false;

                            if (win.localStorage) {

                                if (turn == 'left') {

                                    x = x - ballwidth / 3;

                                } else if (turn == 'right') {

                                    x = x + ballwidth / 3;

                                }

                                localStorage['Inertia_' + ele.id] = [x, y].join();

                                localStorage['Inertia_turn_' + ele.id] = turn;

                            }
                        }

                        var onchange = x - init;

                    };
                    run();
                };

                step();


                if (topdatames <= 6 && topdatames >= 0) {

                    if (leftdatames <= 37 && leftdatames >= 0) {
                        popdisPlay(event);
                    }
                    if (leftdatames >= -37 && leftdatames < 0) {
                        popdisPlay(event);
                    }


                }

                if (topdatames >= -6 && topdatames < 0) {

                    if (leftdatames <= 37 && leftdatames >= 0) {
                        popdisPlay(event);
                    }
                    if (leftdatames >= -37 && leftdatames < 0) {
                        popdisPlay(event);
                    }

                }


            });
        };

    return Inertia;
}));

var QRCode;
!function () {
    function a(a) {
        this.mode = c.MODE_8BIT_BYTE, this.data = a, this.parsedData = [];
        for (var b = [], d = 0, e = this.data.length; e > d; d++) {
            var f = this.data.charCodeAt(d);
            f > 65536 ? (b[0] = 240 | (1835008 & f) >>> 18, b[1] = 128 | (258048 & f) >>> 12, b[2] = 128 | (4032 & f) >>> 6, b[3] = 128 | 63 & f) : f > 2048 ? (b[0] = 224 | (61440 & f) >>> 12, b[1] = 128 | (4032 & f) >>> 6, b[2] = 128 | 63 & f) : f > 128 ? (b[0] = 192 | (1984 & f) >>> 6, b[1] = 128 | 63 & f) : b[0] = f, this.parsedData = this.parsedData.concat(b)
        }
        this.parsedData.length != this.data.length && (this.parsedData.unshift(191), this.parsedData.unshift(187), this.parsedData.unshift(239))
    }

    function b(a, b) {
        this.typeNumber = a, this.errorCorrectLevel = b, this.modules = null, this.moduleCount = 0, this.dataCache = null, this.dataList = []
    }

    function i(a, b) {
        if (void 0 == a.length) throw new Error(a.length + "/" + b);
        for (var c = 0; c < a.length && 0 == a[c];) c++;
        this.num = new Array(a.length - c + b);
        for (var d = 0; d < a.length - c; d++) this.num[d] = a[d + c]
    }

    function j(a, b) {
        this.totalCount = a, this.dataCount = b
    }

    function k() {
        this.buffer = [], this.length = 0
    }

    function m() {
        return "undefined" != typeof CanvasRenderingContext2D
    }

    function n() {
        var a = !1, b = navigator.userAgent;
        return /android/i.test(b) && (a = !0, aMat = b.toString().match(/android ([0-9]\.[0-9])/i), aMat && aMat[1] && (a = parseFloat(aMat[1]))), a
    }

    function r(a, b) {
        for (var c = 1, e = s(a), f = 0, g = l.length; g >= f; f++) {
            var h = 0;
            switch (b) {
                case d.L:
                    h = l[f][0];
                    break;
                case d.M:
                    h = l[f][1];
                    break;
                case d.Q:
                    h = l[f][2];
                    break;
                case d.H:
                    h = l[f][3]
            }
            if (h >= e) break;
            c++
        }
        if (c > l.length) throw new Error("Too long data");
        return c
    }

    function s(a) {
        var b = encodeURI(a).toString().replace(/\%[0-9a-fA-F]{2}/g, "a");
        return b.length + (b.length != a ? 3 : 0)
    }

    a.prototype = {
        getLength: function () {
            return this.parsedData.length
        }, write: function (a) {
            for (var b = 0, c = this.parsedData.length; c > b; b++) a.put(this.parsedData[b], 8)
        }
    }, b.prototype = {
        addData: function (b) {
            var c = new a(b);
            this.dataList.push(c), this.dataCache = null
        }, isDark: function (a, b) {
            if (0 > a || this.moduleCount <= a || 0 > b || this.moduleCount <= b) throw new Error(a + "," + b);
            return this.modules[a][b]
        }, getModuleCount: function () {
            return this.moduleCount
        }, make: function () {
            this.makeImpl(!1, this.getBestMaskPattern())
        }, makeImpl: function (a, c) {
            this.moduleCount = 4 * this.typeNumber + 17, this.modules = new Array(this.moduleCount);
            for (var d = 0; d < this.moduleCount; d++) {
                this.modules[d] = new Array(this.moduleCount);
                for (var e = 0; e < this.moduleCount; e++) this.modules[d][e] = null
            }
            this.setupPositionProbePattern(0, 0), this.setupPositionProbePattern(this.moduleCount - 7, 0), this.setupPositionProbePattern(0, this.moduleCount - 7), this.setupPositionAdjustPattern(), this.setupTimingPattern(), this.setupTypeInfo(a, c), this.typeNumber >= 7 && this.setupTypeNumber(a), null == this.dataCache && (this.dataCache = b.createData(this.typeNumber, this.errorCorrectLevel, this.dataList)), this.mapData(this.dataCache, c)
        }, setupPositionProbePattern: function (a, b) {
            for (var c = -1; 7 >= c; c++) if (!(-1 >= a + c || this.moduleCount <= a + c)) for (var d = -1; 7 >= d; d++) -1 >= b + d || this.moduleCount <= b + d || (this.modules[a + c][b + d] = c >= 0 && 6 >= c && (0 == d || 6 == d) || d >= 0 && 6 >= d && (0 == c || 6 == c) || c >= 2 && 4 >= c && d >= 2 && 4 >= d ? !0 : !1)
        }, getBestMaskPattern: function () {
            for (var a = 0, b = 0, c = 0; 8 > c; c++) {
                this.makeImpl(!0, c);
                var d = f.getLostPoint(this);
                (0 == c || a > d) && (a = d, b = c)
            }
            return b
        }, createMovieClip: function (a, b, c) {
            var d = a.createEmptyMovieClip(b, c), e = 1;
            this.make();
            for (var f = 0; f < this.modules.length; f++) for (var g = f * e, h = 0; h < this.modules[f].length; h++) {
                var i = h * e, j = this.modules[f][h];
                j && (d.beginFill(0, 100), d.moveTo(i, g), d.lineTo(i + e, g), d.lineTo(i + e, g + e), d.lineTo(i, g + e), d.endFill())
            }
            return d
        }, setupTimingPattern: function () {
            for (var a = 8; a < this.moduleCount - 8; a++) null == this.modules[a][6] && (this.modules[a][6] = 0 == a % 2);
            for (var b = 8; b < this.moduleCount - 8; b++) null == this.modules[6][b] && (this.modules[6][b] = 0 == b % 2)
        }, setupPositionAdjustPattern: function () {
            for (var a = f.getPatternPosition(this.typeNumber), b = 0; b < a.length; b++) for (var c = 0; c < a.length; c++) {
                var d = a[b], e = a[c];
                if (null == this.modules[d][e]) for (var g = -2; 2 >= g; g++) for (var h = -2; 2 >= h; h++) this.modules[d + g][e + h] = -2 == g || 2 == g || -2 == h || 2 == h || 0 == g && 0 == h ? !0 : !1
            }
        }, setupTypeNumber: function (a) {
            for (var b = f.getBCHTypeNumber(this.typeNumber), c = 0; 18 > c; c++) {
                var d = !a && 1 == (1 & b >> c);
                this.modules[Math.floor(c / 3)][c % 3 + this.moduleCount - 8 - 3] = d
            }
            for (var c = 0; 18 > c; c++) {
                var d = !a && 1 == (1 & b >> c);
                this.modules[c % 3 + this.moduleCount - 8 - 3][Math.floor(c / 3)] = d
            }
        }, setupTypeInfo: function (a, b) {
            for (var c = this.errorCorrectLevel << 3 | b, d = f.getBCHTypeInfo(c), e = 0; 15 > e; e++) {
                var g = !a && 1 == (1 & d >> e);
                6 > e ? this.modules[e][8] = g : 8 > e ? this.modules[e + 1][8] = g : this.modules[this.moduleCount - 15 + e][8] = g
            }
            for (var e = 0; 15 > e; e++) {
                var g = !a && 1 == (1 & d >> e);
                8 > e ? this.modules[8][this.moduleCount - e - 1] = g : 9 > e ? this.modules[8][15 - e - 1 + 1] = g : this.modules[8][15 - e - 1] = g
            }
            this.modules[this.moduleCount - 8][8] = !a
        }, mapData: function (a, b) {
            for (var c = -1, d = this.moduleCount - 1, e = 7, g = 0, h = this.moduleCount - 1; h > 0; h -= 2) for (6 == h && h--; ;) {
                for (var i = 0; 2 > i; i++) if (null == this.modules[d][h - i]) {
                    var j = !1;
                    g < a.length && (j = 1 == (1 & a[g] >>> e));
                    var k = f.getMask(b, d, h - i);
                    k && (j = !j), this.modules[d][h - i] = j, e--, -1 == e && (g++, e = 7)
                }
                if (d += c, 0 > d || this.moduleCount <= d) {
                    d -= c, c = -c;
                    break
                }
            }
        }
    }, b.PAD0 = 236, b.PAD1 = 17, b.createData = function (a, c, d) {
        for (var e = j.getRSBlocks(a, c), g = new k, h = 0; h < d.length; h++) {
            var i = d[h];
            g.put(i.mode, 4), g.put(i.getLength(), f.getLengthInBits(i.mode, a)), i.write(g)
        }
        for (var l = 0, h = 0; h < e.length; h++) l += e[h].dataCount;
        if (g.getLengthInBits() > 8 * l) throw new Error("code length overflow. (" + g.getLengthInBits() + ">" + 8 * l + ")");
        for (g.getLengthInBits() + 4 <= 8 * l && g.put(0, 4); 0 != g.getLengthInBits() % 8;) g.putBit(!1);
        for (; ;) {
            if (g.getLengthInBits() >= 8 * l) break;
            if (g.put(b.PAD0, 8), g.getLengthInBits() >= 8 * l) break;
            g.put(b.PAD1, 8)
        }
        return b.createBytes(g, e)
    }, b.createBytes = function (a, b) {
        for (var c = 0, d = 0, e = 0, g = new Array(b.length), h = new Array(b.length), j = 0; j < b.length; j++) {
            var k = b[j].dataCount, l = b[j].totalCount - k;
            d = Math.max(d, k), e = Math.max(e, l), g[j] = new Array(k);
            for (var m = 0; m < g[j].length; m++) g[j][m] = 255 & a.buffer[m + c];
            c += k;
            var n = f.getErrorCorrectPolynomial(l), o = new i(g[j], n.getLength() - 1), p = o.mod(n);
            h[j] = new Array(n.getLength() - 1);
            for (var m = 0; m < h[j].length; m++) {
                var q = m + p.getLength() - h[j].length;
                h[j][m] = q >= 0 ? p.get(q) : 0
            }
        }
        for (var r = 0, m = 0; m < b.length; m++) r += b[m].totalCount;
        for (var s = new Array(r), t = 0, m = 0; d > m; m++) for (var j = 0; j < b.length; j++) m < g[j].length && (s[t++] = g[j][m]);
        for (var m = 0; e > m; m++) for (var j = 0; j < b.length; j++) m < h[j].length && (s[t++] = h[j][m]);
        return s
    };
    for (var c = {MODE_NUMBER: 1, MODE_ALPHA_NUM: 2, MODE_8BIT_BYTE: 4, MODE_KANJI: 8}, d = {
        L: 1,
        M: 0,
        Q: 3,
        H: 2
    }, e = {
        PATTERN000: 0,
        PATTERN001: 1,
        PATTERN010: 2,
        PATTERN011: 3,
        PATTERN100: 4,
        PATTERN101: 5,
        PATTERN110: 6,
        PATTERN111: 7
    }, f = {
        PATTERN_POSITION_TABLE: [[], [6, 18], [6, 22], [6, 26], [6, 30], [6, 34], [6, 22, 38], [6, 24, 42], [6, 26, 46], [6, 28, 50], [6, 30, 54], [6, 32, 58], [6, 34, 62], [6, 26, 46, 66], [6, 26, 48, 70], [6, 26, 50, 74], [6, 30, 54, 78], [6, 30, 56, 82], [6, 30, 58, 86], [6, 34, 62, 90], [6, 28, 50, 72, 94], [6, 26, 50, 74, 98], [6, 30, 54, 78, 102], [6, 28, 54, 80, 106], [6, 32, 58, 84, 110], [6, 30, 58, 86, 114], [6, 34, 62, 90, 118], [6, 26, 50, 74, 98, 122], [6, 30, 54, 78, 102, 126], [6, 26, 52, 78, 104, 130], [6, 30, 56, 82, 108, 134], [6, 34, 60, 86, 112, 138], [6, 30, 58, 86, 114, 142], [6, 34, 62, 90, 118, 146], [6, 30, 54, 78, 102, 126, 150], [6, 24, 50, 76, 102, 128, 154], [6, 28, 54, 80, 106, 132, 158], [6, 32, 58, 84, 110, 136, 162], [6, 26, 54, 82, 110, 138, 166], [6, 30, 58, 86, 114, 142, 170]],
        G15: 1335,
        G18: 7973,
        G15_MASK: 21522,
        getBCHTypeInfo: function (a) {
            for (var b = a << 10; f.getBCHDigit(b) - f.getBCHDigit(f.G15) >= 0;) b ^= f.G15 << f.getBCHDigit(b) - f.getBCHDigit(f.G15);
            return (a << 10 | b) ^ f.G15_MASK
        },
        getBCHTypeNumber: function (a) {
            for (var b = a << 12; f.getBCHDigit(b) - f.getBCHDigit(f.G18) >= 0;) b ^= f.G18 << f.getBCHDigit(b) - f.getBCHDigit(f.G18);
            return a << 12 | b
        },
        getBCHDigit: function (a) {
            for (var b = 0; 0 != a;) b++, a >>>= 1;
            return b
        },
        getPatternPosition: function (a) {
            return f.PATTERN_POSITION_TABLE[a - 1]
        },
        getMask: function (a, b, c) {
            switch (a) {
                case e.PATTERN000:
                    return 0 == (b + c) % 2;
                case e.PATTERN001:
                    return 0 == b % 2;
                case e.PATTERN010:
                    return 0 == c % 3;
                case e.PATTERN011:
                    return 0 == (b + c) % 3;
                case e.PATTERN100:
                    return 0 == (Math.floor(b / 2) + Math.floor(c / 3)) % 2;
                case e.PATTERN101:
                    return 0 == b * c % 2 + b * c % 3;
                case e.PATTERN110:
                    return 0 == (b * c % 2 + b * c % 3) % 2;
                case e.PATTERN111:
                    return 0 == (b * c % 3 + (b + c) % 2) % 2;
                default:
                    throw new Error("bad maskPattern:" + a)
            }
        },
        getErrorCorrectPolynomial: function (a) {
            for (var b = new i([1], 0), c = 0; a > c; c++) b = b.multiply(new i([1, g.gexp(c)], 0));
            return b
        },
        getLengthInBits: function (a, b) {
            if (b >= 1 && 10 > b) switch (a) {
                case c.MODE_NUMBER:
                    return 10;
                case c.MODE_ALPHA_NUM:
                    return 9;
                case c.MODE_8BIT_BYTE:
                    return 8;
                case c.MODE_KANJI:
                    return 8;
                default:
                    throw new Error("mode:" + a)
            } else if (27 > b) switch (a) {
                case c.MODE_NUMBER:
                    return 12;
                case c.MODE_ALPHA_NUM:
                    return 11;
                case c.MODE_8BIT_BYTE:
                    return 16;
                case c.MODE_KANJI:
                    return 10;
                default:
                    throw new Error("mode:" + a)
            } else {
                if (!(41 > b)) throw new Error("type:" + b);
                switch (a) {
                    case c.MODE_NUMBER:
                        return 14;
                    case c.MODE_ALPHA_NUM:
                        return 13;
                    case c.MODE_8BIT_BYTE:
                        return 16;
                    case c.MODE_KANJI:
                        return 12;
                    default:
                        throw new Error("mode:" + a)
                }
            }
        },
        getLostPoint: function (a) {
            for (var b = a.getModuleCount(), c = 0, d = 0; b > d; d++) for (var e = 0; b > e; e++) {
                for (var f = 0, g = a.isDark(d, e), h = -1; 1 >= h; h++) if (!(0 > d + h || d + h >= b)) for (var i = -1; 1 >= i; i++) 0 > e + i || e + i >= b || (0 != h || 0 != i) && g == a.isDark(d + h, e + i) && f++;
                f > 5 && (c += 3 + f - 5)
            }
            for (var d = 0; b - 1 > d; d++) for (var e = 0; b - 1 > e; e++) {
                var j = 0;
                a.isDark(d, e) && j++, a.isDark(d + 1, e) && j++, a.isDark(d, e + 1) && j++, a.isDark(d + 1, e + 1) && j++, (0 == j || 4 == j) && (c += 3)
            }
            for (var d = 0; b > d; d++) for (var e = 0; b - 6 > e; e++) a.isDark(d, e) && !a.isDark(d, e + 1) && a.isDark(d, e + 2) && a.isDark(d, e + 3) && a.isDark(d, e + 4) && !a.isDark(d, e + 5) && a.isDark(d, e + 6) && (c += 40);
            for (var e = 0; b > e; e++) for (var d = 0; b - 6 > d; d++) a.isDark(d, e) && !a.isDark(d + 1, e) && a.isDark(d + 2, e) && a.isDark(d + 3, e) && a.isDark(d + 4, e) && !a.isDark(d + 5, e) && a.isDark(d + 6, e) && (c += 40);
            for (var k = 0, e = 0; b > e; e++) for (var d = 0; b > d; d++) a.isDark(d, e) && k++;
            var l = Math.abs(100 * k / b / b - 50) / 5;
            return c += 10 * l
        }
    }, g = {
        glog: function (a) {
            if (1 > a) throw new Error("glog(" + a + ")");
            return g.LOG_TABLE[a]
        }, gexp: function (a) {
            for (; 0 > a;) a += 255;
            for (; a >= 256;) a -= 255;
            return g.EXP_TABLE[a]
        }, EXP_TABLE: new Array(256), LOG_TABLE: new Array(256)
    }, h = 0; 8 > h; h++) g.EXP_TABLE[h] = 1 << h;
    for (var h = 8; 256 > h; h++) g.EXP_TABLE[h] = g.EXP_TABLE[h - 4] ^ g.EXP_TABLE[h - 5] ^ g.EXP_TABLE[h - 6] ^ g.EXP_TABLE[h - 8];
    for (var h = 0; 255 > h; h++) g.LOG_TABLE[g.EXP_TABLE[h]] = h;
    i.prototype = {
        get: function (a) {
            return this.num[a]
        }, getLength: function () {
            return this.num.length
        }, multiply: function (a) {
            for (var b = new Array(this.getLength() + a.getLength() - 1), c = 0; c < this.getLength(); c++) for (var d = 0; d < a.getLength(); d++) b[c + d] ^= g.gexp(g.glog(this.get(c)) + g.glog(a.get(d)));
            return new i(b, 0)
        }, mod: function (a) {
            if (this.getLength() - a.getLength() < 0) return this;
            for (var b = g.glog(this.get(0)) - g.glog(a.get(0)), c = new Array(this.getLength()), d = 0; d < this.getLength(); d++) c[d] = this.get(d);
            for (var d = 0; d < a.getLength(); d++) c[d] ^= g.gexp(g.glog(a.get(d)) + b);
            return new i(c, 0).mod(a)
        }
    }, j.RS_BLOCK_TABLE = [[1, 26, 19], [1, 26, 16], [1, 26, 13], [1, 26, 9], [1, 44, 34], [1, 44, 28], [1, 44, 22], [1, 44, 16], [1, 70, 55], [1, 70, 44], [2, 35, 17], [2, 35, 13], [1, 100, 80], [2, 50, 32], [2, 50, 24], [4, 25, 9], [1, 134, 108], [2, 67, 43], [2, 33, 15, 2, 34, 16], [2, 33, 11, 2, 34, 12], [2, 86, 68], [4, 43, 27], [4, 43, 19], [4, 43, 15], [2, 98, 78], [4, 49, 31], [2, 32, 14, 4, 33, 15], [4, 39, 13, 1, 40, 14], [2, 121, 97], [2, 60, 38, 2, 61, 39], [4, 40, 18, 2, 41, 19], [4, 40, 14, 2, 41, 15], [2, 146, 116], [3, 58, 36, 2, 59, 37], [4, 36, 16, 4, 37, 17], [4, 36, 12, 4, 37, 13], [2, 86, 68, 2, 87, 69], [4, 69, 43, 1, 70, 44], [6, 43, 19, 2, 44, 20], [6, 43, 15, 2, 44, 16], [4, 101, 81], [1, 80, 50, 4, 81, 51], [4, 50, 22, 4, 51, 23], [3, 36, 12, 8, 37, 13], [2, 116, 92, 2, 117, 93], [6, 58, 36, 2, 59, 37], [4, 46, 20, 6, 47, 21], [7, 42, 14, 4, 43, 15], [4, 133, 107], [8, 59, 37, 1, 60, 38], [8, 44, 20, 4, 45, 21], [12, 33, 11, 4, 34, 12], [3, 145, 115, 1, 146, 116], [4, 64, 40, 5, 65, 41], [11, 36, 16, 5, 37, 17], [11, 36, 12, 5, 37, 13], [5, 109, 87, 1, 110, 88], [5, 65, 41, 5, 66, 42], [5, 54, 24, 7, 55, 25], [11, 36, 12], [5, 122, 98, 1, 123, 99], [7, 73, 45, 3, 74, 46], [15, 43, 19, 2, 44, 20], [3, 45, 15, 13, 46, 16], [1, 135, 107, 5, 136, 108], [10, 74, 46, 1, 75, 47], [1, 50, 22, 15, 51, 23], [2, 42, 14, 17, 43, 15], [5, 150, 120, 1, 151, 121], [9, 69, 43, 4, 70, 44], [17, 50, 22, 1, 51, 23], [2, 42, 14, 19, 43, 15], [3, 141, 113, 4, 142, 114], [3, 70, 44, 11, 71, 45], [17, 47, 21, 4, 48, 22], [9, 39, 13, 16, 40, 14], [3, 135, 107, 5, 136, 108], [3, 67, 41, 13, 68, 42], [15, 54, 24, 5, 55, 25], [15, 43, 15, 10, 44, 16], [4, 144, 116, 4, 145, 117], [17, 68, 42], [17, 50, 22, 6, 51, 23], [19, 46, 16, 6, 47, 17], [2, 139, 111, 7, 140, 112], [17, 74, 46], [7, 54, 24, 16, 55, 25], [34, 37, 13], [4, 151, 121, 5, 152, 122], [4, 75, 47, 14, 76, 48], [11, 54, 24, 14, 55, 25], [16, 45, 15, 14, 46, 16], [6, 147, 117, 4, 148, 118], [6, 73, 45, 14, 74, 46], [11, 54, 24, 16, 55, 25], [30, 46, 16, 2, 47, 17], [8, 132, 106, 4, 133, 107], [8, 75, 47, 13, 76, 48], [7, 54, 24, 22, 55, 25], [22, 45, 15, 13, 46, 16], [10, 142, 114, 2, 143, 115], [19, 74, 46, 4, 75, 47], [28, 50, 22, 6, 51, 23], [33, 46, 16, 4, 47, 17], [8, 152, 122, 4, 153, 123], [22, 73, 45, 3, 74, 46], [8, 53, 23, 26, 54, 24], [12, 45, 15, 28, 46, 16], [3, 147, 117, 10, 148, 118], [3, 73, 45, 23, 74, 46], [4, 54, 24, 31, 55, 25], [11, 45, 15, 31, 46, 16], [7, 146, 116, 7, 147, 117], [21, 73, 45, 7, 74, 46], [1, 53, 23, 37, 54, 24], [19, 45, 15, 26, 46, 16], [5, 145, 115, 10, 146, 116], [19, 75, 47, 10, 76, 48], [15, 54, 24, 25, 55, 25], [23, 45, 15, 25, 46, 16], [13, 145, 115, 3, 146, 116], [2, 74, 46, 29, 75, 47], [42, 54, 24, 1, 55, 25], [23, 45, 15, 28, 46, 16], [17, 145, 115], [10, 74, 46, 23, 75, 47], [10, 54, 24, 35, 55, 25], [19, 45, 15, 35, 46, 16], [17, 145, 115, 1, 146, 116], [14, 74, 46, 21, 75, 47], [29, 54, 24, 19, 55, 25], [11, 45, 15, 46, 46, 16], [13, 145, 115, 6, 146, 116], [14, 74, 46, 23, 75, 47], [44, 54, 24, 7, 55, 25], [59, 46, 16, 1, 47, 17], [12, 151, 121, 7, 152, 122], [12, 75, 47, 26, 76, 48], [39, 54, 24, 14, 55, 25], [22, 45, 15, 41, 46, 16], [6, 151, 121, 14, 152, 122], [6, 75, 47, 34, 76, 48], [46, 54, 24, 10, 55, 25], [2, 45, 15, 64, 46, 16], [17, 152, 122, 4, 153, 123], [29, 74, 46, 14, 75, 47], [49, 54, 24, 10, 55, 25], [24, 45, 15, 46, 46, 16], [4, 152, 122, 18, 153, 123], [13, 74, 46, 32, 75, 47], [48, 54, 24, 14, 55, 25], [42, 45, 15, 32, 46, 16], [20, 147, 117, 4, 148, 118], [40, 75, 47, 7, 76, 48], [43, 54, 24, 22, 55, 25], [10, 45, 15, 67, 46, 16], [19, 148, 118, 6, 149, 119], [18, 75, 47, 31, 76, 48], [34, 54, 24, 34, 55, 25], [20, 45, 15, 61, 46, 16]], j.getRSBlocks = function (a, b) {
        var c = j.getRsBlockTable(a, b);
        if (void 0 == c) throw new Error("bad rs block @ typeNumber:" + a + "/errorCorrectLevel:" + b);
        for (var d = c.length / 3, e = [], f = 0; d > f; f++) for (var g = c[3 * f + 0], h = c[3 * f + 1], i = c[3 * f + 2], k = 0; g > k; k++) e.push(new j(h, i));
        return e
    }, j.getRsBlockTable = function (a, b) {
        switch (b) {
            case d.L:
                return j.RS_BLOCK_TABLE[4 * (a - 1) + 0];
            case d.M:
                return j.RS_BLOCK_TABLE[4 * (a - 1) + 1];
            case d.Q:
                return j.RS_BLOCK_TABLE[4 * (a - 1) + 2];
            case d.H:
                return j.RS_BLOCK_TABLE[4 * (a - 1) + 3];
            default:
                return void 0
        }
    }, k.prototype = {
        get: function (a) {
            var b = Math.floor(a / 8);
            return 1 == (1 & this.buffer[b] >>> 7 - a % 8)
        }, put: function (a, b) {
            for (var c = 0; b > c; c++) this.putBit(1 == (1 & a >>> b - c - 1))
        }, getLengthInBits: function () {
            return this.length
        }, putBit: function (a) {
            var b = Math.floor(this.length / 8);
            this.buffer.length <= b && this.buffer.push(0), a && (this.buffer[b] |= 128 >>> this.length % 8), this.length++
        }
    };
    var l = [[17, 14, 11, 7], [32, 26, 20, 14], [53, 42, 32, 24], [78, 62, 46, 34], [106, 84, 60, 44], [134, 106, 74, 58], [154, 122, 86, 64], [192, 152, 108, 84], [230, 180, 130, 98], [271, 213, 151, 119], [321, 251, 177, 137], [367, 287, 203, 155], [425, 331, 241, 177], [458, 362, 258, 194], [520, 412, 292, 220], [586, 450, 322, 250], [644, 504, 364, 280], [718, 560, 394, 310], [792, 624, 442, 338], [858, 666, 482, 382], [929, 711, 509, 403], [1003, 779, 565, 439], [1091, 857, 611, 461], [1171, 911, 661, 511], [1273, 997, 715, 535], [1367, 1059, 751, 593], [1465, 1125, 805, 625], [1528, 1190, 868, 658], [1628, 1264, 908, 698], [1732, 1370, 982, 742], [1840, 1452, 1030, 790], [1952, 1538, 1112, 842], [2068, 1628, 1168, 898], [2188, 1722, 1228, 958], [2303, 1809, 1283, 983], [2431, 1911, 1351, 1051], [2563, 1989, 1423, 1093], [2699, 2099, 1499, 1139], [2809, 2213, 1579, 1219], [2953, 2331, 1663, 1273]],
        o = function () {
            var a = function (a, b) {
                this._el = a, this._htOption = b
            };
            return a.prototype.draw = function (a) {
                function g(a, b) {
                    var c = document.createElementNS("http://www.w3.org/2000/svg", a);
                    for (var d in b) b.hasOwnProperty(d) && c.setAttribute(d, b[d]);
                    return c
                }

                var b = this._htOption, c = this._el, d = a.getModuleCount();
                Math.floor(b.width / d), Math.floor(b.height / d), this.clear();
                var h = g("svg", {
                    viewBox: "0 0 " + String(d) + " " + String(d),
                    width: "100%",
                    height: "100%",
                    fill: b.colorLight
                });
                h.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xlink", "http://www.w3.org/1999/xlink"), c.appendChild(h), h.appendChild(g("rect", {
                    fill: b.colorDark,
                    width: "1",
                    height: "1",
                    id: "template"
                }));
                for (var i = 0; d > i; i++) for (var j = 0; d > j; j++) if (a.isDark(i, j)) {
                    var k = g("use", {x: String(i), y: String(j)});
                    k.setAttributeNS("http://www.w3.org/1999/xlink", "href", "#template"), h.appendChild(k)
                }
            }, a.prototype.clear = function () {
                for (; this._el.hasChildNodes();) this._el.removeChild(this._el.lastChild)
            }, a
        }(), p = "svg" === document.documentElement.tagName.toLowerCase(), q = p ? o : m() ? function () {
            function a() {
                this._elImage.src = this._elCanvas.toDataURL("image/png"), this._elImage.style.display = "block", this._elCanvas.style.display = "none"
            }

            function d(a, b) {
                var c = this;
                if (c._fFail = b, c._fSuccess = a, null === c._bSupportDataURI) {
                    var d = document.createElement("img"), e = function () {
                        c._bSupportDataURI = !1, c._fFail && _fFail.call(c)
                    }, f = function () {
                        c._bSupportDataURI = !0, c._fSuccess && c._fSuccess.call(c)
                    };
                    return d.onabort = e, d.onerror = e, d.onload = f, d.src = "data:image/gif;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==", void 0
                }
                c._bSupportDataURI === !0 && c._fSuccess ? c._fSuccess.call(c) : c._bSupportDataURI === !1 && c._fFail && c._fFail.call(c)
            }

            if (this._android && this._android <= 2.1) {
                var b = 1 / window.devicePixelRatio, c = CanvasRenderingContext2D.prototype.drawImage;
                CanvasRenderingContext2D.prototype.drawImage = function (a, d, e, f, g, h, i, j) {
                    if ("nodeName" in a && /img/i.test(a.nodeName)) for (var l = arguments.length - 1; l >= 1; l--) arguments[l] = arguments[l] * b; else "undefined" == typeof j && (arguments[1] *= b, arguments[2] *= b, arguments[3] *= b, arguments[4] *= b);
                    c.apply(this, arguments)
                }
            }
            var e = function (a, b) {
                this._bIsPainted = !1, this._android = n(), this._htOption = b, this._elCanvas = document.createElement("canvas"), this._elCanvas.width = b.width, this._elCanvas.height = b.height, a.appendChild(this._elCanvas), this._el = a, this._oContext = this._elCanvas.getContext("2d"), this._bIsPainted = !1, this._elImage = document.createElement("img"), this._elImage.style.display = "none", this._el.appendChild(this._elImage), this._bSupportDataURI = null
            };
            return e.prototype.draw = function (a) {
                var b = this._elImage, c = this._oContext, d = this._htOption, e = a.getModuleCount(), f = d.width / e,
                    g = d.height / e, h = Math.round(f), i = Math.round(g);
                b.style.display = "none", this.clear();
                for (var j = 0; e > j; j++) for (var k = 0; e > k; k++) {
                    var l = a.isDark(j, k), m = k * f, n = j * g;
                    c.strokeStyle = l ? d.colorDark : d.colorLight, c.lineWidth = 1, c.fillStyle = l ? d.colorDark : d.colorLight, c.fillRect(m, n, f, g), c.strokeRect(Math.floor(m) + .5, Math.floor(n) + .5, h, i), c.strokeRect(Math.ceil(m) - .5, Math.ceil(n) - .5, h, i)
                }
                this._bIsPainted = !0
            }, e.prototype.makeImage = function () {
                this._bIsPainted && d.call(this, a)
            }, e.prototype.isPainted = function () {
                return this._bIsPainted
            }, e.prototype.clear = function () {
                this._oContext.clearRect(0, 0, this._elCanvas.width, this._elCanvas.height), this._bIsPainted = !1
            }, e.prototype.round = function (a) {
                return a ? Math.floor(1e3 * a) / 1e3 : a
            }, e
        }() : function () {
            var a = function (a, b) {
                this._el = a, this._htOption = b
            };
            return a.prototype.draw = function (a) {
                for (var b = this._htOption, c = this._el, d = a.getModuleCount(), e = Math.floor(b.width / d), f = Math.floor(b.height / d), g = ['<table style="border:0;border-collapse:collapse;">'], h = 0; d > h; h++) {
                    g.push("<tr>");
                    for (var i = 0; d > i; i++) g.push('<td style="border:0;border-collapse:collapse;padding:0;margin:0;width:' + e + "px;height:" + f + "px;background-color:" + (a.isDark(h, i) ? b.colorDark : b.colorLight) + ';"></td>');
                    g.push("</tr>")
                }
                g.push("</table>"), c.innerHTML = g.join("");
                var j = c.childNodes[0], k = (b.width - j.offsetWidth) / 2, l = (b.height - j.offsetHeight) / 2;
                k > 0 && l > 0 && (j.style.margin = l + "px " + k + "px")
            }, a.prototype.clear = function () {
                this._el.innerHTML = ""
            }, a
        }();
    QRCode = function (a, b) {
        if (this._htOption = {
            width: 256,
            height: 256,
            typeNumber: 4,
            colorDark: "#000000",
            colorLight: "#ffffff",
            correctLevel: d.H
        }, "string" == typeof b && (b = {text: b}), b) for (var c in b) this._htOption[c] = b[c];
        "string" == typeof a && (a = document.getElementById(a)), this._android = n(), this._el = a, this._oQRCode = null, this._oDrawing = new q(this._el, this._htOption), this._htOption.text && this.makeCode(this._htOption.text)
    }, QRCode.prototype.makeCode = function (a) {
        this._oQRCode = new b(r(a, this._htOption.correctLevel), this._htOption.correctLevel), this._oQRCode.addData(a), this._oQRCode.make(), this._el.title = a, this._oDrawing.draw(this._oQRCode), this.makeImage()
    }, QRCode.prototype.makeImage = function () {
        "function" == typeof this._oDrawing.makeImage && (!this._android || this._android >= 3) && this._oDrawing.makeImage()
    }, QRCode.prototype.clear = function () {
        this._oDrawing.clear()
    }, QRCode.CorrectLevel = d
}();
;(function (doc) {

    var loadingHideen = function () {

            query.$('#ball').style.display = 'none';

            var gameIframe = query.$('#h5_ifream');

            if (gameIframe == null) return;

            if (gameIframe.onload) {

                gameIframe.onload = function () {

                    query.$('.loading_wap').style.display = 'none';

                };

            } else {

                setTimeout(function () {

                    query.$('.loading_wap').style.display = 'none';

                }, 1000);

            }

        }(),

        getCookie = function (name) {

            var arr, reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");

            if (arr = document.cookie.match(reg))

                return unescape(arr[2]);

            else

                return null;
        },
        setCookie = function (name, value) {
            var Days = 30;
            var exp = new Date();
            exp.setTime(exp.getTime() + Days * 24 * 60 * 60 * 1000);
            document.cookie = name + "=" + escape(value) + ";expires=" + exp.toGMTString();
        },
        pageLading = function () {

            var ballFun = function () {

                    var h5_ball = query.$('#ball');

                    if (IsPC() && window.parent.position == 'vertically') {//1.判断是否pc端 2.判断有无父页面 3.父页面 position == 'vertically' 是横屏游戏
                        h5_ball.style.display = 'none';
                        return;
                    } else {
                        h5_ball.style.display = 'block';
                    }

                    var ballHidden = function () {
                            query.addClass(doc.body, 'ballHidden');
                        },
                        HiddenBall = function () {
                            h5_ball.style.display = 'none';
                        },
                        ballShow = function () {
                            query.removeClass(doc.body, 'ballHidden');
                        },

                        ballCodeShow = function (code) {
                            var ele = query.$('.ball_jhmtc');
                            ballShow();
                            ele.style.display = 'block';
                            query.$('.jhm', ele).innerHTML = code;
                        },

                        clickEvent = function (event) {

                            var target = event.target;

                            if (target.className == 'ball_message' || (target.nodeName.toLowerCase() == 'span' && target.parentNode.id == 'ball')) {//领取礼包

                                ballHidden();

                                query.$('.ball_message').style.display = 'none';

                                if (!getCookie('userData_uid' + userData.uid)) setCookie('userData_uid' + userData.uid, 1);

                                return;
                            }


                            if (target.className == 'ball_jhmtc' || target.className == 'jhm' || (target.nodeName.toLowerCase() == 'b' && target.parentNode.className == 'ball_jhmtc')) {

                                query.$('.ball_jhmtc').style.display = 'none';

                                return;
                            }

                        };


                    h5_ball.addEventListener('click', clickEvent, false);


                    new Inertia(document.getElementById('ball'));

                    return {
                        HiddenBall: HiddenBall,
                        ballCodeShow: ballCodeShow,
                        ballShow: ballShow
                    };

                }(),

                copyToClipboard = function (target) { //点击复制

                    var ele = target.parentNode.getElementsByTagName('input')[0],

                        txt = ele.value;

                    if (window.clipboardData) {

                        window.clipboardData.clearData();

                        window.clipboardData.setData("Text", txt);

                        messageShow.show('复制成功!');


                    } else {

                        try {

                            ele.select();

                            doc.execCommand('Copy');

                            messageShow.show('复制成功!');


                        } catch (err) {

                            popshow.show("请手动进行复制");

                        }

                    }
                },

                popBox = function () {

                    var showMessage = function () {//错误弹框

                            var sdkb_popmask = query.$('.sdkb_popmask'),

                                clickEvent = function (event) {

                                    var target = event.target;


                                    if (target.className == 'back_clickme') {

                                        hidden();

                                        ballFun.ballCodeShow(target.parentNode.getElementsByTagName('input')[0].value);

                                    }


                                    if (target.className == 'clo' || target.className.indexOf('sdkb_popmask') > -1) {

                                        hidden();

                                    }


                                    if (target.className == 'code_copy') {

                                        copyToClipboard(target);

                                        hidden();

                                    }

                                },

                                hidden = function () {

                                    sdkb_popmask.style.display = 'none';

                                },


                                showCode = function (code, usage) {

                                    sdkb_popmask.style.display = 'block';

                                    var str = '	<p>(复制兑换码，去游戏中使用)</p><b class="back_clickme">若兑换码无法粘贴，请回来点我</b><span class="code_copy">复制</span>';

                                    if (!!navigator.userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/)) {

                                        str = '<p>请长按礼包码复制</p><b class="back_clickme">或者点击此处</b>';

                                    }

                                    sdkb_popmask.innerHTML = ['<div class="sdkb_pop loginmod">',
                                        '	<i class="clo"></i>',
                                        '	<h2>' + usage + '</h2>',
                                        '	<div class="gift_code">兑换码:<input value="' + code + '" readonly></div>',
                                        str,
                                        '</div>'].join('');


                                    var ele = sdkb_popmask.firstElementChild;

                                    ele.style.marginTop = -ele.offsetHeight / 2 + 'px';

                                };


                            sdkb_popmask.addEventListener('click', clickEvent, false);


                            return {

                                showCode: showCode

                            };

                        }(),
                        sdkGiftlist = function () {

                            var giftWrap = query.$('.sp_giftlist'),


                                insertGift = function (data) {

                                    var _data = data.data,

                                        str = '',

                                        str1 = '',

                                        lens = _data.length;


                                    if (!lens || getCookie('userData_uid' + userData.uid)) {

                                        query.$('.ball_message').style.display = 'none';

                                    }


                                    if (_data != '') {


                                        str1 = '<li class="gift_usemod"><i>领取方式：</i>' + _data[0].usage + '</li>';


                                    }


                                    _data.forEach(function (item) {

                                        var tempStr = '<span class="nogift">已领</span>';

                                        if (item.code == '') {

                                            if (item.left) {

                                                tempStr = '<p class="no_get">兑换码：</p><span class="gitgift"  v="' + item.id + '||' + item.name + '||' + item.left + '||' + item.content + '">领取</span>';

                                            }


                                        } else {

                                            tempStr = '<p class="gift_dhm">兑换码：' + item.code + '</p><span class="seegift" v="' + item.code + '">查看</span>';

                                        }


                                        str += '<li class="gift_giftmod"><div class="spg_gifttit"><h3>' + item.name + '</h3><b>（剩余' + item.left + '）</b></div><p>礼包内容：' + item.content + '</p>' + tempStr + '</li>';


                                    });

                                    giftWrap.innerHTML = str1 + str;


                                },


                                getGift = function (target) {

                                    var ary = target.getAttribute('v').split('||');


                                    if (target.state) return;

                                    target.state = true;

                                    ary[2]--;

                                    IO.jsonp(ajaxurl + 'getcdk.asp', {id: ary[0]}, function (data) {

                                        console.log(data);

                                        var _data = data.data;

                                        target.state = false;

                                        if (data.code == 200) {

                                            showMessage.showCode(data.card, ary[1]);

                                            target.parentNode.innerHTML = ['<div class="spg_gifttit">',
                                                '		<h3>' + ary[1] + '</h3>',
                                                '		<b>(剩余：' + ary[2] + ')</b>',
                                                '	</div>',
                                                '	<p>' + ary[3] + '</p>',
                                                '	<p class="gift_dhm">兑换码：' + data.card + '</p>',
                                                '	<span class="seegift" v="' + data.card + '">查看</span>'].join('');

                                            return;

                                        }

                                        popshow.show(data.message);

                                    });


                                },

                                clickEvent = function (event) {

                                    var target = event.target;

                                    if (target.className == 'gitgift') {//领取礼包


                                        getGift(target);

                                        return;
                                    }

                                    if (target.className == 'seegift') {

                                        showMessage.showCode(target.getAttribute('v'), target.parentNode.getElementsByTagName('h3')[0].innerHTML);

                                        return;
                                    }

                                };


                            giftWrap.addEventListener('click', clickEvent, false);

                            IO.jsonp(ajaxurl + 'coupon1.asp', {game_id: gid}, function (data) {

                                console.log(data);

                                if (data.code == 200) {

                                    insertGift(data);

                                    return;
                                }

                                popshow.show(data.message);


                            });

                        }(),

                        sdkNewslist = function () {

                            var wrap = query.$('.sp_news'),

                                newswrap = query.$('.sp_newslist'),

                                conWrap = query.$('.sp_article'),

                                bottom_btn_prev = query.$('.bottom_btn_prev'),

                                bottom_btn_next = query.$('.bottom_btn_next'),

                                page = 1,

                                insetHTML = function (data) {

                                    var str = '',

                                        btnStr = '<li class="no_more"><span>没有更多了</span></li>';

                                    if (page < data.allpage) {

                                        btnStr = '<li class="news_more_btn"><span>点击加载更多~</span></li>';

                                    }


                                    if (page == 1 && !data.data.length) {

                                        query.addClass(query.$('.sp_tab '), 'no_newslist');

                                        return;

                                    }


                                    if (newswrap.lastElementChild) newswrap.lastElementChild.parentNode.removeChild(newswrap.lastElementChild);

                                    data.data.forEach(function (item, index) {

                                        var itemId = item.id//item.link.split('=')[1];

                                        str += ['<li class="spnewslist" v="' + itemId + '">',
                                            '	<i>' + item.time + '</i><b>' + item.title + '</b>',
                                            '</li>'].join('');

                                    });

                                    newswrap.insertAdjacentHTML('beforeend', str + btnStr);

                                },

                                getData = function () {

                                    IO.jsonp(ajaxurl + 'homelist.asp', {
                                        type: 'notice',
                                        page: page,
                                        pagesize: 5,
                                        game_id: gid
                                    }, function (data) {

                                        if (data.code == 200) {


                                            insetHTML(data);


                                            return;

                                        }

                                        popshow.show(data.message);


                                    });

                                },

                                showArtical = function (newsid) {

                                    query.addClass(wrap, 'pageShow');

                                    IO.jsonp(ajaxurl + 'article.asp', {id: newsid}, function (data) {

                                        var _data = data.data[0];

                                        if (data.code == 200) {

                                            conWrap.innerHTML = '<h2 class="sp_title">' + _data.title + '</h2><b class="sp_time">' + _data.time + '</b><div class="sp_message">' + _data.content + '</div>';


                                            if (_data.next_id == '') {

                                                bottom_btn_prev.style.display = 'none';

                                            } else {

                                                bottom_btn_prev.style.display = 'block';

                                                bottom_btn_prev.setAttribute('v', _data.next_id);
                                            }

                                            if (_data.prev_id == '') {

                                                bottom_btn_next.style.display = 'none';

                                            } else {

                                                bottom_btn_next.style.display = 'block';

                                                bottom_btn_next.setAttribute('v', _data.prev_id);

                                            }

                                            return;
                                        }
                                        popshow.show(data.message);


                                    });

                                },

                                clickEvent = function (event) {

                                    var target = event.target;

                                    if (target.className.toLowerCase() == 'spnewslist') {

                                        showArtical(target.getAttribute('v'));

                                        return;

                                    }

                                    if (target.nodeName.toLowerCase() == 'i' && target.parentNode.className.toLowerCase() == 'spnewslist') {

                                        showArtical(target.parentNode.getAttribute('v'));

                                        return;

                                    }

                                    if (target.nodeName.toLowerCase() == 'b' && target.parentNode.className.toLowerCase() == 'spnewslist') {

                                        showArtical(target.parentNode.getAttribute('v'));

                                        return;

                                    }


                                    if (target.className == 'bottom_goback') {

                                        query.removeClass(wrap, 'pageShow');

                                        return;

                                    }


                                    if (target.className == 'bottom_btn_next') {

                                        showArtical(target.getAttribute('v'));

                                        return;

                                    }

                                    if (target.className == 'bottom_btn_prev') {

                                        showArtical(target.getAttribute('v'));

                                        return;

                                    }


                                    if (target.className == 'news_more_btn' || (target.nodeName.toLowerCase() == 'span' || target.parentNode.className == 'news_more_btn')) {//新闻列表点击加载更多

                                        page++;

                                        getData();

                                        return;

                                    }

                                };


                            wrap.addEventListener('click', clickEvent, false);

                            getData();

                        }(),


                        sdkGmaelist = function () {

                            var inserHotGame = function (data) {

                                    var str = '';

                                    data.data.forEach(function (item) {

                                        var tempStr = '';

                                        if (item.gift) {
                                            tempStr = '<b class="spg_gift">礼包</b>';
                                        }


                                        if (item.hot) {
                                            tempStr += '<b class="spg_hot">热门</b>';
                                        }


                                        str += ['<li>',
                                            '	<img src="' + item.icon + '">',
                                            '	<div class="game_left">',
                                            '		<div class="game_tit">',
                                            '			<h3>' + item.name + '</h3>',
                                            tempStr,
                                            '		</div>',
                                            '		<p>' + item.content + '</p>',
                                            '	</div>',
                                            '	<a class="spgbk_start" target="_top" href="' + item.link_startgame + '">开始</a>',
                                            '</li>'].join('');

                                    });

                                    query.$('.spg_bklist').innerHTML = str;

                                },

                                inserNewGame = function (data) {

                                    var str = '';

                                    data.data.forEach(function (item, index) {

                                        var tempStr = '';

                                        if (item.gift) {
                                            tempStr = '<b class="spg_gift">礼包</b>';
                                        }


                                        if (item.hot) {

                                            tempStr += '<b class="spg_hot">热门</b>';
                                        }

                                        str += ['<li>',


                                            '	<img src="' + item.icon + '">',
                                            '	<div class="game_left">',
                                            '		<div class="game_tit">',
                                            '			<h3>' + item.name + '</h3>',
                                            tempStr,
                                            '		</div>',
                                            '		<p>' + item.content + '</p>',
                                            '	</div>',
                                            '	<a class="spgbk_start" target="_top" href="' + item.link_startgame + '">开始</a>',
                                            '</li>'].join('');

                                    });

                                    query.$('.spg_bklistn').innerHTML = str;

                                };


                            IO.jsonp(ajaxurl + 'homelist.asp', {type: 'hot', page: 1, pagesize: 3}, function (data) {


                                if (data.code == 200) {

                                    inserHotGame(data);

                                    return;

                                }

                                popshow.show(data.message);


                            });

                            IO.jsonp(ajaxurl + 'homelist.asp', {type: 'new', page: 1, pagesize: 6}, function (data) {


                                if (data.code == 200) {


                                    inserNewGame(data);


                                    return;
                                }

                                popshow.show(data.message);


                            });
                        },
                        sdkKaifulist = function () {

                            var inserNewzone = function (data) {
                                    var str = '';
                                    var tempStr = '';
                                    tempStr += ['<table class="table normal_table">',
                                        '<tbody><tr class="odd">',
                                        '<th>开服时间</th>',
                                        '<th>服务器名</th>',
                                        '<th>开服福利</th>',
                                        '<th>开服状态</th>',
                                        '</tr>'].join('');

                                    data.data.forEach(function (item) {
                                        str += ['<tr>',
                                            '<td>' + item.opendate + "&nbsp" + item.opentime + '</td>',
                                            '<td>' + item.zone + '</td>',
                                            '<td>免费首冲</td>',
                                            '<td>未开服</td>',
                                            '</tr>'].join('');

                                    });


                                    query.$('.spg_kflistn').innerHTML = tempStr + str + '</tbody></table>';

                                },

                                inserOpenZone = function (data) {

                                    var str = '';
                                    var tempStr = '';
                                    tempStr += ['<table class="table normal_table">',
                                        '<tbody><tr class="odd">',
                                        '<th>开服时间</th>',
                                        '<th>服务器名</th>',
                                        '<th>开服福利</th>',
                                        '<th>开服状态</th>',
                                        '</tr>'].join('');

                                    data.data.forEach(function (item) {
                                        str += ['<tr>',
                                            '<td>' + item.opendate + "&nbsp" + item.opentime + '</td>',

                                            '<td>' + item.zone + '</td>',
                                            '<td>免费首冲</td>',
                                            '<td>已开服</td>',
                                            '</tr>'].join('');

                                    });
                                    query.$('.spg_kflist').innerHTML = tempStr + str + '</tbody></table>';

                                };


                            IO.jsonp(ajaxurl + 'gamezone.asp', {
                                type: 'new',
                                game_id: gid,
                                pagesize: 6
                            }, function (data) {


                                if (data.code == 200) {

                                    inserNewzone(data);
                                    return;

                                }

                                popshow.show(data.message);


                            });

                            IO.jsonp(ajaxurl + 'gamezone.asp', {
                                type: 'open',
                                game_id: gid,
                                pagesize: 6
                            }, function (data) {


                                if (data.code == 200) {

                                    inserOpenZone(data);
                                    return;
                                }

                                popshow.show(data.message);


                            });
                        },
                        sdkKfWrap = function () {

                            var wrap = query.$('.sp_kf'),

                                clickEvent = function (event) {

                                    var target = event.target;


                                    if (target.className == 'spkf_copy') {

                                        copyToClipboard(target);

                                        return;

                                    }
                                    if (target.className == 'spkf_qq') {

                                        window.location.href = ajaxurl + 'cs.asp';

                                        return;


                                    }
                                    if (target.className == 'spkf_zz') {

                                        window.location.href = ajaxurl + 'msg.asp?action=qa_post';

                                        return;

                                    }
                                    if (target.className == 'spkf_my') {

                                        window.location.href = ajaxurl + 'msg.asp?action=qa_my';

                                        return;

                                    }
                                };


                            wrap.addEventListener('click', clickEvent, false);

                            if (!!navigator.userAgent.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/)) {

                                var spkfcopy = query.$('.spkf_copy');

                                query.addClass(spkfcopy, 'spkfcopy_ios');

                                spkfcopy.innerHTML = '请长按微信号复制';

                            }


                        }(),


                        popTab = function (target) {

                            var btn = Array.prototype.slice.call(target.parentNode.children),

                                childrenEle = Array.prototype.slice.call(query.$('.sdk_listbox').children);


                            btn.forEach(function (item, index) {

                                if (target == item) {

                                    query.addClass(item, 'cur');

                                    query.addClass(childrenEle[index], 'cur');

                                    if (index == 2 && !item.state) {

                                        sdkGmaelist();

                                        item.state = true;

                                    } else if (index == 3 && !item.state) {


                                        sdkKaifulist();

                                        item.state = true;

                                    }


                                } else {

                                    query.removeClass(item, 'cur');

                                    query.removeClass(childrenEle[index], 'cur');

                                }

                            });

                        },
                        clickEvent = function (event) {

                            var target = event.target;

                            if (target.className == 'sdk_closebtn') {

                                ballFun.ballShow();

                                return;
                            }
                            if (target.className == 'ball_closebtn') {
                                ballFun.ballShow();
                                ballFun.HiddenBall();
                                return;
                            }

                            if (target.className == 'sdk_resetbtn') {//刷新页面

                                doc.location.reload(true);

                                return;

                            }

                            if (target.className == 'sdk_paybtn') {

                                query.$('.s-pay').href = ajaxurl + 'pay.asp';

                                return;

                            }

                            if (target.className == 'sdk_sharebtn') {

                                shareCallbackSend();
                                popshow.show('游戏里可以领取分享礼包了！');
                                return;

                            }
                            if (target.className == 'sdk_xhbtn') {

                                login.openXHlist();
                                //popshow.show('本功能近期开放！');
                                return;

                            }
                            if (target.nodeName.toLowerCase() == 'a' && target.parentNode.className.indexOf('sp_tab') > -1) {

                                popTab(target);

                                return;

                            }


                            if (target.className == 'spt_collection') {//点击收藏


                                popTab(query.$('.spt_kf'));

                                return;

                            }

                            if (target.className == 'spt_account') {//切换账号


                                login.santanglogin();

                                return;
                            }
                            if (target.className == 'sp_user') {//切换账号
                                if (isSW) {
                                    login.testsign();
                                } else {
                                    query.$('.s-avatar').href = ajaxurl + 'user.asp';

                                }
                                return;
                            }


                        };


                    h5_wap.addEventListener('click', clickEvent, false);

                    query.$('.sp_user').src = userData.avatar;

                    query.$('.sp_user_name').innerHTML = userData.nickname;
                    query.$('.sp_uid').innerHTML = "UID:" + userData.username;
                    if (userData.username.indexOf('@st') > -1) {//试玩
                        isSW = true;
                    } else {
                        isSW = false;
                    }

                }(),

                historyState = function () {//返回弹窗

                    var win = window,

                        setCookie = function (name, value) {


                            var argv = setCookie.arguments;
                            var argc = setCookie.arguments.length;
                            var expires = (argc > 2) ? argv[2] : null;
                            if (expires != null) {
                                var LargeExpDate = new Date();
                                LargeExpDate.setTime(LargeExpDate.getTime() + (expires * 1000 * 3600 * 24));
                            }
                            document.cookie = name + "=" + escape(value) + ((expires == null) ? "" : ("; expires=" + LargeExpDate.toGMTString()));
                        },

                        getCookie = function (name) {
                            var search = name + "="
                            if (document.cookie.length > 0) {
                                offset = document.cookie.indexOf(search)
                                if (offset != -1) {
                                    offset += search.length
                                    end = document.cookie.indexOf(";", offset)
                                    if (end == -1) end = document.cookie.length
                                    return unescape(document.cookie.substring(offset, end))
                                } else return ""
                            }
                        },

                        deleteCookie = function () {
                            var expdate = new Date();
                            expdate.setTime(expdate.getTime() - (86400 * 1000 * 1));
                            setCookie(name, "", expdate);
                        },

                        createState = function () {//进入页面创建history State

                            if (getCookie('h5_isdonremove')) return;

                            if (win.history.state) return;//已经存在就返回，防止刷新页面重复添加多条history记录

                            win.history.pushState({page: 'history'}, "history", win.location.href);

                        }(),

                        popWrap = function () {

                            var ele = function () {

                                    var div = document.createElement('div');

                                    div.className = 'leavepop';

                                    return document.body.appendChild(div);

                                }(),


                                show = function () {
                                    IO.jsonp(ajaxurl + 'leavegame.asp', {channel_id: 50, count: 3}, function (data) {

                                        var str = '';

                                        if (data.code == 200) {

                                            data.data.forEach(function (item) {

                                                str += '<a target="_top" href="' + item.link_startgame + '"><img src="' + item.icon + '"><i>' + item.name + '</i></a>';

                                            });
                                            str += '<a target="_top" href="' + ajaxurl + 'index.asp"><img src="' + baseurl + 'gamesimg/20184863585659.png"><i>更多游戏</i></a>';
                                            ele.innerHTML =

                                                ['<div class="leaveout">',
                                                    '	<i class="clo"></i>',
                                                    '	<h2>更多游戏尽在三唐游戏<br><font color="#CC6600" style="font-size:0.24rem">搜索微信公众号3tang</font></h2>',
                                                    '	<div class="leav_game_list">',
                                                    str,
                                                    '	</div>',
                                                    '	<a class="shure_back" target="_top" href="' + baseurl + 'downloadapp.asp?id=wyb">下载微端</a>',
                                                    '<div><font color="#CC6600" style="font-size:0.24rem">仍要推出游戏请退回上一步或关闭窗口</div></font>',
                                                    '	<div class="dont_repeat"><b></b>今日不再提醒</div>',

                                                    '</div>'].join('');

                                            ele.style.display = 'block';

                                            bottomTap.show && bottomTap.show();

                                        }

                                    });

                                },

                                hide = function () {

                                    ele.innerHTML = '';

                                    ele.style.display = 'none';
                                },

                                clickEvent = function (event) {

                                    var target = event.target;

                                    if (target.className == 'clo') {

                                        hide();

                                        win.history.pushState({page: 'history'}, "history", win.location.href);

                                    }

                                    if (target.className == 'shure_back') {

                                        if (doc.referrer === '') {

                                            event.preventDefault();

                                            window.history.go(-1);

                                            return;

                                        }

                                    }


                                    if (target.className == 'dont_repeat') {

                                        if (target.children[0].className == 'cur') {

                                            target.children[0].className = '';

                                            deleteCookie('h5_isdonremove');

                                        } else {

                                            target.children[0].className = 'cur';

                                            setCookie('h5_isdonremove', 'dont');
                                        }

                                    }


                                    if (target.nodeName.toLowerCase() == 'b' && target.parentNode.className == 'dont_repeat') {

                                        if (target.className == 'cur') {

                                            target.className = '';

                                            deleteCookie('h5_isdonremove');

                                        } else {

                                            target.className = 'cur';

                                            setCookie('h5_isdonremove', 'dont');

                                        }

                                    }

                                };


                            ele.addEventListener('click', clickEvent, false);

                            return {

                                show: show
                            }
                        }();

                    if (typeof miniClient == 'undefined') {
                        window.onpopstate = function (event) {
                            popWrap.show();
                        };
                    }

                }(),

                payFunction = function () {

                    var messageCallback = function () {


                        var params = '',
                            tangcoin = 0,
                            goodsmoney = 0,
                            paypop = query.$('.paypop'),
                            createEle = function () {

                                // paypop = doc.createElement('div');

                                // paypop.className = 'paypop';

                                // paypop.id = 'paypop';

                                // h5_wap.appendChild(paypop);

                            }(),
                            popHide = function () {

                                paypop.style.display = 'none';
                            },
                            pcPay = function () {
                                console.log("pcPay");
                                var show = function (money) {

                                        paypop.style.display = 'block';


                                        if (isWeiXin()) {

                                            paypop.innerHTML = ['<div class="pay cur">',
                                                '<p>订单价格：&#65509;<em>' + money + '</em>元</p>',
                                                '<div id="pay_tangcoin_num" class="pay-mod" style="display: block;">',
                                                '<div class="li_last_end">',
                                                '<label for="pay_case" class="frs"><input id="stangpay" class="ios7CBox" type="checkbox"></label>',
                                                '使用<input style="color:#FF0000; font-size:0.2rem" class="form-control ptbinpt" id="tangcoinNumber" onbeforepaste="clipboardData.setData(\'text\',clipboardData.getData(\'text\').replace(/[^\d]/g,\'\'))" pattern="[0-9]*" placeholder="0" maxlength="6" type="text"><span>通用唐币</span> ,余额 <i></i><span style="color:#FF0000; font-size:0.2rem">' + tangcoin + '</span>元</div></div>',
                                                '<div id="pay_btangcoin_num" class="pay-mod" style="display: block;">',
                                                '<div class="li_last_end"> ',
                                                '<label for="pay_btangcoin" class="frs"><input id="bstangpay" class="ios7CBox" type="checkbox"></label>',
                                                '使用<input style="color:#FF0000; font-size:0.2rem" class="form-control ptbinpt" id="btangcoinNumber" onbeforepaste="clipboardData.setData(\'text\',clipboardData.getData(\'text\').replace(/[^\d]/g,\'\'))" pattern="[0-9]*" placeholder="0" maxlength="6" type="text"><span>专用唐币</span> ,余额 <i></i><span style="color:#FF0000; font-size:0.2rem">' + btangcoin + '</span>元</div></div>',
                                                '	<p>选择支付方式<em id="paycash" style="color:#FF0000;float: right;" >支付现金：&#65509;' + money + '元</em></p>',
                                                '	<div id="pay_type">',
                                                // '	<a  class="alipay"></a>',
                                                '	<a class="weixinpay"></a>',
                                                '	</div>',
                                                '</div>'].join('');

                                            return;

                                        }

                                        paypop.innerHTML = ['<div class="pay cur">',
                                            '	<p>订单价格：&#65509;<em>' + money + '</em>元</p>',


                                            '<div id="pay_tangcoin_num" class="pay-mod" style="display: block;">',
                                            '<div class="li_last_end"> <label for="pay_case" class="frs">',
                                            '<input id="stangpay" class="ios7CBox" type="checkbox"></label>',
                                            '使用<input style="color:#FF0000; font-size:0.2rem" class="form-control ptbinpt" id="tangcoinNumber" onbeforepaste="clipboardData.setData(\'text\',clipboardData.getData(\'text\').replace(/[^\d]/g,\'\'))" pattern="[0-9]*" placeholder="0" maxlength="6" type="text"><span>通用唐币</span> ,余额 <i></i><span style="color:#FF0000; font-size:0.2rem">' + tangcoin + '</span>元</div></div>',
                                            '<div id="pay_btangcoin_num" class="pay-mod" style="display: block;">',
                                            '<div class="li_last_end"> ',
                                            '<label for="pay_btangcoin" class="frs"><input id="bstangpay" class="ios7CBox" type="checkbox"></label>',
                                            '使用<input style="color:#FF0000; font-size:0.2rem" class="form-control ptbinpt" id="btangcoinNumber" onbeforepaste="clipboardData.setData(\'text\',clipboardData.getData(\'text\').replace(/[^\d]/g,\'\'))" pattern="[0-9]*" placeholder="0" maxlength="6" type="text"><span>专用唐币</span> ,余额 <i></i><span style="color:#FF0000; font-size:0.2rem">' + btangcoin + '</span>元</div></div>',
                                            '<p>选择支付方式 <em id="paycash" style="color:#FF0000;float: right;" >支付现金：&#65509;' + money + '元</em></p>',
                                            '	<div id="pay_type">',

                                            '	<a  class="alipay"></a>',
                                            '	<a class="weixinpay"></a>',
                                            '	</div>',
                                            '</div>'].join('');

                                    },

                                    payisok = function (sn) {


                                        var createcode = function () {

                                                IO.jsonp(ajaxurl + 'payment/query.asp?id=' + sn, function (data) {
                                                    if (data.code == 200) {
                                                        clearInterval(nIntervId);
                                                        popshow.show('充值成功');
                                                        popHide();
                                                        document.getElementById('h5_ifream').contentWindow.postMessage('payOK', '*');
                                                    }

                                                });

                                            },
                                            nIntervId = setInterval(createcode, 5000);


                                    },
                                    payTangcoin_isok = function (sn) {

                                        IO.jsonp(baseurl + 'pay.asp?id=' + sn + '&from=wap', function (data) {

                                            if (data.code == 200) {
                                                IO.jsonp(ajaxurl + 'payment/query.asp?id=' + sn, function (data) {

                                                    if (data.code == 200) {
                                                        popshow.show('充值成功');
                                                        popHide();
                                                        document.getElementById('h5_ifream').contentWindow.postMessage('payOK', '*');

                                                    } else {

                                                        messageShow.show(data.msg);
                                                    }
                                                });

                                            } else {
                                                messageShow.show(data.message);

                                            }

                                        });
                                    },
                                    stangpay = function (target) {
                                        if (target.state) return;

                                        target.state = true;
                                        if (query.$("#tangcoinNumber").value > 0) {
                                            params = params + "&ptb_money=" + query.$("#tangcoinNumber").value;
                                        }
                                        if (query.$("#btangcoinNumber").value > 0) {
                                            params = params + "&bptb_money=" + query.$("#btangcoinNumber").value;
                                        }
                                        IO.jsonp(ajaxurl + 'payment/order.asp?' + params + '&method=3tang', function (data) {
                                            target.state = false;
                                            if (data.code == 200) {
                                                paypop.innerHTML =
                                                    ['<div class="stang_box">',
                                                        '<h2>使用唐币兑换<span id="tang_orderid" style="display: none;">' + data.data.sn + '</span></h2>',
                                                        '<div class="stang_pic"></div>',
                                                        '<span id="tangpay_sub">确定支付</span>',
                                                        '</div>'].join('');

                                                var stangBox = query.$('.stang_box');

                                                stangBox.style.marginTop = -stangBox.offsetHeight / 2 + 'px';
                                                //payTangcoin_isok(data.data.sn);

                                            } else {
                                                messageShow.show(data.message);
                                            }


                                        });
                                    },

                                    alipay = function (target) {

                                        if (target.state) return;

                                        target.state = true;
                                        if (query.$("#tangcoinNumber").value > 0) {
                                            params = params + "&ptb_money=" + query.$("#tangcoinNumber").value;
                                        }
                                        if (query.$("#btangcoinNumber").value > 0) {
                                            params = params + "&bptb_money=" + query.$("#btangcoinNumber").value;
                                        }
                                        IO.jsonp(ajaxurl + 'payment/order.asp?' + params + '&method=alipay', function (data) {

                                            target.state = false;
                                            if (data.code == 200) {
                                                paypop.innerHTML =
                                                    ['<a class="alipay_box" href="' + baseurl + 'pay.asp?id=' + data.data.sn + '" target="_blank">',
                                                        '<h2>支付宝支付</h2>',
                                                        '<div class="alipay_pic"></div>',
                                                        '<span>确定支付</span>',
                                                        '</a>'].join('');

                                                var alipayBox = query.$('.alipay_box');

                                                alipayBox.style.marginTop = -alipayBox.offsetHeight / 2 + 'px';

                                                payisok(data.data.sn);

                                            } else {
                                                messageShow.show(data.message);
                                            }


                                        });
                                    },


                                    weixinpay = function (target) {

                                        if (target.state) return;

                                        target.state = true;
                                        if (query.$("#tangcoinNumber").value > 0) {
                                            params = params + "&ptb_money=" + query.$("#tangcoinNumber").value;
                                        }
                                        if (query.$("#btangcoinNumber").value > 0) {
                                            params = params + "&bptb_money=" + query.$("#btangcoinNumber").value;
                                        }
                                        IO.jsonp(ajaxurl + 'payment/order.asp?' + params + '&method=wechat', function (data) {

                                            target.state = false;
                                            if (data.code == 200) {
                                                paypop.innerHTML = ['<div class="weixinpay_box">',
                                                    '<h2>微信支付</h2>',
                                                    '<div id="qrcode"></div>',
                                                    '<p>请使用微信扫一扫<br>扫描二维码完成支付</p>',
                                                    '</div>'].join('');

                                                new QRCode('qrcode').makeCode(data.data.code);

                                                var weixinBox = query.$('.weixinpay_box');

                                                weixinBox.style.marginTop = -weixinBox.offsetHeight / 2 + 'px';


                                                payisok(data.data.sn);
                                            } else {
                                                messageShow.show(data.message);
                                            }

                                        });
                                    },

                                    clickEvent = function (event) {
                                        var target = event.target;

                                        if (target.id == 'stangpay') {
                                            console.log("stangpay")
                                            var tempbtangcoinNum = 0;
                                            /^[1-9]\d*$/.test(query.$("#btangcoinNumber").value) ? tempbtangcoinNum = parseInt(query.$("#btangcoinNumber").value) : tempbtangcoinNum = 0;
                                            if (query.$("#stangpay").checked) {
                                                if (tangcoin >= goodsmoney) {
                                                    query.$("#paycash").innerHTML = "支付现金：&#65509;0元";
                                                    query.$("#pay_type").style.display = "none";
                                                    query.$("#bstangpay").checked = false;
                                                    query.$("#btangcoinNumber").value = "";
                                                    query.$("#tangcoinNumber").value = goodsmoney;

                                                    stangpay(target);

                                                } else {

                                                    if ((tangcoin + tempbtangcoinNum) >= goodsmoney) {
                                                        query.$("#paycash").innerHTML = "支付现金：&#65509;0元";
                                                        query.$("#pay_type").style.display = "none";
                                                        query.$("#tangcoinNumber").value = tangcoin;
                                                        query.$("#btangcoinNumber").value = goodsmoney - tangcoin;
                                                        stangpay(target);

                                                    } else {
                                                        query.$("#paycash").innerHTML = "支付现金：&#65509;" + (goodsmoney - tangcoin - tempbtangcoinNum) + "元";
                                                        query.$("#pay_type").style.display = "block";
                                                        query.$("#tangcoinNumber").value = tangcoin;


                                                    }

                                                }

                                            } else {
                                                //检测绑定绑定唐币是否选中

                                                if (tempbtangcoinNum > goodsmoney) {
                                                    query.$("#paycash").innerHTML = "支付现金：&#65509;0元";
                                                    query.$("#pay_type").style.display = "none";
                                                    query.$("#tangcoinNumber").value = "";
                                                    query.$("#btangcoinNumber").value = goodsmoney;
                                                    stangpay(target);
                                                } else {
                                                    query.$("#paycash").innerHTML = "支付现金：&#65509;" + (goodsmoney - tempbtangcoinNum) + "元";
                                                    query.$("#pay_type").style.display = "block";
                                                    query.$("#tangcoinNumber").value = "";

                                                }


                                            }
                                            return;

                                        }

                                        if (target.id == 'bstangpay') {

                                            var temptangcoinNum = 0;
                                            /^[1-9]\d*$/.test(query.$("#tangcoinNumber").value) ? temptangcoinNum = parseInt(query.$("#tangcoinNumber").value) : temptangcoinNum = 0;

                                            if (query.$("#bstangpay").checked) {
                                                if (btangcoin >= goodsmoney) {
                                                    query.$("#paycash").innerHTML = "支付现金：&#65509;0元";
                                                    query.$("#pay_type").style.display = "none";
                                                    query.$("#stangpay").checked = false;
                                                    query.$("#tangcoinNumber").value = "";
                                                    query.$("#btangcoinNumber").value = goodsmoney;

                                                    stangpay(target);

                                                } else {
                                                    if ((btangcoin + temptangcoinNum) >= goodsmoney) {
                                                        query.$("#paycash").innerHTML = "支付现金：&#65509;0元";
                                                        query.$("#pay_type").style.display = "none";
                                                        query.$("#btangcoinNumber").value = btangcoin;
                                                        query.$("#tangcoinNumber").value = goodsmoney - btangcoin;
                                                        stangpay(target);

                                                    } else {
                                                        query.$("#paycash").innerHTML = "支付现金：&#65509;" + (goodsmoney - btangcoin - temptangcoinNum) + "元";

                                                        query.$("#pay_type").style.display = "block";
                                                        query.$("#btangcoinNumber").value = btangcoin;

                                                    }

                                                }

                                            } else {
                                                //检测通用唐币是否选中
                                                if (temptangcoinNum >= goodsmoney) {
                                                    query.$("#paycash").innerHTML = "支付现金：&#65509;0元";

                                                    query.$("#pay_type").style.display = "none";
                                                    query.$("#btangcoinNumber").value = "";
                                                    query.$("#tangcoinNumber").value = goodsmoney;
                                                    stangpay(target);
                                                } else {
                                                    query.$("#paycash").innerHTML = "支付现金：&#65509;" + (goodsmoney - temptangcoinNum) + "元"

                                                    query.$("#pay_type").style.display = "block";
                                                    query.$("#btangcoinNumber").value = "";

                                                }


                                            }
                                            return;

                                        }


                                        if (target.className == 'alipay') {

                                            alipay(target);

                                            return;

                                        }

                                        if (target.className == 'weixinpay') {

                                            weixinpay(target);

                                            return;

                                        }


                                        if (target.nodeName.toLowerCase() == 'h2' && target.parentNode.className == 'alipay_box') {

                                            popHide();

                                            return;

                                        }


                                        if (target.nodeName.toLowerCase() == 'span' && target.parentNode.className == 'alipay_box') {

                                            popHide();

                                            return;

                                        }

                                        if (target.className == 'paypop') {

                                            popHide();

                                            return;
                                        }


                                        if (target.className == 'alipay_pic' && target.className == 'alipay_box') {

                                            popHide();

                                            return;

                                        }
                                        if (target.id == 'tangpay_sub') {

                                            payTangcoin_isok(parseInt(query.$("#tang_orderid").innerHTML));

                                            return;

                                        }


                                    },
                                    KeyUpEvent = function (event) {
                                        console.log("pckeyup");
                                        var target = event.target;
                                        var pay_tangcoinNum = 0;
                                        /^[1-9]\d*$/.test(query.$("#tangcoinNumber").value) ? (parseInt(query.$("#tangcoinNumber").value) > tangcoin ? query.$("#tangcoinNumber").value = tangcoin : pay_tangcoinNum = parseInt(query.$("#tangcoinNumber").value)) : pay_tangcoinNum = 0;
                                        console.log("pay_tangcoinNum:" + pay_tangcoinNum);
                                        console.log("btangcoin:" + btangcoin);

                                        var pay_btangcoinNum = 0;
                                        /^[1-9]\d*$/.test(query.$("#btangcoinNumber").value) ? (parseInt(query.$("#btangcoinNumber").value) > btangcoin ? query.$("#btangcoinNumber").value = btangcoin : pay_btangcoinNum = parseInt(query.$("#btangcoinNumber").value)) : pay_btangcoinNum = 0;
                                        console.log("pay_btangcoinNum:" + pay_btangcoinNum);
                                        pay_tangcoinNum ? query.$("#stangpay").checked = true : (query.$("#stangpay").checked = false, query.$("#tangcoinNumber").value = "");
                                        pay_btangcoinNum ? query.$("#bstangpay").checked = true : (query.$("#bstangpay").checked = false, query.$("#btangcoinNumber").value = "");


                                        if ((pay_tangcoinNum + pay_btangcoinNum) >= goodsmoney) {
                                            if (pay_tangcoinNum >= goodsmoney) {
                                                query.$("#tangcoinNumber").value = goodsmoney;
                                                query.$("#btangcoinNumber").value = 0;
                                                query.$("#pay_type").style.display = "none";
                                                query.$("#paycash").innerHTML = "支付现金：&#65509;0元";
                                                stangpay(target);
                                                return;
                                            } else if (pay_btangcoinNum >= goodsmoney) {
                                                query.$("#btangcoinNumber").value = goodsmoney;
                                                query.$("#tangcoinNumber").value = 0;
                                                query.$("#pay_type").style.display = "none";
                                                query.$("#paycash").innerHTML = "支付现金：&#65509;0元";
                                                stangpay(target);
                                                return;
                                            } else {
                                                query.$("#pay_type").style.display = "none";
                                                query.$("#tangcoinNumber").value = pay_tangcoinNum;
                                                query.$("#btangcoinNumber").value = goodsmoney - pay_tangcoinNum;
                                                query.$("#paycash").innerHTML = "支付现金：&#65509;0元";
                                                stangpay(target);
                                                return;
                                            }
                                        } else {
                                            query.$("#pay_type").style.display = "block";
                                            query.$("#paycash").innerHTML = "支付现金：&#65509;" + (goodsmoney - pay_tangcoinNum - pay_btangcoinNum) + "元";
                                        }

                                    };

                                query.$('.paypop').addEventListener('click', clickEvent, false);
                                query.$('.paypop').addEventListener('keyup', KeyUpEvent, false);


                                return {

                                    show: show

                                };

                            }(),


                            wapPay = function () {
                                console.log("wappay");
                                var wxClickType = 1,

                                    payObj = {

                                        defaultPay: 'pwb_alipay',

                                        pwb_alipay: function (target) {
                                            if (target.state) return;
                                            target.state = true;
                                            if (query.$("#tangcoinNumber").value > 0) {
                                                params = params + "&ptb_money=" + query.$("#tangcoinNumber").value
                                            }
                                            if (query.$("#btangcoinNumber").value > 0) {
                                                params = params + "&bptb_money=" + query.$("#btangcoinNumber").value;
                                            }
                                            IO.jsonp(ajaxurl + 'payment/order.asp?' + params + '&method=alipay_wap', function (data) {

                                                target.state = false;
                                                if (data.code == 200) {
                                                    if (isIOS) {
                                                        query.$('.af_gopay').href = data.data.code;
                                                        query.$('.alipay_fk').style.display = 'block';
                                                        return;
                                                    } else {
                                                        var alipay_iframe = query.$('#alipay-game');
                                                        query.$('#alipay_ifream').src = data.data.code;
                                                        alipay_iframe.style.display = 'block';
                                                        query.$('.pay_ifarme_close').style.display = 'block';
                                                        return;
                                                    }

                                                }

                                                messageShow.show(data.message);

                                            });


                                        },
                                        pwb_weixinpay: function (target) {

                                            if (target.state) return;

                                            target.state = true;
                                            if (query.$("#tangcoinNumber").value > 0) {
                                                params = params + "&ptb_money=" + query.$("#tangcoinNumber").value
                                            }
                                            if (query.$("#btangcoinNumber").value > 0) {
                                                params = params + "&bptb_money=" + query.$("#btangcoinNumber").value;
                                            }
                                            IO.jsonp(ajaxurl + 'payment/order.asp?' + params + '&method=wechat_h5', function (data) {

                                                target.state = false;

                                                if (data.code == 200) {
                                                    query.$('.wf_gopay').href = data.data.code;
                                                    query.$('.weixin_fk').style.display = 'block';
                                                    return;
                                                }


                                                messageShow.show(data.message);

                                            });


                                        },

                                        weixinPay: function () {
                                            if (query.$("#tangcoinNumber").value > 0) {
                                                params = params + "&ptb_money=" + query.$("#tangcoinNumber").value
                                            }
                                            if (query.$("#btangcoinNumber").value > 0) {
                                                params = params + "&bptb_money=" + query.$("#btangcoinNumber").value;
                                            }
                                            IO.jsonp(ajaxurl + 'payment/order.asp?' + params + '&method=wechat_wap', function (data) {
                                                if (data.code == 200) {
                                                    function onBridgeReady(data) {

                                                        WeixinJSBridge.invoke('getBrandWCPayRequest', {
                                                            "appId": data.data.code.appId,
                                                            "timeStamp": data.data.code.timeStamp,
                                                            "nonceStr": data.data.code.nonceStr,
                                                            "package": data.data.code.package,
                                                            "signType": data.data.code.signType,
                                                            "paySign": data.data.code.paySign
                                                        }, function (res) {
                                                            if (res.err_msg == "get_brand_wcpay_request:ok") {
                                                                alert('支付成功');
                                                                query.$('.paypop').style.display = 'none';
                                                            } else if (res.err_msg == "get_brand_wcpay_request:cancel") {
                                                                alert('取消支付!');
                                                            } else if (res.err_msg == "get_brand_wcpay_request:fail") {
                                                                alert('支付失败');
                                                            }
                                                        });
                                                    }

                                                    function callpay(data) {
                                                        if (typeof WeixinJSBridge == "undefined") {
                                                            if (document.addEventListener) {
                                                                alert(4);
                                                                document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);
                                                            } else if (document.attachEvent) {
                                                                alert(3);
                                                                document.attachEvent('WeixinJSBridgeReady', onBridgeReady);
                                                                document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);
                                                            }
                                                        } else {
                                                            onBridgeReady(data);
                                                        }
                                                    }

                                                    callpay(data);
                                                    wx.error(function (res) {
                                                        console.log(res.err_code + res.err_desc + res.err_msg);
                                                    });
                                                }

                                            });
                                        }

                                    },

                                    show = function (money) {
                                        query.$('.paypop').style.display = 'block';

                                        if (navigator.userAgent.toLowerCase().match(/MicroMessenger/i) == 'micromessenger') {
                                            payObj.defaultPay = 'weixinPay';
                                            paypop.innerHTML = ['<div class="pay_wap_box">',

                                                '<i class="clo"></i>',
                                                '<h1>选择支付方式</h1>',
                                                '<div class="pwb_money">',
                                                '<p>订单金额</p>',
                                                '<span class="pwb_moneym">' + money + '</span>',
                                                '</div>',

                                                '<div id="pay_tangcoin_num" class="pay-mod" style="display: block;">',
                                                '<div class="li_last_end">',
                                                '<label for="pay_case" class="frs"><input id="stangpay" class="ios7CBox" type="checkbox"></label>',
                                                '使用<input style="color:#FF0000; font-size:0.3rem" class="form-control ptbinpt" id="tangcoinNumber" onbeforepaste="clipboardData.setData(\'text\',clipboardData.getData(\'text\').replace(/[^\d]/g,\'\'))" pattern="[0-9]*" placeholder="0" maxlength="6" type="text"><span>通用唐币</span> ,余额 <i></i><span style="color:#FF0000; font-size:0.3rem" id="own_tangcoinNum">' + tangcoin + '</span>元',
                                                '</div>',
                                                '</div>',
                                                '<div id="pay_btangcoin_num" class="pay-mod" style="display: block;">',
                                                '<div class="li_last_end">',
                                                '<label for="pay_case" class="frs"><input id="bstangpay" class="ios7CBox" type="checkbox"></label>',
                                                '使用<input style="color:#FF0000; font-size:0.3rem" class="form-control ptbinpt" id="btangcoinNumber" onbeforepaste="clipboardData.setData(\'text\',clipboardData.getData(\'text\').replace(/[^\d]/g,\'\'))" pattern="[0-9]*" placeholder="0" maxlength="6" type="text"><span>专用唐币</span> ,余额 <i></i><span style="color:#FF0000; font-size:0.3rem" id="own_btangcoinNum">' + btangcoin + '</span>元',
                                                '</div>',
                                                '</div>',
                                                '<div class="pwb_pay">选择支付方式<em id="paycash" style="color:#FF0000;float: right;">支付现金：￥' + money + '元</em></div>',
                                                '<div id="pay_type">',
                                                '<ul class="pwb_list" >',
                                                '<li id="weixinPay" class="cur">微信支付<i class="pay_ico"></i></li>',
                                                '</ul>',
                                                '<div class="pwb_shure">确认支付</div>',
                                                ' </div>',
                                                '</div>'].join('');


                                        } else {


                                            paypop.innerHTML = ['<div class="pay_wap_box">',

                                                '<i class="clo"></i>',
                                                '<h1>选择支付方式</h1>',
                                                '<div class="pwb_money">',
                                                '<p>订单金额</p>',
                                                '<span class="pwb_moneym">' + money + '</span>',
                                                '</div>',

                                                '<div id="pay_tangcoin_num" class="pay-mod" style="display: block;">',
                                                '<div class="li_last_end">',
                                                '<label for="pay_case" class="frs"><input id="stangpay" class="ios7CBox" type="checkbox"></label>',
                                                '使用<input style="color:#FF0000; font-size:0.3rem" class="form-control ptbinpt" id="tangcoinNumber" onbeforepaste="clipboardData.setData(\'text\',clipboardData.getData(\'text\').replace(/[^\d]/g,\'\'))" pattern="[0-9]*" placeholder="0" maxlength="6" type="text"><span>通用唐币</span> ,余额 <i></i><span style="color:#FF0000; font-size:0.3rem" id="own_tangcoinNum">' + tangcoin + '</span>元',
                                                '</div>',
                                                '</div>',
                                                '<div id="pay_btangcoin_num" class="pay-mod" style="display: block;">',
                                                '<div class="li_last_end">',
                                                '<label for="pay_case" class="frs"><input id="bstangpay" class="ios7CBox" type="checkbox"></label>',
                                                '使用<input style="color:#FF0000; font-size:0.3rem" class="form-control ptbinpt" id="btangcoinNumber" onbeforepaste="clipboardData.setData(\'text\',clipboardData.getData(\'text\').replace(/[^\d]/g,\'\'))" pattern="[0-9]*" placeholder="0" maxlength="6" type="text"><span>专用唐币</span> ,余额 <i></i><span style="color:#FF0000; font-size:0.3rem" id="own_btangcoinNum">' + btangcoin + '</span>元',
                                                '</div>',
                                                '</div>',
                                                '<div class="pwb_pay">选择支付方式<em id="paycash" style="color:#FF0000;float: right;">支付现金：￥' + money + '元</em></div>',
                                                '<div id="pay_type">',
                                                '<ul class="pwb_list" >',
                                                ' <li id="pwb_alipay" class="cur">支付宝支付<i class="pay_ico"></i></li>',
                                                '<li id="pwb_weixinpay">微信支付<i class="pay_ico"></i></li>',
                                                '</ul>',
                                                '<div class="pwb_shure">确认支付</div>',
                                                ' </div>',
                                                '</div>',
                                                '<div class="alipay_fk">',
                                                '<div class="af_fk">返回游戏<i class="clo"></i></div>',
                                                '<div class="af_ico"></div>',
                                                '<a class="af_gopay">点击打开支付宝支付</a>',
                                                '</div>',
                                                '<div class="weixin_fk">',
                                                '<div class="wf_fk">返回游戏<i class="clo"></i></div>',
                                                '<div class="wf_ico"></div>',
                                                '<a class="wf_gopay gopay">点击打开微信支付</a>',
                                                '</div>',
                                                '<div id="alipay-game" class="alipay-game">',
                                                '<iframe id="alipay_ifream" width="100%" height="100%" src="about:blank" frameborder="0" scrolling="no"></iframe>',
                                                '</div>',
                                                '<div class="pay_ifarme_close"><i class="ifarme_clo"></i>返回游戏</div>'].join('');


                                        }
                                    },

                                    payisok = function (sn) {


                                        var createcode = function () {

                                                IO.jsonp(ajaxurl + 'payment/query.asp?id=' + sn, function (data) {

                                                    if (data.code == 200) {
                                                        clearInterval(nIntervId);
                                                        popshow.show('充值成功');
                                                        popHide();

                                                    }
                                                });

                                            },
                                            nIntervId = setInterval(createcode, 5000);


                                    },

                                    pay_isok = function (sn) {

                                        IO.jsonp(ajaxurl + 'payment/query.asp?id=' + sn, function (data) {

                                            if (data.code == 200) {

                                                popshow.show('充值成功');

                                                popHide();

                                            } else {
                                                messageShow.show(data.message);

                                            }

                                        });
                                    },


                                    alipay_code = '',


                                    alipay_sn = '',


                                    weixinpay_code = '',


                                    weixinpay_sn = '',


                                    payItemTab = function (target) {

                                        Array.prototype.slice.call(target.parentNode.children).forEach(function (item, index) {

                                            if (item == target) {

                                                item.className = 'cur';

                                                payObj.defaultPay = item.id;

                                            } else {

                                                item.className = '';

                                            }
                                        });

                                    },

                                    clickEvent = function (event) {
                                        var target = event.target;


                                        if (target.className == 'pay_ico') {

                                            payItemTab(target.parentNode);

                                            return;
                                        }

                                        if (target.nodeName.toLowerCase() == 'li' && target.id == 'pwb_alipay') {

                                            payItemTab(target);

                                            return;
                                        }
                                        if (target.nodeName.toLowerCase() == 'li' && target.id == 'weixinPay') {

                                            payItemTab(target);

                                            return;
                                        }

                                        if (target.nodeName.toLowerCase() == 'li' && target.id == 'pwb_weixinpay') {

                                            payItemTab(target);

                                            return;
                                        }


                                        if (target.className == 'pwb_shure') {
                                            //alert(payObj.defaultPay);

                                            payObj[payObj.defaultPay](target);

                                            return;

                                        }


                                        if (target.className == 'clo') {

                                            popHide();
                                            query.$('.alipay_fk').style.display = 'none';

                                            query.$('.weixin_fk').style.display = 'none';

                                            wxClickType = 1;

                                        }


                                        if (target.className == 'pay_ifarme_close' || target.className == 'ifarme_clo') {

                                            popHide();

                                            query.$('#alipay_ifream').src = '';

                                            query.$('#alipay-game').style.display = 'none';


                                            query.$('.pay_ifarme_close').style.display = 'none';

                                        }


                                        if (target.className == 'wf_gopay gopay') {
                                            if (wxClickType != 1) {
                                                event.preventDefault();
                                                return false;
                                            }
                                            wxClickType = 0;
                                        }

                                        console.log(wxClickType);

                                    };

                                query.$('.paypop').addEventListener('click', clickEvent, false);


                                return {

                                    show: show
                                };


                            }(),

                            getObj = function (obj) {

                                var datamessage = obj.data;
                                //console.log('obj',obj);
                                console.log('datamessage', datamessage);
                                if (datamessage.method === 'pay') {

                                    delete datamessage.method;
                                    params = '';
                                    for (var s in datamessage) {

                                        if (typeof datamessage[s] == 'function') {

                                            datamessage[s]();

                                        } else {

                                            params += '&' + s + '=' + datamessage[s];
                                        }
                                    }
                                    params = params.substr(1);

                                    if (!userData.id_card && userData.username.indexOf('@xh') < 0) {
                                        if (isSW) {//试玩
                                            login.testsign();
                                        } else {
                                            login.verifyLogin();
                                        }
                                    } else {
                                        IO.jsonp(ajaxurl + 'payment/getangcoin.asp?gid=' + game_id, function (data) {
                                            if (data.code == 200) {
                                                tangcoin = parseInt(data.tangcoinNum);
                                                btangcoin = parseInt(data.btangcoinNum);
                                                goodsmoney = parseInt(datamessage.money);

                                                if (IsPC()) {

                                                    //pcPay.show((datamessage.money /100).toFixed(2));
                                                    pcPay.show(datamessage.money);
                                                    return;
                                                }

                                                if (/macintosh|window/.test(navigator.userAgent.toLowerCase())) {

                                                    //pcPay.show((datamessage.money /100).toFixed(2));
                                                    pcPay.show(datamessage.money);
                                                    return;

                                                }

                                                //wapPay.show((datamessage.money /100).toFixed(2));
                                                wapPay.show(datamessage.money);
                                                return;
                                            }
                                        });
                                    }
                                } else if (datamessage.method === 'report') {
                                    console.log('datamessage', datamessage);
                                    params = '';
                                    for (var s in datamessage) {

                                        if (typeof datamessage[s] == 'function') {

                                            datamessage[s]();

                                        } else {

                                            params += '&' + s + '=' + datamessage[s];
                                        }
                                    }
                                    params = params.substr(1);
                                    IO.jsonp(ajaxurl + 'reportgameRole.asp?' + params, function (data) {
                                        if (data.code == 200) {
                                            console.log('数据已经上报', datamessage);

                                        }
                                    });
                                } else if (datamessage.method === 'sharegame') {
                                    delete datamessage.method;
                                    if (IsPC()) {
                                        shareCallbackSend();
                                        popshow.show('游戏里可以领取分享礼包了！');
                                        return;

                                    }
                                    if (/macintosh|window/.test(navigator.userAgent.toLowerCase())) {
                                        query.$('#sharetip').style.display = 'block';
                                        return;

                                    } else {
                                        shareCallbackSend();
                                        popshow.show('游戏里可以领取分享礼包了！');
                                        return;
                                    }
                                } else if (datamessage.method === 'follow') {
                                    delete datamessage.method;
                                    params = '';
                                    for (var s in datamessage) {

                                        if (typeof datamessage[s] == 'function') {

                                            datamessage[s]();

                                        } else {

                                            params += '&' + s + '=' + datamessage[s];
                                        }
                                    }
                                    params = params.substr(1);

                                    IO.jsonp(ajaxurl + 'followstang.asp?' + params, function (data) {
                                        if (data.code == 200) {
                                            query.$('#h5_ifream').contentWindow.postMessage('followOK', '*');
                                            messageShow.show('关注成功');
                                            return;
                                        } else {
                                            messageShow.show('关注失败');
                                            showstangER();
                                        }

                                    });
                                } else if (datamessage.method === 'verifyID') {
                                    delete datamessage.method;
                                    params = '';
                                    for (var s in datamessage) {

                                        if (typeof datamessage[s] == 'function') {

                                            datamessage[s]();

                                        } else {

                                            params += '&' + s + '=' + datamessage[s];
                                        }
                                    }
                                    params = params.substr(1);

                                    IO.jsonp(ajaxurl + 'verifyID.asp?' + params, function (data) {
                                        if (data.code == 200) {
                                            query.$('#h5_ifream').contentWindow.postMessage('verifyOK', '*');
                                            messageShow.show('认证成功');
                                            return;
                                        } else {
                                            if (!userData.id_card && userData.username.indexOf('@xh') < 0) {
                                                if (isSW) {//试玩
                                                    login.testsign();
                                                } else {
                                                    login.verifyLogin();
                                                }
                                            }
                                        }

                                    });
                                } else {
                                    return;
                                }


                            };


                        h5_wap.addEventListener('click', function (event) {

                            var target = event.target;

                            if (target.className == 'paypop') {

                                popHide();

                                return;

                            }

                        }, false);


                        return {

                            getObj: getObj
                        };

                    }();

                    window.addEventListener('message', messageCallback.getObj, false);

                }();
        },
        gameIsLogin = function () {


            islogin(function (data) {


                if (data.code == 200) {


                    pageLading();


                    return;

                }

                query.addClass(doc.body, 'issdklogin');

                login.gameLogin();

            });
        }();

}(document));

document.addEventListener('plusready', function () {
    var webview = plus.webview.currentWebview();
    plus.key.addEventListener('backbutton', function () {
        webview.canBack(function (e) {
            if (e.canBack) {
                webview.back();
            } else {
                webview.close(); //hide,quit
                plus.runtime.quit();
            }
        })
    });
});
