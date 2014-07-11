var orderModule = angular.module("order");

orderModule.controller("UserOrdersController", function($scope, $rootScope, $interpolate) {

	var initRowDetails = function(index, parentElement, gridElement, datarecord) {
		var tabsdiv = null;
		var information = null;
		tabsdiv = angular.element(angular.element(parentElement).children()[0]);
		if (tabsdiv !== null) {
			var template = angular.element("#ordersTableDetailTmpl2").text();
			var exp = $interpolate(template);
			var context = {greeting: 'Hello', name: "Sanek"};
			var result = exp(context);
			//result.appendTo(tabsdiv);
			tabsdiv.append(result);
			/*information = tabsdiv.find('.information');
			var title = tabsdiv.find('.title');
			title.text(datarecord.userName);
			var container = angular.element('<div style="margin: 5px;"></div>')
			container.appendTo(angular.element(information));
			var photocolumn = angular.element('<div style="float: left; width: 15%;"></div>');
			var leftcolumn = angular.element('<div style="float: left; width: 45%;"></div>');
			var rightcolumn = angular.element('<div style="float: left; width: 40%;"></div>');
			container.append(photocolumn);
			container.append(leftcolumn);
			container.append(rightcolumn);
			var photo = angular.element("<div class='jqx-rc-all' style='margin: 10px;'><b>Photo:</b></div>");
			var image = angular.element("<div style='margin-top: 10px;'></div>");
			var imgurl = window.context + "webapi/profiles/" + datarecord.userPublicKey + "/photo";
			var img = angular.element('<img height="60" src="' + imgurl + '"/>');
			image.append(img);
			image.appendTo(photo);
			photocolumn.append(photo);

			var context = {greeting: 'Hello', name: "Sanek"};

			var template = angular.element("#ordersTableDetailTmpl").text();
			var exp = $interpolate(template);
			//expect(exp(context)).toEqual('Hello !');
			var result = exp(context);
			console.log("--- " + result);

			var languages = result;//orderDetailTemp;//"<div style='margin: 10px;'><b>Languages:</b> " + datarecord.languages + "</div>";
			var currencies = "<div style='margin: 10px;'><b>Currencies:</b> " + datarecord.currencies + "</div>";

			angular.element(leftcolumn).append(languages);
			angular.element(leftcolumn).append(currencies);

			var reward = "<div style='margin: 10px;'><b>Rating:</b> " + datarecord.reward + "</div>";
			var creationDate = "<div style='margin: 10px;'><b>Creation Date:</b> " + datarecord.creationDate + "</div>";
			var endDate = "<div style='margin: 10px;'><b>End Date:</b> " + datarecord.endDate + "</div>";
			angular.element(rightcolumn).append(creationDate);
			angular.element(rightcolumn).append(endDate);
			angular.element(rightcolumn).append(reward);*/


			angular.element(tabsdiv).jqxTabs({width: "95%", height: 170});
		}
	};
	var ordersTable = angular.element('#user-orders-table');
	var dataAdapter = new $.jqx.dataAdapter(getSource("webapi/orders/current/withFilter", ordersTable), getAdapterFields());
	
	var bindingCount = 0;
	ordersTable.on("bindingComplete", function(event) {
		if ($rootScope.userOrderDetails && $rootScope.userOrderDetails === true) {
			if (bindingCount > 0)
				bindingCount--;
			if (bindingCount === 0) {
				$rootScope.userOrderDetails = false;
			}
			var paginginformation = ordersTable.jqxGrid('getpaginginformation');
			var pagenum = paginginformation.pagenum;
			console.log("P: " + pagenum)
			ordersTable.jqxGrid('showrowdetails', 0);
			if (pagenum !== 0) {
				bindingCount = 3;
				ordersTable.jqxGrid('gotopage', 0);
				//ordersTable.jqxGrid('showrowdetails', 0);
			} else {
				console.log("~==============ws~")
			}

			console.log("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~")
		}
	});
	ordersTable.jqxGrid(
			{
				theme: "bootstrap",
				width: '100%',
				source: dataAdapter,
				pageable: true,
				showfilterrow: true,
				sortable: true,
				filterable: true,
				virtualmode: true,
				rendergridrows: function() {
					return dataAdapter.records;
				},
				rowdetails: true,
				rowdetailstemplate: {rowdetails: "<div style='margin: 10px;'></div>", rowdetailsheight: 200},
				ready: function() {
				},
				initrowdetails: initRowDetails,
				columns: [
					{text: "Categories", dataField: "categories", columntype: 'textbox', filtertype: 'textbox', filtercondition: 'starts_with', sortable: false, cellclassname: cellclassname},
					{text: "Languages", dataField: "languages", columntype: 'textbox', filtertype: 'none', sortable: false, cellclassname: cellclassname},
					{text: "Take", dataField: "takingCurrency", filtertype: 'none', width: '80px', cellclassname: cellclassname},
					{text: "Give", dataField: "givingCurrency", filtertype: 'none', width: '80px', cellclassname: cellclassname},
					{text: "Duration", dataField: "duration", filtertype: 'none', width: '80px', cellclassname: cellclassname},
					{text: "Responses", dataField: "responses", filtertype: 'none', width: '80px', cellclassname: cellclassname},
					{text: "Status", dataField: "status", columntype: 'textbox', filtertype: 'checkedlist', filteritems: ['OPENED', 'PROCESSED', 'SUCCESS', 'NOT SUCCESS', 'ARBITRATION'], width: '110px', cellclassname: cellclassname},
					{text: "Booking deadline", dataField: "endDate", filtertype: 'none', width: '120px', cellclassname: cellclassname, cellsformat: 'd'}
				]
			});
});