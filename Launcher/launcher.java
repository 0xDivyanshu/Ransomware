import javax.swing.*;
import java.awt.*;

class events {

}

class launcher {
    public static void main(final String args[]) {
        final JFrame frame = new JFrame("Ransomware Decorytion Tool");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1980, 1080);
        final JMenuBar mb = new JMenuBar();
        final JMenu m1 = new JMenu("Options");
        final JMenu m2 = new JMenu("Help");
        mb.add(m1);
        mb.add(m2);
        final JMenuItem m11 = new JMenuItem("Load Key");
        final JMenuItem m12 = new JMenuItem("Close");
        final JMenuItem m21 = new JMenuItem("How to Get my Files Back?");
        m1.add(m11);
        m1.add(m12);
        m2.add(m21);
        final JButton button1 = new JButton("Decrypt!");
        final JButton button2 = new JButton("Clear");

        // Customizing the buttons
        button1.setFont(new Font("Serif", Font.ITALIC, 30));
        button2.setFont(new Font("Serif", Font.ITALIC, 30));

        // Create a new panel
        final JPanel panel = new JPanel();
        panel.add(button1);
        panel.add(button2);

        //Set the actions

        frame.getContentPane().add(BorderLayout.SOUTH,panel);
        frame.getContentPane().add(BorderLayout.NORTH,mb);
        frame.setVisible(true);
    }
}