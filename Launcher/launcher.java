import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;

class launcher{
    public static void main(String args[]){
        JFrame frame = new JFrame("Ransomware Decorytion Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1980,1080);
        JMenuBar mb = new JMenuBar();
        JMenu m1 = new JMenu("Options");
        JMenu m2 = new JMenu("Help");
        mb.add(m1);
        mb.add(m2);
        JMenuItem m11 = new JMenuItem("Load Key");
        JMenuItem m12 = new JMenuItem("Close");
        JMenuItem m21 = new JMenuItem("How to Get my Files Back?");
        m1.add(m11);
        m1.add(m12);
        m2.add(m21);
        JButton button1 = new JButton("Decrypt!");
        JButton button2 = new JButton("Clear");

        //Customizing the buttons
        button1.setFont(new Font("Serif",Font.BOLD+Font.ITALIC,40));
        button2.setFont(new Font("Serif",Font.PLAIN,40));

        //Create a new panel
        JPanel panel = new JPanel();
        panel.add(button1);
        panel.add(button2);

        //Set the actions
        
        frame.getContentPane().add(BorderLayout.SOUTH,panel);
        frame.getContentPane().add(BorderLayout.NORTH,mb);
        frame.setVisible(true);
    }
}
