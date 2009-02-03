package edu.colorado.phet.build;

import javax.swing.*;

import edu.colorado.phet.build.PhetProject;

public abstract class PhetServer {
    /* Dano on Feb 2, 2009
     * Consolidated many of the hardcoded strings to make it easier to change
     * the deploy destinations.
     * 
     * Options (null if not relevant):
     * serverHost - host of the serever as accessed by SSH (ex: tigercat.colorado.edu)
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
    public static PhetServer DEVELOPMENT = 
        new PhetDevServer( 
                "spot.colorado.edu", // Server host
                "spot.colorado.edu",  // Web host
                "/Net/www/webdata/htdocs/UCB/AcademicAffairs/ArtsSciences/physics/phet/dev", // Deploy path on server
                "/physics/phet/dev", // Deploy path on web host
                null, // Cache clear full URL
                null, // Cache clear file
                null // Localization generation command
            );

    public static PhetServer PRODUCTION =
        new PhetProdServer(
                "tigercat.colorado.edu", // Server host
                "phet.colorado.edu",  // Web host
                "/web/chroot/phet/usr/local/apache/htdocs/sims", // Deploy path on server
                "/sims", // Deploy path on web host
                "http://192.168.42.102/dev/phet/admin/cache-clear.php?cache=all", // Cache clear full URL
                "cache-clear.php", // Cache clear file
                "/web/chroot/phet/usr/local/apache/htdocs/cl_utils/create-localized-jars.py --verbose " // Localization generation command
            );

    /* Dano's test machine
    public static PhetServer DEVELOPMENT = 
        new PhetDevServer( 
                "192.168.42.102", // Server host
                "192.168.42.102:80",  // Web host
                "/var/www/dev/phet/dev", // Deploy path on server
                "/dev/phet/dev", // Deploy path on web host
                null, // Cache clear full URL
                null, // Cache clear file
                null // Localization generation command
            );
    public static PhetServer PRODUCTION =
        new PhetProdServer(
                "192.168.42.102", // Server host
                "192.168.42.102:80",  // Web host
                "/var/www/dev/phet/sims", // Deploy path on server
                "/dev/phet/sims", // Deploy path on web host
                "http://192.168.42.102/dev/phet/admin/cache-clear.php?cache=all", // Cache clear full URL
                "cache-clear.php", // Cache clear file
                "/var/www/dev/phet/cl_utils/create-localized-jars.py --verbose --sim-root=/var/www/dev/phet/sims --jar-cmd=/usr/lib/jvm/java-6-sun-1.6.0.03/bin/jar" // Localization generation command
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

    public PhetServer( String serverHost, String webHost, String serverDeployPath, String webDeployPath, String cacheClearUrl, String cacheClearFile, String localizationCommand, boolean developmentServer ) {
        this.serverHost =  serverHost;
        this.webHost =  webHost;
        this.serverDeployPath =  serverDeployPath;
        this.webDeployPath =  webDeployPath;
        this.cacheClearUrl =  cacheClearUrl;
        this.cacheClearFile =  cacheClearFile;
        this.localizationCommand =  localizationCommand;
        this.developmentServer = developmentServer;
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

    private static class PhetDevServer extends PhetServer {
        public PhetDevServer( String serverHost, String webHost, String serverDeployPath, String webDeployPath, String cacheClearUrl, String cacheClearFile, String localizationCommand ) {
            super( serverHost, webHost, serverDeployPath, webDeployPath, cacheClearUrl, cacheClearFile, localizationCommand, true );
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

    private static class PhetProdServer extends PhetServer {
        public PhetProdServer( String serverHost, String webHost, String serverDeployPath, String webDeployPath, String cacheClearUrl, String cacheClearFile, String localizationCommand ) {
            super( serverHost, webHost, serverDeployPath, webDeployPath, cacheClearUrl, cacheClearFile, localizationCommand, false );
        }

        public String getCodebase( PhetProject project ) {
            return "http://" + getWebHost() + getWebDeployPath() + "/" + project.getName();
        }

        public String getServerDeployPath( PhetProject project ) {
            System.out.println("getServerDeployPath():" + getServerDeployPath() + "/sims/" + project.getName());
            return getServerDeployPath() + "/" + project.getName();
        }

        public String getWebDeployURL( PhetProject project ) {
            System.out.println("getDeployBaseRemote():" +  "http://" + getWebHost() + "/" + getWebDeployPath() + "/" + project.getName());
            return "http://" + getWebHost() + getWebDeployPath() + "/" + project.getName();
        }

        public void deployFinished() {
            super.deployFinished();
            JOptionPane.showMessageDialog( null, "Reminder:\n" +
                                                 "Document this release in trunk/website/about/changes.txt.\n" +
                                                 "Copy to tigercat:/web/htdocs/phet/about/changes.txt.");
        }
    }

    public static void main( String[] args ) {
        PhetServer.PRODUCTION.deployFinished();
    }
}
