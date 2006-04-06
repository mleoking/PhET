/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.mri.controller.MriModuleA;

/**
 * MriApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriApplication extends PhetApplication {

    private static String title = "MRI";
    private static String description = "MRI";
    private static String version = "0.0.1";
    private static FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );

    public MriApplication( String[] args) {
        super( args, title, description, version, frameSetup );

        setModules( new Module[] {
                new MriModuleA()
        });
    }

    public static void main( String[] args ) {
        PhetApplication app = new MriApplication( args );
        app.startApplication();
    }
}
