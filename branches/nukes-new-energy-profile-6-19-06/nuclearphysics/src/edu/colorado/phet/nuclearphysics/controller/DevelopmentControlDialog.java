/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.coreadditions.ModelSlider;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * DevelopmentControlDialog
 * <p/>
 * A modeless dialog with control for developers to mess with
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class DevelopmentControlDialog extends JDialog {

    private GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                             1, 1, 1, 1, GridBagConstraints.CENTER,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 5, 5, 5, 5 ), 5, 5 );
    private ControlledFissionModule module;


    public DevelopmentControlDialog( Frame owner, final ControlledFissionModule module ) throws HeadlessException {
        super( owner, false );
        this.module = module;
        setContentPane( new ControlPanel() );
        pack();
    }

    private class ControlPanel extends JPanel {

        ControlPanel() {
            setLayout( new GridBagLayout() );

            // Spinner for the number of control rods
            JPanel jp = new JPanel( new GridBagLayout() );
            GridBagConstraints jpGbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
                                                               new Insets( 0, 0, 0, 0 ), 0, 0 );
            final JSpinner controlRodSpinner = new JSpinner( new SpinnerNumberModel( module.getNumControlRods(),
                                                                                     0, 10, 1 ) );
            controlRodSpinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    module.setNumControlRods( ( (Integer)controlRodSpinner.getValue() ).intValue() );
                }
            } );
            jp.add( new JLabel( SimStrings.get( "AdvancedControls.NumRods" ) ), jpGbc );
            jpGbc.gridx = 1;
            jp.add( controlRodSpinner, jpGbc );

            final JSpinner numNeutronsSpinner = new JSpinner( new SpinnerNumberModel( module.getNumNeutronsFired(),
                                                                                      1, 10, 1 ) );
            numNeutronsSpinner.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    module.setNumNeutronsFired( ( (Integer)numNeutronsSpinner.getValue() ).intValue() );
                }
            } );
            jpGbc.gridy = 1;
            jpGbc.gridx = 0;
            jp.add( new JLabel( SimStrings.get( "AdvancedControls.NumNeutrons" ) ), jpGbc );
            jpGbc.gridx = 1;
            jp.add( numNeutronsSpinner, jpGbc );
            add( jp, gbc );

            // Slider for U235 absorption probability
            final ModelSlider u235absorptionSlider = new ModelSlider( SimStrings.get( "AdvancedControls.U235prob" ), 0, 1, 1 );
            u235absorptionSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    module.setU235AbsorptionProbability( u235absorptionSlider.getModelValue() );
                }
            } );
            u235absorptionSlider.setMajorTickSpacing( 0.25 );
            u235absorptionSlider.setPaintLabels( true );
            u235absorptionSlider.setPaintTicks( true );
            add( u235absorptionSlider, gbc );

            // Slider for U238 absorption probability
            final ModelSlider u238AbsorptionSlider = new ModelSlider( SimStrings.get( "AdvancedControls.U238prob" ), 0, 1, .5 );
            u238AbsorptionSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    module.setU238AbsorptionProbability( u238AbsorptionSlider.getModelValue() );
                }
            } );
            u238AbsorptionSlider.setMajorTickSpacing( 0.25 );
            u238AbsorptionSlider.setPaintLabels( true );
            u238AbsorptionSlider.setPaintTicks( true );
            add( u238AbsorptionSlider, gbc );

            // Slider to control rod absorption probability
            final ModelSlider rodAbsoprtionSlider = new ModelSlider( "Rod absorption prob.",
                                                                     0, 1, 1 );
            rodAbsoprtionSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    module.setRodAbsorptionProbability( rodAbsoprtionSlider.getModelValue() );
                }
            } );
            module.setRodAbsorptionProbability( rodAbsoprtionSlider.getModelValue() );
            rodAbsoprtionSlider.setMajorTickSpacing( 0.25 );
            rodAbsoprtionSlider.setPaintLabels( true );
            rodAbsoprtionSlider.setPaintTicks( true );
            add( rodAbsoprtionSlider, gbc );

            // Control for the spacing between nuclei
            final ModelSlider nucleusSpacingSlider = new ModelSlider( SimStrings.get( "AdvancedControls.NucSpacing" ),
                                                                      1, 3, 2.5 );
            nucleusSpacingSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    module.setInterNucleusSpacing( nucleusSpacingSlider.getModelValue() );
                    System.out.println( "nucleusSpacingSlider.getModelValue()  = " + nucleusSpacingSlider.getModelValue() );
                }
            } );
            nucleusSpacingSlider.setMajorTickSpacing( 1 );
            nucleusSpacingSlider.setPaintLabels( true );
            nucleusSpacingSlider.setPaintTicks( true );
            add( nucleusSpacingSlider, gbc );

            // Slider to control the delay for neutron expulsion on fission
            final ModelSlider fissionDelayslider = new ModelSlider( "Neutron release delay",
                                                                    0, 2000, 0 );
            fissionDelayslider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    module.setFissionDelay( (long)fissionDelayslider.getModelValue() );
                }
            } );
            fissionDelayslider.setMajorTickSpacing( 0.5 );
            fissionDelayslider.setMinorTickSpacing( 0.1 );
            fissionDelayslider.setPaintLabels( true );
            fissionDelayslider.setPaintTicks( true );
            add( fissionDelayslider, gbc );

            // Slider to control how often neutrons are injected
            final ModelSlider neutronInjectionSlider = new ModelSlider( SimStrings.get( "AdvancedControls.NeutronInjection" ),
                                                                        1, 30, 30 );
            neutronInjectionSlider.setMajorTickSpacing( 5 );
            neutronInjectionSlider.setPaintLabels( true );
            neutronInjectionSlider.setPaintTicks( true );
//            add( neutronInjectionSlider, gbc );

            // Check box to slow the clock
            final JCheckBox slowMotionCB = new JCheckBox( SimStrings.get( "AdvancedControls.SlowMotion" ) );
            slowMotionCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setSlowMotion( slowMotionCB.isSelected() );
                }
            } );
            add( slowMotionCB, gbc );

            // Controls for the periodic firing of neutrons into the cavity
            JPanel pngPane = new JPanel( new GridLayout( 2, 1 ) );
            pngPane.setBorder( new TitledBorder( "Periodically fire neutrons" ) );
            final JCheckBox pngCB = new JCheckBox( "Enabled" );
            final ModelSlider pngSlider = new ModelSlider( "Period (ms)", 0, 3000, 500 );
            pngSlider.setMajorTickSpacing( 1000 );
            pngSlider.setPaintTicks( true );
            pngSlider.setPaintLabels( true );
            pngPane.add( pngCB );
            pngPane.add( pngSlider );
            add( pngPane, gbc );

            pngCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.enablePeriodicNeutrons( pngCB.isSelected() );
                }
            } );

            pngSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    module.setPeriodicNeutronsPeriod( pngSlider.getModelValue() );
                }
            } );
            module.setPeriodicNeutronsPeriod( pngSlider.getModelValue() );
        }
    }
}



