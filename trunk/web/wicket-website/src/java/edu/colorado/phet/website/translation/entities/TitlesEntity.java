package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.translation.previews.TitlePreviewPanel;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class TitlesEntity extends TranslationEntity {
    public TitlesEntity() {
        // TODO: figure out a better way than grouping all titles together

        addString( "home.title", "Title of the home page." );
        addString( "simulations.translated.title", "Title of the Translated Sims page." );
        addString( "simulationPage.title", "Title for a particular simulation. {0} will be replaced by the simulation title, and {1} {2} and {3} will be replaced by keywords." );
        addString( "simulationDisplay.title", "Title for a category of simulations. {0} will be replaced by the translated category name (like nav.motion)." );
        addString( "about.source-code.title", "Title of the Source Code About page" );
        addString( "about.legend.title", "Title of the Legend About page" );
        addString( "about.contact.title", "Title of the Contact About page" );
        addString( "about.who-we-are.title", "Title of the Who We Are About page" );
        addString( "about.licensing.title", "Title of the Licensing About page" );
        addString( "sponsors.title", "Title of the PhET Sponsors page" );
        addString( "workshops.title", "Title of the Workshops page" );
        addString( "get-phet.title", "Title of the 'Run our Simulations' page" );
        addString( "get-phet.one-at-a-time.title", "Title of the 'One at a Time' page" );
        addString( "get-phet.full-install.title", "Title of the 'Full Install' page" );
        addString( "research.title", "Title of the 'Research' page" );
        addString( "troubleshooting.java.title", "Title of the 'Java Troubleshooting' page" );
        addString( "troubleshooting.flash.title", "Title of the 'Flash Troubleshooting' page" );
        addString( "troubleshooting.javascript.title", "Title of the 'JavaScript Troubleshooting' page" );
        addString( "register.title" );
        addString( "editProfile.title" );
        addString( "signIn.title" );
        addString( "signOut.title" );
        addString( "forTranslators.title" );
        addString( "forTranslators.translationUtility.title" );
        addString( "forTranslators.website.title" );
        addString( "contribution.search.title" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new TitlePreviewPanel( id, context );
            }
        }, "Assorted Titles" );
    }

    public String getDisplayName() {
        return "Titles";
    }
}
