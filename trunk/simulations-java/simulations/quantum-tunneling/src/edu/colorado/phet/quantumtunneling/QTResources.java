/* Copyright 2007, University of Colorado */

package edu.colorado.phet.quantumtunneling;

import java.awt.Image;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;


public class QTResources {
    
    /* not intended for instantiation */
    private QTResources() {}
    
    private static final PhetResources RESOURCES = new PhetResources( QTConstants.PROJECT_NAME );

    public static PhetResources getResourceLoader() {
        return RESOURCES;
    }
    
    public static final String getString( String name ) {
        return RESOURCES.getLocalizedString( name  );
    }
    
    public static final char getChar( String name, char defaultValue ) {
        return RESOURCES.getLocalizedChar( name, defaultValue );
    }

    public static final int getInt( String name, int defaultValue ) {
        return RESOURCES.getLocalizedInt( name, defaultValue );
    }
    
    public static final Image getImage( String name ) {
        return RESOURCES.getImage( name );
    }
    
    public static final String getCommonString( String name ) {
        return PhetCommonResources.getInstance().getLocalizedString( name  );
    }
    
    public static final Image getCommonImage( String name ) {
        return PhetCommonResources.getInstance().getImage( name );
    }
}
