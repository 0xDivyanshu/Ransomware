# JAVA based Ransomware

## Technicalities
1. There is a launcher GUI made in java which should be run by victim to decrypt his system with the private key given to him.
2. We will be using psql database to store the random secret key to identity the user and equivalent private key to automate the process on sending private key once payment conformation is recieved.
3. We are using AES and RSA based hybrud encryption to encprypt the file system.Once encpryted victim will be shipped with a launcher file which will only run on his system with a analoag timer to give a appropriate countdown. 
4. Randomly generated AES key of 50 character is used to encrtpt all the files and that AES key is then encrypted with the public key of MASTER RSA key pair and stored in key.enc file. Once user clicks decrypts button, a GET request will be made with content of key.enc and identifier which will be checked against payment status and if paid, the content will be then decrypted with the private Key of the server and plain AES key will be sent back to victim.
5. In case countdown is experied, he will forever loose his files as his secret and equivalent private key will be earsed from database automatically.He should also make sure to switch on the PC continously for 48 hr i.e duration of countdown.
6. If payment is made, he will be able to decrypt his files, the sole purpose of Ransomware.

## Installation
Just run install.sh file and everything will be setup autmatically for you. Enjoy ! :)

**Note : This is not ready yet. In Devlopment Stage!**

## Compilation 
Go to src/main directory and type below lines
```bash
javac -d ../class encrypt.java
javac -d ../class decrypt.java
cd ../launcher && javac -d ../class -cp ../class launcher.java
cd ../class && java launcher
```

#### Please Note : This is me trying to learn JAVA. Don't be a dick and use it anywhere. I am not responsible for your actions and choices.
