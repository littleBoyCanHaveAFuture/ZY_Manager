window.YXFH5SDK = {
	openPay: function (e) {
		var message = { messageType: 'MESSAGE_OPENPAY', params: e };
		window.parent.postMessage(message, '*');
	},
	login: function () {
		var message = { messageType: 'MESSAGE_LOGIN' };
		window.parent.postMessage(message, '*');
	},
	antiAddiction: function () {
		var message = { messageType: 'MESSAGE_ANTI_ADDICTION' };
		window.parent.postMessage(message, '*');
	},
	init: function (callback) {
		window.addEventListener('message', function (e) {
			if (!e.data.messageType) return
			if (e.data.messageType === 'SEND_PARAMS') {
				callback && callback(e.data.params)
			}
		})
	}
};
