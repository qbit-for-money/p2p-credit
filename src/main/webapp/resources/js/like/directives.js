var likeModule = angular.module("like");

likeModule.directive("likeButtons", function($compile, likesResource) {
	return {
		restrict: "E",
		scope: {
			entityType: "=", entityId: "=", entityField: "="
		},
		link: function(scope, element, attrs) {
			var html = '<button class="btn btn-default btn-xs ng-scope" type="button" ng-click="like()" title="Like">'
				+ '<span class="glyphicon glyphicon glyphicon-thumbs-up"></span>'
				+ '</button>'
				+ '<button class="btn btn-default btn-xs ng-scope" type="button" ng-click="dislike()" title="Dislike">'
				+ '<span class="glyphicon glyphicon glyphicon-thumbs-down"></span>'
				+ '</button>';
			element.append($compile(html)(scope));
			var likeId = {type: scope.entityType, id: scope.entityId, field: scope.entityField};
			function like() {
				var likeResponse = likesResource.like(likeId);
				likeResponse.$promise.then(function() {
					console.debug(likeResponse);
					// TODO
				});
			}
			scope.like = like;
			function dislike() {
				var likeResponse = likesResource.dislike(likeId);
				likeResponse.$promise.then(function() {
					console.debug(likeResponse);
					// TODO
				});
			}
			scope.dislike = dislike;
		}
	};
});