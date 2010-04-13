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

public class TranslationUtilityPanel extends PhetPanel {
    public TranslationUtilityPanel( String id, PageContext context ) {

        // TODO: i18nize

        super( id, context );
    }

    public static String getKey() {
        return "forTranslators.translationUtility";
    }

    public static String getUrl() {
        return "for-translators/translation-utility";
    }

    public static RawLinkable getLinker() {
        return new AbstractLinker() {
            @Override
            public String getRawUrl( PageContext context, PhetRequestCycle cycle ) {
                if ( DistributionHandler.redirectPageClassToProduction( cycle, TranslationUtilityPanel.class ) ) {
                    return "http://phet.colorado.edu/contribute/translation-utility.php";
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