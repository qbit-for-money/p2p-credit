var orderModule = angular.module("order");

orderModule.controller("OrdersController", function($scope, ordersResource, userProfileService, userService, $interpolate, $compile, $modal) {

	$scope.addResponse = function(orderId, comment) {
		var response = {};
		response.orderId = orderId;
		if (comment) {
			response.comment = comment;
		}
		var orderResponse = ordersResource.addResponse({}, response);

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
		var tabsdiv = null;
		tabsdiv = angular.element(angular.element(parentElement).children()[0]);
		if (tabsdiv !== null) {
			var imgurl = window.context + "webapi/profiles/" + datarecord.userPublicKey + "/photo";
			var userurl = window.context + "#/users/" + datarecord.userPublicKey;
			var template = angular.element("#ordersTableDetailTmpl").text();
			var exp = $interpolate(template);
			var disabled = false;
			for (var i in datarecord.responses) {
				if(datarecord.responses[i].userPublicKey === userService.get().publicKey) {
					disabled = true;
				}
			}
			
			var context = {
				name: datarecord.userName,
				imgurl: imgurl,
				languages: datarecord.userLanguages,
				currencies: datarecord.userCurrencies,
				userurl: userurl,
				mail: datarecord.userMail,
				phone: datarecord.userPhone,
				freeDescription: datarecord.orderData,
				orderId: datarecord.id,
				disabled: disabled,
				//isUserOrder: (datarecord.userPublicKey === userService.get().publicKey)
			};
			var content = exp(context);
			var result = $compile(content)($scope);
			tabsdiv.append(result);
			$scope.$apply();
			angular.element(tabsdiv).jqxTabs({width: "95%", height: 240});
		}
	};


	var dataAdapter = new $.jqx.dataAdapter(getSource("webapi/orders/withFilter", "#orders-table"), getAdapterFields());
	angular.element("#orders-table").on("bindingComplete", function(event) {
		console.log("BIND")
	});
	function initTable(categories, languages) {
		angular.element("#orders-table").jqxGrid(
			{
				theme: "bootstrap",
				width: '100%',
				source: dataAdapter,
				pageable: true,
				sortable: true,
				showfilterrow: true,
				filterable: true,
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
					{text: "Take", dataField: "takingCurrency", filtertype: 'textbox', width: '85px', cellclassname: cellclassname},
					{text: "Give", dataField: "givingCurrency", filtertype: 'textbox', width: '85px', cellclassname: cellclassname},
					{text: "Duration", dataField: "duration", filtertype: 'number', width: '80px', cellclassname: cellclassname},
					{text: "Rating", dataField: "summaryRating", columntype: 'textbox', filtertype: 'textbox', width: '60px', cellclassname: cellclassname},
					{text: "Openness rating", dataField: "opennessRating", columntype: 'textbox', filtertype: 'textbox', width: '120px', cellclassname: cellclassname},
					{text: "Orders", dataField: "ordersSumValue", columntype: 'textbox', filtertype: 'textbox', cellclassname: cellclassname, width: '60px'},
					{text: "Success value", dataField: "successValue", filtertype: 'textbox', cellclassname: cellclassname, width: '150px'},
					{text: "Partners rating", dataField: "partnersRating", columntype: 'textbox', filtertype: 'textbox', width: '100px', cellclassname: cellclassname},
					{text: "Booking deadline", dataField: "endDate", filtertype: 'date', width: '120px', cellclassname: cellclassname, cellsformat: 'd'}
				]
			});
	}

	$scope.openCommentDialog = function(orderId) {
		angular.element("#contentorders-table > div.jqx-grid-content.jqx-grid-content-bootstrap.jqx-widget-content.jqx-widget-content-bootstrap > div.jqx-enableselect.jqx-widget-content.jqx-widget-content-bootstrap").addClass("static-position");
		angular.element("#contentuser-orders-table > div.jqx-grid-content.jqx-grid-content-bootstrap.jqx-widget-content.jqx-widget-content-bootstrap > div.jqx-enableselect.jqx-widget-content.jqx-widget-content-bootstrap").addClass("static-position");
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
				angular.element("#contentorders-table > div.jqx-grid-content.jqx-grid-content-bootstrap.jqx-widget-content.jqx-widget-content-bootstrap > div.jqx-enableselect.jqx-widget-content.jqx-widget-content-bootstrap").removeClass("static-position");
				angular.element("#contentuser-orders-table > div.jqx-grid-content.jqx-grid-content-bootstrap.jqx-widget-content.jqx-widget-content-bootstrap > div.jqx-enableselect.jqx-widget-content.jqx-widget-content-bootstrap").removeClass("static-position");
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


