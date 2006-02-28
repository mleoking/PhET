
import edu.colorado.phet.collision.SphereHotAirBalloonExpert;
import edu.colorado.phet.collision.SphericalBody;
import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.controller.AddModelElementCmd;
import edu.colorado.phet.idealgas.controller.HotAirBalloon;
import edu.colorado.phet.idealgas.controller.IdealGasModule;
import edu.colorado.phet.idealgas.controller.PumpMoleculeCmd;
import edu.colorado.phet.idealgas.model.Box2D;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.view.HotAirBalloonGraphic;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

public class TestMoleculeHotAirBalloonCollision extends PhetApplication {

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

            // Add collision experts to the model
            getIdealGasModel().addCollisionExpert( new SphereHotAirBalloonExpert( getIdealGasModel(), clock.getDt() ) );

            final Box2D box = getIdealGasModel().getBox();
            box.setBounds( 300, 100, box.getMaxX(), box.getMaxY() );
            HotAirBalloon sphere = null;
            sphere = new HotAirBalloon( new Point2D.Double( box.getMinX() + box.getWidth() / 2,
                                                            box.getMinY() + box.getHeight() / 2 ),
                                        new Vector2D.Double( 0, 0 ),
                                        new Vector2D.Double( 0, 0 ),
                                        100,
                                        50,
                                        60,
                                        getIdealGasModel() );

            new AddModelElementCmd( getIdealGasModel(), sphere ).doIt();
            getIdealGasModel().getBox().addContainedBody( sphere );
            addGraphic( new HotAirBalloonGraphic( getApparatusPanel(), sphere ), 20 );

//            GasMolecule gm = new HeavySpecies( new Point2D.Double( box.getMinX() + box.getWidth() / 2,
//                                                                    box.getMinY() + box.getHeight() / 2 ),
//                                             new Vector2D.Double( 10, 0 ),
//                                             new Vector2D.Double(),
//                                             5 );
//            PumpMoleculeCmd cmd = new PumpMoleculeCmd( (IdealGasModel)getModel(),
//                                                       gm, TestIdealGasModule.this );
//            cmd.doIt();


            JButton testBtn = new JButton( "Test" );
            final GasMolecule[] m = new GasMolecule[]{null};
            final SphericalBody sphere1 = sphere;
            testBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m[0] = new HeavySpecies( new Point2D.Double( sphere1.getPosition().getX(),
                                                                 sphere1.getPosition().getY() + 60 ),
                                             new Vector2D.Double( 0, -130 ),
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


    public TestMoleculeHotAirBalloonCollision() {
        super( new TestApplicationModel() );
        this.startApplication();
//        this.getApplicationDescriptor().getClock().setPaused( true );
    }

    public static void main( String[] args ) {
        TestMoleculeHotAirBalloonCollision test = new TestMoleculeHotAirBalloonCollision();
    }
}
