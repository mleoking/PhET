
import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.Strings;
import edu.colorado.phet.idealgas.controller.IdealGasModule;
import edu.colorado.phet.idealgas.controller.PumpMoleculeCmd;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;

import java.awt.geom.Point2D;

public class TestMoleculeBoxCollision extends PhetApplication {

    static class TestApplicationModel extends ApplicationModel {
        public TestApplicationModel() {
            super( Strings.title,
                   Strings.description,
                   IdealGasConfig.VERSION,
                   IdealGasConfig.FRAME_SETUP );

            // Create the clock
            setClock( new SwingTimerClock( IdealGasConfig.s_timeStep,
                                           IdealGasConfig.s_waitTime ) );

            // Create the modules
            Module idealGasModule = new TestIdealGasModule( getClock() );
            Module[] modules = new Module[]{
                idealGasModule
            };
            setModules( modules );
            setInitialModule( idealGasModule );
        }
    }

    static class TestIdealGasModule extends IdealGasModule {
        public TestIdealGasModule( AbstractClock clock ) {
            super( clock );
            GasMolecule m = new HeavySpecies( new Point2D.Double( 400, 300 ),
                                              new Vector2D.Double( 0, 0 ),
                                              new Vector2D.Double(),
                                              5 );
            m.setVelocity( new Vector2D.Double( 0, 200 ) );
            PumpMoleculeCmd cmd = new PumpMoleculeCmd( (IdealGasModel)getModel(),
                                                       m,
                                                       this );
            cmd.doIt();
        }
    }


    public TestMoleculeBoxCollision() {
        super( new TestApplicationModel() );
        this.startApplication();
    }

    public static void main( String[] args ) {
        TestMoleculeBoxCollision test = new TestMoleculeBoxCollision();
    }
}
