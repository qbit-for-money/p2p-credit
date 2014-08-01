var navbarModule = angular.module("navbar");

navbarModule.controller("NavbarController", function($scope, $location, navbarService) {
	$scope.isNavbarCollapsed = true;
	if (typeof String.prototype.startsWith !== 'function') {
		String.prototype.startsWith = function(str) {
			return this.indexOf(str) === 0;
		};
	}
	$scope.isActive = function(viewLocation) {
		return $location.path().startsWith(viewLocation);
	};

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