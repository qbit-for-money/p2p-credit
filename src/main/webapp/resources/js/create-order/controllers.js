var orderModule = angular.module("order");

orderModule.controller("CreateOrderController", function($scope, $rootScope, $timeout, userProfileService, usersProfileResource, $window, ordersResource) {
	//var creditInitData = "&nbsp; I want to take credit..";
	//var borrowInitData = " &nbsp; I want to invest..";
	//var exchangeInitData = "&nbsp; I want to exchange..";
	//$scope.allCategories = [];
	//var categoriesMap = {};
	$scope.allCurrencies = [];
	$scope.allCurrenciesWithPercent = [];
	$scope.currency = {};
	//$scope.currency.selectedGivingCurrency = "RUR";
	//$scope.currency.selectedTakingCurrency = "BTC";
	$scope.takingValue = "";
	$scope.givingValue = "";
	$scope.durationValue = "";
	//$scope.durationTypes = ["Hours", "Days"];
	//$scope.selectedDurationType = "Days";
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
		//$scope.currency.selectedGivingCurrency = "%";
		//$scope.currency.selectedTakingCurrency = "RUR";
		//$scope.takingValue = 1000;
		//$scope.givingValue = 10;
		//$scope.durationValue = 10;
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
		//$scope.currency.selectedGivingCurrency = "BTC";
		//$scope.currency.selectedTakingCurrency = "%";
		//$scope.takingValue = 10;
		//$scope.givingValue = 1000;
		//$scope.durationValue = 10;
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
		//$scope.currency.selectedGivingCurrency = "BTC";
		//$scope.currency.selectedTakingCurrency = "RUR";
		//$scope.takingValue = 2000;
		//$scope.givingValue = 0.1;
		//$scope.durationValue = 0;
		$scope.isCreditInit = true;
		$scope.isBorrowInit = true;
		$timeout(function() {
			angular.element("#taking-order-currency input").focus();
			$scope.isValidOrder();
		});
	};



	function creationRequestInit() {
		/*$rootScope.searchRequest = {};
		$rootScope.searchRequest.selectedGivingCurrency = "RUR";
		$rootScope.searchRequest.givingValue = 23.5;
		$rootScope.searchRequest.selectedTakingCurrency = "WebMoney RUR";
		$rootScope.searchRequest.takingValue = 34;
		$rootScope.searchRequest.isBond = true;
		$rootScope.searchRequest.duration = 4;
		$rootScope.searchRequest.type = "exchange";*/
		if (!$rootScope.searchRequest) {
			$scope.currency.selectedGivingCurrency = "RUR";
			$scope.currency.selectedTakingCurrency = "BTC";
			$scope.creditInit();
			return;
		}
		console.log("REQUEST: " + JSON.stringify($rootScope.searchRequest))
		//$timeout(function() {
		//$scope.$apply(function() {
		$scope.currency.selectedGivingCurrency = $rootScope.searchRequest.selectedTakingCurrency;
		$scope.currency.selectedTakingCurrency = $rootScope.searchRequest.selectedGivingCurrency;
		//angular.element("#giving-currency").find(".li-item").text($scope.currency.selectedGivingCurrency)
		//angular.element("#taking-currency").find(".li-item").text($scope.currency.selectedTakingCurrency)
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
		//$scope.creditInit();
	});
	/*loadCurrentUserLanguages();
	 function loadCurrentUserLanguages() {
	 var userProfileResponse = usersProfileResource.current({});
	 userProfileResponse.$promise.then(function() {
	 $scope.userLanguages = userProfileResponse.languages;
	 });
	 }*/
	function initDeadline() {
		var date = new Date();
		var newDate = new Date(new Date(date).setDate(date.getDate() + 10));
		$scope.deadline = newDate.toISOString().substring(0, 10);
	}

	/*function initCategories() {
	 userProfileService.getAllCategories(function(categories) {
	 for (var i in categories) {
	 $scope.allCategories.push(categories[i].code);
	 categoriesMap[categories[i].code] = categories[i].type;
	 }
	 $scope.creditInit();
	 });
	 }
	 initCategories();*/

	$scope.isValidOrder = function() {

		//var data = CKEDITOR.instances.orderDataEditable.getData();
		var givingValue = angular.element("#giving-order-currency input").val();
		var takingValue = angular.element("#taking-order-currency input").val();
		var durationValue = angular.element("#order-duration input").val();
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

	/*$scope.increaseDescriptionSize = function() {
	 if (CKEDITOR.instances.orderDataEditable.getData() === "") {
	 angular.element("#orderDataEditable").css("min-height", "120px");
	 }
	 };
	 $scope.decreaseDescriptionSize = function() {
	 if (CKEDITOR.instances.orderDataEditable.getData() === "") {
	 angular.element("#orderDataEditable").css("min-height", "30px");
	 }
	 };
	 
	 $scope.validateDescription = function() {
	 if (CKEDITOR.instances.orderDataEditable.getData() !== "") {
	 angular.element("#orderDataEditable").removeClass("sc-has-error");
	 angular.element("#orderDataEditable").addClass("sc-has-success");
	 } else {
	 angular.element("#orderDataEditable").removeClass("sc-has-success");
	 angular.element("#orderDataEditable").addClass("sc-has-error");
	 }
	 };*/

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
//console.log($scope.isBond)
		//orderInfo.orderData = data;
		orderInfo.bookingDeadline = $scope.deadline;
		orderInfo.incomingCurrency = incomingCurrency;
		orderInfo.incomingAmount = incomingAmount;
		//addItems($scope.userPropertiesMap['currencies'], takingCurrency);
		orderInfo.outcomingCurrency = outcomingCurrency;
		orderInfo.outcomingAmount = outcomingAmount;
		//orderInfo.languages = $scope.userLanguages;

		orderInfo.duration = durationValue;
		orderInfo.durationType = "DAY";
		orderInfo.bond = $scope.isBond;
		orderInfo.description = angular.element("#description").val();
		
		$rootScope.searchRequest = {};
		//$rootScope.searchRequest.orderCategories = $scope.ordersSearchMap['orderCategories'];
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
		/*var durationType = $scope.selectedDurationType;
		 if (durationType === "Hours") {
		 orderInfo.durationType = "HOUR";
		 } else if (durationType === "Days") {
		 orderInfo.durationType = "DAY";
		 }*/
		/*orderInfo.categories = [];
		 for (var i in $scope.orderCreatingMap['orderCategories']) {
		 orderInfo.categories[i] = {};
		 orderInfo.categories[i].code = $scope.orderCreatingMap['orderCategories'][i];
		 orderInfo.categories[i].type = categoriesMap[$scope.orderCreatingMap['orderCategories'][i]];
		 }*/

		console.log(JSON.stringify(orderInfo))
		console.log("** " + JSON.stringify($rootScope.searchRequest))

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


