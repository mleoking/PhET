/**
 * Class: RigidHollowSphereModule
 * Class: edu.colorado.phet.idealgas.controller
 * User: Ron LeMaster
 * Date: Sep 18, 2004
 * Time: 12:35:56 PM
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.collision.SphereHollowSphereExpert;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.IdealGasStrings;
import edu.colorado.phet.idealgas.model.*;
import edu.colorado.phet.idealgas.view.HollowSphereGraphic;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Point2D;

public abstract class RigidHollowSphereModule extends IdealGasModule {

    private static final float initialVelocity = 35;

    private HollowSphere sphere;
    private int numMoleculesInSphere;
    private Class gasSpecies;

    public RigidHollowSphereModule( AbstractClock clock, String name, Class gasSpecies ) {
        super( clock, name );
        this.gasSpecies = gasSpecies;

        double xOrigin = 200;
        double yOrigin = 250;
        double xDiag = 434;
        double yDiag = 397;

        // Add collision experts to the model
        getIdealGasModel().addCollisionExpert( new SphereHollowSphereExpert( getIdealGasModel(), clock.getDt() ) );

        // Set the size of the box
        final Box2D box = getIdealGasModel().getBox();
        box.setBounds( 300, 100, box.getMaxX(), box.getMaxY() );
        sphere = new HollowSphere( new Point2D.Double( box.getMinX() + box.getWidth() / 2,
                                                       box.getMinY() + box.getHeight() / 2 ),
                                   new Vector2D.Double( 0, 0 ),
                                   new Vector2D.Double( 0, 0 ),
                                   100,
                                   50 );
        box.setMinimumWidth( sphere.getRadius() * 3 );

        new AddModelElementCmd( getIdealGasModel(), sphere ).doIt();
        getIdealGasModel().getBox().addContainedBody( sphere );
        addGraphic( new HollowSphereGraphic( getApparatusPanel(), sphere ), 20 );

        //        Constraint constraintSpec = new BoxMustContainParticle( box, sphere, getIdealGasModel() );
        //        sphere.addConstraint( constraintSpec );

        // Put some heavy gas outside the sphere
        for( int i = 0; i < 0; i++ ) {
            //        for( int i = 0; i < 100; i++ ) {
            double x = Math.random() * ( xDiag - xOrigin - 20 ) + xOrigin + 50;
            double y = Math.random() * ( yDiag - yOrigin - 20 ) + yOrigin + 10;
            double theta = Math.random() * Math.PI * 2;
            double vx = (double)Math.cos( theta ) * initialVelocity;
            double vy = (double)Math.sin( theta ) * initialVelocity;
            GasMolecule p1 = new HeavySpecies( new Point2D.Double( x, y ),
                                               new Vector2D.Double( vx, vy ),
                                               new Vector2D.Double( 0, 0 ) );
            new PumpMoleculeCmd( getIdealGasModel(), p1, this ).doIt();
            //            constraintSpec = new BoxMustContainParticle( box, p1, getIdealGasModel() );
            //            p1.addConstraint( constraintSpec );
            //
            //            constraintSpec = new HollowSphereMustNotContainParticle( sphere, p1 );
            //            p1.addConstraint( constraintSpec );
        }

        // Put some heavy gas in the sphere
        GasMolecule p1 = null;
//        int num = 0;
        //        int num = 6;
        int num = 4;
        for( int i = 1; i <= num; i++ ) {
            for( int j = 0; j < num; j++ ) {
                double v = initialVelocity;
                double theta = Math.random() * Math.PI * 2;
                double vx = Math.cos( theta ) * v;
                double vy = Math.sin( theta ) * v;
                if( HeavySpecies.class.isAssignableFrom( gasSpecies ) ) {
                    p1 = new HeavySpecies( new Point2D.Double( 350 + i * 10, 230 + j * 10 ),
                                           //                        new Point2D.Double( 280 + i * 10, 330 + j * 10 ),
                                           new Vector2D.Double( vx, vy ),
                                           new Vector2D.Double( 0, 0 ) );
                    new PumpMoleculeCmd( getIdealGasModel(), p1, this ).doIt();
                }
                if( LightSpecies.class.isAssignableFrom( gasSpecies ) ) {
                    p1 = new LightSpecies( new Point2D.Double( 350 + i * 10, 230 + j * 10 ),
                                           //                        new Point2D.Double( 280 + i * 10, 330 + j * 10 ),
                                           new Vector2D.Double( vx, vy ),
                                           new Vector2D.Double( 0, 0 ));
                    new PumpMoleculeCmd( getIdealGasModel(), p1, this ).doIt();
                }
                sphere.addContainedBody( p1 );

                //                constraintSpec = new BoxMustContainParticle( box, p1 );
                //                p1.addConstraint( constraintSpec );
                //
                //                constraintSpec = new HollowSphereMustContainParticle( sphere, p1 );
                //                p1.addConstraint( constraintSpec );
            }
        }

        // Turn on gravity
        //        getIdealGasApplication().setGravityEnabled( true );
        //        getIdealGasApplication().setGravity( 15 );


        // Add the specific controls we need to the control panel
        //        hsaControlPanel = new HollowSphereControlPanel( getIdealGasApplication() );
        //        JPanel mainControlPanel = getIdealGasApplication().getPhetMainPanel().getControlPanel();
        //        mainControlPanel.add( hsaControlPanel );
        //        hsaControlPanel.setGasSpeciesClass( LightSpecies.class );
        getControlPanel().add( new NumParticlesControls() );
    }

    class RigidSphereControlPanel extends JPanel {
        public RigidSphereControlPanel() {
            this.add( new JLabel( IdealGasStrings.get( "MeasurementControlPanel.Number_of_particles" ) ));
            // Set up the spinner for controlling the number of particles in
            // the hollow sphere
            Integer value = new Integer( 0 );
            Integer min = new Integer( 0 );
            Integer max = new Integer( 1000 );
            Integer step = new Integer( 1 );
            SpinnerNumberModel model = new SpinnerNumberModel( value, min, max, step );
            final JSpinner particleSpinner = new JSpinner( model );
            particleSpinner.setPreferredSize( new Dimension( 20, 5 ) );
            this.add( particleSpinner );

            particleSpinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    setNumParticlesInSphere( ( (Integer)particleSpinner.getValue() ).intValue() );
                }
            } );

        }

        private void setNumParticlesInSphere( int numParticles ) {
            int dn = numParticles - numMoleculesInSphere;
            if( dn > 0 ) {
                for( int i = 0; i < dn; i++ ) {
                    pumpGasMolecules( 1 );
                }
            }
            else if( dn < 0 ) {
                for( int i = 0; i < -dn; i++ ) {
                    removeGasMolecule();
                }
            }
        }
    }

    private class NumParticlesControls extends JPanel {

        NumParticlesControls() {
            super( new GridLayout( 1, 2 ) );
            this.setPreferredSize( new Dimension( IdealGasConfig.CONTROL_PANEL_WIDTH, 40 ) );
            this.setLayout( new GridLayout( 2, 1 ) );

            this.add( new JLabel( IdealGasStrings.get( "MeasurementControlPanel.Number_of_particles" ) ));
            // Set up the spinner for controlling the number of particles in
            // the hollow sphere
            Integer value = new Integer( 0 );
            Integer min = new Integer( 0 );
            Integer max = new Integer( 1000 );
            Integer step = new Integer( 1 );
            SpinnerNumberModel model = new SpinnerNumberModel( value, min, max, step );
            final JSpinner particleSpinner = new JSpinner( model );
            particleSpinner.setPreferredSize( new Dimension( 20, 5 ) );
            this.add( particleSpinner );

            particleSpinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    setNumParticlesInSphere( ( (Integer)particleSpinner.getValue() ).intValue() );
                }
            } );
        }

        private void setNumParticlesInSphere( int numParticles ) {
            int dn = numParticles - numMoleculesInSphere;
            if( dn > 0 ) {
                for( int i = 0; i < dn; i++ ) {
                    Class species = getPump().getCurrentGasSpecies();
                    GasMolecule gm = null;
                    if( species == HeavySpecies.class ) {
                        gm = new HeavySpecies( sphere.getPosition(), new Vector2D.Double(), new Vector2D.Double() );
                    }
                    if( species == LightSpecies.class ) {
                        gm = new LightSpecies( sphere.getPosition(), new Vector2D.Double(), new Vector2D.Double() );
                    }
                    PumpMoleculeCmd cmd = new PumpMoleculeCmd( (IdealGasModel)getModel(), gm,
                                                               RigidHollowSphereModule.this );
                    cmd.doIt();
                }
            }
            else if( dn < 0 ) {
                for( int i = 0; i < -dn; i++ ) {
                    removeGasMolecule();
                }
            }
        }
    }

}
