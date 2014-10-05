var orderModule = angular.module("order");

orderModule.directive('bsDropdown', function($compile, $modal, ordersResource) {
	return {
		restrict: 'E',
		scope: {
			doSelect: '&selectVal',
			orderId: '=orderId',
			openCommentDialog: '&openCommentDialog',
			doFunction: '&doFunction'
		},
		link: function(scope, element, attrs) {
			scope.items = [{
					id: 0,
					name: "Успешна"
				}, {
					id: 1,
					name: "Не успешна"
				}, {
					id: 2,
					name: "Арбитраж"
				}];
			scope.selectedItem = 0;
			var html = '';
			switch (attrs.menuType) {
				case "button":
					html += '<div class="btn-group"><button class="btn button-label btn-default" ng-click="openCommentDialog()">Action</button><button class="btn btn-default dropdown-toggle" data-toggle="dropdown"><span class="caret"></span></button>';
					break;
				default:
					html += '<div class="dropdown"><a class="dropdown-toggle" role="button" data-toggle="dropdown"  href="javascript:;">Dropdown<b class="caret"></b></a>';
					break;
			}
			html += '<ul class="dropdown-menu"><li ng-repeat="item in items"><a tabindex="-1" data-ng-click="selectVal(item)" style="cursor:pointer">{{item.name}}</a></li></ul></div>';
			element.append($compile(html)(scope));
			for (var i = 0; i < scope.items.length; i++) {
				if (scope.items[i].id === scope.selectedItem) {
					scope.bSelectedItem = scope.items[i];
					break;
				}
			}
			scope.selectVal = function(item) {
				switch (attrs.menuType) {
					case "button":
						$('button.button-label', element).html(item.name);
						break;
					default:
						$('a.dropdown-toggle', element).html('<b class="caret"></b> ' + item.name);
						break;
				}
				scope.selectedItem = item.id;
				scope.doSelect({
					selectedVal: item.id
				});
			};

			scope.openCommentDialog = function() {
				var status = scope.items[scope.selectedItem].name;
				if (status === "Не успешна") {
					status = "NOT_SUCCESS";
				}
				if (status === "Успешна") {
					status = "SUCCESS";
				}
				if (status === "Арбитраж") {
					status = "ARBITRATION";
				}

				var modalInstance = $modal.open({
					controller: "ChangeStatusDialogController",
					templateUrl: "resources/html/order/comment-dialog.html",
					windowClass: "auth-dialog",
					backdrop: "none", keyboard: false, backdropClick: false, dialogFade: false,
					resolve: {
						addResponse: function() {
							return scope.saveStatus;
						},
						orderId: function() {
							return scope.orderId;
						},
						status: function() {
							return status;
						}
					}
				});
				modalInstance.result.then(function() {
				},
						function() {
							angular.element("#contentorders-table > div.jqx-grid-content.jqx-grid-content-bootstrap.jqx-widget-content.jqx-widget-content-bootstrap > div.jqx-enableselect.jqx-widget-content.jqx-widget-content-bootstrap").removeClass("static-position");
							angular.element("#contentuser-orders-table > div.jqx-grid-content.jqx-grid-content-bootstrap.jqx-widget-content.jqx-widget-content-bootstrap > div.jqx-enableselect.jqx-widget-content.jqx-widget-content-bootstrap").removeClass("static-position");
						});
			};

			scope.saveStatus = function(orderId, comment, status) {
				var response = {};
				response.orderId = orderId;
				response.status = status;
				response.comment = comment;
				var orderResponse = ordersResource.changeStatus({id : orderId}, response);
				orderResponse.$promise.then(function() {
					scope.doFunction();
				});
			};

			scope.selectVal(scope.bSelectedItem);
		}
	};
});

