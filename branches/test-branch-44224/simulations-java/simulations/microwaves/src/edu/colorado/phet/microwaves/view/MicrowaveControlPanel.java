/**
 * Class: MicrowaveControlPanel
 * Package: edu.colorado.phet.microwave.view
 * Author: Another Guy
 * Date: Aug 19, 2003
 */
package edu.colorado.phet.microwaves.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.microwaves.MicrowaveModule;
import edu.colorado.phet.microwaves.MicrowavesConfig;
import edu.colorado.phet.microwaves.MicrowavesResources;
import edu.colorado.phet.microwaves.coreadditions.MeasuringTape;
import edu.colorado.phet.microwaves.model.MicrowavesModel;

public class MicrowaveControlPanel extends JPanel {

    MicrowavesModel model;
    MicrowaveModule module;
    PhetApplication application;
    private JRadioButton noFieldViewRB;
    private JRadioButton fullViewRB;
    private JRadioButton singleViewRB;
    private ButtonGroup fieldViewBtnGrp;
    private JRadioButton splineViewRB;
    private LinearValueControl frequencyControl;
    private LinearValueControl amplitudeControl;

    public MicrowaveControlPanel( MicrowaveModule module, MicrowavesModel model ) {
        this.module = module;
        this.model = model;
        layoutPanel();
    }

    private void layoutPanel() {

        // Create the controls
        frequencyControl = new LinearValueControl( 0, MicrowavesConfig.MAX_FREQUENCY, module.getMicrowaveFrequency(), MicrowavesResources.getString( "MicrowaveControlPanel.FrequencyLabel" ), "0.00000", "" );
        amplitudeControl = new LinearValueControl( 0, MicrowavesConfig.MAX_AMPLITUDE, module.getMicrowaveAmplitude(), MicrowavesResources.getString( "MicrowaveControlPanel.AmplitudeLabel" ), "0.00", "" );

        frequencyControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.setMicrowaveFrequency( frequencyControl.getValue() );
            }
        } );

        amplitudeControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.setMicrowaveAmplitude( amplitudeControl.getValue() );
            }
        } );

        // Button to toggle the microwave
        JButton button = new JButton( MicrowavesResources.getString( "MicrowaveControlPanel.MicrowaveOnOffButton" ) );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.toggleMicrowave();
            }
        } );

        // Button to set field display type
        noFieldViewRB = new JRadioButton( MicrowavesResources.getString( "MicrowaveControlPanel.NoneRadioButton" ) );
        noFieldViewRB.addActionListener( fieldViewActionListener );
        fullViewRB = new JRadioButton( MicrowavesResources.getString( "MicrowaveControlPanel.FullFieldRadioButton" ) );
        fullViewRB.addActionListener( fieldViewActionListener );
        singleViewRB = new JRadioButton( MicrowavesResources.getString( "MicrowaveControlPanel.SingleLineRadioButton" ) );
        singleViewRB.addActionListener( fieldViewActionListener );
        splineViewRB = new JRadioButton( MicrowavesResources.getString( "MicrowaveControlPanel.CurveRadioButton" ) );
        splineViewRB.addActionListener( fieldViewActionListener );
        fieldViewBtnGrp = new ButtonGroup();
        fieldViewBtnGrp.add( fullViewRB );
        fieldViewBtnGrp.add( singleViewRB );
        fieldViewBtnGrp.add( splineViewRB );
        fieldViewBtnGrp.add( noFieldViewRB );

        // Set the initial condition of the radio buttons
        setDefaults();

        JPanel fieldViewRBPane = new JPanel( new GridBagLayout() );
        int rowIdx2 = 0;
        try {
            SwingUtils.addGridBagComponent( fieldViewRBPane, splineViewRB, 0, rowIdx2++, 1, 1,
                                            GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( fieldViewRBPane, singleViewRB, 0, rowIdx2++, 1, 1,
                                            GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( fieldViewRBPane, fullViewRB, 0, rowIdx2++, 1, 1,
                                            GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( fieldViewRBPane, noFieldViewRB, 0, rowIdx2++, 1, 1,
                                            GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
        fieldViewRBPane.setBorder( BorderFactory.createTitledBorder( MicrowavesResources.getString( "MicrowaveControlPanel.FieldViewBorderTitle" ) ) );

        // Test button for measuring tool
        final JToggleButton measureBtn = new JToggleButton( MicrowavesResources.getString( "MicrowaveControlPanel.MeasureToggleButton" ) );
        measureBtn.addActionListener( new ActionListener() {
            MeasuringTape mt = null;

            public void actionPerformed( ActionEvent e ) {
//                if( !measureBtn.isSelected() ) {
//                    mt.setArmed( false );
//                }
//                else {
//                    mt = new MeasuringTape( (DynamicApparatusPanel)module.getApparatusPanel() );
//                    mt.setArmed( true );
//                }
            }
        } );

        // A Reset button
        JButton resetBtn = new JButton( MicrowavesResources.getString( "MicrowaveControlPanel.ResetButton" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
                MicrowaveControlPanel.this.reset();
            }
        } );

        // Lay out the controls
        EmptyBorder emptyBorder = (EmptyBorder) BorderFactory.createEmptyBorder();
        this.setBorder( emptyBorder );
        this.setLayout( new GridBagLayout() );
        int rowIdx = 0;
        try {
            SwingUtils.addGridBagComponent( this, new MicrowaveLegend(), 0, rowIdx++, 1, 1,
                                            GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( this, button,
                                            0, rowIdx++, 1, 1,
                                            GridBagConstraints.NONE,
                                            GridBagConstraints.CENTER );

            SwingUtils.addGridBagComponent( this, frequencyControl,
                                            0, rowIdx++, 1, 1,
                                            GridBagConstraints.NONE,
                                            GridBagConstraints.CENTER );

            SwingUtils.addGridBagComponent( this, amplitudeControl,
                                            0, rowIdx++, 1, 1,
                                            GridBagConstraints.NONE,
                                            GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( this, fieldViewRBPane,
                                            0, rowIdx++, 1, 1,
                                            GridBagConstraints.HORIZONTAL,
                                            GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( this, resetBtn,
                                            0, rowIdx++, 1, 1,
                                            GridBagConstraints.NONE,
                                            GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
    }

    public void reset() {
        setDefaults();
    }

    protected void setDefaults() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                splineViewRB.setSelected( true );
                fieldViewActionListener.actionPerformed( null );
                frequencyControl.setValue( module.getMicrowaveFrequency() );
                amplitudeControl.setValue( module.getMicrowaveAmplitude() );
            }
        } );
    }

    private ActionListener fieldViewActionListener = new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
            JRadioButton selection = SwingUtils.getSelection( fieldViewBtnGrp );
            if ( selection == noFieldViewRB ) {
                module.setFieldViewOff();
            }
            if ( selection == fullViewRB ) {
                module.setFieldViewFull();
            }
            if ( selection == singleViewRB ) {
                module.setFieldViewSingle();
            }
            if ( selection == splineViewRB ) {
                module.setFieldViewSingle();
                module.setFiledViewSpline();
            }
        }
    };

}
