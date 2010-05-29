package edu.colorado.phet.website.content;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.templates.PhetPage;
import edu.colorado.phet.website.util.PhetUrlMapper;

public class ErrorPage extends PhetPage {
    public ErrorPage( PageParameters parameters ) {
        super( parameters );

        addTitle( getLocalizer().getString( "error.pageNotFound", this ) );
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
        mapper.addMap( "^$", ErrorPage.class );
    }
}