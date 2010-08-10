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
    public static OldPhetServer DEVELOPMENT =
            new PhetDevServer(
                    "spot.colorado.edu", // Server host
                    "www.colorado.edu",  // Web host
                    "/Net/www/webdata/htdocs/UCB/AcademicAffairs/ArtsSciences/physics/phet/dev", // Deploy path on server
                    "/physics/phet/dev", // Deploy path on web host
                    null, // Cache clear full URL
                    null, // Cache clear file
                    null, // Localization generation command
                    "/Net/www/webdata/htdocs/UCB/AcademicAffairs/ArtsSciences/physics/phet/dev/build-tools/config/build-local.properties",//todo: could just require specifying subpath from path on server, see above
                    null
            );

    public static OldPhetServer PRODUCTION =
            new PhetProdServer(
                    "tigercat.colorado.edu", // Server host
                    "phet.colorado.edu",  // Web host
                    "/web/chroot/phet/usr/local/apache/htdocs/staging/sims", // Deploy path on server
                    "/sims", // Deploy path on web host
                    "http://phet.colorado.edu/admin/cache-clear.php?cache=all", // Cache clear full URL
                    "cache-clear.php", // Cache clear file
                    "/web/chroot/phet/usr/local/apache/htdocs/cl_utils/create-localized-jars.py --verbose ", // Localization generation command
                    "/web/htdocs/phet/phet-dist/build-tools-config/build-local.properties",
                    "/web/chroot/phet/usr/local/apache/htdocs/staging/sims"
            );
    public static OldPhetServer FIGARO = new PhetProdServer(
            "figaro.colorado.edu",
            "phetsims.colorado.edu",
            "/data/web/htdocs/phetsims/staging/sims",
            "/sims",
            null,
            null,
            null,
            "/usr/local/tomcat/conf/build-local.properties",
            "/data/web/htdocs/phetsims/staging/sims"
    );
    /* Dano's test machine
    public static OldPhetServer DEVELOPMENT =
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

    public abstract String getWebDeployURL( PhetProject project );

    public boolean isDevelopmentServer() {
        return developmentServer;
    }

    public void deployFinished() {
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

        public String getWebDeployURL( PhetProject project ) {
            return "http://" + getWebHost() + getWebDeployPath() + "/" + project.getName() + "/" + project.getDevDirectoryBasename();
        }
    }

    private static class PhetProdServer extends OldPhetServer {
        public PhetProdServer( String serverHost, String webHost, String serverDeployPath, String webDeployPath, String cacheClearUrl, String cacheClearFile, String localizationCommand, String buildLocalPropertiesFile, String stagingArea ) {
            super( serverHost, webHost, serverDeployPath, webDeployPath, cacheClearUrl, cacheClearFile, localizationCommand, false, buildLocalPropertiesFile, stagingArea );
        }

        public String getCodebase( PhetProject project ) {
            return "http://" + getWebHost() + getWebDeployPath() + "/" + project.getName();
        }

        //TODO: refactor getting server side path, should be specified in PhetProject
        public String getServerDeployPath( PhetProject project ) {
            if ( project.getProdServerDeployPath() != null ) {
                String path = project.getProdServerDeployPath();
                System.out.println( "getServerDeployPath()<override>:" + path );
                return path;
            }
            else {
                String path = getServerDeployPath() + "/" + project.getName();
                System.out.println( "getServerDeployPath():" + path );
                return path;
            }
        }

        public String getWebDeployURL( PhetProject project ) {
            System.out.println( "getDeployBaseRemote():" + "http://" + getWebHost() + "/" + getWebDeployPath() + "/" + project.getName() );
            return "http://" + getWebHost() + getWebDeployPath() + "/" + project.getName();
        }

        public void deployFinished() {
            super.deployFinished();
            if ( OldPhetServer.showReminder ) {
                JOptionPane.showMessageDialog( null, "Reminder:\n" +
                                                     "Document this release in trunk/web/website/about/changes.txt.\n" +
                                                     "Copy to tigercat:/web/htdocs/phet/about/changes.txt." );
            }
        }
    }

}
