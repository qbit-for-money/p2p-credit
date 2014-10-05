var likeModule = angular.module("like");

likeModule.directive("likeButtons", function($compile, likesResource, usersResource, $timeout, $rootScope, $location) {
	return {
		restrict: "E",
		scope: {
			entityType: "=", entityId: "=", entityField: "=", userAltId: "="
		},
		link: function(scope, element, attrs) {
			scope.userPublicKeyFromPath = $location.$$path.replace("/users/", "");
			$timeout(function() {
				if (!scope.userAltId || (scope.userPublicKeyFromPath === scope.userAltId)) {

					scope.$apply(function() {
						scope.isCurrentUser = true;
					});

				} else {
					scope.$apply(function() {
						scope.isCurrentUser = false;
						if (($rootScope.user.publicKey.indexOf("@") === -1) 
							&& ($rootScope.user.publicKey.indexOf("vk-") === -1) 
							&& ($rootScope.user.publicKey.indexOf("fb-") === -1)) {
							scope.isCaptchaUser = true;
						} else {
							scope.isCaptchaUser = false;
						}
					});
				}
			});
			


			scope.alreadyVotedUser = function() {

				if (scope.alreadyVotedUserPublicKeys && $rootScope.user && (scope.alreadyVotedUserPublicKeys.indexOf($rootScope.user.publicKey) !== -1)) {
					return true;
				}
				return false;
			};
			var likeId = {type: scope.entityType, id: scope.entityId, field: scope.entityField};
			reloadLikesCount(likeId, init);


			function init() {
				element.children().remove();
				var html = '<span class="badge like-count">{{likeCount}}</span><span class="badge like-count">{{dislikeCount}}</span>'
					+ '<button class="btn btn-default btn-xs ng-scope" type="button" ng-click="like()" title="Like" ng-disabled="alreadyVotedUser()" ng-hide="isCurrentUser || isCaptchaUser">'
					+ '<span class="glyphicon glyphicon glyphicon-thumbs-up"></span>'
					+ '</button>'
					+ '<button class="btn btn-default btn-xs ng-scope" type="button" ng-click="dislike()" title="Dislike" ng-disabled="alreadyVotedUser()" ng-hide="isCurrentUser || isCaptchaUser">'
					+ '<span class="glyphicon glyphicon glyphicon-thumbs-down"></span>'
					+ '</button>';
				element.append($compile(html)(scope));
			}

			function like() {
				var likeResponse = likesResource.like(likeId);
				likeResponse.$promise.then(function() {
					console.debug(likeResponse);
					reloadLikesCount(likeId, init);
				});
			}
			scope.like = like;
			function dislike() {
				var likeResponse = likesResource.dislike(likeId);
				likeResponse.$promise.then(function() {
					console.debug(likeResponse);
					reloadLikesCount(likeId, init);
				});
			}
			scope.dislike = dislike;
			function reloadLikesCount(likeId, callback) {
				var likeResponse = likesResource.get(likeId);
				likeResponse.$promise.then(function() {
					console.debug(likeResponse);
					if (callback) {
						$timeout(function() {
							scope.$apply(function() {
								scope.likeCount = (likeResponse.likeCount) ? likeResponse.likeCount : 0;
								scope.dislikeCount = (likeResponse.dislikeCount) ? likeResponse.dislikeCount : 0;
								scope.alreadyVotedUserPublicKeys = likeResponse.alreadyVotedUserPublicKeys;
							});
							if (callback) {
								callback();
							}
						});
					}
				});
			}
		}
	};
});