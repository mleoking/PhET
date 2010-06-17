package edu.colorado.phet.website.util;

import org.apache.log4j.Logger;
import org.apache.wicket.Response;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.protocol.http.WebRequestCycle;
import org.apache.wicket.protocol.http.request.WebClientInfo;
import org.hibernate.Session;

import edu.colorado.phet.website.PhetWicketApplication;

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

    private String cachedUserAgent;
    private boolean installer;
    private boolean offlineInstaller;
    private boolean ksu;
    private boolean youngAndFreeman;

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
        logger.debug( "----------" );
        session = HibernateUtils.getInstance().openSession();
        super.onBeginRequest();
    }

    @Override
    protected void onEndRequest() {
        logger.debug( "onEndRequest" );
        logger.debug( "Request cycle: " + ( System.currentTimeMillis() - start ) + " ms" );
        session.close();
        super.onEndRequest();
    }

    public Session getHibernateSession() {
        return session;
    }

    public String getRequestURI() {
       return getWebRequest().getHttpServletRequest().getRequestURI();
    }

    public String getQueryString() {
        return getWebRequest().getHttpServletRequest().getQueryString();
    }

    public String getUserAgent() {
        if ( cachedUserAgent == null ) {
            readUserAgent();
        }
        return cachedUserAgent;
    }

    public boolean isInstaller() {
        if ( cachedUserAgent == null ) {
            readUserAgent();
        }
        return installer;
    }

    public boolean isOfflineInstaller() {
        if ( cachedUserAgent == null ) {
            readUserAgent();
        }
        return offlineInstaller;
    }

    public boolean isKsuRipperRequest() {
        if ( cachedUserAgent == null ) {
            readUserAgent();
        }
        return ksu;
    }

    public boolean isYoungAndFreedmanRipperRequest() {
        if ( cachedUserAgent == null ) {
            readUserAgent();
        }
        return youngAndFreeman;
    }

    private void readUserAgent() {
        cachedUserAgent = ( (WebClientInfo) getClientInfo() ).getUserAgent();
        if ( cachedUserAgent == null ) {
            cachedUserAgent = "";
        }
        installer = getUserAgent().startsWith( RIPPER_USER_AGENT_SUBSTRING );
        offlineInstaller = getUserAgent().startsWith( OFFLINE_USER_AGENT );
        ksu = getUserAgent().equals( KSU_RIPPER_USER_AGENT );
        youngAndFreeman = getUserAgent().equals( YOUNG_AND_FREEDMAN_USER_AGENT );
    }

    /**
     * @return Whether we are being accessed from the production server URL (or a separate URL). Note that this can
     *         return various values for the same website instance depending on the name used to access it.
     */
    public boolean isForProductionServer() {
        return getWebRequest().getHttpServletRequest().getServerName().equals( PhetWicketApplication.getProductionServerName() );
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
        return (PhetRequestCycle) WebRequestCycle.get();
    }

}
