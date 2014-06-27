var userProfileModule = angular.module("user-profile");

userProfileModule.controller("UserProfileController", function($scope, $rootScope, $modal, $location, usersResource, usersProfileResource, currencyResource, fileReader, $timeout) {
	var PHOTO_MIN_HEIGHT = 400;
	var PHOTO_MIN_WIDTH = 300;
	var PHOTO_MAX_HEIGHT = 2000;
	var PHOTO_MAX_WIDTH = 2000;
	var resultX1 = 0;
	var resultX2 = PHOTO_MIN_WIDTH;
	var resultY1 = 0;
	var resultY2 = PHOTO_MIN_HEIGHT;
	$scope.edit = false;
	$scope.editedPassport = false;
	$scope.editUserPhoto = false;
	$scope.userProfile = {};
	$scope.userPropertiesMap = {};
	$scope.hasFocus = false;
	$scope.isCurrentUser = false;




	var currenciesMap = {};
	$scope.scEditor = {};
	$scope.scEditor.dataInitialized = false;
	$scope.scEditor.BKIInitialized = false;

	var visible = "open";
	var notVisible = "close";
	var defaultPersonalData = '<p style="text-align: center;"><img src="resources/img/elephant-logo.png"/></p>';
	var userPublicKeyFromPath = $location.$$path.replace("/users/", "");
	var userProfileResponse;
	var currentUser = usersResource.current({});
	currentUser.$promise.then(function() {
		if (currentUser.publicKey && userPublicKeyFromPath === currentUser.publicKey) {
			if (currentUser.publicKey.indexOf("@") === -1) {
				window.location.href = window.context;
			} else {
				$scope.isCurrentUser = true;
				$scope.userPropertiesMap['isCurrentUser'] = true;
				userProfileResponse = usersProfileResource.current({});
			}
		} else {
			$scope.isCurrentUser = false;
			$scope.userPropertiesMap['isCurrentUser'] = false;
			userProfileResponse = usersProfileResource.getById({'id': userPublicKeyFromPath});
		}
		reloadData();
	});

	function reloadData() {
		$scope.currency = undefined;
		userProfileResponse.$promise.then(function() {
			$scope.userPropertiesMap['name'] = userProfileResponse.name;
			$scope.userPropertiesMap['mail'] = userProfileResponse.mail;
			$scope.userPropertiesMap['mailEnabled'] = (userProfileResponse.mailEnabled === true) ? visible : notVisible;
			$scope.userPropertiesMap['phone'] = userProfileResponse.phone;
			$scope.userPropertiesMap['phoneEnabled'] = (userProfileResponse.phoneEnabled === true) ? visible : notVisible;
			$scope.userPropertiesMap['languagesEnabled'] = (userProfileResponse.languagesEnabled === true) ? visible : notVisible;
			$scope.userPropertiesMap['languages'] = "";
			if (userProfileResponse.languages) {
				var languages = "";
				for (var i = 0; i < userProfileResponse.languages.length; i++) {
					languages += (userProfileResponse.languages[i] + ", ");
				}
				languages = languages.substring(0, languages.length - 2);
				$scope.userPropertiesMap['languages'] = languages;
			}
			$scope.userPropertiesMap['currenciesEnabled'] = (userProfileResponse.currenciesEnabled === true) ? visible : notVisible;
			$scope.currencies = undefined;
			if (userProfileResponse.currencies) {
				var currencies = "";
				for (var i = 0; i < userProfileResponse.currencies.length; i++) {
					currencies += (userProfileResponse.currencies[i].code + ", ");
				}
				currencies = currencies.substring(0, currencies.length - 2);
				$scope.currencies = currencies;
				if (userProfileResponse.currencies) {
					$scope.userPropertiesMap['currencies'] = userProfileResponse.currencies;
				} else {
					$scope.userPropertiesMap['currencies'] = [];
				}

			}
			$scope.userPropertiesMap['passportEnabled'] = userProfileResponse.passportEnabled;
			$scope.userPropertiesMap['phones'] = (userProfileResponse.phones) ? userProfileResponse.phones : [];
			$scope.userPropertiesMap['videos'] = (userProfileResponse.videos) ? userProfileResponse.videos : [];
			$scope.userPropertiesMap['names'] = (userProfileResponse.namesLinks) ? userProfileResponse.namesLinks : [];
			$scope.userPropertiesMap['socialLinks'] = (userProfileResponse.socialLinks) ? userProfileResponse.socialLinks : [];
			if (userProfileResponse.statistic) {
				$scope.ratingOpenness = userProfileResponse.statistic.opennessRating;
				$scope.ratingTransactions = userProfileResponse.statistic.transactionsRating;
				$scope.ordersSumValue = userProfileResponse.statistic.ordersSumValue;
				$scope.transactionsSum = userProfileResponse.statistic.transactionsSum;
				$scope.successTransactionsSum = userProfileResponse.statistic.successTransactionsSum;
				$scope.allTransactionsSum = userProfileResponse.statistic.allTransactionsSum;
				$scope.allSuccessTransactionsSum = userProfileResponse.statistic.allSuccessTransactionsSum;

			}

			$scope.userPropertiesMap['personalData'] = userProfileResponse.personalData;
			$scope.userPropertiesMap['personalDataEnabled'] = (userProfileResponse.personalDataEnabled === true) ? visible : notVisible;
			$scope.userPhotoSrc = window.context + "webapi/profiles/" + userProfileResponse.publicKey + "/photo";
			$scope.initBKISCEditor(reloadBKIscEditor);

			var currenciesResponse = currencyResource.findAll();

			currenciesResponse.$promise.then(function() {
				var currencies = currenciesResponse.currencies;
				for (var i = 0; i < currencies.length; i++) {
					var currency = currencies[i];
					currenciesMap[currency.code] = {};
					currenciesMap[currency.code].max = currency.maxValue;
					currenciesMap[currency.code].id = currency.id;
					currenciesMap[currency.code].step = currency.maxValue / 10;
				}
			});
		});
	}

	$scope.initDataSCEditor = function(id, reload) {
		if (!$scope.scEditor.dataInitialized) {
			if ($scope.isCurrentUser) {
				CKEDITOR.disableAutoInline = true;
				CKEDITOR.inline(id);
			}

			reload();
			$scope.scEditor.dataInitialized = true;
		}
	};
	$scope.initBKISCEditor = function(reload) {

		if ($scope.scEditor.BKIInitialized === false) {
			if ($scope.isCurrentUser) {
				$timeout(function() {
					CKEDITOR.disableAutoInline = true;
					CKEDITOR.inline("bkiEditable");
				}, 500);

			}

			reload();
			$scope.scEditor.BKIInitialized = true;
		}
	};

	function reloadBKIscEditor() {
		//
	}



	$scope.savePersonalPage = function() {
		var data = CKEDITOR.instances.personalEditable.getData();
		if (data !== $scope.userPropertiesMap['personalData']) {
			$scope.userPropertiesMap['personalData'] = data;
			$scope.updateProfile();
		}
	};

	$scope.editProfile = function() {
		if ($scope.disabledEditButton) {
			return;
		}
		if ($scope.edit) {
			endEditing();
		} else {
			startEditing();
		}
		disableEditButton();
	};
	
	function disableEditButton() {
		$scope.disabledEditButton = true;
		$timeout(function() {
			$scope.$apply(function() {
				$scope.disabledEditButton = false;
			});
		}, 500);
	}

	function endEditing() {
		$scope.edit = false;

		var languages = angular.element("#languages").val();
		if (languages.substring(languages.length - 2, languages.length) === ", ") {
			languages = languages.substring(0, languages.length - 2);
		}
		$scope.userPropertiesMap['languages'] = languages;
		addToCurrencies();

		var currenciesStr = angular.element("#currencies").val();
		if (currenciesStr && currenciesStr.substring(currenciesStr.length - 2, currenciesStr.length) === ", ") {
			currenciesStr = currenciesStr.substring(0, currenciesStr.length - 2);
		}
		$scope.currencies = currenciesStr;

		var currencies = currenciesStr.split(/\s*,\s*/);

		if (!currencies) {
			$scope.userPropertiesMap['currencies'] = null;
		} else if ($scope.userPropertiesMap['currencies']) {
			for (var i = 0; i < $scope.userPropertiesMap['currencies'].length; i++) {
				if (currencies.indexOf($scope.userPropertiesMap['currencies'][i].code) === -1) {
					$scope.userPropertiesMap['currencies'].splice(i, 1);
					i--;
				}
			}
		}
		$scope.updateProfile();
		$scope.cancel();
	}

	function startEditing() {
		$scope.edit = true;
		if ($scope.userPropertiesMap['languages']) {
			$scope.userPropertiesMap['languages'] += ", ";
		}
		if ($scope.currencies) {
			$scope.currencies += ", ";
		}
		$timeout(function() {
			var languages = new Array("English", "Russian", "Arabic");

			angular.element("#languages").jqxInput({
				source: function(query, response) {
					var item = query.split(/,\s*/).pop();
					angular.element("#languages").jqxInput({query: item});
					response(languages);
				},
				renderer: function(itemValue, inputValue) {
					var terms = inputValue.split(/,\s*/);
					terms.pop();
					terms.push(itemValue);
					terms.push("");
					var value = terms.join(", ");
					return value;
				}
			});

			var allCurrencies = [];
			for (var key in currenciesMap) {
				allCurrencies.push(key);
			}

			angular.element("#phone").jqxMaskedInput({mask: '+## (###)###-##-##'});
			angular.element("#phone").jqxMaskedInput('inputValue', "07");

			angular.element("#currencies").jqxInput({
				source: function(query, response) {
					var item = query.split(/,\s*/).pop();
					angular.element("#currencies").jqxInput({query: item});
					response(allCurrencies);
				},
				renderer: function(itemValue, inputValue) {
					if ($scope.currency) {
						addToCurrencies();
					}

					var terms = inputValue.split(/,\s*/);
					terms.pop();

					if (!$scope.userPropertiesMap['currencies']) {
						$scope.userPropertiesMap['currencies'] = [];
					}
					for (var i = 0; i < $scope.userPropertiesMap['currencies'].length; i++) {
						if ($scope.userPropertiesMap['currencies'][i].code === itemValue) {
							terms.push("");
							var value = terms.join(", ");
							return value;
						}
					}
					terms.push(itemValue);
					terms.push("");
					var value = terms.join(", ");

					$scope.$apply(function() {
						$scope.currency = {};

						$scope.currency.code = itemValue;
						$scope.currency.id = currenciesMap[itemValue].id;
					});
					return value;
				}
			});
			angular.element("#phone").jqxMaskedInput('maskedValue', $scope.userPropertiesMap['phone']);
		}, 10);
	}

	function addToCurrencies() {
		if (!$scope.currency) {
			return;
		}
		for (var i = 0; i < $scope.userPropertiesMap['currencies'].length; i++) {
			if ($scope.userPropertiesMap['currencies'][i].code === $scope.currency.code) {
				return;
			}
		}
		var currency = {};
		currency.code = $scope.currency.code;
		currency.id = $scope.currency.id;

		$scope.userPropertiesMap['currencies'].push(currency);
	}
	$scope.reloadSCEditorInstance = function() {
		if ($scope.userPropertiesMap['personalData'] && (($scope.userPropertiesMap['personalData'] === "DEFAULT")
				|| ($scope.userPropertiesMap['personalData'].indexOf("<p>DEFAULT</p>") === 0))) {
			if ($scope.isCurrentUser) {
				CKEDITOR.instances.personalEditable.setData(defaultPersonalData);
			} else {
				angular.element("#personalEditable").html(defaultPersonalData);
			}
		} else {
			if ($scope.isCurrentUser) {
				CKEDITOR.instances.personalEditable.setData($scope.userPropertiesMap['personalData']);
			} else {
				angular.element("#personalEditable").html($scope.userPropertiesMap['personalData']);
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

		var userPublicProfile = {};
		userPublicProfile.name = $scope.userPropertiesMap['name'];
		userPublicProfile.phone = $scope.userPropertiesMap['phone'];
		if ($scope.userPropertiesMap['phoneEnabled'] === visible) {
			userPublicProfile.phoneEnabled = true;
		} else {
			userPublicProfile.phoneEnabled = false;
		}

		userPublicProfile.mail = $scope.userPropertiesMap['mail'];
		userPublicProfile.mailEnabled = ($scope.userPropertiesMap['mailEnabled'] === visible) ? true : false;

		if ($scope.userPropertiesMap['personalDataEnabled'] === visible) {
			userPublicProfile.personalDataEnabled = true;
		} else {
			userPublicProfile.personalDataEnabled = false;
		}

		userPublicProfile.personalData = $scope.userPropertiesMap['personalData'];
		userPublicProfile.languages = $scope.userPropertiesMap['languages'].split(/\s*,\s*/);
		userPublicProfile.languagesEnabled = ($scope.userPropertiesMap['languagesEnabled'] === visible) ? true : false;
		userPublicProfile.currencies = $scope.userPropertiesMap['currencies'];
		userPublicProfile.currenciesEnabled = ($scope.userPropertiesMap['currenciesEnabled'] === visible) ? true : false;
		userPublicProfile.socialLinks = [];
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
		userPublicProfile.passportEnabled = $scope.userPropertiesMap['passportEnabled'];

		userProfileResponse = usersProfileResource.updatePublicProfile({}, userPublicProfile);
		userProfileResponse.$promise.then(function() {
			reloadData();
		});

		//var userPrivateProfile = {};
	};

	angular.element(document).on("focus", "#name", function() {
		angular.element(this).mask("SSSSSSSSSSSSSSSSSSSSSSSSSSS",
				{'translation': {
						S: {pattern: /[A-Za-zА-Яа-я0-9]/}
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

		var data = $scope.imageSrc.replace(/^data:image\/(png|jpg|jpeg);base64,/, "");
		userPhotoRequest.imageString = data;
		var userPhotoResponse = usersProfileResource.setUserPhoto({}, userPhotoRequest);
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
	};
	$scope.deleteUserPhoto = function() {
		var userPhotoRequest = {};
		userPhotoRequest.imageString = "";
		var userPhotoResponse = usersProfileResource.setUserPhoto({}, userPhotoRequest);
		userPhotoResponse.$promise.then(function() {
			$timeout(function() {
				location.reload();
			}, 100);
		});
	};
});