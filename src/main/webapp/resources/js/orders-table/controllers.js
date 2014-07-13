var orderModule = angular.module("order");

orderModule.controller("OrdersController", function($scope, ordersResource, userProfileService, $interpolate, $compile, $modal) {

	$scope.addResponse = function(orderId, comment) {
		alert(orderId + " " + comment)
		var response = {};
		response.orderId = orderId;
		if (comment) {
			response.comment = comment;
		}
		var orderResponse = ordersResource.addResponse({}, response);

		orderResponse.$promise.then(function() {
			console.log(JSON.stringify(orderResponse))
		});
	};
	userProfileService.getAllCategories(function(categories) {
		userProfileService.getAllLanguages(function(languages) {
			initTable(categories, languages);
		});
	});

	var initRowDetails = function(index, parentElement, gridElement, datarecord) {
		var tabsdiv = null;
		var information = null;
		var order = null;
		tabsdiv = angular.element(angular.element(parentElement).children()[0]);
		if (tabsdiv !== null) {
			//console.log("&&***&& " + JSON.stringify(datarecord))
			var imgurl = window.context + "webapi/profiles/" + datarecord.userPublicKey + "/photo"
			var userurl = window.context + "#/users/" + datarecord.userPublicKey;
			var template = angular.element("#ordersTableDetailTmpl").text();
			var exp = $interpolate(template);

			var context = {
				name: datarecord.userName,
				imgurl: imgurl,
				languages: datarecord.userLanguages,
				currencies: datarecord.userCurrencies,
				userurl: userurl,
				mail: datarecord.userMail,
				phone: datarecord.userPhone,
				freeDescription: datarecord.orderData,
				orderId: datarecord.id
			};
			//$scope.name = datarecord.userName;
			//$scope.imgurl = imgurl;
			//$scope.languages = datarecord.languages;
			//$scope.currencies = datarecord.userCurrencies;
			//$scope.userurl = userurl;
			//$scope.mail = datarecord.userMail;
			//$scope.phone = datarecord.userPhone;
			//$scope.freeDescription = datarecord.orderData;
			//$scope.orderId = datarecord.id;
			var content = exp(context);
			var result = $compile(content)($scope);
			//var result = exp(context);
			//result.appendTo(tabsdiv);
			tabsdiv.append(result);
			$scope.$apply();
			/*information = tabsdiv.find('.information');
			 order = tabsdiv.find('.order-init');
			 var title = tabsdiv.find('.title');
			 title.text(datarecord.userName);
			 var container = angular.element('<div style="margin: 5px;"></div>');
			 container.appendTo(angular.element(information));
			 var photocolumn = angular.element('<div style="float: left; width: 15%;"></div>');
			 var leftcolumn = angular.element('<div style="float: left; width: 45%;"></div>');
			 var rightcolumn = angular.element('<div style="float: left; width: 40%;"></div>');
			 container.append(photocolumn);
			 container.append(leftcolumn);
			 container.append(rightcolumn);
			 var photo = angular.element("<div class='jqx-rc-all' style='margin: 10px;'><b>Photo:</b></div>");
			 var image = angular.element("<div style='margin-top: 10px;'></div>");
			 var imgurl = window.context + "webapi/profiles/" + datarecord.userPublicKey + "/photo"
			 var img = angular.element('<img height="60" src="' + imgurl + '"/>');
			 image.append(img);
			 image.appendTo(photo);
			 photocolumn.append(photo);
			 var languages = "<div style='margin: 10px;'><b>Languages:</b> " + datarecord.languages + "</div>";
			 var currencies = "<div style='margin: 10px;'><b>Currencies:</b> " + datarecord.currencies + "</div>";
			 
			 angular.element(leftcolumn).append(languages);
			 angular.element(leftcolumn).append(currencies);
			 
			 var reward = "<div style='margin: 10px;'><b>Rating:</b> " + datarecord.reward + "</div>";
			 var creationDate = "<div style='margin: 10px;'><b>Creation Date:</b> " + datarecord.creationDate + "</div>";
			 var endDate = "<div style='margin: 10px;'><b>End Date:</b> " + datarecord.endDate + "</div>";
			 angular.element(rightcolumn).append(creationDate);
			 angular.element(rightcolumn).append(endDate);
			 angular.element(rightcolumn).append(reward);
			 
			 var ordercontainer = angular.element('<div style="margin: 5px;"></div>');
			 ordercontainer.appendTo(angular.element(order));
			 angular.element(order).append(ordercontainer);
			 var chatButton = angular.element('<div style="float: left; width: 50%;"><button style="float: right;" class="btn btn-default btn-lg" type="button">Chat</button></div>');
			 var approveButton = angular.element('<div style="float: left; width: 40%;"><button style="margin-left: 10px;" class="btn btn-success btn-lg" type="button">Approve</button></div>');
			 ordercontainer.append(chatButton);
			 ordercontainer.append(approveButton);*/


			angular.element(tabsdiv).jqxTabs({width: "95%", height: 240});
		}
	};



	var daterenderer = function(row, column, value) {
		if (value.toString().indexOf("/") === -1) {
			var date = new Date(value);
			var month = date.getMonth();
			month++;
			var dd = date.getDate().toString();
			value = (dd[1] ? dd : "0" + dd[0]) + "/" + month + "/" + date.getFullYear();
		}
		return value;
	};
	var dataAdapter = new $.jqx.dataAdapter(getSource("webapi/orders/withFilter", "#orders-table"), getAdapterFields());
	angular.element("#orders-table").on("bindingComplete", function(event) {
		console.log("BIND")
	});
	function initTable(categories, languages) {
		//var languages = userProfileService.getAllLanguages();
		angular.element("#orders-table").jqxGrid(
			{
				theme: "bootstrap",
				width: '100%',
				source: dataAdapter,
				pageable: true,
				sortable: true,
				showfilterrow: true,
				filterable: true,
				//autorowheight: true,
				//autoheight: true,
				virtualmode: true,
				//autoshowfiltericon: true,
				rendergridrows: function() {
					return dataAdapter.records;
				},
				rowdetails: true,
				rowdetailstemplate: {rowdetails: "<div style='margin: 10px;'></div>", rowdetailsheight: 300},
				/*ready: function() {
				 var localizationObject = {
				 filterstringcomparisonoperators: ['contains', 'does not contain'],
				 // filter numeric comparison operators.
				 filternumericcomparisonoperators: ['less than', 'greater than'],
				 // filter date comparison operators.
				 filterdatecomparisonoperators: ['less than', 'greater than'],
				 // filter bool comparison operators.
				 filterbooleancomparisonoperators: ['equal', 'not equal']
				 }
				 $("#orders-table").jqxGrid('localizestrings', localizationObject);
				 },*/
				/*updatefilterconditions: function (type, defaultconditions) {
				 var stringcomparisonoperators = ['CONTAINS', 'DOES_NOT_CONTAIN'];
				 var numericcomparisonoperators = ['LESS_THAN', 'GREATER_THAN'];
				 var datecomparisonoperators = ['LESS_THAN', 'GREATER_THAN'];
				 var booleancomparisonoperators = ['EQUAL', 'NOT_EQUAL'];
				 switch (type) {
				 case 'stringfilter':
				 return stringcomparisonoperators;
				 case 'numericfilter':
				 return numericcomparisonoperators;
				 case 'datefilter':
				 return datecomparisonoperators;
				 case 'booleanfilter':
				 return booleancomparisonoperators;
				 }
				 },*/
				/*updatefilterpanel: function (filtertypedropdown1, filtertypedropdown2, filteroperatordropdown, filterinputfield1, filterinputfield2, filterbutton, clearbutton,
				 columnfilter, filtertype, filterconditions) {
				 var index1 = 0;
				 var index2 = 0;
				 if (columnfilter != null) {
				 var filter1 = columnfilter.getfilterat(0);
				 var filter2 = columnfilter.getfilterat(1);
				 if (filter1) {
				 index1 = filterconditions.indexOf(filter1.comparisonoperator);
				 var value1 = filter1.filtervalue;
				 filterinputfield1.val(value1);
				 }
				 if (filter2) {
				 index2 = filterconditions.indexOf(filter2.comparisonoperator);
				 var value2 = filter2.filtervalue;
				 filterinputfield2.val(value2);
				 }
				 }
				 filtertypedropdown1.jqxDropDownList({ autoDropDownHeight: true, selectedIndex: index1 });
				 filtertypedropdown2.jqxDropDownList({ autoDropDownHeight: true, selectedIndex: index2 });
				 },*/
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

	//$('#clearfilteringbutton').jqxButton({ height: 25});
	$('#clearfilteringbutton').click(function() {
		console.log("RRRRR%")
		$("#orders-table").jqxGrid('clearfilters');
	});

	$scope.openCommentDialog = function(orderId) {
		angular.element("#contentorders-table > div.jqx-grid-content.jqx-grid-content-bootstrap.jqx-widget-content.jqx-widget-content-bootstrap > div.jqx-enableselect.jqx-widget-content.jqx-widget-content-bootstrap").addClass("static-position");
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


