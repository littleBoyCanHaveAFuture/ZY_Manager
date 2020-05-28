var JSON = JSON || {};
JSON.stringify = JSON.stringify || function (obj) {
    var t = typeof (obj);
    if (t != "object" || obj === null) {
        if (t == "string") {
            obj = '"' + obj + '"'
        }
        return String(obj)
    } else {
        var n, v, json = [], arr = (obj && obj.constructor == Array);
        for (n in obj) {
            v = obj[n];
            t = typeof (v);
            if (t == "string") {
                v = '"' + v + '"'
            } else {
                if (t == "object" && v !== null) {
                    v = JSON.stringify(v)
                }
            }
            json.push((arr ? "" : '"' + n + '":') + String(v))
        }
        return (arr ? "[" : "{") + String(json) + (arr ? "]" : "}")
    }
};
var H5Pay = function () {
    String.format = function () {
        if (arguments.length == 0) {
            return null
        }
        var str = arguments[0];
        for (var i = 1; i < arguments.length; i++) {
            var re = new RegExp("\\{" + (i - 1) + "\\}", "gm");
            str = str.replace(re, arguments[i])
        }
        return str
    };
    var native_jsonp = function () {
        var that = {};
        that.send = function (src, opts) {
            var options = opts || {}, callback_name = options.callbackName || "callback",
                on_success = options.onSuccess || function () {
                }, on_timeout = options.onTimeout || function () {
                }, timeout = options.timeout || 10;
            var timeout_trigger = window.setTimeout(function () {
                window[callback_name] = function () {
                };
                on_timeout()
            }, timeout * 1000);
            window[callback_name] = function (data) {
                window.clearTimeout(timeout_trigger);
                on_success(data)
            };
            var script = document.createElement("script");
            script.type = "text/javascript";
            script.async = true;
            script.src = src;
            document.getElementsByTagName("head")[0].appendChild(script)
        };
        return that
    };
    var isMobile = function () {
        var u = navigator.userAgent;
        var isAndroid = u.indexOf("Android") > -1 || u.indexOf("Adr") > -1;
        var isiOS = !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/);
        return isAndroid || isiOS
    };
    var getUrl = function (opt) {
        var gid = opt.gameId;
        var sign = opt.sign;
        var _url = "";
        if (isMobile()) {
            _url = "laosiji.swjoy.com/front/pay/{0}/m_pay.do?sign={1}&data={2}"
        } else {
            _url = "laosiji.swjoy.com/client/api/{0}/qr_code.do?sign={1}&data={2}"
        }
        return String.format(_url, gid, sign, encodeURIComponent(opt.data))
    };
    var main = function (opt, callback) {
        if (!opt) {
            return
        }
        var _url = getUrl(opt);
        if (isMobile()) {
            var postUrl = "http://h5.swjoy.com";
            var refer = window.name;
            var protocol = refer == "https" ? true : false;
            if (protocol) {
                postUrl = "https://m.swjoy.com"
            }
            window.parent.postMessage(_url, postUrl);
            return
        }
        _url = "https://" + _url;
        if (typeof jQuery !== "undefined") {
            $.ajax({
                url: _url, method: "GET", dataType: "jsonp", jsonp: "callback", success: function (res) {
                    callback && callback(res)
                }
            })
        } else {
            native_jsonp().send(_url + "&callback=callback", {
                callbackName: "callback", onSuccess: function (json) {
                    callback && callback(json)
                }, onTimeout: function () {
                    callback && callback()
                }, timeout: 3
            })
        }
    };
    return {
        init: function (opt, callback) {
            main(opt, callback)
        }
    }
};
var SwjoyGameShare = {
    main: function (opt) {
        var operateType = 'thirdGameClickShare';
        opt.operateType = operateType;
        window.top.postMessage(opt, '*');
        window.addEventListener("message", SwjoyGameShare.receiveMessage, false)
    }, receiveMessage: function (e) {
        if (e.data.operateType) {
            SwjoyGameShare.cb(e)
        }
    }, cb: function (e) {
    }, init: function (opt, callback) {
        SwjoyGameShare.main(opt);
        SwjoyGameShare.cb = callback
    }
};
