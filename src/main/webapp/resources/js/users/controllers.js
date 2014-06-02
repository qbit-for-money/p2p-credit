var usersModule = angular.module("users");

usersModule.controller("UsersController", function($scope, $rootScope) {

	var source =
		{
			dataType: "json",
			dataFields: [
				{name: "publicKey", type: "string"},
				{name: "firstName", type: "string"},
				{name: "lastName", type: "string"},
				{name: "rating", type: "string"}
			],
			root: "users",
			url: window.context + "webapi/users-profile/users?"
		};
	var dataAdapter = new $.jqx.dataAdapter(source,
		{
			formatData: function(data) {

				if (source.totalRecords) {
					data.$skip = data.pagenum * data.pagesize;
					data.$top = data.pagesize;
					if (data.sortdatafield && data.sortorder) {
						data.$orderby = data.sortdatafield + " " + data.sortorder;
					}
				}
				return data;
			},
			downloadComplete: function(data, status, xhr) {
				if (!source.totalRecords) {
					source.totalRecords = data.length;
				}
			},
			loadError: function(xhr, status, error) {
				console.log(error.toString());
			}
		}
	);




	angular.element("#users-table").jqxDataTable(
		{
			//width: 850,
			theme: "bootstrap",
			pageable: true,
			pagerButtonsCount: 10,
			serverProcessing: true,
			source: dataAdapter,
			altRows: true,
			sortable: true,
			columnsResize: true,
			columns: [
				{text: "Public Key", dataField: "publicKey", width: 300},
				{text: "First Name", dataField: "firstName", width: 250},
				{text: "Last Name", dataField: "lastName", width: 250},
				{text: "Rating", dataField: "rating", width: 50}
			]
		});

	var click = new Date();
	var lastClick = new Date();
	var lastRow = -1;
	angular.element("#users-table").bind("rowSelect", function(event) {
		click = new Date();
		if (click - lastClick < 500) {
			if (lastRow === event.args.index) {
				var rowData = event.args.row;
				window.location.href = window.context + "#/users/" + rowData.publicKey;
			}
		}
		lastClick = new Date();
		lastRow = event.args.index;
	});
});