var navbarModule = angular.module("navbar");

navbarModule.factory("navbarService", function($rootScope, authService) {
	function goToProfile() {
		if (isCaptchaAuth()) {
			window.localStorage.setItem("URL", "profile");
			authService.openAuthDialog(false, true, "profile");
		} else {
			window.location.href = window.context + "#/users/" + $rootScope.currentUserAltId;
		}
	};
	
	function goToOrderCreating() {
		if (isCaptchaAuth()) {
			window.localStorage.setItem("URL", window.context + "#/create-order");
			authService.openAuthDialog(false, true);
		} else {
			window.location.href = window.context + "#/create-order";
		}
	};
	
	function goToOrders() {
		if (isCaptchaAuth()) {
			window.localStorage.setItem("URL", window.context + "#/orders");
			authService.openAuthDialog(false, true);
		} else {
			window.location.href = window.context + "#/orders";
		}
	};
	
	function goToChat() {
		if (isCaptchaAuth()) {
			authService.openAuthDialog(false, true, window.context + "#/orders");
		} else {
			window.location.href = window.context + "#/messages";
		}
	}
	
	return {
		goToProfile: goToProfile,
		goToOrderCreating: goToOrderCreating,
		goToOrders: goToOrders,
		goToChat: goToChat
	};
	
	function isCaptchaAuth() {
		return !($rootScope.user && (($rootScope.user.publicKey.indexOf("@") !== -1) 
			|| ($rootScope.user.publicKey.indexOf("vk-") !== -1)
			|| ($rootScope.user.publicKey.indexOf("fb-") !== -1)))
	}
});


