var chatModule = angular.module("chat");

chatModule.factory("chatService", function($modal, messagesResource, $interval) {
	return {
		openChatDialog: function(currentUserId, partnerId) {
			var data = {};
			var modalInstance = $modal.open({
				controller: "ChatDialogController",
				templateUrl: "resources/html/chat/chat-dialog.html",
				windowClass: "auth-dialog chat-dialog",
				backdrop: "none", keyboard: false, backdropClick: false, dialogFade: false,
				resolve: {
					partnerId: function() {
						return partnerId;
					},
					currentUserId: function() {
						return currentUserId;
					},
					data: function() {
						return data;
					}
				}
			});

			modalInstance.result.then(function() {
			},
				function() {
					if (data.secondsInWaitingTimerId) {
						$interval.cancel(data.secondsInWaitingTimerId);
					}
				});
		},
		sendMessage: function(partnerId, message) {
			var messageRequest = {};
			messageRequest.partnerId = partnerId;
			messageRequest.message = message;
			var messageResponse = messagesResource.sendMessage({}, messageRequest);
		}, 
		sendMessageForAdmin: function(message) {
			var messageRequest = {};
			messageRequest.message = message;
			console.log("SEND: " + JSON.stringify(messageRequest))
			var messageResponse = messagesResource.sendMessageForAdmin({}, messageRequest);
		}
	};
});