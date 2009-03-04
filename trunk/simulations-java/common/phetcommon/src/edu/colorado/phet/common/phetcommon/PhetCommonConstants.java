package edu.colorado.phet.common.phetcommon;


public class PhetCommonConstants {

    public static final String PHET_NAME = "PhET";  // user-visible name, does not require translation
    
    public static final String PHET_HOME_URL = "http://phet.colorado.edu";
    public static final String PHET_EMAIL = "phethelp@colorado.edu";
    
    // web pages
    public static final String PHET_INSTALLER_URL = PHET_HOME_URL + "/get_phet/full_install.php";
    public static final String PHET_LICENSE_URL = PHET_HOME_URL + "/about/licensing.php";
    
    // services
    public static final String SIM_WEBSITE_REDIRECT_URL = PHET_HOME_URL + "/services/sim-website-redirect";
    public static final String SIM_JAR_REDIRECT_URL = PHET_HOME_URL + "/services/sim-jar-redirect";
    public static final String PHET_INFO_URL = PHET_HOME_URL + "/services/phet-info";
    public static final String STATISTICS_SERVICE_URL = PHET_HOME_URL + "/statistics/submit_message";
    
    // service request version numbers
    public static final int SIM_WEBSITE_REDIRECT_VERSION = 1; // associated with SIM_WEBSITE_REDIRECT_URL
    public static final int SIM_JAR_REDIRECT_VERSION = 1; // associated with SIM_JAR_REDIRECT_URL
    public static final int SIM_VERSION_VERSION = 1; // <sim_version> request to PHET_INFO_URL
    public static final int PHET_INSTALLER_UPDATE_VERSION = 1; // <phet_installer_update> request to PHET_INFO_URL
    
    // Properties used to set the locale.
    // Since these need to be usable via JNLP, they begin with "javaws".
    // For an untrusted application, system properties set in the JNLP file will 
    // only be set by Java Web Start if property name begins with "jnlp." or "javaws.".
    public static final String PROPERTY_PHET_LANGUAGE = "javaws.user.language";
    public static final String PROPERTY_PHET_COUNTRY = "javaws.user.country";
}
