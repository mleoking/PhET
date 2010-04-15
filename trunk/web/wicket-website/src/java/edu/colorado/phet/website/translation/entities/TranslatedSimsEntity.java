package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.simulations.TranslationListPanel;
import edu.colorado.phet.website.content.simulations.TranslationLocaleListPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class TranslatedSimsEntity extends TranslationEntity {
    public TranslatedSimsEntity() {
        addString( "simulations.translated.allTranslations" );
        addString( "simulations.translated.language" );
        addString( "simulations.translated.languageTranslated" );
        addString( "simulations.translated.numberofTranslations" );
        addString( "simulations.translated.runNow" );
        addString( "simulations.translated.download" );
        addString( "simulations.translated.language.title" );
        addString( "simulations.translated.untranslated" );
        addString( "simulations.translated.toTranslate" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new TranslationLocaleListPanel( id, context );
            }
        }, "Translated Sims" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new TranslationListPanel( id, context, context.getLocale() );
            }
        }, "Translation list" );
    }

    public String getDisplayName() {
        return "Translated Sims";
    }
}