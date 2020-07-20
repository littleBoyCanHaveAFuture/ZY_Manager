//紫霞游戏内点击充值按钮引导充值
var ziXiaToPay={
		GetToPayUrl:function (obj){
			if(!ziXiaToPay.IsObject(obj)){			
				return 208;//alert("不是对象");
			}
			if(isNaN(parseInt(obj.uid))){			

				return 201;//alert("UID参数必须为纯数字");
			}
			if(isNaN(parseInt(obj.skey))){		

				return 202;//alert("skey参数必须为纯数字");
			}
			if(isNaN(parseInt(obj.money))){		

				return 203;//alert("money参数必须为纯数字");
			}
			if(isNaN(parseInt(obj.time))){		

				return 204;//alert("time参数必须为纯数字");
			}
			if(ziXiaToPay.IsNull(obj.gkey)){		

				return 205;//alert("gkey不能为空");
			}
			if(ziXiaToPay.IsNull(obj.order_id)){		

				return 206;//alert("order_id不能为空");
			}
			if(ziXiaToPay.IsNull(obj.sign)){		

				return 207;//alert("sign不能为空");
			}
			var platform = "zixia"
			obj['platform'] = platform;
			
			window.top.postMessage(obj,'*');

		},
		IsNull:function (data){
			return (data=="" || data==undefined || data==null) ? true : false;
		},
		IsObject:function (json){
			return Object.prototype.toString.call(json)==="[object Object]";
		},
		fadeOut:function (el){
			el.style.opacity = 1;
			(function fade() {
				if ((el.style.opacity -= .1) < 0) {
					el.style.display = "none";
				} else {
					requestAnimationFrame(fade);
				}
			})();
		},
		fadeIn:function (el){
			el.style.opacity = 0;
			el.style.display =  "block";

			(function fade() {
				var val = parseFloat(el.style.opacity);
				if (!((val += .1) > 1)) {
					el.style.opacity = val;
					requestAnimationFrame(fade);
				}
			})();
		},

}



var browser={  
	    versions:function(){   
	           var u = navigator.userAgent, app = navigator.appVersion;   
	           return {//移动终端浏览器版本信息   
	                trident: u.indexOf('Trident') > -1, //IE内核  
	                presto: u.indexOf('Presto') > -1, //opera内核  
	                webKit: u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核  
	                gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核  
	                mobile: !!u.match(/AppleWebKit.*Mobile.*/), //是否为移动终端  
	                ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/), //ios终端  
	                android: u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器  
	                iPhone: u.indexOf('iPhone') > -1 , //是否为iPhone或者QQHD浏览器  
	                iPad: u.indexOf('iPad') > -1, //是否iPad    
	                webApp: u.indexOf('Safari') == -1, //是否web应该程序，没有头部与底部  
	                weixin: u.indexOf('MicroMessenger') > -1, //是否微信   
	                qq: u.match(/\sQQ/i) == " qq" //是否QQ  
	            };  
	         }(),  
	         language:(navigator.browserLanguage || navigator.language).toLowerCase()  
	}   
//调用实例
//if(browser.versions.mobile || browser.versions.ios || browser.versions.android ||   
//	    browser.versions.iPhone || browser.versions.iPad){        
//	     
//	  }   

