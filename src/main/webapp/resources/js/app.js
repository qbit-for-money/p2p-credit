angular.module("common", ["ngResource"]);

angular.module("user", ["ngResource"]);

angular.module("captcha-auth", ["ngResource"]);

angular.module("order", ["ngResource"]);

angular.module("user-profile", ["ngResource", "ngSanitize"]);

angular.module("user-edit", ["ngResource"]);

angular.module("navbar", ["ngResource", "common"]);

angular.module("like", ["ngResource"]);

angular.module("main", ["ngRoute", "ui.bootstrap", "chieffancypants.loadingBar", "common", "user",
	"captcha-auth", "order", "user-profile", "user-edit", "navbar", "like", "ui.select2"]);

angular.module("main").config(function($routeProvider, $locationProvider) {
	$routeProvider.when("/", {
		templateUrl: "resources/html/order/order-init.html",
		controller: "OrderInitController"
	}).when("/users/:id", {
		templateUrl: "resources/html/user/user.html",
		controller: "UserProfileController"
	}).when("/create-order", {
		templateUrl: "resources/html/order/create-order.html",
		controller: "CreateOrderController"
	}).when("/orders", {
		templateUrl: "resources/html/order/orders-table.html",
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
