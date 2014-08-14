var userModule = angular.module("user");

userModule.factory("usersProfileResource", function($resource) {
	return $resource(window.context + "webapi/profiles", {}, {
		current: {method: "GET", url: window.context + "webapi/profiles/current"},
		getAll: {method: "GET"},
		getById: {method: "GET", url: window.context + "webapi/profiles/:id", params: {id: "@id"}},
		getShortById: {method: "GET", url: window.context + "webapi/profiles/short/:id", params: {id: "@id"}},
		updateUserMainAttributes: {method: "POST", url: window.context + "webapi/profiles/current"},
		updateUserSocialLinks: {method: "POST", url: window.context + "webapi/profiles/current/social"},
		updateUserVideos: {method: "POST", url: window.context + "webapi/profiles/current/video"},
		updatePassportEnabled: {method: "POST", url: window.context + "webapi/profiles/current/passport"},
		setUserPhoto: {method: "PUT", url: window.context + "webapi/profiles/current/photo"},
		getStatisticsById: {method: "GET", url: window.context + "webapi/statistics/:id", params: {id: "@id"}},
		getGlobalStatistics: {method: "GET", url: window.context + "webapi/statistics/global"}
	});
});

userModule.factory("photosResource", function($resource) {
	return $resource(window.context + "webapi/photos", {}, {
		getPhoto: {method: "GET",url: window.context + "webapi/photos/:id"},
		setUserPhoto: {method: "PUT", url: window.context + "webapi/photos/current"}
	});
});

userModule.factory("currencyResource", function($resource) {
	return $resource(window.context + "webapi/currency/:id", {}, {
		findAll: {method: "GET"}
	});
});

userModule.factory("languagesResource", function($resource) {
	return $resource(window.context + "webapi/languages", {}, {
		findAll: {method: "GET"}
	});
});