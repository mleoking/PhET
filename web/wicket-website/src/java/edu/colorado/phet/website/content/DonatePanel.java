package edu.colorado.phet.website.content;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.borders.SmallOrangeButtonBorder;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class DonatePanel extends PhetPanel {
    public DonatePanel( String id, PageContext context ) {

        super( id, context );

        add( new SmallOrangeButtonBorder( "small-orange", context ) );

        add( new LocalizedText( "donate-questions", "donate.questions", new Object[]{
                "<a href=\"mailto:Kathryn.Dessau@colorado.edu\">Kathryn.Dessau@colorado.edu</a>"
        } ) );

    }

    public static String getKey() {
        return "donate";
    }

    public static String getUrl() {
        return "donate";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, DonatePanel.class ) ) {
                    return "http://phet.colorado.edu/contribute/donate.php";
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