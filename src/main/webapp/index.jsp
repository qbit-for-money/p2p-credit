<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html ng-app="main">
	<head>
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />

		<link rel="stylesheet" type="text/css" href="resources/lib/bootstrap/core/css/bootstrap.min.css">

		<link rel="stylesheet" type="text/css" href="resources/lib/angular/loading-bar.min.css">

		<link rel="stylesheet" type="text/css" href="resources/css/common.css">
		<link rel="stylesheet" type="text/css" href="resources/css/auth-dialog.css">
	</head>
	<body>
		<div class="container" ng-if="user === null" ng-controller="AuthDialogOpeningController">
		</div>
		<div class="wrapper">
			<div class="container">
				<div ng-view></div>
			</div>
			<div class="push"></div>
		</div>
		<div class="footer navbar-default">&copy; Bitgates 2014</div>

		<script type="text/javascript" src="resources/lib/jquery/jquery-2.1.0.min.js"></script>
		<script type="text/javascript" src="resources/lib/jquery/jquery.mask.min.js"></script>
		<script type="text/javascript" src="resources/lib/angular/angular.min.js"></script>
		<script type="text/javascript" src="resources/lib/angular/angular-route.min.js"></script>
		<script type="text/javascript" src="resources/lib/angular/angular-resource.min.js"></script>
		<script type="text/javascript" src="resources/lib/angular/angular-touch.min.js"></script>
		<script type="text/javascript" src="resources/lib/bootstrap/core/js/ui-bootstrap-tpls-0.10.0.min.js"></script>
		<script type="text/javascript" src="resources/lib/angular/loading-bar.min.js"></script>
		<script type="text/javascript">
			window.context = "${context}";
		</script>
		<script type="text/javascript" src="resources/js/app.js"></script>

		<script type="text/javascript" src="resources/js/common/resources.js"></script>
		<script type="text/javascript" src="resources/js/common/services.js"></script>
		<script type="text/javascript" src="resources/js/common/filters.js"></script>

		<script type="text/javascript" src="resources/js/user/resources.js"></script>
		<script type="text/javascript" src="resources/js/user/services.js"></script>
		<script type="text/javascript" src="resources/js/user/controllers.js"></script>

		<script type="text/javascript" src="resources/js/auth/controllers.js"></script>
		<script type="text/javascript" src="resources/js/auth/resources.js"></script>
	</body>
</html>
