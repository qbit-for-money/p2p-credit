function isInt(str) {
	var n = ~~Number(str);
	return String(n) === str && n >= 0;
}

function isNumberField(field) {
	switch (field) {
		case "summaryRating":
			return true;
		case "opennessRating":
			return true;
		case "success":
			return true;
		case "partnersRating":
			return true;
		case "responsesCount":
			return true;
		case "duration":
			return true;
		default:
			return false;
	}
}

function formatDate(value) {
	var dateParts = value.split("/");
	var date = new Date(dateParts[2], (dateParts[1] - 1), dateParts[0]);
	return date;
}

function formatDownloadedOrders(orders) {
	for (var i in orders) {
		if (orders[i].order.languages !== undefined) {
			var languagesStr = "";
			var categoriesStr = "";
			for (var j in orders[i].order.languages) {
				languagesStr += orders[i].order.languages[j].code + ", ";
			}
			for (var j in orders[i].order.categories) {
				categoriesStr += orders[i].order.categories[j].code + ", ";
			}
		}
		orders[i].languages = languagesStr.substring(0, languagesStr.length - 2);
		orders[i].categories = categoriesStr.substring(0, categoriesStr.length - 2);
		var takingCurrenciesStr = "";
		if (!orders[i].order.incomingCurrency || !orders[i].order.incomingAmount) {
			orders[i].order.take = takingCurrenciesStr;
		} else {
			takingCurrenciesStr = orders[i].order.incomingAmount + " " + orders[i].order.incomingCurrency;
			orders[i].take = takingCurrenciesStr;
		}

		var givingCurrenciesStr = "";
		if (!orders[i].order.outcomingCurrency || !orders[i].order.outcomingAmount) {
			orders[i].order.outcomingCurrency = givingCurrenciesStr;
		} else {
			givingCurrenciesStr = orders[i].order.outcomingAmount + " " + orders[i].order.outcomingCurrency;
			orders[i].give = givingCurrenciesStr;
		}
		
		if (orders[i].order.status === "NOT_SUCCESS") {
			orders[i].order.status = "NOT SUCCESS";
		}
		if (orders[i].order.status === "IN_PROCESS") {
			orders[i].order.status = "IN PROCESS";
		}

		if (orders[i].userCurrencies && !orders[i].userCurrencies.length !== 0) {
			var userCurrenciesStr = "";
			for (var j in orders[i].userCurrencies) {
				userCurrenciesStr += orders[i].userCurrencies[j].code + ", "
			}
			orders[i].userCurrencies = userCurrenciesStr.substring(0, userCurrenciesStr.length - 2);
		}
		if (orders[i].userLanguages && orders[i].userLanguages.length !== 0) {
			var userLanguagesStr = "";
			for (var j in orders[i].userLanguages) {
				userLanguagesStr += orders[i].userLanguages[j].title + ", "
			}
			orders[i].userLanguages = userLanguagesStr.substring(0, userLanguagesStr.length - 2);
		}

		orders[i].title = orders[i].order.title;
		orders[i].orderData = orders[i].order.orderData;
		orders[i].status = orders[i].order.status;
		orders[i].type = orders[i].order.type;
		orders[i].responses = orders[i].order.responses;
		if (orders[i].responses && orders[i].responses.length) {
			orders[i].responsesCount = orders[i].responses.length;
		} else {
			orders[i].responsesCount = 0;
		}
		orders[i].approvedResponseId = orders[i].order.approvedResponseId;
		orders[i].duration = orders[i].order.duration + " " + ((orders[i].order.durationType === "HOUR") ? "часов" : "дней");
		orders[i].endDate = orders[i].order.bookingDeadline;
		orders[i].creationDate = orders[i].order.creationDate;
		orders[i].userId = orders[i].order.userId;
		orders[i].partnerId = orders[i].order.partnerId;
		orders[i].id = orders[i].order.id;
		orders[i].description = orders[i].order.description;
		
		if((orders[i].order.incomingCurrency !== "%") && (orders[i].order.outcomingCurrency !== "%")) {
			orders[i].type = "EXCHANGE";
		} else if(orders[i].order.incomingCurrency === "%") {
			orders[i].type = "GIVE";
		} else {
			orders[i].type = "TAKE";
		}
		orders[i].order = undefined;
	}
	return orders;
}

