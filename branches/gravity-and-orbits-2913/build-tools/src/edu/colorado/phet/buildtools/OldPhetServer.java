package edu.colorado.phet.buildtools;

import javax.swing.*;

public abstract class OldPhetServer {
    /* Dano on Feb 2, 2009
     * Consolidated many of the hardcoded strings to make it easier to change
     * the deploy destinations.
     * 
     * Options (null if not relevant):
     * serverHost - host of the server as accessed by SSH (ex: tigercat.colorado.edu)
     * webHost - host of the server as accessed by HTTP (ex: phet.colorado.edu)
     * deployServerPath - path to the base directory to deploy to as seen by SSH (ex: /web/htdocs/phet/sims)
     * deployWebPath - path to the base web directory to deploy as seen by HTTP (ex: /sims)
     * cacheClearUrl - full URI to clear the cache (ex: http://phet.colorado.edu/admin/cache-clear.php?cache=all)
     * cacheClearFile - simple filename used to clear the cache (ex: cache-clear.php)
     * localizationCommand - command to execute to generate the localized JAR files, including command line options (if any) (ex: /web/htdocs/phet/cl_utils/create-localized-jars.py --verbose)
     * 
     * Guidelines:  For paths DO include leading slashes '/', DO NOT include trailing slashes '/'
     *   Ex:  Yes: "/web/htdocs/phet"
     *        No:  "/web/htdocs/phet/"
     */
    public static OldPhetServer SPOT =
            new PhetDevServer(
                    "spot.colorado.edu", // Server host
                    "www.colorado.edu",  // Web host
                    "/htdocs/UCB/AcademicAffairs/ArtsSciences/physics/phet/dev", // Deploy path on server
                    "/physics/phet/dev", // Deploy path on web host
                    null, // Cache clear full URL
                    null, // Cache clear file
                    null, // Localization generation command
                    "/htdocs/UCB/AcademicAffairs/ArtsSciences/physics/phet/dev/build-tools/config/build-local.properties",
                    null
            );

    public static OldPhetServer FIGARO = new PhetProdServer(
            "figaro.colorado.edu",
            "phet.colorado.edu",
            "/data/web/htdocs/phetsims/staging/sims",
            "/sims",
            null,
            null,
            null,
            "/usr/local/tomcat/conf/build-local.properties",
            "/data/web/htdocs/phetsims/staging/sims",
            "/data/web/htdocs/phet"
    );
    public static OldPhetServer FIGARO_DEV = new PhetDevServer(
            "figaro.colorado.edu", // Server host
            "phet.colorado.edu",  // Web host
            "/data/web/htdocs/phetsims/files/dev", // Deploy path on server
            "/files/dev", // Deploy path on web host
            null, // Cache clear full URL
            null, // Cache clear file
            null, // Localization generation command
            "/web/htdocs/phet/phet-dist/build-tools-config/build-local.properties",
            null
    );

    public static OldPhetServer PHET_SERVER = new PhetProdServer(
            "phet-server.colorado.edu",
            "phet-server.colorado.edu",
            "/var/www/wicket/staging/sims",
            "/sims",
            null,
            null,
            null,
            "/etc/tomcat6/build-local.properties",
            "/var/www/wicket/staging/sims",
            "/var/www/wicket"
    );
    public static OldPhetServer PHET_SERVER_DEV = new PhetDevServer(
            "phet-server.colorado.edu", // Server host
            "phet-server.colorado.edu",  // Web host
            "/var/www/wicket/dev", // Deploy path on server
            "/dev", // Deploy path on web host
            null, // Cache clear full URL
            null, // Cache clear file
            null, // Localization generation command
            "/etc/tomcat6/build-local.properties",
            null
    );

    public static OldPhetServer JON_DEV = new PhetProdServer(
            "127.0.0.1",
            "127.0.0.1",
            "/var/phet/staging/sims",
            "/sims",
            null,
            null,
            null,
            "/etc/tomcat6/build-local.properties",
            "/var/phet/staging/sims",
            "/var/phet"
    );
    public static OldPhetServer JON_DEV_DEV = new PhetDevServer(
            "127.0.0.1", // Server host
            "127.0.0.1",  // Web host
            "/var/phet/dev", // Deploy path on server
            "/dev", // Deploy path on web host
            null, // Cache clear full URL
            null, // Cache clear file
            null, // Localization generation command
            "/etc/tomcat6/build-local.properties",
            null
    );
    /* Dano's test machine
    public static OldPhetServer SPOT =
        new PhetDevServer( 
                "192.168.42.102", // Server host
                "192.168.42.102:80",  // Web host
                "/var/www/dev/phet/dev", // Deploy path on server
                "/dev/phet/dev", // Deploy path on web host
                null, // Cache clear full URL
                null, // Cache clear file
                null, // Localization generation command
		"/var/www/dev/phet/phet-dist/build-tools-config/build-local.properties" // build-local.properties file on server
            );
    public static OldPhetServer PRODUCTION =
        new PhetProdServer(
                "192.168.42.102", // Server host
                "192.168.42.102:80",  // Web host
                "/var/www/dev/phet/sims", // Deploy path on server
                "/dev/phet/sims", // Deploy path on web host
                "http://192.168.42.102/dev/phet/admin/cache-clear.php?cache=all", // Cache clear full URL
                "cache-clear.php", // Cache clear file
                "/var/www/dev/phet/cl_utils/create-localized-jars.py --verbose --sim-root=/var/www/dev/phet/sims --jar-cmd=/usr/lib/jvm/java-6-sun-1.6.0.03/bin/jar", // Localization generation command
		"/var/www/dev/phet/phet-dist/build-tools-config/build-local.properties" // build-local.properties file on server
            );
    */

