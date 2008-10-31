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
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common_microwaves.application.PhetApplication;
import edu.colorado.phet.microwaves.MicrowavesConfig;
import edu.colorado.phet.microwaves.MicrowaveModule;
import edu.colorado.phet.microwaves.MicrowavesResources;
import edu.colorado.phet.microwaves.coreadditions.MeasuringTape;
import edu.colorado.phet.microwaves.coreadditions.ModelViewTx1D;
import edu.colorado.phet.microwaves.model.MicrowaveModel;

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
        freqSliderTx = new ModelViewTx1D( 0, MicrowavesConfig.MAX_FREQUENCY,
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
        final ModelViewTx1D ampSliderTx = new ModelViewTx1D( 0, MicrowavesConfig.MAX_AMPLITUDE,
                                                             0, 100 );
        ampSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double amp = ampSliderTx.viewToModel( ampSlider.getValue() );
                module.setMicrowaveAmplitude( amp );
                displayAmplitude( amp );
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
            SwingUtils.addGridBagComponent( this, new JLabel( MicrowavesResources.getString( "MicrowaveControlPanel.FrequencyLabel" ) ),
                                            0, rowIdx++, 1, 1,
                                            GridBagConstraints.NONE,
                                            GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( this, freqSlider,
                                            0, rowIdx++, 1, 1,
                                            GridBagConstraints.NONE,
                                            GridBagConstraints.CENTER );
            freqTF = new JTextField();
            displayFrequency( freqSliderTx.viewToModel( freqSlider.getValue() ) );
            SwingUtils.addGridBagComponent( this, freqTF,
                                            0, rowIdx++, 1, 1,
                                            GridBagConstraints.NONE,
                                            GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( this, new JLabel( MicrowavesResources.getString( "MicrowaveControlPanel.AmplitudeLabel" ) ),
                                            0, rowIdx++, 1, 1,
                                            GridBagConstraints.NONE,
                                            GridBagConstraints.CENTER );
            SwingUtils.addGridBagComponent( this, ampSlider,
                                            0, rowIdx++, 1, 1,
                                            GridBagConstraints.HORIZONTAL,
                                            GridBagConstraints.CENTER );
            ampTF = new JTextField();
            displayAmplitude( ampSliderTx.viewToModel( ampSlider.getValue() ) );
//            ampTF = new JTextField( Double.toString( ampSliderTx.viewToModel( ampSlider.getValue() ) ), 10 );
            SwingUtils.addGridBagComponent( this, ampTF,
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

    private void reset() {
        setDefaults();
    }

    protected void setDefaults() {
        splineViewRB.setSelected( true );
        fieldViewActionListener.actionPerformed( null );
    }

    private void displayFrequency( double freq ) {
        freqTF.setText( frequencyFormatter.format( freq ) );
    }

    private void displayAmplitude( double amplitude ) {
        ampTF.setText( amplitudeFormatter.format( amplitude ) );
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
