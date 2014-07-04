var navbarModule = angular.module("navbar");

navbarModule.controller("NavbarController", function($scope, $location) {
	$scope.isActive = function(viewLocation) {
		return viewLocation === $location.path();
	};
	
	$scope.gotoOrderInit = function() {
		$location.hash("order-init");
		$anchorScroll();
	};
	
	$scope.gotoLastOrders = function() {
		$location.hash("last-orders-table");
		$anchorScroll();
	};
	
	$scope.gotoVideo = function() {
		$location.hash("video-container");
		$anchorScroll();
	};
	
	$scope.gotoImage = function() {
		$location.hash("presentation-image");
		$anchorScroll();
	};
});