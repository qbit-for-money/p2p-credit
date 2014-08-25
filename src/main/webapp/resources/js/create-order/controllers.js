var orderModule = angular.module("order");

orderModule.controller("CreateOrderController", function($scope, $rootScope, $timeout, userProfileService, usersProfileResource, $window, ordersResource) {
	var creditInitData = "&nbsp; I want to take credit..";
	var borrowInitData = " &nbsp; I want to invest..";
	var exchangeInitData = "&nbsp; I want to exchange..";
	$scope.allCategories = [];
	var categoriesMap = {};
	$scope.allCurrencies = [];
	$scope.allCurrenciesWithPercent = [];
	$scope.currency = {};
	$scope.currency.selectedGivingCurrency = "RUR";
	$scope.currency.selectedTakingCurrency = "RUR";
	$scope.takingValue = "10";
	$scope.givingValue = "";
	$scope.durationValue = "";
	$scope.durationTypes = ["Hours", "Days"];
	$scope.selectedDurationType = "Days";
	$scope.scEditor = {};
	$scope.scEditor.orderDataInitialized = false;
	$scope.isCreditInit = true;
	$scope.isBorrowInit = true;

	var currenciesMap = {};
	$scope.orderCreatingMap = {};
	$scope.orderCreatingMap['orderCategories'] = [];

	$scope.categorySelect2Options = {
		allowClear: true,
		tags: $scope.allCategories,
		multiple: true,
		'simple_tags': true,
		maximumSelectionSize: 20,
		maximumInputLength: 20,
		formatResult: formatResult,
		createSearchChoice: function(term, data) {
			if ($(data).filter(function() {
				return this.text.localeCompare(term) === 0;
			}).length === 0) {
				return {id: term, text: term};
			}
		}
	};

	function formatResult(item) {
		$timeout(function() {
			angular.element("span[type='CREDIT']").parent().addClass("credit-row");
			angular.element("span[type='BORROW']").parent().addClass("borrow-row");
			angular.element("span[type='EXCHANGE']").parent().addClass("exchange-row");
		});
		if (!item.id) {
			return item.text;
		}
		return "<span type='" + categoriesMap[item.text] + "'>" + item.text + "</span>";
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
	});
	loadCurrentUserLanguages();
	function loadCurrentUserLanguages() {
		var userProfileResponse = usersProfileResource.current({});
		userProfileResponse.$promise.then(function() {
			$scope.userLanguages = userProfileResponse.languages;
		});
	}
	function initDeadline() {
		var date = new Date();
		var newDate = new Date(new Date(date).setMonth(date.getMonth() + 3));
		$scope.deadline = newDate.toISOString().substring(0, 10);
	}

	function initCategories() {
		userProfileService.getAllCategories(function(categories) {
			for (var i in categories) {
				$scope.allCategories.push(categories[i].code);
				categoriesMap[categories[i].code] = categories[i].type;
			}
			$scope.creditInit();
		});
	}
initCategories();
	function initOrderckEditor() {
		if (!$scope.scEditor.orderDataInitialized) {
			CKEDITOR.disableAutoInline = true;
			CKEDITOR.inline('orderDataEditable', {
				on: {
					instanceReady: function(evt) {
						$scope.scEditor.orderDataInitialized = true;
						CKEDITOR.instances.orderDataEditable.setData("");
						$scope.validateDescription();
						initCategories();
					},
					change: function(evt) {
						$scope.isValidOrder();
						$scope.validateDescription();
					}
				}
			});
		}
	}

	//initOrderckEditor();

	$scope.isValidOrder = function() {

		//var data = CKEDITOR.instances.orderDataEditable.getData();
		var givingValue = angular.element("#giving-order-currency input").val();
		var takingValue = angular.element("#taking-order-currency input").val();
		var durationValue = angular.element("#order-duration input").val();
		if(($scope.currency.selectedGivingCurrency === "%") && (givingValue !== "") && (givingValue >= 0)) {
			setSuccess("#giving-order-currency input");
		} else if ((givingValue !== "") && (givingValue > 0)) {
			setSuccess("#giving-order-currency input");
		} else {
			setError("#giving-order-currency input");
		}
		if(($scope.currency.selectedTakingCurrency === "%") && (takingValue !== "") && (takingValue >= 0)) {
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
				&& $scope.orderCreatingMap['orderCategories'] && ($scope.orderCreatingMap['orderCategories'].length !== 0)
				&& $scope.deadline && ($scope.deadline !== "") && $scope.userLanguages && ($scope.userLanguages.length > 0)) {
			if((($scope.currency.selectedGivingCurrency !== "%") && (givingValue <= 0)) 
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

	$scope.increaseDescriptionSize = function() {
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
	};

	$scope.goBack = function() {
		$window.history.back();
	};

	$scope.creditInit = function() {
		$scope.currency.selectedGivingCurrency = "%";
		$scope.currency.selectedTakingCurrency = "RUR";
		$scope.takingValue = 1000;
		$scope.givingValue = 10;
		$scope.durationValue = 10;
		//CKEDITOR.instances.orderDataEditable.setData(creditInitData);
		$scope.orderCreatingMap['orderCategories'].splice(0, $scope.orderCreatingMap['orderCategories'].length);
		$scope.orderCreatingMap['orderCategories'].push($scope.allCategories[3]);
		$scope.isCreditInit = true;
		$scope.isBorrowInit = false;
		$timeout(function() {
			angular.element("#taking-order-currency input").focus();
			$scope.isValidOrder();
		});
	};

	$scope.borrowInit = function() {
		$scope.currency.selectedGivingCurrency = "BTC";
		$scope.currency.selectedTakingCurrency = "%";
		$scope.takingValue = 10;
		$scope.givingValue = 1000;
		$scope.durationValue = 10;
		//CKEDITOR.instances.orderDataEditable.setData(borrowInitData);
		$scope.orderCreatingMap['orderCategories'].splice(0, $scope.orderCreatingMap['orderCategories'].length);
		$scope.orderCreatingMap['orderCategories'].push($scope.allCategories[7]);
		$scope.isCreditInit = false;
		$scope.isBorrowInit = true;
		$timeout(function() {
			angular.element("#giving-order-currency input").focus();
			$scope.isValidOrder();
		});

	};

	$scope.exchangeInit = function() {
		$scope.currency.selectedGivingCurrency = "BTC";
		$scope.currency.selectedTakingCurrency = "RUR";
		$scope.takingValue = 2000;
		$scope.givingValue = 0.1;
		$scope.durationValue = 0;
		//CKEDITOR.instances.orderDataEditable.setData(exchangeInitData);
		$scope.orderCreatingMap['orderCategories'].splice(0, $scope.orderCreatingMap['orderCategories'].length);
		$scope.orderCreatingMap['orderCategories'].push($scope.allCategories[4]);
		$scope.isCreditInit = true;
		$scope.isBorrowInit = true;
		$timeout(function() {
			angular.element("#taking-order-currency input").focus();
			$scope.isValidOrder();
		});
	};

	$scope.createOrder = function() {
		if (!$scope.isValidOrder()) {
			return;
		}
		var orderInfo = {};
		//var data = CKEDITOR.instances.orderDataEditable.getData();
		var outcomingAmount = angular.element("#giving-order-currency input").val();
		var incomingAmount = angular.element("#taking-order-currency input").val();
		var durationValue = angular.element("#order-duration input").val();
		var incomingCurrency = $scope.currency.selectedTakingCurrency;
		var outcomingCurrency = $scope.currency.selectedGivingCurrency;

		//orderInfo.orderData = data;
		orderInfo.bookingDeadline = $scope.deadline;
		orderInfo.incomingCurrency = incomingCurrency;
		orderInfo.incomingAmount = incomingAmount;
		//addItems($scope.userPropertiesMap['currencies'], takingCurrency);
		orderInfo.outcomingCurrency = outcomingCurrency;
		orderInfo.outcomingAmount = outcomingAmount;
		orderInfo.languages = $scope.userLanguages;

		orderInfo.duration = durationValue;
		var durationType = $scope.selectedDurationType;
		if (durationType === "Hours") {
			orderInfo.durationType = "HOUR";
		} else if (durationType === "Days") {
			orderInfo.durationType = "DAY";
		}
		orderInfo.categories = [];
		for (var i in $scope.orderCreatingMap['orderCategories']) {
			orderInfo.categories[i] = {};
			orderInfo.categories[i].code = $scope.orderCreatingMap['orderCategories'][i];
			orderInfo.categories[i].type = categoriesMap[$scope.orderCreatingMap['orderCategories'][i]];
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


