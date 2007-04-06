/* Copyright 2007, University of Colorado */

package edu.colorado.phet.quantumtunneling;

import edu.colorado.phet.common.view.util.PhetProjectConfig;


public class QTStrings {
    
    private static final PhetProjectConfig CONFIG = QTConstants.CONFIG;
    
    /* not intended for instantiation */
    private QTStrings() {}
    
    public static final String getString( String name ) {
        return CONFIG.getString( name  );
    }
    
    public static final char getChar( String name, char defaultValue ) {
        return CONFIG.getChar( name, defaultValue );
    }

    public static final int getInt( String name, int defaultValue ) {
        return CONFIG.getInt( name, defaultValue );
    }
}
