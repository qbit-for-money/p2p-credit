var chatModule = angular.module("chat");

chatModule.controller("ChatDialogController", function($scope, $rootScope, partnerId, currentUserId, data, $modalInstance, chatService, messagesResource, $timeout, usersProfileResource, $interval) {
	$scope.partnerId = partnerId;
	$scope.currentUserId = currentUserId;
	$scope.partnerImgUrl = window.context + "webapi/photos/" + partnerId;
	$scope.partnerUrl = window.context + "#/users/" + partnerId;
	$scope.userImgUrl = window.context + "webapi/photos/" + currentUserId;
	$scope.currentUserUrl = window.context + "#/users/" + currentUserId;
	$scope.message = "";
	$scope.messageHistory = [];
	var messagesCount = 0;
	var pageNumber = 0;
	var loadOnScroll = false;
	$timeout(function() {
		if (!$rootScope.isPhone) {
			angular.element(".chat-dialog").find(".modal-dialog").css("height", "70%");
		}
		angular.element("#chat-history").scroll(function() {
			//console.log("SCROLL: " + angular.element("#chat-history").scrollTop() + " " + angular.element("#chat-history").height() + " " + angular.element("#chat-history").parent().height())
			if (angular.element("#chat-history").scrollTop() === 0) {
				$timeout(function() {
					if ((pageNumber > 30) || (messagesCount <= pageNumber * 15)) {
						return;
					}
					if (loadOnScroll === false) {
						return;
					}
					loadOnScroll = false;
					loadMessages(true);
				}, 500);
			}
		});
	});



	$scope.goToPartnerPage = function() {
		$scope.close();
		window.location.href = window.context + "#/users/" + partnerId;
	};

	$scope.goToCurrentUserPage = function() {
		$scope.close();
		window.location.href = window.context + "#/users/" + $scope.currentUserId;
	};

	var currentUserProfileResponse = usersProfileResource.getShortById({'id': currentUserId});
	currentUserProfileResponse.$promise.then(function() {
		$scope.currentUserName = (currentUserProfileResponse.name) ? currentUserProfileResponse.name : currentUserId;
		var partnerProfileResponse = usersProfileResource.getShortById({'id': partnerId});
		partnerProfileResponse.$promise.then(function() {
			$scope.partnerName = (partnerProfileResponse.name) ? partnerProfileResponse.name : partnerId;
			console.log("USER: " + $scope.currentUserName + " PARTNER: " + $scope.partnerName)
			loadMessages();
		});
	});


	console.log(currentUserId + " " + partnerId)
	//var response = messagesResource.getLaterThan({date: });
	$scope.send = function(message) {
		chatService.sendMessage(partnerId, message);
		angular.element("#chat-text-area").val('');
		var messageObject = {};
		messageObject.creationDate = formatDate(new Date());
		messageObject.message = message;
		messageObject.partnerId = partnerId;
		messageObject.userId = currentUserId;

		console.log(JSON.stringify(messageObject))
		//$scope.messageHistory.push(messageObject);

		$timeout(function() {
			$scope.$apply(function() {
				$scope.message = "";
				loadLastMessages(null, function() {
					$timeout(function() {
						var block = document.getElementById("chat-history");
						block.scrollTop = block.scrollHeight;
					});
				});
			});

		});

	};

	data.secondsInWaitingTimerId = $interval(loadLastMessages, 4000);
	$scope.$on("$destroy", function() {
		if (data.secondsInWaitingTimerId) {
			$interval.cancel(data.secondsInWaitingTimerId);
		}
	});

	function loadMessages(scrollLoad) {
		console.log("M_FIRST_PAGE: " + pageNumber)
		var messagesResponse = messagesResource.getMessagesByPartnerId({partnerId: partnerId, firstPage: pageNumber, pageSize: 15});
		pageNumber += 1;
		messagesResponse.$promise.then(function() {
			messagesCount = messagesResponse.length;
			var messages = messagesResponse.messages;
			for (var i in messages) {
				messages[i].creationDate = formatDate(messages[i].creationDate);
				$scope.messageHistory.unshift(messages[i]);
			}

			$timeout(function() {
				var block = document.getElementById("chat-history");
				if (scrollLoad) {
					if ((messagesCount - (pageNumber - 1) * 15) < 15) {
						block.scrollTop = 0;
					} else {
						block.scrollTop = block.scrollHeight / pageNumber;
					}
				} else {
					block.scrollTop = block.scrollHeight;
				}
				loadOnScroll = true;
			});
			console.log(JSON.stringify($scope.messageHistory))

		});
	}

	function loadLastMessages(value, callback) {
		if ($scope.messageHistory.length === 0) {
			var response = messagesResource.getMessagesByPartnerId({partnerId: partnerId});
		} else {
			var lastMessageCreationDateStr = $scope.messageHistory[$scope.messageHistory.length - 1].creationDate;
			var response = messagesResource.getLaterThan({creationDate: lastMessageCreationDateStr, partnerId: partnerId});
		}
		response.$promise.then(function() {
			//console.log(JSON.stringify(response.messages))
			response.messages = response.messages.reverse();
			var messages = response.messages;
			for (var i in messages) {
				messages[i].creationDate = formatDate(messages[i].creationDate);
				$scope.messageHistory.push(messages[i]);
			}
			if (callback) {
				callback();
			}
		});
	}

	$scope.close = function() {
		$modalInstance.close();
	};

	function formatDate(date) {
		return $.format.date(date, "dd/MM/yy HH:mm:ss");
	}
});

