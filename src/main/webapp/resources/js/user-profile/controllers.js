var userProfileModule = angular.module("user-profile");

userProfileModule.controller("UserProfileController", function($scope, $rootScope, $modal, $location, usersProfileResource, currencyResource, fileReader) {
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
	var currenciesMap = {};
	var scEditorInitialized = false;
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
		}
	} else {
		$scope.isCurrentUser = false;
		userProfileResponse = usersProfileResource.getById({'id': userPublicKeyFromPath});
	}
	reloadData();

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
			$scope.userPropertiesMap['currencies'] = userProfileResponse.currencies;
			$scope.currencies = undefined;
			if (userProfileResponse.currencies) {
				var currencies = "";
				for (var i = 0; i < userProfileResponse.currencies.length; i++) {
					currencies += (userProfileResponse.currencies[i].currency.code + ", ");
				}
				currencies = currencies.substring(0, currencies.length - 2);
				$scope.currencies = currencies;
			}
			$scope.userPropertiesMap['personalData'] = userProfileResponse.personalData;
			$scope.userPropertiesMap['personalDataEnabled'] = (userProfileResponse.personalDataEnabled === true) ? visible : notVisible;
			$scope.userPhotoSrc = window.context + "webapi/profiles/" + userProfileResponse.publicKey + "/photo";

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

	$scope.initSCEditor = function() {
		if (!scEditorInitialized) {
			CKEDITOR.disableAutoInline = true;
			CKEDITOR.inline("personalEditable");
			$scope.reloadSCEditorInstance();
			scEditorInitialized = true;
		}
	};

	$scope.savePersonalPage = function() {
		var data = CKEDITOR.instances.personalEditable.getData();
		if (data !== $scope.userPropertiesMap['personalData']) {
			$scope.userPropertiesMap['personalData'] = data;
			updateProfile();
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
		$scope.disabledEditButton = true;
		setTimeout(function() {
			$scope.$apply(function() {
				$scope.disabledEditButton = false;
			});
		}, 500);
	};

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
		} else {
			for (var i = 0; i < $scope.userPropertiesMap['currencies'].length; i++) {
				if (currencies.indexOf($scope.userPropertiesMap['currencies'][i].currency.code) === -1) {
					$scope.userPropertiesMap['currencies'].splice(i, 1);
					i--;
				}
			}
		}
		updateProfile();
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
		setTimeout(function() {
			var languages = new Array("English", "Russian");

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

			angular.element("#phone").jqxMaskedInput({mask: '(###)###-####'});
			angular.element("#priceSlider").on('change', function(event) {
				if (!$scope.currency) {
					return;
				}
				$scope.$apply(function() {
					$scope.currency.startValue = event.args.value.rangeStart;
					$scope.currency.endValue = event.args.value.rangeEnd;
				});
			});

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
						if ($scope.userPropertiesMap['currencies'][i].currency.code === itemValue) {
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
						$scope.currency.startValue = 0;
						$scope.currency.endValue = 0;
					});
					//angular.element("#priceSlider").jqxSlider({min: 0, max: currenciesMap[itemValue].max, step: currenciesMap[itemValue].step, width: "100%"});
					angular.element("#priceSlider").jqxSlider({theme: "bootstrap", width: "100%", showButtons: true, ticksFrequency: 350, mode: 'fixed',
						max: currenciesMap[itemValue].max, step: currenciesMap[itemValue].step, rangeSlider: true});
					angular.element("#priceSlider").jqxSlider('setValue', [0, currenciesMap[itemValue].max]);

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
			if ($scope.userPropertiesMap['currencies'][i].currency.code === $scope.currency.code) {
				return;
			}
		}
		var currency = {};
		currency.currency = {};
		currency.currency.code = $scope.currency.code;
		currency.currency.id = $scope.currency.id;
		currency.startValue = $scope.currency.startValue;
		currency.endValue = $scope.currency.endValue;
		
		$scope.userPropertiesMap['currencies'].push(currency);
	}
	$scope.reloadSCEditorInstance = function() {
		if (($scope.userPropertiesMap['personalData'] === "DEFAULT")
				|| ($scope.userPropertiesMap['personalData'].indexOf("<p>DEFAULT</p>") === 0)) {
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
	function updateProfile() {
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

		userProfileResponse = usersProfileResource.updatePublicProfile({}, userPublicProfile);
		userProfileResponse.$promise.then(function() {
			reloadData();
		});

		//var userPrivateProfile = {};
	}

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