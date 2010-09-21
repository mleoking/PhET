/*  */
package edu.colorado.phet.balloons;

import java.awt.*;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Apr 11, 2005
 * Time: 6:56:12 PM
 */

public class ImageFrame extends JFrame {
    public ImageFrame( Image im ) {
        JLabel contentPane = new JLabel( new ImageIcon( im ) );
        contentPane.setOpaque( true );
        setContentPane( contentPane );
        pack();
    }
}
