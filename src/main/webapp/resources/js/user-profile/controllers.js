var userProfileModule = angular.module("user-profile");

userProfileModule.controller("UserProfileController", function($scope, $rootScope, $modal, $location, usersProfileResource) {
	$scope.edit = false;
	$scope.userProfile = {};
	$scope.userPropertiesMap = {};
	$scope.hasFocus = false;
	$scope.genderTypes = ["Male", "Female"];
	var visible = "open";
	var notVisible = "close";

	var defaultPersonalData = '<p style="text-align: center;"><img src="resources/img/elephant-logo.png"/></p>';

	var userPublicKeyFromPath = $location.$$path.replace("/users/", "");
	var userProfileResponse;

	if (userPublicKeyFromPath === $rootScope.user.publicKey) {
		if ($rootScope.user.publicKey.indexOf("@") === -1) {
			window.location.href = window.context;
		} else {
			$scope.isCurrentUser = true;
			userProfileResponse = usersProfileResource.current({});
			CKEDITOR.disableAutoInline = true;
			CKEDITOR.inline("personalEditable");
		}
	} else {
		$scope.isCurrentUser = false;
		userProfileResponse = usersProfileResource.getById({'id': userPublicKeyFromPath});
	}

	userProfileResponse.$promise.then(function() {


		$scope.userPropertiesMap['firstName'] = userProfileResponse.firstName;
		$scope.userPropertiesMap['lastName'] = userProfileResponse.lastName;
		$scope.userPropertiesMap['rating'] = userProfileResponse.rating;
		$scope.userPropertiesMap['country'] = userProfileResponse.country;
		if (userProfileResponse.countryEnabled === true) {
			$scope.userPropertiesMap['countryEnabled'] = visible;
		} else {
			$scope.userPropertiesMap['countryEnabled'] = notVisible;
		}

		$scope.userPropertiesMap['city'] = userProfileResponse.city;
		if (userProfileResponse.cityEnabled === true) {
			$scope.userPropertiesMap['cityEnabled'] = visible;
		} else {
			$scope.userPropertiesMap['cityEnabled'] = notVisible;
		}

		$scope.userPropertiesMap['age'] = userProfileResponse.age;
		if (userProfileResponse.ageEnabled === true) {
			$scope.userPropertiesMap['ageEnabled'] = visible;
		} else {
			$scope.userPropertiesMap['ageEnabled'] = notVisible;
		}

		$scope.userPropertiesMap['hobby'] = userProfileResponse.hobby;
		if (userProfileResponse.hobbyEnabled === true) {
			$scope.userPropertiesMap['hobbyEnabled'] = visible;
		} else {
			$scope.userPropertiesMap['hobbyEnabled'] = notVisible;
		}

		if (userProfileResponse.gender === "FEMALE") {
			$scope.userPropertiesMap['gender'] = $scope.genderTypes[1];
		} else {
			$scope.userPropertiesMap['gender'] = $scope.genderTypes[0];
		}


		$scope.userPropertiesMap['personalPageData'] = userProfileResponse.personalPageData;

		$scope.userPhotoSrc = window.context + "webapi/users-profile/user-photo?userId=" + userProfileResponse.publicKey;

		$scope.reloadSCEditorInstance();

	});

	$scope.savePersonalPage = function() {
		var data = CKEDITOR.instances.personalEditable.getData();
		if (data !== $scope.userPropertiesMap['personalPageData']) {
			$scope.userPropertiesMap['personalPageData'] = data;
			updateProfile();
		}
	};

	$scope.editProfile = function() {
		if ($scope.edit) {
			$scope.edit = false;
			updateProfile();
		} else {
			$scope.edit = true;

			angular.element(".user-attributes input").mask("0000");
			setTimeout(function() {
				angular.element(this).mask("0000");
			}, 1000);

			angular.element("#input1").on("focus", function() {
				alert("asdf")
				angular.element(this).mask("0000");
			});
		}

	};

	$scope.reloadSCEditorInstance = function() {
		if (($scope.userPropertiesMap['personalPageData'] === "DEFAULT")
			|| ($scope.userPropertiesMap['personalPageData'].indexOf("<p>DEFAULT</p>") === 0)) {
			if ($scope.isCurrentUser) {
				CKEDITOR.instances.personalEditable.setData(defaultPersonalData);
			} else {
				angular.element("#personalEditable").html(defaultPersonalData);
			}
		} else {
			if ($scope.isCurrentUser) {
				CKEDITOR.instances.personalEditable.setData($scope.userPropertiesMap['personalPageData']);
			} else {
				angular.element("#personalEditable").html($scope.userPropertiesMap['personalPageData']);
			}
		}
	};

	$scope.changeVisible = function(propertyName) {
		if ($scope.userPropertiesMap[propertyName] === visible) {
			$scope.userPropertiesMap[propertyName] = notVisible;
		} else if ($scope.userPropertiesMap[propertyName] === notVisible) {
			$scope.userPropertiesMap[propertyName] = visible;
		}
	};

	function updateProfile() {
		var userPublicProfile = {};

		userPublicProfile.firstName = $scope.userPropertiesMap['firstName'];
		userPublicProfile.lastName = $scope.userPropertiesMap['lastName'];
		userPublicProfile.country = $scope.userPropertiesMap['country'];
		if ($scope.userPropertiesMap['countryEnabled'] === visible) {
			userPublicProfile.countryEnabled = true;
		} else {
			userPublicProfile.countryEnabled = false;
		}

		userPublicProfile.city = $scope.userPropertiesMap['city'];

		if ($scope.userPropertiesMap['cityEnabled'] === visible) {
			userPublicProfile.cityEnabled = true;
		} else {
			userPublicProfile.cityEnabled = false;
		}

		userPublicProfile.age = $scope.userPropertiesMap['age'];

		if ($scope.userPropertiesMap['ageEnabled'] === visible) {
			userPublicProfile.ageEnabled = true;
		} else {
			userPublicProfile.ageEnabled = false;
		}

		userPublicProfile.hobby = $scope.userPropertiesMap['hobby'];

		if ($scope.userPropertiesMap['hobbyEnabled'] === visible) {
			userPublicProfile.hobbyEnabled = true;
		} else {
			userPublicProfile.hobbyEnabled = false;
		}

		if ($scope.userPropertiesMap['gender'] === $scope.genderTypes[0]) {
			userPublicProfile.gender = "MALE";
		} else {
			userPublicProfile.gender = "FEMALE";
		}

		userPublicProfile.personalPageData = $scope.userPropertiesMap['personalPageData'];

		userProfileResponse = usersProfileResource.updatePublicProfile({}, userPublicProfile);

		//var userPrivateProfile = {};
	}

	angular.element(document).on("focus", "#firstName", function() {
		angular.element(this).mask("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	});
	angular.element(document).on("focus", "#lastName", function() {
		angular.element(this).mask("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	});
	angular.element(document).on("focus", "#country", function() {
		angular.element(this).mask("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	});
	angular.element(document).on("focus", "#city", function() {
		angular.element(this).mask("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	});
	angular.element(document).on("focus", "#age", function() {
		angular.element(this).mask("00");
	});
	angular.element(document).on("focus", "#hobby", function() {
		angular.element(this).mask("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
			+ "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
	});

	$scope.changeUserPhoto = function() {
		var modalInstance = $modal.open({
			controller: "UserPhotoController",
			templateUrl: "resources/html/user/change-user-photo-dialog.html",
			keyboard: true, backdropClick: false, dialogFade: true
		});

		modalInstance.result.then(function() {
		},
			function() {
				var ias = angular.element('#user-photo-change').imgAreaSelect({instance: true});
				ias.setOptions({hide: true});
				ias.update();

			});
	};
});