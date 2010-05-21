package edu.colorado.phet.website.content;

import org.apache.wicket.markup.html.link.Link;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.InvisibleComponent;
import edu.colorado.phet.website.constants.Images;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.content.about.AboutMainPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.TranslationMainPage;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class ForTranslatorsPanel extends PhetPanel {
    public ForTranslatorsPanel( String id, PageContext context ) {

        // TODO: i18nize

        super( id, context );

        add( TranslationUtilityPanel.getLinker().getLink( "translation-utility-link", context, getPhetCycle() ) );
        Link screenLink = TranslationUtilityPanel.getLinker().getLink( "translation-utility-link-2", context, getPhetCycle() );
        add( screenLink );

        screenLink.add( new StaticImage( "translation-utility-screenshot", Images.TRANSLATION_UTILITY_SMALL, "Translation Utility Screenshot" ) );

        add( AboutMainPanel.getLinker().getLink( "about-phet-link", context, getPhetCycle() ) );

        if ( DistributionHandler.hideWebsiteTranslations( getPhetCycle() ) ) {
            add( new InvisibleComponent( "translate-website" ) );
        }
        else {
            add( TranslationMainPage.getLinker().getLink( "translate-website", context, getPhetCycle() ) );
        }
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