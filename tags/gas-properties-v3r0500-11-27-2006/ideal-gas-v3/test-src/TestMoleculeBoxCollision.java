
import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.controller.IdealGasModule;
import edu.colorado.phet.idealgas.controller.PumpMoleculeCmd;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

public class TestMoleculeBoxCollision extends PhetApplication {

    static class TestApplicationModel extends ApplicationModel {

        public TestApplicationModel() {
            super( SimStrings.get( "IdealGasApplication.title" ),
                   SimStrings.get( "IdealGasApplication.description" ),
                   IdealGasConfig.VERSION,
                   IdealGasConfig.FRAME_SETUP );

            // Create the clock
            SwingTimerClock clock = new SwingTimerClock( IdealGasConfig.s_timeStep,
                                                         IdealGasConfig.s_waitTime );
            setClock( clock );

            // Create the modules
            Module idealGasModule = new TestIdealGasModule( getClock() );
            Module idealGasModule2 = new TestIdealGasModule( getClock() );
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
            JButton testBtn = new JButton( "Test" );
            final GasMolecule[] m = new GasMolecule[]{null};
            testBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m[0] = new HeavySpecies( new Point2D.Double( 500, 279.1 ),
                                             new Vector2D.Double( -30, 0 ),
                                             new Vector2D.Double() );
//                    m[0] = new HeavySpecies( new Point2D.Double( 383.3, 389.35 ),
//                                                      new Vector2D.Double( -217.8, 999.85 ),
//                                                      new Vector2D.Double(),
//                                                      5 );
//                  m = new HeavySpecies( new Point2D.Double( 510, 177 ),
//                                                      new Vector2D.Double( 0, 0 ),
//                                                      new Vector2D.Double(),
//                                                      5 );
//                    m.setVelocity( new Vector2D.Double( 109, -500 ) );
                    PumpMoleculeCmd cmd = new PumpMoleculeCmd( (IdealGasModel)getModel(),
                                                               m[0],
                                                               TestIdealGasModule.this );
                    cmd.doIt();
                }
            } );
            getControlPanel().add( testBtn );

        }
    }


    public TestMoleculeBoxCollision() {
        super( new TestApplicationModel() );

//        getApplicationDescriptor().getClock().addClockTickListener( new ClockTickListener() {
//            public void clockTicked( AbstractClock c, double dt ) {
//                getApplicationView().getBasicPhetPanel().repaint();
//            }
//        } );

        this.startApplication();
    }

    public static void main( String[] args ) {
        TestMoleculeBoxCollision test = new TestMoleculeBoxCollision();
    }
}
