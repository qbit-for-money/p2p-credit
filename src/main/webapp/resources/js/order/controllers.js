var orderModule = angular.module("order");

orderModule.controller("OrderInitController", function($scope, $rootScope) {
	$scope.goToCreditOrder = function() {
		window.location.href = window.context + "#/" + $rootScope.user.publicKey + "/credit";
	};
	
	$scope.goToBorrowOrder = function() {
		window.location.href = window.context + "#/" + $rootScope.user.publicKey + "/borrow";
	};
});
