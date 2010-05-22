package edu.colorado.phet.website.util;

import org.apache.log4j.Logger;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;
import org.apache.wicket.request.IRequestCodingStrategy;

public class PhetCycleProcessor extends WebRequestCycleProcessor {

    private static final Logger logger = Logger.getLogger( PhetCycleProcessor.class.getName() );

    public PhetCycleProcessor() {
        super();

        logger.debug( "created PhetCycleProcessor" );
    }


    @Override
    public void respond( RuntimeException e, RequestCycle requestCycle ) {

        logger.warn( "error encountered!" );

        super.respond( e, requestCycle );
    }

    @Override
    protected IRequestCodingStrategy newRequestCodingStrategy() {
        return new PhetRequestCodingStrategy();
    }
}
