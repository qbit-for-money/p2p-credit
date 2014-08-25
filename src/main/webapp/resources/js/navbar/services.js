var navbarModule = angular.module("navbar");

navbarModule.factory("navbarService", function($rootScope, authService) {
	function goToProfile() {
		if (!isNoCaptchaAuth()) {
			authService.openAuthDialog(false, true, "profile");
		} else {
			window.location.href = window.context + "#/users/" + $rootScope.user.publicKey;
		}
	};
	
	function goToOrderCreating() {
		if (!isNoCaptchaAuth()) {
			authService.openAuthDialog(false, true, window.context + "#/create-order");
		} else {
			window.location.href = window.context + "#/create-order";
		}
	};
	
	function goToOrders() {
		if (!isNoCaptchaAuth()) {
			authService.openAuthDialog(false, true, window.context + "#/orders");
		} else {
			window.location.href = window.context + "#/orders";
		}
	};
	
	return {
		goToProfile: goToProfile,
		goToOrderCreating: goToOrderCreating,
		goToOrders: goToOrders
	};
	
	function isNoCaptchaAuth() {
		return $rootScope.user && (($rootScope.user.publicKey.indexOf("@") !== -1) 
			|| ($rootScope.user.publicKey.indexOf("vk/") !== -1))
	}
});


