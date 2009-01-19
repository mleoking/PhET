<?php
	
	// used for database tests
	
	include("db_util.php");
	$link = setup_mysql();

	
	
	
	function simple_graph($data, $height, $blockwidth, $forecolor, $backcolor) {
		$str = "";
		$str .= "<div style=\"width: " . (sizeof($data) * $blockwidth) . "px; height: " . $height . "px; background-color: #" . $forecolor . "; border: 1px solid black;\">";
		foreach($data as $point) {
			$str .= "<div style=\"width: " . $blockwidth . "px; height: " . ($height - $point) . "px; background-color: #" . $backcolor . "; float: left;\"></div>";
		}
		$str .= "</div>";
		return $str;
	}
?>
<html>
<head>
<title>Test</title>
</head>
<body>
<p>.</p>
<p>.</p>
<p>.</p>
<p>.</p>
<p>.</p>
<p>.</p>
<p>.</p>
<p>.</p>
<div style="width: 40px; height: 100px; background-color: #000000; border: 1px solid black;">
	<div style="width: 5px; height: 100px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 95px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 90px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 85px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 80px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 75px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 70px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 65px; background-color: #FFFFFF; float: left;"></div>
</div>
<br/>
<div style="width: 40px; height: 100px; background-color: #000000; border: 1px solid black;">
	<div style="width: 5px; height: 100px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 95px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 90px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 85px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 80px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 75px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 70px; background-color: #FFFFFF; float: left;"></div>
	<div style="width: 5px; height: 65px; background-color: #FFFFFF; float: left;"></div>
</div>
<br/>
<div style="width: 20px; height: 100px; background-color: #000000; border: 1px solid black;">
	<div style="width: 5px; float: left;">
		<div style="height: 35px; background-color: #FF0000;"></div>
		<div style="height: 65px; background-color: #0000FF;"></div>
	</div>
	<div style="width: 5px; float: left;">
		<div style="height: 40px; background-color: #FF0000;"></div>
		<div style="height: 60px; background-color: #0000FF;"></div>
	</div>
	<div style="width: 5px; float: left;">
		<div style="height: 45px; background-color: #FF0000;"></div>
		<div style="height: 55px; background-color: #0000FF;"></div>
	</div>
	<div style="width: 5px; float: left;">
		<div style="height: 50.5px; background-color: #FF0000;"></div>
		<div style="height: 49.5px; background-color: #0000FF;"></div>
	</div>
</div>
<br/>
<?php
	function boo($x) {
		return rand(0, 100);
	}
	$arr = array_fill(0, 100, 50);
	$randarr = array_map(boo, $arr);
	echo simple_graph($randarr, 100, 10, '000000', 'FFFFFF');
?>
</body>
</html>
