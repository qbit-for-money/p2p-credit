var orderModule = angular.module("order");

orderModule.controller("LastOrdersController", function($scope, $rootScope) {
	var dataAdapter = new $.jqx.dataAdapter(getSource("webapi/orders/last", "#last-orders-table"), getAdapterFields());
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
					{text: "Categories", dataField: "categories", columntype: 'textbox', width: '140px', cellclassname: cellclassname},
					{text: "Languages", dataField: "languages", columntype: 'textbox', cellclassname: cellclassname},
					{text: "Take", dataField: "takingCurrency", width: '80px', cellclassname: cellclassname},
					{text: "Give", dataField: "givingCurrency", width: '80px', cellclassname: cellclassname},
					{text: "Duration", dataField: "duration", width: '80px', cellclassname: cellclassname},
					{text: "Rating", dataField: "summaryRating", width: '60px', cellclassname: cellclassname},
					{text: "Openness rating", dataField: "opennessRating", width: '60px', cellclassname: cellclassname},
					{text: "Orders", dataField: "ordersSumValue", cellclassname: cellclassname},
					{text: "Success value", dataField: "successValue", cellclassname: cellclassname},
					{text: "Partners rating", dataField: "partnersRating", cellclassname: cellclassname},
					{text: "Booking deadline", dataField: "endDate", width: '120px', cellclassname: cellclassname, cellsformat: 'd'}
				]
			});
});