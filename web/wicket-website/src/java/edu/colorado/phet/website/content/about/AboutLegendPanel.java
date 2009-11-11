package edu.colorado.phet.website.content.about;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class AboutLegendPanel extends PhetPanel {
    public AboutLegendPanel( String id, PageContext context ) {
        super( id, context );

        // TODO: consolidate image names
        add( new StaticImage( "guidance-recommended-image", "/images/ratings/guidance-recommended.png", null ) );
        add( new StaticImage( "under-construction-image", "/images/ratings/under-construction.png", null ) );
        add( new StaticImage( "classroom-tested-image", "/images/ratings/classroom-tested.png", null ) );

        add( new LocalizedText( "about-legend-guidance-recommended", "about.legend.guidance-recommended" ) );
        add( new LocalizedText( "about-legend-under-construction", "about.legend.under-construction" ) );
        add( new LocalizedText( "about-legend-classroom-tested", "about.legend.classroom-tested" ) );

    }

    public static String getKey() {
        return "about.legend";
    }

    public static String getUrl() {
        return "about/legend";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context ) {
                if ( DistributionHandler.redirectPageClassToProduction( context.getCycle(), AboutLegendPanel.class ) ) {
                    return "http://phet.colorado.edu/about/legend.php";
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