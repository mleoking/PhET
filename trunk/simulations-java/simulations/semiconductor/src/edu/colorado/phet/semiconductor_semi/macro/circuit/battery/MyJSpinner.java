/*, 2003.*/
package edu.colorado.phet.semiconductor_semi.macro.circuit.battery;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 15, 2004
 * Time: 10:34:12 AM
 *
 */
public class MyJSpinner extends JSpinner {
    public MyJSpinner( SpinnerNumberModel spinnerNumberModel ) {
        super( spinnerNumberModel );
    }

    protected void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D)g;
//        new BasicGraphicsSetup().setup(g2);
        g2.setStroke( new BasicStroke( 1 ) );
        super.paintComponent( g2 );
    }

}
