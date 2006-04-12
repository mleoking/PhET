/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 22, 2006
 * Time: 9:33:11 PM
 * Copyright (c) Jan 22, 2006 by Sam Reid
 */

public class ShinyBorder extends CompoundBorder {

    public ShinyBorder() {
        super( createCompoundBorder(), new EmptyBorder( 0, 0, 0, 0 ) );
    }

    protected static CompoundBorder createCompoundBorder() {
        Color[]gradient = new Color[8];
        for( int i = 0; i < gradient.length; i++ ) {
            float value = ( (float)i ) / ( gradient.length - 1 );
            gradient[i] = new Color( 1 - value, 1 - value, 1 - value );
        }
        Border outsiteBorder = new BevelBorder( BevelBorder.RAISED, gradient[0], gradient[1], gradient[7], gradient[6] );
        Border insideBorder = new BevelBorder( BevelBorder.RAISED, gradient[2], gradient[3], gradient[5], gradient[4] );
        return new CompoundBorder( outsiteBorder, insideBorder );
    }
}
