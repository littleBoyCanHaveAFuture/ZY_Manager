(function () {
    function e(e) {
        var a = new Image, t = "_hlmy_img_" + Math.random();
        window[t] = a, a.onload = a.onerror = function () {
            window[t] = null
        }, a.src = e
    }

    "function" != typeof Object.assign && Object.defineProperty(Object, "assign", {
        value: function (e, a) {
            "use strict";
            if (null == e) throw new TypeError("Cannot convert undefined or null to object");
            for (var t = Object(e), n = 1; n < arguments.length; n++) {
                var o = arguments[n];
                if (null != o) for (var i in o) Object.prototype.hasOwnProperty.call(o, i) && (t[i] = o[i])
            }
            return t
        }, writable: !0, configurable: !0
    });
    var a = function () {
        this.version = "2.3.9", console.log("[1758SDK %cversion", "color:blue;", "]:", this.version)
    }, t = {
        appKey: "",
        gid: "",
        hlmy_gw: "",
        pf: "",
        isAds: !0,
        ads: {playUrl: "", time: 0, cache: !1, obj: {}, type: "", closeLock: !1, cpParam: ""},
        state: {isCheck: !1, tuiaInitInfo: null, tuiaInitCallback: null}
    }, n = {}, o = {
        pay: "//h5.g1758.cn/pay/v4/jsonp/payInit.jsonp",
        auth: "//h5.g1758.cn/play/v4/jsonp/auth.jsonp",
        share: "//h5.g1758.cn/play/v4/jsonp/share.jsonp",
        picture: "//h5.g1758.cn/play/game/getCustomizeShareImgUrl.json",
        init: "//h5.g1758.cn/play/v4/jsonp/init.jsonp",
        role: "//h5.g1758.cn/play/v4/jsonp/roleInfo.jsonp",
        follow: "//h5.g1758.cn/play/v4/jsonp/follow.jsonp",
        checkFollow: "//h5.g1758.cn/play/v4/jsonp/checkFollow.jsonp",
        initAd: "//h5.g1758.cn/play/v4/jsonp/initAd.jsonp",
        checkAd: "//h5.g1758.cn/play/v4/jsonp/checkAd.jsonp",
        showAd: "//h5.g1758.cn/play/v4/jsonp/reportAd.jsonp",
        playAd: "//h5.g1758.cn/play/v4/jsonp/playAd.jsonp",
        finishAd: "//h5.g1758.cn/play/v4/jsonp/finishAd.jsonp",
        execute: "//h5.g1758.cn/play/v4/jsonp/execute.jsonp",
        checkRealNameCert: "//h5.g1758.cn/play/v4/jsonp/hasRealnameCert.jsonp",
        realVerify: "//h5.g1758.cn/play/v4/jsonp/realVerify.jsonp",
        debug: "//h5.g1758.cn/play/v4/jsonp/debug.jsonp"
    };
    n.shareResult = function (e) {
        "success" == e[0] && "function" == typeof n.shareSuccess ? n.shareSuccess() : "success" == e[0] && "function" == typeof window[e[1]] ? window[e[1]]() : "cancel" == e[0] && "function" == typeof n.shareCancel && n.shareCancel()
    }, a.prototype = {
        postData: function (e) {
            e.hlmy = !0, top.postMessage(e, "*")
        }, auth: function (e) {
            t.appKey = e.appKey || "", t.hlmy_gw = e.hlmy_gw || "";
            var a = {appKey: e.appKey || "", hlmy_gw: e.hlmy_gw || "", userToken: e.userToken || ""}, n = function (a) {
                1 == a.result ? (t.gid = a.data.userInfo.gid, HLMY_SDK.init({
                    appKey: e.appKey,
                    hlmy_gw: e.hlmy_gw,
                    gid: t.gid
                }), "function" == typeof e.callback && e.callback(a)) : "function" == typeof e.callback && e.callback(a)
            };
            this.dynamicScript(o.auth, a, n, !0)
        }, pay: function (a) {
            var i, c = {}, l = navigator.userAgent.toLocaleLowerCase();
            if (c.paySafecode = a.paySafecode || a.paySafeCode, c.paySafecode) if (t.appKey && t.gid && t.hlmy_gw) if (e("http://nlog.1758.com/log?type=pay&actionName=cpJsPay&appKey=" + t.appKey + "&gid=" + t.gid + "&gw=" + t.hlmy_gw + "&message=cpJsPay&openId=&orderId=" + c.paySafecode + "&time=" + (new Date).getTime()), c.appKey = t.appKey, c.gid = t.gid, c.hlmy_gw = t.hlmy_gw, i = a.callback || function () {
            }, "0" == t.pf || "" == t.pf) {
                if ("5e025d2eb1a456dd207a4a14cdf03c4a" == t.appKey && l.indexOf("micromessenger") < 0 && l.indexOf("dwjia") < 0) return void (top.location.href = "http://wx.1758.com/pay/payChoice?gid=" + t.gid + "&hlmy_gw=" + t.hlmy_gw + "&paySafecode=" + c.paySafecode + "&rcontentimeId=292190");
                n.paySuccess = i, this.pay1758(c)
            } else e("http://nlog.1758.com/log?type=pay&actionName=dynamicJsPay&appKey=" + t.appKey + "&gid=" + t.gid + "&gw=" + t.hlmy_gw + "&message=dynamicJsPay&openId=&orderId=" + c.paySafecode + "&time=" + (new Date).getTime()), this.dynamicScript(o.pay, c, i); else e("http://nlog.1758.com/log?type=pay&actionName=cpJsPay&appKey=" + t.appKey + "&gid=" + t.gid + "&gw=" + t.hlmy_gw + "&message=noparams&openId=&orderId=" + c.paySafecode + "&time=" + (new Date).getTime()), "" == t.appKey && console.log("[1758SDK %cpay%c]\n%c初始化时缺少appKey参数", "color:blue", "color:black", "color:red"), "" == t.gid && console.log("[1758SDK %cpay%c]\n%c初始化时缺少gid参数", "color:blue", "color:black", "color:red"), "" == t.hlmy_gw && console.log("[1758SDK %cpay%c]\n%c初始化时缺少hlmy_gw参数", "color:blue", "color:black", "color:red"); else console.log("[1758SDK %cpay%c]\n%c缺少【paySafeCode | paySafecode】 参数", "color:blue", "color:black", "color:red")
        }, pay1758: function (a) {
            console.log("pay 1758"), e("http://nlog.1758.com/log?type=pay&actionName=hlmyJsPay&appKey=" + t.appKey + "&gid=" + t.gid + "&gw=" + t.hlmy_gw + "&message=hlmyJsPay&openId=&orderId=" + a.paySafecode + "&time=" + (new Date).getTime()), this.postData({
                type: "pay",
                value: {fn: "payInfo", args: a}
            })
        }, payLoading: function (e, a) {
            e = e || 2e3, a = a || !0;
            var t = document.createElement("div");
            t.setAttribute("style", "position: fixed;width: 100%;height:100%;left:0;top:0;");
            var n = document.createElement("div");
            n.setAttribute("style", "position: absolute;width: 150px;height: 70px;background-color:rgba(0,0,0,.7);margin: 0 auto;left: 0;right: 0;top: 50%;margin-top: -35px;border-radius: 8px;text-align: center;color:#fff;font-size:14px;");
            var o = document.createElement("div");
            o.setAttribute("style", "margin-top:19px;"), o.innerText = "支付加载中";
            var i = document.createElement("img");
            i.setAttribute("style", "width: 30px;"), n.appendChild(o), n.appendChild(i), t.appendChild(n), document.body.appendChild(t), a && setTimeout(function () {
                t.remove()
            }, e)
        }, checkFollow: function (e) {
            var a = function (a) {
                "function" != typeof e && (e = function () {
                }), e(a)
            };
            t.appKey && t.gid && t.hlmy_gw ? this.dynamicScript(o.checkFollow, {
                appKey: t.appKey,
                gid: t.gid,
                hlmy_gw: t.hlmy_gw
            }, a, !0) : ("" == t.appKey && console.log("[1758SDK %csubscribe%c]\n%c初始化时缺少appKey参数", "color:blue", "color:black", "color:red"), "" == t.gid && console.log("[1758SDK %csubscribe%c]\n%c初始化时缺少gid参数", "color:blue", "color:black", "color:red"), "" == t.hlmy_gw && console.log("[1758SDK %csubscribe%c]\n%c初始化时缺少hlmy_gw参数", "color:blue", "color:black", "color:red"))
        }, follow: function () {
            "0" == t.pf || "" == t.pf ? this.postData({
                type: "follow",
                value: {fn: "followWx", args: []}
            }) : (this.dynamicScript(o.follow, {
                appKey: t.appKey,
                gid: t.gid,
                hlmy_gw: t.hlmy_gw
            }, "", !0), console.log("this is not 1758 platform"))
        }, setShareInfo: function (e) {
            var a = {tipInfo: !0, callback: "shareResult"};
            if (e = Object.assign({}, a, e), "object" == typeof e && null != e && ("function" == typeof e.success ? (n.shareSuccess = e.success, e.success = "shareSuccess") : n.shareSuccess = "", "function" == typeof e.cancel ? (n.shareCancel = e.cancel, e.cancel = "shareCancel") : n.shareCancel = ""), "0" == t.pf || "" == t.pf) this.checkShareAbility || "function" != typeof n.shareCancel ? this.postData({
                type: "share",
                value: {share: "shareInfo", shareInfo: e}
            }) : n.shareCancel(); else {
                if (e && "pic" === e.type) {
                    var i = e.picUrl;
                    return e.picUrl = "", e.appKey = t.appKey, e.gid = t.gid, e.hlmy_gw = t.hlmy_gw, void this.dynamicScript(o.share, e, function (e) {
                        "function" == typeof e.fn && e.fn(i)
                    }, !0)
                }
                "object" == typeof e && null != e ? (e.appKey = t.appKey, e.gid = t.gid, e.hlmy_gw = t.hlmy_gw, this.dynamicScript(o.share, e, function (e) {
                    n.shareResult([e, "onShareTimeline"])
                })) : this.dynamicScript(o.share, {
                    appKey: t.appKey,
                    gid: t.gid,
                    hlmy_gw: t.hlmy_gw,
                    data: e
                }, function (e) {
                    n.shareResult([e, "onShareTimeline"])
                }, !0)
            }
        }, checkShareAbility: function () {
            var e = navigator.userAgent.toLocaleLowerCase();
            return /micromessenger|dwjia/gi.test(e)
        }, sharePicture: function (e) {
            var a = e.img;
            a = a.substr(a.indexOf("base64,") + 7), a = a.trim(), this._ajax({
                method: "post",
                url: o.picture,
                data: {appKey: t.appKey, gid: t.gid, hlmy_gw: t.hlmy_gw, img_data: a},
                callback: function (e) {
                    if (1 == e.result) {
                        var a = window, n = e.data.fn.split(".");
                        if ("0" == t.pf || "" == t.pf) i.postData({
                            type: "share",
                            value: {share: "sharePicture", shareInfo: {imgUrl: e.data.shareImgUrl}}
                        }); else if (1 === n.length && "function" == typeof window[n[0]]) window[n[0]](e.data.shareImgUrl); else {
                            for (var o = 0, c = n.length; o < c; o++) a[n[o]] && (a = a[n[o]]);
                            "function" == typeof a && a(e.data.shareImgUrl)
                        }
                    }
                }
            })
        }, setBaseState: function (e) {
            var a;
            void 0 === e ? (a = !1, e = "") : a = !0, this.postData({type: "baseState", value: {state: e, isState: a}})
        }, onShareTimeline: function (e) {
            "function" == typeof onShareTimeline ? onShareTimeline(e) : console.log("[1758SDK %conShareTimeline%c]\n%csorry,not find function onShareTimeline", "color:blue", "color:black", "color:red")
        }, onShareFriend: function (e) {
            "function" == typeof onShareFriend ? onShareFriend(e) : console.log("[1758SDK %conShareFriend%c]\n%csorry,not find function onShareFriend", "color:blue", "color:black", "color:red")
        }, dynamicScript: function (e, a, t, n) {
            var o = {}, i = "";
            1 === arguments.length && "object" == typeof arguments[0] && (o = arguments[0], e = o.url, a = o.data, t = o.success, n = o.bl);
            var c = "jsonpcallback" + (Math.random() + "").substring(2);
            if ("object" == typeof a && null != a) for (var l in "function" == typeof t && (window[c] = t, a.callback = c), a) i = i + "&" + l + "=" + a[l];
            e = e.indexOf("?") > 0 ? e + "&" + i : e + "?" + i;
            var s = document.createElement("script");
            s.type = "text/javascript", s.src = e, s.onload = function () {
                "function" == typeof o.loadEndFn && o.loadEndFn(o.fnData), n && r.removeChild(s)
            };
            var r = document.getElementsByTagName("head").item(0);
            r.appendChild(s)
        }, kefu: function (e) {
        }, reload: function (e) {
            e && e.appKey && (t.appKey = e.appKey), e && e.hlmy_gw && (t.hlmy_gw = e.hlmy_gw), e && void 0 !== e.hlmy_gw && (t.pf = e.hlmy_gw.split("_")[0]);
            var a = "//wx.1758.com/play/login/floginV4?appKey=" + t.appKey + "&hlmy_gw=" + t.hlmy_gw;
            if ("0" == t.pf || "" == t.pf) try {
                top.location = a
            } catch (e) {
                this.postData({type: "fn", value: {fn: "reloadUrl", args: a}})
            } else {
                var n = {appKey: t.appKey, gid: t.gid, hlmy_gw: t.hlmy_gw};
                this.dynamicScript("//h5.g1758.cn/play/v4/jsonp/reload.jsonp", n, function (e) {
                }, !0)
            }
        }, adaptParams: function (e) {
            var a = "//h5.g1758.cn/play/v4/jsonp/adaptParams.jsonp",
                n = {appKey: t.appKey, gid: t.gid, hlmy_gw: t.hlmy_gw};
            this.dynamicScript(a, n, function (a) {
                e(a)
            }, !0)
        }, roleInfo: function (e) {
            var a = {appKey: t.appKey, gid: t.gid, hlmy_gw: t.hlmy_gw};
            a = Object.assign(e, a), a.serverId = e.serverId || "", a.serverName = encodeURIComponent(e.serverName || ""), a.roleId = e.roleId || "", a.roleName = encodeURIComponent(e.roleName || ""), a.roleLevel = e.roleLevel || "", a.roleCoins = e.roleCoins || "", a.isNewRole = e.isNewRole || !1, a.roleCreateTime = e.roleCreateTime || 0, this.dynamicScript(o.role, a, "", !0)
        }, shareGuide: function () {
            var e = '<style>.share-square{position:fixed;background:rgba(0,0,0,.4);width:100%;height:100%;left:0;top:0;font-size:14px;color:#fff;z-index:110000;font-weight:700}.share-square .share-box{position:relative;top:5px}.share-square .share-box>img{position:absolute;-moz-animation:icon-bounce .2s ease-in-out infinite alternate;-webkit-animation:icon-bounce .2s ease-in-out infinite alternate;animation:icon-bounce .2s ease-in-out infinite alternate;right:10px}.share-square span{position:absolute;right:5px;top:50px}.share-square span img{position:relative;top:5px;vertical-align:baseline}@-moz-keyframes icon-bounce{0%{top:5px}50%{top:0}100%{top:-5px}}@-webkit-keyframes icon-bounce{0%{top:5px}50%{top:0}100%{top:-5px}}@keyframes icon-bounce{0%{top:5px}50%{top:0}100%{top:-5px}}</style><div id="share-square" class="share-square"><div class="share-box"><img src="http://images.1758.com/image/20161124/open_1_50b53ad143c7d193e61bb9733ceabe5c.png"><span><img class="z" src="http://images.1758.com/image/20161124/open_1_8a3082c3ea9b04e38a8c76d301f5518a.png" alt="">发送微信群或朋友</span></div></div>',
                a = document.createElement("div");
            a.id = "shareContainer", a.innerHTML = e;
            var t = document.getElementById("shareContainer");
            null === t && document.body.appendChild(a), a.addEventListener("touchstart", function () {
                a.remove()
            }), a.addEventListener("click", function () {
                a.remove()
            })
        }, execute: function (e) {
            var a = function () {
            };
            "object" == typeof e && null !== e || (e = {}), e.name && (e.appKey = t.appKey, e.gid = t.gid, e.hlmy_gw = t.hlmy_gw, e.callback && (a = e.callback), this.dynamicScript(o.execute, e, a, !0))
        }, checkAd: function (e) {
            var a;
            t.isAds ? (a = {
                url: o.checkAd,
                data: {appKey: t.appKey, gid: t.gid, hlmy_gw: t.hlmy_gw},
                success: function (n) {
                    1 === n.result && n.data.adEnable ? (t.ads.time = n.data.adTimeout, "tuia" == n.data.adType && (t.ads.type = "tuia", i.adsTuiInit(n.data, e))) : (a = {
                        result: 0,
                        msg: "暂无广告内容"
                    }, e(a))
                }
            }, this.dynamicScript(a)) : (a = {result: 0, msg: "暂无广告内容"}, e(a))
        }, showAd: function (e) {
            var a = 1;
            e && void 0 !== e.cpPosId && "" !== e.cpPosId && (a = e.cpPosId);
            var n = {url: o.showAd, data: {appKey: t.appKey, gid: t.gid, hlmy_gw: t.hlmy_gw, cpPosId: a}};
            this.dynamicScript(n)
        }, playAd: function (e) {
            var a, n, c, l, s, r, p = e.callback || function () {
            };
            if (t.ads.cache) {
                t.ads.cpParam = e.cpParam;
                var d = 1;
                if (void 0 !== e.cpPosId && (d = e.cpPosId), t.ads.playUrl) {
                    function y(e) {
                        var a = t.ads.time;
                        r = setInterval(function () {
                            0 === a ? (clearInterval(r), s.innerText = "点击关闭", t.ads.closeLock = !0, e({
                                result: 1,
                                finished: !0,
                                cpParam: t.ads.cpParam,
                                cpPosId: d
                            }), i.dynamicScript({
                                url: o.finishAd,
                                data: {
                                    appKey: t.appKey,
                                    gid: t.gid,
                                    hlmy_gw: t.hlmy_gw,
                                    cpParam: t.ads.cpParam,
                                    cpPosId: d
                                }
                            })) : l.innerText = --a
                        }, 1e3)
                    }

                    a = document.querySelector("#hlmyAdsContainer"), n = document.querySelector("#hlmyAdsIframe"), c = document.querySelector("#hlmyAdsLoading"), s = document.querySelector("#hlmyAdsClose"), s.innerHTML = '<span id="hlmyAdsTime">' + t.ads.time + "</span> 可关闭", l = document.querySelector("#hlmyAdsTime"), n.setAttribute("src", t.ads.playUrl), n.style.display = "none", c.style.display = "block", setTimeout(function () {
                        c.style.display = "none", n.style.display = "block", y(p)
                    }, 2e3), a.style.display = "block", s.addEventListener("touchstart", function (e) {
                        t.ads.closeLock && (n.removeAttribute("src"), a.style.display = "none", t.ads.closeLock = !1, e.stopPropagation())
                    }), s.addEventListener("click", function (e) {
                        t.ads.closeLock && (n.removeAttribute("src"), a.style.display = "none", t.ads.closeLock = !1, e.stopPropagation())
                    }), document.getElementById("hlmyCustomer").click(), this.dynamicScript({
                        url: o.playAd,
                        data: {appKey: t.appKey, gid: t.gid, hlmy_gw: t.hlmy_gw, cpParam: t.ads.cpParam, cpPosId: d}
                    }), t.ads.cache = !1
                }
            } else p({result: 0, msg: "请先使用checkAd方法进行查询"})
        }, checkRealNameCert: function (e) {
            e = "function" == typeof e ? e : function () {
            }, i.dynamicScript({
                url: o.checkRealNameCert,
                data: {appKey: t.appKey, gid: t.gid, hlmy_gw: t.hlmy_gw},
                success: e
            })
        }, showRealNameCert: function (e) {
            e = "function" == typeof e ? e : function () {
            }, n.realNameCallback = e, "0" == t.pf || "" == t.pf ? this.postData({
                type: "fn",
                value: {fn: "showRealNameCert", args: []}
            }) : i.dynamicScript({
                url: o.realVerify,
                data: {appKey: t.appKey, gid: t.gid, hlmy_gw: t.hlmy_gw},
                success: e
            })
        }, adsInit: function () {
            var e = {
                url: o.initAd, data: {appKey: t.appKey, gid: t.gid, hlmy_gw: t.hlmy_gw}, success: function (e) {
                    1 === e.result && e.data.adEnable ? (i.loadAdsScript(), i.createAdsDom()) : t.isAds = !1
                }
            };
            this.dynamicScript(e)
        }, createAdsDom: function () {
            var e = '<div id="hlmyAdsContainer" style="position: fixed;width: 100%;left: 0;right:0;top:35px;bottom: 0;display:none;"><div style="height: 35px;line-height: 35px;background-color: #3d3d3d;position: absolute;top: -35px;left: 0;right: 0;width: 100%;color: #fff;text-align: right;box-sizing:border-box;padding:0 5px;"><div style="float:left;color:#ffe21b;">参与活动后得奖励</div><div id="hlmyAdsClose" style="display: inline-block;padding: 0px 10px;background-color:#1e1e1e;border-radius:10px;line-height: initial;vertical-align: baseline;padding:1px 12px 2px;"><span id="hlmyAdsTime">30</span> 可关闭</div></div><div id="hlmyAdsIcon" style="display:none;"></div><div id="hlmyAdsLoading" style="position: absolute;display: inline-block;padding:12px 25px;background-color: rgba(0,0,0,.4);color:#fff;margin:0 auto;left:50%;top:50%;transform: translate(-50%,-50%);-ms-transform:translate(-50%,-50%);-moz-transform:translate(-50%,-50%);-webkit-transform:translate(-50%,-50%);-o-transform:translate(-50%,-50%);border-radius: 4px;display:none;">加载中</div><iframe id="hlmyAdsIframe" style="margin: 0;padding: 0;display: block;width: 100%;width: 100px;min-width: 100%;height: 100px;min-height: 100%;border: 0;"></iframe></div>',
                a = document.createElement("div");
            a.innerHTML = e, document.body.appendChild(a)
        }, loadAdsScript: function () {
            var e = {
                url: "//yun.tuia.cn/h5-tuia/media/media-3.0.1.min.js", loadEndFn: function () {
                    t.state.isCheck && i.adsTuiInit(t.state.tuiaInitInfo, t.state.tuiaInitCallback)
                }
            };
            this.dynamicScript(e)
        }, adsTuiInit: function (e, a) {
            "function" == typeof TuiaMedia ? (t.state.isCheck = !1, t.state.tuiaInitInfo = null, t.state.tuiaInitCallback = null, t.ads.obj = new TuiaMedia({
                container: "#hlmyAdsIcon",
                appKey: e.adVendorKey,
                adslotId: e.adVendorSn,
                clickTag: !0,
                success: function (e) {
                    var n = '<img id="hlmyCustomer"  src="' + e.img_url + '"/> ';
                    document.querySelector("#hlmyAdsIcon").innerHTML = n, t.ads.playUrl = e.clickurl, t.ads.cache = !0, a({
                        result: 1,
                        msg: "",
                        title: e.ad_title,
                        imgUrl: e.img_url,
                        imgWidth: e.img_width,
                        imgHeight: e.img_height
                    })
                }
            })) : (t.state.isCheck = !0, t.state.tuiaInitInfo = e, t.state.tuiaInitCallback = a, this.loadAdsScript())
        }, _ajax: function (e) {
            function a(e, a) {
                if (null == e || null == e) return a;
                if (a) for (var t in a) e[t] = a[t];
                return e
            }

            var t = {
                method: "GET", url: "", data: {}, type: "json", async: !0, callback: function () {
                }
            };
            switch (arguments.length) {
                case 0:
                    break;
                case 1:
                    "string" == typeof arguments[0] && (t.url = arguments[0]), "object" == typeof arguments[0] && (t = a(t, arguments[0]));
                    break;
                case 2:
                    t.url = arguments[0], t.callback = arguments[1]
            }
            t.url && this._xhrAjax(t.url, t.method, t.data, t.async, t.callback, t.type)
        }, _xhrAjax: function (e, a, t, n, o, i) {
            var c, l = "";
            if (c = window.XMLHttpRequest ? new XMLHttpRequest : new ActiveXObject("Microsoft.XMLHTTP"), c.onreadystatechange = function () {
                4 === c.readyState && 200 === c.status && ("json" === i && "string" == typeof c.response ? o(JSON.parse(c.response)) : o(c.response))
            }, "object" == typeof t) {
                for (key in t) t.hasOwnProperty(key) && (l += key + "=" + t[key] + "&");
                l = l.slice(0, -1), t = l
            }
            "get" === a.toLocaleLowerCase() ? (c.open(a, e + "?" + t, n), c.responseType = i, c.send()) : "post" === a.toLocaleLowerCase() && (c.open(a, e, n), c.responseType = i, c.setRequestHeader("Content-Type", "application/x-www-form-urlencoded"), c.send(t))
        }, openDebug: function (e) {
            this.dynamicScript(o.debug, e, "", !0)
        }, setDebug: function (e) {
            for (var a = "//h5.g1758.cn", t = "//wtest.1758.com", n = e ? t : a, i = e ? a : t, c = Object.keys(o), l = 0; l < c.length; l++) o[c[l]] = o[c[l]].replace(n, i)
        }
    };
    var i = window.HLMY_SDK = new a;
    window.HLMY_SDK.init = function (e) {
        t.appKey = e.appKey || "", t.gid = e.gid || "", t.hlmy_gw = e.hlmy_gw || "", void 0 !== e.hlmy_gw && (t.pf = e.hlmy_gw.split("_")[0]);
        var a = {appKey: t.appKey, gid: t.gid, hlmy_gw: t.hlmy_gw};
        this.dynamicScript(o.init, a, "", !0), this.adsInit(), this.openDebug(a)
    }, window.addEventListener("message", function (e) {
        var a = {
            onShareTimeline: function (e) {
                "function" == typeof onShareTimeline && onShareTimeline(e)
            }, onShareFriend: function (e) {
                "function" == typeof onShareFriend && onShareFriend(e)
            }
        };
        if (e.data.hlmy) switch (e.data.type) {
            case"fn":
                "function" == typeof a[e.data.value.fn] ? a[e.data.value.fn].apply(window, e.data.value.args) : "function" == typeof window[e.data.value.fn] ? window[e.data.value.fn](e.data.value.args) : console.log(e.data.value.fn, "the function is undefined");
                break;
            case"fnCb":
                "function" == typeof n[e.data.value.fn] && n[e.data.value.fn](e.data.value.args);
                break;
            default:
                console.log(e.data.type)
        }
    }, !1)
})();
