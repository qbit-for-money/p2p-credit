var orderModule = angular.module("order");

orderModule.controller("LastOrdersController", function($scope, $rootScope) {
	
	var source =
			{
				dataType: "json",
				dataFields: [
					{name: "userPublicKey", type: "string"},
					{name: "userName", type: "string"},
					{name: "title", type: "string"},
					{name: "languages", type: "string"},
					{name: "currency", type: "string"},
					{name: "endDate", type: "string"},
					{name: "reward", type: "string"},
					{name: "responses", type: "int"},
					{name: "status", type: "string"},
					{name: "rating", type: "int"},
					{name: "success", type: "int"},
					{name: "successValue", type: "string"},
					{name: "partnersRating", type: "int"}
				],
				beforeprocessing: function(data) {
					source.totalrecords = data.length;
				},
				sort: function() {
					angular.element("#orders-table").jqxGrid('updatebounddata');
				},
				filter: function() {
					angular.element("#orders-table").jqxGrid('updatebounddata');
				},
				root: "orders",
				type: "POST",
				url: window.context + "webapi/orders/last"
			};

	var adapterFields = {
		contentType: 'application/json; charset=utf-8',
		formatData: function(data) {
			var newData = {};
			newData.filterItems = [];
			
			for(var i = 0; i < data.filterscount; i++) {
				newData.filterItems[i] = {};
				var operator = data[data["filterdatafield" + i] + "operator"];
				if(operator) {
					if(operator === "and") {
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
			if($rootScope.userType === "CREDITOR") {
				newData.orderType = 2;
			}
			if($rootScope.userType === "BORROWER") {
				newData.orderType = 1;
			}
			console.log(JSON.stringify(newData));
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
			//console.log(JSON.stringify(data));
		},
		loadError: function(xhr, status, error) {
			console.log(error.toString());
		}
	};
	var dataAdapter = new $.jqx.dataAdapter(source, adapterFields);
	angular.element("#last-orders-table").jqxGrid(
			{
				theme: "bootstrap",
				pagesize: 4,
				width: '100%',
				height: '126px',
				source: dataAdapter,
				virtualmode: true,
				selectionmode: 'none',
				rendergridrows: function() {
					return dataAdapter.records;
				},
				ready: function() {
				},
				columns: [
					{text: "Title", dataField: "title", columntype: 'textbox', width: '120'},
					{text: "Languages", dataField: "languages", columntype: 'textbox', width: '120'},
					{text: "Currency", dataField: "currency", width: '130'},
					{text: "Responses", dataField: "responses"},
					{text: "Status", dataField: "status", columntype: 'textbox', width: '80'},
					{text: "Rating", dataField: "rating"},
					{text: "Successful deal", dataField: "success"},
					{text: "Successful value", dataField: "successValue"},
					{text: "Partners rating", dataField: "partnersRating"}
				]
			});
});