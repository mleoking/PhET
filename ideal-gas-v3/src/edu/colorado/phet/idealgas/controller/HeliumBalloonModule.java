/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 13, 2003
 * Time: 2:20:47 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.collision.SphereBalloonExpert;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.controller.command.RemoveMoleculeCmd;
import edu.colorado.phet.idealgas.model.*;
import edu.colorado.phet.idealgas.view.HollowSphereGraphic;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Point2D;

public class HeliumBalloonModule extends IdealGasModule implements GasSource, IdealGasModule.ResetListener {

    private static final float initialVelocity = 30;
    private static double MASS = 500;

    private HollowSphere balloon;
    private Class gasSpecies = LightSpecies.class;
    private Object containerAffected;

    public HeliumBalloonModule( AbstractClock clock ) {
        super( clock, SimStrings.get( "ModuleTitle.HeliumBalloon" ) );

        double xOrigin = 200;
        double yOrigin = 250;
        double xDiag = 434;
        double yDiag = 397;

        // So we'll get events sent by parent classes
        this.addListener( this );

        // Add collision experts to the model
        getIdealGasModel().addCollisionExpert( new SphereBalloonExpert( getIdealGasModel(), clock.getDt() ) );

        balloon = new Balloon( new Point2D.Double( 300, 350 ),
                               new Vector2D.Double( 0, 0 ),
                               new Vector2D.Double( 0, 0 ),
                               MASS,
                               Balloon.MIN_RADIUS,
                               getIdealGasModel().getBox() );
        getBox().setMinimumWidth( balloon.getRadius() * 3 );
        getIdealGasModel().addModelElement( balloon );
        getIdealGasModel().getBox().addContainedBody( balloon );
        HollowSphereGraphic graphic = new HollowSphereGraphic( getApparatusPanel(), balloon );
        addGraphic( graphic, 10 );
        Constraint constraintSpec = new BoxMustContainParticle( getIdealGasModel().getBox(), balloon,
                                                                getIdealGasModel() );
        balloon.addConstraint( constraintSpec );

        for( int i = 0; i < 0; i++ ) {
//        for( int i = 0; i < 50; i++ ) {
            double x = Math.random() * ( xDiag - xOrigin - 20 ) + xOrigin + 50;
            double y = Math.random() * ( yDiag - yOrigin - 20 ) + yOrigin + 10;
            double theta = Math.random() * Math.PI * 2;
            double vx = (float)Math.cos( theta ) * initialVelocity;
            double vy = (float)Math.sin( theta ) * initialVelocity;
            double m = 10;
            GasMolecule p1 = new HeavySpecies( new Point2D.Double( x, y ),
                                               new Vector2D.Double( vx, vy ),
                                               new Vector2D.Double( 0, 0 ) );
            new PumpMoleculeCmd( getIdealGasModel(), p1, this ).doIt();
            constraintSpec = new BoxMustContainParticle( getIdealGasModel().getBox(), p1, getIdealGasModel() );
            p1.addConstraint( constraintSpec );

            constraintSpec = new HollowSphereMustNotContainParticle( balloon, p1, getIdealGasModel() );
            p1.addConstraint( constraintSpec );
        }

        // Put some light gas inside the balloon
        int num = 0;
//        int num = 1;
//        int num = 6;
//        int num = 4;
        for( int i = 1; i <= num; i++ ) {
            for( int j = 0; j < num; j++ ) {
                double v = initialVelocity * HeavySpecies.getMoleculeMass() / LightSpecies.getMoleculeMass();
                double theta = Math.random() * Math.PI * 2;
                float vx = (float)( Math.cos( theta ) * v );
                float vy = (float)( Math.sin( theta ) * v );
                GasMolecule p1 = new LightSpecies( new Point2D.Double( 280 + i * 10, 330 + j * 10 ),
                                                   new Vector2D.Double( vx, vy ),
                                                   new Vector2D.Double( 0, 0 ) );
                balloon.addContainedBody( p1 );
//                getIdealGasModel().addModelElement( p1 );
                new PumpMoleculeCmd( getIdealGasModel(), p1, this ).doIt();

                constraintSpec = new BoxMustContainParticle( getIdealGasModel().getBox(), p1, getIdealGasModel() );
                p1.addConstraint( constraintSpec );

                constraintSpec = new HollowSphereMustContainParticle( balloon, p1, getIdealGasModel() );
                p1.addConstraint( constraintSpec );
            }
        }

        // Turn on gravity
//        getIdealGasApplication().setGravityEnabled( true );
//        getIdealGasApplication().setGravity( 15 );

        // Set up the control panel
        JPanel controlPanel = new JPanel( new GridBagLayout() );
        controlPanel.setBorder( new TitledBorder( SimStrings.get( "HeliumBalloonControlPanel.controlsTitle" ) ) );

        GridBagConstraints gbc = null;
        Insets insets = new Insets( 0, 0, 0, 0 );
        gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                      GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                      insets, 0, 0 );
        controlPanel.add( new HeliumBalloonModule.HeliumFactoryPanel(), gbc );
        getIdealGasControlPanel().addParticleControl( controlPanel );
//        getIdealGasControlPanel().addComponent( controlPanel );
    }

    public void setCurrentGasSpecies( Class gasSpecies ) {
        this.gasSpecies = LightSpecies.class;
    }

    public Class getCurrentGasSpecies() {
        return gasSpecies;
    }

    public void removeGasMoleculeFromBalloon() {
        Command cmd = new RemoveMoleculeCmd( getIdealGasModel(), LightSpecies.class );
        cmd.doIt();
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Inner classes
    //

    class HeliumFactoryPanel extends JPanel implements IdealGasModule.ResetListener {

        private int currNumMolecules;

        HeliumFactoryPanel() {

            super( new GridBagLayout() );
            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                             GridBagConstraints.WEST,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 0, 0, 0, 0 ), 0, 0 );

            JLabel label = new JLabel( SimStrings.get( "MeasurementControlPanel.Number_of_particles" ) );
            this.add( label, gbc );
            // Set up the spinner for controlling the number of particles in
            // the hollow sphere
            Integer value = new Integer( 0 );
            Integer min = new Integer( 0 );
            Integer max = new Integer( 1000 );
            Integer step = new Integer( 1 );
            SpinnerNumberModel model = new SpinnerNumberModel( value, min, max, step );
            final JSpinner particleSpinner = new JSpinner( model );
            particleSpinner.setPreferredSize( new Dimension( 50, 20 ) );
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.EAST;
            this.add( particleSpinner, gbc );

            particleSpinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    setNumParticles( ( (Integer)particleSpinner.getValue() ).intValue() );
                }
            } );
