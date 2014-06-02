var orderModule = angular.module("order");

orderModule.controller("OrderInitController", function($scope, usersResource) {

	var userResponse = usersResource.current({});
	
	userResponse.$promise.then(function() {
		if (userResponse.publicKey.indexOf("@") === -1) {
			window.location.href = window.context + "#/users";
		}
	});

	$scope.goToCreditOrder = function() {
		window.location.href = window.context + "#/credit";
	};

	$scope.goToBorrowOrder = function() {
		window.location.href = window.context + "#/borrow";
	};
});
