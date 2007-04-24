/*  */
package edu.colorado.phet.qm.util;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 15, 2006
 * Time: 11:27:32 AM
 *
 */

public class FontSetter {
    public static void setFont( Font font, JComponent parent ) {
        parent.setFont( font );
        for( int i = 0; i < parent.getComponentCount(); i++ ) {
            if( parent.getComponent( i ) instanceof JComponent ) {
                setFont( font, (JComponent)parent.getComponent( i ) );
            }
        }
    }
}
