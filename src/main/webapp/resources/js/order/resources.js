var orderModule = angular.module("order");

orderModule.factory("categoriesResource", function($resource) {
	return $resource(window.context + "webapi/orders/categories", {}, {
		findAll: {method: "GET"}
	});
});

orderModule.factory("ordersResource", function($resource) {
	return $resource(window.context + "webapi/orders", {}, {
		create: {method: "PUT"},
		getResponse: {method: "GET", url: window.context + "webapi/orders/respond"},
		addResponse: {method: "PUT", url: window.context + "webapi/orders/respond"},
		approveResponse: {method: "POST", url: window.context + "webapi/orders/respond"},
		changeStatus: {method: "POST", url: window.context + "webapi/orders/status"}
	});
});
