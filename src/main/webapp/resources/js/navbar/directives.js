var navbarModule = angular.module("navbar");


navbarModule.directive('ngFocus', ['$parse', function($parse) {
		return function(scope, element, attr) {
			var fn = $parse(attr['ngFocus']);
			element.bind('focus', function(event) {
				scope.$apply(function() {
					fn(scope, {$event: event});
				});
			});
		};
	}]);

navbarModule.directive('ngBlur', ['$parse', function($parse) {
		return function(scope, element, attr) {
			var fn = $parse(attr['ngBlur']);
			element.bind('blur', function(event) {
				scope.$apply(function() {
					fn(scope, {$event: event});
				});
			});
		};
	}]);

navbarModule.directive('uiDropdown', function($compile, $timeout) {
	return {
		restrict: 'E',
		scope: {
			//doSelect: '&selectVal',
			onChangeInput: '&onChangeInput',
			items: '=dropdownData',
			selectedItem: '=preselectedItem',
			placeholder: '=placeholder',
			onEnter: '&onEnter',
			inputValue: '=inputValue',
			elementId: '=elementId',
			isDropdownInput: '=isDropdownInput'
		},
		link: function(scope, element, attrs) {
			//scope.bSelectedItem = scope.selectedItem;
			scope.inputText = "";
			var html = '';
			html += '<div class="input-group" id="{{elementId}}"><input type="number" class="form-control" ng-model="inputValue" ng-change="onChangeInput()" placeholder="{{placeholder}}">';
			html += '<div class="input-group-btn ui-dropdown-button"><button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown"><span class="li-item">Action</span> <b class="caret"></b></button>';

			html += '<ul class="dropdown-menu dropdown-menu-left" style="right: 0; left: auto;"><li ng-repeat="item in items"><a tabindex="-1" data-ng-click="selectVal(item)" style="cursor:pointer">{{item}}</a></li><li ng-hide="!isDropdownInput"><input type="text" class="dropdown-input" placeholder=" Other.." ng-model="inputText"/></li></ul></div>';
			element.append($compile(html)(scope));

			if (scope.isDropdownInput === true) {
				angular.element(element).find('.dropdown-input').on("click", function(e) {
					e.stopPropagation();
				});

				angular.element(element).find('.dropdown-input').on("blur", function(e) {



					$timeout(function() {
						scope.$apply(function() {
							scope.selectedItem = scope.inputText;
							scope.inputText = "";
							if (scope.selectedItem !== "") {
								$('button.dropdown-toggle', element).html('<b class="caret"></b> <span class="li-item">' + scope.selectedItem + '</span>');
							}
						});
						scope.onChangeInput();
					});
				});
			}


			scope.selectVal = function(item) {
				if (!angular.element(scope.elementId + " .input-text").is(":focus")) {
					switch (attrs.menuType) {
						case "button":
							$('button.button-label', element).html(item);
							break;
						default:
							$('button.dropdown-toggle', element).html('<b class="caret"></b> <span class="li-item">' + item + '</span>');
							break;
					}
				}
				
				$timeout(function() {
					scope.$apply(function() {
						scope.selectedItem = item;
					});
					scope.onChangeInput();
				});

				if (scope.doSelect) {
					scope.doSelect({
						selectedVal: item
					});
				}

			};

			scope.selectVal(scope.selectedItem);
			element.bind("keypress", function(event) {
				if (event.which === 13) {
					scope.onEnter();
					event.preventDefault();
				}
			});
		}
	};
});