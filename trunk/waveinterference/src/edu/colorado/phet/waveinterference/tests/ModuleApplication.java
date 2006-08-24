/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.tests;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.waveinterference.WaveIntereferenceLookAndFeel;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:18:50 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class ModuleApplication {
    public ModuleApplication() {
        new WaveIntereferenceLookAndFeel().initLookAndFeel();
    }

    public void startApplication( String[]args, Module module ) {
        PhetApplication phetApplication = new PhetApplication( args, module.getName(), "", "" );
        phetApplication.addModule( module );
        SwingUtilities.updateComponentTreeUI( phetApplication.getPhetFrame() );
        phetApplication.startApplication();
    }
}
