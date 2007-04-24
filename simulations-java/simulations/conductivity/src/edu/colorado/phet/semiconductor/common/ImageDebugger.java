/*  */
package edu.colorado.phet.semiconductor.common;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 17, 2006
 * Time: 9:43:01 AM
 *
 */

public class ImageDebugger {
    public static void show( String name, Image image ) {
        JFrame frame = new JFrame( name );
        JLabel contentPane = new JLabel( new ImageIcon( image ) );
        contentPane.setOpaque( true );
        frame.setContentPane( contentPane );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.pack();
        frame.setVisible( true );
    }
}
