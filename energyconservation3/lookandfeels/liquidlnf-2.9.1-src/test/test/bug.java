package test;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EtchedBorder;

public class bug extends JApplet
{
    public bug()
    {
    }

    public void init()
    {
        try
        {
            jbInit();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    private void jbInit() throws Exception
    {
        UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");

        BorderLayout borderLayout = new BorderLayout();
        this.getContentPane().setLayout(borderLayout);

        JLayeredPane jLayeredPane = new JLayeredPane();

        JPanel jPanel1 = new JPanel();
        jPanel1.setBounds(new Rectangle(20, 20, 215, 205));
        jPanel1.setBackground(Color.blue);
        jLayeredPane.add(jPanel1, new Integer(1));

        JPanel jPanel2 = new JPanel();
        jPanel2.setBounds(new Rectangle(55, 55, 125, 115));
        jPanel2.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
        jPanel2.setOpaque(false);
        jLayeredPane.add(jPanel2, new Integer(2));

        this.getContentPane().add(jLayeredPane, BorderLayout.CENTER);
    }

    public static void main(String[] args)
    {
        bug applet = new bug();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(applet, BorderLayout.CENTER);
        frame.setTitle("Applet Frame");
        applet.init();
        applet.start();
        frame.setSize(300, 300);
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        frame.setLocation((d.width - frameSize.width) / 2, (d.height - frameSize.height) / 2);
        frame.setVisible(true);
    }
}
