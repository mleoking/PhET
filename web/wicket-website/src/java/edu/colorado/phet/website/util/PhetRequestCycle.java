package edu.colorado.phet.website.util;

import org.apache.log4j.Logger;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.hibernate.Session;

/**
 * A request cycle is created for each HTTP request, and is available at any point from pages or components by calling
 * (PhetRequestCycle) getRequestCycle(). Only PhetRequestCycles should be returned.
 * <p/>
 * The PhetRequestCycle is responsible for initializing a Hibernate session that will be used for all database
 * interactions for the pages, components, and everything else.
 * <p/>
 * Additionally, support is provided to determine whether this request was made from a specialized ripper (for
 * external installers)
 */
public class PhetRequestCycle extends WebRequestCycle {

    private Session session;
    private Long start;

    public static final String KSU_RIPPER_USER_AGENT = "httrack-web-mirror-ar";
    public static final String YOUNG_AND_FREEDMAN_USER_AGENT = "httrack-web-mirror-yf";
    public static final String RIPPER_USER_AGENT_SUBSTRING = "httrack-web";
    public static final String OFFLINE_USER_AGENT = "httrack-web-offline"; // example of full would be httrack-web-offline-en
    public static final String HIDE_TRANSLATIONS_USER_AGENT = "hide-translations";

    private static final Logger logger = Logger.getLogger( PhetRequestCycle.class.getName() );

    public PhetRequestCycle( WebApplication webApplication, WebRequest webRequest, Response response ) {
        this( webApplication, webRequest, response, webRequest.getHttpServletRequest().getRequestURI() );
    }

    public PhetRequestCycle( WebApplication webApplication, WebRequest webRequest, Response response, String originalRequest ) {
        super( webApplication, webRequest, response );

        logger.debug( "created request cycle for " + originalRequest );
    }

    @Override
    protected void onBeginRequest() {
        logger.debug( "onBeginRequest" );
        logger.debug( "request for: " + getWebRequest().getHttpServletRequest().getRequestURI() );
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

    public String getQueryString() {
        return getWebRequest().getHttpServletRequest().getQueryString();
    }

    public String getUserAgent() {
        String userAgent = ( (WebClientInfo) getClientInfo() ).getUserAgent();
        return userAgent == null ? "" : userAgent;
    }

    public boolean isInstaller() {
        return getUserAgent().startsWith( RIPPER_USER_AGENT_SUBSTRING );
    }

    public boolean isOfflineInstaller() {
        return getUserAgent().startsWith( OFFLINE_USER_AGENT );
    }

    public boolean isKsuRipperRequest() {
        return getUserAgent().equals( KSU_RIPPER_USER_AGENT );
    }

    public boolean isYoungAndFreedmanRipperRequest() {
        return getUserAgent().equals( YOUNG_AND_FREEDMAN_USER_AGENT );
    }

    public boolean isLocalRequest() {
        String addr = getWebRequest().getHttpServletRequest().getRemoteAddr();
        String host = getWebRequest().getHttpServletRequest().getRemoteHost();
        String localhost = getWebRequest().getHttpServletRequest().getServerName();

        if ( localhost.equals( host ) || localhost.equals( "phetsims.colorado.edu" ) || localhost.equals( "phet.colorado.edu" ) ) {
            return true;
        }
        else {
            return false;
        }
    }

    public static PhetRequestCycle get() {
        // TODO: verify
        return (PhetRequestCycle) WebRequestCycle.get();
    }

}
