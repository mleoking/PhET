package edu.colorado.phet.website.content.workshops;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class UgandaWorkshopPhotosPanel extends PhetPanel {
    public UgandaWorkshopPhotosPanel( String id, PageContext context ) {
        super( id, context );

    }

    public static String getKey() {
        return "workshops.uganda-photos";
    }

    public static String getUrl() {
        return "for-teachers/workshops/uganda-photos";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, UgandaWorkshopPhotosPanel.class ) ) {
                    return "http://phet.colorado.edu/teacher_ideas/workshop_uganda_photos.php";
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