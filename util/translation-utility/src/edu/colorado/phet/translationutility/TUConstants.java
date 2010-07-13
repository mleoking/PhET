/* Copyright 2008-2010, University of Colorado */

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
    
    // hack for avoiding the Windows task bar in most cases
    public static final int WINDOWS_TASK_BAR_HEIGHT = 200;
    
    // PhET's help email address
    public static final String PHETHELP_EMAIL = "phethelp@colorado.edu";
    
    // Host name portion of PhET URLs
    public static final String PHET_HOSTNAME = "phet.colorado.edu";
    
    // PhET website home page
    private static final String PHET_HOME_URL = "http://" + PHET_HOSTNAME;
    
    // Translation Utility home page
    public static final String TU_HOME_URL = PHET_HOME_URL + "/contribute/translation-utility.php";
    
    // version info for the latest version of Translation Utility lives here
    public static final String TU_LATEST_VERSION_URL = PHET_HOME_URL + "/phet-dist/translation-utility/translation-utility.properties";
    
    // web page that describes how to "Get PhET"
    public static final String GET_PHET_URL = PHET_HOME_URL + "/new/get_phet/index.php";
}
