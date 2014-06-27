var userModule = angular.module("user");

userModule.controller("UserController", function($scope, $rootScope, usersResource, authService) {
	$scope.keyType = "user";
	$scope.logoutButton = "";
	var currentUser = usersResource.current({});
	currentUser.$promise.then(function() {
		if (currentUser.publicKey) {
			if (currentUser.publicKey.indexOf("@") !== -1) {
				$scope.keyType = "envelope";
			} else {
				$scope.keyType = "user";
			}
			$scope.logoutButton = "glyphicon-log-out";
		} else {
			$scope.keyType = "user";
		}
	});
	angular.element("#user-form").removeClass("invisible");

	$rootScope.isGoogleAuth = function() {
		return $rootScope.user && ($rootScope.user.publicKey.indexOf("@") !== -1);
	};
	
	$scope.goToOrderInit = function() {
		window.location.href = window.context;
	};

	$scope.goToProfile = function() {
		$rootScope.userType = undefined;
		window.location.href = window.context + "#/users/" + currentUser.publicKey;
	};

	$scope.authWithGoogle = function() {
		window.location.href = window.context + "webapi/oauth2/authenticate";
	};
	
	$scope.openAuthDialog = function() {
		authService.openAuthDialog(false);
	};

	$scope.logout = function() {
		var logoutResponse = usersResource.logout({});
		logoutResponse.$promise.then(function() {
			$scope.goToOrderInit();
		});
	};
});