package edu.colorado.phet.buildtools;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Represents a Wicket-able website configuration that can have things deployed
 */
public abstract class PhetWebsite {

    /*---------------------------------------------------------------------------*
    * static methods
    *----------------------------------------------------------------------------*/

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

    /*---------------------------------------------------------------------------*
    * abstract methods
    *----------------------------------------------------------------------------*/

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
     * @return The server side path to the Apache document root
     */
    public abstract String getDocumentRoot();

    /**
     * @return Short identifier for the website used in property keys and other places
     */
    public abstract String getName();

    /**
     * @return Short description of the server
     */
    public abstract String getDescription();

    // will be removed after OldPhetServer is removed

    public abstract OldPhetServer getOldProductionServer();

    public abstract OldPhetServer getOldDevelopmentServer();

    /*---------------------------------------------------------------------------*
    * default getters (that used the provided abstract methods)
    *----------------------------------------------------------------------------*/

    @Override
    public String toString() {
        return getName();
    }

    /**
     * @return Either "http" or "https" depending on how to contact Tomcat Manager
     */
    public String getTomcatManagerProtocol() {
        return "http";
    }

    /**
     * @return SSH credentials
     */
    public AuthenticationInfo getServerAuthenticationInfo( BuildLocalProperties properties ) {
        return properties.getWebsiteAuthenticationInfo( this );
    }

    /**
     * @return Tomcat manager credentials
     */
    public AuthenticationInfo getTomcatManagerAuthenticationInfo( BuildLocalProperties properties ) {
        return properties.getWebsiteTomcatManagerAuthenticationInfo( this );
    }

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
     * @param resourceDir Directory where the translation has been prepared on the server
     * @return The URL to which a user should be directed to initiate the resource deployment server
     */
    public String getDeployResourceUrl( String resourceDir ) {
        try {
            return getWebBaseURL() + "/admin/deploy-resource?dir=" + URLEncoder.encode( resourceDir, "UTF-8" );
        }
        catch( UnsupportedEncodingException e ) {
            e.printStackTrace();
            return null;
        }
    }

    /*---------------------------------------------------------------------------*
    * URLs in the server
    *----------------------------------------------------------------------------*/

    /**
     * @return The URL where the front page (base) of the website can be accessed
     */
    public String getWebBaseURL() {
        return "http://" + getWebHost();
    }

    public String getProjectDevUrl( PhetProject project ) {
        // TODO: simplify path locations so we don't duplicate them in URL and Path queries
        return "http://" + getWebHost() + "/dev/" + project.getName() + "/" + project.getDevDirectoryBasename();
    }

    public String getProjectBaseUrl( PhetProject project ) {
        String projectPath = project.getProdServerDeployPath();
        if ( projectPath == null ) {
            // is null if the project is a simulation
            return "http://" + getWebHost() + "/sims/" + project.getName();
        }
        return "http://" + getWebHost() + projectPath;
    }

    /*---------------------------------------------------------------------------*
    * Paths used in the server
    *----------------------------------------------------------------------------*/

    public String getRootStagingPath() {
        return getDocumentRoot() + "/staging";
    }

    /**
     * @return The server-side path to the staging directory for simulations. Simulation files are uploaded here, and
     *         later moved over into the main sims directory
     */
    public String getSimsStagingPath() {
        return getRootStagingPath() + "/sims";
    }

    /**
     * @return A temporary directory that is not guaranteed to exist on the website
     */
    public String getTemporaryPath() {
        return getRootStagingPath() + "/tmp";
    }

    /**
     * @return The server side translations staging path
     */
    public String getTranslationStagingPath() {
        return getRootStagingPath() + "/translations";
    }

    public String getResourceStagingPath() {
        return getRootStagingPath() + "/resources";
    }

    /**
     * @return The location of the PHP offline statistics code
     */
    public String getOfflineStatisticsPath() {
        return getDocumentRoot() + "/statistics";
    }

    /**
     * @return The location of stored development versions of simulations (and other files moved over from spot.colorado.edu)
     */
    public String getDevFilesPath() {
        return getDocumentRoot() + "/dev";
    }

