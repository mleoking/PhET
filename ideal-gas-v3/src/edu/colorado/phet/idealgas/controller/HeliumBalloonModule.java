/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 13, 2003
 * Time: 2:20:47 PM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.collision.SphereHollowSphereExpert;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.Command;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.idealgas.IdealGasConfig;
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

    private static double MASS = 500;

    private HollowSphere balloon;
    private Class gasSpecies = LightSpecies.class;
    private int defaultGravity = IdealGasConfig.MAX_GRAVITY / 2;

    public HeliumBalloonModule( AbstractClock clock ) {
        super( clock, SimStrings.get( "ModuleTitle.HeliumBalloon" ) );

        // So we'll get events sent by parent classes
        this.addResetListener( this );

        // Add collision experts to the model
        getIdealGasModel().addCollisionExpert( new SphereHollowSphereExpert() );

        // Create the balloon
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

        // Turn on gravity
        setGravity( defaultGravity );
    }

    protected Pump.PumpingEnergyStrategy getPumpingEnergyStrategy() {
        return new Pump.FixedEnergyStrategy();
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
        private JSpinner particleSpinner;

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
            particleSpinner = new JSpinner( model );
            particleSpinner.setPreferredSize( new Dimension( 50, 20 ) );
            gbc.gridx = 1;
            gbc.anchor = GridBagConstraints.EAST;
            this.add( particleSpinner, gbc );

            particleSpinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    setNumParticles( ( (Integer)particleSpinner.getValue() ).intValue() );
                }
            } );

            HeliumBalloonModule.this.addResetListener( this );
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
                    removeGasMolecule( LightSpecies.class );
                }
            }
            currNumMolecules += dn;
        }

        public void resetOccurred( ResetEvent event ) {
            currNumMolecules = 0;
            particleSpinner.setEnabled( false );
            particleSpinner.setValue( new Integer( 0 ) );
            particleSpinner.setEnabled( true );
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////
    // Event handling
    //
    public void resetOccurred( ResetEvent event ) {
        balloon.setRadius( Balloon.MIN_RADIUS );
    }
}
