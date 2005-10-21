/**
 * Class: MicrowaveControlPanel
 * Package: edu.colorado.phet.microwave.view
 * Author: Another Guy
 * Date: Aug 19, 2003
 */
package edu.colorado.phet.microwave.view;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.coreadditions.MeasuringTape;
import edu.colorado.phet.coreadditions.ModelViewTx1D;
import edu.colorado.phet.microwave.MicrowaveConfig;
import edu.colorado.phet.microwave.MicrowaveModule;
import edu.colorado.phet.microwave.model.MicrowaveModel;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MicrowaveControlPanel extends JPanel {

    MicrowaveModel model;
    MicrowaveModule module;
    PhetApplication application;
    private JSlider freqSlider;
    private JSlider ampSlider;
    private JTextField freqTF;
    private JTextField ampTF;
    private JRadioButton noFieldViewRB;
    private JRadioButton fullViewRB;
    private JRadioButton singleViewRB;
    private ButtonGroup fieldViewBtnGrp;
    private JRadioButton splineViewRB;
    NumberFormat frequencyFormatter = new DecimalFormat( ".00000" );
    NumberFormat amplitudeFormatter = new DecimalFormat( ".00" );
    private ModelViewTx1D freqSliderTx;

    public MicrowaveControlPanel( MicrowaveModule module, MicrowaveModel model ) {
        this.module = module;
        this.model = model;
        layoutPanel();
    }

    private void layoutPanel() {

        // Create the controls

        // Slider to control frequency
        freqSlider = new JSlider( SwingConstants.HORIZONTAL, 0,
                                  100,
                                  50 );
        freqSliderTx = new ModelViewTx1D( 0, MicrowaveConfig.s_maxFreq,
                                          0, 100 );
        freqSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double freq = freqSliderTx.viewToModel( freqSlider.getValue() );
                module.setMicrowaveFrequency( freq );
                displayFrequency( freq );
            }
        } );

        // Slider to control amplitude
        ampSlider = new JSlider( SwingConstants.HORIZONTAL, 0,
                                 100, 50 );
        final ModelViewTx1D ampSliderTx = new ModelViewTx1D( 0, MicrowaveConfig.s_maxAmp,
                                                             0, 100 );
        ampSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double amp = ampSliderTx.viewToModel( ampSlider.getValue() );
                module.setMicrowaveAmplitude( amp );
                displayAmplitude( amp );
            }
        } );

        // Button to toggle the microwave
        JButton button = new JButton( SimStrings.get( "MicrowaveControlPanel.MicrowaveOnOffButton" ) );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.toggleMicrowave();
            }
        } );

        // Button to set field display type
        noFieldViewRB = new JRadioButton( SimStrings.get( "MicrowaveControlPanel.NoneRadioButton" ) );
        noFieldViewRB.addActionListener( fieldViewActionListener );
        fullViewRB = new JRadioButton( SimStrings.get( "MicrowaveControlPanel.FullFieldRadioButton" ) );
        fullViewRB.addActionListener( fieldViewActionListener );
        singleViewRB = new JRadioButton( SimStrings.get( "MicrowaveControlPanel.SingleLineRadioButton" ) );
        singleViewRB.addActionListener( fieldViewActionListener );
        splineViewRB = new JRadioButton( SimStrings.get( "MicrowaveControlPanel.CurveRadioButton" ) );
        splineViewRB.addActionListener( fieldViewActionListener );
        fieldViewBtnGrp = new ButtonGroup();
        fieldViewBtnGrp.add( fullViewRB );
        fieldViewBtnGrp.add( singleViewRB );
        fieldViewBtnGrp.add( splineViewRB );
        fieldViewBtnGrp.add( noFieldViewRB );

        // Set the initial condition of the radio buttons
        splineViewRB.setSelected( true );
//        fullViewRB.setSelected( true );
        fieldViewActionListener.actionPerformed( null );


        JPanel fieldViewRBPane = new JPanel( new GridBagLayout() );
        int rowIdx2 = 0;
        try {
            GraphicsUtil.addGridBagComponent( fieldViewRBPane, splineViewRB, 0, rowIdx2++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( fieldViewRBPane, singleViewRB, 0, rowIdx2++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( fieldViewRBPane, fullViewRB, 0, rowIdx2++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( fieldViewRBPane, noFieldViewRB, 0, rowIdx2++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
        fieldViewRBPane.setBorder( BorderFactory.createTitledBorder( SimStrings.get( "MicrowaveControlPanel.FieldViewBorderTitle" ) ) );

        // Test button for measuring tool
        final JToggleButton measureBtn = new JToggleButton( SimStrings.get( "MicrowaveControlPanel.MeasureToggleButton" ) );
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
        JButton resetBtn = new JButton( SimStrings.get( "MicrowaveControlPanel.ResetButton" ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );

        // Lay out the controls
        EmptyBorder emptyBorder = (EmptyBorder)BorderFactory.createEmptyBorder();
        this.setBorder( emptyBorder );
        this.setLayout( new GridBagLayout() );
        int rowIdx = 0;
        try {
            GraphicsUtil.addGridBagComponent( this, new MicrowaveLegend(), 0, rowIdx++, 1, 1,
                                              GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, button,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new JLabel( SimStrings.get( "MicrowaveControlPanel.FrequencyLabel" ) ),
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, freqSlider,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            freqTF = new JTextField();
            displayFrequency( freqSliderTx.viewToModel( freqSlider.getValue() ) );
            GraphicsUtil.addGridBagComponent( this, freqTF,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new JLabel( SimStrings.get( "MicrowaveControlPanel.AmplitudeLabel" ) ),
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, ampSlider,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.CENTER );
            ampTF = new JTextField();
            displayAmplitude( ampSliderTx.viewToModel( ampSlider.getValue() ) );
//            ampTF = new JTextField( Double.toString( ampSliderTx.viewToModel( ampSlider.getValue() ) ), 10 );
            GraphicsUtil.addGridBagComponent( this, ampTF,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, fieldViewRBPane,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.HORIZONTAL,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, resetBtn,
                                              0, rowIdx++, 1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
    }

    private void displayFrequency( double freq ) {
        freqTF.setText( frequencyFormatter.format( freq ) );
    }

    private void displayAmplitude( double amplitude ) {
        ampTF.setText( amplitudeFormatter.format( amplitude ) );
    }

    private ActionListener fieldViewActionListener = new ActionListener() {
        public void actionPerformed( ActionEvent e ) {
            JRadioButton selection = GraphicsUtil.getSelection( fieldViewBtnGrp );
            if( selection == noFieldViewRB ) {
                module.setFieldViewOff();
            }
            if( selection == fullViewRB ) {
                module.setFieldViewFull();
            }
            if( selection == singleViewRB ) {
                module.setFieldViewSingle();
            }
            if( selection == splineViewRB ) {
                module.setFieldViewSingle();
                module.setFiledViewSpline();
            }
        }
    };

}
