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
        map.put( "index.php", "/" );
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
        if ( map.containsKey( requestPath ) ) {
            return new PermanentRedirectRequestTarget( map.get( requestPath ) );
        }

        logger.error( "Did not find path: " + requestPath );

        throw new RuntimeException( "Did not find path: " + requestPath );
    }

    public boolean matches( IRequestTarget requestTarget ) {
        return requestTarget instanceof RedirectRequestTarget;
    }

    public boolean matches( String path ) {
        boolean inMap = map.containsKey( path );

        logger.debug( "testing: " + path );

        if ( inMap ) {
            return true;
        }

        // TODO: add in more complicated redirection matching here?

        return false;
    }
}
