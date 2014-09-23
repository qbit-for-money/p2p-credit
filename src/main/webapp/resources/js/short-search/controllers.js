var navbarModule = angular.module("navbar");

navbarModule.controller("ShortSearchController", function($scope, $rootScope, userProfileService, getRandBinary, authService) {
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
		if(!$rootScope.searchRequest.givingValue) {
			$rootScope.searchRequest.givingValue = angular.element("#giving-currency-min").find("input").val();
			$rootScope.searchRequest.selectedGivingCurrency = angular.element("#giving-currency-min").find(".li-item").text();
		}
		$rootScope.searchRequest.selectedTakingCurrency = angular.element("#taking-currency").find(".li-item").text();
		$rootScope.searchRequest.takingValue = angular.element("#taking-currency").find("input").val();
		if(!$rootScope.searchRequest.takingValue) {
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
			authService.openAuthDialog(false, true, window.context + "#/orders");
		} else {
			window.location.href = window.context + "#/orders";
		}
	};
});


