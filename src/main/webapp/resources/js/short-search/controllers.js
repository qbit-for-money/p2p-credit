var navbarModule = angular.module("navbar");

navbarModule.controller("ShortSearchController", function($scope, $rootScope, userProfileService, getRandBinary, authService, $timeout) {

	$scope.allCurrencies = [];
	$scope.allCurrenciesWithoutPersent = [];
	$scope.selectedGivingCurrency = "RUR";
	$scope.selectedTakingCurrency = "%";
	$scope.takingValue = "";
	$scope.givingValue = "";
	$scope.type = "Взять";
	$scope.types = [{
			id: 0,
			name: "Взять"
		}, {
			id: 1,
			name: "Дать"
		}, {
			id: 2,
			name: "Обменять"
		}];


	/*var givingCurrencyFromStorage = window.localStorage.getItem("GIVING_CURRENCY");
	var takingCurrencyFromStorage = window.localStorage.getItem("TAKING_CURRENCY");
	var givingValueFromStorage = window.localStorage.getItem("GIVING_VALUE");
	var takingValueFromStorage = window.localStorage.getItem("TAKING_VALUE");
	var type = window.localStorage.getItem("TYPE");

	if (givingCurrencyFromStorage !== null) {
		$scope.selectedGivingCurrency = givingCurrencyFromStorage;
		//window.localStorage.removeItem("GIVING_CURRENCY");
	} else {
		$scope.selectedGivingCurrency = "RUR";
	}
	if (givingCurrencyFromStorage !== null) {
		$scope.selectedTakingCurrency = takingCurrencyFromStorage;
		//window.localStorage.removeItem("TAKING_CURRENCY");
	} else {
		$scope.selectedTakingCurrency = "%";
	}
	if (givingValueFromStorage !== null) {
		$scope.givingValue = parseFloat(givingValueFromStorage);
		//window.localStorage.removeItem("GIVING_VALUE");
	} else {
		$scope.givingValue = "";
	}
	if (takingValueFromStorage !== null) {
		$scope.takingValue = parseFloat(takingValueFromStorage);
		//window.localStorage.removeItem("TAKING_VALUE");
	} else {
		$scope.takingValue = "";
	}
	if (type !== null) {
		//$timeout(function() {
			//$scope.$apply(function() {
				$scope.type = type;
				for(var i in $scope.types) {
					if($scope.types[i].name === $scope.type) {
						$scope.selectedItem = $scope.types[i].id;
					}
				}
		//	});
		//});
		//window.localStorage.removeItem("TYPE");
	} else {
		$scope.type = "Взять";
	}

	console.log(givingCurrencyFromStorage);
	console.log(takingCurrencyFromStorage);
	console.log(givingValueFromStorage);
	console.log(takingValueFromStorage);
	console.log(type);*/


	$scope.selectType = function(selectedItem) {
		$scope.type = selectedItem.item.name;
		if ($scope.type === "Взять") {
			$scope.selectedGivingCurrency = "RUR";
			$scope.selectedTakingCurrency = "%";
		} else if ($scope.type === "Дать") {
			$scope.selectedGivingCurrency = "%";
			$scope.selectedTakingCurrency = "RUR";
		} else {
			$scope.selectedGivingCurrency = "RUR";
			$scope.selectedTakingCurrency = "BTC";
		}
		/*if(givingCurrencyFromStorage) {
			$scope.selectedGivingCurrency = givingCurrencyFromStorage;
		}
		if(takingCurrencyFromStorage) {
			$scope.selectedTakingCurrency = takingCurrencyFromStorage;
		}*/
	};

	var currenciesMap = {};
	$scope.ordersSearchMap = {};
	$scope.ordersSearchMap['orderCategories'] = [];
	$scope.ordersSearchMap['orderTakingValue'];

	userProfileService.getAllCurrencies(function(currencies) {
		for (var i in currencies) {
			var currency = currencies[i];
			currenciesMap[currency.code] = {};
			currenciesMap[currency.code].max = currency.maxValue;
			currenciesMap[currency.code].id = currency.id;
			currenciesMap[currency.code].step = currency.maxValue / 10;
			$scope.allCurrencies.push(currency.code);
			if (currency.id !== "PERCENT") {
				$scope.allCurrenciesWithoutPersent.push(currency.code);
			}
		}
	});

	$scope.search = function() {
		$rootScope.searchRequest = {};
		$rootScope.searchRequest.selectedGivingCurrency = angular.element("#giving-currency").find(".li-item").text();
		$rootScope.searchRequest.givingValue = angular.element("#giving-currency").find("input").val();
		if (!$rootScope.searchRequest.givingValue) {
			$rootScope.searchRequest.givingValue = angular.element("#giving-currency-min").find("input").val();
			$rootScope.searchRequest.selectedGivingCurrency = angular.element("#giving-currency-min").find(".li-item").text();
		}
		$rootScope.searchRequest.selectedTakingCurrency = angular.element("#taking-currency").find(".li-item").text();
		$rootScope.searchRequest.takingValue = angular.element("#taking-currency").find("input").val();
		if (!$rootScope.searchRequest.takingValue) {
			$rootScope.searchRequest.takingValue = angular.element("#taking-currency-min").find("input").val();
			$rootScope.searchRequest.selectedTakingCurrency = angular.element("#taking-currency-min").find(".li-item").text();
		}
		if ($rootScope.searchRequest.selectedGivingCurrency === "%") {
			$rootScope.searchRequest.type = "credit";
		} else if ($rootScope.searchRequest.selectedTakingCurrency === "%") {
			$rootScope.searchRequest.type = "borrow";
		} else {
			$rootScope.searchRequest.type = "exchange";
		}

		console.log($rootScope.searchRequest)
		if (!$rootScope.user || (($rootScope.user.publicKey.indexOf("@") === -1)
			&& ($rootScope.user.publicKey.indexOf("vk-") === -1) && ($rootScope.user.publicKey.indexOf("fb-") === -1))) {
			//$rootScope.url = window.context + "#/orders";

			window.localStorage.setItem("GIVING_CURRENCY", $rootScope.searchRequest.selectedGivingCurrency);
			window.localStorage.setItem("TAKING_CURRENCY", $rootScope.searchRequest.selectedTakingCurrency);
			window.localStorage.setItem("GIVING_VALUE", $rootScope.searchRequest.givingValue);
			window.localStorage.setItem("TAKING_VALUE", $rootScope.searchRequest.takingValue);
			window.localStorage.setItem("TYPE", $rootScope.searchRequest.type);
			window.localStorage.setItem("URL", window.context + "#/orders");
			authService.openAuthDialog(false, true);
		} else {
			window.location.href = window.context + "#/orders";
		}
	};
});


