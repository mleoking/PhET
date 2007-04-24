/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.common;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 9, 2005
 * Time: 10:30:21 PM
 *
 */

public class LucidaSansFont extends Font {
    public LucidaSansFont( int size ) {
        this( size, false );
    }

    public LucidaSansFont( int size, boolean bold ) {
        this( size, bold, false );
    }

    public LucidaSansFont( int size, boolean bold, boolean italics ) {
        super( "Lucida Sans", ( bold ? Font.BOLD : Font.PLAIN ) | ( italics ? Font.ITALIC : Font.PLAIN ), size );
    }
}
