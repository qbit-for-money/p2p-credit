var orderModule = angular.module("order");

orderModule.controller("OrdersController", function($scope, $rootScope, ordersResource, responsesResource, usersResource, userProfileService, usersProfileResource, userService, $interpolate, $compile, $modal, $timeout, chatService, navbarService) {
	$scope.isUserTable = false;
	$scope.ordersCount;
	$scope.userOrdersCount;
	var pageNumber = 0;
	var userPageNumber = 0;
	var loadOnScroll = false;
	var searchRequest = {};
	searchRequest.filterItems = [];
	$scope.emptyList = false;
	searchRequest.pageSize = 10;
	$scope.ordersSearchMap = {};
	$scope.allCategories = [];
	var currenciesMap = {};
	$scope.allCurrencies = [];
	$scope.allCurrenciesWithoutPersent = [];
	$scope.selectedCurrency;
	$scope.isBlocked = false;
	//$scope.givingValue = "";
	$scope.sortedBy = [/*{
	 id: 0,
	 name: "Проценты",
	 fieldTitle: "percent"
	 }, */{
			id: 0,
			name: "Сумма",
			fieldTitle: "amount"
		}, {
			id: 1,
			name: "Срок",
			fieldTitle: "duration"
		}];
	//incomingAmount outcomingAmount
	$scope.ordersOwn = [{
			id: 0,
			name: "Все"
		}, {
			id: 1,
			name: "Мои"
		}];

	$scope.isBond = false;

	/*function blockRequest() {
	 $scope.isBlocked = true;
	 }
	 
	 function unblockRequest() {
	 $scope.isBlocked = false;
	 }*/
	console.log($rootScope.searchRequest)
	$scope.selectOrdersOwn = function(ordersOwn) {
		if (!loadOnScroll || ($scope.allCurrencies.length === 0)) {
			return;
		}
		console.log("OWN")
		//loadOnScroll = false;
		//$scope.isBlocked = true;
		if (ordersOwn.item.id === 1) {
			$scope.isUserTable = true;
			//loadOnScroll = true;
			searchRequest.filterItems.splice(1, searchRequest.filterItems.length);
			userOrdersTableFilterInit();
		} else {
			$scope.isUserTable = false;
			//loadOnScroll = true;
			ordersTableFilterInit();
		}
	}

	$scope.selectType = function(selectedItem) {
		$scope.type = selectedItem;
		if ($scope.type === "borrow") {
			$scope.selectedGivingCurrency = "RUR";
			$scope.selectedTakingCurrency = "%";
		} else if ($scope.type === "credit") {
			$scope.selectedGivingCurrency = "%";
			$scope.selectedTakingCurrency = "RUR";
		} else {
			$scope.selectedGivingCurrency = "RUR";
			$scope.selectedTakingCurrency = "BTC";
		}
		loadOnScroll = true;
		console.log($scope.type)
	}
	/*$scope.editIsUserTable = function() {
	 //$(document).scrollTop();
	 //$('html').animate({scrollTop: 0},'slow');
	 window.scrollTo(0, 0);
	 if (!$scope.isUserTable) {
	 $scope.isUserTable = true;
	 userPageNumber = 0;
	 loadOnScroll = true;
	 } else {
	 $scope.isUserTable = false;
	 pageNumber = 0;
	 loadOnScroll = true;
	 }
	 };*/



	/*$scope.selectOrdersFilter = function(selectedItem) {
	 $scope.filterItem = selectedItem.item.fieldTitle;
	 };*/

	$scope.filterOrdersTable = function() {
		if (!loadOnScroll) {
			return;
		}

		ordersTableFilterInit();
		loadOnScroll = false;
		pageNumber = 0;
		userPageNumber = 0;
		/*if ($scope.filterItem === "categories" && $scope.ordersSearchMap.orderCategories && $scope.ordersSearchMap.orderCategories.length !== 0) {
		 var filterItem = {};
		 filterItem.filterOperator = "1";
		 filterItem.filterDataField = "categories";
		 filterItem.filterCondition = "IS_MEMBER";
		 var cateroiesStr = "";
		 for (var i in $scope.ordersSearchMap.orderCategories) {
		 cateroiesStr += $scope.ordersSearchMap.orderCategories[i] + ", ";
		 }
		 filterItem.filterValue = cateroiesStr;
		 searchRequest.filterItems[1] = filterItem;
		 } else if (($scope.filterItem === "incomingCurrency") || ($scope.filterItem === "outcomingCurrency")) {
		 var filterItem = {};
		 filterItem.filterOperator = "1";
		 filterItem.filterDataField = $scope.filterItem;
		 filterItem.filterCondition = "EQUAL";
		 filterItem.filterValue = $scope.selectedCurrency;
		 searchRequest.filterItems[1] = filterItem;
		 } else if (($scope.filterItem === "incomingAmount") || ($scope.filterItem === "outcomingAmount")) {
		 var filterItem = {};
		 filterItem.filterOperator = "1";
		 filterItem.filterDataField = $scope.filterItem;
		 filterItem.filterCondition = "GREATER_THAN";
		 var val = angular.element("#amount").val();
		 filterItem.filterValue = (val) ? val : 0;
		 searchRequest.filterItems[1] = filterItem;
		 } else {
		 searchRequest.filterItems.splice(1, searchRequest.filterItems.length);
		 }*/

		console.log(JSON.stringify(searchRequest))

		if (!$scope.isUserTable) {
			initOrdersTable();
		} else {
			initUserOrdersTable();
		}
	};

	function initFilter() {
		$scope.sortTable({item: {fieldTitle: "amount"}});
		if ($rootScope.searchRequest) {
			//$rootScope.searchRequest.duration = 10;
			console.log("> " + JSON.stringify($rootScope.searchRequest))
			$scope.selectType($rootScope.searchRequest.type);
			if($rootScope.searchRequest.givingValue) {
				$scope.givingValue = parseFloat($rootScope.searchRequest.givingValue);
			}
			if($rootScope.searchRequest.takingValue) {
				$scope.takingValue = parseFloat($rootScope.searchRequest.takingValue);
			}
			
			$scope.selectedGivingCurrency = $rootScope.searchRequest.selectedGivingCurrency;
			$scope.selectedTakingCurrency = $rootScope.searchRequest.selectedTakingCurrency;
			if($rootScope.searchRequest.duration) {
				$scope.durationValue = parseInt($rootScope.searchRequest.duration);
			}
			if($rootScope.searchRequest.isBond) {
				$scope.isBond = $rootScope.searchRequest.isBond;
			}
			
			
			//angular.element("#order-duration").find("input").val($rootScope.searchRequest.duration);
		} else {
			//$scope.givingValue = "1234";
			//$scope.takingValue = "24";
			$scope.selectType("borrow");

		}
		$timeout(function() {
			$scope.filterOrdersTable();
		}, 700);
		
	}

	$scope.sortTable = function(selectedItem) {
		console.log("SORT")
		loadOnScroll = true;
		/*if (!loadOnScroll) {
		 return;
		 }*/
		//loadOnScroll = false;
		// pageNumber = 0;
		//userPageNumber = 0;
		searchRequest.sortDataField = selectedItem.item.fieldTitle;
		searchRequest.sortOrder = "DESC";
		// if (!$scope.isUserTable) {
		//ordersTableFilterInit();
		// } else {
		//userOrdersTableFilterInit();
		// }
	};

	/*$scope.sortUserOrdersTable = function(selectedItem) {
	 searchRequest.sortDataField = selectedItem.item.fieldTitle;
	 searchRequest.sortOrder = "ASC";
	 userOrdersTableFilterInit();
	 };*/

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
		$scope.allCurrenciesDropdown = [];
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
			/*if (i !== 0) {
			 $scope.allCurrenciesDropdown[i - 1] = {};
			 $scope.allCurrenciesDropdown[i - 1].id = parseInt(i - 1);
			 $scope.allCurrenciesDropdown[i - 1].name = currencies[i].code;
			 }*/
		}
		$timeout(function() {
			initFilter();
		});
	});

	/*userProfileService.getAllCategories(function(categories) {
	 $scope.allCategories.splice(0, $scope.allCategories.length);
	 
	 for (var i in categories) {
	 $scope.allCategories.push(categories[i].code);
	 }
	 });*/

	/*$scope.selectCurrency = function(selectedItem) {
	 $scope.selectedCurrency = currenciesMap[selectedItem.item.name].id;
	 };*/

	$scope.selectOutcomingCurrency = function(item) {
		console.log("OUT: " + JSON.stringify(item))
	};

	$scope.selectIncomingCurrency = function(item) {
		console.log("IN: " + JSON.stringify(item))
	};

	function ordersTableFilterInit() {
		searchRequest.filterItems.splice(0, searchRequest.filterItems.length);
		//console.log(">> " + $scope.givingValue + " " + $scope.takingValue)
		console.log(">>" + angular.element("#giving-currency").find(".li-item").text())
		console.log(">>>" + angular.element("#taking-currency").find(".li-item").text())
		pageNumber = 0;
		//searchRequest.filterItems = [];
		var filterItem = {};
		filterItem.filterOperator = "1";
		filterItem.filterDataField = "status";
		filterItem.filterCondition = "EQUAL";
		filterItem.filterValue = "OPENED";
		searchRequest.filterItems[0] = filterItem;



		var bondFilterItem = {};
		bondFilterItem.filterOperator = "1";
		bondFilterItem.filterDataField = "bond";
		bondFilterItem.filterCondition = "EQUAL";
		bondFilterItem.filterValue = $scope.isBond;
		searchRequest.filterItems[1] = bondFilterItem;

		/*var borowFilterItem = {};
		 borowFilterItem.filterOperator = "1";
		 borowFilterItem.filterDataField = "incomingCurrency";
		 borowFilterItem.filterCondition = "EQUAL";
		 borowFilterItem.filterValue = "PERCENT";
		 
		 var creditFilterItem = {};
		 creditFilterItem.filterOperator = "1";
		 creditFilterItem.filterDataField = "outcomingCurrency";
		 creditFilterItem.filterCondition = "EQUAL";
		 creditFilterItem.filterValue = "PERCENT";*/

		var durationFilterItem = {};
		durationFilterItem.filterOperator = "1";
		durationFilterItem.filterDataField = "duration";
		var duration = angular.element("#order-duration").find("input").val();
		durationFilterItem.filterCondition = "GREATER_THAN_OR_EQUAL";
		durationFilterItem.filterValue = (duration !== "") ? duration : 0;

		searchRequest.filterItems[2] = durationFilterItem;

		var incomingCurrency = angular.element("#taking-currency").find(".li-item").text();
		if (incomingCurrency === "") {
			incomingCurrency = $scope.selectedTakingCurrency;
		}
		var outcomingCurrency = angular.element("#giving-currency").find(".li-item").text();
		if (outcomingCurrency === "") {
			outcomingCurrency = $scope.selectedGivingCurrency;
		}

		var incomingCurrencyFilterItem = {};
		incomingCurrencyFilterItem.filterOperator = "1";
		incomingCurrencyFilterItem.filterDataField = "incomingCurrency";
		incomingCurrencyFilterItem.filterCondition = "EQUAL";
		incomingCurrencyFilterItem.filterValue = incomingCurrency;

		var outcomingCurrencyFilterItem = {};
		outcomingCurrencyFilterItem.filterOperator = "1";
		outcomingCurrencyFilterItem.filterDataField = "outcomingCurrency";
		outcomingCurrencyFilterItem.filterCondition = "EQUAL";
		outcomingCurrencyFilterItem.filterValue = outcomingCurrency;

		searchRequest.filterItems[3] = incomingCurrencyFilterItem;
		searchRequest.filterItems[4] = outcomingCurrencyFilterItem;

		var incomingAmountFilterItem = {};
		incomingAmountFilterItem.filterOperator = "1";
		incomingAmountFilterItem.filterDataField = "incomingAmount";
		var incomingAmount = angular.element("#taking-currency").find("input").val();
		incomingAmountFilterItem.filterValue = (incomingAmount !== "") ? incomingAmount : 0;

		var outcomingAmountFilterItem = {};
		outcomingAmountFilterItem.filterOperator = "1";
		outcomingAmountFilterItem.filterDataField = "outcomingAmount";
		var outcomingAmount = angular.element("#giving-currency").find("input").val();
		outcomingAmountFilterItem.filterValue = (outcomingAmount !== "") ? outcomingAmount : 0;

		if (($scope.type === "borrow") || ($scope.type === "exchange")) {
			//incomingAmount = (incomingAmount && incomingAmount !== "") ? incomingAmount : 100;
			//outcomingAmount = (outcomingAmount && outcomingAmount !== "") ? outcomingAmount : 0;
			incomingAmountFilterItem.filterCondition = "LESS_THAN_OR_EQUAL";
			outcomingAmountFilterItem.filterCondition = "GREATER_THAN_OR_EQUAL";
			incomingAmountFilterItem.filterValue = incomingAmount;
			outcomingAmountFilterItem.filterValue = outcomingAmount;
		} else if ($scope.type === "credit") {
			incomingAmountFilterItem.filterCondition = "GREATER_THAN_OR_EQUAL";
			outcomingAmountFilterItem.filterCondition = "LESS_THAN_OR_EQUAL";
			incomingAmountFilterItem.filterValue = (incomingAmount !== "") ? incomingAmount : 0;
			outcomingAmountFilterItem.filterValue = (outcomingAmount !== "") ? outcomingAmount : 100;
		}

		console.log("TYPE: " + $scope.type)
		console.log("INCOMING: " + incomingAmount + " " + incomingCurrencyFilterItem.filterValue + " " + incomingAmountFilterItem.filterCondition)
		console.log("OUTCOMING: " + outcomingAmount + " " + outcomingCurrencyFilterItem.filterValue + " " + outcomingAmountFilterItem.filterCondition)
		if (incomingAmount && (incomingAmount !== "")) {
			searchRequest.filterItems.push(incomingAmountFilterItem);
		}
		if (outcomingAmount && (outcomingAmount !== "")) {
			searchRequest.filterItems.push(outcomingAmountFilterItem);
		}

		/*if ($scope.type === "borrow") {
		 searchRequest.filterItems[1] = outcomingCurrencyFilterItem;
		 
		 var outcomingCurrencyFilterItem = {};
		 outcomingCurrencyFilterItem.filterOperator = "1";
		 outcomingCurrencyFilterItem.filterDataField = "outcomingCurrency";
		 outcomingCurrencyFilterItem.filterCondition = "EQUAL";
		 outcomingCurrencyFilterItem.filterValue = $scope.selectedGivingCurrency;
		 } else if ($scope.type === "credit") {
		 searchRequest.filterItems[1] = creditFilterItem;
		 
		 var incomingCurrencyFilterItem = {};
		 incomingCurrencyFilterItem.filterOperator = "1";
		 incomingCurrencyFilterItem.filterDataField = "outcomingCurrency";
		 incomingCurrencyFilterItem.filterCondition = "EQUAL";
		 incomingCurrencyFilterItem.filterValue = $scope.selectedTakingCurrency;
		 
		 
		 //$scope.selectedGivingCurrency = "%";
		 //$scope.selectedTakingCurrency = "RUR";
		 //giving-currency
		 } else {
		 var notCreditFilterItem = creditFilterItem;
		 notCreditFilterItem.filterCondition = "NOT_EQUAL";
		 var notBorrowFilterItem = creditFilterItem;
		 notBorrowFilterItem.filterCondition = "NOT_EQUAL";
		 searchRequest.filterItems[1] = notCreditFilterItem;
		 searchRequest.filterItems[2] = notBorrowFilterItem;
		 }*/

		//initOrdersTable();
	}

	function userOrdersTableFilterInit() {
		//userPageNumber = 0;
		var userResponse = usersResource.current({});
		userResponse.$promise.then(function() {
			userPageNumber = 0;
			//searchRequest.filterItems = [];
			var filterItem = {};
			filterItem.filterOperator = "1";
			filterItem.filterDataField = "userId";
			filterItem.filterCondition = "EQUAL";
			filterItem.filterValue = userResponse.publicKey;
			searchRequest.filterItems[0] = filterItem;
			initUserOrdersTable();
		});
	}

	$scope.updateTables = function() {
		initUserOrdersTable();
		initOrdersTable();
	};

	$scope.approve = function(orderId, partnerId) {
		var response = {};
		response.orderId = orderId;
		response.partnerId = partnerId;
		var orderResponse = responsesResource.approveResponse({}, response);

		orderResponse.$promise.then(function() {
			$scope.updateTables();
		});
	};

	$scope.addResponse = function(orderId, comment) {
		var response = {};
		response.orderId = orderId;
		if (comment) {
			response.comment = comment;
		}
		var orderResponse = responsesResource.addResponse({}, response);

		orderResponse.$promise.then(function() {
			pageNumber = 0;
			$scope.updateTables();
		});
	};

	function isVisible(e, publicKey) {
		var id = angular.element(this).attr('id');
		var userProfileResponse = usersProfileResource.getShortById({'id': publicKey});
		userProfileResponse.$promise.then(function() {
			var name = (userProfileResponse.name) ? userProfileResponse.name : "Не указано";
			angular.element("#" + id + " #name span").text(name);
			var phone = (userProfileResponse.phone) ? userProfileResponse.phone : "Скрыт";
			angular.element("#" + id + " #phone span").text(phone);
			var mail = (userProfileResponse.mail) ? userProfileResponse.mail : "Скрыта";
			angular.element("#" + id + " #mail span").text(mail);
		});
	}

	function initUserOrdersTable(reload) {
		
		searchRequest.pageNumber = userPageNumber;
		if(reload) {
			userPageNumber += 1;
		}
		
		var orderResponse = ordersResource.search({}, searchRequest);

		orderResponse.$promise.then(function() {
			$scope.userOrdersCount = orderResponse.length;
			console.log("USER PAGE NUMBER: " + userPageNumber)
			var orders = formatDownloadedOrders(orderResponse.orderWrappers);
			var tabsdiv = angular.element("#user-orders-table");
			if (!reload) {
				tabsdiv.find("div").remove();
			}
			if (reload) {
				processUserOrdersTableData(orders, tabsdiv);
			} else {
				angular.element("#userOrdersTableTmpl").ready(function() {
					angular.element("#userOrderAttrsTmpl").ready(function() {
						$timeout(function() {
							processUserOrdersTableData(orders, tabsdiv);
						}, 500);
					});
				});
			}
		});
	}

	function processUserOrdersTableData(orders, tabsdiv) {
		var tableTemplate = angular.element("#userOrdersTableTmpl").text();
		var orderAttrsTemplate = angular.element("#userOrderAttrsTmpl").text();
		if ((!orders || (orders.length === 0)) && (userPageNumber === 0)) {
			$scope.emptyList = true;
			userPageNumber--;
		} else {
			$scope.emptyList = false;
		}

		for (var i in orders) {
			console.log(JSON.stringify(orders[i]))
			var orderId = orders[i].id;
			var orderAttrsContext = {
				orderId: orderId,
				categories: orders[i].categories,
				languages: orders[i].languages,
				take: orders[i].take,
				give: orders[i].give,
				endDate: orders[i].endDate,
				duration: orders[i].duration,
				orderStatus: orders[i].status,
				description: orders[i].description
			};
			var exp = $interpolate(orderAttrsTemplate);
			var orderAttrsContent = exp(orderAttrsContext);

			var liTemplate = "<div style='' id='" + orderId + "'>" + tableTemplate + "</div>";
			var responsesCount;
			if (orders[i].responses && (orders[i].responses.length !== 0)) {
				responsesCount = orders[i].responses.length;
			} else {
				responsesCount = 0;
			}
			var tabsContext = {
				orderId: orderId,
				freeDescription: "DESCRIPTION",
				status: (orders[i].partnerId) ? true : false,
				orderStatus: orders[i].status,
				responsesList: getResponsesContent(orders[i]),
				orderAttributes: orderAttrsContent,
				partnerId: orders[i].partnerId,
				type: orders[i].type,
				responsesCount: responsesCount
			};
			exp = $interpolate(liTemplate);
			var content = exp(tabsContext);
			var result = $compile(content)($scope);
			tabsdiv.append(result);

			for (var j in orders[i].responses) {
				var userId = orders[i].responses[j].userId;
				angular.element('#' + orders[i].responses[j].id).bind('isVisible', isVisible);
				angular.element('#' + orders[i].responses[j].id).show('slow', function() {
					angular.element(this).trigger('isVisible', [userId]);
				});
			}
		}
		$('#user-order-tab').tab();
		$timeout(function() {
			loadOnScroll = true;
		}, 200);
	}

	function getResponsesContent(order) {
		var responsesContent = "";
		for (var i in order.responses) {
			var publicKey = order.responses[i].userId;
			order.responses[i].imgurl = window.context + "webapi/photos/" + publicKey;
			order.responses[i].userurl = window.context + "#/users/" + publicKey;

			var responsesTemplate = angular.element("#responsesTmpl").text();
			var responsesExp = $interpolate(responsesTemplate);


			var responsesContext = {
				imgurl: order.responses[i].imgurl,
				userurl: order.responses[i].userurl,
				isComment: (!order.responses[i].comment) ? false : true,
				isAttributes: order.responses[i].comment ? false : true,
				comment: order.responses[i].comment,
				orderId: order.id,
				userId: publicKey,
				responseId: order.responses[i].id,
				approvedResponseId: order.partnerId,
				orderStatus: order.status,
				status: (order.partnerId) ? true : false,
				isApprovedResponse: (order.partnerId === order.responses[i].id) ? true : false
			};
			var responseContent = responsesExp(responsesContext);
			responsesContent += responseContent;
		}
		if (responsesContent === "") {
			responsesContent = '<div style="width: 100%;text-align: center; padding-top: 10px;" >Список пуст</div>';
		}
		return responsesContent;
	}

	function initOrdersTable(reload) {
		searchRequest.pageNumber = pageNumber;
		var orderResponse = ordersResource.search({}, searchRequest);
		orderResponse.$promise.then(function() {
			$scope.ordersCount = orderResponse.length;

			console.log("PAGE NUMBER: " + pageNumber)
			var orders = formatDownloadedOrders(orderResponse.orderWrappers);
			var tabsdiv = angular.element("#orders-table");
			if (!reload) {
				tabsdiv.find("div").remove();
			}

			if (reload) {
				processOrdersTableData(orders, tabsdiv);
			} else {
				angular.element("#ordersTableTmpl").ready(function() {
					angular.element("#orderAttrsTmpl").ready(function() {
						$timeout(function() {
							processOrdersTableData(orders, tabsdiv);
						}, 500);
					});
				});
			}
		});
	}

	function processOrdersTableData(orders, tabsdiv) {
		var tableTemplate = angular.element("#ordersTableTmpl").text();
		var orderAttrsTemplate = angular.element("#orderAttrsTmpl").text();
		if ((!orders || (orders.length === 0)) && (pageNumber === 0)) {
			$scope.emptyList = true;
			pageNumber--;
		} else {
			$scope.emptyList = false;
		}
		for (var i in orders) {

			var orderId = orders[i].id;
			console.log(orders[i].status)
			var imgurl = window.context + "webapi/photos/" + orders[i].userId;
			var userurl = window.context + "#/users/" + orders[i].userId;
			console.log(orders[i].creationDate);
			var orderAttrsContext = {
				orderId: orderId,
				imgurl: imgurl,
				userurl: userurl,
				userId: orders[i].userId,
				categories: orders[i].categories,
				languages: orders[i].languages,
				take: orders[i].take,
				give: orders[i].give,
				endDate: orders[i].endDate,
				duration: orders[i].duration,
				summaryRating: getSummaryRating(orders[i].statistics.opennessRating, orders[i].statistics.successOrdersCount),
				opennessRating: orders[i].statistics.opennessRating,
				ordersRating: orders[i].statistics.ordersRating,
				ordersCount: orders[i].statistics.ordersCount,
				successOrdersCount: orders[i].statistics.successOrdersCount,
				partnersRating: orders[i].statistics.partnersRating,
				creationDate: orders[i].creationDate,
				description: orders[i].description
			};
			var exp = $interpolate(orderAttrsTemplate);
			var orderAttrsContent = exp(orderAttrsContext);
			var liTemplate = "<div style='margin-top: -22px;' id='" + orderId + "'>" + tableTemplate + "</div>";
			var disabled = false;
			for (var j in orders[i].responses) {
				var respond = orders[i].responses[j];
				if (respond.userId === $rootScope.user.publicKey) {
					disabled = true;
				}
			}
			var tabsContext = {
				orderId: orderId,
				userId: orders[i].userId,
				freeDescription: "DESCRIPTION",
				status: (orders[i].partnerId) ? true : false,
				orderStatus: orders[i].status,
				orderAttributes: orderAttrsContent,
				isUserOrder: (orders[i].userId === userService.get().publicKey),
				type: orders[i].type,
				disabled: disabled
			};
			exp = $interpolate(liTemplate);
			var content = exp(tabsContext);
			var result = $compile(content)($scope);
			tabsdiv.append(result);
		}

		$('#order-tab').tab();

		for (var i in orders) {
			angular.element("#user-name-" + orders[i].id).text();
			angular.element('#user-name-' + orders[i].id).bind('isVisible', setUserAttrs);
			angular.element('#user-name-' + orders[i].id).show('slow', function() {
				angular.element(this).trigger('isVisible', [orders[i].userId]);
			});
		}

		$timeout(function() {
			loadOnScroll = true;
		}, 200);
	}

	function setUserAttrs(e, publicKey) {
		var id = angular.element(this).attr('id');
		var userProfileResponse = usersProfileResource.getShortById({'id': publicKey});
		userProfileResponse.$promise.then(function() {
			var name = (userProfileResponse.name) ? userProfileResponse.name : publicKey;
			angular.element("#" + id).text(name);
		});
	}

	function getSummaryRating(ratingOpenness, successOrdersCount) {
		var summaryRating = Math.floor(ratingOpenness * $rootScope.env.userRatingOpenessFactor
			+ successOrdersCount * $rootScope.env.userSuccessOrdersCountFactor);
		if (summaryRating === null) {
			summaryRating = 0;
		}
		return summaryRating;
	}

	$scope.openCommentDialog = function(orderId) {
		var modalInstance = $modal.open({
			controller: "CommentDialogController",
			templateUrl: "resources/html/order/comment-dialog.html",
			windowClass: "auth-dialog",
			backdrop: "none", keyboard: false, backdropClick: false, dialogFade: false,
			resolve: {
				addResponse: function() {
					return $scope.addResponse;
				},
				orderId: function() {
					return orderId;
				}
			}
		});
		modalInstance.result.then(function() {
		},
			function() {
			});
	};

	$scope.openChatDialog = function(userId) {
		chatService.openChatDialog($rootScope.user.publicKey, userId);
	};

	angular.element(window).scroll(function() {
		if (angular.element(window).scrollTop()
			>= (angular.element(document).height() - angular.element(window).height()) * 0.75) {
			console.log("USER: " + $scope.isUserTable)
			if ($scope.isUserTable) {
				loadUserData();
			} else {
				loadData();
			}
		}
	});

	function loadData() {
		console.log("LOAD DATA")

		if ((pageNumber > 30) || (($scope.ordersCount) && ($scope.ordersCount <= pageNumber * 10))) {
			return;
		}
		if (loadOnScroll === false) {
			return;
		}
		

		$timeout(function() {
			if (loadOnScroll === false) {
			return;
		}
			loadOnScroll = false;
			pageNumber += 1;
			initOrdersTable(true);
		}, 700);
	}

	function loadUserData() {
		console.log("LOAD USER DATA")

		if ((userPageNumber > 30) || (($scope.userOrdersCount) && ($scope.userOrdersCount <= userPageNumber * 10))) {
			return;
		}
		if (loadOnScroll === false) {
			return;
		}
		loadOnScroll = false;

		$timeout(function() {
			initUserOrdersTable(true);
		}, 700);
	}
	
	$scope.goToOrderCreating = function() {
		$rootScope.searchRequest = {};
		$rootScope.searchRequest.selectedGivingCurrency = angular.element("#giving-currency").find(".li-item").text();
		$rootScope.searchRequest.givingValue = angular.element("#giving-currency").find("input").val();
		$rootScope.searchRequest.selectedTakingCurrency = angular.element("#taking-currency").find(".li-item").text();
		$rootScope.searchRequest.takingValue = angular.element("#taking-currency").find("input").val();
		$rootScope.searchRequest.duration = angular.element("#order-duration").find("input").val();
		$rootScope.searchRequest.isBond = $scope.isBond;
		if($rootScope.searchRequest.selectedGivingCurrency === "%") {
			$rootScope.searchRequest.type = "credit";
		} else if($rootScope.searchRequest.selectedTakingCurrency === "%") {
			$rootScope.searchRequest.type = "borrow";
		} else {
			$rootScope.searchRequest.type = "exchange";
		}
		console.log(JSON.stringify($rootScope.searchRequest))
		navbarService.goToOrderCreating();
	};
	
	$scope.$on("$destroy", function() {
		console.log("DESTROY")
		angular.element(window).unbind('scroll');
	});
	
});

orderModule.controller("CommentDialogController", function($scope, addResponse, orderId, $modalInstance) {
	$scope.comment = "";
	$scope.addResponse = addResponse;
	$scope.orderId = orderId;

	$scope.save = function(comment) {
		addResponse(orderId, comment);
		$modalInstance.close();
	};

	$scope.cancel = function() {
		$modalInstance.dismiss('canceled');
	};
});




