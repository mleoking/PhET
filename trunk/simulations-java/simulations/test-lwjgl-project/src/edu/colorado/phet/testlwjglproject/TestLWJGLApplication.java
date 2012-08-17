// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane.TabbedModule;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.StartupUtils;

public class TestLWJGLApplication extends PhetApplication {

    public TestLWJGLApplication( PhetApplicationConfig config ) {
        super( config );
        final LWJGLCanvas canvas = LWJGLCanvas.getCanvasInstance( getPhetFrame() );
        addModule( new TabbedModule( canvas ) {{
            Tab[] tabs = new Tab[]{
                    new TestingTab( canvas, TestProjectResources.getString( "sim1.module1" ) ),
                    new TestingTab( canvas, TestProjectResources.getString( "sim1.module2" ) )
            };
            for ( Tab tab : tabs ) {
                addTab( tab );
            }
        }} );
    }

    public static void main( String[] args ) {
        try {
            StartupUtils.setupLibraries();
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        new PhetApplicationLauncher().launchSim( new PhetApplicationConfig( args, "test-lwjgl-project", "sim1" ), TestLWJGLApplication.class );
    }
}
