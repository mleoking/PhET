package edu.colorado.phet.website.content.workshops;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.content.getphet.FullInstallPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class UgandaWorkshopsPanel extends PhetPanel {
    public UgandaWorkshopsPanel( String id, PageContext context ) {
        super( id, context );

        add( FullInstallPanel.getLinker().getLink( "full-install-1", context, getPhetCycle() ) );
        add( FullInstallPanel.getLinker().getLink( "full-install-2", context, getPhetCycle() ) );
        add( UgandaWorkshopPhotosPanel.getLinker().getLink( "photos-link", context, getPhetCycle() ) );
    }

    public static String getKey() {
        return "workshops.uganda";
    }

    public static String getUrl() {
        return "for-teachers/workshops/uganda";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, UgandaWorkshopsPanel.class ) ) {
                    return "http://phet.colorado.edu/teacher_ideas/workshop_uganda.php";
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