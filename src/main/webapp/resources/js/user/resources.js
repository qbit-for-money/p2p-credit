var userModule = angular.module("user");

userModule.factory("usersResource", function($resource) {
	return $resource(window.context + "webapi/users", {}, {
		current: {method: "GET", url: window.context + "webapi/users/current"},
		currentAltId: {method: "GET", url: window.context + "webapi/users/current/alt-id"},
		byAltId: {method: "GET", url: window.context + "webapi/users/:id/alt-id", params: {id: "@id"}},
		logout: {method: "POST", url: window.context + "webapi/users/logout"},
		setMachineId: {method: "POST", url: window.context + "webapi/users/machine"},
		setUserLocation: {method: "POST", url: window.context + "webapi/users/location"}
	});
});

userModule.run(function(userService) {
	userService.get();
});