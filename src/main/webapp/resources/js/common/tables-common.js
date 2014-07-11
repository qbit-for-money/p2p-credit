var cellclassname = function(row, column, value, data) {
	if (data.takingCurrency && data.givingCurrency) {
		return "exchange-row";
	} else if (data.takingCurrency) {
		return "credit-row";
	} else if (data.givingCurrency) {
		return "borrow-row";
	}
};
//path = "webapi/orders/withFilter"
//id = "#orders-table"
function getSource(path, id) {
	var source =
			{
				dataType: "json",
				dataFields: [
					{name: "categories", type: "string"},
					{name: "userName", type: "string"},
					{name: "title", type: "string"},
					{name: "languages", type: "string"},
					{name: "takingCurrency", type: "string"},
					{name: "givingCurrency", type: "string"},
					{name: "duration", type: "string"},
					{name: "reward", type: "string"},
					{name: "responses", type: "number"},
					{name: "status", type: "string"},
					{name: "summaryRating", type: "number"},
					{name: "opennessRating", type: "number"},
					{name: "ordersSumValue", type: "string"},
					{name: "successValue", type: "string"},
					{name: "partnersRating", type: "number"},
					{name: "endDate", type: "date"}
				],
				beforeprocessing: function(data) {
					source.totalrecords = data.length;
				},
				sort: function() {
					angular.element(id).jqxGrid('updatebounddata');
				},
				filter: function() {
					angular.element(id).jqxGrid('updatebounddata');
				},
				root: "orders",
				type: "POST",
				url: window.context + path
			};
	return source;
}

function getAdapterFields() {
	return {
		contentType: 'application/json; charset=utf-8',
		formatData: function(data) {
			var newData = {};
			newData.filterItems = [];
			for (var i = 0; i < data.filterscount; i++) {
				if (!isInt(data["filtervalue" + i]) && ((data["filterdatafield" + i] === "summaryRating") || (data["filterdatafield" + i] === "opennessRating") || (data["filterdatafield" + i] === "success") || (data["filterdatafield" + i] === "partnersRating"))) {
					continue;
				}

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
				if (data["filterdatafield" + i] === "endDate") {
					var date = new Date(data["filtervalue" + i]);
					var monthNames = ["January", "February", "March", "April", "May", "June",
						"July", "August", "September", "October", "November", "December"];
					//monthNames[date.getMonth()]
					//var month = date.get
					newData.filterItems[i].filterValue = monthNames[date.getMonth()] + " " + date.getDate() + ", " + date.getFullYear();
				}
				if ((data["filterdatafield" + i] === "summaryRating") || (data["filterdatafield" + i] === "opennessRating") || (data["filterdatafield" + i] === "success") || (data["filterdatafield" + i] === "partnersRating")) {
					newData.filterItems[i].filterCondition = "GREATER_THAN_OR_EQUAL";
				}
				//console.log(JSON.stringify(newData.filterItems[i]))
			}
			console.log(JSON.stringify(newData.filterItems))
			newData.sortOrder = data.sortorder;
			newData.pageNumber = data.pagenum;
			newData.pageSize = data.pagesize;
			newData.recordstartindex = data.recordstartindex;
			newData.recordendindex = data.recordendindex;
			newData.sortDataField = data.sortdatafield;
			return JSON.stringify(newData);
		},
		downloadComplete: function(data, status, xhr) {
			var orders = data.orders;
			for (var i in orders) {
				if (orders[i].order.languages !== undefined) {
					var languagesStr = "";
					var categoriesStr = "";
					for (var j in orders[i].order.languages) {
						languagesStr += orders[i].order.languages[j] + ", ";
					}
					for (var j in orders[i].order.categories) {
						categoriesStr += orders[i].order.categories[j] + ", ";
					}
				}
				orders[i].languages = languagesStr.substring(0, languagesStr.length - 2);
				orders[i].categories = categoriesStr.substring(0, categoriesStr.length - 2);

				var takingCurrenciesStr = "";
				if (!orders[i].order.takingCurrency || !orders[i].order.takingValue) {
					orders[i].order.takingCurrency = takingCurrenciesStr;
				} else {
					takingCurrenciesStr = orders[i].order.takingCurrency.code + " ( " + orders[i].order.takingValue + " )";
					orders[i].takingCurrency = takingCurrenciesStr;
				}

				var givingCurrenciesStr = "";
				if (!orders[i].order.givingCurrency || !orders[i].order.givingValue) {
					orders[i].order.givingCurrency = givingCurrenciesStr;
				} else {
					givingCurrenciesStr = orders[i].order.givingCurrency.code + " ( " + orders[i].order.givingValue + " )";
					orders[i].givingCurrency = givingCurrenciesStr;
				}
				
				orders[i].ordersSumValue = orders[i].ordersSumValue + " : " + orders[i].successTransactionsSum;

				if (orders[i].order.status === "NOT_SUCCESS") {
					orders[i].order.status = "NOT SUCCESS";
				}
				orders[i].title = orders[i].order.title;
				orders[i].orderData = orders[i].order.orderData;
				orders[i].status = orders[i].order.status;
				orders[i].type = orders[i].order.type;
				orders[i].responses = orders[i].order.responses;
				orders[i].duration = orders[i].order.duration + " " + ((orders[i].order.durationType === "HOUR") ? "hours" : "days");
				orders[i].endDate = formatDate(orders[i].order.endDate);
				orders[i].order = undefined;
			}
			console.log("BIND: " + JSON.stringify(orders))

		},
		loadError: function(xhr, status, error) {
			console.log(error.toString());
		}
	};
}

function isInt(str) {
	var n = ~~Number(str);
	return String(n) === str && n >= 0;
}

function formatDate(value) {
	var dateParts = value.split("/");
	var date = new Date(dateParts[2], (dateParts[1] - 1), dateParts[0]);
	return date;
}
;