//            this.balloon = balloon;
//            this.gasSpecies = gasSpecies;
        }

        protected void setNumParticles( int numParticles ) {
            int dn = numParticles - currNumMolecules;
            if( dn > 0 ) {
                for( int i = 0; i < dn; i++ ) {
                    Class species = getCurrentGasSpecies();
                    Point2D location = balloon.getNewMoleculeLocation();
                    Vector2D velocity = balloon.getNewMoleculeVelocity( species, getIdealGasModel() );
                    GasMolecule molecule = null;
                    if( species == HeavySpecies.class ) {
                        molecule = new HeavySpecies( location, velocity, new Vector2D.Double() );
                    }
                    if( species == LightSpecies.class ) {
                        molecule = new LightSpecies( location, velocity, new Vector2D.Double() );
                    }
                    PumpMoleculeCmd cmd = new PumpMoleculeCmd( getIdealGasModel(), molecule,
                                                               HeliumBalloonModule.this );
                    cmd.doIt();
                    balloon.addContainedBody( molecule );

                }
            }
            else if( dn < 0 ) {
                for( int i = 0; i < -dn; i++ ) {
                    removeGasMolecule();
                }
            }
            currNumMolecules += dn;
        }

        public void resetOccurred( ResetEvent event ) {
            currNumMolecules = 0;
//            super.reset();
        }
    }

//    public class HeliumFactoryPanel extends MoleculeFactoryPanel implements IdealGasModule.ResetListener {
//        private int currNumMolecules;
//
//        public HeliumFactoryPanel() {
//            super( HeliumBalloonModule.this, balloon, gasSpecies );
//            HeliumBalloonModule.this.addListener( this );
//        }
//
//        protected void setNumParticles( int numParticles ) {
//            int dn = numParticles - currNumMolecules;
//            if( dn > 0 ) {
//                for( int i = 0; i < dn; i++ ) {
//                    Class species = getCurrentGasSpecies();
//                    Point2D location = getNewMoleculeLocation();
//                    Vector2D velocity = getNewMoleculeVelocity();
//                    GasMolecule gm = null;
//                    if( species == HeavySpecies.class ) {
//                        gm = new HeavySpecies( location, velocity, new Vector2D.Double() );
//                    }
//                    if( species == LightSpecies.class ) {
//                        gm = new LightSpecies( location, velocity, new Vector2D.Double() );
//                    }
//                    pumpMolecule( gm );
//                }
//            }
//            else if( dn < 0 ) {
//                for( int i = 0; i < -dn; i++ ) {
//                    removeGasMoleculeFromBalloon();
//                }
//            }
//            currNumMolecules += dn;
//        }
//
//        public void resetOccurred( ResetEvent event ) {
//            currNumMolecules = 0;
//            super.reset();
//        }
//    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Event handling
    //
    public void resetOccurred( ResetEvent event ) {
        balloon.setRadius( Balloon.MIN_RADIUS );
    }
}
