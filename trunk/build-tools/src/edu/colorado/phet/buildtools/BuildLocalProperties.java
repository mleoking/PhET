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
     * Initializes the singleton instance by loading properties from a properties file
     * that exists at a location relation to some trunk directory.
     *
     * @param trunk
     * @return
     */
    public static BuildLocalProperties initRelativeToTrunk( File trunk ) {
        return initFromPropertiesFile( new File( trunk, "build-tools/build-local.properties" ) );
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
     * Gets authentication information for the production server.
     *
     * @return
     */
    public AuthenticationInfo getProdAuthenticationInfo() {
        return new AuthenticationInfo( getProdUsername(), getProdPassword() );
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
     * Gets information needed to sign jars.
     *
     * @return
     */
    public JarsignerInfo getJarsignerInfo() {
        return new JarsignerInfo( getJarSignerKeystore(), getJarSignerPassword(), getJarSignerAlias(), getJarSignerTsaUrl() );
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
     * Should Wine be used in the Flash build process?
     * Defaults to false.
     *
     * @return
     */
    public boolean getWine() {
        return getBoolean( "wine", false );
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

    private String getJarSignerKeystore() {
        return getRequiredString( "jarsigner.keystore", "jarsigner keystore location" );
    }

    private String getJarSignerPassword() {
        return getRequiredString( "jarsigner.password", "jarsigner password" );
    }

    private String getJarSignerAlias() {
        return getRequiredString( "jarsigner.alias", "jarsigner alias" );
    }

    private String getJarSignerTsaUrl() {
        return getRequiredString( "jarsigner.tsa-url", "jarsigner TSA URL" );
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
            value = prompt( prompt );
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
}