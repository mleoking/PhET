package edu.colorado.phet.website.services;

import java.io.IOException;
import java.net.HttpURLConnection;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.log4j.Logger;

/**
 * Wraps the HTTP response so that we can catch 404 errors and if necessary replace them with redirections.
 * <p/>
 * Should send everything else (including other error codes) straight through.
 */
public class PostWrapper extends HttpServletResponseWrapper {

    /**
     * Whether or not the page was not found (and returned a 404)
     */
    private boolean notFound = false;

    /**
     * If there was one, the message with the 404 error
     */
    private String notFoundMessage = null;

    private static final Logger logger = Logger.getLogger( PostWrapper.class.getName() );

    public PostWrapper( HttpServletResponse httpServletResponse ) {
        super( httpServletResponse );
    }

    @Override
    public void sendError( int i, String s ) throws IOException {
        logger.debug( "error: " + i + ": " + s );
        if ( i == HttpURLConnection.HTTP_NOT_FOUND ) {
            notFound = true;
            notFoundMessage = s;
        }
        else {
            super.sendError( i, s );
        }
    }

    @Override
    public void sendError( int i ) throws IOException {
        logger.debug( "error: " + i );
        if ( i == HttpURLConnection.HTTP_NOT_FOUND ) {
            notFound = true;
        }
        else {
            super.sendError( i );
        }
    }

    public boolean isNotFound() {
        return notFound;
    }

    /**
     * If we caught a 404 message, this will cause it to be sent.
     */
    public void triggerNotFound() throws IOException {
        if ( notFound ) {
            if ( notFoundMessage == null ) {
                super.sendError( HttpURLConnection.HTTP_NOT_FOUND );
            }
            else {
                super.sendError( HttpURLConnection.HTTP_NOT_FOUND, notFoundMessage );
            }
        }
    }
}
