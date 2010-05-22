package edu.colorado.phet.website.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;

/**
 * Permanent redirection version of Wicket's RedirectRequestTarget. Should send an HTTP 301 instead of a 302
 */
public class PermanentRedirectRequestTarget extends RedirectRequestTarget {

    private static final Logger logger = Logger.getLogger( PermanentRedirectRequestTarget.class.getName() );

    private String url;

    /**
     * Your URL should be one of the following:
     * <ul>
     * <li>Fully qualified "http://foo.com/bar"</li>
     * <li>Relative to the Wicket filter/servlet, e.g. "?wicket:interface=foo", "mounted_page"</li>
     * <li>Absolute within your web application's context root, e.g. "/foo.html"</li>
     * </ul>
     *
     * @param url URL to redirect to.
     */
    public PermanentRedirectRequestTarget( String url ) {
        super( url );
        this.url = url;
    }

    @Override
    public void respond( RequestCycle requestCycle ) {
        logger.info( "redirecting to " + url );

        WebResponse response = (WebResponse) requestCycle.getResponse();
        response.reset();
        HttpServletResponse httpResponse = response.getHttpServletResponse();
        HttpServletRequest httpRequest = ( (ServletWebRequest) requestCycle.getRequest() ).getHttpServletRequest();

        int status = HttpServletResponse.SC_MOVED_PERMANENTLY;

        logger.info( "status: " + status );
        httpResponse.setStatus( status );

        String location;

        if ( url.startsWith( "/" ) ) {
            location = "http://" + httpRequest.getHeader( "Host" ) + url;
        }
        else if ( url.indexOf( "://" ) != -1 ) {
            location = url;
        }
        else {
            location = requestCycle.getRequest().getRelativePathPrefixToWicketHandler() + url;
        }
        location = httpResponse.encodeRedirectURL( location );

        response.setHeader( "Location", location );

        logger.info( "location: " + location );

    }

}
