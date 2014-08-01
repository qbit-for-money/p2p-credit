var navbarModule = angular.module("navbar");

navbarModule.controller("ShortSearchController", function($scope, $rootScope, $modal, $location, usersResource, userService, usersProfileResource, currencyResource, categoriesResource, ordersResource, fileReader, $timeout, $sce, authService, userProfileService, languagesResource) {
	$scope.allCategories = [];
	$scope.allCurrencies = [];
	$scope.selectedGivingCurrency = "RUR";
	$scope.selectedTakingCurrency = "RUR";
	$scope.takingValue = "";
	$scope.givingValue = "";

	var currenciesMap = {};
	$scope.ordersSearchMap = {};
	$scope.ordersSearchMap['orderCategories'] = [];
	$scope.ordersSearchMap['orderTakingValue'];

	$scope.categorySelect2Options = {
		allowClear: true,
		tags: $scope.allCategories,
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

	userProfileService.getAllCurrencies(function(currencies) {
		for (var i in currencies) {
			var currency = currencies[i];
			currenciesMap[currency.code] = {};
			currenciesMap[currency.code].max = currency.maxValue;
			currenciesMap[currency.code].id = currency.id;
			currenciesMap[currency.code].step = currency.maxValue / 10;
			$scope.allCurrencies.push(currency.code);
		}
	});

	userProfileService.getAllCategories(function(categories) {
		$scope.allCategories.splice(0, $scope.allCategories.length);
		for (var i in categories) {
			$scope.allCategories.push(categories[i].title);
		}
		if(getRandBinary() === 1) {
			creditInit();
		} else {
			borrowInit();
		}
	});

	$scope.search = function() {
		console.log(JSON.stringify($scope.ordersSearchMap['orderCategories']))
		console.log($scope.selectedGivingCurrency + " " + $scope.givingValue)
		console.log($scope.selectedTakingCurrency + " " + $scope.takingValue)

		/*if (!$rootScope.user || ($rootScope.user.publicKey.indexOf("@") === -1)) {
		 authService.openAuthDialog(false, true, window.context + "#/orders");
		 } else {
		 window.location.href = window.context + "#/orders";
		 }*/
	};

	function creditInit() {
		$scope.takingValue = 1000;
		$scope.ordersSearchMap['orderCategories'].push($scope.allCategories[0]);
	}

	function borrowInit() {
		$scope.givingValue = 1000;
		$scope.ordersSearchMap['orderCategories'].push($scope.allCategories[0]);
	}

	function getRandBinary() {
		return Math.floor(Math.random() * 2);
	}
});


