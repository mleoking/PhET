package edu.colorado.phet.website.content;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.constants.Linkers;
import edu.colorado.phet.website.constants.WebsiteConstants;
import edu.colorado.phet.website.content.simulations.SimulationPage;
import edu.colorado.phet.website.content.simulations.TranslatedSimsPage;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class TranslationUtilityPanel extends PhetPanel {

    public TranslationUtilityPanel( String id, PageContext context ) {

        super( id, context );

        String mailString = "<a " + new Linkers.HelpMailer( "Translation Utility" ).getHref( context, getPhetCycle() ) +
                            "><span class=\"red\">" + WebsiteConstants.HELP_EMAIL + "</span></a>";

        add( new LocalizedText( "checkAlreadyTranslated", "translationUtility.checkAlreadyTranslated", new Object[]{
                TranslatedSimsPage.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "step-download", "translationUtility.general.download", new Object[]{
                Linkers.PHET_TRANSLATION_UTILITY_JAR.getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "step-selectLanguage", "translationUtility.general.selectLanguage", new Object[]{
                mailString
        } ) );

        add( new LocalizedText( "example-hydrogen", "translationUtility.example.downloadHydrogen", new Object[]{
                SimulationPage.getLinker( "hydrogen-atom", "hydrogen-atom" ).getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "common-howTo", "translationUtility.common.howTo", new Object[]{
                Linkers.JAVA_COMMON_STRINGS_JAR.getHref( context, getPhetCycle() ),
                Linkers.FLASH_COMMON_STRINGS_JAR.getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "bug-reports", "translationUtility.bugReports.whatToDo", new Object[]{
                mailString
        } ) );
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