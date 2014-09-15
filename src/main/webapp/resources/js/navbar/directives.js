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

navbarModule.directive('ngEnterKey', ['$parse', function($parse) {
		return function(scope, element, attr) {
			var fn = $parse(attr['ngEnterKey']);
			element.bind("keypress", function(event) {
				if (event.which === 13) {
					scope.$apply(function() {
						fn(scope, {$event: event});
					});
				}
			});
		};
	}]);

navbarModule.directive('ngClickOut', ['$parse', function($parse) {
		return function(scope, element, attr) {
			var fn = $parse(attr['ngClickOut']);
			element.bind("mouseout", function(event) {
				//console.log("OUT " + $(element).find("button").attr("id"));
				//var buttonId = $(element).find("button").attr("id");
				console.log("& " + $(element).find("button").attr("id"))
				$(document).bind("click", function(event) {
					//console.log("CLICK " + this);
					event = event || window.event;
					var el = event.target || event.srcElement;
					console.log(el.id + " " + $(element).find("button").attr("id"));
					if ($(element).find("button").attr("id") !== el.id) {
						fn(scope, {$event: event});
						$(document).unbind('click');
					}
				});
			});
		};
	}]);

/*navbarModule.directive('ngBlur', ['$parse', function($parse) {
 return function(scope, element, attr) {
 //var fn = $parse(attr['ngBlur']);
 //element.bind('blur', function(event) {
 //scope.$apply(function() {
 //fn(scope, {$event: event});
 //});
 //});
 };
 }]);*/

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
			html += '<div class="input-group" id="{{elementId}}"><input type="number" min="0" class="form-control" ng-model="inputValue" ng-change="onChangeInput()" placeholder="{{placeholder}}">';
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

navbarModule.directive('dropdown', function($compile, $timeout) {
	return {
		restrict: 'E',
		scope: {
			items: "=items",
			title: "=dropdownTitle",
			dClass: "=dClass",
			dStyle: "=dStyle",
			doSelect: '&doSelect'
		},
		link: function(scope, element, attrs) {
			scope.selectedItem = 0;
			var html = '<div class="dropdown">{{title}} <button class="btn btn-default dropdown-toggle {{dClass}}" style="{{dStyle}}" role="button" data-toggle="dropdown"  href="javascript:;">Dropdown<b class="caret"></b></button>';
			html += '<ul class="dropdown-menu"><li ng-repeat="item in items"><a tabindex="-1" data-ng-click="selectVal(item)" style="cursor:pointer">{{item.name}}</a></li></ul></div>';
			element.append($compile(html)(scope));
			for (var i = 0; i < scope.items.length; i++) {
				if (scope.items[i].id === scope.selectedItem) {
					scope.bSelectedItem = scope.items[i];
					break;
				}
			}
			
			scope.selectVal = function(item) {
				$('button.dropdown-toggle', element).html('<b class="caret"></b> ' + item.name);
				$timeout(function() {
					scope.$apply(function() {
						var expressionHandler = scope.doSelect();
						scope.selectedItem = item.id;
						expressionHandler({
							item: item
						});
					});
				});

			};
			scope.selectVal(scope.bSelectedItem);
		}
	};
});
