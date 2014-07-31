var navbarModule = angular.module("navbar");

navbarModule.factory("navbarService", function($rootScope, authService) {
	function goToProfile() {
		if (!$rootScope.user || ($rootScope.user.publicKey.indexOf("@") === -1)) {
			authService.openAuthDialog(false, true, "profile");
		} else {
			window.location.href = window.context + "#/users/" + $rootScope.user.publicKey;
		}
	};
	
	function goToOrderCreating() {
		if (!$rootScope.user || ($rootScope.user.publicKey.indexOf("@") === -1)) {
			authService.openAuthDialog(false, true, window.context + "#/create-order");
		} else {
			window.location.href = window.context + "#/create-order";
		}
	};
	
	function goToOrders() {
		if (!$rootScope.user || ($rootScope.user.publicKey.indexOf("@") === -1)) {
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
});


