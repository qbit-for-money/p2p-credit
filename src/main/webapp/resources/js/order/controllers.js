var orderModule = angular.module("order");

orderModule.controller("OrderInitController", function($scope, $rootScope, usersResource, authService) {

	$scope.goToCreditOrder = function() {
		if(!$rootScope.user || ($rootScope.user.publicKey.indexOf("@") === -1)) {
			authService.openAuthDialog(false, true);
		} else {
			$rootScope.userType = "CREDITOR";
			window.location.href = window.context + "#/users/" +$rootScope.user.publicKey;
		}
	};

	$scope.goToBorrowOrder = function() {
		if(!$rootScope.user || ($rootScope.user.publicKey.indexOf("@") === -1)) {
			authService.openAuthDialog(false, true);
		} else {
			$rootScope.userType = "BORROWER";
			window.location.href = window.context + "#/users/" +$rootScope.user.publicKey;
		}
	};
});
