package edu.colorado.phet.website.content.workshops;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class WorkshopsPanel extends PhetPanel {
    public WorkshopsPanel( String id, PageContext context ) {
        super( id, context );

        add( UgandaWorkshopsPanel.getLinker().getLink( "workshop-uganda-link", context, getPhetCycle() ) );

    }

    public static String getKey() {
        return "workshops";
    }

    public static String getUrl() {
        return "for-teachers/workshops";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( cycle != null && DistributionHandler.redirectPageClassToProduction( cycle, WorkshopsPanel.class ) ) {
                    return "http://phet.colorado.edu/teacher_ideas/workshops.php";
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