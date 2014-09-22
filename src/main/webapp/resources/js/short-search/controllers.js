var navbarModule = angular.module("navbar");

navbarModule.controller("ShortSearchController", function($scope, $rootScope, userProfileService, getRandBinary, authService) {
	$scope.allCategories = [];
	$scope.allCurrencies = [];
	$scope.allCurrenciesWithoutPersent = [];
	$scope.selectedGivingCurrency = "RUR";
	$scope.selectedTakingCurrency = "%";
	$scope.takingValue = "";
	$scope.givingValue = "";
	$scope.type = "Дают";

	$scope.types = [{
			id: 0,
			name: "Дают"
		}, {
			id: 1,
			name: "Берут"
		}, {
			id: 2,
			name: "Меняют"
		}];

	$scope.selectType = function(selectedItem) {
		$scope.type = selectedItem.item.name;
		if ($scope.type === "Дают") {
			$scope.selectedGivingCurrency = "RUR";
			$scope.selectedTakingCurrency = "%";
		} else if($scope.type === "Берут") {
			$scope.selectedGivingCurrency = "%";
			$scope.selectedTakingCurrency = "RUR";
		} else {
			$scope.selectedGivingCurrency = "RUR";
			$scope.selectedTakingCurrency = "BTC";
		}
		console.log($scope.type)
	}

	var currenciesMap = {};
	$scope.ordersSearchMap = {};
	$scope.ordersSearchMap['orderCategories'] = [];
	$scope.ordersSearchMap['orderTakingValue'];

	/*$scope.categorySelect2Options = {
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
	};*/

	userProfileService.getAllCurrencies(function(currencies) {
		for (var i in currencies) {
			var currency = currencies[i];
			currenciesMap[currency.code] = {};
			currenciesMap[currency.code].max = currency.maxValue;
			currenciesMap[currency.code].id = currency.id;
			currenciesMap[currency.code].step = currency.maxValue / 10;
			$scope.allCurrencies.push(currency.code);
			console.log(currency.id)
			if (currency.id !== "PERCENT") {
				$scope.allCurrenciesWithoutPersent.push(currency.code);
			}
		}
	});

	/*userProfileService.getAllCategories(function(categories) {
	 $scope.allCategories.splice(0, $scope.allCategories.length);
	 for (var i in categories) {
	 $scope.allCategories.push(categories[i].code);
	 }
	 if (getRandBinary === 1) {
	 creditInit();
	 } else {
	 borrowInit();
	 }
	 });*/

	$scope.search = function() {
		//console.log("->>" + angular.element("#giving-currency").find(".li-item").text())
		//console.log("->>>" + angular.element("#taking-currency").find(".li-item").text())
		//console.log("%% " + angular.element("#giving-currency").find("input").val() + " " + angular.element("#taking-currency").find("input").val())
		//console.log(JSON.stringify($scope.ordersSearchMap['orderCategories']))
		console.log($scope.selectedGivingCurrency + " " + $scope.givingValue)
		console.log($scope.selectedTakingCurrency + " " + $scope.takingValue)
		$rootScope.searchRequest = {};
		//$rootScope.searchRequest.orderCategories = $scope.ordersSearchMap['orderCategories'];
		$rootScope.searchRequest.selectedGivingCurrency = angular.element("#giving-currency").find(".li-item").text();
		$rootScope.searchRequest.givingValue = angular.element("#giving-currency").find("input").val();
		$rootScope.searchRequest.selectedTakingCurrency = angular.element("#taking-currency").find(".li-item").text();
		$rootScope.searchRequest.takingValue = angular.element("#taking-currency").find("input").val();
		if($rootScope.searchRequest.selectedGivingCurrency === "%") {
			$rootScope.searchRequest.type = "credit";
		} else if($rootScope.searchRequest.selectedTakingCurrency === "%") {
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
	
	$scope.isValidRequest = function() {

		var givingValue = angular.element("#giving-order-currency input").val();
		var takingValue = angular.element("#taking-order-currency input").val();
		if (($scope.currency.selectedGivingCurrency === "%") && (givingValue !== "") && (givingValue >= 0)) {
			setSuccess("#giving-order-currency input");
		} else if ((givingValue !== "") && (givingValue > 0)) {
			setSuccess("#giving-order-currency input");
		} else {
			setError("#giving-order-currency input");
		}
		if (($scope.currency.selectedTakingCurrency === "%") && (takingValue !== "") && (takingValue >= 0)) {
			setSuccess("#giving-order-currency input");
		} else if ((takingValue !== "") && (takingValue > 0)) {
			setSuccess("#taking-order-currency input");
		} else {
			setError("#taking-order-currency input");
		}
		if ((durationValue !== "") && (durationValue >= 0)) {
			setSuccess("#order-duration input");
		} else {
			setError("#order-duration input");
		}
		if (takingValue && (takingValue !== "") && isNumber(takingValue)
			&& givingValue && (givingValue !== "") && isNumber(givingValue)
			&& durationValue && (durationValue !== "")
			//&& data && (data !== "")
			//&& $scope.orderCreatingMap['orderCategories'] && ($scope.orderCreatingMap['orderCategories'].length !== 0)
			&& $scope.deadline && ($scope.deadline !== "")) {
			if ((($scope.currency.selectedGivingCurrency !== "%") && (givingValue <= 0))
				|| (($scope.currency.selectedTakingCurrency !== "%") && (takingValue <= 0))) {
				disableCreateOrderButton();
			} else {
				enableCreateOrderButton();
			}


		} else {
			disableCreateOrderButton();
		}
		return $scope.createOrderButtonEnabled;
	};

	/*function creditInit() {
	 $scope.takingValue = 1000;
	 $scope.ordersSearchMap['orderCategories'].push($scope.allCategories[0]);
	 }
	 
	 function borrowInit() {
	 $scope.givingValue = 1000;
	 $scope.ordersSearchMap['orderCategories'].push($scope.allCategories[0]);
	 }*/
});


