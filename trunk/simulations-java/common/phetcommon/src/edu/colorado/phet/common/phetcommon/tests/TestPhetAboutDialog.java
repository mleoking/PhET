/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.phetcommon.tests;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.SwingClock;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;

/**
 * Test to automatically display a PhetAboutDialog
 */
public class TestPhetAboutDialog {
    private PhetApplication phetApplication;
    private PhetAboutDialog phetAboutDialog;

    public TestPhetAboutDialog( String[] args ) {
        String title = "Test PhetAboutDialog";
        String description = "<html>This is the simulation description, which can be (optionally)<br>specified using HTML. It is typically a couple of lines long.</html>";
        String version = "0.01";
        phetApplication = new PhetApplication( args, title, description, version, new FrameSetup.CenteredWithSize( 400, 400 ) );
        Module module = new TestModule( "example module", new SwingClock( 30, 1 ) );
        module.setModel( new BaseModel() );
        phetApplication.addModule( module );
        phetAboutDialog = new PhetAboutDialog( phetApplication );
    }

    public static void main( String[] args ) {
        new TestPhetAboutDialog( args ).start();
    }

    private void start() {
        phetApplication.startApplication();
        phetAboutDialog.show();
    }

    static class TestModule extends Module {
        public TestModule( String name, IClock clock ) {
            super( name, clock );
            setSimulationPanel( new JPanel() );
        }
    }
}
