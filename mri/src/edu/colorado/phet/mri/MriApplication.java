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

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.mri.controller.HeadModule;
import edu.colorado.phet.mri.controller.MriModuleA;
import edu.colorado.phet.mri.controller.OptionMenu;

/**
 * MriApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriApplication extends PhetApplication {

    private static String title = "Simplified MRI";
    private static String description = "A simplified model of magnetic resonance imaging";
    private static String version = "0.0.1";
    private static FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );

    public MriApplication( String[] args ) {
        super( args, title, description, version, frameSetup );

        setModules( new Module[]{
                new MriModuleA(),
                new HeadModule()
        } );
    }

    protected void parseArgs( String[] args ) {
        super.parseArgs( args );

        for( int i = 0; args != null && i < args.length; i++ ) {
            String arg = args[i];
            if( arg.startsWith( "-d" ) ) {
                PhetUtilities.getPhetFrame().addMenu( new OptionMenu() );
            }
        }
    }

    public static void main( String[] args ) {
        PhetApplication app = new MriApplication( args );
        app.startApplication();
    }
}
