package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.TranslatedSimsPanel;
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

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new TranslatedSimsPanel( id, context );
            }
        }, "Translated Sims" );
    }

    public String getDisplayName() {
        return "Translated Sims";
    }
}