package edu.colorado.phet.website.content;

import java.util.HashSet;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.menu.NavLocation;
import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.PhetUrlMapper;

public class NotFoundPage extends PhetMenuPage {

    private static final Logger logger = Logger.getLogger( NotFoundPage.class.getName() );

    public NotFoundPage( PageParameters parameters ) {
        super( parameters );

        addTitle( getLocalizer().getString( "error.pageNotFound", this ) );

        logger.info( "Not found: " + getWebRequestCycle().getWebRequest().getHttpServletRequest().getRequestURI() );

    }

    @Override
    protected void configureResponse() {
        super.configureResponse();
        getWebRequestCycle().getWebResponse().getHttpServletResponse().setStatus( HttpServletResponse.SC_NOT_FOUND );
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @Override
    public boolean isErrorPage() {
        return true;
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^404$", NotFoundPage.class );
    }
}
