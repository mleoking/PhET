package edu.colorado.phet.website.content;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.PhetLink;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.content.about.AboutMainPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ForTranslatorsPanel extends PhetPanel {
    public ForTranslatorsPanel( String id, PageContext context ) {
        super( id, context );

        // TODO: update link
        add( new PhetLink( "translation-utility-link", "#" ) );
        PhetLink screenLink = new PhetLink( "translation-utility-link-2", "#" );
        add( screenLink );

        screenLink.add( new StaticImage( "translation-utility-screenshot", "/images/screenshots/translation-utility-small.png", "Translation Utility Screenshot" ) );

        add( AboutMainPanel.getLinker().getLink( "about-phet-link", context, getPhetCycle() ) );
    }

    public static String getKey() {
        return "forTranslators";
    }

    public static String getUrl() {
        return "for-translators";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, ForTranslatorsPanel.class ) ) {
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