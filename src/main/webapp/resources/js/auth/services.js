var userModule = angular.module("captcha-auth");

userModule.factory("authService", function($modal) {
	return {
		openAuthDialog: function(isModal, isNotCaptcha, redirectUrl) {
			var backdrop;
			if (isModal) {
				backdrop = "static";
			} else {
				backdrop = "none";
			}

			$modal.open({
				controller: "AuthDialogController",
				templateUrl: "resources/html/auth/dialog.html",
				windowClass: "auth-dialog",
				backdrop: backdrop, keyboard: false, backdropClick: false, dialogFade: false,
				resolve: {
					isNotCaptcha: function() {
						return isNotCaptcha;
					},
					redirectUrl: function() {
						return redirectUrl;
					}
				}
			});
		}
	};
});