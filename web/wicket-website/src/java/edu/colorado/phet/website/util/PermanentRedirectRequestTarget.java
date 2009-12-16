package edu.colorado.phet.website.util;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.wicket.RequestContext;
import org.apache.wicket.RequestCycle;
import org.apache.wicket.protocol.http.WebResponse;
import org.apache.wicket.protocol.http.portlet.PortletRequestContext;
import org.apache.wicket.request.target.basic.RedirectRequestTarget;

/**
 * Permanent redirection version of Wicket's RedirectRequestTarget. Should send an HTTP 301 instead of a 302
 */
public class PermanentRedirectRequestTarget extends RedirectRequestTarget {

    private static Logger logger = Logger.getLogger( PhetCycleProcessor.class.getName() );

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
        WebResponse response = (WebResponse) requestCycle.getResponse();
        response.reset();

        logger.info( "redirecting to " + url );

        // added this line from the RedirectRequestTarget Wicket implementation
        response.getHttpServletResponse().setStatus( HttpServletResponse.SC_MOVED_PERMANENTLY );
        if ( url.startsWith( "/" ) ) {
            // context-absolute url

            RequestContext rc = RequestContext.get();
            String continueTo = null;
            if ( rc.isPortletRequest() && ( (PortletRequestContext) rc ).isEmbedded() ) {
                response.redirect( url );
            }
            else {
                String location = RequestCycle.get()
                        .getRequest()
                        .getRelativePathPrefixToContextRoot() +
                                  url.substring( 1 );

                if ( location.startsWith( "./" ) && location.length() > 2 ) {
                    location = location.substring( 2 );
                }

                response.redirect( location );
            }
        }
        else if ( url.indexOf( "://" ) > 0 ) {
            // absolute url
            response.redirect( url );
        }
        else {
            // relative url
            response.redirect( RequestCycle.get()
                    .getRequest()
                    .getRelativePathPrefixToWicketHandler() +
                               url );
        }
    }

}
