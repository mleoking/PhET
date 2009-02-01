package edu.colorado.phet.build;

import javax.swing.*;

import edu.colorado.phet.build.PhetProject;

public abstract class PhetServer {
    public static PhetServer DEVELOPMENT = new PhetDevServer( "spot.colorado.edu",
                                                              "/Net/www/webdata/htdocs/UCB/AcademicAffairs/ArtsSciences/physics/phet/dev",
                                                              "http://www.colorado.edu/physics/phet/dev" );
    public static PhetServer PRODUCTION = new PhetProdServer( "tigercat.colorado.edu",
                                                              "/web/chroot/phet/usr/local/apache/htdocs",
                                                              "http://phet.colorado.edu" );

    private String host;
    private String path;
    private String url;
    private boolean developmentServer;

    public PhetServer( String host, String path, String url, boolean developmentServer ) {
        this.host = host;
        this.path = path;
        this.url = url;
        this.developmentServer = developmentServer;
    }

    public String getHost() {
        return host;
    }

    public String getPath() {
        return path;
    }

    public String getURL() {
        return url;
    }

    public abstract String getURL( PhetProject project );

    public abstract String getPath( PhetProject project );

    public boolean isDevelopmentServer() {
        return developmentServer;
    }

    public void deployFinished() {
    }

    private static class PhetDevServer extends PhetServer {
        public PhetDevServer( String host, String path, String url ) {
            super( host, path, url, true );
        }

        public String getURL( PhetProject project ) {
            return getURL() + "/" + project.getName() + "/" + project.getDevDirectoryBasename();
        }

        public String getPath( PhetProject project ) {
            return getPath() + "/" + project.getName() + "/" + project.getDevDirectoryBasename();
        }
    }

    private static class PhetProdServer extends PhetServer {
        public PhetProdServer( String host, String path, String url ) {
            super( host, path, url, false );
        }

        public String getURL( PhetProject project ) {
            return getURL() + "/sims/" + project.getName();
        }

        public String getPath( PhetProject project ) {
            return getPath() + "/sims/" + project.getName();
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
