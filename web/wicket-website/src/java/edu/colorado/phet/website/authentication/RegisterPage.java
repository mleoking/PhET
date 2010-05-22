package edu.colorado.phet.website.authentication;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;
import org.apache.wicket.model.ResourceModel;

import edu.colorado.phet.website.authentication.panels.RegisterPanel;
import edu.colorado.phet.website.templates.PhetPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;

public class RegisterPage extends PhetPage {

    private static final Logger logger = Logger.getLogger( RegisterPage.class.getName() );

    public RegisterPage( PageParameters parameters ) {
        super( parameters, true );

        String destination = null;

        if ( parameters != null && parameters.containsKey( "dest" ) ) {
            destination = parameters.getString( "dest" );
        }

        add( new RegisterPanel( "register-panel", getPageContext(), destination ) );

        addTitle( new ResourceModel( "register.title" ) );
    }

    public static AbstractLinker getLinker( final String destination ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                return "register?dest=" + destination;
            }
        };
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^register$", RegisterPage.class );
    }

}