    public String getProjectDevPath( PhetProject project ) {
        // TODO: simplify path locations so we don't duplicate them in URL and Path queries
        return getDocumentRoot() + "/dev/" + project.getName() + "/" + project.getDevDirectoryBasename();
    }

    public String getProjectBasePath( PhetProject project ) {
        String projectPath = project.getProdServerDeployPath();
        if ( projectPath == null ) {
            // is null if the project is a simulation
            return getSimsStagingPath() + "/" + project.getName();
        }
        return getDocumentRoot() + projectPath;
    }

    /*---------------------------------------------------------------------------*
    * available websites
    *----------------------------------------------------------------------------*/

    public static PhetWebsite FIGARO = new PhetWebsite() {
        @Override
        public String getName() {
            return "figaro";
        }

        @Override
        public String getDescription() {
            return "The production website figaro.colorado.edu, available at phet.colorado.edu";
        }

        @Override
        public OldPhetServer getOldProductionServer() {
            return OldPhetServer.FIGARO;
        }

        @Override
        public OldPhetServer getOldDevelopmentServer() {
            return OldPhetServer.FIGARO_DEV;
        }

        @Override
        public String getServerHost() {
            return "figaro.colorado.edu";
        }

        @Override
        public String getWebHost() {
            return "phet.colorado.edu";
        }

        @Override
        public String getBuildLocalPropertiesLocation() {
            return "/usr/local/tomcat/conf/build-local.properties";
        }

        @Override
        public String getDocumentRoot() {
            return "/data/web/htdocs/phetsims";
        }

        @Override
        public String getTomcatManagerProtocol() {
            return "https";
        }
    };

    public static PhetWebsite PHET_SERVER = new PhetWebsite() {
        @Override
        public String getName() {
            return "phet-server";
        }

        @Override
        public String getDescription() {
            return "The development website phet-server.colorado.edu";
        }

        @Override
        public OldPhetServer getOldProductionServer() {
            return OldPhetServer.PHET_SERVER;
        }

        @Override
        public OldPhetServer getOldDevelopmentServer() {
            return OldPhetServer.PHET_SERVER_DEV;
        }

        @Override
        public String getServerHost() {
            return "phet-server.colorado.edu";
        }

        @Override
        public String getWebHost() {
            return "phet-server.colorado.edu";
        }

        @Override
        public String getBuildLocalPropertiesLocation() {
            return "/home/phet/apache-tomcat-6.0.24/conf/build-local.properties";
        }

        @Override
        public String getDocumentRoot() {
            return "/var/www/wicket";
        }
    };

    public static PhetWebsite JON_DEV = new PhetWebsite() {
        @Override
        public String getName() {
            return "jondev";
        }

        @Override
        public String getDescription() {
            return "Jonathan Olson's Website Development Server";
        }

        @Override
        public OldPhetServer getOldProductionServer() {
            return OldPhetServer.JON_DEV;
        }

        @Override
        public OldPhetServer getOldDevelopmentServer() {
            return OldPhetServer.JON_DEV_DEV;
        }

        @Override
        public String getServerHost() {
            return "192.168.1.64";
        }

        @Override
        public String getWebHost() {
            return "192.168.1.64";
        }

        @Override
        public String getBuildLocalPropertiesLocation() {
            return "/etc/tomcat6/build-local.properties";
        }

        @Override
        public String getDocumentRoot() {
            return "/var/phet";
        }
    };

    public static PhetWebsite DEFAULT_PRODUCTION_WEBSITE = FIGARO;

    private static final PhetWebsite[] websites = new PhetWebsite[]{FIGARO, JON_DEV, PHET_SERVER};

    /**
     * Find one of the websites with the specified name
     */
    public static PhetWebsite getWebsiteByName( String name ) {
        for ( PhetWebsite website : websites ) {
            if ( website.getName().equals( name ) ) {
                return website;
            }
        }
        throw new RuntimeException( "Website name not matched: " + name );
    }

}
