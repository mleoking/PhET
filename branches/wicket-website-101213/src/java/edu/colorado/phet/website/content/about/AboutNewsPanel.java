package edu.colorado.phet.website.content.about;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.cache.CacheableUrlStaticPanel;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.constants.Images;
import edu.colorado.phet.website.panels.NewsChangelogPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class AboutNewsPanel extends PhetPanel implements CacheableUrlStaticPanel {
    public AboutNewsPanel( String id, PageContext context ) {
        super( id, context );

        add( new StaticImage( "newsletter-screenshot", Images.NEWSLETTER_SUMMER_10, "Screenshot of the 2009 PhET newsletter" ) );

        add( new NewsChangelogPanel( "news-changelog-panel", context ) );
    }

    public static String getKey() {
        return "about.news";
    }

    public static String getUrl() {
        return "about/news";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, AboutNewsPanel.class ) ) {
                    return "http://phet.colorado.edu/about/news.php";
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