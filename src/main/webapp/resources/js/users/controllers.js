var usersModule = angular.module("users");

usersModule.controller("UsersController", function($scope, $rootScope) {
	
	$scope.dataLength;
	$scope.filters = ["Public Key", "First Name", "Last Name", "Country", "City", "Rating"];
	$scope.filtersMap = {};
	$scope.filtersMap["Public Key"] = "publicKey";
	$scope.filtersMap["First Name"] = "firstName";
	$scope.filtersMap["Last Name"] = "lastName";
	$scope.filtersMap["Country"] = "country";
	$scope.filtersMap["City"] = "city";
	$scope.filtersMap["Rating"] = "rating";
	$scope.filterType = "Public Key";
	$scope.filterParam = "";

	$scope.directions = ["No greater than", "No less than"];
	$scope.directionType = "No greater than";

	$scope.selectFilterType = function(filterType) {
		$scope.filterType = filterType;
		if (filterType === "Rating") {
			$scope.filterParam = 0;
		} else {
			$scope.filterParam = "";
		}
		$scope.reloadDataTable();
	};
	$scope.selectDirectionType = function(directionType) {
		$scope.directionType = directionType;
		$scope.reloadDataTable();
	};
	var source =
		{
			dataType: "json",
			dataFields: [
				{name: "publicKey", type: "string"},
				{name: "firstName", type: "string"},
				{name: "lastName", type: "string"},
				{name: "country", type: "string"},
				{name: "city", type: "string"},
				{name: "rating", type: "string"}
			],
			root: "users",
			url: window.context + "webapi/profiles/withFilter"
		};
	var adapterFields = {
		formatData: function(data) {

			data.filter = $scope.filterParam;
			data.filterdatafield = $scope.filtersMap[$scope.filterType];
			if ($scope.directionType === "No greater than") {
				data.isLess = true;
			} else {
				data.isLess = false;
			}
			return data;
		},
		downloadComplete: function(data, status, xhr) {
			$scope.dataLength = data.length;
			source.totalRecords = data.length;
		},
		loadError: function(xhr, status, error) {
			console.log(error.toString());
		}
	};

	$scope.reloadDataTable = function() {
		angular.element("#users-table").jqxDataTable('clear');
		source.totalRecords = undefined;
		var dataAdapter = new $.jqx.dataAdapter(source, adapterFields);
		angular.element("#users-table").jqxDataTable(
			{
				width: '100%',
				theme: "bootstrap",
				pageable: true,
				pagerButtonsCount: 10,
				serverProcessing: true,
				source: dataAdapter,
				altRows: true,
				sortable: true,
				columnsResize: true,
				columns: [
					{text: "Public Key", dataField: "publicKey"},
					{text: "First Name", dataField: "firstName"},
					{text: "Last Name", dataField: "lastName"},
					{text: "Country", dataField: "country"},
					{text: "City", dataField: "city"},
					{text: "Rating", dataField: "rating"}
				]
			});
	};

	$scope.reloadDataTable();

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