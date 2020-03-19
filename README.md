# JAVA based Ransomware

## Technicalities
1. There is a launcher GUI made in java which should be run by victim to decrypt his system with the private key given to him.
2. We will be using mysql database to store the random secret key to identity the user and equivalent private key to automate the process on sending private key once payment conformation is recieved.
3. We are using RSA based encryption to encprypt the file system.Once encpryted victim will be shipped with a launcher file which will only run on his system with a analoag timer to give a appropriate countdown. 
4. In case countdown is experied, he will forever loose his files as his secret and equivalent private key will be earsed from database automatically.He should also make sure to switch on the PC continously for 48 hr i.e duration of countdown.
5. If payment is made, he will be able to decrypt his files, the sole purpose of Ransomware.

#### Please Note : This is me trying to learn JAVA. Don't be a dick and use it anywhere. I am not responsible for your actions and choices.
