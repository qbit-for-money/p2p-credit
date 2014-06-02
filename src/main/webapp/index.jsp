<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html ng-app="main">
	<head>
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<meta name="description" content="With this service you can give and take credit">

		<link rel="stylesheet" type="text/css" href="resources/lib/bootstrap/core/css/bootstrap.min.css">

		<link rel="stylesheet" type="text/css" href="resources/lib/angular/loading-bar.min.css">
		<link rel="stylesheet" type="text/css" href="resources/lib/imgareaselect/css/imgareaselect-animated.css" />

		<link rel="stylesheet" href="resources/lib/jqwidgets/jqwidgets/styles/jqx.base.css" type="text/css" />
		<link rel="stylesheet" href="resources/lib/jqwidgets/jqwidgets/styles/jqx.arctic.css" type="text/css" />
		<link rel="stylesheet" href="resources/lib/jqwidgets/jqwidgets/styles/jqx.bootstrap.css" type="text/css" />

		<link rel="stylesheet" type="text/css" href="resources/css/common.css">
		<link rel="stylesheet" type="text/css" href="resources/css/auth-dialog.css">
		<link rel="stylesheet" type="text/css" href="resources/css/order.css">
		<link rel="stylesheet" type="text/css" href="resources/css/user.css">
		<script type="text/javascript" src="resources/lib/ckeditor/ckeditor.js"></script>
	</head>
	<body class="default">
		<div class="container" ng-if="user === null" ng-controller="AuthDialogOpeningController">
		</div>
		<div class="wrapper">
			<%@ include file="WEB-INF/jspf/nav.jspf" %>
			<div class="container" ng-if="user">
				<div ng-view></div>
			</div>
			<div class="push"></div>
		</div>

		<div class="footer navbar-default">
			<div class="pluso" data-background="#ebebeb" data-options="medium,square,line,horizontal,nocounter,theme=07" data-services="google,twitter,facebook,vkontakte,odnoklassniki,moimir" data-url="https://www.bitgates.com" data-title="p2p-credit"></div>
			<p>&copy; Bitgates 2014</p>
		</div>

		<script type="text/javascript" src="resources/lib/pluso/pluso.js"></script>
		<script type="text/javascript" src="resources/lib/jquery/jquery-2.1.0.min.js"></script>
		<script type="text/javascript" src="resources/lib/jquery/jquery.mask.min.js"></script>
		<script type="text/javascript" src="resources/lib/angular/angular.min.js"></script>
		<script type="text/javascript" src="resources/lib/angular/angular-route.min.js"></script>
		<script type="text/javascript" src="resources/lib/angular/angular-resource.min.js"></script>
		<script type="text/javascript" src="resources/lib/angular/angular-touch.min.js"></script>
		<script type="text/javascript" src="resources/lib/bootstrap/core/js/ui-bootstrap-tpls-0.10.0.min.js"></script>
		<script type="text/javascript" src="resources/lib/angular/loading-bar.min.js"></script>
		<script type="text/javascript" src="resources/lib/bootstrap/accordion/bootstrap-collapse.js"></script>
		<script type="text/javascript" src="resources/lib/imgareaselect/scripts/jquery.imgareaselect.pack.js"></script>

		<script type="text/javascript" src="resources/lib/jqwidgets/jqwidgets/jqxcore.js"></script>
		<script type="text/javascript" src="resources/lib/jqwidgets/jqwidgets/jqxdata.js"></script>
		<script type="text/javascript" src="resources/lib/jqwidgets/jqwidgets/jqxbuttons.js"></script>
		<script type="text/javascript" src="resources/lib/jqwidgets/jqwidgets/jqxscrollbar.js"></script>
		<script type="text/javascript" src="resources/lib/jqwidgets/jqwidgets/jqxdatatable.js"></script>
		<script type="text/javascript" src="resources/lib/jqwidgets/jqwidgets/jqxchart.js"></script>
		<script type="text/javascript" src="resources/lib/jqwidgets/jqwidgets/jqxlistbox.js"></script> 
		<script type="text/javascript" src="resources/lib/jqwidgets/jqwidgets/jqxdropdownlist.js"></script> 
		<script type="text/javascript" src="resources/lib/jqwidgets/jqwidgets/jqxtooltip.js"></script> 
		<script type="text/javascript" src="resources/lib/jqwidgets/jqwidgets/jqxinput.js"></script> 
		<!--script type="text/javascript" src="resources/lib/jqwidgets/scripts/demos.js"></script-->

		<script type="text/javascript">
			// Global constants
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

		<script type="text/javascript" src="resources/js/order/controllers.js"></script>

		<script type="text/javascript" src="resources/lib/imgareaselect/scripts/upload.js"></script>
		<script type="text/javascript" src="resources/js/user-profile/resources.js"></script>
		<script type="text/javascript" src="resources/js/user-profile/controllers.js"></script>
		<script type="text/javascript" src="resources/js/user-profile/directives.js"></script>

		<script type="text/javascript" src="resources/js/user-photo/controllers.js"></script>

		<script type="text/javascript" src="resources/js/users/controllers.js"></script>
	</body>
</html>