chatModule.controller("ChatController", function($scope, $rootScope, chatService, messagesResource, $timeout, usersProfileResource, $interval) {

	//$scope.partnerId = "aleksashka6666@gmail.com";
	//var partnerId = $scope.partnerId
	$scope.currentUserId = $rootScope.user.publicKey;
	var currentUserId = $scope.currentUserId;
	//$scope.partnerImgUrl = window.context + "webapi/photos/" + partnerId;
	//$scope.partnerUrl = window.context + "#/users/" + partnerId;
	$scope.userImgUrl = window.context + "webapi/photos/" + $scope.currentUserId;
	$scope.currentUserUrl = window.context + "#/users/" + $scope.currentUserId;
	$scope.message = "";
	$scope.usersHistory = [];
	$scope.lastMessages = [];
	$scope.messageHistory = [];
	var lastMessages;
	$scope.isDialog = false;
	var messagesCount = 0;
	var pageNumber = 0;
	var lastMessagesPageNumber = 0;
	var loadOnScroll = false;
	var secondsInWaitingTimerId;
	$timeout(function() {



		/*angular.element("#chat-history").scroll(function() {
		 console.log("SCROLL: " + angular.element("#chat-history").scrollTop() + " " + angular.element("#chat-history").height() + " " + angular.element("#chat-history").parent().height())
		 if (angular.element("#chat-history").scrollTop() === 0) {
		 $timeout(function() {
		 if ((pageNumber > 30) || (messagesCount <= pageNumber * 15)) {
		 return;
		 }
		 if (loadOnScroll === false) {
		 return;
		 }
		 loadOnScroll = false;
		 loadMessages(true);
		 }, 500);
		 }
		 });*/
	});

	loadUsersLastMessages();

	var usersSecondsInWaitingTimerId = $interval(loadUsersLastMessages, 4000);

	function loadUsersLastMessages() {

		var lastMessagesResponse = messagesResource.getPartnersLastMessages({firstPage: lastMessagesPageNumber, pageSize: 15});
		lastMessagesResponse.$promise.then(function() {

			lastMessages = lastMessagesResponse.messages;
			for (var i in lastMessages) {
				var linkId = "";
				if (lastMessages[i].userId === $scope.currentUserId) {
					linkId = lastMessages[i].partnerId;
				} else {
					linkId = lastMessages[i].userId;
				}
				console.log(lastMessages[i].userId + " " + lastMessages[i].partnerId)
				lastMessages[i].linkId = linkId;
				lastMessages[i].imgUrl = window.context + "webapi/photos/" + linkId;
				lastMessages[i].userImgUrl = window.context + "webapi/photos/" + lastMessages[i].userId;
				lastMessages[i].url = window.context + "#/users/" + linkId;
				lastMessages[i].name = linkId;
				//setUserName(lastMessages[i]);
				lastMessages[i].creationDate = formatDate(lastMessages[i].creationDate);

				if (lastMessages[i].message.length > 50) {
					var message = lastMessages[i].message.substring(0, 50);
					lastMessages[i].message = message + "..."
				}
			}
			$scope.lastMessages.splice(0, $scope.lastMessages.length);
			$scope.lastMessages = lastMessages;
			console.log(JSON.stringify($scope.lastMessages))
			
			
			for (var i in lastMessages) {
				
			}
		});
	}
	
	function setUserName(message) {
		var userId = message.partnerId;
		var userProfileResponse = usersProfileResource.getShortById({'id': userId});
		userProfileResponse.$promise.then(function() {
			var name = (userProfileResponse.name) ? userProfileResponse.name : userId;
			message.name = name;
		});
	}

	/*function loadLastMessages() {
	 
	 
	 lastMessagesPageNumber += 1;
	 
	 messagesResponse.$promise.then(function() {
	 messagesCount = messagesResponse.length;
	 var messages = messagesResponse.messages;
	 for (var i in messages) {
	 messages[i].creationDate = formatDate(messages[i].creationDate);
	 $scope.messageHistory.unshift(messages[i]);
	 }
	 
	 $timeout(function() {
	 var block = document.getElementById("chat-history");
	 if(!block) {
	 return;
	 }
	 if (scrollLoad) {
	 if((messagesCount - (pageNumber - 1) * 15) < 15) {
	 block.scrollTop = 0;
	 } else {
	 block.scrollTop = block.scrollHeight / pageNumber;
	 }
	 } else {
	 block.scrollTop = block.scrollHeight;
	 }
	 loadOnScroll = true;
	 });
	 console.log(JSON.stringify($scope.messageHistory))
	 
	 });
	 }*/


	$scope.goToPartnerPage = function(partnerId) {
		window.location.href = window.context + "#/users/" + partnerId;
	};

	$scope.goToCurrentUserPage = function() {
		window.location.href = window.context + "#/users/" + $scope.currentUserId;
	};

	var currentUserProfileResponse = usersProfileResource.getShortById({'id': currentUserId});
	currentUserProfileResponse.$promise.then(function() {
		$scope.currentUserName = (currentUserProfileResponse.name) ? currentUserProfileResponse.name : currentUserId;
		console.log("USER: " + $scope.currentUserName + " PARTNER: " + $scope.partnerName)
	});
	/*var partnerProfileResponse = usersProfileResource.getShortById({'id': partnerId});
	 partnerProfileResponse.$promise.then(function() {
	 $scope.partnerName = (partnerProfileResponse.name) ? partnerProfileResponse.name : partnerId;
	 });*/
	//loadMessages();
	//console.log(currentUserId + " " + partnerId)
	//var response = messagesResource.getLaterThan({date: });
	$scope.send = function(message) {
		chatService.sendMessage($scope.partnerId, message);
		angular.element("#chat-text-area").val('');
		var messageObject = {};
		messageObject.creationDate = formatDate(new Date());
		messageObject.message = message;
		messageObject.partnerId = $scope.partnerId;
		messageObject.userId = currentUserId;

		console.log(JSON.stringify(messageObject))
		//$scope.messageHistory.push(messageObject);

		$timeout(function() {
			$scope.$apply(function() {
				$scope.message = "";
				loadLastMessages(null, function() {
					$timeout(function() {
						var block = document.getElementById("chat-history");
						if (block) {
							block.scrollTop = block.scrollHeight;
						}
					});
				});
			});

		});

	};

	function loadLastMessages(value, callback) {
		if ($scope.messageHistory.length === 0) {
			var response = messagesResource.getMessagesByPartnerId({partnerId: $scope.partnerId});
		} else {
			var lastMessageCreationDateStr = $scope.messageHistory[$scope.messageHistory.length - 1].creationDate;
			var response = messagesResource.getLaterThan({creationDate: lastMessageCreationDateStr, partnerId: $scope.partnerId});
		}
		response.$promise.then(function() {
			//console.log(JSON.stringify(response.messages))
			response.messages = response.messages.reverse();
			for (var i in response.messages) {
				response.messages[i].creationDate = formatDate(response.messages[i].creationDate);
				$scope.messageHistory.push(response.messages[i]);
			}
			if (callback) {
				callback();
			}
		});
	}

	function formatDate(date) {
		return $.format.date(date, "dd/MM/yy HH:mm:ss");
	}

	$scope.openDialog = function(partnerId) {
		console.log(partnerId)
		$scope.isDialog = true;
		pageNumber = 0;
		$scope.partnerId = partnerId;
		$scope.partnerName = "";
		$scope.partnerImgUrl = window.context + "webapi/photos/" + partnerId;
		$scope.partnerUrl = window.context + "#/users/" + partnerId;
		$scope.messageHistory.splice(0, $scope.messageHistory.length);
		var partnerProfileResponse = usersProfileResource.getShortById({'id': partnerId});
		partnerProfileResponse.$promise.then(function() {
			$scope.partnerName = (partnerProfileResponse.name) ? partnerProfileResponse.name : partnerId;
		});
		loadMessages(partnerId);

		angular.element("#chat-history").scroll(function() {
			//console.log("SCROLL: " + angular.element("#chat-history").scrollTop() + " " + angular.element("#chat-history").height() + " " + angular.element("#chat-history").parent().height())
			if (angular.element("#chat-history").scrollTop() === 0) {
				$timeout(function() {
					if ((pageNumber > 30) || (messagesCount <= pageNumber * 15)) {
						return;
					}
					if (loadOnScroll === false) {
						return;
					}
					loadOnScroll = false;
					loadMessages(partnerId, true);
				}, 500);
			}
		});


		secondsInWaitingTimerId = $interval(loadLastMessages, 4000);
		if (usersSecondsInWaitingTimerId) {
			$interval.cancel(usersSecondsInWaitingTimerId);
		}
	}

	$scope.goToLastMessages = function() {
		console.log("LAST")
		$scope.isDialog = false;
		if (secondsInWaitingTimerId) {
			$interval.cancel(secondsInWaitingTimerId);
		}
		usersSecondsInWaitingTimerId = $interval(loadUsersLastMessages, 4000);
		$scope.partnerId = "";
	}

	function loadMessages(partnerId, scrollLoad) {
		var messagesResponse = messagesResource.getMessagesByPartnerId({partnerId: partnerId, firstPage: pageNumber, pageSize: 15});
		pageNumber += 1;
		messagesResponse.$promise.then(function() {
			messagesCount = messagesResponse.length;
			var messages = messagesResponse.messages;
			for (var i in messages) {
				messages[i].creationDate = formatDate(messages[i].creationDate);
				$scope.messageHistory.unshift(messages[i]);
			}

			$timeout(function() {
				var block = document.getElementById("chat-history");
				if (scrollLoad) {
					if ((messagesCount - (pageNumber - 1) * 15) < 15) {
						block.scrollTop = 0;
					} else {
						block.scrollTop = block.scrollHeight / pageNumber;
					}
				} else {
					block.scrollTop = block.scrollHeight;
				}
				loadOnScroll = true;
			});
			console.log(JSON.stringify($scope.messageHistory))

		});
	}

	$scope.$on("$destroy", function() {
		if (secondsInWaitingTimerId) {
			$interval.cancel(secondsInWaitingTimerId);
		}
		if (usersSecondsInWaitingTimerId) {
			$interval.cancel(usersSecondsInWaitingTimerId);
		}
	});
});