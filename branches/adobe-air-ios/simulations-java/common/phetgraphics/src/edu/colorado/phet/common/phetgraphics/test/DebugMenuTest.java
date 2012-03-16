// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetgraphics.test;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.application.PhetTestApplication;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetgraphics.application.PhetGraphicsModule;
import edu.colorado.phet.common.phetgraphics.view.ApparatusPanel2;

/**
 * DebugMenuTest
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DebugMenuTest {

    public static void main( String[] args ) {
        SwingClock clock = new SwingClock( 1, 25 );
//        ApplicationModel am = new ApplicationModel( "Debug Test", "", "" );
//        am.setClock( clock );
//        DebugMenuTestModule debugMenuTestModule = new DebugMenuTestModule( am.getClock() );
//        am.setModules( new PhetGraphicsModule[]{debugMenuTestModule} );
//        am.setInitialModule( debugMenuTestModule );

        PhetTestApplication app = new PhetTestApplication( args );
        DebugMenuTestModule debugMenuTestModule = new DebugMenuTestModule( clock );
        app.addModule( debugMenuTestModule );
        app.startApplication();
    }

    static class DebugMenuTestModule extends PhetGraphicsModule {
        protected DebugMenuTestModule( IClock clock ) {
            super( "Debug Menu Test", clock );

            BaseModel model = new BaseModel();
            setModel( model );
            ApparatusPanel2 ap = new ApparatusPanel2( clock );
//            ap.setUseOffscreenBuffer( true );
            ap.setBackground( Color.white );
            setApparatusPanel( ap );
        }
    }
}