    private String serverHost;
    private String webHost;
    private String serverDeployPath;
    private String webDeployPath;
    private String cacheClearUrl;
    private String cacheClearFile;
    private String localizationCommand;
    private boolean developmentServer;
    private String buildLocalPropertiesFile;
    private String stagingArea;
    public static boolean showReminder = true;

    public OldPhetServer( String serverHost, String webHost, String serverDeployPath, String webDeployPath, String cacheClearUrl, String cacheClearFile, String localizationCommand, boolean developmentServer, String buildLocalPropertiesFile, String stagingArea ) {
        this.serverHost = serverHost;
        this.webHost = webHost;
        this.serverDeployPath = serverDeployPath;
        this.webDeployPath = webDeployPath;
        this.cacheClearUrl = cacheClearUrl;
        this.cacheClearFile = cacheClearFile;
        this.localizationCommand = localizationCommand;
        this.developmentServer = developmentServer;
        this.buildLocalPropertiesFile = buildLocalPropertiesFile;
        this.stagingArea = stagingArea;
    }

    public String getHost() {
        return serverHost;
    }

    public String getWebHost() {
        return webHost;
    }

    public String getServerDeployPath() {
        return serverDeployPath;
    }

    public String getWebDeployPath() {
        return webDeployPath;
    }

    public String getCacheClearUrl() {
        return cacheClearUrl;
    }

    public String getCacheClearFile() {
        return cacheClearFile;
    }

    public String getLocalizationCommand() {
        return localizationCommand;
    }

    public abstract String getCodebase( PhetProject project );

    public abstract String getServerDeployPath( PhetProject project );

    public boolean isDevelopmentServer() {
        return developmentServer;
    }

    public String getJavaCommand() {
        return "java";
    }

    public String getJarCommand() {
        return "jar";
    }

    public String getBuildLocalPropertiesFile() {
        return buildLocalPropertiesFile;
    }

    public String getStagingArea() {
        return stagingArea;
    }

    private static class PhetDevServer extends OldPhetServer {
        public PhetDevServer( String serverHost, String webHost, String serverDeployPath, String webDeployPath, String cacheClearUrl, String cacheClearFile, String localizationCommand, String buildLocalPropertiesFile, String stagingArea ) {
            super( serverHost, webHost, serverDeployPath, webDeployPath, cacheClearUrl, cacheClearFile, localizationCommand, true, buildLocalPropertiesFile, stagingArea );
        }

        public String getCodebase( PhetProject project ) {
            return "http://" + getWebHost() + getWebDeployPath() + "/" + project.getName() + "/" + project.getDevDirectoryBasename();
        }

        public String getServerDeployPath( PhetProject project ) {
            return getServerDeployPath() + "/" + project.getName() + "/" + project.getDevDirectoryBasename();
        }

    }

    private static class PhetProdServer extends OldPhetServer {
        private String docRoot;
        public PhetProdServer( String serverHost, String webHost, String serverDeployPath, String webDeployPath, String cacheClearUrl, String cacheClearFile, String localizationCommand, String buildLocalPropertiesFile, String stagingArea, String docRoot ) {
            super( serverHost, webHost, serverDeployPath, webDeployPath, cacheClearUrl, cacheClearFile, localizationCommand, false, buildLocalPropertiesFile, stagingArea );
            this.docRoot = docRoot;
        }

        public String getCodebase( PhetProject project ) {
            return "http://" + getWebHost() + getWebDeployPath() + "/" + project.getName();
        }

        //TODO: refactor getting server side path, should be specified in PhetProject
        public String getServerDeployPath( PhetProject project ) {
            if ( project.getProdServerDeployPath() != null ) {
                // temporary workaround to specify an absolute path on figaro
                // TODO: refactor this out so we don't rely on OldPhetServer, and don't use getServerDeployPath (points to sim root)
                String path = docRoot + project.getProdServerDeployPath();
                System.out.println( "getServerDeployPath()<override>:" + path );
                return path;
            }
            else {
                // this means we are deploying to the sims staging location. this should only happen for simulation projects
                String path = getServerDeployPath() + "/" + project.getName();
                System.out.println( "getServerDeployPath():" + path );
                return path;
            }
        }
    }

    public static final OldPhetServer DEFAULT_PRODUCTION_SERVER = FIGARO;
    public static final OldPhetServer DEFAULT_DEVELOPMENT_SERVER = SPOT;

}
