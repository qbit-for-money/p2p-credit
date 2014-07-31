var navbarModule = angular.module("navbar");

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
			elementId: '=elementId'
		},
		link: function(scope, element, attrs) {
			scope.bSelectedItem = scope.selectedItem;
			var html = '';
			html += '<div class="input-group" id="{{elementId}}"><input type="text" class="form-control" ng-model="inputValue" ng-change="onChangeInput()" placeholder="{{placeholder}}">';
			html += '<div class="input-group-btn ui-dropdown-button"><button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown">Action <span class="caret"></span></button>'

			html += '<ul class="dropdown-menu dropdown-menu-right"><li ng-repeat="item in items"><a tabindex="-1" data-ng-click="selectVal(item)" style="cursor:pointer">{{item}}</a></li></ul></div>';
			element.append($compile(html)(scope));

			scope.selectVal = function(item) {
				switch (attrs.menuType) {
					case "button":
						$('button.button-label', element).html(item);
						break;
					default:
						$('button.dropdown-toggle', element).html('<b class="caret"></b> <span class="item">' + item + '</span>');
						break;
				}
				scope.selectedItem = item;
				if (scope.doSelect) {
					scope.doSelect({
						selectedVal: item
					});
				}
			};

			scope.selectVal(scope.bSelectedItem);
			element.bind("keypress", function(event) {
				if (event.which === 13) {
					scope.onEnter();
					event.preventDefault();
				}
			});
		}
	};
});