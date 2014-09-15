var chatModule = angular.module("chat");

chatModule.factory("messagesResource", function($resource) {
	return $resource(window.context + "webapi/messages", {}, {
		getMessages: {method: "GET", url: window.context + "webapi/messages"},
		getMessagesByPartnerId: {method: "GET", url: window.context + "webapi/messages/by-partner"},
		getLaterThan: {method: "GET", url: window.context + "webapi/messages/later-than"},
		getPartnersLastMessages: {method: "GET", url: window.context + "webapi/messages/partners-last-messages"},
		sendMessage: {method: "PUT", url: window.context + "webapi/messages"}
	});
});
