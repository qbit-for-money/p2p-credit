var userProfileModule = angular.module("main");

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
			ngModel: '=', data: '=', validate: '&'
		},
		link: function(scope, element, attrs, ngModel) {
			var endDate = new Date();
			endDate.setYear(1900 + endDate.getYear() - 5);
			element.datepicker({
				format: "dd/mm/yyyy",
				showMeridian: false,
				autoclose: true,
				todayBtn: true,
				todayHighlight: true
			}).on('changeDate', function(ev) {
				angular.element("body > div.datepicker.dropdown-menu").css("display","none");
				scope.$apply(function() {
					scope.validate();
				});
			});
		}
	};
});

userProfileModule.directive('embedSrc', function() {
	return {
		restrict: 'A',
		link: function(scope, element, attrs) {
			var current = element;
			attrs.$observe('embedSrc', function() {
				var clone = element
						.clone()
						.attr('src', attrs.embedSrc);
				current.replaceWith(clone);
				current = clone;
			});
		}
	};
});

userProfileModule.directive("linksList", function() {
	return {
		restrict: "E",
		scope: {links: "=", type: "=", current: "=", edited: "=", saveData: "&", editAttribute: "&"},
		templateUrl: "resources/html/user/links.html",
		link: function(scope, element, attrs, ngModelCtrl) {
			scope.newLink = {};
			scope.newLink.title = "";
			scope.newLink.link = "";
			if (scope.type === "phone") {
				scope.itemPlaceholder = "Phone";
				scope.title = "Phones";
			}
			if (scope.type === "social-link") {
				scope.itemPlaceholder = "Link";
				scope.title = "Social Networks";
			}
			if (scope.type === "video") {
				scope.title = "YouTube videos";
				scope.itemPlaceholder = "YouTube Link";
			}
			if (scope.type === "name") {
				scope.title = "Names";
				scope.itemPlaceholder = "Link";
			}

			scope.editItems = function() {
				if(scope.edited === true) {
					scope.saveData();
				}
				scope.editAttribute();
			};

			scope.isValidItem = function(link, isEditing) {
				if (!link.link || link.link === "") {
					return false;
				}
				if (scope.type === "phone" && (link.link.indexOf('_') !== -1)) {
					return false;
				}
				var urlregex = new RegExp("^(http|https)\://([a-zA-Z0-9\.\-]+(\:[a-zA-Z0-9\.&amp;%\$\-]+)*@)*((25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9])\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[1-9]|0)\.(25[0-5]|2[0-4][0-9]|[0-1]{1}[0-9]{2}|[1-9]{1}[0-9]{1}|[0-9])|([a-zA-Z0-9\-]+\.)*[a-zA-Z0-9\-]+\.(com|edu|gov|int|mil|net|org|biz|arpa|info|name|pro|aero|coop|museum|[a-zA-Z]{2}))(\:[0-9]+)*(/($|[a-zA-Z0-9\.\,\?\'\\\+&amp;%\$#\=~_\-]+))*$");
				if (((scope.type === "social-link") || (scope.type === "video") || (scope.type === "name")) && !urlregex.test(link.link)) {
					return false;
				}
				var count = 0;
				for (var i in scope.links) {
					if (scope.type === "video" && (scope.links[i].link === updateYouTubeLink(link.link))) {
						count++;
					} else if (scope.links[i].link === link.link) {
						count++;
					}
				}
				if (!isEditing && count > 0 || (isEditing && (isEditing === true) && (count > 1))) {
					return false;
				}
				return true;
			};

			function updateYouTubeLink(link) {
				link = link.replace("http:", "https:");
				link = link.replace("watch?v=", "v/");
				return link;
			}

			scope.addLink = function() {
				if (!scope.isValidItem(scope.newLink)) {
					return;
				}

				var newLink = {};
				newLink.title = scope.newLink.title;
				newLink.link = scope.newLink.link;
				newLink.id = "id_" + Math.floor((Math.random() * 1000000) + 1);
				if (scope.type === "video") {
					newLink.link = updateYouTubeLink(newLink.link);
				}
				scope.links.push(newLink);
				scope.newLink.title = "";
				scope.newLink.link = "";
				scope.newLink.edited = false;
				scope.saveData();
			};

			scope.removeLink = function(id) {
				for (var i = 0; i < scope.links.length; i++) {
					if (scope.links[i].id === id) {
						scope.links.splice(i, 1);
						i--;
					}
				}
			};

			scope.editLink = function(id) {
				for (var i = 0; i < scope.links.length; i++) {
					if (scope.links[i].id === id) {
						if (!scope.links[i].edited || (scope.links[i].edited === false)) {
							scope.links[i].edited = true;
							return;
						} else {
							if (scope.isValidItem(scope.links[i], true)) {
								scope.links[i].edited = false;
							}
							return;
						}
					}
				}
			};
		}
	};
});

