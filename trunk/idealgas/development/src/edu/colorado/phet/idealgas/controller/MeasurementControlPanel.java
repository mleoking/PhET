/**
 * Class: MeasurementControlPanel
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Jan 20, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.controller.command.Command;
import edu.colorado.phet.graphics.util.GraphicsUtil;
import edu.colorado.phet.idealgas.physics.HeavySpecies;
import edu.colorado.phet.idealgas.physics.GasMolecule;
import edu.colorado.phet.idealgas.physics.LightSpecies;
import edu.colorado.phet.idealgas.physics.body.Particle;
import edu.colorado.phet.physics.body.Body;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Observer;
import java.util.Observable;
import java.util.List;

public class MeasurementControlPanel extends IdealGasControlPanel {
    public MeasurementControlPanel( PhetApplication application ) {
        super( application );
        init();
    }

    private void init() {
        this.remove( getGravityControlPanel() );
        addLinebergerControls();
    }

    private void addLinebergerControls() {
        JPanel linebergerPanel = new JPanel( new GridBagLayout() );
        int rowIdx = 0;
        try {
            GraphicsUtil.addGridBagComponent( linebergerPanel, new NumParticlesControls(),
                                              1, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.WEST );
            GraphicsUtil.addGridBagComponent( linebergerPanel, new ParticleInteractionControl(),
                                              1, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.WEST );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
        this.add( linebergerPanel );
    }

    private void setNumParticlesInBox( int numParticles ) {
        int dn = numParticles - ( HeavySpecies.getNumMolecules().intValue()
                + LightSpecies.getNumMolecules().intValue() );
        if( dn > 0 ) {
            for( int i = 0; i < dn; i++ ) {
                pumpGasMolecule();
            }
        }
        else if( dn < 0 ) {
            Particle particle = null;
            for( int i = 0; i < -dn; i++ ) {
                List bodies = PhetApplication.instance().getPhysicalSystem().getBodies();
                for( int j = 0; particle == null && j < bodies.size(); j++ ) {
                    Body body = (Body)bodies.get( j );
                    if( body instanceof GasMolecule ) {
                        particle = (Particle)body;
                    }
                }
                Command cmd = new RemoveBodyCmd( particle );
                PhetApplication.instance().getPhysicalSystem().addPrepCmd( cmd );
                HeavySpecies.removeParticle( particle );
                PhetApplication.instance().getPhetMainPanel().getApparatusPanel().removeBody( particle );
            }
        }
    }


    //
    // Inner classes
    //
    private class NumParticlesControls extends JPanel {

        NumParticlesControls() {
            super( new GridLayout( 1, 2 ) );
            this.setPreferredSize( new Dimension( IdealGasConfig.CONTROL_PANEL_WIDTH, 40 ) );
            this.setLayout( new GridLayout( 2, 1 ) );

            this.add( new JLabel( "Number of particles" ) );
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
                    setNumParticlesInBox( ( (Integer)particleSpinner.getValue() ).intValue() );
                }
            } );

            // Hook the spinner up so it will track molecules put in the box by the pump
            PhetApplication.instance().getPhysicalSystem().addObserver( new Observer() {
                public void update( Observable o, Object arg ) {
                    int h =  HeavySpecies.getNumMolecules().intValue();
                    int l = LightSpecies.getNumMolecules().intValue();
                    particleSpinner.setValue( new Integer( l + h ));
                }
            } );
        }
    }

    private class ParticleInteractionControl extends JPanel {
        ParticleInteractionControl() {
            final JCheckBox noSphereSphereCollisionCB = new JCheckBox( "No particle interactions" );
            this.add( noSphereSphereCollisionCB );
            noSphereSphereCollisionCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    GasMolecule.enableParticleParticleInteractions( !noSphereSphereCollisionCB.isSelected() );
                }
            } );

            // Set default state
            noSphereSphereCollisionCB.setSelected( true );
            GasMolecule.enableParticleParticleInteractions( !noSphereSphereCollisionCB.isSelected() );
        }
    }

}
