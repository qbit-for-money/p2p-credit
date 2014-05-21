var orderModule = angular.module("order");

orderModule.controller("OrderInitController", function($scope) {
	$scope.goToCreditOrder = function() {
		window.location.href = window.context + "#/credit";
	};
	
	$scope.goToBorrowOrder = function() {
		window.location.href = window.context + "#/borrow";
	};
});
