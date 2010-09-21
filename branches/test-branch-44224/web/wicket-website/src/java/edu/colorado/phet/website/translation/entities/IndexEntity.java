package edu.colorado.phet.website.translation.entities;

import edu.colorado.phet.website.content.IndexPanel;
import edu.colorado.phet.website.panels.PhetPanel;
import edu.colorado.phet.website.translation.PhetPanelFactory;
import edu.colorado.phet.website.util.PageContext;
import edu.colorado.phet.website.util.PhetRequestCycle;

public class IndexEntity extends TranslationEntity {
    public IndexEntity() {
        addString( "home.header" );
        addString( "home.subheader" );
        addString( "home.playWithSims" );
        addString( "home.runOurSims" );
        addString( "home.onLine" );
        addString( "home.fullInstallation" );
        addString( "home.oneAtATime" );
        addString( "home.teacherIdeasAndActivities" );
        addString( "home.browseActivities" );
        addString( "home.contribute" );
        addString( "home.workshops" );
        addString( "home.submitActivity" );
        addString( "home.supportPhet" );
        addString( "home.translateSimulations" );
        addString( "home.translateWebsite" );
        addString( "home.browseSims" );
        addString( "home.simulations" );
        addString( "home.rotator.next" );
        addString( "home.rotator.previous" );
        addString( "home.about" );
        addString( "home.about.phet" );
        addString( "home.about.news" );
        addString( "home.about.contact" );
        addString( "home.about.sponsors" );

        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new IndexPanel( id, context );
            }
        }, "Home Page" );
    }

    public String getDisplayName() {
        return "Home";
    }

    @Override
    public int getMinDisplaySize() {
        return 765;
    }
}
