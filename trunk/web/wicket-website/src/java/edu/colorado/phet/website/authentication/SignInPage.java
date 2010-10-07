package edu.colorado.phet.website.authentication;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.apache.wicket.PageParameters;

import edu.colorado.phet.website.authentication.panels.SignInPanel;
import edu.colorado.phet.website.templates.PhetMenuPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetUrlMapper;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

/**
 * The page to send people to if they need to sign in. Specify a destination that they will be taken to after signing
 * in (even after registering)
 */
public class SignInPage extends PhetMenuPage {

    private static final Logger logger = Logger.getLogger( SignInPage.class.getName() );

    public SignInPage( PageParameters parameters ) {
        super( parameters );

        String destination = null;

        if ( parameters != null && parameters.containsKey( "dest" ) ) {
            destination = parameters.getString( "dest" );
        }

        setTitle( getLocalizer().getString( "signIn.title", this ) );

        add( new SignInPanel( "sign-in-panel", getPageContext(), destination ) );

    }


    public static RawLinkable getLinker( final String destination ) {
        return new AbstractLinker() {
            @Override
            public String getSubUrl( PageContext context ) {
                try {
                    // always encode the destination because special characters might be hidden
                    String finalDestination = destination;
                    if ( finalDestination.equals( "/en/" ) ) {
                        finalDestination = "/";
                    }
                    return "sign-in?dest=" + URLEncoder.encode( finalDestination, "UTF-8" );
                }
                catch ( UnsupportedEncodingException e ) {
                    e.printStackTrace();
                    throw new RuntimeException( e );
                }
            }
        };
    }

    public static void addToMapper( PhetUrlMapper mapper ) {
        mapper.addMap( "^sign-in$", SignInPage.class );
    }

}
