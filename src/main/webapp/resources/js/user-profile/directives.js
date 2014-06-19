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

userProfileModule.directive("ngFileSelect", function() {
	return {
		link: function($scope, el) {
			el.bind("change", function(e) {
				$scope.file = (e.srcElement || e.target).files[0];
				$scope.getFile($scope.file);
			});
		}
	};
});

userProfileModule.directive('dateTimePicker', function() {
	return {
		scope: {
			ngModel: '='
		},
		link: function(scope, element, attrs) {
			var endDate = new Date();
			endDate.setYear(1900 + endDate.getYear() - 5);
			element.datepicker({
				format: "dd/mm/yyyy",
				showMeridian: false,
				autoclose: true,
				todayBtn: true,
				todayHighlight: true
			});
		}
	};
});

