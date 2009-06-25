package edu.colorado.phet.tomcattest.filters;

import java.util.Locale;

import javax.servlet.ServletConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import edu.colorado.phet.common.phetcommon.util.LocaleUtils;

public class LocalizationHandler {

    public void run( ServletRequest request, ServletResponse response ) {
        if ( request.getAttribute( "locale" ) != null ) {
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        System.out.println( "Running LocalizationHandler on " + httpRequest.getRequestURI() );
        Locale locale = LocaleUtils.stringToLocale( "en" );
        String[] chunks = httpRequest.getRequestURI().split( "/" );
        if ( chunks.length > 1 ) {
            String str = chunks[1];
            System.out.println( "chunk 1:" + str );
            if ( str.length() == 2 || ( str.length() == 5 && str.substring( 2, 3 ).equals( "_" ) ) ) {
                boolean pass = true;
                for ( int i = 0; i < str.length(); i++ ) {
                    if ( !Character.isLetter( str.charAt( i ) ) ) {
                        if ( i != 2 ) {
                            pass = false;
                            break;
                        }
                    }
                }
                if ( pass ) {
                    locale = LocaleUtils.stringToLocale( str );
                }
            }
        }
        request.setAttribute( "locale", locale );
        response.setLocale( locale );
    }

    public void init( ServletConfig config ) {

    }

    public void destroy() {

    }
}
