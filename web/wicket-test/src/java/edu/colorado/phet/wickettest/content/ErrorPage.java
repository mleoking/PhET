package edu.colorado.phet.wickettest.content;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.wickettest.templates.PhetPage;
import edu.colorado.phet.wickettest.util.PhetUrlMapper;

public class ErrorPage extends PhetPage {
    public ErrorPage( PageParameters parameters ) {
        super( parameters, true );

        //addTitle( getLocalizer().getString( "error.pageNotFound", this ) );
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^$", ErrorPage.class );
    }
}