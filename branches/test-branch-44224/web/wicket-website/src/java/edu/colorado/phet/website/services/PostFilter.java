package edu.colorado.phet.website.services;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import edu.colorado.phet.website.util.RedirectionStrategy;

/**
 * Filter after Wicket, so if doFilter() is called here, Wicket isn't handling it. Mainly useful for catching files that
 * are not found, so that they can be redirected.
 */
public class PostFilter implements Filter {

    private static final Logger logger = Logger.getLogger( PostFilter.class.getName() );

    public void init( FilterConfig filterConfig ) throws ServletException {

    }

    public void doFilter( ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain ) throws IOException, ServletException {
        boolean http = servletRequest instanceof HttpServletRequest;
        if ( !http ) {
            // if not an HTTP request, don't mess with it
            chain.doFilter( servletRequest, servletResponse );
        }
        else {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            HttpServletResponse response = (HttpServletResponse) servletResponse;

            /**
             * We wrap the response so we can change it if necessary. This will catch 404 errors and allow us to
             * redirect to a proper URL, or to fall back and trigger the original 404 error. 
             */
            PostWrapper responseWrapper = new PostWrapper( response );

            // Get the relative path (should be the entire path)
            String requestPath = request.getServletPath();

            logger.debug( requestPath );

            // process the request at lower levels. If lower levels show a 404, it will be recorded in the wrapper
            chain.doFilter( servletRequest, responseWrapper );

            if ( responseWrapper.isNotFound() ) {
                // see if we want to redirect this miss. If null, we won't redirect
                String redirect = RedirectionStrategy.redirectFile( requestPath, request.getQueryString() );
                if ( redirect != null ) {
                    if ( request.getQueryString() != null && request.getQueryString().length() > 0 ) {
                        redirect += "?" + request.getQueryString();
                    }
                    responseWrapper.sendRedirect( redirect );
                }
                else {
                    responseWrapper.triggerNotFound();
                }
            }
        }
    }

    public void destroy() {

    }
}