/*
 * InternalFrameHeightTest.java
 *
 * Created on October 3, 2006, 7:34 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package test;

import com.birosoft.liquid.LiquidLookAndFeel;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicInternalFrameUI;

/**
 *
 * @author xendren
 */
public class InternalFrameHeightTest {

    /** Creates a new instance of InternalFrameHeightTest */
    public InternalFrameHeightTest() {

        try {
          UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
          UIManager.getDefaults().put("InternalFrame.frameTitleHeight", new Integer(32));
        }
        catch (Exception e) {
          e.printStackTrace();
        }
        JFrame frame = new JFrame();
        frame.setTitle("Test title");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JDesktopPane desktop = new JDesktopPane();
        frame.setContentPane(desktop);
        JInternalFrame jif = new
        JInternalFrame("abcdef",true,true,true,true);
//        int height = 32;
//        JComponent north_pane = 
//        ((BasicInternalFrameUI)jif.getUI()).getNorthPane();
//        north_pane.setMinimumSize( new 
//        Dimension(north_pane.getMinimumSize().width , height));
//        north_pane.setPreferredSize( new 
//        Dimension(north_pane.getPreferredSize().width , height));
//        north_pane.setMaximumSize( new 
//        Dimension(north_pane.getMaximumSize().width , height));
//        north_pane.setSize( new Dimension(north_pane.getSize().width , height));
        jif.getContentPane().add(new JLabel("JInternalFrame Title Lenght test"));
        jif.pack();
        jif.setVisible(true);
        desktop.add(jif);
        frame.setSize(300, 150);
        frame.setVisible(true);
    }

  /**
   * @param args
   */
  public static void main(String[] args) {
    InternalFrameHeightTest tif = new InternalFrameHeightTest();
  }
    
}
