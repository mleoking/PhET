package edu.colorado.phet.website.util;

import org.apache.log4j.Logger;
import org.apache.wicket.protocol.http.request.WebRequestCodingStrategy;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;

/**
 * Custom request coding strategy. Implemented so that we can intercept URLs that need to be 301 redirected before
 * Wicket processes them to our page-not-found page.
 */
public class PhetRequestCodingStrategy extends WebRequestCodingStrategy {

    private static final Logger logger = Logger.getLogger( PhetRequestCodingStrategy.class.getName() );

    public PhetRequestCodingStrategy() {
    }

    public PhetRequestCodingStrategy( Settings settings ) {
        super( settings );
    }

    @Override
    /**
     * Test for redirections before doing the default Wicket behavior. If we detect that we need to redirect the URL,
     * then that will cut in first.
     */
    public IRequestTargetUrlCodingStrategy urlCodingStrategyForPath( String path ) {

        logger.debug( "urlCodingStrategyForPath: " + path );

        IRequestTargetUrlCodingStrategy testStrategy = new RedirectionStrategy();
        IRequestTargetUrlCodingStrategy strategy;

        if ( testStrategy.matches( path, true ) ) {
            strategy = testStrategy;
        }
        else {
            strategy = super.urlCodingStrategyForPath( path );
        }

        return strategy;
    }
}
