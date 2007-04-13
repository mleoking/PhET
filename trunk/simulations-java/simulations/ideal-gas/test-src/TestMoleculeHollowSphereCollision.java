
import edu.colorado.phet.collision.SphereHollowSphereExpert;
import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.controller.AddModelElementCmd;
import edu.colorado.phet.idealgas.controller.IdealGasModule;
import edu.colorado.phet.idealgas.controller.PumpMoleculeCmd;
import edu.colorado.phet.idealgas.model.*;
import edu.colorado.phet.idealgas.view.HollowSphereGraphic;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

public class TestMoleculeHollowSphereCollision extends PhetApplication {

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
            getIdealGasModel().addCollisionExpert( new SphereHollowSphereExpert( getIdealGasModel(), clock.getDt() ) );

            final Box2D box = getIdealGasModel().getBox();
            box.setBounds( 300, 100, box.getMaxX(), box.getMaxY() );
            HollowSphere sphere = null;
            sphere = new HollowSphere( new Point2D.Double( box.getMinX() + box.getWidth() / 2,
                                                           box.getMinY() + box.getHeight() / 2 ),
                                       new Vector2D.Double( 0, -100 ),
                                       new Vector2D.Double( 0, 0 ),
                                       100,
                                       50 );

            new AddModelElementCmd( getIdealGasModel(), sphere ).doIt();
            getIdealGasModel().getBox().addContainedBody( sphere );
            addGraphic( new HollowSphereGraphic( getApparatusPanel(), sphere ), 20 );

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
            testBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    m[0] = new HeavySpecies( new Point2D.Double( box.getMinX() + box.getWidth() / 2 + 80,
                                                                 box.getMinY() + box.getHeight() / 2 ),
                                             new Vector2D.Double( -250, 0 ),
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


    public TestMoleculeHollowSphereCollision() {
        super( new TestApplicationModel() );
        this.startApplication();
//        this.getApplicationDescriptor().getClock().setPaused( true );
    }

    public static void main( String[] args ) {
        TestMoleculeHollowSphereCollision test = new TestMoleculeHollowSphereCollision();
    }
}
