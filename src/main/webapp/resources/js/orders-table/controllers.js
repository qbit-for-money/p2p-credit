var orderModule = angular.module("order");

orderModule.controller("OrdersController", function($scope, $rootScope, ordersResource, responsesResource, usersResource, userProfileService, usersProfileResource, userService, $interpolate, $compile, $modal, $timeout, chatService) {
	$scope.isUserTable = false;
	var ordersCount;
	var userOrdersCount;
	var pageNumber = 0;
	var userPageNumber = 0;
	var loadOnScroll = true;
	var searchRequest = {};
	searchRequest.filterItems = [];
	$scope.emptyList = false;
	searchRequest.pageSize = 10;
	$scope.ordersSearchMap = {};
	$scope.allCategories = [];
	var currenciesMap = {};
	$scope.allCurrencies = [];
	$scope.selectedCurrency;
	$scope.sortedBy = [{
			id: 0,
			name: "Продолжительность",
			fieldTitle: "duration"
		}, {
			id: 1,
			name: "Взять в валюте",
			fieldTitle: "incomingCurrency"
		}, {
			id: 2,
			name: "Дать в валюте",
			fieldTitle: "outcomingCurrency"
		}, {
			id: 3,
			name: "Взять в сумме",
			fieldTitle: "incomingAmount"
		}, {
			id: 4,
			name: "Дать в сумме",
			fieldTitle: "outcomingAmount"
		}];
	$scope.ordersOwn = [{
			id: 0,
			name: "Все"
		}, {
			id: 1,
			name: "Свои"
		}];

	$scope.filterBy = [/*{
			id: 0,
			name: "Нет",
			fieldTitle: "no"
		},*/ {
			id: 0,
			name: "Категории",
			fieldTitle: "categories"
		}, {
			id: 1,
			name: "Взять в валюте",
			fieldTitle: "incomingCurrency"
		}, {
			id: 2,
			name: "Дать в валюте",
			fieldTitle: "outcomingCurrency"
		}, {
			id: 3,
			name: "Взять в сумме",
			fieldTitle: "incomingAmount"
		}, {
			id: 4,
			name: "Дать в сумме",
			fieldTitle: "outcomingAmount"
		}];

	$scope.selectOrdersOwn = function(ordersOwn) {
		console.log(ordersOwn.item.id);
		if (ordersOwn.item.id === 1) {
			$scope.isUserTable = true;
			loadOnScroll = true;
			userOrdersTableFilterInit();
		} else {
			$scope.isUserTable = false;
			loadOnScroll = true;
			ordersTableFilterInit();
		}
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

	$scope.selectOrdersFilter = function(selectedItem) {
		$scope.filterItem = selectedItem.item.fieldTitle;
	};

	$scope.filterOrdersTable = function() {
		pageNumber = 0;
		userPageNumber = 0;
		if ($scope.filterItem === "categories" && $scope.ordersSearchMap.orderCategories && $scope.ordersSearchMap.orderCategories.length !== 0) {
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
		}
		
		if(!$scope.isUserTable) {
			initOrdersTable();
		} else {
			initUserOrdersTable();
		}
	};

	$scope.sortTable = function(selectedItem) {
		pageNumber = 0;
		userPageNumber = 0;
		searchRequest.sortDataField = selectedItem.item.fieldTitle;
		searchRequest.sortOrder = "DESC";
		if(!$scope.isUserTable) {
			ordersTableFilterInit();
		} else {
			userOrdersTableFilterInit();
		}
	};

	/*$scope.sortUserOrdersTable = function(selectedItem) {
		searchRequest.sortDataField = selectedItem.item.fieldTitle;
		searchRequest.sortOrder = "ASC";
		userOrdersTableFilterInit();
	};*/

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
		$scope.allCurrenciesDropdown = [];
		for (var i in currencies) {
			var currency = currencies[i];
			currenciesMap[currency.code] = {};
			currenciesMap[currency.code].max = currency.maxValue;
			currenciesMap[currency.code].id = currency.id;
			currenciesMap[currency.code].step = currency.maxValue / 10;
			$scope.allCurrencies.push(currency.code);
			if (i !== 0) {
				$scope.allCurrenciesDropdown[i - 1] = {};
				$scope.allCurrenciesDropdown[i - 1].id = parseInt(i - 1);
				$scope.allCurrenciesDropdown[i - 1].name = currencies[i].code;
			}
		}
	});

	userProfileService.getAllCategories(function(categories) {
		$scope.allCategories.splice(0, $scope.allCategories.length);

		for (var i in categories) {
			$scope.allCategories.push(categories[i].code);
		}
	});

	$scope.selectCurrency = function(selectedItem) {
		$scope.selectedCurrency = currenciesMap[selectedItem.item.name].id;
	};

	function ordersTableFilterInit() {
		pageNumber = 0;
		//searchRequest.filterItems = [];
		var filterItem = {};
		filterItem.filterOperator = "1";
		filterItem.filterDataField = "status";
		filterItem.filterCondition = "EQUAL";
		filterItem.filterValue = "OPENED";
		searchRequest.filterItems[0] = filterItem;
		initOrdersTable();
	}

	function userOrdersTableFilterInit() {
		userPageNumber = 0;
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
			$scope.updateTables();
		});
	};

	function isVisible(e, publicKey) {
		var id = angular.element(this).attr('id');
		var userProfileResponse = usersProfileResource.getShortById({'id': publicKey});
		userProfileResponse.$promise.then(function() {
			var name = (userProfileResponse.name) ? userProfileResponse.name : "Hidden";
			angular.element("#" + id + " #name span").text(name);
			var phone = (userProfileResponse.phone) ? userProfileResponse.phone : "Hidden";
			angular.element("#" + id + " #phone span").text(phone);
			var mail = (userProfileResponse.mail) ? userProfileResponse.mail : "Hidden";
			angular.element("#" + id + " #mail span").text(mail);
		});
	}
	/*userProfileService.getAllCategoriesTitle(function(categories) {
	 userProfileService.getAllLanguages(function(languages) {
	 userProfileService.getAllCurrencies(function(currencies) {
	 initUserOrdersTable(categories, languages, currencies);
	 });
	 });
	 });*/

	function initUserOrdersTable(reload) {
		searchRequest.pageNumber = userPageNumber;
		userPageNumber += 1;
		var orderResponse = ordersResource.search({}, searchRequest);

		orderResponse.$promise.then(function() {
			userOrdersCount = orderResponse.length;
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
		if (!orders || (orders.length === 0)) {
			$scope.emptyList = true;
		} else {
			$scope.emptyList = false;
		}
		for (var i in orders) {
			var orderId = orders[i].id;
			var orderAttrsContext = {
				orderId: orderId,
				categories: orders[i].categories,
				languages: orders[i].languages,
				take: orders[i].take,
				give: orders[i].give,
				endDate: orders[i].endDate,
				duration: orders[i].duration,
				orderStatus: orders[i].status
			};
			var exp = $interpolate(orderAttrsTemplate);
			var orderAttrsContent = exp(orderAttrsContext);

			var liTemplate = "<div style='margin-top: -22px;' id='" + orderId + "'>" + tableTemplate + "</div>";

			var tabsContext = {
				orderId: orderId,
				freeDescription: "DESCRIPTION",
				status: (orders[i].partnerId) ? true : false,
				orderStatus: orders[i].status,
				responsesList: getResponsesContent(orders[i]),
				orderAttributes: orderAttrsContent
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
					//openCommentDialog: openCommentDialog
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
		pageNumber += 1;
		var orderResponse = ordersResource.search({}, searchRequest);
		orderResponse.$promise.then(function() {
			ordersCount = orderResponse.length;

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
		if (!orders || (orders.length === 0)) {
			$scope.emptyList = true;
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
				userName: orders[i].userId,
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
				creationDate: orders[i].creationDate

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
		$timeout(function() {
			loadOnScroll = true;
		}, 200);
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

		if ((pageNumber > 30) || ((ordersCount) && (ordersCount <= pageNumber * 10))) {
			return;
		}
		if (loadOnScroll === false) {
			return;
		}
		loadOnScroll = false;

		$timeout(function() {

			initOrdersTable(true);


		}, 700);
	}

	function loadUserData() {
		console.log("LOAD USER DATA")

		if ((userPageNumber > 30) || ((userOrdersCount) && (userOrdersCount <= userPageNumber * 10))) {
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




