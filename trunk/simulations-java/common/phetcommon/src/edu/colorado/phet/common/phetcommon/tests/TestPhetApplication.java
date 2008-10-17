/**
 * Copyright (C) 2007 - University of Colorado
 *
 * User: New Admin
 * Date: Feb 2, 2007
 * Time: 11:03:32 AM
 */

package edu.colorado.phet.common.phetcommon.tests;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;

public class TestPhetApplication {
    private PhetApplicationConfig config;

    public TestPhetApplication() {
        config = new PhetApplicationConfig( new String[0], new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                PhetApplication app = new PhetApplication( config );

                MyModule module = new MyModule();

                module.setModel( new BaseModel() );

                module.setSimulationPanel( new JLabel() );

                app.addModule( module );
                return app;
            }
        }, "phetcommon" );

    }

    public void start() {
        config.launchSim();
    }

    public static void main( String[] args ) {
        ( new TestPhetApplication() ).start();
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
