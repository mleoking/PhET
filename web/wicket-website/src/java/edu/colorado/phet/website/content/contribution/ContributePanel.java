package edu.colorado.phet.website.content.contribution;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.content.about.AboutSponsorsPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ContributePanel extends PhetPanel {
    public ContributePanel( String id, PageContext context ) {
        super( id, context );

        add( new LocalizedText( "contribute-main", "contribute.main", new Object[]{
                "http://www.cufund.org/giving-opportunities/fund-description/?id=3685"
        } ) );

        add( new LocalizedText( "contribute-thanks", "contribute.thanks", new Object[]{
                AboutSponsorsPanel.getLinker().getHref( context, getPhetCycle() ),
                "href=\"http://www.royalinteractive.com/\""
        } ) );
    }

    public static String getKey() {
        return "contribute";
    }

    public static String getUrl() {
        return "contribute";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, ContributePanel.class ) ) {
                    return "http://phet.colorado.edu/contribute/index.php";
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