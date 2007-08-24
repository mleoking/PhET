/* Copyright 2004, Sam Reid */
package org.reid.particles.tutorial;

import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Aug 9, 2005
 * Time: 10:30:21 PM
 * Copyright (c) Aug 9, 2005 by Sam Reid
 */

public class LucidaSansFont extends Font {
    public LucidaSansFont( int size ) {
        this( size, false );
    }

    public LucidaSansFont( int size, boolean bold ) {
        this( size, bold, false );
    }

    public LucidaSansFont( int size, boolean bold, boolean italics ) {
        super( PhetDefaultFont.LUCIDA_SANS, ( bold ? Font.BOLD : Font.PLAIN ) | ( italics ? Font.ITALIC : Font.PLAIN ), size );
    }
}
