package edu.colorado.phet.wickettest.content.about;

import edu.colorado.phet.wickettest.components.LocalizedText;
import edu.colorado.phet.wickettest.content.ResearchPanel;
import edu.colorado.phet.wickettest.content.SimulationDisplay;
import edu.colorado.phet.wickettest.content.troubleshooting.TroubleshootingFlashPanel;
import edu.colorado.phet.wickettest.content.troubleshooting.TroubleshootingJavaPanel;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.links.AbstractLinker;
import edu.colorado.phet.wickettest.util.links.RawLinkable;

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
            public String getSubUrl( PageContext context ) {
                return getUrl();
            }
        };
    }
}
