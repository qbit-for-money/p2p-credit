var userProfileModule = angular.module("user-profile");
userProfileModule.controller("UserProfileController", function($scope, $rootScope, $modal, $location, usersResource, photosResource, usersProfileResource, currencyResource, categoriesResource, ordersResource, fileReader, $timeout, $sce, authService, userProfileService, languagesResource) {
	var PHOTO_MIN_HEIGHT = 400;
	var PHOTO_MIN_WIDTH = 300;
	var PHOTO_MAX_HEIGHT = 2000;
	var PHOTO_MAX_WIDTH = 2000;
	var resultX1 = 0;
	var resultX2 = PHOTO_MIN_WIDTH;
	var resultY1 = 0;
	var resultY2 = PHOTO_MIN_HEIGHT;
	var errorMessages = {};
	errorMessages["MIN_SIZE"] = "Фото должно быть больше 400x300";
	errorMessages["MAX_SIZE"] = "Фото должно быть меньше 2000x2000";
	errorMessages["IMAGE_TYPE"] = "Расширение изображения должно быть JPG";
	$scope.popoverShow = false;
	$scope.disabledEditButton = false;
	$scope.edit = false;
	$scope.editAdditional = false;
	$scope.editedPassport = false;
	$scope.editUserPhoto = false;
	$scope.editMap = {};
	$scope.editMap['links'] = false;
	$scope.editMap['videos'] = false;
	$scope.editName = false;
	$scope.editMail = false;
	$scope.userProfile = {};
	$scope.userPropertiesMap = {};
	$scope.orderCreatingMap = {};
	$scope.hasFocus = false;
	$scope.isCurrentUser = false;
	$scope.isCaptchaUser = false;
	$scope.authMap = {};
	var currenciesMap = {};
	$scope.scEditor = {};
	$scope.scEditor.dataInitialized = false;
	$scope.scEditor.BKIInitialized = false;
	var visible = "open";
	var notVisible = "close";
	var defaultPersonalData = '<p style="text-align: center;"><img src="resources/img/elephant-logo.png"/></p>';
	$scope.userPublicKeyFromPath = $location.$$path.replace("/users/", "");
	var userProfileResponse;
	$scope.currentUser = usersResource.current({});
	$scope.languages = {};
	$scope.languages.allLanguages = [];
	var allCurrencies = [];
	var orderCurrencies = ["None"];
	$scope.userPropertiesMap['currencies'] = [];
	$scope.userPropertiesMap['languages'] = [];
	$scope.languagesMap = {};
	$scope.additionAttrsHidden = false;
	$scope.likesCount = {};
	$scope.currenciesSelect2Options = {
		allowClear: true,
		tags: allCurrencies,
		multiple: true,
		'simple_tags': true,
		maximumSelectionSize: 20,
		maximumInputLength: 20,
		createSearchChoice: function(term, data) {
			if ($(data).filter(function() {
				return this.text.localeCompare(term) === 0;
			}).length === 0) {
				return null;
			}
		}
	};
	$scope.languageSelect2Options = {
		allowClear: true,
		tags: $scope.languages.allLanguages,
		multiple: true,
		'simple_tags': true,
		maximumSelectionSize: 20,
		maximumInputLength: 20,
		createSearchChoice: function(term, data) {
			if ($(data).filter(function() {
				return this.text.localeCompare(term) === 0;
			}).length === 0) {
				return {id: term, text: term};
			}
		}
	};
	var userByAltId = usersResource.byAltId({id: $scope.userPublicKeyFromPath});

	userByAltId.$promise.then(function() {
		$scope.authMap["userIdByAltId"] = userByAltId.publicKey;

		$scope.currentUser.$promise.then(function() {
			if (!$scope.currentUser.publicKey) {
				authService.openAuthDialog(true, false, "/users/" + $scope.userPublicKeyFromPath);
			}
			var currentUserAltId = usersResource.currentAltId({});
			currentUserAltId.$promise.then(function() {
				if ($scope.userPublicKeyFromPath === currentUserAltId.userId) {
					if (($scope.currentUser.publicKey.indexOf("@") === -1) && ($scope.currentUser.publicKey.indexOf("vk-") === -1) && ($scope.currentUser.publicKey.indexOf("fb-") === -1)) {
						window.location.href = window.context;
					} else {
						$scope.isCurrentUser = true;
						$scope.userPropertiesMap['isCurrentUser'] = true;
						userProfileResponse = usersProfileResource.current({});
						$scope.currentUserAltId = currentUserAltId.userId;
						$scope.authMap["userIdByAltId"] = currentUserAltId.userId;
						hideAuthServices();
						reloadAllCurrencies();
						reloadData();
					}
				} else {
					angular.element("#personalEditable").addClass("invisible");
					$scope.isCurrentUser = false;
					if (($scope.currentUser.publicKey.indexOf("@") === -1) && ($scope.currentUser.publicKey.indexOf("vk-") === -1) && ($scope.currentUser.publicKey.indexOf("fb-") === -1)) {
						$scope.isCaptchaUser = true;
					}
					$scope.userPropertiesMap['isCurrentUser'] = false;
					$scope.authMap["userIdByAltId"] = userByAltId.publicKey;
					userProfileResponse = usersProfileResource.getById({'id': $scope.authMap["userIdByAltId"]});
					hideAuthServices();
					reloadAllCurrencies();
					reloadData();
				}

			});
		});
	});

	function hideAuthServices() {
		if ($scope.currentUserAltId.indexOf("@") !== -1) {
			$scope.authMap["GOOGLE"] = true;
		}
		if ($scope.currentUserAltId.indexOf("vk-") !== -1) {
			$scope.authMap["VK"] = true;
		}
		if ($scope.currentUserAltId.indexOf("fb-") !== -1) {
			$scope.authMap["FB"] = true;
		}
	}
	function reloadData() {
		userProfileResponse.$promise.then(function() {
			$scope.userPropertiesMap['name'] = userProfileResponse.name;
			$scope.userPropertiesMap['mail'] = userProfileResponse.mail;
			$scope.userPropertiesMap['mailEnabled'] = (userProfileResponse.mailEnabled === true) ? visible : notVisible;
			$scope.userPropertiesMap['phone'] = userProfileResponse.phone;
			$scope.userPropertiesMap['phoneEnabled'] = (userProfileResponse.phoneEnabled === true) ? visible : notVisible;
			$scope.userPropertiesMap['languagesEnabled'] = (userProfileResponse.languagesEnabled === true) ? visible : notVisible;
			$scope.userPropertiesMap['languages'] = [];
			if (userProfileResponse.languages) {
				var languages = "";
				for (var i = 0; i < userProfileResponse.languages.length; i++) {
					$scope.userPropertiesMap['languages'].push(userProfileResponse.languages[i].code);
					$scope.languagesMap[userProfileResponse.languages[i].code] = userProfileResponse.languages[i];
					languages += (userProfileResponse.languages[i].code + ", ");
				}
				if (userProfileResponse.languages.length !== 0) {
					languages = languages.substring(0, languages.length - 2);
				}
				$scope.userPropertiesMap['languagesStr'] = languages;
			}
			$scope.userPropertiesMap['currenciesEnabled'] = (userProfileResponse.currenciesEnabled === true) ? visible : notVisible;
			$scope.userPropertiesMap['currencies'] = [];
			if (userProfileResponse.currencies) {
				var currencies = "";
				for (var i = 0; i < userProfileResponse.currencies.length; i++) {
					currencies += (userProfileResponse.currencies[i].code + ", ");
					$scope.userPropertiesMap['currencies'].push(userProfileResponse.currencies[i].code);
				}
				currencies = currencies.substring(0, currencies.length - 2);
				$scope.userPropertiesMap['currenciesStr'] = currencies;
			}
			$scope.userPropertiesMap['passportEnabled'] = userProfileResponse.passportEnabled;
			$scope.userPropertiesMap['phones'] = (userProfileResponse.phones) ? userProfileResponse.phones : [];
			$scope.userPropertiesMap['videos'] = (userProfileResponse.videos) ? userProfileResponse.videos : [];
			$scope.userPropertiesMap['names'] = (userProfileResponse.namesLinks) ? userProfileResponse.namesLinks : [];
			$scope.userPropertiesMap['socialLinks'] = (userProfileResponse.socialLinks) ? userProfileResponse.socialLinks : [];
			var userStatisticsResponse = usersProfileResource.getStatisticsById({'id': userProfileResponse.userId});
			userStatisticsResponse.$promise.then(function() {
				$scope.ratingOpenness = (userStatisticsResponse.opennessRating) ? userStatisticsResponse.opennessRating : 0;
				$scope.ordersRating = (userStatisticsResponse.ordersRating) ? userStatisticsResponse.ordersRating : 0;
				$scope.ordersCount = (userStatisticsResponse.ordersCount) ? userStatisticsResponse.ordersCount : 0;
				$scope.successOrdersCount = (userStatisticsResponse.successOrdersCount) ? userStatisticsResponse.successOrdersCount : 0;
				$scope.partnersRating = (userStatisticsResponse.partnersRating) ? userStatisticsResponse.partnersRating : 0;
				$scope.summaryRating = Math.floor($scope.ratingOpenness * $rootScope.env.userRatingOpenessFactor
					+ $scope.successOrdersCount * $rootScope.env.userSuccessOrdersCountFactor);
				if (!$scope.summaryRating || ($scope.summaryRating === null) || ($scope.summaryRating === "NaN")) {
					$scope.summaryRating = 0;
				}
				var globalStatisticsResponse = usersProfileResource.getGlobalStatistics();

				globalStatisticsResponse.$promise.then(function() {
					$scope.allOrdersCount = globalStatisticsResponse.allOrdersCount;
					$scope.allSuccessOrdersCount = globalStatisticsResponse.allSuccessOrdersCount;
				});
			});

			$scope.userPropertiesMap['personalData'] = $sce.trustAsHtml(userProfileResponse.personalData);
			$scope.userPropertiesMap['personalDataEnabled'] = (userProfileResponse.personalDataEnabled === true) ? visible : notVisible;
			$scope.userPropertiesMap['bkiData'] = $sce.trustAsHtml(userProfileResponse.bkiData);
			$scope.userPhotoSrc = window.context + "webapi/photos/" + userProfileResponse.userId;
			var languagesResponse = languagesResource.findAll();
			languagesResponse.$promise.then(function() {
				$scope.languages.allLanguages.splice(0, $scope.languages.allLanguages.length);
				for (var i in languagesResponse.languages) {
					$scope.languages.allLanguages.push(languagesResponse.languages[i].code);
				}
			});
			if ($scope.edit) {
				startEditing();
			}
			$timeout(function() {
				$scope.$apply(function() {
					$scope.disabledEditButton = false;
				});
			});
		});
	}

	function reloadAllCurrencies(callback) {
		var currenciesResponse = currencyResource.findAll();
		currenciesMap = {};
		currenciesResponse.$promise.then(function() {
			var currencies = currenciesResponse.currencies;
			if (!currencies) {
				return;
			}
			for (var i = 0; i < currencies.length; i++) {
				var currency = currencies[i];
				currenciesMap[currency.code] = {};
				currenciesMap[currency.code].max = currency.maxValue;
				currenciesMap[currency.code].id = currency.id;
				currenciesMap[currency.code].step = currency.maxValue / 10;
				allCurrencies.push(currency.code);
				orderCurrencies.push(currency.code);
			}
			if (callback) {
				callback();
			}
		});
	}

	function addItems(destArray, original) {
		if (!destArray || !original) {
			return;
		}
		if (original instanceof Array) {
			for (var i in original) {
				var state = false;
				for (var j in destArray) {
					if (original[i] === destArray[j]) {
						state = true;
					}
				}
				if (state === false) {
					destArray.push(original[i]);
				}
			}
		} else {
			var state = false;
			for (var j in destArray) {
				if (original === destArray[j]) {
					state = true;
				}
			}
			if (state === false) {
				destArray.push(original);
			}
		}
	}

	$scope.showStatisticsPopover = function() {
		if ($scope.popoverShow === true) {
			$scope.popoverShow = false;
		} else {
			$scope.popoverShow = true;
		}
	};

	$scope.editAttribute = function(attribute) {
		if ($scope.disabledEditButton || !$scope.isCurrentUser) {
			return;
		}
		if ($scope.editMap[attribute] === true) {
			$scope.editMap[attribute] = false;
			if ($scope.editMap["old" + attribute] !== $scope.userPropertiesMap[attribute]) {
				disableEditButton();
				$scope.endEditing();
			}
		} else {
			$scope.editMap[attribute] = true;
			$scope.editMap["old" + attribute] = $scope.userPropertiesMap[attribute]
			$timeout(function() {
				angular.element("#" + attribute).focus();
			});
		}
		$(document).unbind('click');
	};

	$scope.disableEditAttribute = function(attribute) {
		$timeout(function() {
			$scope.$apply(function() {
				$scope.editMap[attribute] = false;
			});
			if ($scope.editMap["old" + attribute] !== $scope.userPropertiesMap[attribute]) {
				$scope.endEditing();
			}
		});
	};


	$scope.editNameAttribute = function() {
		if (!$scope.isCurrentUser) {
			return;
		}
		if ($scope.editName === true) {
			$scope.editName = false;
			if ($scope.oldName !== $scope.userPropertiesMap['name']) {
				$scope.endEditing();
			}
		} else {
			$scope.editName = true;
			$scope.oldName = $scope.userPropertiesMap['name'];
			$timeout(function() {
				angular.element("#xsUserName").focus();
				if (!angular.element("#xsUserName").is(":focus")) {
					angular.element("#userName").focus();
				}
			});
		}
	};

	$scope.editMailAttribute = function() {
		if (!$scope.isCurrentUser) {
			return;
		}
		if ($scope.editMail === true) {
			$scope.editMail = false;
			if ($scope.oldMail !== $scope.userPropertiesMap['mail']) {
				$scope.endEditing();
			}
		} else {
			$scope.editMail = true;
			$scope.oldMail = $scope.userPropertiesMap['mail'];
			$timeout(function() {
				angular.element("#mail").focus();
			});
		}
	};



	$scope.editProfile = function() {
		if (!$scope.isCurrentUser) {
			return;
		}
		if ($scope.disabledEditButton) {
			return;
		}
		if ($scope.edit) {
			$scope.edit = false;
		} else {
			$scope.edit = true;
		}
	};
	$scope.editAdditionalProfile = function() {
		if (!$scope.isCurrentUser) {
			return;
		}
		if ($scope.disabledEditButton) {
			return;
		}
		if ($scope.editAdditional) {
			$scope.editAdditional = false;
			$scope.updateProfile();
		} else {
			$scope.editAdditional = true;
		}
		disableEditButton();
	};
	function disableEditButton() {
		$scope.disabledEditButton = true;
		$timeout(function() {
			$scope.$apply(function() {
				$scope.disabledEditButton = true;
			});
		});
	}

	$scope.endEditing = function() {
		if ($scope.disabledEditButton) {
			return;
		}
		disableEditButton();
		var languages = "";
		for (var i in $scope.userPropertiesMap['languages']) {
			languages += $scope.userPropertiesMap['languages'][i] + ", "
		}
		if (languages.substring(languages.length - 2, languages.length) === ", ") {
			languages = languages.substring(0, languages.length - 2);
		}
		$scope.userPropertiesMap['languagesStr'] = languages;
		var currenciesStr = "";
		for (var i in $scope.userPropertiesMap['currencies']) {
			currenciesStr += $scope.userPropertiesMap['currencies'][i] + ", "
		}
		if (currenciesStr && currenciesStr.substring(currenciesStr.length - 2, currenciesStr.length) === ", ") {
			currenciesStr = currenciesStr.substring(0, currenciesStr.length - 2);
		}
		$scope.userPropertiesMap['currenciesStr'] = currenciesStr;
		$scope.updateProfile();
	};
	function startEditing() {
		if ($scope.currencies) {
			$scope.currencies += ", ";
		}
	}

	$scope.changeVisible = function(propertyName) {
		if ($scope.userPropertiesMap[propertyName] === visible) {
			$scope.userPropertiesMap[propertyName] = notVisible;
		} else if ($scope.userPropertiesMap[propertyName] === notVisible) {
			$scope.userPropertiesMap[propertyName] = visible;
		}
	};
	$scope.editSocialLinks = function() {
		if ($scope.socialLinksEdited === false) {
			$scope.socialLinksEdited = true;
		} else {
			$scope.socialLinksEdited = false;
		}
	};
	$scope.editPhones = function() {
		if ($scope.phonesEdited === false) {
			$scope.phonesEdited = true;
		} else {
			$scope.phonesEdited = false;
		}
	};
	$scope.editPassport = function() {
		if ($scope.editedPassport === false) {
			$scope.editedPassport = true;
		} else {
			$scope.editedPassport = false;
			$scope.updateProfile();
		}
	};
	$scope.updateProfile = function() {
		if (!$scope.isCurrentUser) {
			return;
		}
		var userMainAttributes = {};
		userMainAttributes.userId = $scope.currentUser.publicKey;
		userMainAttributes.name = $scope.userPropertiesMap['name'];
		userMainAttributes.phone = $scope.userPropertiesMap['phone'];
		if ($scope.userPropertiesMap['phoneEnabled'] === visible) {
			userMainAttributes.phoneEnabled = true;
		} else {
			userMainAttributes.phoneEnabled = false;
		}
		if ($scope.userPropertiesMap['mail'] && isValidEmail($scope.userPropertiesMap['mail'])) {
			userMainAttributes.mail = $scope.userPropertiesMap['mail'];
		}

		userMainAttributes.mailEnabled = ($scope.userPropertiesMap['mailEnabled'] === visible) ? true : false;
		if ($scope.userPropertiesMap['personalDataEnabled'] === visible) {
			userMainAttributes.personalDataEnabled = true;
		} else {
			userMainAttributes.personalDataEnabled = false;
		}
		var languages = [];
		if ($scope.userPropertiesMap['languages'] && ($scope.userPropertiesMap['languages'].length !== null)) {

			for (var i in $scope.userPropertiesMap['languages']) {
				languages[i] = {};
				languages[i].code = $scope.userPropertiesMap['languages'][i];
			}
		}
		userMainAttributes.languages = languages;
		userMainAttributes.languagesEnabled = ($scope.userPropertiesMap['languagesEnabled'] === visible) ? true : false;
		userMainAttributes.currencies = [];
		for (var i in $scope.userPropertiesMap['currencies']) {
			if (currenciesMap[$scope.userPropertiesMap['currencies'][i]] !== undefined) {
				userMainAttributes.currencies.push(currenciesMap[$scope.userPropertiesMap['currencies'][i]]);
			}
		}
		userMainAttributes.currenciesEnabled = ($scope.userPropertiesMap['currenciesEnabled'] === visible) ? true : false;
		userProfileResponse = usersProfileResource.updateUserMainAttributes({}, userMainAttributes);
		userProfileResponse.$promise.then(function() {
			var userSocialLinksRequest = {};
			userSocialLinksRequest.userId = $scope.currentUser.publicKey;
			userSocialLinksRequest.socialLinks = [];
			var socialLinks = $scope.userPropertiesMap['socialLinks'];
			for (var i in socialLinks) {
				var link = {};
				link.title = socialLinks[i].title;
				link.link = socialLinks[i].link;
				link.id = socialLinks[i].id;
				userSocialLinksRequest.socialLinks.push(link);
			}
			userProfileResponse = usersProfileResource.updateUserSocialLinks({}, userSocialLinksRequest);
			userProfileResponse.$promise.then(function() {
				var userVideosRequest = {};
				userVideosRequest.userId = $scope.currentUser.publicKey;
				userVideosRequest.videos = [];
				var videos = $scope.userPropertiesMap['videos'];
				for (var i in videos) {
					var link = {};
					link.title = videos[i].title;
					link.link = videos[i].link;
					link.id = videos[i].id;
					userVideosRequest.videos.push(link);
				}
				userProfileResponse = usersProfileResource.updateUserVideos({}, userVideosRequest);
				userProfileResponse.$promise.then(function() {
					var userPassportEnabledRequest = {};
					userPassportEnabledRequest.userId = $scope.currentUser.publicKey;
					userPassportEnabledRequest.passportEnabled = $scope.userPropertiesMap['passportEnabled'];
					userProfileResponse = usersProfileResource.updatePassportEnabled({}, userPassportEnabledRequest);
					userProfileResponse.$promise.then(function() {
						$timeout(function() {
							$scope.$apply(function() {
								$rootScope.userName = userMainAttributes.name;
								$scope.disabledEditButton = false;
							});
						});
					});
				});
			});
		});
	};

	function isValidEmail(email) {
		var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
		return re.test(email);
	}

	angular.element(document).on("focus", "#userName", function() {
		angular.element(this).mask("SSSSSSSSSSSSSSSSSSSSSSSSSSS",
			{'translation': {
					S: {pattern: /[A-Za-zА-Яа-я0-9\s]/}
				}});
	});

	$scope.changeUserPhoto = function() {
		if (!$scope.isCurrentUser) {
			return;
		}
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
	function loadImageError() {
		$scope.showUserPhotoError = true;
		$scope.imageSrc = null;
		$scope.editUserPhoto = false;
		$scope.$apply();
		return;
	}
	$scope.getFile = function(file) {
		$scope.editUserPhoto = true;
		$scope.showUserPhotoError = false;
		fileReader.readAsDataUrl(file, $scope).then(function(result) {
			$scope.imageSrc = result;
			var imag = new Image();
			imag.onload = function() {

				if ((result.indexOf("jpg") === -1)
					&& (result.indexOf("jpeg") === -1)) {
					$scope.photoErrorMessage = errorMessages["IMAGE_TYPE"];
					loadImageError();
				}
				if ((imag.width > PHOTO_MAX_WIDTH) || (imag.height > PHOTO_MAX_HEIGHT)) {
					$scope.photoErrorMessage = errorMessages["MAX_SIZE"];
					loadImageError();
				}
				if ((imag.width < PHOTO_MIN_WIDTH) || (imag.height < PHOTO_MIN_HEIGHT)) {
					$scope.photoErrorMessage = errorMessages["MIN_SIZE"];
					loadImageError();
				}

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
			};
			imag.src = $scope.imageSrc;
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
		var data = $scope.imageSrc.replace(/^data:image\/(jpg|jpeg);base64,/, "");
		userPhotoRequest.imageString = data;
		var userPhotoResponse = photosResource.setUserPhoto({}, userPhotoRequest);
		userPhotoResponse.$promise.then(function() {
			$timeout(function() {
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
		$scope.showUserPhotoError = false;
	};
	$scope.deleteUserPhoto = function() {
		var userPhotoRequest = {};
		userPhotoRequest.imageString = "";
		var userPhotoResponse = photosResource.setUserPhoto({}, userPhotoRequest);
		userPhotoResponse.$promise.then(function() {
			$timeout(function() {
				location.reload();
			}, 100);
		});
	};

	$scope.changeAdditionAttrsVisible = function() {
		if ($scope.additionAttrsHidden === false) {
			$scope.additionAttrsHidden = true;
		} else {
			$scope.additionAttrsHidden = false;
		}
	};
});