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

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.view.ApparatusPanel;
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
        ApplicationModel am = new ApplicationModel( "Debug Test", "", "" );
        am.setClock( new SwingTimerClock( 1, 25, AbstractClock.FRAMES_PER_SECOND ) );
        DebugMenuTestModule debugMenuTestModule = new DebugMenuTestModule();
        am.setModules( new Module[]{debugMenuTestModule} );
        am.setInitialModule( debugMenuTestModule );

        PhetApplication app = new PhetApplication( am, args );
        app.startApplication();
    }

    static class DebugMenuTestModule extends Module {
        protected DebugMenuTestModule() {
            super( "Debug Menu Test" );

            BaseModel model = new BaseModel();
            setModel( model );
            ApparatusPanel ap = new ApparatusPanel2( model );
            ap.setBackground( Color.white );
            setApparatusPanel( ap );
        }
    }
}
