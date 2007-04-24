/*  */
package edu.colorado.phet.waveinterference;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Apr 14, 2006
 * Time: 10:35:28 AM
 *
 */

public class VerticalSeparator extends JPanel {
    public VerticalSeparator() {
        this( 20 );
    }

    public VerticalSeparator( int h ) {
        add( Box.createRigidArea( new Dimension( h, h ) ) );
    }
}
