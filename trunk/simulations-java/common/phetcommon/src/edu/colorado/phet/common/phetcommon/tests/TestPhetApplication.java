/**
 * Copyright (C) 2007 - University of Colorado
 *
 * User: New Admin
 * Date: Feb 2, 2007
 * Time: 11:03:32 AM
 */

package edu.colorado.phet.common.phetcommon.tests;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;

public class TestPhetApplication {

    public TestPhetApplication() {

    }

    public static void main( String[] args ) {
        new PhetApplicationLauncher().launchSim( new PhetApplicationConfig( new String[0], null, "phetcommon" ), new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                PhetApplication app = new PhetApplication( config );

                MyModule module = new MyModule();

                module.setModel( new BaseModel() );

                module.setSimulationPanel( new JLabel() );

                app.addModule( module );
                return app;
            }
        } );
    }

    private static class MyModule extends Module {
        public MyModule() {
            super( "Name", new SwingClock( 10, 0.01 ) );
        }

        public void setSimulationPanel( JComponent panel ) {
            super.setSimulationPanel( panel );
        }
    }
}
