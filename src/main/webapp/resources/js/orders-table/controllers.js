var orderModule = angular.module("order");

orderModule.controller("OrdersController", function($scope, $rootScope, ordersResource, responsesResource, usersResource, userProfileService, usersProfileResource, userService, $interpolate, $compile, $modal, $timeout) {
	$scope.isUserTable = true;

	$scope.editIsUserTable = function() {
		if (!$scope.isUserTable) {
			$scope.isUserTable = true;
		} else {
			$scope.isUserTable = false;
		}
	};

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

	$('.sliding-panel').tabSlideOut({
		tabHandle: '.handle',
		pathToTabImage: 'resources/img/arrow_right.png',
		imageHeight: '92px',
		imageWidth: '80px',
		tabLocation: 'left',
		speed: 300,
		action: 'click',
		topPos: '100px',
		fixedPosition: true
	});

	initUserOrdersTable();

	var initRowDetails = function(index, parentElement, gridElement, datarecord) {
		var tabsdiv = null;
		tabsdiv = angular.element(angular.element(parentElement).children()[0]);
		if (tabsdiv !== null) {
			var imgurl = window.context + "webapi/profiles/" + datarecord.userPublicKey + "/photo";
			var userurl = window.context + "#/users/" + datarecord.userPublicKey;
			var template = angular.element("#ordersTableDetailTmpl").text();
			var exp = $interpolate(template);
			var disabled = false;
			for (var i in datarecord.responses) {
				if (datarecord.responses[i].userPublicKey === userService.get().publicKey) {
					disabled = true;
				}
			}

			var publicKey = datarecord.userPublicKey;
			var userProfileResponse = usersProfileResource.getShortById({'id': publicKey});
			userProfileResponse.$promise.then(function() {
				var userLanguages = "";
				if (userProfileResponse.languages) {
					for (var i in userProfileResponse.languages) {
						userLanguages += userProfileResponse.languages[i].title + ", ";
					}
					userLanguages = userLanguages.substring(0, userLanguages.length - 2);
				}
				var userCurrencies = "";
				if (userProfileResponse.currencies) {
					for (var i in userProfileResponse.currencies) {
						userCurrencies += userProfileResponse.currencies[i].code + ", ";
					}
					userCurrencies = userCurrencies.substring(0, userCurrencies.length - 2);
				}


				var context = {
					name: (userProfileResponse.name) ? userProfileResponse.name : "Hidden",
					imgurl: imgurl,
					languages: (userLanguages !== "") ? userLanguages : "Hidden",
					currencies: (userCurrencies !== "") ? userCurrencies : "Hidden",
					userurl: userurl,
					mail: (userProfileResponse.mail) ? userProfileResponse.mail : "Hidden",
					phone: (userProfileResponse.phone) ? userProfileResponse.phone : "Hidden",
					freeDescription: datarecord.orderData,
					orderId: datarecord.id,
					disabled: disabled,
					isUserOrder: (datarecord.userPublicKey === userService.get().publicKey)
				};
				var content = exp(context);
				var result = $compile(content)($scope);
				tabsdiv.append(result);
				//$scope.$apply();
				angular.element(tabsdiv).jqxTabs({width: "95%", height: 240});
			});


		}
	};




	function initUserOrdersTable() {
		var userResponse = usersResource.current({});
		userResponse.$promise.then(function() {
			var searchRequest = {};
			searchRequest.pageNumber = 0;
			searchRequest.pageSize = 10;
			searchRequest.filterItems = [];
			var filterItem = {};
			filterItem.filterOperator = "1";
			filterItem.filterDataField = "userId";
			filterItem.filterCondition = "EQUAL";
			filterItem.filterValue = userResponse.publicKey;
			searchRequest.filterItems[0] = filterItem;

			var orderResponse = ordersResource.search({}, searchRequest);

			orderResponse.$promise.then(function() {



				console.log("@@ " + JSON.stringify(userResponse));
				var orders = formatDownloadedOrders(orderResponse.orderWrappers);

				var tabsdiv = angular.element("#user-orders-table");
				tabsdiv.find("div").remove();

				for (var i in orders) {
					var tableTemplate = angular.element("#userOrdersTableTmpl").text();

					var orderAttrsTemplate = angular.element("#userOrderAttrsTmpl").text();
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

					var liTemplate = "<div style='margin-top: -21px;' id='" + orderId + "'>" + tableTemplate + "</div>";

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
			});
		});

		//var dataAdapter = new $.jqx.dataAdapter(getSource("webapi/orders/withFilter", "#orders-table"), getAdapterFields(allCurrencies));
		/*angular.element("#orders-table").jqxGrid(
		 {
		 theme: "bootstrap",
		 width: '100%',
		 source: dataAdapter,
		 pageable: true,
		 sortable: true,
		 showfilterrow: true,
		 filterable: true,
		 columnsresize: true,
		 columnsreorder: true,
		 virtualmode: true,
		 rendergridrows: function() {
		 return dataAdapter.records;
		 },
		 rowdetails: true,
		 rowdetailstemplate: {rowdetails: "<div style='margin: 10px;'></div>", rowdetailsheight: 300},
		 initrowdetails: initRowDetails,
		 columns: [
		 {text: "Categories", dataField: "categories", columntype: 'textbox', filtertype: 'checkedlist', filteritems: categories, filtercondition: 'starts_with', width: '140px', sortable: false, cellclassname: cellclassname},
		 {text: "Languages", dataField: "languages", columntype: 'textbox', filtertype: 'checkedlist', filteritems: languages, width: '150px', sortable: false, cellclassname: cellclassname},
		 {text: "Take", dataField: "takingCurrency", filtertype: 'list', filteritems: allCurrenciesCode, width: '85px', cellclassname: cellclassname},
		 {text: "Give", dataField: "givingCurrency", filtertype: 'list', filteritems: allCurrenciesCode, width: '85px', cellclassname: cellclassname},
		 {text: "Duration", dataField: "duration", filtertype: 'textbox', width: '80px', cellclassname: cellclassname},
		 {text: "Rating", dataField: "summaryRating", columntype: 'textbox', filtertype: 'textbox', sortable: false, width: '60px', cellclassname: cellclassname},
		 {text: "Openness rating", dataField: "opennessRating", columntype: 'textbox', filtertype: 'textbox', sortable: false, width: '120px', cellclassname: cellclassname},
		 {text: "Orders", dataField: "ordersSumValue", columntype: 'textbox', filtertype: 'textbox', sortable: false, cellclassname: cellclassname, width: '60px'},
		 {text: "Success value", dataField: "successValue", filtertype: 'textbox', sortable: false, cellclassname: cellclassname, width: '150px'},
		 {text: "Partners rating", dataField: "partnersRating", columntype: 'textbox', sortable: false, filtertype: 'textbox', width: '100px', cellclassname: cellclassname},
		 {text: "Booking deadline", dataField: "endDate", filtertype: 'date', width: '120px', cellclassname: cellclassname, cellsformat: 'd'}
		 ]
		 });*/
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
			responsesContent = "Empty list";
		}
		return responsesContent;
	}
	initOrdersTable();
	function initOrdersTable() {
		var searchRequest = {};
		searchRequest.pageNumber = 0;
		searchRequest.pageSize = 10;
		searchRequest.filterItems = [];
		var filterItem = {};
		filterItem.filterOperator = "1";
		filterItem.filterDataField = "status";
		filterItem.filterCondition = "EQUAL";
		filterItem.filterValue = "OPENED";
		searchRequest.filterItems[0] = filterItem;

		var orderResponse = ordersResource.search({}, searchRequest);
		orderResponse.$promise.then(function() {
			var orders = formatDownloadedOrders(orderResponse.orderWrappers);
			var tabsdiv = angular.element("#orders-table");
			tabsdiv.find("div").remove();

			for (var i in orders) {
				var tableTemplate = angular.element("#ordersTableTmpl").text();
				var orderAttrsTemplate = angular.element("#orderAttrsTmpl").text();
				var orderId = orders[i].id;
				var orderAttrsContext = {
					orderId: orderId,
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
					partnersRating: orders[i].statistics.partnersRating
				};
				var exp = $interpolate(orderAttrsTemplate);
				var orderAttrsContent = exp(orderAttrsContext);

				var liTemplate = "<div style='margin-top: -21px;' id='" + orderId + "'>" + tableTemplate + "</div>";
				var tabsContext = {
					orderId: orderId,
					freeDescription: "DESCRIPTION",
					status: (orders[i].partnerId) ? true : false,
					orderStatus: orders[i].status,
					orderAttributes: orderAttrsContent,
					isUserOrder: (orders[i].userId === userService.get().publicKey)
				};
				exp = $interpolate(liTemplate);
				var content = exp(tabsContext);
				var result = $compile(content)($scope);
				tabsdiv.append(result);
			}
			$('#order-tab').tab();
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




