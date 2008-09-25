/*  */
package edu.colorado.phet.conductivity.common;

import java.awt.*;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Mar 17, 2006
 * Time: 9:43:01 AM
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
