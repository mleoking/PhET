/**
 * Class: AlphaSetter
 * Class: edu.colorado.phet.coreadditions
 * User: Ron LeMaster
 * Date: Mar 5, 2004
 * Time: 9:10:36 AM
 */
package edu.colorado.phet.coreadditions;

import java.awt.*;

public class AlphaSetter {


    //
    // Interfaces implemented
    //
    
    //
    // Static fields and methods
    //
    public static void set( Graphics2D g2, double alpha ) {
        g2.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, (float)alpha ) );
    }

    //
    // Inner classes
    //
}
