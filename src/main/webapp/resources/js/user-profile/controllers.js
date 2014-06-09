var userProfileModule = angular.module("user-profile");

userProfileModule.controller("UserProfileController", function($scope, $rootScope, $modal, $location, usersProfileResource, fileReader) {
	var PHOTO_MIN_HEIGHT = 400;
	var PHOTO_MIN_WIDTH = 300;
	var PHOTO_MAX_HEIGHT = 2000;
	var PHOTO_MAX_WIDTH = 2000;

	var resultX1 = 0;
	var resultX2 = PHOTO_MIN_WIDTH;
	var resultY1 = 0;
	var resultY2 = PHOTO_MIN_HEIGHT;

	$scope.edit = false;
	$scope.editUserPhoto = false;
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

		$scope.userPropertiesMap['birthDate'] = userProfileResponse.birthDate;
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

		$scope.userPhotoSrc = window.context + "webapi/profiles/" + userProfileResponse.publicKey + "/photo";

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
			$scope.userPropertiesMap['birthDate'] = angular.element("#birth-date").val();
			updateProfile();
			$scope.cancel();			
		} else {
			$scope.edit = true;

			setTimeout(function() {
				var countries = new Array("Afghanistan", "Albania", "Algeria", "Andorra", "Angola", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana", "Brazil", "Brunei", "Bulgaria", "Burkina Faso", "Burma", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Central African Republic", "Chad", "Chile", "China", "Colombia", "Comoros", "Congo, Democratic Republic", "Congo, Republic of the", "Costa Rica", "Cote d'Ivoire", "Croatia", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Fiji", "Finland", "France", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Greece", "Greenland", "Grenada", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, North", "Korea, South", "Kuwait", "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein", "Lithuania", "Luxembourg", "Macedonia", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mexico", "Micronesia", "Moldova", "Mongolia", "Morocco", "Monaco", "Mozambique", "Namibia", "Nauru", "Nepal", "Netherlands", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Norway", "Oman", "Pakistan", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Poland", "Portugal", "Qatar", "Romania", "Russia", "Rwanda", "Samoa", "San Marino", " Sao Tome", "Saudi Arabia", "Senegal", "Serbia and Montenegro", "Seychelles", "Sierra Leone", "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "Spain", "Sri Lanka", "Sudan", "Suriname", "Swaziland", "Sweden", "Switzerland", "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Togo", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Yemen", "Zambia", "Zimbabwe");
				angular.element("#country").jqxInput({source: countries});
			}, 10);
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

		userPublicProfile.birthDate = $scope.userPropertiesMap['birthDate'];

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
		angular.element(this).mask("SSSSSSSSSSSSSSSSSSSSSSSSSSSS",
			{'translation': {
					S: {pattern: /[A-Za-zА-Яа-я0-9]/}
				}});
	});
	angular.element(document).on("focus", "#lastName", function() {
		angular.element(this).mask("SSSSSSSSSSSSSSSSSSSSSSSSSSSS",
			{'translation': {
					S: {pattern: /[A-Za-zА-Яа-я0-9]/}
				}});
	});
	angular.element(document).on("focus", "#country", function() {
		angular.element(this).mask("SSSSSSSSSSSSSSSSSSSSSSSSSSSS",
			{'translation': {
					S: {pattern: /[A-Za-zА-Яа-я0-9\s]/}
				}});
	});
	angular.element(document).on("focus", "#city", function() {
		angular.element(this).mask("SSSSSSSSSSSSSSSSSSSSSSSSSSSS",
			{'translation': {
					S: {pattern: /[A-Za-zА-Яа-я0-9\s]/}
				}});
	});
	angular.element(document).on("focus", "#birth-date", function() {
		angular.element(this).mask("00/00/0000");
	});
	angular.element(document).on("focus", "#hobby", function() {
		angular.element(this).mask("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS"
			+ "SSSSSSSSSSSSSSSSSSSSSSSSSSSS",
			{'translation': {
					S: {pattern: /[A-Za-zА-Яа-я0-9\s]/}
				}});
	});

	$scope.changeUserPhoto = function() {
		$scope.editUserPhoto = true;
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

	$scope.getFile = function(file) {
		$scope.editUserPhoto = true;
		fileReader.readAsDataUrl(file, $scope)
			.then(function(result) {
				$scope.imageSrc = result;
				var imag = new Image();
				imag.src = $scope.imageSrc;
				if ((imag.width > PHOTO_MAX_WIDTH) || (imag.height > PHOTO_MAX_HEIGHT) || (imag.width < PHOTO_MIN_WIDTH) || (imag.height < PHOTO_MIN_HEIGHT)) {
					$scope.imageSrc = null;
					$scope.editUserPhoto = false;
					return;
				}
				setTimeout(function() {
					var elementWidth = angular.element("#user-photo-change").width();
					var ratio = imag.width / elementWidth;
					angular.element('#user-photo-change').imgAreaSelect({
						aspectRatio: '3:4',
						handles: true,
						minWidth: elementWidth / ratio,
						x1: 0, y1: 0, x2: 80, y2: 105,
						onSelectEnd: function(img, selection) {

							resultX1 = Math.floor(selection.x1 * ratio);
							resultX2 = Math.floor(selection.x2 * ratio);
							resultY1 = Math.floor(selection.y1 * ratio);
							resultY2 = Math.floor(selection.y2 * ratio);
							resultX2 = (resultX2 - resultX1 > 800) ? (resultX1 + 800) : resultX2;
							resultY2 = (resultY2 - resultY1 > 800) ? (resultY1 + 800) : resultY2;
						}
					});
				}, 150);
			});
	};

	$scope.saveUserPhoto = function() {

		var userPhotoRequest = {};
		userPhotoRequest.startPoint = {};
		userPhotoRequest.endPoint = {};
		userPhotoRequest.startPoint.x = resultX1;
		userPhotoRequest.endPoint.x = resultX2;
		userPhotoRequest.startPoint.y = resultY1;
		userPhotoRequest.endPoint.y = resultY2;
		
		//console.log(JSON.stringify(userPhotoRequest));

		var data = $scope.imageSrc.replace(/^data:image\/(png|jpg|jpeg);base64,/, "");
		userPhotoRequest.imageString = data;

		var userPhotoResponse = usersProfileResource.setUserPhoto({}, userPhotoRequest);

		userPhotoResponse.$promise.then(function() {
			setTimeout(function() {
				location.reload();
			}, 100);
		});
	};

	$scope.cancel = function() {
		var ias = angular.element('#user-photo-change').imgAreaSelect({instance: true});
		ias.setOptions({hide: true});
		ias.update();
		$scope.imageSrc = null;
		$scope.editUserPhoto = false;
	};

	$scope.deleteUserPhoto = function() {
		var userPhotoRequest = {};
		userPhotoRequest.imageString = "";

		var userPhotoResponse = usersProfileResource.setUserPhoto({}, userPhotoRequest);

		userPhotoResponse.$promise.then(function() {
			setTimeout(function() {
				location.reload();
			}, 100);
		});
	};
});