/**
 * Class: MeasurementControlPanel
 * Package: edu.colorado.phet.idealgas.controller
 * Author: Another Guy
 * Date: Jan 20, 2004
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.GasMolecule;
import edu.colorado.phet.idealgas.model.HeavySpecies;
import edu.colorado.phet.idealgas.model.LightSpecies;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MeasurementControlPanel extends IdealGasControlPanel {
    public MeasurementControlPanel( IdealGasModule module ) {
        super( module );
        init();
    }

    private void init() {
        this.remove( getGravityControlPanel() );
        this.add( new PressureSliceControl() );
        this.add( new RulerControlPanel() );
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
                getModule().pumpGasMolecules( 1 );
            }
        }
        else if( dn < 0 ) {
            for( int i = 0; i < -dn; i++ ) {
                getModule().removeGasMolecule();
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
            getModule().getModel().addObserver( new SimpleObserver() {
                public void update() {
                    int h = HeavySpecies.getNumMolecules().intValue();
                    int l = LightSpecies.getNumMolecules().intValue();
                    particleSpinner.setValue( new Integer( l + h ) );
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
            noSphereSphereCollisionCB.setSelected( false );
//            noSphereSphereCollisionCB.setSelected( true );
            GasMolecule.enableParticleParticleInteractions( !noSphereSphereCollisionCB.isSelected() );
        }
    }

    protected class RulerControlPanel extends JPanel {
        RulerControlPanel() {
            final JCheckBox rulerCB = new JCheckBox( "Display ruler" );
            rulerCB.setPreferredSize( new Dimension( 140, 15 ) );
            this.add( rulerCB );
            rulerCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ( (MeasurementModule)getModule() ).setRulerEnabed( rulerCB.isSelected() );
                }
            } );
        }
    }

    protected class PressureSliceControl extends JPanel {
        PressureSliceControl() {
            String msg = "<html>Measure pressure<br>in layer</html>";
            final JCheckBox pressureSliceCB = new JCheckBox( msg );
            pressureSliceCB.setPreferredSize( new Dimension( 140, 30 ) );
            //            final JCheckBox pressureSliceCB = new JCheckBox( "Measure pressure in layer" );
            this.add( pressureSliceCB );
            pressureSliceCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    ( (MeasurementModule)getModule() ).setPressureSliceEnabled( pressureSliceCB.isSelected() );
                    //                    IdealGasControlPanel.this.getIdealGasApplication().setPressureSliceEnabled( pressureSliceCB.isSelected() );
                }
            } );
        }
    }

}
