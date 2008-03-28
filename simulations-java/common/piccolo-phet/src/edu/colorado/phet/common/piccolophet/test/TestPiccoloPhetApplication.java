/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14676 $
 * Date modified : $Date:2007-04-17 02:58:50 -0500 (Tue, 17 Apr 2007) $
 */
package edu.colorado.phet.common.piccolophet.test;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;

/**
 * TestPiccoloPhetApplication
 * <p/>
 * Creates two PiccoloPhetApplications. One has PhetTabbedPanes, the other has JTabbedPanes.
 * Note that they come up on top of each other, and you have to move one of them out of the
 * way to see the other.
 *
 * @author Ron LeMaster
 * @version $Revision:14676 $
 */
public class TestPiccoloPhetApplication {

    public static void main( String[] args ) {

        // App with PhetTabbedPanes
        PiccoloPhetApplication phetTabbedPaneApp = new PiccoloPhetApplication( args,
                                                                 "Test App",
                                                                 "Test App Description",
                                                                 "0.00.01" );
        phetTabbedPaneApp.setModules( new Module[]{new ModuleA(), new ModuleB()} );
        phetTabbedPaneApp.startApplication();

        // App with JTabbedPanes
        PiccoloPhetApplication jtabbedPaneApp = new PiccoloPhetApplication( args,
                                                              "Test App",
                                                              "Test App Description",
                                                              "0.00.01",
                                                              PiccoloPhetApplication.JTABBED_PANE_TYPE );
        jtabbedPaneApp.setModules( new Module[]{new ModuleA(), new ModuleB()} );
        jtabbedPaneApp.startApplication();
    }

    static class ModuleA extends Module {
        public ModuleA() {
            super( "Module A", new SwingClock( 25, 1 ) );
            setSimulationPanel( new JButton( "A" ) );
        }
    }

    static class ModuleB extends Module {
        public ModuleB() {
            super( "Module B", new SwingClock( 25, 1 ) );
            setSimulationPanel( new JButton( "B" ) );
        }
    }
}
