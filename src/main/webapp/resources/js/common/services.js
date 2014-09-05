var commonModule = angular.module("common");

commonModule.factory("storage", function() {
	return {
		getItem: function(key) {
			return this[key];
		},
		setItem: function(key, value) {
			this[key] = value;
		},
		removeItem: function(key) {
			delete this[key];
		}
	};
});
commonModule.factory("localStorage", function(storage) {
	return window.localStorage || storage;
});
commonModule.factory("sessionStorage", function(storage) {
	return window.sessionStorage || storage;
});

commonModule.factory("delayedProxy", function($timeout) {
	return function(f, delay) {
		var proxy = function() {
			if (proxy.promise) {
				$timeout.cancel(proxy.promise);
			}
			var self = this;
			var args = arguments;
			proxy.promise = $timeout(function() {
				f.apply(self, args);
			}, delay);
		};
		return proxy;
	};
});

commonModule.factory("getRandBinary", function() {
	return Math.floor(Math.random() * 2);
});

commonModule.factory("isPhone", function() {
	return (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent));
});

commonModule.factory("phone", function() {
	return {
		isPhone: function() {
			return (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent));
		}
	};
});

commonModule.factory("location", function() {
	return {
		getIp: function() {
			var xmlhttp;
			if (window.XMLHttpRequest)
				xmlhttp = new XMLHttpRequest();
			else
				xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
			xmlhttp.open("GET", " http://api.hostip.info/get_html.php ", false);
			xmlhttp.send();
			var hostipInfo = xmlhttp.responseText.split("\n");
			for (var i = 0; hostipInfo.length >= i; i++) {
				var ipAddress = hostipInfo[i].split(":");
				if (ipAddress[0] == "IP")
					return ipAddress[1].replace(" ", "");
			}
			return false;
		},
		getLocation: function(callback) {
			var userLocation = {};
			jQuery.ajax({
				url: '//freegeoip.net/json/',
				type: 'POST',
				dataType: 'jsonp',
				success: function(location) {
					userLocation.latitude = location.latitude;
					userLocation.longitude = location.longitude;
					userLocation.country = location.country_name;
					userLocation.city = location.city;
					if (callback) {
						callback(userLocation);
					}
				}
			});
		}
	};
});