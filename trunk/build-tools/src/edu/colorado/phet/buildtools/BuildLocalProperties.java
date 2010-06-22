package edu.colorado.phet.buildtools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.StringUtil;

/**
 * Encapsulates the build-local.properties file.
 * Providers getters and documentation for all properties.
 * Some properties (eg, authentication info) are not accessed directly,
 * but are returned as objects that contain related property values.
 * If required values are missing, prompts the user on demand.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BuildLocalProperties {

    //Keys used in multiple locations.
    private static final String JARSIGNER_KEYSTORE_KEY = "jarsigner.keystore";
    private static final String JARSIGNER_PASSWORD_KEY = "jarsigner.password";
    private static final String JARSIGNER_ALIAS_KEY = "jarsigner.alias";
    private static final String JARSIGNER_TSA_URL_KEY = "jarsigner.tsa-url";
    private static final String GUI_PROMPT_USER_FOR_CHANGE_MESSAGE = "gui.prompt-user-for-change-message";//see BuildLocalProperties.isPromptUserForChangeMessage

    /* singleton */
    private static BuildLocalProperties instance;

    /**
     * Initializes the singleton instance by loading properties from a specified properties file.
     *
     * @param propertiesFile
     * @return
     */
    public static BuildLocalProperties initFromPropertiesFile( File propertiesFile ) {
        if ( instance != null ) {
            throw new IllegalStateException( "attempted to create singleton more than once" );
        }
        instance = new BuildLocalProperties( propertiesFile );
        return instance;
    }

    /**
     * Return instance of BuildLocalProperties initialized from the specified properties file.
     * NOTE: this function should only be called from one properties file
     *
     * @param propertiesFile
     * @return
     */
    public static BuildLocalProperties getInstanceFromPropertiesFile( File propertiesFile ) {
        if ( instance != null ) {
            if ( instance.properties.equals( propertiesFile ) ) {
                return instance;
            }
            else {
                throw new IllegalStateException( "Attempted to initialize BuildLocalProperties with different properties file" );
            }
        }
        instance = new BuildLocalProperties( propertiesFile );
        return instance;
    }

    /**
     * Initializes the singleton instance by loading properties from a properties file
     * that exists at a location relation to some trunk directory.
     *
     * @param trunk
     * @return
     */
    public static BuildLocalProperties initRelativeToTrunk( File trunk ) {
        return initFromPropertiesFile( new File( trunk, BuildToolsPaths.BUILD_LOCAL_PROPERTIES ) );
    }

    /**
     * Return instance of BuildLocalProperties initialized from the properties within the specified trunk
     *
     * @param trunk Reference to the trunk directory
     * @return
     */
    public static BuildLocalProperties getInstanceRelativeToTrunk( File trunk ) {
        return getInstanceFromPropertiesFile( new File( trunk, BuildToolsPaths.BUILD_LOCAL_PROPERTIES ) );
    }

    public static BuildLocalProperties getInstance() {
        if ( instance == null ) {
            throw new IllegalStateException( "did you forget to call initInstance?" );
        }
        return instance;
    }

    private final Properties properties;

    /* singleton */
    private BuildLocalProperties( File propertiesFile ) {
        properties = new Properties();
        load( propertiesFile );
    }

    private void load( File file ) {
        if ( file.exists() ) {
            try {
                properties.load( new FileInputStream( file ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        else {
            System.err.println( "WARNING, properties file does not exist: " + file.getAbsolutePath() );
        }
    }

    /**
     * Gets authentication information for the repository server.
     *
     * @return
     */
    public AuthenticationInfo getRespositoryAuthenticationInfo() {
        return new AuthenticationInfo( getRepositoryUsername(), getRespositoryPassword() );
    }

    /**
     * Gets authentication information for the development server.
     *
     * @return
     */
    public AuthenticationInfo getDevAuthenticationInfo() {
        return new AuthenticationInfo( getDevUsername(), getDevPassword() );
    }

    /**
     * @return Authentication for the user/ssh account of the website development server (possibly local)
     */
    public AuthenticationInfo getWebsiteDevAuthenticationInfo() {
        return new AuthenticationInfo( getWebsiteDevUsername(), getWebsiteDevPassword() );
    }

    /**
     * @return Authentication for the Tomcat manager account of the website development server (possibly local)
     */
    public AuthenticationInfo getWebsiteDevManagerAuthenticationInfo() {
        return new AuthenticationInfo( getWebsiteDevManagerUsername(), getWebsiteDevManagerPassword() );
    }

    /**
     * @return Authentication for the user/ssh account of the website production server
     */
    public AuthenticationInfo getWebsiteProdAuthenticationInfo() {
        return new AuthenticationInfo( getWebsiteProdUsername(), getWebsiteProdPassword() );
    }

    /**
     * @return Authentication for the Tomcat manager account of the website production server
     */
    public AuthenticationInfo getWebsiteProdManagerAuthenticationInfo() {
        return new AuthenticationInfo( getWebsiteProdManagerUsername(), getWebsiteProdManagerPassword() );
    }

    /**
     * Gets information needed to sign jars.
     *
     * @return
     */
    public JarsignerInfo getJarsignerInfo() {
        return new JarsignerInfo( getJarSignerKeystore(), getJarSignerPassword(), getJarSignerAlias(), getJarSignerTsaUrl() );
    }

    /**
     * Determine whether the Jarsigner credentials are specified in the properties file; if not, signing will be disabled for test functionality, see #1838.
     *
     * @return true if the Jarsigner credentials are specified in the properties file
     */
    public boolean isJarsignerCredentialsSpecified() {
        return properties.getProperty( JARSIGNER_ALIAS_KEY ) != null &&
               properties.getProperty( JARSIGNER_KEYSTORE_KEY ) != null &&
               properties.getProperty( JARSIGNER_PASSWORD_KEY ) != null &&
               properties.getProperty( JARSIGNER_TSA_URL_KEY ) != null;
    }

    /**
     * Gets the path to the web browser executable.
     * Used to test Flash sims, and to go to deployed web pages.
     * <p/>
     * Mac tip: /usr/bin/open
     *
     * @return
     */
    public String getBrowser() {
        return getRequiredString( "browser", "Browser executable path" );
    }

    /**
     * Gets the path to the Flash IDE executable.
     * <p/>
     * Mac tip: /Applications/Macromedia Flash 8/Flash 8.app/Contents/MacOS/Flash
     *
     * @return
     */
    public String getFlash() {
        return getRequiredString( "flash", "Flash IDE executable path" );
    }

    /**
     * Flex SDK path
     * IE: /...../flex3.3
     * Thus getFlexSDK() + "/bin/mxmlc" should point to the mxmlc executable
     *
     * @return The path to the Flex SDK
     */
    public String getFlexSDK() {
        return getRequiredString( "flex", "Path to a Flex SDK" );
    }

    /**
     * Should Wine be used in the Flash build process?
     * Defaults to false.
     *
     * @return
     */
    public boolean getWine() {
        return getBoolean( "wine", false );
    }

    /**
     * The path to trunk as seen by wine
     * NOTE: This is required for use with wine if the trunk directory is not under the wine directory
     * In this case, a symbolic link should be placed somewhere within the wine directory to the actual trunk
     * This should only be called when wine is true
     *
     * @return
     */
    public String getWineTrunk() {
        return getRequiredString( "wine.trunk", "Path to trunk that is wine-compatible" );
    }

    /**
     * ??
     * Use this carefully! It should be used only for debugging.
     * Defaults to false.
     *
     * @return
     */
    public boolean getDebugDryRun() {
        return getBoolean( "debug.dry-run", false );
    }

    /**
     * ??
     * Use this carefully! It should be used only for debugging.
     * Defaults to false.
     *
     * @return
     */
    public boolean getDebugSkipBuild() {
        return getBoolean( "debug.skip-build", false );
    }

    /**
     * Should we skip the step that confirms that our working copy is up-to-date?
     * Use this carefully! It should be used only for debugging.
     * Defaults to false.
     *
     * @return
     */
    public boolean getDebugSkipStatus() {
        return getBoolean( "debug.skip-status", false );
    }

    /**
     * Should we skip the commit of version info?
     * Use this carefully! It should be used only for debugging.
     * Defaults to false.
     *
     * @return
     */
    public boolean getDebugSkipCommit() {
        return getBoolean( "debug.skip-commit", false );
    }


    /**
     * Gets the name of the Flash application on Mac, as it appears in the Finder.
     * For example, "Flash 8".
     *
     * @return
     */
    public String getMacFlashName() {
        return getRequiredString( "mac.flash.name", "name of the Flash application (Mac)" );
    }

    /**
     * Gets the name of the Mac volume on which your trunk directory resides.
     *
     * @return
     */
    public String getMacTrunkVolume() {
        return getRequiredString( "mac.trunk.volume", "name of the volume where your trunk directory resides (Mac)" );
    }

    public String getWebsiteDevHost() {
        return getRequiredString( "website.dev.host", "hostname of the dev (local or remote) website" );
    }

    public String getWebsiteProdHost() {
        // separate for now, since tigercat is the other production server
        return getRequiredString( "website.prod.host", "hostname of the production (figaro) website" );
    }

    public String getWebsiteDevProtocol() {
        return getRequiredString( "website.dev.protocol", "protocol (http or https) for the dev website" );
    }

    public String getWebsiteProdProtocol() {
        // separate for now, since tigercat is the other production server
        return getRequiredString( "website.prod.protocol", "protocol (http or https) for the production (figaro) website" );
    }

    private String getDevUsername() {
        return getRequiredString( "dev.username", "Development server username" );
    }

    private String getDevPassword() {
        return getRequiredString( "dev.password", "Development server password" );
    }

    private String getProdUsername() {
        return getRequiredString( "prod.username", "Production server username" );
    }

    private String getProdPassword() {
        return getRequiredString( "prod.password", "Production server password" );
    }

    private String getRepositoryUsername() {
        return getRequiredString( "repository.username", "SVN username" );
    }

    private String getRespositoryPassword() {
        return getRequiredString( "repository.password", "SVN password" );
    }

    /*---------------------------------------------------------------------------*
    * Wicket website authentication
    *----------------------------------------------------------------------------*/

    private String getWebsiteDevUsername() {
        return getRequiredString( "website.dev.username", "Website dev (local or remote) server user/ssh username" );
    }

    private String getWebsiteDevPassword() {
        return getRequiredString( "website.dev.password", "Website dev (local or remote) server user/ssh password" );
    }

    private String getWebsiteDevManagerUsername() {
        return getRequiredString( "website.dev.manager.username", "Website dev (local or remote) server Tomcat manager username" );
    }

    private String getWebsiteDevManagerPassword() {
        return getRequiredString( "website.dev.manager.password", "Website dev (local or remote) server Tomcat manager password" );
    }

    private String getWebsiteProdUsername() {
        // separate for now, since tigercat is the other production server
        return getRequiredString( "website.prod.username", "Website production (figaro) server user/ssh username" );
    }

    private String getWebsiteProdPassword() {
        // separate for now, since tigercat is the other production server
        return getRequiredString( "website.prod.password", "Website production (figaro) server user/ssh password" );
    }

    private String getWebsiteProdManagerUsername() {
        return getRequiredString( "website.prod.manager.username", "Website production (figaro) server Tomcat manager username" );
    }

    private String getWebsiteProdManagerPassword() {
        return getRequiredString( "website.prod.manager.password", "Website production (figaro) server Tomcat manager password" );
    }


    /*---------------------------------------------------------------------------*
    * Jarsigner authentication
    *----------------------------------------------------------------------------*/

    private String getJarSignerKeystore() {
        return getRequiredString( JARSIGNER_KEYSTORE_KEY, "jarsigner keystore location" );
    }

    private String getJarSignerPassword() {
        return getRequiredString( JARSIGNER_PASSWORD_KEY, "jarsigner password" );
    }

    private String getJarSignerAlias() {
        return getRequiredString( JARSIGNER_ALIAS_KEY, "jarsigner alias" );
    }

    private String getJarSignerTsaUrl() {
        return getRequiredString( JARSIGNER_TSA_URL_KEY, "jarsigner TSA URL" );
    }


    /*
    * Gets a boolean value.
    */
    private boolean getBoolean( String key, boolean defaultValue ) {
        boolean value = defaultValue;
        String s = properties.getProperty( key );
        if ( s != null ) {
            value = StringUtil.asBoolean( s );
        }
        return value;
    }

    /*
    * Gets a required string value.
    * If the value is not specified, the user is prompted for the value,
    * and the value is saved for the next time it is requested.
    */
    private String getRequiredString( String key, String prompt ) {
        String value = properties.getProperty( key );
        if ( value == null || value.length() == 0 ) {
            value = prompt( prompt +
                    " ("+key+")" );//report the missing key name, not just the description, to facilitate adding it to a build-local.properties file
            properties.setProperty( key, value );
        }
        return value;
    }

    /*
    * Prompts the user for a value via a modal dialog.
    */
    private static String prompt( String label ) {
        if ( !label.endsWith( ":" ) ) {
            label += ":";
        }
        String s = JOptionPane.showInputDialog( label );
        if ( s == null ) {
            //TODO something better we should do if the user chooses Cancel?
            System.exit( 1 );
        }
        return s;
    }

    /**
     * Returns the BuildLocalProperties for a given buildLocalProperties file.  This method was introduced
     * to overcome the problem that BuildLocalProperties is singleton and throws an exception if
     * you try to init it more than once.
     *
     * @param propertiesFile
     * @return
     */
    public static BuildLocalProperties getProperties( File propertiesFile ) {
        return new BuildLocalProperties( propertiesFile );
    }

    /**
     * Determine whether the user should be prompted to enter a change message, see #2326
     *
     * @return true if the user should be prompted for a change message; defaults to true
     */
    public boolean isPromptUserForChangeMessage() {
        return getBoolean(GUI_PROMPT_USER_FOR_CHANGE_MESSAGE, true);
    }
}