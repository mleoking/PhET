package edu.colorado.phet.rotation.torque;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.piccolophet.PhetApplication;
import edu.colorado.phet.rotation.RotationFrameSetup;
import edu.colorado.phet.rotation.RotationResources;
import edu.colorado.phet.rotation.controls.RotationDevMenu;
import edu.colorado.phet.rotation.controls.RotationTestMenu;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;
import edu.umd.cs.piccolox.pswing.PSwingRepaintManager;

/**
 * Author: Sam Reid
 * May 29, 2007, 12:56:31 AM
 */
public class TorqueApplication extends PhetApplication {
    private AbstractTorqueModule torqueModule;
    private IntroModule introModule;
    private MomentOfInertiaModule momentModule;
    private AngularMomentumModule angMomModule;

    public TorqueApplication( String[] args ) {
        super( new PhetApplicationConfig( args, new RotationFrameSetup(), RotationResources.getInstance(), "torque" ) );
//        introModule = new IntroModule( getPhetFrame() );
        torqueModule = new TorqueModule( getPhetFrame() );
//        momentModule = new MomentOfInertiaModule( getPhetFrame() );
//        angMomModule = new AngularMomentumModule( getPhetFrame() );

//        addModule( introModule );
        addModule( torqueModule );
//        addModule( momentModule );
//        addModule( angMomModule );
        getPhetFrame().addMenu( new RotationDevMenu( this, torqueModule ) );
        getPhetFrame().addMenu( new RotationTestMenu() );
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                PSwingRepaintManager synchronizedPSwingRepaintManager = new PSwingRepaintManager();
                synchronizedPSwingRepaintManager.setDoMyCoalesce( true );
                RepaintManager.setCurrentManager( synchronizedPSwingRepaintManager );
                new RotationLookAndFeel().initLookAndFeel();
                new TorqueApplication( args ).startApplication();
            }
        } );
    }
}
