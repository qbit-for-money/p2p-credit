var orderModule = angular.module("order");

orderModule.controller("OrderInitController", function($scope, $rootScope, usersResource, authService) {
	
	$scope.goToProfile = function() {
		if (!$rootScope.user || ($rootScope.user.publicKey.indexOf("@") === -1)) {
			authService.openAuthDialog(false, true, "profile");
		} else {
			window.location.href = window.context + "#/users/" + $rootScope.user.publicKey;
		}
	};
});
