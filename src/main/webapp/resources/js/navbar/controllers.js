var navbarModule = angular.module("navbar");

navbarModule.controller("NavbarController", function($scope, $location) {
	$scope.isActive = function(viewLocation) {
		return viewLocation === $location.path();
	};
});