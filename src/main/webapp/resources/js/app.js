angular.module("common", ["ngResource"]);

angular.module("user", ["ngResource"]);

angular.module("captcha-auth", ["ngResource"]);

angular.module("order", ["ngResource"]);

angular.module("main", ["ngRoute", "ui.bootstrap", "chieffancypants.loadingBar", "common", "user", "captcha-auth", "order"]);

angular.module("main").config(function($routeProvider) {
	$routeProvider.when("/", {
		templateUrl: "resources/html/order/order-init.html",
		controller: "OrderInitController"
	}).when("/:id", {
		templateUrl: "resources/html/user/user.html",
		controller: "CurrencyController"
	}).when("/:id/credit", {
		templateUrl: "resources/html/order/credit.html",
		controller: "CurrencyController"
	}).when("/:id/borrow", {
		templateUrl: "resources/html/order/borrow.html",
		controller: "CurrencyController"
	}).otherwise({redirectTo: "/"});
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
