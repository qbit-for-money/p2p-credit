var userModule = angular.module("user");

userModule.factory("usersProfileResource", function($resource) {
	return $resource(window.context + "webapi/users-profile", {}, {
		current: {method: "GET", url: window.context + "webapi/users-profile/current"},
		getById: {method: "GET", url: window.context + "webapi/users-profile/byId"},
		updatePublicProfile: {method: "POST", url: window.context + "webapi/users-profile/updatePublicProfile"},
		updatePrivateProfile: {method: "POST", url: window.context + "webapi/users-profile/updatePrivateProfile"}
	});
});