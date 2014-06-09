angular.module("common", ["ngResource"]);

angular.module("user", ["ngResource"]);

angular.module("captcha-auth", ["ngResource"]);

angular.module("order", ["ngResource"]);

angular.module("user-profile", ["ngResource"]);

angular.module("user-edit", ["ngResource"]);

angular.module("users", ["ngResource"]);

angular.module("navbar", ["ngResource"]);

angular.module("main", ["ngRoute", "ui.bootstrap", "chieffancypants.loadingBar", "common", "user", "captcha-auth", "order", "user-profile", "user-edit", "users", "navbar"]);

angular.module("main").config(function($routeProvider) {
	$routeProvider.when("/", {
		templateUrl: "resources/html/user/users.html",
		controller: "UsersController"
	}).when("/order-init", {
		templateUrl: "resources/html/order/order-init.html",
		controller: "OrderInitController"
	}).when("/orders", {
		templateUrl: "resources/html/order/orders.html",
		controller: "OrdersController"
	}).when("/users/:id", {
		templateUrl: "resources/html/user/user.html",
		controller: "UserProfileController"
	}).when("/credit", {
		templateUrl: "resources/html/order/credit.html",
		controller: "CreditController"
	}).when("/borrow", {
		templateUrl: "resources/html/order/borrow.html",
		controller: "BorrowController"
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
