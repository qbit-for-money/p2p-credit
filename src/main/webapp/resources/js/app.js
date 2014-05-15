angular.module("common", ["ngResource"]);

angular.module("user", ["ngResource"]);

angular.module("captcha-auth", ["ngResource"]);

angular.module("main", ["ngRoute", "ui.bootstrap", "chieffancypants.loadingBar", "common", "user", "captcha-auth"]);

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
