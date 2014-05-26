var userProfileModule = angular.module("main");

userProfileModule.directive('ngFocus', ['$parse', function($parse) {
		return function(scope, element, attr) {
			var fn = $parse(attr['ngFocus']);
			element.bind('focus', function(event) {
				scope.$apply(function() {
					fn(scope, {$event: event});
				});
			});
		};
	}]);

userProfileModule.directive('ngBlur', ['$parse', function($parse) {
		return function(scope, element, attr) {
			var fn = $parse(attr['ngBlur']);
			element.bind('blur', function(event) {
				scope.$apply(function() {
					fn(scope, {$event: event});
				});
			});
		};
	}]);

