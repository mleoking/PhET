package edu.colorado.phet.rotation.util;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 11:43:13 AM
 * Copyright (c) Jan 9, 2007 by Sam Reid
 */

public class LucidaSansFont extends Font {

    public LucidaSansFont( int size ) {
        this( size, false );
    }

    public LucidaSansFont( int size, boolean bold ) {
        super( "Lucida Sans", bold ? Font.BOLD : Font.PLAIN, size );
    }
}
