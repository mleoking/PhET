package edu.colorado.phet.website.content;

import edu.colorado.phet.website.DistributionHandler;
import edu.colorado.phet.website.components.LocalizedText;
import edu.colorado.phet.website.content.simulations.SimulationPage;
import edu.colorado.phet.website.content.simulations.TranslatedSimsPage;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;
import edu.colorado.phet.website.util.links.AbstractLinker;
import edu.colorado.phet.website.util.links.RawLinkable;

public class TranslationUtilityPanel extends PhetPanel {

    // TODO: turn into linker? (make a page for misc linkers to things like installers)
    public static final String TRANSLATION_UTILITY_PATH = "/files/translation-utility/translation-utility.jar";

    public TranslationUtilityPanel( String id, PageContext context ) {

        super( id, context );

        // TODO: separate out paths for files that need to be in apache's htdocs

        add( new LocalizedText( "checkAlreadyTranslated", "translationUtility.checkAlreadyTranslated", new Object[]{
                TranslatedSimsPage.getLinker().getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "step-download", "translationUtility.general.download", new Object[]{
                "href=\"" + TRANSLATION_UTILITY_PATH + "\""
        } ) );

        add( new LocalizedText( "step-selectLanguage", "translationUtility.general.selectLanguage", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu?subject=Translation%20Utility\"><span class=\"red\">phethelp@colorado.edu</span></a>"
        } ) );

        add( new LocalizedText( "example-hydrogen", "translationUtility.example.downloadHydrogen", new Object[]{
                SimulationPage.getLinker( "hydrogen-atom", "hydrogen-atom" ).getHref( context, getPhetCycle() )
        } ) );

        add( new LocalizedText( "common-howTo", "translationUtility.common.howTo", new Object[]{
                "href=\"/sims/java-common-strings/java-common-strings_en.jar\"",
                "href=\"/sims/flash-common-strings/flash-common-strings_en.jar\""
        } ) );

        add( new LocalizedText( "bug-reports", "translationUtility.bugReports.whatToDo", new Object[]{
                "<a href=\"mailto:phethelp@colorado.edu?subject=Translation%20Utility\"><span class=\"red\">phethelp@colorado.edu</span></a>"
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