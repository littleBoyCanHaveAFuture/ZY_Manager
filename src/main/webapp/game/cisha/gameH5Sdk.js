// [H5-SDK]  Build version: 2.9.7 - 2018-12-20 7:10:11 PM
!function (t) {
    function n(r) {
        if (e[r]) return e[r].exports;
        var o = e[r] = {exports: {}, id: r, loaded: !1};
        return t[r].call(o.exports, o, o.exports, n), o.loaded = !0, o.exports
    }

    var e = {};
    return n.m = t, n.c = e, n.p = "//11wan.yy.com/s/assets/sdk/h5-yygame-sdk/2.9.7/", n(0)
}([function (t, n, e) {
    "use strict";

    function r(t) {
        return t && t.__esModule ? t : {default: t}
    }

    var o = e(107), i = r(o);
    window.WanGameH5sdk = {
        init: function () {
            this.messenger = new i.default(this.listenEvent.bind(this)), this.initLog(), this.pageStartLog(), this.pageShowLog()
        }, config: function (t) {
            var n = t.share,
                e = void 0 === n ? {} : n,
                r = t.focus,
                o = void 0 === r ? {} : r;

            console.info(e);
            console.info(o);
            this.shareSuccessCb = e.success || function () {
            }, this.shareCancelCb = e.cancel || function () {
            }, this.focusSuccessCb = o.success || function () {
            }
        }, login: function (t) {
            var n = t || {}, e = n.success, r = n.fail;
            this.messenger.send({event: "login"}),
                this.loginSuccessCb = e || function () {
                }, this.loginFailedCb = r || function () {
            }
        }, loginSuccess: function (t) {
        }, loginFailed: function (t) {
        }, logout: function () {
            this.messenger.send({event: "logout"})
        }, log: function (t) {
            t.roleName && (t.rolename = t.roleName, delete t.roleName), "createrole" === t.action && (t.action = "creterole"), ["grender", "gloadbegin", "gloadend"].indexOf(t.action) >= 0 && !t.actime && (t.actime = t.actionvalue), this.messenger.send({
                event: "log",
                data: t
            })
        }, initLog: function () {
            try {
                this.log({
                    action: "gsdkinit",
                    actime: window.performance.timing.navigationStart + Math.floor(performance.now())
                })
            } catch (t) {
            }
        }, pageStartLog: function () {
            try {
                this.log({action: "gstart", actime: window.performance.timing.navigationStart})
            } catch (t) {
            }
        }, pageShowLog: function () {
            try {
                this.log({action: "genter", actime: window.performance.timing.responseStart})
            } catch (t) {
            }
        }, placeOrder: function (t) {
            this.messenger.send({event: "placeOrder", data: t})
        }, getChannelCode: function (t) {
            this.messenger.send({event: "getChannelCode"}), this.getChannelCodeCb = t
        }, getInviter: function (t) {
            this.messenger.send({event: "getInviter"}), this.getInviterCb = t
        }, isShowShare: function (t) {
            this.messenger.send({event: "isShowShare"}), this.isShowShareCb = t
        }, showShare: function () {
            this.messenger.send({event: "showShare"})
        }, isShowFocus: function (t) {
            this.messenger.send({event: "isShowFocus"}), this.isShowFocusCb = t
        }, isFocus: function (t) {
            this.messenger.send({event: "isFocus"}), this.isFocusCb = t
        }, showFocus: function () {
            this.messenger.send({event: "showFocus"})
        }, isShowFcm: function (t) {
            this.messenger.send({event: "isShowFcm"}), this.isShowFcmCb = t
        }, getAdultStatus: function (t) {
            this.messenger.send({event: "getAdultStatus"}), this.getAdultStatusCb = t
        }, verifyAdult: function (t) {
            this.messenger.send({event: "verifyAdult"}), this.verifyAdultCb = t
        }, isShowForum: function (t) {
            this.messenger.send({event: "isShowForum"}), this.isShowForumCb = t
        }, toForum: function () {
            this.messenger.send({event: "toForum"})
        }, isChangeCount: function (t) {
            this.isChangeAccount(t)
        }, isChangeAccount: function (t) {
            this.messenger.send({event: "isChangeAccount"}), this.isChangeAccountCb = t
        }, isShowSaveGame: function (t) {
            this.messenger.send({event: "isShowSaveGame"}), this.isShowSaveGameCb = t
        }, saveGame: function () {
            this.messenger.send({event: "saveGame"})
        }, listenEvent: function (t) {
            var n = t.data, e = t.event;
            "loginSuccess" === e ? (this.loginSuccess(n), this.loginSuccessCb(n)) : "loginFailed" === e ? (this.loginFailed(n), this.loginFailedCb(n)) : "getChannelCode" === e ? this.getChannelCodeCb(n) : "getInviter" === e ? this.getInviterCb(n) : "isShowShare" === e ? this.isShowShareCb(n) : "shareSuccess" === e ? this.shareSuccessCb() : "shareCancel" === e ? this.shareCancelCb() : "focusSuccess" === e ? this.focusSuccessCb() : "isShowFocus" === e ? this.isShowFocusCb(n) : "isFocus" === e ? this.isFocusCb(n) : "isShowFcm" === e ? this.isShowFcmCb(n) : "getAdultStatus" === e ? this.getAdultStatusCb(n) : "verifyAdult" === e ? this.verifyAdultCb(n) : "isShowForum" === e ? this.isShowForumCb(n) : "isChangeAccount" === e ? this.isChangeAccountCb(n) : "isShowSaveGame" === e && this.isShowSaveGameCb(n)
        }
    }, window.WanGameH5sdk.init()
}, function (t, n) {
    var e = t.exports = {version: "2.6.1"};
    "number" == typeof __e && (__e = e)
}, function (t, n) {
    var e = t.exports = "undefined" != typeof window && window.Math == Math ? window : "undefined" != typeof self && self.Math == Math ? self : Function("return this")();
    "number" == typeof __g && (__g = e)
}, function (t, n, e) {
    var r = e(43)("wks"), o = e(31), i = e(2).Symbol, u = "function" == typeof i, s = t.exports = function (t) {
        return r[t] || (r[t] = u && i[t] || (u ? i : o)("Symbol." + t))
    };
    s.store = r
}, , , function (t, n, e) {
    var r = e(2), o = e(1), i = e(20), u = e(11), s = e(12), c = "prototype", f = function (t, n, e) {
        var a, l, h, p = t & f.F, d = t & f.G, v = t & f.S, g = t & f.P, y = t & f.B, m = t & f.W,
            b = d ? o : o[n] || (o[n] = {}), S = b[c], w = d ? r : v ? r[n] : (r[n] || {})[c];
        d && (e = n);
        for (a in e) l = !p && w && void 0 !== w[a], l && s(b, a) || (h = l ? w[a] : e[a], b[a] = d && "function" != typeof w[a] ? e[a] : y && l ? i(h, r) : m && w[a] == h ? function (t) {
            var n = function (n, e, r) {
                if (this instanceof t) {
                    switch (arguments.length) {
                        case 0:
                            return new t;
                        case 1:
                            return new t(n);
                        case 2:
                            return new t(n, e)
                    }
                    return new t(n, e, r)
                }
                return t.apply(this, arguments)
            };
            return n[c] = t[c], n
        }(h) : g && "function" == typeof h ? i(Function.call, h) : h, g && ((b.virtual || (b.virtual = {}))[a] = h, t & f.R && S && !S[a] && u(S, a, h)))
    };
    f.F = 1, f.G = 2, f.S = 4, f.P = 8, f.B = 16, f.W = 32, f.U = 64, f.R = 128, t.exports = f
}, function (t, n, e) {
    var r = e(10);
    t.exports = function (t) {
        if (!r(t)) throw TypeError(t + " is not an object!");
        return t
    }
}, function (t, n, e) {
    t.exports = !e(16)(function () {
        return 7 != Object.defineProperty({}, "a", {
            get: function () {
                return 7
            }
        }).a
    })
}, function (t, n, e) {
    var r = e(7), o = e(65), i = e(45), u = Object.defineProperty;
    n.f = e(8) ? Object.defineProperty : function (t, n, e) {
        if (r(t), n = i(n, !0), r(e), o) try {
            return u(t, n, e)
        } catch (t) {
        }
        if ("get" in e || "set" in e) throw TypeError("Accessors not supported!");
        return "value" in e && (t[n] = e.value), t
    }
}, function (t, n) {
    t.exports = function (t) {
        return "object" == typeof t ? null !== t : "function" == typeof t
    }
}, function (t, n, e) {
    var r = e(9), o = e(30);
    t.exports = e(8) ? function (t, n, e) {
        return r.f(t, n, o(1, e))
    } : function (t, n, e) {
        return t[n] = e, t
    }
}, function (t, n) {
    var e = {}.hasOwnProperty;
    t.exports = function (t, n) {
        return e.call(t, n)
    }
}, function (t, n, e) {
    var r = e(66), o = e(39);
    t.exports = function (t) {
        return r(o(t))
    }
}, function (t, n) {
    "use strict";
    n.__esModule = !0, n.default = function (t, n) {
        if (!(t instanceof n)) throw new TypeError("Cannot call a class as a function")
    }
}, function (t, n, e) {
    "use strict";

    function r(t) {
        return t && t.__esModule ? t : {default: t}
    }

    n.__esModule = !0;
    var o = e(91), i = r(o);
    n.default = function () {
        function t(t, n) {
            for (var e = 0; e < n.length; e++) {
                var r = n[e];
                r.enumerable = r.enumerable || !1, r.configurable = !0, "value" in r && (r.writable = !0), (0, i.default)(t, r.key, r)
            }
        }

        return function (n, e, r) {
            return e && t(n.prototype, e), r && t(n, r), n
        }
    }()
}, function (t, n) {
    t.exports = function (t) {
        try {
            return !!t()
        } catch (t) {
            return !0
        }
    }
}, , function (t, n) {
    var e = {}.toString;
    t.exports = function (t) {
        return e.call(t).slice(8, -1)
    }
}, function (t, n) {
    t.exports = {}
}, function (t, n, e) {
    var r = e(25);
    t.exports = function (t, n, e) {
        if (r(t), void 0 === n) return t;
        switch (e) {
            case 1:
                return function (e) {
                    return t.call(n, e)
                };
            case 2:
                return function (e, r) {
                    return t.call(n, e, r)
                };
            case 3:
                return function (e, r, o) {
                    return t.call(n, e, r, o)
                }
        }
        return function () {
            return t.apply(n, arguments)
        }
    }
}, function (t, n) {
    t.exports = !0
}, function (t, n, e) {
    var r = e(69), o = e(40);
    t.exports = Object.keys || function (t) {
        return r(t, o)
    }
}, function (t, n, e) {
    "use strict";

    function r(t) {
        return t && t.__esModule ? t : {default: t}
    }

    n.__esModule = !0;
    var o = e(109), i = r(o), u = e(108), s = r(u),
        c = "function" == typeof s.default && "symbol" == typeof i.default ? function (t) {
            return typeof t
        } : function (t) {
            return t && "function" == typeof s.default && t.constructor === s.default && t !== s.default.prototype ? "symbol" : typeof t
        };
    n.default = "function" == typeof s.default && "symbol" === c(i.default) ? function (t) {
        return "undefined" == typeof t ? "undefined" : c(t)
    } : function (t) {
        return t && "function" == typeof s.default && t.constructor === s.default && t !== s.default.prototype ? "symbol" : "undefined" == typeof t ? "undefined" : c(t)
    }
}, , function (t, n) {
    t.exports = function (t) {
        if ("function" != typeof t) throw TypeError(t + " is not a function!");
        return t
    }
}, function (t, n, e) {
    "use strict";

    function r(t) {
        return t && t.__esModule ? t : {default: t}
    }

    n.__esModule = !0;
    var o = e(37), i = r(o);
    n.default = i.default || function (t) {
        for (var n = 1; n < arguments.length; n++) {
            var e = arguments[n];
            for (var r in e) Object.prototype.hasOwnProperty.call(e, r) && (t[r] = e[r])
        }
        return t
    }
}, function (t, n, e) {
    var r = e(9).f, o = e(12), i = e(3)("toStringTag");
    t.exports = function (t, n, e) {
        t && !o(t = e ? t : t.prototype, i) && r(t, i, {configurable: !0, value: n})
    }
}, , function (t, n) {
    n.f = {}.propertyIsEnumerable
}, function (t, n) {
    t.exports = function (t, n) {
        return {enumerable: !(1 & t), configurable: !(2 & t), writable: !(4 & t), value: n}
    }
}, function (t, n) {
    var e = 0, r = Math.random();
    t.exports = function (t) {
        return "Symbol(".concat(void 0 === t ? "" : t, ")_", (++e + r).toString(36))
    }
}, function (t, n, e) {
    var r = e(39);
    t.exports = function (t) {
        return Object(r(t))
    }
}, , , , , function (t, n, e) {
    t.exports = {default: e(110), __esModule: !0}
}, function (t, n, e) {
    var r = e(10), o = e(2).document, i = r(o) && r(o.createElement);
    t.exports = function (t) {
        return i ? o.createElement(t) : {}
    }
}, function (t, n) {
    t.exports = function (t) {
        if (void 0 == t) throw TypeError("Can't call method on  " + t);
        return t
    }
}, function (t, n) {
    t.exports = "constructor,hasOwnProperty,isPrototypeOf,propertyIsEnumerable,toLocaleString,toString,valueOf".split(",")
}, function (t, n) {
    n.f = Object.getOwnPropertySymbols
}, function (t, n, e) {
    var r = e(43)("keys"), o = e(31);
    t.exports = function (t) {
        return r[t] || (r[t] = o(t))
    }
}, function (t, n, e) {
    var r = e(1), o = e(2), i = "__core-js_shared__", u = o[i] || (o[i] = {});
    (t.exports = function (t, n) {
        return u[t] || (u[t] = void 0 !== n ? n : {})
    })("versions", []).push({
        version: r.version,
        mode: e(21) ? "pure" : "global",
        copyright: "© 2018 Denis Pushkarev (zloirock.ru)"
    })
}, function (t, n) {
    var e = Math.ceil, r = Math.floor;
    t.exports = function (t) {
        return isNaN(t = +t) ? 0 : (t > 0 ? r : e)(t)
    }
}, function (t, n, e) {
    var r = e(10);
    t.exports = function (t, n) {
        if (!r(t)) return t;
        var e, o;
        if (n && "function" == typeof (e = t.toString) && !r(o = e.call(t))) return o;
        if ("function" == typeof (e = t.valueOf) && !r(o = e.call(t))) return o;
        if (!n && "function" == typeof (e = t.toString) && !r(o = e.call(t))) return o;
        throw TypeError("Can't convert object to primitive value")
    }
}, function (t, n, e) {
    var r = e(2), o = e(1), i = e(21), u = e(47), s = e(9).f;
    t.exports = function (t) {
        var n = o.Symbol || (o.Symbol = i ? {} : r.Symbol || {});
        "_" == t.charAt(0) || t in n || s(n, t, {value: u.f(t)})
    }
}, function (t, n, e) {
    n.f = e(3)
}, function (t, n, e) {
    var r = e(7), o = e(122), i = e(40), u = e(42)("IE_PROTO"), s = function () {
    }, c = "prototype", f = function () {
        var t, n = e(38)("iframe"), r = i.length, o = "<", u = ">";
        for (n.style.display = "none", e(61).appendChild(n), n.src = "javascript:", t = n.contentWindow.document, t.open(), t.write(o + "script" + u + "document.F=Object" + o + "/script" + u), t.close(), f = t.F; r--;) delete f[c][i[r]];
        return f()
    };
    t.exports = Object.create || function (t, n) {
        var e;
        return null !== t ? (s[c] = r(t), e = new s, s[c] = null, e[u] = t) : e = f(), void 0 === n ? e : o(e, n)
    }
}, , , , , function (t, n, e) {
    var r = e(29), o = e(30), i = e(13), u = e(45), s = e(12), c = e(65), f = Object.getOwnPropertyDescriptor;
    n.f = e(8) ? f : function (t, n) {
        if (t = i(t), n = u(n, !0), c) try {
            return f(t, n)
        } catch (t) {
        }
        if (s(t, n)) return o(!r.f.call(t, n), t[n])
    }
}, , , , , , function (t, n, e) {
    "use strict";
    var r = e(124)(!0);
    e(67)(String, "String", function (t) {
        this._t = String(t), this._i = 0
    }, function () {
        var t, n = this._t, e = this._i;
        return e >= n.length ? {value: void 0, done: !0} : (t = r(n, e), this._i += t.length, {value: t, done: !1})
    })
}, function (t, n, e) {
    e(126);
    for (var r = e(2), o = e(11), i = e(19), u = e(3)("toStringTag"), s = "CSSRuleList,CSSStyleDeclaration,CSSValueList,ClientRectList,DOMRectList,DOMStringList,DOMTokenList,DataTransferItemList,FileList,HTMLAllCollection,HTMLCollection,HTMLFormElement,HTMLSelectElement,MediaList,MimeTypeArray,NamedNodeMap,NodeList,PaintRequestList,Plugin,PluginArray,SVGLengthList,SVGNumberList,SVGPathSegList,SVGPointList,SVGStringList,SVGTransformList,SourceBufferList,StyleSheetList,TextTrackCueList,TextTrackList,TouchList".split(","), c = 0; c < s.length; c++) {
        var f = s[c], a = r[f], l = a && a.prototype;
        l && !l[u] && o(l, u, f), i[f] = i.Array
    }
}, function (t, n, e) {
    var r = e(2).document;
    t.exports = r && r.documentElement
}, , function (t, n, e) {
    var r = e(44), o = Math.min;
    t.exports = function (t) {
        return t > 0 ? o(r(t), 9007199254740991) : 0
    }
}, function (t, n) {
}, function (t, n, e) {
    t.exports = !e(8) && !e(16)(function () {
        return 7 != Object.defineProperty(e(38)("div"), "a", {
            get: function () {
                return 7
            }
        }).a
    })
}, function (t, n, e) {
    var r = e(18);
    t.exports = Object("z").propertyIsEnumerable(0) ? Object : function (t) {
        return "String" == r(t) ? t.split("") : Object(t)
    }
}, function (t, n, e) {
    "use strict";
    var r = e(21), o = e(6), i = e(70), u = e(11), s = e(19), c = e(118), f = e(27), a = e(74), l = e(3)("iterator"),
        h = !([].keys && "next" in [].keys()), p = "@@iterator", d = "keys", v = "values", g = function () {
            return this
        };
    t.exports = function (t, n, e, y, m, b, S) {
        c(e, n, y);
        var w, x, C, O = function (t) {
                if (!h && t in M) return M[t];
                switch (t) {
                    case d:
                        return function () {
                            return new e(this, t)
                        };
                    case v:
                        return function () {
                            return new e(this, t)
                        }
                }
                return function () {
                    return new e(this, t)
                }
            }, _ = n + " Iterator", F = m == v, j = !1, M = t.prototype, P = M[l] || M[p] || m && M[m], E = P || O(m),
            L = m ? F ? O("entries") : E : void 0, A = "Array" == n ? M.entries || P : P;
        if (A && (C = a(A.call(new t)), C !== Object.prototype && C.next && (f(C, _, !0), r || "function" == typeof C[l] || u(C, l, g))), F && P && P.name !== v && (j = !0, E = function () {
            return P.call(this)
        }), r && !S || !h && !j && M[l] || u(M, l, E), s[n] = E, s[_] = g, m) if (w = {
            values: F ? E : O(v),
            keys: b ? E : O(d),
            entries: L
        }, S) for (x in w) x in M || i(M, x, w[x]); else o(o.P + o.F * (h || j), n, w);
        return w
    }
}, function (t, n, e) {
    var r = e(69), o = e(40).concat("length", "prototype");
    n.f = Object.getOwnPropertyNames || function (t) {
        return r(t, o)
    }
}, function (t, n, e) {
    var r = e(12), o = e(13), i = e(115)(!1), u = e(42)("IE_PROTO");
    t.exports = function (t, n) {
        var e, s = o(t), c = 0, f = [];
        for (e in s) e != u && r(s, e) && f.push(e);
        for (; n.length > c;) r(s, e = n[c++]) && (~i(f, e) || f.push(e));
        return f
    }
}, function (t, n, e) {
    t.exports = e(11)
}, , , , function (t, n, e) {
    var r = e(12), o = e(32), i = e(42)("IE_PROTO"), u = Object.prototype;
    t.exports = Object.getPrototypeOf || function (t) {
        return t = o(t), r(t, i) ? t[i] : "function" == typeof t.constructor && t instanceof t.constructor ? t.constructor.prototype : t instanceof Object ? u : null
    }
}, , , , , , , , , , , , , , , , , function (t, n, e) {
    t.exports = {default: e(111), __esModule: !0}
}, , , , , , , , , , , , , , , , function (t, n, e) {
    "use strict";

    function r(t) {
        return t && t.__esModule ? t : {default: t}
    }

    Object.defineProperty(n, "__esModule", {value: !0});
    var o = e(26), i = r(o), u = e(23), s = r(u), c = e(14), f = r(c), a = e(15), l = r(a), h = function () {
        function t(n, e) {
            (0, f.default)(this, t), this.origin = e || "*", this.initlisten(n)
        }

        return (0, l.default)(t, [{
            key: "initlisten", value: function (t) {
                window.addEventListener("message", function (n) {
                    "object" == (0, s.default)(n.data) && "h5game" == n.data.rule && t(n.data)
                }, !1)
            }
        }, {
            key: "send", value: function (t, n) {
                if ("object" === ("undefined" == typeof t ? "undefined" : (0, s.default)(t))) {
                    var e = (0, i.default)({rule: "h5game"}, t);
                    document.getElementById(n) ? document.getElementById(n).contentWindow.postMessage(e, this.origin) : window.parent.window.postMessage(e, this.origin)
                }
            }
        }]), t
    }();
    n.default = h
}, function (t, n, e) {
    t.exports = {default: e(112), __esModule: !0}
}, function (t, n, e) {
    t.exports = {default: e(113), __esModule: !0}
}, function (t, n, e) {
    e(127), t.exports = e(1).Object.assign
}, function (t, n, e) {
    e(128);
    var r = e(1).Object;
    t.exports = function (t, n, e) {
        return r.defineProperty(t, n, e)
    }
}, function (t, n, e) {
    e(129), e(64), e(130), e(131), t.exports = e(1).Symbol
}, function (t, n, e) {
    e(59), e(60), t.exports = e(47).f("iterator")
}, function (t, n) {
    t.exports = function () {
    }
}, function (t, n, e) {
    var r = e(13), o = e(63), i = e(125);
    t.exports = function (t) {
        return function (n, e, u) {
            var s, c = r(n), f = o(c.length), a = i(u, f);
            if (t && e != e) {
                for (; f > a;) if (s = c[a++], s != s) return !0
            } else for (; f > a; a++) if ((t || a in c) && c[a] === e) return t || a || 0;
            return !t && -1
        }
    }
}, function (t, n, e) {
    var r = e(22), o = e(41), i = e(29);
    t.exports = function (t) {
        var n = r(t), e = o.f;
        if (e) for (var u, s = e(t), c = i.f, f = 0; s.length > f;) c.call(t, u = s[f++]) && n.push(u);
        return n
    }
}, function (t, n, e) {
    var r = e(18);
    t.exports = Array.isArray || function (t) {
        return "Array" == r(t)
    }
}, function (t, n, e) {
    "use strict";
    var r = e(48), o = e(30), i = e(27), u = {};
    e(11)(u, e(3)("iterator"), function () {
        return this
    }), t.exports = function (t, n, e) {
        t.prototype = r(u, {next: o(1, e)}), i(t, n + " Iterator")
    }
}, function (t, n) {
    t.exports = function (t, n) {
        return {value: n, done: !!t}
    }
}, function (t, n, e) {
    var r = e(31)("meta"), o = e(10), i = e(12), u = e(9).f, s = 0, c = Object.isExtensible || function () {
        return !0
    }, f = !e(16)(function () {
        return c(Object.preventExtensions({}))
    }), a = function (t) {
        u(t, r, {value: {i: "O" + ++s, w: {}}})
    }, l = function (t, n) {
        if (!o(t)) return "symbol" == typeof t ? t : ("string" == typeof t ? "S" : "P") + t;
        if (!i(t, r)) {
            if (!c(t)) return "F";
            if (!n) return "E";
            a(t)
        }
        return t[r].i
    }, h = function (t, n) {
        if (!i(t, r)) {
            if (!c(t)) return !0;
            if (!n) return !1;
            a(t)
        }
        return t[r].w
    }, p = function (t) {
        return f && d.NEED && c(t) && !i(t, r) && a(t), t
    }, d = t.exports = {KEY: r, NEED: !1, fastKey: l, getWeak: h, onFreeze: p}
}, function (t, n, e) {
    "use strict";
    var r = e(22), o = e(41), i = e(29), u = e(32), s = e(66), c = Object.assign;
    t.exports = !c || e(16)(function () {
        var t = {}, n = {}, e = Symbol(), r = "abcdefghijklmnopqrst";
        return t[e] = 7, r.split("").forEach(function (t) {
            n[t] = t
        }), 7 != c({}, t)[e] || Object.keys(c({}, n)).join("") != r
    }) ? function (t, n) {
        for (var e = u(t), c = arguments.length, f = 1, a = o.f, l = i.f; c > f;) for (var h, p = s(arguments[f++]), d = a ? r(p).concat(a(p)) : r(p), v = d.length, g = 0; v > g;) l.call(p, h = d[g++]) && (e[h] = p[h]);
        return e
    } : c
}, function (t, n, e) {
    var r = e(9), o = e(7), i = e(22);
    t.exports = e(8) ? Object.defineProperties : function (t, n) {
        o(t);
        for (var e, u = i(n), s = u.length, c = 0; s > c;) r.f(t, e = u[c++], n[e]);
        return t
    }
}, function (t, n, e) {
    var r = e(13), o = e(68).f, i = {}.toString,
        u = "object" == typeof window && window && Object.getOwnPropertyNames ? Object.getOwnPropertyNames(window) : [],
        s = function (t) {
            try {
                return o(t)
            } catch (t) {
                return u.slice()
            }
        };
    t.exports.f = function (t) {
        return u && "[object Window]" == i.call(t) ? s(t) : o(r(t))
    }
}, function (t, n, e) {
    var r = e(44), o = e(39);
    t.exports = function (t) {
        return function (n, e) {
            var i, u, s = String(o(n)), c = r(e), f = s.length;
            return c < 0 || c >= f ? t ? "" : void 0 : (i = s.charCodeAt(c), i < 55296 || i > 56319 || c + 1 === f || (u = s.charCodeAt(c + 1)) < 56320 || u > 57343 ? t ? s.charAt(c) : i : t ? s.slice(c, c + 2) : (i - 55296 << 10) + (u - 56320) + 65536)
        }
    }
}, function (t, n, e) {
    var r = e(44), o = Math.max, i = Math.min;
    t.exports = function (t, n) {
        return t = r(t), t < 0 ? o(t + n, 0) : i(t, n)
    }
}, function (t, n, e) {
    "use strict";
    var r = e(114), o = e(119), i = e(19), u = e(13);
    t.exports = e(67)(Array, "Array", function (t, n) {
        this._t = u(t), this._i = 0, this._k = n
    }, function () {
        var t = this._t, n = this._k, e = this._i++;
        return !t || e >= t.length ? (this._t = void 0, o(1)) : "keys" == n ? o(0, e) : "values" == n ? o(0, t[e]) : o(0, [e, t[e]])
    }, "values"), i.Arguments = i.Array, r("keys"), r("values"), r("entries")
}, function (t, n, e) {
    var r = e(6);
    r(r.S + r.F, "Object", {assign: e(121)})
}, function (t, n, e) {
    var r = e(6);
    r(r.S + r.F * !e(8), "Object", {defineProperty: e(9).f})
}, function (t, n, e) {
    "use strict";
    var r = e(2), o = e(12), i = e(8), u = e(6), s = e(70), c = e(120).KEY, f = e(16), a = e(43), l = e(27), h = e(31),
        p = e(3), d = e(47), v = e(46), g = e(116), y = e(117), m = e(7), b = e(10), S = e(13), w = e(45), x = e(30),
        C = e(48), O = e(123), _ = e(53), F = e(9), j = e(22), M = _.f, P = F.f, E = O.f, L = r.Symbol, A = r.JSON,
        k = A && A.stringify, T = "prototype", I = p("_hidden"), N = p("toPrimitive"), G = {}.propertyIsEnumerable,
        D = a("symbol-registry"), R = a("symbols"), W = a("op-symbols"), V = Object[T], H = "function" == typeof L,
        B = r.QObject, J = !B || !B[T] || !B[T].findChild, z = i && f(function () {
            return 7 != C(P({}, "a", {
                get: function () {
                    return P(this, "a", {value: 7}).a
                }
            })).a
        }) ? function (t, n, e) {
            var r = M(V, n);
            r && delete V[n], P(t, n, e), r && t !== V && P(V, n, r)
        } : P, K = function (t) {
            var n = R[t] = C(L[T]);
            return n._k = t, n
        }, q = H && "symbol" == typeof L.iterator ? function (t) {
            return "symbol" == typeof t
        } : function (t) {
            return t instanceof L
        }, Y = function (t, n, e) {
            return t === V && Y(W, n, e), m(t), n = w(n, !0), m(e), o(R, n) ? (e.enumerable ? (o(t, I) && t[I][n] && (t[I][n] = !1), e = C(e, {enumerable: x(0, !1)})) : (o(t, I) || P(t, I, x(1, {})), t[I][n] = !0), z(t, n, e)) : P(t, n, e)
        }, Q = function (t, n) {
            m(t);
            for (var e, r = g(n = S(n)), o = 0, i = r.length; i > o;) Y(t, e = r[o++], n[e]);
            return t
        }, U = function (t, n) {
            return void 0 === n ? C(t) : Q(C(t), n)
        }, X = function (t) {
            var n = G.call(this, t = w(t, !0));
            return !(this === V && o(R, t) && !o(W, t)) && (!(n || !o(this, t) || !o(R, t) || o(this, I) && this[I][t]) || n)
        }, Z = function (t, n) {
            if (t = S(t), n = w(n, !0), t !== V || !o(R, n) || o(W, n)) {
                var e = M(t, n);
                return !e || !o(R, n) || o(t, I) && t[I][n] || (e.enumerable = !0), e
            }
        }, $ = function (t) {
            for (var n, e = E(S(t)), r = [], i = 0; e.length > i;) o(R, n = e[i++]) || n == I || n == c || r.push(n);
            return r
        }, tt = function (t) {
            for (var n, e = t === V, r = E(e ? W : S(t)), i = [], u = 0; r.length > u;) !o(R, n = r[u++]) || e && !o(V, n) || i.push(R[n]);
            return i
        };
    H || (L = function () {
        if (this instanceof L) throw TypeError("Symbol is not a constructor!");
        var t = h(arguments.length > 0 ? arguments[0] : void 0), n = function (e) {
            this === V && n.call(W, e), o(this, I) && o(this[I], t) && (this[I][t] = !1), z(this, t, x(1, e))
        };
        return i && J && z(V, t, {configurable: !0, set: n}), K(t)
    }, s(L[T], "toString", function () {
        return this._k
    }), _.f = Z, F.f = Y, e(68).f = O.f = $, e(29).f = X, e(41).f = tt, i && !e(21) && s(V, "propertyIsEnumerable", X, !0), d.f = function (t) {
        return K(p(t))
    }), u(u.G + u.W + u.F * !H, {Symbol: L});
    for (var nt = "hasInstance,isConcatSpreadable,iterator,match,replace,search,species,split,toPrimitive,toStringTag,unscopables".split(","), et = 0; nt.length > et;) p(nt[et++]);
    for (var rt = j(p.store), ot = 0; rt.length > ot;) v(rt[ot++]);
    u(u.S + u.F * !H, "Symbol", {
        for: function (t) {
            return o(D, t += "") ? D[t] : D[t] = L(t)
        }, keyFor: function (t) {
            if (!q(t)) throw TypeError(t + " is not a symbol!");
            for (var n in D) if (D[n] === t) return n
        }, useSetter: function () {
            J = !0
        }, useSimple: function () {
            J = !1
        }
    }), u(u.S + u.F * !H, "Object", {
        create: U,
        defineProperty: Y,
        defineProperties: Q,
        getOwnPropertyDescriptor: Z,
        getOwnPropertyNames: $,
        getOwnPropertySymbols: tt
    }), A && u(u.S + u.F * (!H || f(function () {
        var t = L();
        return "[null]" != k([t]) || "{}" != k({a: t}) || "{}" != k(Object(t))
    })), "JSON", {
        stringify: function (t) {
            for (var n, e, r = [t], o = 1; arguments.length > o;) r.push(arguments[o++]);
            if (e = n = r[1], (b(n) || void 0 !== t) && !q(t)) return y(n) || (n = function (t, n) {
                if ("function" == typeof e && (n = e.call(this, t, n)), !q(n)) return n
            }), r[1] = n, k.apply(A, r)
        }
    }), L[T][N] || e(11)(L[T], N, L[T].valueOf), l(L, "Symbol"), l(Math, "Math", !0), l(r.JSON, "JSON", !0)
}, function (t, n, e) {
    e(46)("asyncIterator")
}, function (t, n, e) {
    e(46)("observable")
}]);