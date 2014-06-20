var orderModule = angular.module("order");

orderModule.controller("LastOrdersController", function($scope, $rootScope) {
	
	var source =
			{
				dataType: "json",
				dataFields: [
					{name: "userPublicKey", type: "string"},
					{name: "userName", type: "string"},
					{name: "languages", type: "string"},
					{name: "currencies", type: "string"},
					{name: "creationDate", type: "string"},
					{name: "endDate", type: "string"},
					{name: "reward", type: "string"}
				],
				beforeprocessing: function(data) {
					source.totalrecords = data.length;
				},
				root: "orders",
				type: "POST",
				url: window.context + "webapi/orders/last"
			};

	var adapterFields = {
		contentType: 'application/json; charset=utf-8',
		formatData: function(data) {
			return JSON.stringify(data);
		},
		downloadComplete: function(data, status, xhr) {
			var orders = data.orders;
			for (var i in orders) {
				if (orders[i].languages === undefined)
					continue;

				var languagesStr = "";
				for (var j in orders[i].languages) {
					languagesStr += orders[i].languages[j].substring(0, 3) + ", ";
				}
				orders[i].languages = languagesStr.substring(0, languagesStr.length - 2);
			}
			for (var i in orders) {
				if (orders[i].currencies === undefined)
					continue;

				var currenciesStr = "";

				for (var j in orders[i].currencies) {
					currenciesStr += orders[i].currencies[j].currency.code.substring(0, 3) + ", ";
				}
				orders[i].currencies = currenciesStr.substring(0, currenciesStr.length - 2);
			}
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
					{text: "Name", dataField: "userName", columntype: 'textbox'},
					{text: "Languages", dataField: "languages", columntype: 'textbox'},
					{text: "Currencies", dataField: "currencies"},
					{text: "Creation Date", dataField: "creationDate", columntype: 'textbox'},
					{text: "End Date", dataField: "endDate", columntype: 'textbox'},
					{text: "Reward", dataField: "reward", columntype: 'textbox'}
				]
			});
});