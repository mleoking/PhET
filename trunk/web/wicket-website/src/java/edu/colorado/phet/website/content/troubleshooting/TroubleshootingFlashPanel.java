package edu.colorado.phet.website.content.troubleshooting;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class TroubleshootingFlashPanel extends PhetPanel {
    public TroubleshootingFlashPanel( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( CSS.TROUBLESHOOTING ) );

        add( new LocalizedText( "troubleshooting-flash-intro", "troubleshooting.flash.intro", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu\"><span class=\"red\">phethelp@colorado.edu</span></a>"
        } ) );

        add( new LocalizedText( "troubleshooting-flash-q1-answer", "troubleshooting.flash.q1.answer" ) );

    }

    public static String getKey() {
        return "troubleshooting.flash";
    }

    public static String getUrl() {
        return "troubleshooting/flash";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, TroubleshootingFlashPanel.class ) ) {
                    return "http://phet.colorado.edu/tech_support/support-flash.php";
                }
                else {
                    return super.getRawUrl( context, cycle );
                }
            }

            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}