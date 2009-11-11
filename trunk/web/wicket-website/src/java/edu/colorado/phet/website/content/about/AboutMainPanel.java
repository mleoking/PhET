package edu.colorado.phet.website.content.about;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.content.ResearchPanel;
import edu.colorado.phet.website.content.SimulationDisplay;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingFlashPanel;
import edu.colorado.phet.website.content.troubleshooting.TroubleshootingJavaPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class AboutMainPanel extends PhetPanel {
    public AboutMainPanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedText( "about-p1", "about.p1", new Object[]{
                SimulationDisplay.getLinker().getHref( context ),
                //"href=\"http://phet.colorado.edu/simulations/index.php\"",
                ResearchPanel.getLinker().getHref( context )
                //"href=\"http://phet.colorado.edu/research/index.php\""
        } ) );

        add( new LocalizedText( "about-p2", "about.p2" ) );

        add( new LocalizedText( "about-p3", "about.p3", new Object[]{
                "href=\"http://phet.colorado.edu/about/legend.php\""
        } ) );

        add( new LocalizedText( "about-p4", "about.p4", new Object[]{
                "href=\"http://phet.colorado.edu/index.php\"",
                TroubleshootingJavaPanel.getLinker().getHref( context ),
                TroubleshootingFlashPanel.getLinker().getHref( context )
        } ) );
    }

    public static String getKey() {
        return "about";
    }

    public static String getUrl() {
        return "about";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context ) {
                if ( DistributionHandler.redirectPageClassToProduction( context.getCycle(), AboutMainPanel.class ) ) {
                    return "http://phet.colorado.edu/about/index.php";
                }
                else {
                    return super.getRawUrl( context );
                }
            }

            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}
