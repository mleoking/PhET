package edu.colorado.phet.website.util;

import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.apache.log4j.Logger;
import org.hibernate.Session;

public class PhetRequestCycle extends WebRequestCycle {

    private Session session;
    private Long start;

    public static final String KSU_RIPPER_USER_AGENT = "httrack-web-mirror-ar";
    public static final String YOUNG_AND_FREEDMAN_USER_AGENT = "httrack-web-mirror-yf";

    private static Logger logger = Logger.getLogger( PhetRequestCycle.class.getName() );

    public PhetRequestCycle( WebApplication webApplication, WebRequest webRequest, Response response ) {
        super( webApplication, webRequest, response );
    }

    @Override
    protected void onBeginRequest() {
        logger.debug( "onBeginRequest" );
        start = System.currentTimeMillis();
        logger.info( "----------" );
        session = HibernateUtils.getInstance().openSession();
        super.onBeginRequest();
    }

    @Override
    protected void onEndRequest() {
        logger.debug( "onEndRequest" );
        logger.info( "Request cycle: " + ( System.currentTimeMillis() - start ) + " ms" );
        session.close();
        super.onEndRequest();
    }

    public Session getHibernateSession() {
        return session;
    }

    public String getUserAgent() {
        return ( (WebClientInfo) getClientInfo() ).getUserAgent();
    }

    public boolean isKsuRipperRequest() {
        return getUserAgent().equals( KSU_RIPPER_USER_AGENT );
    }

    public boolean isYoungAndFreedmanRipperRequest() {
        return getUserAgent().equals( YOUNG_AND_FREEDMAN_USER_AGENT );
    }
}
