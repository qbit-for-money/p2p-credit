var authModule = angular.module("captcha-auth");

authModule.controller("AuthDialogOpeningController", function(authService) {
	authService.openAuthDialog(true);
});

authModule.controller("AuthDialogController", function($scope, captchaAuthResource, isNotCaptcha, redirectUrl) {
	$scope.isNotCaptcha = isNotCaptcha;
	var timestamp = new Date().getTime();
	$scope.auth = "google";
	$scope.model = {};
	$scope.model.pin = "";
	$scope.model.encodedKey = "";
	$scope.captchaSrc = "";
	$scope.url = "webapi/oauth2/authenticate";
	if((redirectUrl !== undefined) && (redirectUrl !== "")) {
		$scope.url +=  "?redirect=" + redirectUrl;
	}

	function changePin() {
		var pin = $scope.model.pin;
		$scope.pinValid = (pin && /\d{4}/.test(pin));
		if ($scope.pinValid) {
			$scope.model.encodedKey = "";
			updateCaptcha();
		}
	}
	$scope.changePin = changePin;

	function changeEncodedKey() {
		if ($scope.model.encodedKey) {
			var authRequest = {encodedKey: $scope.model.encodedKey, pin: $scope.model.pin, timestamp: timestamp};
			var authResponse = captchaAuthResource.auth({}, authRequest);
			authResponse.$promise.then(function() {
				$scope.encodedKeyValid = true;
				setTimeout(function() {
					location.reload();
				}, 2000);
			});
		} else {
			$scope.encodedKeyValid = false;
		}
	};
	$scope.changeEncodedKey = changeEncodedKey;

	function updateCaptcha() {
		$scope.captchaSrc = window.context + "webapi/captcha-auth/image?pin=" + $scope.model.pin + "&timestamp=" + timestamp + "&rand=" + Math.round(Math.random() * 1000);
	};
	$scope.updateCaptcha = updateCaptcha;

	function changeAuthMode() {
		if ($scope.auth === "google") {
			$scope.model.pin = "";
			$scope.pinValid = false;
		}
	}
	$scope.changeAuthMode = changeAuthMode;
	
	angular.element(document).on("focus", "#pin", function() {
		angular.element(this).mask("0000");
	});
});