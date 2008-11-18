package edu.colorado.phet.build.java;

public class PhetServer {
    public static PhetServer DEVELOPMENT = new PhetServer( "spot.colorado.edu",
                                                           "/Net/www/webdata/htdocs/UCB/AcademicAffairs/ArtsSciences/physics/phet/dev",
                                                           "http://www.colorado.edu/physics/phet/dev" );
    public static PhetServer PRODUCTION = new PhetServer( "tigercat.colorado.edu",
                                                          "/web/htdocs/phet/sims",
                                                          "http://phet.colorado.edu/sims" );

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

    public String getUrl() {
        return url;
    }
}
