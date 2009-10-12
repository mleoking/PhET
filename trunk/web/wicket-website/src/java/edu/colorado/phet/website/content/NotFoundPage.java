package edu.colorado.phet.website.content;

import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.templates.PhetPage;
import edu.colorado.phet.website.util.PhetUrlMapper;

public class NotFoundPage extends PhetPage {
    public NotFoundPage( PageParameters parameters ) {
        super( parameters, true );

        addTitle( getLocalizer().getString( "error.pageNotFound", this ) );
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
