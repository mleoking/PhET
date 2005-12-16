/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.tests;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.PhetGraphicsModule;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common.view.ApparatusPanel2;

import java.awt.*;

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

        PhetApplication app = new PhetApplication( args, "title", "desc", "version" );
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
