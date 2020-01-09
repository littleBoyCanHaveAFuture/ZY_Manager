<!DOCTYPE html>
<html lang="utf-8">
<head>
    <base href="http://dev.soeasysdk.com:80/">
    <meta charset="utf-8"/>
    <title></title>
    <meta name="description" content="overview & stats"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link href="http://res.soeasysdk.com/soeasy/css/bootstrap.min.css" rel="stylesheet"/>
    <link rel="stylesheet" href="static/css/font-awesome.min.css"/>
    <link rel="stylesheet" href="http://res.soeasysdk.com/soeasy/css/ace.min.css?v=1.2"/>
    <!-- 下拉框 -->
    <link rel="stylesheet" href="http://res.soeasysdk.com/soeasy/css/chosen.css"/>

    <script type="text/javascript" src="http://res.soeasysdk.com/soeasy/js/jquery-1.9.1.min.js"></script>
    <script type="text/javascript" src="http://res.soeasysdk.com/soeasy/js/jquery.tips.js"></script>
    <style type="text/css">
        tr, td {
            border: 0px;
        }

        .table-bordered td {
            border-left: 0px;
        }

        .table th, .table td {
            border-top: 0px;
            padding: 8px;
            line-height: 20px;
            text-align: left;
            vertical-align: top;
        }

        #zhongxin {
            font-size: 14px;
        }

        #add_sdk {
            color: #3f89ec;
            font-size: 13px;
        }

        .payAddress {
            text-align: center;
            cursor: pointer;
            vertical-align: middle;
            display: inline-block;
            color: #438eb9;
            height: 25px;
        }

        .omsdk-cps-point-article {
            -webkit-border-radius: 0;
            background: #75a8d7;
            border: none;
            width: 200px;
            font-size: 12px;
            padding: 8px 5px;
            z-index: 1;
            position: relative;
            right: 4.8%;
            bottom: 42px;
            float: right;
            margin-bottom: -67px;
        }

        .omsdk-cps-point-article span {
            width: 11px;
            height: 13px;
            content: "";
            display: block;
            position: absolute;
            top: 10px;
            right: -11px;
            -webkit-transform: rotate(180deg);
            background: url(http://res.soeasysdk.com/soeasy/login/imagex/icon_arr.png) no-repeat;
        }

        .omsdk-cps-point-article p {
            color: #fff !important;
        }

        .addBtn {
            float: left;
            margin-left: 10px;
            margin-bottom: 10px;
            background-color: #3f89ec !important;
            border: 2px solid #3f89ec !important;
        }

        .addBtn:hover {
            background-color: #045e9f !important;
        }

        #selectFee {
            border: 2px solid #3f89ec;
            text-align: center;
            height: 22px;
            line-height: 22px;
            cursor: pointer;
            padding: 0 5px;
            background-color: #3f89ec;
            border-radius: 5px;
            color: #fff;
            font-size: 12px;
        }

        #feeUl {
            list-style: none; /* 去掉ul前面的符号 */
            margin: 0px; /* 与外界元素的距离为0 */
            padding: 0px; /* 与内部元素的距离为0 */
            display: none;
            margin-bottom: 10px;
        }

        #feeUl li {
            font-size: 0.9rem;
            height: 2rem;
            line-height: 2.1rem;
            color: #666;
            width: 100%;
            text-align: center;
            border-radius: 5px;
            border: 1px solid #f5f5f5;
            cursor: pointer;
        }
    </style>
