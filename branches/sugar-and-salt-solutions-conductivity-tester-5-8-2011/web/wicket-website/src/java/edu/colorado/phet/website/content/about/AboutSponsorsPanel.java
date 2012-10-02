package edu.colorado.phet.website.content.about;

import org.apache.wicket.behavior.HeaderContributor;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.constants.CSS;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class AboutSponsorsPanel extends PhetPanel {
    public AboutSponsorsPanel( String id, PageContext context ) {
        super( id, context );

        add( HeaderContributor.forCss( CSS.SPONSORS ) );
    }

    public static String getKey() {
        return "sponsors";
    }

    public static String getUrl() {
        return "about/sponsors";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, AboutSponsorsPanel.class ) ) {
                    return "http://phet.colorado.edu/sponsors/index.php";
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