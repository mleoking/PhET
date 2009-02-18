package edu.colorado.phet.common.phetcommon;


public class PhetCommonConstants {

    public static final String PHET_NAME = "PhET";  // user-visible name, does not require translation
    public static final String PHET_HOME_URL = "http://phet.colorado.edu";
    public static final String PHET_EMAIL = "phethelp@colorado.edu";
    
    // Properties used to set the locale.
    // Since these need to be usable via JNLP, they begin with "javaws".
    // For an untrusted application, system properties set in the JNLP file will 
    // only be set by Java Web Start if property name begins with "jnlp." or "javaws.".
    public static final String PROPERTY_PHET_LANGUAGE = "javaws.user.language";
    public static final String PROPERTY_PHET_COUNTRY = "javaws.user.country";
}
