var orderModule = angular.module("order");

orderModule.factory("categoriesResource", function($resource) {
	return $resource(window.context + "webapi/orders/categories", {}, {
		findAll: {method: "GET"}
	});
});

orderModule.factory("ordersResource", function($resource) {
	return $resource(window.context + "webapi/orders", {}, {
		create: {method: "PUT"},
		addResponse: {method: "POST", url: window.context + "webapi/orders/addResponse"},
		approveResponse: {method: "POST", url: window.context + "webapi/orders/approveResponse"},
		changeStatus: {method: "POST", url: window.context + "webapi/orders/changeStatus"}
	});
});


