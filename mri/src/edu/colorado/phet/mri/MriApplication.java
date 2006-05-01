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
import edu.colorado.phet.mri.controller.ScanModule;
import edu.colorado.phet.piccolo.PiccoloPhetApplication;

/**
 * MriApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriApplication extends PiccoloPhetApplication {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static String title = "Simplified MRI";
    private static String description = "A simplified model of magnetic resonance imaging";
    private static String version = "0.01";
    private static FrameSetup frameSetup = new FrameSetup.CenteredWithSize( 1024, 768 );

    private static Module[] singleModule = new Module[]{
            new MriModuleA(),
    };

    private static Module[] fullAppModules = new Module[]{
            new MriModuleA(),
            new HeadModule(),
            new ScanModule(),
    };
    private static Module[] modules = fullAppModules;


    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    public MriApplication( String[] args ) {
//        super( args, title, description, version, frameSetup, PhetApplication.JTABBED_PANE );
//        super( args, title, description, version, frameSetup );
        super( args, title, description, version, frameSetup, PiccoloPhetApplication.PHET_TABBED_PANE );
        setModules( modules );
    }

    protected void parseArgs( String[] args ) {
        super.parseArgs( args );

        for( int i = 0; args != null && i < args.length; i++ ) {
            String arg = args[i];
            if( arg.startsWith( "-d" ) ) {
                PhetUtilities.getPhetFrame().addMenu( new OptionMenu() );
            }
            if( arg.equals( "-singlemodule" ) ) {
                modules = singleModule;
            }
        }
    }

    public static void main( String[] args ) {
        System.out.println( "MriApplication.main A" );

        PhetApplication app = new MriApplication( args );

        System.out.println( "MriApplication.main B" );

        app.startApplication();

        System.out.println( "MriApplication.main C" );

    }
}
