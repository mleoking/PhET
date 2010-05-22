package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.ForTranslatorsPanel;
import edu.colorado.phet.website.content.TranslationUtilityPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class TranslationUtilityEntity extends TranslationEntity {
    public TranslationUtilityEntity() {

        addString( "forTranslators.translatingSimulations" );
        addString( "forTranslators.simulationsTranslatable" );
        addString( "forTranslators.translationUtilityScreenshot" );
        addString( "forTranslators.translatingMission" );
        addString( "forTranslators.translatingTheWebsite" );
        addString( "forTranslators.websiteText" );

        addString( "translationUtility.checkAlreadyTranslated" );
        addString( "translationUtility.pleaseRead" );
        addString( "translationUtility.newOrEdit" );
        addString( "translationUtility.header.generalInstructions" );
        addString( "translationUtility.general.download" );
        addString( "translationUtility.general.downloadSimJar" );
        addString( "translationUtility.general.doubleClickUtility" );
        addString( "translationUtility.general.enterJarPath" );
        addString( "translationUtility.general.selectLanguage" );
        addString( "translationUtility.general.pressContinue" );
        addString( "translationUtility.general.enterStrings" );
        addString( "translationUtility.general.pressTest" );
        addString( "translationUtility.general.saveAndLoad" );
        addString( "translationUtility.general.submitToPhet" );
        addString( "translationUtility.general.emailTranslationFile" );
        addString( "translationUtility.header.exampleUsage" );
        addString( "translationUtility.frenchExample" );
        addString( "translationUtility.example.downloadUtility" );
        addString( "translationUtility.example.downloadHydrogen" );
        addString( "translationUtility.example.doubleClickUtility" );
        addString( "translationUtility.example.pressBrowse" );
        addString( "translationUtility.example.selectFrench" );
        addString( "translationUtility.example.pressContinue" );
        addString( "translationUtility.example.enterTranslations" );
        addString( "translationUtility.example.pressTest" );
        addString( "translationUtility.example.saveWork" );
        addString( "translationUtility.example.pressSubmit" );
        addString( "translationUtility.example.emailProperties" );
        addString( "translationUtility.header.commonStrings" );
        addString( "translationUtility.common.description" );
        addString( "translationUtility.common.howTo" );
        addString( "translationUtility.header.caveats" );
        addString( "translationUtility.header.bugReports" );
        addString( "translationUtility.bugReports.whatToDo" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new ForTranslatorsPanel( id, context );
            }
        }, "For Translators" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new TranslationUtilityPanel( id, context );
            }
        }, "Translation Utility" );
    }

    public String getDisplayName() {
        return "Translations";
    }
}
