var orderModule = angular.module("order");

orderModule.controller("OrderInitController", function($scope, $rootScope, usersResource, authService) {

	$scope.goToCreditOrder = function() {
		if(!$rootScope.user || ($rootScope.user.publicKey.indexOf("@") === -1)) {
			authService.openAuthDialog(false, true);
		} else {
			window.location.href = window.context + "#/credit";
		}
	};

	$scope.goToBorrowOrder = function() {
		if(!$rootScope.user || ($rootScope.user.publicKey.indexOf("@") === -1)) {
			authService.openAuthDialog(false, true);
		} else {
			window.location.href = window.context + "#/borrow";
		}
	};
});
