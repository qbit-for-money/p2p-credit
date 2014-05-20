var userModule = angular.module("user");

userModule.controller("UserController", function($scope, usersResource) {
	$scope.keyType = "user";
	$scope.logoutButton = "";
	//$scope.linkToProfile = window.context + "webapi/users/current";
	var currentUser = usersResource.current({});
	currentUser.$promise.then(function() {
		if (currentUser.publicKey) {
			if (currentUser.publicKey.indexOf("@") !== -1) {
				$scope.keyType = "envelope";
			} else {
				$scope.keyType = "user";
			}
			$scope.logoutButton = "glyphicon-log-out";
		}
	});
	
	$scope.goToProfile = function() {
		window.location.href = window.context + "#/" + currentUser.publicKey;
	};

	$scope.logout = function() {
		var logoutResponse = usersResource.logout({});
		logoutResponse.$promise.then(function() {
			location.reload();
		});
	};
});