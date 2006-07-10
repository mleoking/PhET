/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.tests.piccolo;

import edu.colorado.phet.piccolo.PiccoloPhetApplication;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.model.clock.SwingClock;

import javax.swing.*;

/**
 * TestPiccoloPhetApplication
 * <p>
 * Creates two PiccoloPhetApplications. One has PhetTabbedPanes, the other has JTabbedPanes.
 * Note that they come up on top of each other, and you have to move one of them out of the
 * way to see the other.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class TestPiccoloPhetApplication {

    public static void main( String[] args ) {

        // App with PhetTabbedPanes
        PiccoloPhetApplication phetTabbedPaneApp = new PiccoloPhetApplication( args,
                                                                 "Test App",
                                                                 "Test App Description",
                                                                 "0.00.01" );
        phetTabbedPaneApp.setModules( new Module[]{ new ModuleA(), new ModuleB() } );
        phetTabbedPaneApp.startApplication();

        // App with JTabbedPanes
        PiccoloPhetApplication jtabbedPaneApp = new PiccoloPhetApplication( args,
                                                                 "Test App",
                                                                 "Test App Description",
                                                                 "0.00.01",
                                                                 PiccoloPhetApplication.JTABBED_PANE );
        jtabbedPaneApp.setModules( new Module[]{ new ModuleA(), new ModuleB() } );
        jtabbedPaneApp.startApplication();
    }

    static class ModuleA extends Module {
        public ModuleA() {
            super( "Module A", new SwingClock( 25, 1 ));
            setSimulationPanel( new JButton("A") );
        }
    }

    static class ModuleB extends Module {
        public ModuleB() {
            super( "Module B", new SwingClock( 25, 1 ));
            setSimulationPanel( new JButton("B") );
        }
    }
}
