package edu.colorado.phet.website.util;

import org.apache.log4j.Logger;
import org.apache.wicket.protocol.http.request.WebRequestCodingStrategy;
import org.apache.wicket.request.target.coding.IRequestTargetUrlCodingStrategy;

/**
 * Custom request coding strategy. Implemented so that we can intercept URLs that need to be 301 redirected before
 * Wicket processes them to our page-not-found page.
 */
public class PhetRequestCodingStrategy extends WebRequestCodingStrategy {

    private static Logger logger = Logger.getLogger( PhetRequestCodingStrategy.class.getName() );

    public PhetRequestCodingStrategy() {
        logger.debug( "Created PhetRequestCodingStrategy" );
    }

    public PhetRequestCodingStrategy( Settings settings ) {
        super( settings );
        logger.debug( "Created PhetRequestCodingStrategy" );
    }

    @Override
    public IRequestTargetUrlCodingStrategy urlCodingStrategyForPath( String path ) {

        logger.debug( "urlCodingStrategyForPath: " + path );

        IRequestTargetUrlCodingStrategy strategy = super.urlCodingStrategyForPath( path );

        if ( strategy == null ) {
            IRequestTargetUrlCodingStrategy testStrategy = new RedirectionStrategy();
            if ( testStrategy.matches( path ) ) {
                strategy = testStrategy;
            }
        }

        return strategy;
    }
}
