package edu.colorado.phet.build.java;

import edu.colorado.phet.build.PhetProject;

public abstract class PhetServer {
    public static PhetServer DEVELOPMENT = new PhetDevServer( "spot.colorado.edu",
                                                              "/Net/www/webdata/htdocs/UCB/AcademicAffairs/ArtsSciences/physics/phet/dev",
                                                              "http://www.colorado.edu/physics/phet/dev" );
    public static PhetServer PRODUCTION = new PhetProdServer( "tigercat.colorado.edu",
                                                              "/web/htdocs/phet",
                                                              "http://phet.colorado.edu" );

    private String host;
    private String path;
    private String url;

    public PhetServer( String host, String path, String url ) {
        this.host = host;
        this.path = path;
        this.url = url;
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

    private static class PhetDevServer extends PhetServer {
        public PhetDevServer( String host, String path, String url ) {
            super( host, path, url );
        }

        public String getURL( PhetProject project ) {
            return getURL() + "/" + project.getName() + "/" + project.getVersionString();
        }

        public String getPath( PhetProject project ) {
            return getPath() + "/" + project.getName() + "/" + project.getVersionString();
        }
    }

    private static class PhetProdServer extends PhetServer {
        public PhetProdServer( String host, String path, String url ) {
            super( host, path, url );
        }

        public String getURL( PhetProject project ) {
            return getURL() + "/sims/" + project.getName();
        }

        public String getPath( PhetProject project ) {
            return getPath() + "/sims/" + project.getName();
        }
    }
}
