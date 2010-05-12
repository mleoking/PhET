package edu.colorado.phet.website.content;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;

import edu.colorado.phet.website.PhetWicketApplication;
import edu.colorado.phet.website.util.PhetRequestCycle;

/**
 * Displays the robots.txt response. Content depends on whether we are running on the production server (allow indexing)
 * or any other server (disallow indexing).
 * <p/>
 * See http://en.wikipedia.org/wiki/Robots_exclusion_standard for more information on robots.txt
 */
public class RobotsTxtPage extends WebPage {
    public RobotsTxtPage() {
        String server = PhetRequestCycle.get().getWebRequest().getHttpServletRequest().getServerName();

        String response;

        if ( server.equals( PhetWicketApplication.getProductionServerName() ) ) {
            response = "User-agent: *\nDisallow:";
        }
        else {
            response = "User-agent: *\nDisallow: /";
        }

        Label text = new Label( "text", response );
        text.setRenderBodyOnly( true );
        text.setEscapeModelStrings( false );
        add( text );
    }

    @Override
    protected void configureResponse() {
        super.configureResponse();
        getWebRequestCycle().getWebResponse().setContentType( "text/plain" );
    }
}
