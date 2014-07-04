var orderModule = angular.module("order");

orderModule.factory("categoriesResource", function($resource) {
	return $resource(window.context + "webapi/orders/categories", {}, {
		findAll: {method: "GET"}
	});
});

orderModule.factory("ordersResource", function($resource) {
	return $resource(window.context + "webapi/orders", {}, {
		create: {method: "PUT"}
	});
});


