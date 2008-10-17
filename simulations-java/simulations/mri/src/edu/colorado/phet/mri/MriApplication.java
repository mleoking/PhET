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

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.mri.controller.HeadModule;
import edu.colorado.phet.mri.controller.NmrModule;
import edu.colorado.phet.mri.controller.OptionMenu;

/**
 * MriApplication
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MriApplication extends PiccoloPhetApplication {

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private Module[] singleModule = new Module[]{
            new NmrModule(),
    };

    private Module[] fullAppModules = new Module[]{
            new NmrModule(),
            new HeadModule()
//            ,new ScanModule(),
//            new ScanModuleB(),
    };

    private Module[] modules = fullAppModules;

    public MriApplication( PhetApplicationConfig config ) {
        super( config );
//        PhetTabbedPane.setLogoVisible( false );
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

    private static class MriLookAndFeel extends PhetLookAndFeel {
        public MriLookAndFeel() {
            setFont( new PhetFont( new PhetFont( ).getSize(),true) );
            setTitledBorderFont( new PhetFont( new PhetFont( ).getSize(),true) );
            initLookAndFeel();
        }
    }
    public static void main( final String[] args ) {
        
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new MriApplication( config );
            }
        };
        
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, MriConfig.PROJECT_NAME );
        appConfig.setLookAndFeel( new MriLookAndFeel() );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }
}
