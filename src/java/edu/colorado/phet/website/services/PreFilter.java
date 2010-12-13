package edu.colorado.phet.website.services;

import java.io.IOException;

import javax.servlet.*;

import org.apache.log4j.Logger;
import org.apache.wicket.util.io.IOUtils;

/**
 * Before Wicket is even called, we have to grab the raw POST-data before Wicket obliterates it. Thank you Wicket.
 */
public class PreFilter implements Filter {

    private static final Logger logger = Logger.getLogger( PreFilter.class.getName() );

    public void init( FilterConfig filterConfig ) throws ServletException {

    }

    public void doFilter( ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain ) throws IOException, ServletException {
        logger.debug( "prefiltering" );
        String rawData = IOUtils.toString( servletRequest.getInputStream() );
        servletRequest.setAttribute( "raw-data", rawData );
        chain.doFilter( servletRequest, servletResponse );
    }

    public void destroy() {

    }
}
