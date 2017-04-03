<!DOCTYPE html>
<html>
	<head>
		@include('layouts/header')
		@yield('scripts')
	</head>
	<body>
		 	@include('layouts/sidebar')
		<div class="pull-left request">
			
		</div>
		<br>
		<div class="pull-left main-content">
		 	@yield('content')
		</div>
	</body>
	@include('layouts/footer')
	<?php 
		function current_page($urltest)
		{
			return request()->path() == $urltest;
		}
	?>
</html>