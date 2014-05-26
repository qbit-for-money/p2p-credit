var userProfileModule = angular.module("user-profile");

userProfileModule.controller("UserProfileController", function($scope, $rootScope, $location, usersProfileResource) {
	$scope.edit = false;
	$scope.userProfile = {};
	$scope.hasFocus = false;
	var defaultPersonalData = '<p style="text-align: center;">Hello! This is My Personal Page!</p>';

	var userPublicKeyFromPath = $location.$$path.replace("/users/", "");
	var userProfileResponse;


	if (userPublicKeyFromPath === $rootScope.user.publicKey) {
		if ($rootScope.user.publicKey.indexOf("@") === -1) {
			window.location.href = window.context;
		} else {
			$scope.isCurrentUser = true;
			userProfileResponse = usersProfileResource.current({});
			CKEDITOR.disableAutoInline = true;
			CKEDITOR.inline('personalEditable');
		}
	} else {
		$scope.isCurrentUser = false;
		userProfileResponse = usersProfileResource.getById({'id': userPublicKeyFromPath});
	}

	userProfileResponse.$promise.then(function() {


		$scope.userProfile.firstName = userProfileResponse.firstName;
		$scope.userProfile.lastName = userProfileResponse.lastName;
		$scope.userProfile.rating = userProfileResponse.rating;
		$scope.userProfile.country = userProfileResponse.country;
		$scope.userProfile.countryEnabled = userProfileResponse.countryEnabled;
		$scope.userProfile.city = userProfileResponse.city;
		$scope.userProfile.cityEnabled = userProfileResponse.cityEnabled;
		$scope.userProfile.age = userProfileResponse.age;
		$scope.userProfile.ageEnabled = userProfileResponse.ageEnabled;
		$scope.userProfile.hobby = userProfileResponse.hobby;
		$scope.userProfile.hobbyEnabled = userProfileResponse.hobbyEnabled;
		//$scope.userProfile.userType = userProfileResponse.userType;

		$scope.userProfile.gender = userProfileResponse.gender;
		$scope.userProfile.personalPageData = userProfileResponse.personalPageData;

		if ($scope.isCurrentUser) {
			if (userProfileResponse.personalPageData === "DEFAULT") {
				CKEDITOR.instances.personalEditable.setData(defaultPersonalData);
			} else {
				CKEDITOR.instances.personalEditable.setData($scope.userProfile.personalPageData);
			}
		} else {
			angular.element("#personalEditable").html(userProfileResponse.personalPageData);
		}
	});

	$scope.savePersonalPage = function() {
		var data = CKEDITOR.instances.personalEditable.getData();
		if (data !== $scope.userProfile.personalPageData) {
			$scope.userProfile.personalPageData = data;
			updateProfile();
		}
	};

	$scope.editProfile = function() {
		if ($scope.edit) {
			$scope.edit = false;
			updateProfile();
		} else {
			$scope.edit = true;
		}

	};

	function updateProfile() {
		var userPublicProfile = {};

		userPublicProfile.firstName = $scope.userProfile.firstName;
		userPublicProfile.lastName = $scope.userProfile.lastName;
		userPublicProfile.country = $scope.userProfile.country;
		userPublicProfile.countryEnabled = $scope.userProfile.countryEnabled;
		userPublicProfile.city = $scope.userProfile.city;
		userPublicProfile.cityEnabled = $scope.userProfile.cityEnabled;
		userPublicProfile.age = $scope.userProfile.age;
		userPublicProfile.ageEnabled = $scope.userProfile.ageEnabled;
		userPublicProfile.hobby = $scope.userProfile.hobby;
		userPublicProfile.hobbyEnabled = $scope.userProfile.hobbyEnabled;
		userPublicProfile.gender = $scope.userProfile.gender;
		userPublicProfile.personalPageData = $scope.userProfile.personalPageData;

		userProfileResponse = usersProfileResource.updatePublicProfile({}, userPublicProfile);




		//var userPrivateProfile = {};
	}
});