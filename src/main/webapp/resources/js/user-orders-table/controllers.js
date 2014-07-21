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
			initTable(categories, languages);
		});
	});


	var initRowDetails = function(index, parentElement, gridElement, datarecord) {
//console.log(JSON.stringify(datarecord))
		var tabsdiv = null;
		tabsdiv = angular.element(angular.element(parentElement).children()[0]);
		if (tabsdiv !== null) {
			var responsesContent = "";
			//ordersResource.getResponse({'id': datarecord.approvedResponseId});
			//console.log("#$#$#$ " + datarecord.approvedResponseId + " ** " + JSON.stringify(datarecord.responses));
			/*var responses = [];
			 for (var i in datarecord.responses) {
			 
			 
			 }
			 var userProfileResponse = usersProfileResource.getById({'id': publicKey});
			 userProfileResponse.$promise.then(function() {
			 
			 });*/
			for (var i in datarecord.responses) {
				console.log(datarecord.responses[i].id)
				var publicKey = datarecord.responses[i].userPublicKey;
				datarecord.responses[i].imgurl = window.context + "webapi/profiles/" + publicKey + "/photo";
				datarecord.responses[i].userurl = window.context + "#/users/" + publicKey;

				var responsesTemplate = angular.element("#responsesTmpl").text();
				var responsesExp = $interpolate(responsesTemplate);
				console.log((datarecord.approvedResponseId) ? true : false)




				var responsesContext = {
					imgurl: datarecord.responses[i].imgurl,
					userurl: datarecord.responses[i].userurl,
					isComment: (!datarecord.responses[i].comment) ? false : true,
					isAttributes: datarecord.responses[i].comment ? false : true,
					comment: datarecord.responses[i].comment,
					name: datarecord.responses[i].userName,
					mail: datarecord.responses[i].userEmail,
					phone: datarecord.responses[i].userPhone,
					orderId: datarecord.id,
					userId: publicKey,
					approvedResponseId: datarecord.approvedResponseId,
					orderStatus: datarecord.status,
					status: (datarecord.approvedResponseId) ? true : false
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
				freeDescription: datarecord.orderData
			};

			var content = exp(context);
			content = content.replace('#responses-list', responsesContent)
			var result = $compile(content)($scope);
			tabsdiv.append(result);
			$scope.$apply();
			angular.element(tabsdiv).jqxTabs({width: "95%", height: 240});
		}
	};
	var ordersTable = angular.element('#user-orders-table');
	var dataAdapter = new $.jqx.dataAdapter(getSource("webapi/orders/current/withFilter", ordersTable), getAdapterFields());
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
	function initTable(categories, languages) {
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
						{text: "Take", dataField: "takingCurrency", filtertype: 'textbox', width: '85px', cellclassname: cellclassname},
						{text: "Give", dataField: "givingCurrency", filtertype: 'textbox', width: '85px', cellclassname: cellclassname},
						{text: "Duration", dataField: "duration", filtertype: 'number', width: '80px', cellclassname: cellclassname},
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