var orderModule = angular.module("order");

orderModule.controller("CreateOrderController", function($scope, $rootScope, $timeout, userProfileService, $window, ordersResource) {
	$scope.allCategories = [];
	$scope.allCurrencies = [];
	$scope.allCurrenciesWithPercent = [];
	$scope.selectedGivingCurrency = "RUR";
	$scope.selectedTakingCurrency = "RUR";
	$scope.takingValue = "";
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
		createSearchChoice: function(term, data) {
			if ($(data).filter(function() {
				return this.text.localeCompare(term) === 0;
			}).length === 0) {
				return {id: term, text: term};
			}
		}
	};

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
		$scope.allCurrenciesWithPercent.unshift("%");
	});

	userProfileService.getAllCategories(function(categories) {
		for (var i in categories) {
			$scope.allCategories.push(categories[i].title);
		}
		$scope.creditInit();
	});

	function initDeadline() {
		var date = new Date();
		var newDate = new Date(new Date(date).setMonth(date.getMonth() + 3));
		$scope.deadline = newDate.toISOString().substring(0, 10);
	}

	function initOrderckEditor() {
		if (!$scope.scEditor.orderDataInitialized) {
			CKEDITOR.disableAutoInline = true;
			CKEDITOR.inline('orderDataEditable', {
				on: {
					instanceReady: function(evt) {
						$scope.scEditor.orderDataInitialized = true;
						CKEDITOR.instances.orderDataEditable.setData("");
						$scope.validateDescription();
					},
					change: function(evt) {
						$scope.isValidOrder();
						$scope.validateDescription();
					}
				}
			});
		}
	}

	initOrderckEditor();

	$scope.isValidOrder = function() {

		var data = CKEDITOR.instances.orderDataEditable.getData();

		var givingValue = angular.element("#giving-order-currency input").val();
		var takingValue = angular.element("#taking-order-currency input").val();
		var durationValue = angular.element("#order-duration input").val();
		if (givingValue !== "") {
			setSuccess("#giving-order-currency input");
		} else {
			setError("#giving-order-currency input");
		}
		if (takingValue !== "") {
			setSuccess("#taking-order-currency input");
		} else {
			setError("#taking-order-currency input");
		}
		if (durationValue !== "") {
			setSuccess("#order-duration input");
		} else {
			setError("#order-duration input");
		}
		console.log($scope.deadline)
		if (takingValue && (takingValue !== "")
				&& givingValue && (givingValue !== "")
				&& durationValue && (durationValue !== "")
				&& data && (data !== "")
				&& $scope.orderCreatingMap['orderCategories'] && ($scope.orderCreatingMap['orderCategories'].length !== 0)
				&& $scope.deadline && ($scope.deadline !== "")) {
			$timeout(function() {
				$scope.$apply(function() {
					$scope.createOrderButtonEnabled = true;
				});
			});

		} else {
			$timeout(function() {
				$scope.$apply(function() {
					$scope.createOrderButtonEnabled = false;
				});
			});
		}
		return $scope.createOrderButtonEnabled;
	};

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
		$scope.selectedGivingCurrency = "%";
		$scope.selectedTakingCurrency = "RUR";
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
		$scope.selectedGivingCurrency = "RUR";
		$scope.selectedTakingCurrency = "%";
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
		$scope.selectedGivingCurrency = "BTC";
		$scope.selectedTakingCurrency = "RUR";
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
		var data = CKEDITOR.instances.orderDataEditable.getData();
		var givingValue = angular.element("#giving-order-currency input").val();
		var takingValue = angular.element("#taking-order-currency input").val();
		var durationValue = angular.element("#order-duration input").val();
		var orderInfo = {};
		orderInfo.orderData = data;
		orderInfo.endDate = $scope.deadline;
		orderInfo.takingCurrency = currenciesMap[$scope.selectedTakingCurrency];
		orderInfo.takingValue = takingValue;
		//addItems($scope.userPropertiesMap['currencies'], takingCurrency);

		orderInfo.givingCurrency = currenciesMap[$scope.selectedGivingCurrency];
		orderInfo.givingValue = givingValue;
		//addItems($scope.userPropertiesMap['currencies'], givingCurrency);

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
			orderInfo.categories[i].title = $scope.orderCreatingMap['orderCategories'][i];
		}

		var orderResponse = ordersResource.create({}, orderInfo);
		orderResponse.$promise.then(function() {
			console.log(JSON.stringify(orderResponse))
		});
	};
});


