var orderModule = angular.module("order");

orderModule.controller("UserOrdersController", function($scope, $rootScope, $interpolate, $timeout, $compile, ordersResource, $modal, userProfileService, usersProfileResource) {

	$scope.approve = function(orderId, userId) {
		var response = {};
		response.orderId = orderId;
		response.userId = userId;
		var orderResponse = ordersResource.approveResponse({}, response);

		orderResponse.$promise.then(function() {
			angular.element('#user-orders-table').jqxGrid('updatebounddata');
			angular.element('#orders-table').jqxGrid('updatebounddata');
		});
	};

	userProfileService.getAllCategories(function(categories) {
		userProfileService.getAllLanguages(function(languages) {
			userProfileService.getAllCurrencies(function(currencies) {
				initTable(categories, languages, currencies);
			});
		});
	});


	var initRowDetails = function(index, parentElement, gridElement, datarecord) {
		var tabsdiv = null;
		tabsdiv = angular.element(angular.element(parentElement).children()[0]);
		if (tabsdiv !== null) {
			var responsesContent = "";
			for (var i in datarecord.responses) {
				var publicKey = datarecord.responses[i].userPublicKey;
				datarecord.responses[i].imgurl = window.context + "webapi/profiles/" + publicKey + "/photo";
				datarecord.responses[i].userurl = window.context + "#/users/" + publicKey;

				var responsesTemplate = angular.element("#responsesTmpl").text();
				var responsesExp = $interpolate(responsesTemplate);


				var responsesContext = {
					imgurl: datarecord.responses[i].imgurl,
					userurl: datarecord.responses[i].userurl,
					isComment: (!datarecord.responses[i].comment) ? false : true,
					isAttributes: datarecord.responses[i].comment ? false : true,
					comment: datarecord.responses[i].comment,
					orderId: datarecord.id,
					userId: publicKey,
					responseId: datarecord.responses[i].id,
					approvedResponseId: datarecord.approvedResponseId,
					orderStatus: datarecord.status,
					status: (datarecord.approvedResponseId) ? true : false,
					isApprovedResponse: (datarecord.approvedResponseId === datarecord.responses[i].id) ? true : false
							//openCommentDialog: openCommentDialog
				};
				var responseContent = responsesExp(responsesContext);
				responsesContent += responseContent;
			}
			if (responsesContent === "") {
				responsesContent = "Empty list";
			}
			var template = angular.element("#userOrdersTableDetailTmpl").text();
			var exp = $interpolate(template);
			var context = {
				freeDescription: datarecord.orderData,
				approvedResponseId: datarecord.approvedResponseId,
				orderId: datarecord.id,
				status: (datarecord.approvedResponseId) ? true : false,
				orderStatus: datarecord.status
			};
			var content = exp(context);
			content = content.replace('#responses-list', responsesContent);
			var result = $compile(content)($scope);
			tabsdiv.append(result);
			$scope.$apply();
			angular.element(tabsdiv).jqxTabs({width: "95%", height: 240});

			for (var i in datarecord.responses) {
				var publicKey = datarecord.responses[i].userPublicKey;
				angular.element('#' + datarecord.responses[i].id).bind('isVisible', isVisible);
				angular.element('#' + datarecord.responses[i].id).show('slow', function() {
					angular.element(this).trigger('isVisible', [publicKey]);
				});
			}
		}
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

	var ordersTable = angular.element('#user-orders-table');
	$scope.orders = {};
	ordersTable.on("bindingComplete", function(event) {
		if ($rootScope.userOrderDetails === true) {
			var paginginformation = ordersTable.jqxGrid('getpaginginformation');
			var pagenum = paginginformation.pagenum;
			if (pagenum > 0) {
				$scope.$apply();
				ordersTable.jqxGrid('gotopage', 0);
			} else {
				$rootScope.userOrderDetails = false;
				$timeout(function() {
					ordersTable.jqxGrid('showrowdetails', 0);
				}, 800);
			}
		}
	});
	function initTable(categories, languages, currencies) {
		var allCurrenciesCode = [];
		var allCurrencies = {};
		for (var i = 0; i < currencies.length; i++) {
				var currency = currencies[i];
				allCurrenciesCode.push(currency.code);
				allCurrencies[currency.code] = currency.id;
			}
		var dataAdapter = new $.jqx.dataAdapter(getSource("webapi/orders/current/withFilter", ordersTable), getAdapterFields(allCurrencies));
		ordersTable.jqxGrid(
				{
					theme: "bootstrap",
					width: '100%',
					source: dataAdapter,
					pageable: true,
					showfilterrow: true,
					sortable: true,
					filterable: true,
					columnsresize: true,
					columnsreorder: true,
					virtualmode: true,
					rendergridrows: function() {
						return dataAdapter.records;
					},
					rowdetails: true,
					rowdetailstemplate: {rowdetails: "<div style='margin: 10px;'></div>", rowdetailsheight: 300},
					ready: function() {
					},
					initrowdetails: initRowDetails,
					columns: [
						{text: "Categories", dataField: "categories", columntype: 'textbox', filtertype: 'checkedlist', filteritems: categories, filtercondition: 'starts_with', width: '160px', sortable: false, cellclassname: cellclassname},
						{text: "Languages", dataField: "languages", columntype: 'textbox', filtertype: 'checkedlist', filteritems: languages, width: '160px', sortable: false, cellclassname: cellclassname},
						{text: "Take", dataField: "takingCurrency", filtertype: 'list', filteritems: allCurrenciesCode, width: '85px', cellclassname: cellclassname},
						{text: "Give", dataField: "givingCurrency", filtertype: 'list', filteritems: allCurrenciesCode, width: '85px', cellclassname: cellclassname},
						{text: "Duration", dataField: "duration", filtertype: 'textbox', width: '80px', cellclassname: cellclassname},
						{text: "Responses", dataField: "responsesCount", filtertype: 'textbox', width: '80px', cellclassname: cellclassname},
						{text: "Status", dataField: "status", columntype: 'textbox', filtertype: 'list', filteritems: ['OPENED', 'IN PROCESS', 'SUCCESS', 'NOT SUCCESS', 'ARBITRATION'], width: '110px', cellclassname: cellclassname},
						{text: "Booking deadline", dataField: "endDate", filtertype: 'date', width: '120px', cellclassname: cellclassname, cellsformat: 'd'}
					]
				});
	}

});

orderModule.controller("ChangeStatusDialogController", function($scope, addResponse, orderId, status, $modalInstance) {
	$scope.comment = "";
	$scope.addResponse = addResponse;
	$scope.orderId = orderId;

	$scope.save = function(comment) {
		addResponse(orderId, comment, status);
		$modalInstance.close();
	};

	$scope.cancel = function() {
		$modalInstance.dismiss('canceled');
	};
});