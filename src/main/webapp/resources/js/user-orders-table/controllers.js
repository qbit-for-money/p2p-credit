var orderModule = angular.module("order");

orderModule.controller("UserOrdersController", function($scope, $rootScope) {

	var source =
			{
				dataType: "json",
				dataFields: [
					{name: "title", type: "string"},
					{name: "languages", type: "string"},
					{name: "currency", type: "string"},
					{name: "responses", type: "int"},
					{name: "status", type: "string"}
				],
				beforeprocessing: function(data) {
					source.totalrecords = data.length;
				},
				sort: function() {
					angular.element("#user-orders-table").jqxGrid('updatebounddata');
				},
				filter: function() {
					angular.element("#user-orders-table").jqxGrid('updatebounddata');
				},
				root: "orders",
				type: "POST",
				url: window.context + "webapi/orders/current/withFilter"
			};

	var adapterFields = {
		contentType: 'application/json; charset=utf-8',
		formatData: function(data) {
			var newData = {};
			newData.filterItems = [];

			for (var i = 0; i < data.filterscount; i++) {
				newData.filterItems[i] = {};
				var operator = data[data["filterdatafield" + i] + "operator"];
				if (operator) {
					if (operator === "and") {
						newData.filterItems[i].filterOperator = 1;
					}
				}

				newData.filterItems[i].filterDataField = data["filterdatafield" + i];
				newData.filterItems[i].filterCondition = data["filtercondition" + i];
				newData.filterItems[i].filterValue = data["filtervalue" + i];
			}

			newData.sortOrder = data.sortorder;
			newData.pageNumber = data.pagenum;
			newData.pageSize = data.pagesize;
			newData.recordstartindex = data.recordstartindex;
			newData.recordendindex = data.recordendindex;
			newData.sortDataField = data.sortdatafield;
			if ($rootScope.userType === "CREDITOR") {
				newData.orderType = 1;
			}
			if ($rootScope.userType === "BORROWER") {
				newData.orderType = 2;
			}
			return JSON.stringify(newData);
		},
		downloadComplete: function(data, status, xhr) {
			
			var orders = data.orders;
			for (var i in orders) {
				if (orders[i].order.languages !== undefined) {
					var languagesStr = "";
					for (var j in orders[i].order.languages) {
						languagesStr += orders[i].order.languages[j] + ", ";
					}
				}
				orders[i].languages = languagesStr.substring(0, languagesStr.length - 2);

				var currenciesStr = "";
				var currency = orders[i].order.currency;
				if (orders[i].order.currency === undefined) {
					orders[i].order.currency = currenciesStr;
				} else {
					var currencyInterval = orders[i].order.currencyInterval;
					currenciesStr = currency.code + " ( " + currencyInterval.startValue + " : " + currencyInterval.endValue + " )";
					orders[i].currency = currenciesStr;

					if (orders[i].order.status === "NOT_SUCCESS") {
						orders[i].order.status = "NOT SUCCESS";
					}
					if (orders[i].order.type === 1) {
						orders[i].order.type = "CREDITOR";
					}
					if (orders[i].order.type === 2) {
						orders[i].order.type = "BORROWER";
					}
				}
				orders[i].title = orders[i].order.title;
				orders[i].orderData = orders[i].order.orderData;
				orders[i].status = orders[i].order.status;
				orders[i].type = orders[i].order.type;
				orders[i].responses = orders[i].order.responses;
				
				orders[i].order = undefined;
			}
		},
		loadError: function(xhr, status, error) {
			console.log(error.toString());
		}
	};

	var initRowDetails = function(index, parentElement, gridElement, datarecord) {
		var tabsdiv = null;
		var information = null;
		tabsdiv = angular.element(angular.element(parentElement).children()[0]);
		if (tabsdiv !== null) {
			information = tabsdiv.find('.information');
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


			angular.element(tabsdiv).jqxTabs({width: "95%", height: 170});
		}
	}
	var dataAdapter = new $.jqx.dataAdapter(source, adapterFields);
	$("#user-orders-table").jqxGrid(
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
				rowdetailstemplate: {rowdetails: "<div style='margin: 10px;'><ul style='margin-left: 30px;'><li class='title'></li></ul><div class='information'></div></div>", rowdetailsheight: 200},
				ready: function() {
					//$("#user-orders-table").jqxGrid('showrowdetails', 0);
				},
				initrowdetails: initRowDetails,
				columns: [
					{text: "Title", dataField: "title", columntype: 'textbox', filtertype: 'textbox', filtercondition: 'starts_with'},
					{text: "Languages", dataField: "languages", columntype: 'textbox', filtertype: 'none'},
					{text: "Currency", dataField: "currency", filtertype: 'none'},
					{text: "Responses", dataField: "responses", filtertype: 'none'},
					{text: "Status", dataField: "status", columntype: 'textbox', filtertype: 'checkedlist', filteritems: ['OPENED', 'PROCESSED', 'SUCCESS', 'NOT SUCCESS', 'ARBITRATION'], width: '110px'},
				]
			});
});