package edu.colorado.phet.wickettest.translation.entities;

import edu.colorado.phet.wickettest.content.TranslatedSimsPanel;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.PhetPanelFactory;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

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