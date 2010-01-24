package edu.colorado.phet.website.authentication;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.website.authentication.panels.SignInPanel;
import edu.colorado.phet.website.templates.PhetPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.Linkable;

public class SignInPage extends PhetPage {

    // TODO: add translation links at the bottom?

    private static Logger logger = Logger.getLogger( SignInPage.class.getName() );

    public SignInPage( PageParameters parameters ) {
        super( parameters, true );

        String destination = null;

        if ( parameters != null && parameters.containsKey( "dest" ) ) {
            destination = parameters.getString( "dest" );
        }

        addTitle( new ResourceModel( "signIn.title" ) );

        add( new SignInPanel( "sign-in-panel", getPageContext(), destination ) );

    }


    public static Linkable getLinker( final String destination ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "sign-in?dest=" + destination;
            }
        };
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^sign-in$", SignInPage.class );
    }

}
