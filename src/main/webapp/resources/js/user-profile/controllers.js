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
	errorMessages["MIN_SIZE"] = "User photo size should be greater than 400x300";
	errorMessages["MAX_SIZE"] = "User photo size should be less than 2000x2000";
	errorMessages["IMAGE_TYPE"] = "Image type should be JPG";
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
	$scope.isOrdersFormOpening = false;
	var currenciesMap = {};
	$scope.scEditor = {};
	$scope.scEditor.dataInitialized = false;
	$scope.scEditor.BKIInitialized = false;
	$scope.scEditor.orderDataInitialized = false;
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
	$scope.additionAttrsHidden = true;
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
	$scope.currentUser.$promise.then(function() {
		if (!$scope.currentUser.publicKey) {
			authService.openAuthDialog(true, false, "/users/" + $scope.userPublicKeyFromPath);
		}
		if ($scope.userPublicKeyFromPath === $scope.currentUser.publicKey) {
			if (($scope.currentUser.publicKey.indexOf("@") === -1) && ($scope.currentUser.publicKey.indexOf("vk/") === -1)) {
				window.location.href = window.context;
			} else {
				$scope.isCurrentUser = true;
				$scope.userPropertiesMap['isCurrentUser'] = true;
				userProfileResponse = usersProfileResource.current({});
			}
		} else {
			angular.element("#personalEditable").addClass("invisible");
			$scope.isCurrentUser = false;
			$scope.userPropertiesMap['isCurrentUser'] = false;
			userProfileResponse = usersProfileResource.getById({'id': $scope.userPublicKeyFromPath});
		}
		reloadAllCurrencies();
		reloadData();
	});
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
				/*if ($scope.userPropertiesMap['languages']) {
				 $scope.userPropertiesMap['languages'].splice(0, $scope.userPropertiesMap['languages'].length);
				 }*/
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
			var userStatisticsResponse = usersProfileResource.getStatisticsById({'id': $scope.userPublicKeyFromPath});
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
			console.log("** " + JSON.stringify(userProfileResponse))
			//reloadSCEditorInstance();
			//reloadBKIscEditor();
			//$scope.isValidOrder();
			$timeout(function() {
				$scope.$apply(function() {
					$scope.disabledEditButton = false;
				});
			});
		});
	}

	/*$scope.reloadLikesCount = function() {
	 
	 
	 
	 $scope.likesCount
	 }*/

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

	function getDigits(x) {
		return Math.log(x) / Math.LN10 + 1;
	}

	/*function initDataSCEditor() {
	 console.log("%%% " + $scope.scEditor.dataInitialized)
	 if (!$scope.scEditor.dataInitialized) {
	 if ($scope.isCurrentUser) {
	 CKEDITOR.disableAutoInline = true;
	 CKEDITOR.inline("personalEditable");
	 }
	 $scope.scEditor.dataInitialized = true;
	 $timeout(function() {
	 reloadSCEditorInstance();
	 });
	 } else {
	 reloadSCEditorInstance();
	 }
	 }*/

	/*function reloadSCEditorInstance() {
	 if ($scope.userPropertiesMap['personalData'] && (($scope.userPropertiesMap['personalData'].toString() === "DEFAULT")
	 || ($scope.userPropertiesMap['personalData'].toString().indexOf("<p>DEFAULT</p>") === 0))) {
	 if ($scope.isCurrentUser && $scope.edit) {
	 CKEDITOR.instances.personalEditable.setData(defaultPersonalData);
	 } else if (!$scope.edit) {
	 $scope.userPropertiesMap['personalData'] = $sce.trustAsHtml(defaultPersonalData);
	 }
	 } else if ($scope.isCurrentUser && $scope.edit) {
	 if ($scope.userPropertiesMap['personalData']) {
	 CKEDITOR.instances.personalEditable.setData($scope.userPropertiesMap['personalData'].toString());
	 }
	 }
	 }*/

	/*function initBKIscEditor() {
	 
	 if ($scope.scEditor.BKIInitialized === false) {
	 if ($scope.isCurrentUser) {
	 CKEDITOR.disableAutoInline = true;
	 CKEDITOR.inline("bkiEditable");
	 }
	 $scope.scEditor.BKIInitialized = true;
	 } else {
	 reloadBKIscEditor();
	 }
	 }*/

	/*function reloadBKIscEditor() {
	 if ($scope.userPropertiesMap['bkiData'] && (($scope.userPropertiesMap['bkiData'].toString() === "DEFAULT")
	 || ($scope.userPropertiesMap['bkiData'].toString().indexOf("<p>DEFAULT</p>") === 0))) {
	 if ($scope.isCurrentUser && $scope.editAdditional) {
	 CKEDITOR.instances.bkiEditable.setData(defaultPersonalData);
	 } else if (!$scope.editAdditional) {
	 $scope.userPropertiesMap['bkiData'] = $sce.trustAsHtml(defaultPersonalData);
	 }
	 } else if ($scope.isCurrentUser && $scope.editAdditional && $scope.userPropertiesMap['bkiData']) {
	 CKEDITOR.instances.bkiEditable.setData($scope.userPropertiesMap['bkiData'].toString());
	 }
	 }*/

	/*$scope.savePersonalPage = function() {
	 var data = CKEDITOR.instances.personalEditable.getData();
	 if (!$scope.userPropertiesMap['personalData'] || data !== $scope.userPropertiesMap['personalData'].toString()) {
	 $scope.userPropertiesMap['personalData'] = $sce.trustAsHtml(data);
	 }
	 $scope.endEditing();
	 };*/
	/*$scope.saveAdditionalData = function() {
	 var data = CKEDITOR.instances.bkiEditable.getData();
	 if (!$scope.userPropertiesMap['bkiData'] || data !== $scope.userPropertiesMap['bkiData'].toString()) {
	 $scope.userPropertiesMap['bkiData'] = $sce.trustAsHtml(data);
	 }
	 $scope.updateProfile();
	 };*/
	$scope.showStatisticsPopover = function() {
		if ($scope.popoverShow === true) {
			$scope.popoverShow = false;
		} else {
			$scope.popoverShow = true;
		}
	};

	$scope.editAttribute = function(attribute) {
		if ($scope.disabledEditButton) {
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
				/*angular.element("#s2id_" + attribute).find(".select2-input").focus();
				 if(!angular.element("#s2id_" + attribute).find(".select2-input").is(":focus")) {
				 angular.element("#" + attribute).focus();
				 }*/
				angular.element("#" + attribute).focus();
				//angular.element("#s2id_languages").find(".select2-input").focus();
			});
		}
		console.log(attribute + " " + $scope.editMap[attribute])
		$(document).unbind('click');
	}

	$scope.disableEditAttribute = function(attribute) {
		if ($scope.disabledEditButton) {
			return;
		}
		$timeout(function() {
			$scope.$apply(function() {
				$scope.editMap[attribute] = false;
			});
			if ($scope.editMap["old" + attribute] !== $scope.userPropertiesMap[attribute]) {
				disableEditButton();
				$scope.endEditing();
			}
		});

	}


	$scope.editNameAttribute = function() {
		if ($scope.disabledEditButton) {
			return;
		}
		if ($scope.editName === true) {
			$scope.editName = false;
			if ($scope.oldName !== $scope.userPropertiesMap['name']) {
				console.log("EDIT " + $scope.editName)
				disableEditButton();
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
		if ($scope.disabledEditButton) {
			return;
		}
		if ($scope.editMail === true) {
			$scope.editMail = false;
			console.log("EDIT " + $scope.userPropertiesMap['mail'])
			if ($scope.oldMail !== $scope.userPropertiesMap['mail']) {
				disableEditButton();
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
		if ($scope.disabledEditButton) {
			return;
		}
		if ($scope.edit) {
			//$scope.cancel();
			$scope.edit = false;
			//$scope.endEditing();
		} else {
			$scope.edit = true;
			//startEditing();
			if (!$scope.additionAttrsHidden) {
				//initDataSCEditor();
			}
		}
		//disableEditButton();
	};
	$scope.editAdditionalProfile = function() {
		if ($scope.disabledEditButton) {
			return;
		}
		if ($scope.editAdditional) {
			$scope.editAdditional = false;
			$scope.updateProfile();
		} else {
			$scope.editAdditional = true;
			/*initBKIscEditor();
			 $timeout(function() {
			 reloadBKIscEditor();
			 });*/
		}
		disableEditButton();
	};
	function disableEditButton() {
		//$scope.disabledEditButton = true;
		$timeout(function() {
			$scope.$apply(function() {
				$scope.disabledEditButton = true;
			});
		});
		/*$timeout(function() {
		 $scope.$apply(function() {
		 $scope.disabledEditButton = false;
		 });
		 }, 500);*/
	}

	$scope.endEditing = function() {
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
			languages += $scope.userPropertiesMap['currencies'][i] + ", "
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
		if ($scope.disabledEditButton) {
			return;
		}
		disableEditButton();
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

		//userPublicProfile.personalData = ($scope.userPropertiesMap['personalData']) ? $scope.userPropertiesMap['personalData'].toString() : null;
		//userPublicProfile.bkiData = ($scope.userPropertiesMap['bkiData']) ? $scope.userPropertiesMap['bkiData'].toString() : null;
		//userPublicProfile.languages = [];
		var languages = [];
		if ($scope.userPropertiesMap['languages'] && ($scope.userPropertiesMap['languages'].length !== null)) {

			for (var i in $scope.userPropertiesMap['languages']) {
				//if($scope.languagesMap[$scope.userPropertiesMap['languages'][i]]) {
				//userPublicProfile.languages[i] = $scope.languagesMap[$scope.userPropertiesMap['languages'][i]];
				//} else {

				languages[i] = {};
				//userPublicProfile.languages[i].custom = false;
				languages[i].code = $scope.userPropertiesMap['languages'][i];

				//}


				//userPublicProfile.languages[i].code = $scope.userPropertiesMap['languages'][i];
			}
		}
		userMainAttributes.languages = languages;
		console.log(JSON.stringify($scope.userPropertiesMap['languages']))
		//userPublicProfile.languages = $scope.userPropertiesMap['languages'];
		userMainAttributes.languagesEnabled = ($scope.userPropertiesMap['languagesEnabled'] === visible) ? true : false;
		userMainAttributes.currencies = [];
		for (var i in $scope.userPropertiesMap['currencies']) {
			if (currenciesMap[$scope.userPropertiesMap['currencies'][i]] !== undefined) {
				userMainAttributes.currencies.push(currenciesMap[$scope.userPropertiesMap['currencies'][i]]);
			}
		}
		userMainAttributes.currenciesEnabled = ($scope.userPropertiesMap['currenciesEnabled'] === visible) ? true : false;
		/*userPublicProfile.socialLinks = [];
		 var socialLinks = $scope.userPropertiesMap['socialLinks'];
		 for (var i in socialLinks) {
		 var link = {};
		 link.title = socialLinks[i].title;
		 link.link = socialLinks[i].link;
		 link.id = socialLinks[i].id;
		 userPublicProfile.socialLinks.push(link);
		 }
		 userPublicProfile.phones = [];
		 var phones = $scope.userPropertiesMap['phones'];
		 for (var i in phones) {
		 var link = {};
		 link.title = phones[i].title;
		 link.link = phones[i].link;
		 link.id = phones[i].id;
		 userPublicProfile.phones.push(link);
		 }
		 userPublicProfile.videos = [];
		 var videos = $scope.userPropertiesMap['videos'];
		 for (var i in videos) {
		 var link = {};
		 link.title = videos[i].title;
		 link.link = videos[i].link;
		 link.id = videos[i].id;
		 userPublicProfile.videos.push(link);
		 }
		 userPublicProfile.namesLinks = [];
		 var names = $scope.userPropertiesMap['names'];
		 for (var i in names) {
		 var link = {};
		 link.title = names[i].title;
		 link.link = names[i].link;
		 link.id = names[i].id;
		 userPublicProfile.namesLinks.push(link);
		 }
		 userPublicProfile.passportEnabled = $scope.userPropertiesMap['passportEnabled'];*/
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
						reloadData();
					});
				});
			});
		});
		//var userPrivateProfile = {};
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
	/*angular.element(document).on("focus", "#mail", function() {
	 angular.element(this).mask("SSSSSSSSSSSSSS",
	 {'translation': {
	 S: {pattern: /[a-zA-Z0-9._%-]+@[a-zA-Z0-9-]+\\.[a-zA-Z]{2,4}/}
	 }});
	 });*/

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
		var data = $scope.imageSrc.replace(/^data:image\/(png|jpg|jpeg);base64,/, "");
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
			if ($scope.edit) {
				//	initDataSCEditor();
			}
		}
	};
});