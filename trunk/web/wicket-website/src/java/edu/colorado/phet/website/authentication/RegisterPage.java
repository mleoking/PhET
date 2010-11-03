package edu.colorado.phet.website.authentication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.authentication.panels.RegisterPanel;
import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;

public class RegisterPage extends PhetMenuPage {

    private static final Logger logger = Logger.getLogger( RegisterPage.class.getName() );

    public RegisterPage( PageParameters parameters ) {
        super( parameters );

        String destination = null;

        if ( parameters != null && parameters.containsKey( "dest" ) ) {
            destination = parameters.getString( "dest" );
        }

        add( new RegisterPanel( "register-panel", getPageContext(), destination ) );

        setTitle( getLocalizer().getString( "register.title", this ) );
    }

    public static AbstractLinker getLinker( final String destination ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                try {
                    // always encode the destination because special characters might be hidden
                    String finalDestination = destination;
                    if ( finalDestination.equals( "/en/" ) ) {
                        finalDestination = "/";
                    }
                    return "register?dest=" + URLEncoder.encode( finalDestination, "UTF-8" );
                }
                catch ( UnsupportedEncodingException e ) {
                    e.printStackTrace();
                    throw new RuntimeException( e );
                }
            }
        };
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^register$", RegisterPage.class );
    }

}
