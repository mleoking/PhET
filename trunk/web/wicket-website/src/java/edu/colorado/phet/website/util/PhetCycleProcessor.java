package edu.colorado.phet.website.util;

import org.apache.log4j.Logger;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebRequestCycleProcessor;

public class PhetCycleProcessor extends WebRequestCycleProcessor {

    private static Logger logger = Logger.getLogger( PhetCycleProcessor.class.getName() );

    public PhetCycleProcessor() {
        super();


    }


    @Override
    public void respond( RuntimeException e, RequestCycle requestCycle ) {

        logger.warn( "error encountered!" );

        super.respond( e, requestCycle );
    }
}
