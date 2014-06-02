var userProfileModule = angular.module("user-profile");

userProfileModule.controller("UserPhotoController", function($scope, fileReader, usersProfileResource) {

	var MIN_HEIGHT = 400;
	var MIN_WIDTH = 300;
	var MAX_HEIGHT = 2000;
	var MAX_WIDTH = 2000;

	var resultX1 = 0;
	var resultX2 = MIN_WIDTH;
	var resultY1 = 0;
	var resultY2 = MIN_HEIGHT;

	$scope.getFile = function(file) {
		$scope.progress = 0;
		fileReader.readAsDataUrl(file, $scope)
			.then(function(result) {
				$scope.imageSrc = result;
				var imag = new Image();
				imag.src = $scope.imageSrc;
				if ((imag.width > MAX_WIDTH) || (imag.height > MAX_HEIGHT) || (imag.width < MIN_WIDTH) || (imag.height < MIN_HEIGHT)) {
					$scope.imageSrc = "";
					return;
				}
				setTimeout(function() {
					angular.element('#user-photo-change').imgAreaSelect({
						aspectRatio: '3:4',
						handles: true,
						minWidth: 100,
						x1: 0, y1: 0, x2: 100, y2: 150,
						onSelectEnd: function(img, selection) {
							var ratio = imag.width / angular.element("#user-photo-change").width();
							resultX1 = Math.floor(selection.x1 * ratio);
							resultX2 = Math.floor(selection.x2 * ratio);
							resultY1 = Math.floor(selection.y1 * ratio);
							resultY2 = Math.floor(selection.y2 * ratio);
							console.log((resultX2 - resultX1) + " " + (resultY2 - resultY1))
							resultX2 = (resultX2 - resultX1 > 800) ? (resultX1 + 800) : resultX2;
							resultY2 = (resultY2 - resultY1 > 800) ? (resultY1 + 800) : resultY2;
						}
					});
				}, 150);
			});
	};

	$scope.save = function() {

		var userPhotoRequest = {};
		userPhotoRequest.x1 = resultX1;
		userPhotoRequest.x2 = resultX2;
		userPhotoRequest.y1 = resultY1;
		userPhotoRequest.y2 = resultY2;

		var data = $scope.imageSrc.replace(/^data:image\/(png|jpg|jpeg);base64,/, "");
		userPhotoRequest.imageString = data;

		var userPhotoResponse = usersProfileResource.setUserPhoto({}, userPhotoRequest);

		userPhotoResponse.$promise.then(function() {
			setTimeout(function() {
				location.reload();
			}, 100);
		});
	};
	
	$scope.deletePhoto = function() {
		var userPhotoRequest = {};
		userPhotoRequest.imageString = "";
		
		var userPhotoResponse = usersProfileResource.setUserPhoto({}, userPhotoRequest);

		userPhotoResponse.$promise.then(function() {
			setTimeout(function() {
				location.reload();
			}, 100);
		});
	};

	$scope.$on("fileProgress", function(e, progress) {
		$scope.progress = progress.loaded / progress.total;
	});
});