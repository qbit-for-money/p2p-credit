var orderModule = angular.module("order");

orderModule.factory("categoriesResource", function($resource) {
	return $resource(window.context + "webapi/categories", {}, {
		findAll: {method: "GET"}
	});
});

orderModule.factory("ordersResource", function($resource) {
	return $resource(window.context + "webapi/orders", {}, {
		create: {method: "PUT"},
		changeStatus: {method: "POST", url: window.context + "webapi/orders/:id/status", params: {id: "@id"}},
		search:{method: "POST", url: window.context + "webapi/orders/search"}
	});
});

orderModule.factory("responsesResource", function($resource) {
	return $resource(window.context + "webapi/responses", {}, {
		create: {method: "PUT"},
		addResponse: {method: "PUT"},
		approveResponse: {method: "POST"}
	});
});
