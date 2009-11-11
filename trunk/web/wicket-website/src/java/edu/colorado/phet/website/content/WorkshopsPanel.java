package edu.colorado.phet.website.content;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class WorkshopsPanel extends PhetPanel {
    public WorkshopsPanel( String id, PageContext context ) {
        super( id, context );

    }

    public static String getKey() {
        return "workshops";
    }

    public static String getUrl() {
        return "workshops";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context ) {
                if ( DistributionHandler.redirectPageClassToProduction( context.getCycle(), WorkshopsPanel.class ) ) {
                    return "http://phet.colorado.edu/teacher_ideas/workshops.php";
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