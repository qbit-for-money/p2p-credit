var userModule = angular.module("user");

userModule.controller("UserController", function($scope, $rootScope, usersResource, authService, $location, phone, location, usersProfileResource) {

	$scope.keyType = "user";
	$scope.logoutButton = "";
	$rootScope.isPhone = phone.isPhone();
	
	

	function supportsHtml5Storage() {
		try {
			return 'localStorage' in window && window['localStorage'] !== null;
		} catch (e) {
			return false;
		}
	}

	location.getLocation(function(location) {
		usersResource.setUserLocation({}, location);
	});

	if (supportsHtml5Storage()) {
		var localStorage = window.localStorage;
		var machineId = localStorage.getItem("MACHINE_ID");
		if (!machineId) {
			var generatedId = Math.floor(Math.random() * 100000000000);
			localStorage.setItem("MACHINE_ID", generatedId);
			machineId = localStorage.getItem("MACHINE_ID");
		}
		var machineIdRequest = {};
		machineIdRequest.machineId = machineId;
		usersResource.setMachineId({}, machineIdRequest);
	}

	var currentUserAltId = usersResource.currentAltId({});
	currentUserAltId.$promise.then(function() {
		if (currentUserAltId && currentUserAltId.userId) {
			if ((currentUserAltId.userId.indexOf("@") !== -1)
				|| (currentUserAltId.userId.indexOf("vk-") !== -1)
				|| (currentUserAltId.userId.indexOf("fb-") !== -1)) {
				$scope.keyType = "envelope";
				var userProfileResponse = usersProfileResource.getShortById({id: currentUserAltId.userId});
				userProfileResponse.$promise.then(function() {
					var name = userProfileResponse.name;
					if(name && (name !== null) && (name !== "")) {
						$scope.userName = name;
					} else {
						$scope.userName = currentUserAltId.userId;
					}
				});
			} else {
				$scope.keyType = "user";
			}
			$scope.logoutButton = "glyphicon-log-out";
		} else {
			$scope.keyType = "user";
		}
	});
	//angular.element(document).ready(function() {
	//$timeout(function() {
	angular.element("#user-chat-form").removeClass("invisible");
	//}, 500);
	//});
	angular.element("#user-form").removeClass("invisible");


	$rootScope.isGoogleAuth = function() {
		return $rootScope.currentUserAltId && ($rootScope.currentUserAltId.indexOf("@") !== -1);
	};

	$rootScope.isVKAuth = function() {
		return $rootScope.currentUserAltId && ($rootScope.currentUserAltId.indexOf("vk-") !== -1);
	};

	$rootScope.isFBAuth = function() {
		return $rootScope.currentUserAltId && ($rootScope.currentUserAltId.indexOf("fb-") !== -1);
	};

	$scope.goToOrderInit = function() {
		window.location.href = window.context;
	};

	$scope.goToProfile = function() {
		$rootScope.userType = undefined;
		window.location.href = window.context + "#/users/" + $rootScope.currentUserAltId;
	};

	$scope.authWithGoogle = function() {
		window.location.href = window.context + "webapi/oauth2/authenticate?redirect=" + window.context + $location.$$path;
	};

	$scope.openAuthDialog = function() {
		authService.openAuthDialog(false, true);
	};

	$scope.logout = function() {
		var logoutResponse = usersResource.logout({});
		logoutResponse.$promise.then(function() {
			window.location.href = window.context;
		});
	};
});