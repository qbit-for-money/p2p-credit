var likeModule = angular.module("like");

likeModule.factory("likesResource", function($resource) {
	return $resource(window.context + "webapi/likes/:type/:id/:field", {type: "@type", id: "@id", field: "@field"}, {
		get: {method: "GET"},
		like: {method: "PUT"},
		dislike: {method: "DELETE"}
	});
});
