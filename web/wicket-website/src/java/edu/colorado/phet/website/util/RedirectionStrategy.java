package edu.colorado.phet.website.util;

import org.apache.log4j.Logger;
import org.apache.wicket.IRequestTarget;
import org.apache.wicket.request.RequestParameters;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;

public class RedirectionStrategy implements IRequestTargetUrlCodingStrategy {

    private static Logger logger = Logger.getLogger( RedirectionStrategy.class.getName() );

    public String getMountPath() {
        return "";
    }

    public CharSequence encode( IRequestTarget requestTarget ) {
        // won't make links to this, so it shouldn't matter
        return null;
    }

    public IRequestTarget decode( RequestParameters requestParameters ) {
        String requestPath = requestParameters.getPath();
        //return null;
        return new PermanentRedirectRequestTarget( "/" );
    }

    public boolean matches( IRequestTarget requestTarget ) {
        return requestTarget instanceof RedirectRequestTarget;
    }

    public boolean matches( String path ) {
        //return true;

        logger.debug( "Checking " + path );

        return path.indexOf( "index.php" ) != -1;
    }
}