</head>
<body>
<form action="" name="Form" id="Form" method="post">
    <input type="hidden" name="app_id" value="">
    <div id="zhongxin">
        <div style="margin-top:20px;height: 420px;overflow-x: hidden;overflow-y: auto;">
            <div>
                <table style="border:0px;margin-bottom: 0px;" id="table_top"
                       class="table table-striped table-bordered table-hover">
                    <tr id="channel_callback_url">
                        <td style="width:100px;text-align: left;padding-top: 13px;">渠道支付回调:</td>
                        <td style="padding-top: 13px;color:#000;font-weight: bold;">
                            <input type="hidden" name="callback_url" id="callback_url"/>
                        </td>
                    </tr>
                    <tr>
                        <td style="width:100px;text-align: left;padding-top: 13px;">渠道入口地址:</td>
                        <td>
                            <input class="showNormal" style="width:94%;font-weight: bold;color:#000;" type="text"
                                   name="url" id="url" maxlength="500" title="渠道入口地址"/>
                            <div id="showSpecial" style="color:#aaa;display:none;font-weight: bold;">注：需要游戏开发者技术大大解决<br>特殊渠道游戏入口<span
                                    style="color:red;cursor: pointer;" onclick="lookchanel()">查看</span>，zmcon之后的数据不可更改
                            </div>
                            <div id="soeasyurl" class="showNormal" style="color:red;">注：zmcon之后的数据不可更改<br>复制以上链接作为渠道游戏入口
                            </div>
                        </td>
                    </tr>
                </table>
                <table style="border:0px;" id="table_report" class="table table-striped table-bordered table-hover">
                    <tr id="gameTest" style="display: none;">
                        <td style="width:100px;text-align: left;padding-top: 13px;">游戏测试地址:</td>
                        <td>
                            <input style="width:95%;color:#000;font-weight: bold;" type="text" name="testUrl"
                                   id="testUrl" value="" maxlength="500" readonly="readonly"/>
                            <div>
                                <font id="testTip1" color="red" style="display: block;">注：保存即可激活测试游戏按钮</font>
                                <font color="#aaa" style="display: block;">游戏中心接入操作指南<a target="_blank"
                                                                                        onclick="javascript:window.open('http://www.soeasysdk.com/website_download.html')"><i>（查看）</i></a></font>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
            <div id="feeDiv" style="display: none;">
                <div style="margin-top:10px;" colspan="10">
                    <a onclick="addFeeItem(1)" class='btn btn-mini addBtn'>添加计费点</a>
                    <div style="float: left;margin:0 20px;">
                        <div id="selectFee" onclick="selectFeeTemplet()">计费模板</div>
                        <ul id="feeUl">

                        </ul>
                    </div>
                    <div style="float: left;color: #666;height:26px;line-height: 26px;">（计费点必填）</div>
                </div>
                <div id="feelist" style="margin-top: 10px;">
                </div>
            </div>
        </div>
        <div style="border-top:1px solid #eee;position: absolute;bottom: 0px;height: 58px;width: 100%;">
            <div style="position:fixed;bottom:10px;right:10px;">
                <div colspan="10">
                    <a class="btn btn-mini btn-primary" style="width:50px;font-size: 13px;" onclick="save();">保存</a>

                </div>
            </div>
        </div>
    </div>
    <div id="zhongxin2" class="center" style="display:none"><br/><br/><br/><br/><br/><img
            src="http://res.soeasysdk.com/soeasy/images/jiazai.gif"/><br/><h4 class="lighter block green">提交中...</h4>
    </div>

