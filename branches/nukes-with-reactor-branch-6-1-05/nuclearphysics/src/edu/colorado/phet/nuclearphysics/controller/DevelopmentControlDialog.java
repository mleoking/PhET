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

import edu.colorado.phet.coreadditions.ModelSlider;

import javax.swing.*;
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
            jp.add( new JLabel( "<html>Number of<br>Control Rods</html" ), jpGbc );
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
            jp.add( new JLabel( "Number Neutrons" ), jpGbc );
            jpGbc.gridx = 1;
            jp.add( numNeutronsSpinner, jpGbc );
            add( jp, gbc );

            // Slider for U235 absorption probability
            final ModelSlider u235absorptionSlider = new ModelSlider( "U235 Absorption Probability", 0, 1, 1 );
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
            final ModelSlider u238AbsorptionSlider = new ModelSlider( "U238 Absorption Probability", 0, 1, 1 );
            u238AbsorptionSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    module.setU238AbsorptionProbability( u238AbsorptionSlider.getModelValue() );
                }
            } );
            u238AbsorptionSlider.setMajorTickSpacing( 0.25 );
            u238AbsorptionSlider.setPaintLabels( true );
            u238AbsorptionSlider.setPaintTicks( true );
            add( u238AbsorptionSlider, gbc );

            // Control for the spacing between nuclei

            final ModelSlider nucleusSpacingSlider = new ModelSlider( "<html>Spacing Between<br>Nuclei (nuc. diam.)</html>",
                                                                      1, 3, 2.5 );
            nucleusSpacingSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    module.setInterNucleusSpacing( nucleusSpacingSlider.getModelValue() );
                }
            } );
            nucleusSpacingSlider.setMajorTickSpacing( 1 );
            nucleusSpacingSlider.setPaintTicks( true );
            nucleusSpacingSlider.setPaintLabels( true );
            add( nucleusSpacingSlider, gbc );

            // Check box to slow the clock
            final JCheckBox slowMotionCB = new JCheckBox( "Slow Motion" );
            slowMotionCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setSlowMotion( slowMotionCB.isSelected() );
                }
            } );
            add( slowMotionCB, gbc );
        }
    }
}



