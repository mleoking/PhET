package edu.colorado.phet.rotation;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.rotation.RotationResources;
import edu.colorado.phet.rotation.torque.*;
import edu.colorado.phet.rotation.controls.RotationDevMenu;
import edu.colorado.phet.rotation.controls.RotationTestMenu;
import edu.colorado.phet.rotation.view.RotationFrameSetup;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;
import edu.umd.cs.piccolox.pswing.PSwingRepaintManager;

/**
 * Author: Sam Reid
 * May 29, 2007, 12:56:31 AM
 */
public class TorqueApplication extends PiccoloPhetApplication {
    private AbstractTorqueModule torqueModule;
    private TorqueIntroModule introModule;
    private MomentOfInertiaModule momentModule;
    private AngularMomentumModule angMomModule;

    public TorqueApplication( String[] args ) {
        super( new PhetApplicationConfig( args, new RotationFrameSetup(), RotationResources.getInstance(), "torque" ) );
        introModule = new TorqueIntroModule( getPhetFrame() );
        torqueModule = new TorqueModule( getPhetFrame() );
        momentModule = new MomentOfInertiaModule( getPhetFrame() );
        angMomModule = new AngularMomentumModule( getPhetFrame() );

        addModule( introModule );
        addModule( torqueModule );
        addModule( momentModule );
        addModule( angMomModule );

//        getPhetFrame().addMenu( new RotationDevMenu( this, torqueModule ) );
//        getPhetFrame().addMenu( new RotationTestMenu() );
    }

    static interface ModuleConstructor {
        Module newModule();
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                MyRepaintManager synchronizedPSwingRepaintManager = new MyRepaintManager();
                synchronizedPSwingRepaintManager.setDoMyCoalesce( true );
                RepaintManager.setCurrentManager( synchronizedPSwingRepaintManager );
                new RotationLookAndFeel().initLookAndFeel();
                final TorqueApplication torqueApplication = new TorqueApplication( args );
                torqueApplication.startApplication();
            }
        } );
    }
}
