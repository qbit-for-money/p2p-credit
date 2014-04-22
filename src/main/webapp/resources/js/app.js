angular.module("main", ["ngRoute", "ui.bootstrap", "chieffancypants.loadingBar"]);

angular.module("main").config(function($routeProvider) {

}).run(function($rootScope, $location) {
	$rootScope.location = $location;
	$rootScope.requestCount = 0;
	$rootScope.$on("cfpLoadingBar:loading", function() {
		$rootScope.requestCount++;
	});
	$rootScope.$on("cfpLoadingBar:loaded", function() {
		$rootScope.requestCount--;
	});
});
