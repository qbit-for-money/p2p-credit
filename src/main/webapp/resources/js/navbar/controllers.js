var navbarModule = angular.module("navbar");

navbarModule.controller("NavbarController", function($scope, $location, navbarService, userService, chatService) {
	$scope.isNavbarCollapsed = true;
	$scope.unreadMessagesCount = 0;
	$scope.message = "";
	$scope.send = function(message) {
		chatService.sendMessageForAdmin(message);
	};
	
	$('.sliding-panel').tabSlideOut({
		tabHandle: '.handle',
		pathToTabImage: 'resources/img/comp_button3.png',
		imageHeight: '92px',
		imageWidth: '17px',
		tabLocation: 'right',
		speed: 300,
		action: 'click',
		topPos: '80%',
		fixedPosition: true
	});
	$("#user-chat-form").removeClass("invisible");
	if (typeof String.prototype.startsWith !== 'function') {
		String.prototype.startsWith = function(str) {
			return this.indexOf(str) === 0;
		};
	}
	$scope.isActive = function(viewLocation) {
		return $location.path().startsWith(viewLocation);
	};

	$scope.goToProfile = function() {
		navbarService.goToProfile();
	};

	$scope.goToOrderCreating = function() {
		navbarService.goToOrderCreating();
	};

	$scope.goToOrders = function() {
		navbarService.goToOrders();
	};

	$scope.goToChat = function() {
		navbarService.goToChat();
	}
});