</form>
<!-- 引入 -->
<script type="text/javascript" src="http://res.soeasysdk.com/soeasy/js/chosen.jquery.min.js"></script><!-- 下拉框 -->
<script type="text/javascript">
    !function (a) {
        "use strict";

        function b(a, b) {
            var c = (65535 & a) + (65535 & b), d = (a >> 16) + (b >> 16) + (c >> 16);
            return d << 16 | 65535 & c
        }

        function c(a, b) {
            return a << b | a >>> 32 - b
        }

        function d(a, d, e, f, g, h) {
            return b(c(b(b(d, a), b(f, h)), g), e)
        }

        function e(a, b, c, e, f, g, h) {
            return d(b & c | ~b & e, a, b, f, g, h)
        }

        function f(a, b, c, e, f, g, h) {
            return d(b & e | c & ~e, a, b, f, g, h)
        }

        function g(a, b, c, e, f, g, h) {
            return d(b ^ c ^ e, a, b, f, g, h)
        }

        function h(a, b, c, e, f, g, h) {
            return d(c ^ (b | ~e), a, b, f, g, h)
        }

        function i(a, c) {
            a[c >> 5] |= 128 << c % 32, a[(c + 64 >>> 9 << 4) + 14] = c;
            var d, i, j, k, l, m = 1732584193, n = -271733879, o = -1732584194, p = 271733878;
            for (d = 0; d < a.length; d += 16) i = m, j = n, k = o, l = p, m = e(m, n, o, p, a[d], 7, -680876936), p = e(p, m, n, o, a[d + 1], 12, -389564586), o = e(o, p, m, n, a[d + 2], 17, 606105819), n = e(n, o, p, m, a[d + 3], 22, -1044525330), m = e(m, n, o, p, a[d + 4], 7, -176418897), p = e(p, m, n, o, a[d + 5], 12, 1200080426), o = e(o, p, m, n, a[d + 6], 17, -1473231341), n = e(n, o, p, m, a[d + 7], 22, -45705983), m = e(m, n, o, p, a[d + 8], 7, 1770035416), p = e(p, m, n, o, a[d + 9], 12, -1958414417), o = e(o, p, m, n, a[d + 10], 17, -42063), n = e(n, o, p, m, a[d + 11], 22, -1990404162), m = e(m, n, o, p, a[d + 12], 7, 1804603682), p = e(p, m, n, o, a[d + 13], 12, -40341101), o = e(o, p, m, n, a[d + 14], 17, -1502002290), n = e(n, o, p, m, a[d + 15], 22, 1236535329), m = f(m, n, o, p, a[d + 1], 5, -165796510), p = f(p, m, n, o, a[d + 6], 9, -1069501632), o = f(o, p, m, n, a[d + 11], 14, 643717713), n = f(n, o, p, m, a[d], 20, -373897302), m = f(m, n, o, p, a[d + 5], 5, -701558691), p = f(p, m, n, o, a[d + 10], 9, 38016083), o = f(o, p, m, n, a[d + 15], 14, -660478335), n = f(n, o, p, m, a[d + 4], 20, -405537848), m = f(m, n, o, p, a[d + 9], 5, 568446438), p = f(p, m, n, o, a[d + 14], 9, -1019803690), o = f(o, p, m, n, a[d + 3], 14, -187363961), n = f(n, o, p, m, a[d + 8], 20, 1163531501), m = f(m, n, o, p, a[d + 13], 5, -1444681467), p = f(p, m, n, o, a[d + 2], 9, -51403784), o = f(o, p, m, n, a[d + 7], 14, 1735328473), n = f(n, o, p, m, a[d + 12], 20, -1926607734), m = g(m, n, o, p, a[d + 5], 4, -378558), p = g(p, m, n, o, a[d + 8], 11, -2022574463), o = g(o, p, m, n, a[d + 11], 16, 1839030562), n = g(n, o, p, m, a[d + 14], 23, -35309556), m = g(m, n, o, p, a[d + 1], 4, -1530992060), p = g(p, m, n, o, a[d + 4], 11, 1272893353), o = g(o, p, m, n, a[d + 7], 16, -155497632), n = g(n, o, p, m, a[d + 10], 23, -1094730640), m = g(m, n, o, p, a[d + 13], 4, 681279174), p = g(p, m, n, o, a[d], 11, -358537222), o = g(o, p, m, n, a[d + 3], 16, -722521979), n = g(n, o, p, m, a[d + 6], 23, 76029189), m = g(m, n, o, p, a[d + 9], 4, -640364487), p = g(p, m, n, o, a[d + 12], 11, -421815835), o = g(o, p, m, n, a[d + 15], 16, 530742520), n = g(n, o, p, m, a[d + 2], 23, -995338651), m = h(m, n, o, p, a[d], 6, -198630844), p = h(p, m, n, o, a[d + 7], 10, 1126891415), o = h(o, p, m, n, a[d + 14], 15, -1416354905), n = h(n, o, p, m, a[d + 5], 21, -57434055), m = h(m, n, o, p, a[d + 12], 6, 1700485571), p = h(p, m, n, o, a[d + 3], 10, -1894986606), o = h(o, p, m, n, a[d + 10], 15, -1051523), n = h(n, o, p, m, a[d + 1], 21, -2054922799), m = h(m, n, o, p, a[d + 8], 6, 1873313359), p = h(p, m, n, o, a[d + 15], 10, -30611744), o = h(o, p, m, n, a[d + 6], 15, -1560198380), n = h(n, o, p, m, a[d + 13], 21, 1309151649), m = h(m, n, o, p, a[d + 4], 6, -145523070), p = h(p, m, n, o, a[d + 11], 10, -1120210379), o = h(o, p, m, n, a[d + 2], 15, 718787259), n = h(n, o, p, m, a[d + 9], 21, -343485551), m = b(m, i), n = b(n, j), o = b(o, k), p = b(p, l);
            return [m, n, o, p]
        }

        function j(a) {
            var b, c = "";
            for (b = 0; b < 32 * a.length; b += 8) c += String.fromCharCode(a[b >> 5] >>> b % 32 & 255);
            return c
        }

        function k(a) {
            var b, c = [];
            for (c[(a.length >> 2) - 1] = void 0, b = 0; b < c.length; b += 1) c[b] = 0;
            for (b = 0; b < 8 * a.length; b += 8) c[b >> 5] |= (255 & a.charCodeAt(b / 8)) << b % 32;
            return c
        }

        function l(a) {
            return j(i(k(a), 8 * a.length))
        }

        function m(a, b) {
            var c, d, e = k(a), f = [], g = [];
            for (f[15] = g[15] = void 0, e.length > 16 && (e = i(e, 8 * a.length)), c = 0; 16 > c; c += 1) f[c] = 909522486 ^ e[c], g[c] = 1549556828 ^ e[c];
            return d = i(f.concat(k(b)), 512 + 8 * b.length), j(i(g.concat(d), 640))
        }

        function n(a) {
            var b, c, d = "0123456789abcdef", e = "";
            for (c = 0; c < a.length; c += 1) b = a.charCodeAt(c), e += d.charAt(b >>> 4 & 15) + d.charAt(15 & b);
            return e
        }

        function o(a) {
            return unescape(encodeURIComponent(a))
        }

        function p(a) {
            return l(o(a))
        }

        function q(a) {
            return n(p(a))
        }

        function r(a, b) {
            return m(o(a), o(b))
        }

        function s(a, b) {
            return n(r(a, b))
        }

        function t(a, b, c) {
            return b ? c ? r(b, a) : s(b, a) : c ? p(a) : q(a)
        }

        "function" == typeof define && define.amd ? define(function () {
            return t
        }) : a.md5 = t
    }(this);

    var arr = {};
    var bodyjson = {};
    var parapame = {};
    var showTip = "0";
    var feeArr = new Array();
    var channel_sdk_name = "";
    var feeConfig;
    $(top.hangge());

    $.ajax({
        type: 'post',
        url: 'product/channelConfig',
        data: {"id": "25349"},
        success: function (res) {
//  	  			console.info(JSON.stringify(res))
            parapame = res;
            channel_sdk_name = res.channel_sdk_name;
            if (res.channel_config_key != null && res.channel_config_key != "") {
                arr = JSON.parse(parapame.channel_config_key);
                if (res.sdkindex == "315") {
                    $("#gameTest").show();
                    $("#testUrl").val("http://hlgame.mz30.cn/?code=" + res.fAppCode + "");
                    configZMKey(arr, res.fCpKey, res.fAppCode, res.fSecretKey, "table_report");
                    if (res.h5_html_path == "") {
                        $("#testTip1").show();
                    } else {
                        $("#testTip1").hide();
                    }
                } else {
                    configKey(arr, "table_report");
                    $("#gameTest").hide();
                }
            } else {
                $("#table_report").hide();
            }
            if (res.feeTempletList != null && res.feeTempletList != "") {
                var feeTempletList = res.feeTempletList;
                for (var i = 0; i < feeTempletList.length; i++) {
                    $("#feeUl").append(feeOption(feeTempletList[i]))
                }
            }
            feeConfig = res.channel_feecode_config;
            if (res.channel_feecode_config != null && res.channel_feecode_config != "") {
                if (res.channel_feecode_config == "1") {
                    $("#feeDiv").show();
                } else if (res.channel_feecode_config == "0") {
                    $("#feeDiv").hide();
                }
            } else {
                $("#feeDiv").hide();
            }
            setValue(res);

        },
        dataType: 'json'
    });

    function feeOption(feeTempletList) {
        return "<li onclick=selectFeeConfig('" + feeTempletList.templet_config + "','" + feeTempletList.templet_name + "')>" + feeTempletList.templet_name + "</li>"
    }

    function selectFeeConfig(templet_config, name) {
        $("#feelist").html("");
        $("#selectFee").html("计费模版：" + name);
        $("#feeUl").hide();
        var fee_config = JSON.parse(templet_config);
        for (var o in fee_config) {
            addFeeItem(2);
            var fee = fee_config[o];
            for (var attr in fee) {
                var list = $('#feelist').find('.' + attr).last().val(fee[attr]);
            }
        }

    }

    function selectFeeTemplet() {
        if ($("#feeUl li").length > 0) {
            if ($("#feeUl").is(":hidden")) {
                $("#feeUl").show();
            } else {
                $("#feeUl").hide();
            }
        } else {
            parent.parent.showInfo("当前无模板，请到计费模板管理中添加！")
        }
    }

    function lookchanel() {
        var url = document.getElementById("url").value;
        var info = "<b style='font-size:18px;'>特殊渠道解决方案</b>" +
            "<h6>需要游戏开发者技术大大参与解决，代码实现如下：</h6>" +
            "<pre>" +
            "&lt;script>" +
            "<br>var gotourl = \"" + url + "\";" +
            "<br>function GetRequest(){" +
            "<br>&nbsp;&nbsp;var url = location.href;" +
            "<br>&nbsp;&nbsp;var theRequest = new Object();" +
            "<br>&nbsp;&nbsp;if(url.indexOf(\"?\") != -1) {" +
            "<br>&nbsp;&nbsp;&nbsp;&nbsp;return \"&\"+url.substr(url.indexOf(\"?\")+1);" +
            "<br>&nbsp;&nbsp;}else{" +
            "<br>&nbsp;&nbsp;&nbsp;&nbsp;return \"\";" +
            "<br>&nbsp;&nbsp;}" +
            "<br>}" +
            "<br>window.location.replace(gotourl+GetRequest());" +
            "&lt;/script>" +
            "</pre>" +
            "<h6>创建一个扩展名为html的文件，复制以上代码放到该文件，文件自己保管然后放到云服务器生成http可访问链接，作为接入渠道游戏入口</h6>";
        top.showInfo(info);
    }

    function setValue(res) {
        var c = "";
        if (res.h5_url) {
            gotourl = res.h5_url;
            var fileName = md5(res.app_id + "" + res.channel_id);
            if (gotourl.indexOf("zmcon") < 0) {
                if (gotourl.indexOf("?") > 0) {
                    gotourl += "&zmcon=" + fileName;
                } else {
                    gotourl += "?zmcon=" + fileName;
                }
            }

            document.getElementById("url").value = gotourl;
        }
        if (res.channel_callback_url != null && res.channel_callback_url != "") {
            $("#channel_callback_url").show();
            var callback = res.channel_callback_url;
            callback = callback.replace("{appid}", res.app_id);
            callback = callback.replace("{sdkindex}", res.sdkindex);
            callback = callback.replace("{channel_code}", res.channel_sdk_code);
            $("#callback_url").after("" + callback + "");

            if (res.sdkindex == "10308") {
                callback = callback.replace("ret", "prelogin");
                $("#table_top").append("<tr>" +
                    "<td style='width:100px;text-align: left;padding-top: 13px;'>阅文渠道入口:</td>" +
                    "<td style='padding-top: 13px;color:#000;font-weight: bold;'>" +
                    callback +
                    "<div><font color='#aaa'>注：保存后生效，上面渠道入口地址不需要提供给渠道，提供该地址即可</font></div>" +
                    "</td>" +
                    "</tr>");
            }
            if (res.sdkindex == "10798") {
                callback = callback.replace("ret", "prelogin");
                $("#table_top").append("<tr>" +
                    "<td style='width:100px;text-align: left;padding-top: 13px;'>搜狗平台渠道入口:</td>" +
                    "<td style='padding-top: 13px;color:#000;font-weight: bold;'>" +
                    callback +
                    "<div><font color='#aaa'></font></div>" +
                    "</td>" +
                    "</tr>");
            }
            if (res.sdkindex == "10833") {
                callback = callback.replace("ret", "prelogin");
                $("#table_top").append("<tr>" +
                    "<td style='width:100px;text-align: left;padding-top: 13px;'>银联游戏渠道入口:</td>" +
                    "<td style='padding-top: 13px;color:#000;font-weight: bold;'>" +
                    callback +
                    "<div><font color='#aaa'></font></div>" +
                    "</td>" +
                    "</tr>");
            }
        } else {
            $("#channel_callback_url").hide();
        }
        if (res.config_key != null && res.config_key != "") {
            var arr = JSON.parse(res.config_key);
            for (var o in arr) {
                if (document.getElementById(o))
                    document.getElementById(o).value = arr[o];
            }
            if (showTip == "1") {
                //$("#showSpecial").show();
                //$(".showNormal").hide();

                $("#soeasyurl").html("注：提供给渠道的地址 <br>https://cn.soeasysdk.com/soeasysr/gameini/apps_conf_html/" + res.app_id + "/" + res.sdkindex + "/" + "<br>http://soeasysdk.com/soeasysr/gameini/apps_conf_html/" + res.app_id + "/" + res.sdkindex + "/");

            }
        } else {
            if (showTip == "1") {
                //$("#showSpecial").show();
                //$(".showNormal").hide();
                //lookchanel();

                $("#soeasyurl").html("注：提供给渠道的地址 <br>https://cn.soeasysdk.com/soeasysr/gameini/apps_conf_html/" + res.app_id + "/" + res.sdkindex + "/" + "<br>http://soeasysdk.com/soeasysr/gameini/apps_conf_html/" + res.app_id + "/" + res.sdkindex + "/");

            }
        }

        if (res.feecode_config != null && res.feecode_config != "") {
            var arr = JSON.parse(res.feecode_config);
            for (var o in arr) {
                addFeeItem(2);
                var fee = arr[o];
                for (var attr in fee) {
                    var list = $('#feelist').find('.' + attr).last().val(fee[attr]);
                }

            }
        }
    }

    function configKey(arr, id) {
        var c = "";
        for (var o in arr) {
            c += addTr(arr[o]);
        }
        $("#" + id + "").prepend(c);
    }

    function configZMKey(arr, fCpKey, fAppCode, fSecretKey, id) {
        var c = "";
        for (var o in arr) {
            c += addZMTr(arr[o], fCpKey, fAppCode, fSecretKey);
        }
        $("#" + id + "").prepend(c);
    }

    function addTr(o) {
        var valData = "";
        var valView = "";
        var valStyle = "style='width:94%;'";
        if (o["alert"]) showTip = o["alert"];
        if (o["val"]) {
            valData = o["val"];
            valView = "readonly='readonly'";
            valStyle = "style='width:94%;font-weight:bold;border: 0;color:#000;'";
        }
        return '<tr>' +
            '<td style="width:100px;text-align: left;padding-top: 13px;">' + o.showName + ':</td>' +
            '<td><input ' + valStyle + ' type="text" ' + valView + ' name="' + o.name + '" id="' + o.name + '" value="' + valData + '"  maxlength="100000000" title="' + o.showName + '" check="' + o.required + '">' +
            '<div><font color="#aaa">注：' + o.desc + '</font></div>' +
            '</td>' +
            '</tr>';
    }

    function addZMTr(o, fCpKey, fAppCode, fSecretKey) {
        var str = "";
        if (o.showName == "APPCODE") {
            str = fAppCode;
        } else if (o.showName == "CPKEY") {
            str = fCpKey;
        } else if (o.showName == "secretkey") {
            str = fSecretKey;
        }
        return '<tr>' +
            '<td style="width:100px;text-align: left;padding-top: 13px;">' + o.showName + ':</td>' +
            '<td><input style="width:94%;" type="text" name="' + o.name + '" id="' + o.name + '" readonly="readonly" value="' + str + '" maxlength="1000" title="' + o.showName + '" check="' + o.required + '">' +
            '<div><font color="#aaa">注：' + o.desc + '</font></div>' +
            '</td>' +
            '</tr>';
    }

    function checkParam() {
        for (var o in arr) {
            val = document.getElementById(arr[o].name).value;
            var check = document.getElementById(arr[o].name).getAttribute("check");
            val = val.trim();
            if (val == "" && check == "1") {
                $("#" + arr[o].name).tips({
                    side: 3,
                    msg: arr[o].showName + "不能为空！",
                    bg: '#AE81FF',
                    time: 2
                });
                return false;
            } else {
                bodyjson[arr[o].name] = val;
            }
        }
        return true;
    }

    function addFeeItem(type) {
        var html = '<table class="table table-striped table-bordered table-hover feeItem">';
        html += '<tr><td><a class="btn btn-mini" onclick="feeItemDel(this)">删除计费点</a></td></tr>';
        html += '<tr><td style="width:90px;text-align: left;padding-top: 13px;">feeId:</td>';
        html += '<td><input style="width:94%;" type="text" name="feeid" class="feeid" maxlength="500" title="feeId"/><div style="color:#aaa;">注：自定义soeasy平台计费点ID 如：8008</div></td></tr>';
        html += '<tr><td style="width:90px;text-align: left;padding-top: 13px;">waresid:</td>';
        html += '<td><input style="width:94%;" type="text" name="waresid" class="waresid" maxlength="500" title="waresid"/><div style="color:#aaa;">注：须与渠道后台配置时填写的计费点编码对应</div></td></tr>';
        html += '<tr><td style="width:90px;text-align: left;padding-top: 13px;">商品名称:</td>';
        html += '<td><input style="width:94%;" type="text" name="name" class="name" maxlength="500" title="商品名称"/><div style="color:#aaa;">注：用户要支付的道具或虚拟货币名称，如：金币</div></td></tr>';
        html += '<tr><td style="width:90px;text-align: left;padding-top: 13px;">订单描述:</td>';
        html += '<td><input style="width:94%;" type="text" name="desc" class="desc" maxlength="500" title="订单描述"/><div style="color:#aaa;">注：商品描述</div></td></tr>';
        html += '<tr><td style="width:90px;text-align: left;padding-top: 13px;">价格（单位分）:</td>';
        html += '<td><input style="width:94%;" type="text" name="price" class="price" maxlength="500" title="价格（单位分）"/><div style="color:#aaa;">注：该计费点的道具价格</div></td></tr>';
        html += '<tr><td style="width:90px;text-align: left;padding-top: 13px;">购买次数:</td>';
        html += '<td><input style="width:94%;" type="text" name="count" class="count" maxlength="500" title="购买次数"/ value="1"><div style="color:#aaa;">注：默认为1</div></td></tr>';
        html += '</table>';
        if (type == 1) {
            $('#feelist').prepend(html);
        } else if (type == 2) {
            $('#feelist').append(html);
        }
    }

    function IsURL(urlString) {
        regExp = /(http|ftp|https):\/\/[\w]+(.[\w]+)([\w\-\.,@?^=%&:\/~\+#]*[\w\-\@?^=%&\/~\+#])/;
        if (urlString.match(regExp)) return true;
        else return false;
    }

    //保存
    function save() {
        feeArr.length = 0;
        var flag = true;
        $('#feelist .feeItem').each(function () {
            var fee = {};
            var attr = $(this).find('.feeid');
            flag = checkNull(attr);
            if (!flag) return false;
            attr = $(this).find('.waresid');
            flag = checkNull(attr);
            if (!flag) return false;
            attr = $(this).find('.name');
            flag = checkNull(attr);
            if (!flag) return false;
            attr = $(this).find('.desc');
            flag = checkNull(attr);
            if (!flag) return false;
            attr = $(this).find('.price');
            flag = checkNull(attr);
            if (!flag) return false;
            attr = $(this).find('.count');
            flag = checkNull(attr);
            if (!flag) return false;
            flag = checkCount(attr);
            if (!flag) return false;

            fee['feeid'] = $(this).find('.feeid').val().trim();
            fee['waresid'] = $(this).find('.waresid').val().trim();
            fee['name'] = $(this).find('.name').val().trim();
            fee['price'] = $(this).find('.price').val().trim();
            fee['desc'] = $(this).find('.desc').val().trim();
            fee['count'] = $(this).find('.count').val().trim();
            feeArr.push(fee);
        });
        if (!checkParam()) return false;
        if ($("#url").val() == "") {
            $("#url").tips({
                side: 3,
                msg: "渠道入口地址不能为空！",
                bg: '#AE81FF',
                time: 2
            });
            return false;
        } else if (IsURL($("#url").val()) == false) {
            $("#url").tips({
                side: 3,
                msg: '请填写正确的源地址，并以http或https开头',
                bg: '#AE81FF',
                time: 2
            });
            $("#url").focus();
            return false;
        } else if ($("#url").val().indexOf("zmcon") < 0) {
            $("#url").tips({
                side: 3,
                msg: '请带上zmcon参数',
                bg: '#AE81FF',
                time: 2
            });
            $("#url").focus();
            return false;
        }
        if (feeConfig == "1") {
            if (feeArr.length < 1) {
                if ($('#feelist').children().length == 0) {
                    top.showInfo("请先填写计费点！");
                    return false;
                }
            }
        }
        parapame.config_key = JSON.stringify(bodyjson);
        parapame.feecode_config = JSON.stringify(feeArr);
        parapame.h5_url = $("#url").val().trim();
        var p = "";
        if ("" == "1") {
            parapame.t = "1";
        }
        if (flag == true) {
            $.ajax({
                type: 'post',
                url: 'product/updateChannelConfig',
                data: parapame,
                dataType: 'json',
                success: function (res) {
                    top.showH5SdkConfig(channel_sdk_name);
                    top.Dialog.close();
                }
            });
        }

    }

    $(document).ready(function () {
        //提示框
        $(".omsdk-cps-point-article").hide();
        $(".payAddress").hover(function () {
            $(this).next(".omsdk-cps-point-article").fadeIn(200);
        }, function () {
            $(this).next(".omsdk-cps-point-article").fadeOut(200);
        });
    });

    function checkNull(obj) {
        if (obj.val().trim() == '') {
            obj.tips({side: 3, msg: "不能为空！", bg: '#AE81FF', time: 2});
            return false;
        }
        return true;
    };

    function checkCount(obj) {
        if (obj.val().trim() < 1) {
            obj.tips({side: 3, msg: "购买次数必须大于0！", bg: '#AE81FF', time: 2});
            return false;
        }
        return true;
    };

    function feeItemDel(obj) {
        $(obj).parent().parent().parent().parent().remove();
    };

</script>
</body>
</html>