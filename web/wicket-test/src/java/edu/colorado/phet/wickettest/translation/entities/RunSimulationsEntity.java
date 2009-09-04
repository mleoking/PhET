package edu.colorado.phet.wickettest.translation.entities;

import edu.colorado.phet.wickettest.content.OneAtATimePanel;
import edu.colorado.phet.wickettest.content.RunOurSimulationsPanel;
import edu.colorado.phet.wickettest.panels.PhetPanel;
import edu.colorado.phet.wickettest.translation.PhetPanelFactory;
import edu.colorado.phet.wickettest.util.PageContext;
import edu.colorado.phet.wickettest.util.PhetRequestCycle;

public class RunSimulationsEntity extends TranslationEntity {

    public RunSimulationsEntity() {
        addString( "get-phet.clickHere" );
        addString( "get-phet.header" );
        addString( "get-phet.install.header" );
        addString( "get-phet.install.howLarge" );
        addString( "get-phet.install.howOften" );
        addString( "get-phet.install.howToGet" );
        addString( "get-phet.install.needInternet" );
        addString( "get-phet.install.whatSims" );
        addString( "get-phet.install.whereCanISave" );
        addString( "get-phet.offline.header" );
        addString( "get-phet.offline.howLarge" );
        addString( "get-phet.offline.howOften" );
        addString( "get-phet.offline.howToGet" );
        addString( "get-phet.offline.needInternet" );
        addString( "get-phet.offline.whatSims" );
        addString( "get-phet.offline.whereCanISave" );
        addString( "get-phet.online.header" );
        addString( "get-phet.online.howLarge" );
        addString( "get-phet.online.howOften" );
        addString( "get-phet.online.howToGet" );
        addString( "get-phet.online.needInternet" );
        addString( "get-phet.online.whatSims" );
        addString( "get-phet.online.whereCanISave" );
        addString( "get-phet.row.howLarge" );
        addString( "get-phet.row.howOften" );
        addString( "get-phet.row.howToGet" );
        addString( "get-phet.row.needInternet" );
        addString( "get-phet.row.whatSims" );
        addString( "get-phet.row.whereCanISave" );


        addString( "get-phet.one-at-a-time.runningSims" );
        addString( "get-phet.one-at-a-time.downloadingSims" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new RunOurSimulationsPanel( id, context );
            }
        }, "Run Simulations" );
        addPreview( new PhetPanelFactory() {
            public PhetPanel getNewPanel( String id, PageContext context, PhetRequestCycle requestCycle ) {
                return new OneAtATimePanel( id, context );
            }
        }, "Run Simulations - One at a Time" );
    }

    public String getDisplayName() {
        return "Run Simulations";
    }
}