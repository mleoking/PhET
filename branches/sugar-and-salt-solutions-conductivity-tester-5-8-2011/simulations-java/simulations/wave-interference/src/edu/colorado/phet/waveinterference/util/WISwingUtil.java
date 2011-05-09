// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.util;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 19, 2006
 * Time: 1:19:12 AM
 */

public class WISwingUtil {
    public static void setChildrenEnabled( Component component, boolean enabled ) {
//        component.setEnabled( enabled );
        if ( component instanceof Container ) {
            Container container = (Container) component;
            for ( int i = 0; i < container.getComponentCount(); i++ ) {
                Component c = container.getComponent( i );
                c.setEnabled( enabled );
                setChildrenEnabled( c, enabled );
            }
        }
    }
}
