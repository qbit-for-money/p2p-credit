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

angular.module("main").config(function($httpProvider) {
	$httpProvider.interceptors.push([
		'$injector',
		function($injector) {
			return $injector.get('SessionInterceptor');
		}
	]);
})
	.factory('SessionInterceptor', function($q, $window, $rootScope) {
		return {
			responseError: function(response) {
				if (isSessionURL(response.config.url)) {
					$rootScope.$broadcast({
						401: goToFirstPage(401),
						403: goToFirstPage(403),
						419: goToFirstPage(419),
						440: goToFirstPage(440)
					}[response.status], response);
				}
				return $q.reject(response);
			}
		};
	}).config(function($routeProvider, $locationProvider, $sceDelegateProvider, $httpProvider) {
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
		controller: "OrdersController"
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
function isSessionURL(url) {
	if (("webapi/env" === url) || ("webapi/captcha-auth/auth" === url)) {
		return false;
	}
	return true;
}

function goToFirstPage(state) {
	window.location.href = window.context;
}
