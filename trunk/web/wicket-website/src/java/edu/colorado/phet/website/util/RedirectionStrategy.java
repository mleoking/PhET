package edu.colorado.phet.website.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;

public class RedirectionStrategy implements IRequestTargetUrlCodingStrategy {

    private static Logger logger = Logger.getLogger( RedirectionStrategy.class.getName() );

    private static Map<String, String> map = new HashMap<String, String>();

    static {
        // initialize redirection mapping
        map.put( "/index.php", "/" );
        map.put( "/simulations/index.php", "/en/simulations/category/featured" );
        map.put( "/tech_support/index.php", "/en/troubleshooting" );
        map.put( "/tech_support/support-flash.php", "/en/troubleshooting/flash" );
        map.put( "/tech_support/support-java.php", "/en/troubleshooting/java" );
        map.put( "/tech_support/support-javascript.php", "/en/troubleshooting/javascript" );
        map.put( "/contribute/index.php", "/en/contribute" );
        map.put( "/research/index.php", "/en/research" );
        map.put( "/about/index.php", "/en/about" );

        map.put( "/simulations/sims.php?sim=Circuit_Construction_Kit_DC_Only", "/en/simulation/circuit-construction-kit/circuit-construction-kit-dc" );

        // TODO: add all URLs
    }

    private static String morphPath( String str ) {
        return "/" + str;
    }

    public String getMountPath() {
        return "";
    }

    public CharSequence encode( IRequestTarget requestTarget ) {
        // won't make links to this, so it shouldn't matter
        return null;
    }

    public IRequestTarget decode( RequestParameters requestParameters ) {
        String requestPath = requestParameters.getPath();
        String path = morphPath( requestPath );
        if ( map.containsKey( path ) ) {
            return new PermanentRedirectRequestTarget( map.get( path ) );
        }

        if ( path.length() == 3 ) {
            // redirect "/ar" to "/ar/" and others, to avoid those error pages
            return new PermanentRedirectRequestTarget( path + "/" );
        }

        logger.error( "Did not find path: " + requestPath );

        throw new RuntimeException( "Did not find path: " + requestPath );
    }

    public boolean matches( IRequestTarget requestTarget ) {
        return requestTarget instanceof RedirectRequestTarget;
    }

    public boolean matches( String path ) {
        boolean inMap = map.containsKey( morphPath( path ) );

        logger.debug( "testing: " + path );

        if ( inMap ) {
            return true;
        }

        if ( path.length() == 2 && path.indexOf( "." ) == -1 ) {
            return true;
        }

        // TODO: add in more complicated redirection matching here?

        return false;
    }
}
