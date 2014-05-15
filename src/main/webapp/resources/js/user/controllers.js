var userModule = angular.module("user");

userModule.controller("UserController", function($scope, usersResource) {

	$scope.logout = function() {
		var logoutResponse = usersResource.logout({});
		logoutResponse.$promise.then(function() {
			location.reload();
		});
	};
});