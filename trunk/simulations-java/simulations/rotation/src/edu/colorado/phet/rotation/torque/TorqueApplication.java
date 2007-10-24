package edu.colorado.phet.rotation.torque;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.piccolophet.PhetApplication;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.rotation.RotationFrameSetup;
import edu.colorado.phet.rotation.RotationResources;
import edu.colorado.phet.rotation.controls.RotationDevMenu;
import edu.colorado.phet.rotation.controls.RotationTestMenu;
import edu.colorado.phet.rotation.view.RotationLookAndFeel;

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
        ModuleConstructor mc = new ModuleConstructor() {
            public Module newModule() {
                return new TorqueModule( getPhetFrame() );
            }
        };
        addDelayedModule( "delay 1", mc );
        addDelayedModule( "delay 2", mc );
        addDelayedModule( "delay 3", mc );
        addDelayedModule( "delay 4", mc );
        addDelayedModule( "delay 5", mc );
//        addModule( momentModule );
//        addModule( angMomModule );
        getPhetFrame().addMenu( new RotationDevMenu( this, torqueModule ) );
        getPhetFrame().addMenu( new RotationTestMenu() );
    }

    static interface ModuleConstructor {
        Module newModule();
    }

    static class DeferModule2 extends PiccoloModule {
        private ModuleConstructor moduleConstructor;
        private Module module;

        public DeferModule2( String name, ModuleConstructor moduleConstructor ) {
            super( name, new ConstantDtClock( 10000, 1 ) );
            this.moduleConstructor = moduleConstructor;
            setModel( new BaseModel() );
            setSimulationPanel( new JPanel() );
        }

        public void activate() {
            super.activate();
            if ( module == null ) {
                module = moduleConstructor.newModule();
                setSimulationPanel( module.getSimulationPanel() );
                setClockControlPanel( module.getClockControlPanel() );
                setHelpPanel( module.getHelpPanel() );
                setMonitorPanel( module.getModulePanel() );
                setModel( module.getModel() );
                setControlPanel( module.getControlPanel() );
            }
        }

    }

    private void addDelayedModule( String name, ModuleConstructor torqueModule ) {
        addModule( new DeferModule2( name, torqueModule ) );
    }

    public static void main( final String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
//                PSwingRepaintManager synchronizedPSwingRepaintManager = new PSwingRepaintManager();
//                synchronizedPSwingRepaintManager.setDoMyCoalesce( true );
//                RepaintManager.setCurrentManager( synchronizedPSwingRepaintManager );
                new RotationLookAndFeel().initLookAndFeel();
                new TorqueApplication( args ).startApplication();
            }
        } );
    }
}
