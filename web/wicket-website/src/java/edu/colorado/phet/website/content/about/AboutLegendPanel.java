package edu.colorado.phet.website.content.about;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.constants.Images;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class AboutLegendPanel extends PhetPanel {
    public AboutLegendPanel( String id, PageContext context ) {
        super( id, context );

        add( new StaticImage( "guidance-recommended-image", Images.GUIDANCE_RECOMMENDED, getPhetLocalizer().getString( "tooltip.legend.guidanceRecommended", this ) ) );
        add( new StaticImage( "under-construction-image", Images.UNDER_CONSTRUCTION, getPhetLocalizer().getString( "tooltip.legend.underConstruction", this ) ) );
        add( new StaticImage( "classroom-tested-image", Images.CLASSROOM_TESTED, getPhetLocalizer().getString( "tooltip.legend.classroomTested", this ) ) );

        add( new LocalizedText( "about-legend-guidance-recommended", "about.legend.guidance-recommended" ) );
        add( new LocalizedText( "about-legend-under-construction", "about.legend.under-construction" ) );
        add( new LocalizedText( "about-legend-classroom-tested", "about.legend.classroom-tested" ) );

    }

    public static String getKey() {
        return "about.legend";
    }

    public static String getUrl() {
        return "for-teachers/legend";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, AboutLegendPanel.class ) ) {
                    return "http://phet.colorado.edu/about/legend.php";
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