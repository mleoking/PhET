/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility;

import java.util.Locale;


public class TUConstants {

    /* not intended for instantiation */
    private TUConstants() {}
    
    public static final Locale ENGLISH_LOCALE = new Locale( "en" );
    
    // use this separator for creating paths to files in the file system
    public static final String FILE_PATH_SEPARATOR = System.getProperty( "file.separator" );
    
    // use this separator for creating paths to resources in the JAR file
    public static final String RESOURCE_PATH_SEPARATOR = "/";
}
