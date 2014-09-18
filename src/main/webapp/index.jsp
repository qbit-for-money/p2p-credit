<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html ng-app="main"><!--manifest="manifest.cache"-->
	<head>
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<meta name="description" content="With this service you can give and take credit">

		<link rel="stylesheet" type="text/css" href="resources/lib/bootstrap/core/css/bootstrap.min.css">
		<link rel="stylesheet" href="resources/lib/bootstrap/datepicker/css/datepicker.css" type="text/css" />

		<link rel="stylesheet" type="text/css" href="resources/lib/angular/loading-bar.min.css">
		<link rel="stylesheet" type="text/css" href="resources/lib/imgareaselect/css/imgareaselect-animated.css" />
		<link rel="stylesheet" type="text/css" href="resources/lib/select2-3.5.0/select2.css">
		<link rel="stylesheet" type="text/css" href="resources/lib/select2-3.5.0/select2-bootstrap.css">

		<link rel="stylesheet" type="text/css" href="resources/css/common.css">
		<link rel="stylesheet" type="text/css" href="resources/css/auth-dialog.css">
		<link rel="stylesheet" type="text/css" href="resources/css/order.css">
		<link rel="stylesheet" type="text/css" href="resources/css/user.css">
		<link rel="stylesheet" type="text/css" href="resources/css/chat.css">
	</head>
	<body class="default">
		<!--div class="container" ng-if="user === null" ng-controller="AuthDialogOpeningController">
		</div-->
		<div class="wrapper">
			<%@ include file="WEB-INF/jspf/nav.jspf" %>
			<div class="container">		
				<div ng-view></div>
			</div>
			<div class="push"></div>
		</div>

		<div class="footer navbar-default">
			<div class="pluso" data-background="#ebebeb" data-options="medium,square,line,horizontal,nocounter,theme=07" data-services="google,twitter,facebook,vkontakte,odnoklassniki,moimir" data-url="https://www.bitgates.com" data-title="p2p-credit"></div>
			<p>&copy; Yes!Money 2014</p>
		</div>

		<script type="text/javascript">
			// Global constants
			window.context = "${context}";

		</script>

		<script type="text/javascript" src="resources/lib/pluso/pluso.js"></script>
		<script type="text/javascript" src="resources/lib/tinymce/js/tinymce/tinymce.min.js"></script>
		<script type="text/javascript" src="resources/lib/jquery/jquery-2.1.0.min.js"></script>
		<script type="text/javascript" src="resources/lib/jquery/jquery.mask.min.js"></script>
		<script type="text/javascript" src="resources/lib/jquery/jquery.autopager-1.0.0.js"></script>
		<script type="text/javascript" src="resources/lib/jquery/jquery.tabslideout.v1.2.js"></script>
		<script type="text/javascript" src="resources/lib/select2-3.5.0/select2.min.js"></script>
		<script type="text/javascript" src="resources/lib/angular/angular.min.js"></script>
		<script type="text/javascript" src="resources/lib/angular/angular-route.min.js"></script>
		<script type="text/javascript" src="resources/lib/angular/angular-resource.min.js"></script>
		<script type="text/javascript" src="resources/lib/angular/angular-touch.min.js"></script>
		<script src="//angular-ui.github.io/bootstrap/ui-bootstrap-tpls-0.11.0.js"></script>
		<script type="text/javascript" src="resources/lib/angular/select2.js"></script>
		<script type="text/javascript" src="resources/lib/angular/loading-bar.min.js"></script>
		<script type="text/javascript" src="resources/lib/angular/angular-sanitize.min.js"></script>
		<script type="text/javascript" src="resources/lib/bootstrap/core/js/ui-bootstrap-tpls-0.10.0.min.js"></script>
		<script type="text/javascript" src="resources/lib/bootstrap/accordion/bootstrap-collapse.js"></script>
		<script type="text/javascript" src="resources/lib/bootstrap/datepicker/js/bootstrap-datepicker.js"></script> 
		<script type="text/javascript" src="resources/lib/bootstrap/tab/bootstrap-tab.js"></script> 
		<script type="text/javascript" src="resources/lib/imgareaselect/scripts/jquery.imgareaselect.pack.js"></script> 
		<script type="text/javascript" src="resources/lib/jquery/jquery-dateFormat.min.js"></script> 

		<script type="text/javascript" src="resources/js/app.js"></script>

		<script type="text/javascript" src="resources/js/common/resources.js"></script>
		<script type="text/javascript" src="resources/js/common/services.js"></script>
		<script type="text/javascript" src="resources/js/common/filters.js"></script>

		<script type="text/javascript" src="resources/js/user/resources.js"></script>
		<script type="text/javascript" src="resources/js/user/services.js"></script>
		<script type="text/javascript" src="resources/js/user/controllers.js"></script>

		<script type="text/javascript" src="resources/js/auth/controllers.js"></script>
		<script type="text/javascript" src="resources/js/auth/resources.js"></script>
		<script type="text/javascript" src="resources/js/auth/services.js"></script>

		<script type="text/javascript" src="resources/js/order/controllers.js"></script>
		<script type="text/javascript" src="resources/js/order/resources.js"></script>
		<script type="text/javascript" src="resources/js/orders-table/controllers.js"></script>

		<script type="text/javascript" src="resources/js/create-order/controllers.js"></script>

		<script type="text/javascript" src="resources/lib/imgareaselect/scripts/upload.js"></script>

		<script type="text/javascript" src="resources/js/user-profile/resources.js"></script>
		<script type="text/javascript" src="resources/js/user-profile/controllers.js"></script>
		<script type="text/javascript" src="resources/js/user-profile/directives.js"></script>
		<script type="text/javascript" src="resources/js/user-profile/services.js"></script>

		<script type="text/javascript" src="resources/js/user-orders-table/controllers.js"></script>
		<script type="text/javascript" src="resources/js/user-orders-table/directives.js"></script>

		<script type="text/javascript" src="resources/js/navbar/controllers.js"></script>
		<script type="text/javascript" src="resources/js/navbar/directives.js"></script>
		<script type="text/javascript" src="resources/js/navbar/services.js"></script>

		<script type="text/javascript" src="resources/js/like/resources.js"></script>
		<script type="text/javascript" src="resources/js/like/directives.js"></script>

		<script type="text/javascript" src="resources/js/short-search/controllers.js"></script>

		<div ng-include src="'resources/html/templates/templates.html'"></div>

		<script type="text/javascript" src="resources/js/common/tables-common.js"></script>
		
		<script type="text/javascript" src="resources/js/chat/controllers.js"></script>
		<script type="text/javascript" src="resources/js/chat/services.js"></script>
		<script type="text/javascript" src="resources/js/chat/resources.js"></script>
	</body>
</html>
