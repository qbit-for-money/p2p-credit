var orderModule = angular.module("order");

orderModule.controller("OrderInitController", function($scope, navbarService) {
	
	$scope.goToProfile = function() {
		navbarService.goToProfile();
	};
	
	$scope.goToOrderCreating = function() {
		navbarService.goToOrderCreating();
	};
	
	$scope.goToOrders = function() {
		navbarService.goToOrders();
	};
});
