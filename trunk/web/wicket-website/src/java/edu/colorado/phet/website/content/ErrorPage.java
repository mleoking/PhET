package edu.colorado.phet.website.content;

import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.constants.Linkers;
import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.PhetUrlMapper;

public class ErrorPage extends PhetMenuPage {
    public ErrorPage( PageParameters parameters ) {
        super( parameters );

        setTitle( getLocalizer().getString( "error.internalError", this ) );

        add( new LocalizedText( "errorMessage", "error.internalError.message", new Object[]{
                Linkers.getHelpLink( "PhET Website Error", getPageContext(), getPhetCycle() )
        } ) );

        hideSocialBookmarkButtons();
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
//        mapper.addMap( "^$", ErrorPage.class );
    }
}