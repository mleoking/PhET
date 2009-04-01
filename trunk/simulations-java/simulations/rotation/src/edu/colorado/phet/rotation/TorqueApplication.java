package edu.colorado.phet.rotation;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.*;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.rotation.torque.*;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;

/**
 * Author: Sam Reid
 * May 29, 2007, 12:56:31 AM
 */
public class TorqueApplication extends PiccoloPhetApplication {
    private AbstractTorqueModule torqueModule;
    private TorqueIntroModule introModule;
    private MomentOfInertiaModule momentModule;
    private AngularMomentumModule angMomModule;

    public TorqueApplication( PhetApplicationConfig torqueApplicationConfig ) {
        super( torqueApplicationConfig );
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
        PhetApplicationConfig config = new PhetApplicationConfig( args,"rotation","torque" );
        new PhetApplicationLauncher().launchSim( config, new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {

                MyRepaintManager synchronizedPSwingRepaintManager = new MyRepaintManager();
                synchronizedPSwingRepaintManager.setDoMyCoalesce( true );
                RepaintManager.setCurrentManager( synchronizedPSwingRepaintManager );
                new RotationLookAndFeel().initLookAndFeel();
                return new TorqueApplication( config );
            }
        } );
    }

}
