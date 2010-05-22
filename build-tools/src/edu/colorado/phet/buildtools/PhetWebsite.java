package edu.colorado.phet.buildtools;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Represents a Wicket-able website configuration that can have things deployed
 */
public abstract class PhetWebsite {
    /**
     * @return The hostname for SSH access
     */
    public abstract String getServerHost();

    /**
     * @return The hostname for HTTP(s) access
     */
    public abstract String getWebHost();

    /**
     * @return The location on the server side where the build-local.properties exists
     */
    public abstract String getBuildLocalPropertiesLocation();

    /**
     * @return The server side general staging path
     */
    public abstract String getStagingPath();

    /**
     * @return SSH credentials
     */
    public abstract AuthenticationInfo getServerAuthenticationInfo( BuildLocalProperties properties );

    /**
     * @return Tomcat manager credentials
     */
    public abstract AuthenticationInfo getTomcatAuthenticationInfo( BuildLocalProperties properties );

    /**
     * @param translationDir Directory where the translation has been prepared on the server
     * @return The URL to which a user should be directed to initiate the translation deployment server
     */
    public String getDeployTranslationUrl( String translationDir ) {
        try {
            return getWebBaseURL() + "/admin/deploy-translation?dir=" + URLEncoder.encode( translationDir, "UTF-8" );
        }
        catch( UnsupportedEncodingException e ) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * @return The URL where the front page (base) of the website can be accessed
     */
    public String getWebBaseURL() {
        return "http://" + getWebHost();
    }

    /**
     * @return The server side translations staging path
     */
    public String getTranslationStagingPath() {
        return getStagingPath() + "/translations";
    }

    public static PhetWebsite FIGARO = new PhetWebsite() {
        @Override
        public String getServerHost() {
            return "figaro.colorado.edu";
        }

        @Override
        public String getWebHost() {
            return "phetsims.colorado.edu";
        }

        @Override
        public String getBuildLocalPropertiesLocation() {
            return "/usr/local/tomcat/conf/build-local.properties";
        }

        @Override
        public String getStagingPath() {
            return "/data/web/htdocs/phetsims/staging";
        }

        @Override
        public AuthenticationInfo getServerAuthenticationInfo( BuildLocalProperties properties ) {
            return properties.getWebsiteProdAuthenticationInfo();
        }

        @Override
        public AuthenticationInfo getTomcatAuthenticationInfo( BuildLocalProperties properties ) {
            return properties.getWebsiteProdManagerAuthenticationInfo();
        }

    };

    public static void openBrowser( String path ) {
        String browser = BuildLocalProperties.getInstance().getBrowser();
        if ( browser != null ) {
            try {
                System.out.println( "command = " + browser + " " + path );
                Runtime.getRuntime().exec( new String[]{browser, path} );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }
}
