var orderModule = angular.module("order");

orderModule.controller("CreateOrderController", function($scope, $rootScope, $timeout, userProfileService, usersProfileResource, $window, ordersResource) {
	$scope.allCurrencies = [];
	$scope.allCurrenciesWithPercent = [];
	$scope.currency = {};
	$scope.takingValue = "";
	$scope.givingValue = "";
	$scope.durationValue = "";
	$scope.isCreditInit = true;
	$scope.isBorrowInit = true;
	$scope.isBond = false;

	var currenciesMap = {};
	$scope.orderCreatingMap = {};
	$scope.orderCreatingMap['orderCategories'] = [];

	$scope.creditInit = function(empty) {
		if(!empty) {
			$scope.currency.selectedGivingCurrency = "%";
			$scope.currency.selectedTakingCurrency = "RUR";
		} 
		$scope.isCreditInit = true;
		$scope.isBorrowInit = false;
		$timeout(function() {
			angular.element("#taking-order-currency input").focus();
			$scope.isValidOrder();
		});
	};

	$scope.borrowInit = function(empty) {
		if(!empty) {
			$scope.currency.selectedGivingCurrency = "BTC";
			$scope.currency.selectedTakingCurrency = "%";
		} 
		$scope.isCreditInit = false;
		$scope.isBorrowInit = true;
		$timeout(function() {
			angular.element("#giving-order-currency input").focus();
			$scope.isValidOrder();
		});

	};

	$scope.exchangeInit = function(empty) {
		if(!empty) {
			$scope.currency.selectedGivingCurrency = "BTC";
			$scope.currency.selectedTakingCurrency = "RUR";
		} 
		$scope.isCreditInit = true;
		$scope.isBorrowInit = true;
		$timeout(function() {
			angular.element("#taking-order-currency input").focus();
			$scope.isValidOrder();
		});
	};



	function creationRequestInit() {
		if (!$rootScope.searchRequest) {
			$scope.currency.selectedGivingCurrency = "RUR";
			$scope.currency.selectedTakingCurrency = "BTC";
			$scope.creditInit();
			return;
		}
		$scope.currency.selectedGivingCurrency = $rootScope.searchRequest.selectedTakingCurrency;
		$scope.currency.selectedTakingCurrency = $rootScope.searchRequest.selectedGivingCurrency;
		if($rootScope.searchRequest.givingValue) {
			$scope.takingValue = parseFloat($rootScope.searchRequest.givingValue);
		}
		if($rootScope.searchRequest.takingValue) {
			$scope.givingValue = parseFloat($rootScope.searchRequest.takingValue);
		}
		if($rootScope.searchRequest.duration) {
			$scope.durationValue = parseInt($rootScope.searchRequest.duration);
		}
		
		$scope.isBond = $rootScope.searchRequest.isBond;
		if ($rootScope.searchRequest.type === "credit") {
			$scope.borrowInit(true);
		} else if ($rootScope.searchRequest.type === "borrow") {
			$scope.creditInit(true);
		} else {
			$scope.exchangeInit(true);
		}
		$rootScope.searchRequest = undefined;
	}

	initDeadline();

	userProfileService.getAllCurrencies(function(currencies) {
		for (var i in currencies) {
			var currency = currencies[i];
			currenciesMap[currency.code] = {};
			currenciesMap[currency.code].max = currency.maxValue;
			currenciesMap[currency.code].id = currency.id;
			currenciesMap[currency.code].step = currency.maxValue / 10;
			$scope.allCurrencies.push(currency.code);
			$scope.allCurrenciesWithPercent.push(currency.code);
		}
		$scope.allCurrencies.splice(0, 1);
		creationRequestInit();
	});
	function initDeadline() {
		var date = new Date();
		var newDate = new Date(new Date(date).setDate(date.getDate() + 10));
		$scope.deadline = newDate.toISOString().substring(0, 10);
	}

	$scope.isValidOrder = function() {
		var givingValue = angular.element("#giving-order-currency input").val();
		var takingValue = angular.element("#taking-order-currency input").val();
		var durationValue = angular.element("#order-duration input").val();
		if (($scope.currency.selectedGivingCurrency === "%") && (givingValue !== "") && (givingValue >= 0)) {
			if(givingValue < 100) {
				setSuccess("#giving-order-currency input");
			} else {
				setError("#giving-order-currency input");
			}
		} else if ((givingValue !== "") && (givingValue > 0) && (givingValue <= 10000)) {
			setSuccess("#giving-order-currency input");
		} else {
			setError("#giving-order-currency input");
		}
		if (($scope.currency.selectedTakingCurrency === "%") && (takingValue !== "") && (takingValue >= 0)) {
			if(takingValue < 100) {
				setSuccess("#taking-order-currency input");
			} else {
				setError("#taking-order-currency input");
			}
		} else if ((takingValue !== "") && (takingValue > 0) && (takingValue <= 10000)) {
			setSuccess("#taking-order-currency input");
		} else {
			setError("#taking-order-currency input");
		}
		if ((durationValue !== "") && (durationValue >= 0) && (durationValue < 365)) {
			setSuccess("#order-duration input");
		} else {
			setError("#order-duration input");
		}
		if (takingValue && (takingValue !== "") && isNumber(takingValue)
			&& givingValue && (givingValue !== "") && isNumber(givingValue)
			&& durationValue && (durationValue !== "")
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

	function enableCreateOrderButton() {
		$timeout(function() {
			$scope.$apply(function() {
				$scope.createOrderButtonEnabled = true;
			});
		});
	}

	function disableCreateOrderButton() {
		$timeout(function() {
			$scope.$apply(function() {
				$scope.createOrderButtonEnabled = false;
			});
		});
	}

	function setError(id) {
		angular.element(id).removeClass("sc-has-success");
		angular.element(id).addClass("sc-has-error");
	}
	function setSuccess(id) {
		angular.element(id).removeClass("sc-has-error");
		angular.element(id).addClass("sc-has-success");
	}

	$scope.goBack = function() {
		$window.history.back();
	};

	$scope.createOrder = function() {
		if (!$scope.isValidOrder()) {
			return;
		}
		var orderInfo = {};
		var outcomingAmount = parseFloat(angular.element("#giving-order-currency input").val());
		var incomingAmount = parseFloat(angular.element("#taking-order-currency input").val());
		var durationValue = parseInt(angular.element("#order-duration input").val());
		var incomingCurrency = $scope.currency.selectedTakingCurrency;
		var outcomingCurrency = $scope.currency.selectedGivingCurrency;
		orderInfo.bookingDeadline = $scope.deadline;
		orderInfo.incomingCurrency = incomingCurrency;
		orderInfo.incomingAmount = incomingAmount;
		orderInfo.outcomingCurrency = outcomingCurrency;
		orderInfo.outcomingAmount = outcomingAmount;

		orderInfo.duration = durationValue;
		orderInfo.durationType = "DAY";
		orderInfo.bond = $scope.isBond;
		orderInfo.description = angular.element("#description").val();
		
		$rootScope.searchRequest = {};
		$rootScope.searchRequest.selectedGivingCurrency = outcomingCurrency;
		$rootScope.searchRequest.givingValue = outcomingAmount;
		$rootScope.searchRequest.selectedTakingCurrency = incomingCurrency;
		$rootScope.searchRequest.takingValue = incomingAmount;
		$rootScope.searchRequest.duration = durationValue;
		$rootScope.searchRequest.isBond = $scope.isBond;
		if(outcomingCurrency === "%") {
			$rootScope.searchRequest.type = "credit";
		} else if(incomingCurrency === "%") {
			$rootScope.searchRequest.type = "borrow";
		} else {
			$rootScope.searchRequest.type = "exchange";
		}

		var orderResponse = ordersResource.create({}, orderInfo);
		orderResponse.$promise.then(function() {
			$rootScope.createdOrderId = orderResponse.id;
			window.location.href = window.context + "#/orders";
		});
	};

	function isNumber(n) {
		return !isNaN(parseFloat(n)) && isFinite(n);
	}
});


