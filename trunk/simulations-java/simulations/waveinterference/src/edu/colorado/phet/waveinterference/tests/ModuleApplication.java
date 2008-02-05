/*  */
package edu.colorado.phet.waveinterference.tests;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.NonPiccoloPhetApplication;
import edu.colorado.phet.waveinterference.WaveIntereferenceLookAndFeel;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:18:50 AM
 */

public class ModuleApplication {
    public ModuleApplication() {
        new WaveIntereferenceLookAndFeel().initLookAndFeel();
    }

    public void startApplication( String[] args, Module module ) {
        NonPiccoloPhetApplication phetApplication = new NonPiccoloPhetApplication( args, module.getName(), "", "" );
        phetApplication.addModule( module );
        SwingUtilities.updateComponentTreeUI( phetApplication.getPhetFrame() );
        phetApplication.startApplication();
    }
}
