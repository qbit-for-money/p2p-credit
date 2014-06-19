var orderModule = angular.module("order");

orderModule.controller("OrdersController", function($scope) {
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
				sort: function() {
					angular.element("#orders-table").jqxGrid('updatebounddata');
				},
				filter: function() {
					angular.element("#orders-table").jqxGrid('updatebounddata');
				},
				root: "orders",
				type: "POST",
				url: window.context + "webapi/orders/current/withFilter"
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

	var initRowDetails = function(index, parentElement, gridElement, datarecord) {
		var tabsdiv = null;
		var information = null;
		var order = null;
		tabsdiv = angular.element(angular.element(parentElement).children()[0]);
		if (tabsdiv !== null) {
			information = tabsdiv.find('.information');
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
			ordercontainer.append(approveButton);


			angular.element(tabsdiv).jqxTabs({width: "95%", height: 170});
		}
	};
	var dataAdapter = new $.jqx.dataAdapter(source, adapterFields);
	angular.element("#orders-table").jqxGrid(
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
				rowdetailstemplate: {rowdetails: "<div style='margin: 10px;'><ul style='margin-left: 30px;'><li class='title'></li><li>Order Init</li></ul><div class='information'></div><div class='order-init'></div></div>", rowdetailsheight: 200},
				ready: function() {
				},
				initrowdetails: initRowDetails,
				columns: [
					{text: "Name", dataField: "userName", columntype: 'textbox', filtertype: 'textbox', filtercondition: 'starts_with'},
					{text: "Languages", dataField: "languages", columntype: 'textbox', filtertype: 'none'},
					{text: "Currencies", dataField: "currencies"},
					{text: "Creation Date", dataField: "creationDate", columntype: 'textbox', filtertype: 'textbox', filtercondition: 'starts_with'},
					{text: "End Date", dataField: "endDate", columntype: 'textbox', filtertype: 'textbox', filtercondition: 'starts_with'},
					{text: "Reward", dataField: "reward", columntype: 'textbox', filtertype: 'textbox', filtercondition: 'starts_with'}
				]
			});
});


