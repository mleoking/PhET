package edu.colorado.phet.motion2d;

//import phet.utils.ExitOnClose;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.plaf.PlafUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jan 17, 2003
 * Time: 7:50:58 PM
 * To change this template use Options | File Templates.
 */
public class Motion2D {
    public static void main( String[] args ) {
        JFrame f = new JFrame( "PhET Motion2D" );

        JApplet ja = new VelAccGui();
        ja.init();
        f.setContentPane( ja );

        f.setSize( 800, 500 );
        GraphicsUtil.centerFrameOnScreen( f );

        f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        f.repaint();
        SwingUtilities.invokeLater( new Repaint( ja ) );

//        JMenuBar jmb=new JMenuBar();
//        JMenuItem[] laf=PlafUtil.getLookAndFeelItems();
//        JMenu menu=new JMenu( "Look and Feel");
//        for( int i = 0; i < laf.length; i++ ) {
//            JMenuItem jMenuItem = laf[i];
//            menu.add(jMenuItem);
//            System.out.println( "jMenuItem = " + jMenuItem );
//        }
//        jmb.add(menu);
//        f.setJMenuBar( jmb );

//        JFrame j2=new JFrame( "Util");
//        j2.setJMenuBar( jmb );
//        j2.pack();
//        j2.setVisible( true);

        f.setVisible( true );
    }

    static final class Repaint implements Runnable {
        Component c;

        public Repaint( Component c ) {
            this.c = c;
        }

        public void run() {
            c.repaint();
            c.validate();
        }

    }
}
