// Copyright 2002-2011, University of Colorado

/**
 * Class: EmfControlPanel Package: edu.colorado.phet.emf.view Author: Another
 * Guy Date: May 27, 2003
 */

package edu.colorado.phet.radiowaves.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.radiowaves.EmfConfig;
import edu.colorado.phet.radiowaves.EmfModule;
import edu.colorado.phet.radiowaves.RadioWavesResources;
import edu.colorado.phet.radiowaves.command.DynamicFieldIsEnabledCmd;
import edu.colorado.phet.radiowaves.command.SetAmplitudeCmd;
import edu.colorado.phet.radiowaves.command.SetFreqencyCmd;
import edu.colorado.phet.radiowaves.command.StaticFieldIsEnabledCmd;
import edu.colorado.phet.radiowaves.coreadditions.MessageFormatter;
import edu.colorado.phet.radiowaves.model.EmfModel;

public class EmfControlPanel extends ControlPanel {

    private EmfModel model;
    private EmfModule module;

    public EmfControlPanel( EmfModel model, EmfModule module ) {
        this.model = model;
        this.module = module;
        addControlFullWidth( new Legend() );
        addControlFullWidth( new MovementControlPane() );
        addControlFullWidth( new OptionControlPane() );
    }

    //
    // Inner classes
    //

    /**
     * An inner class for the controls that enable, disable or set the values
     * of various options
     */
    private class OptionControlPane extends JPanel {

        private JCheckBox stripChartCB = new JCheckBox( RadioWavesResources.getString( "EmfControlPanel.StripChartCheckBox" ) );
        private JRadioButton staticFieldRB = new JRadioButton( RadioWavesResources.getString( "EmfControlPanel.StaticFieldRadioButton" ) );
        private JRadioButton dynamicFieldRB = new JRadioButton( RadioWavesResources.getString( "EmfControlPanel.RadiatedFieldRadioButton" ) );

        private JRadioButton fullFieldRB = new JRadioButton( RadioWavesResources.getString( "EmfControlPanel.FullRadioButton" ) );
        private JRadioButton hideFieldRB = new JRadioButton( RadioWavesResources.getString( "EmfControlPanel.NoneRadioButton" ) );
        private ButtonGroup fieldDisplayRBGroup;
        private JRadioButton vectorWCurveRB;
        private JRadioButton curveRB;

        //        private JCheckBox curveVisibleCB;

        /**
         * Constructor
         */
        OptionControlPane() {

            this.setLayout( new GridBagLayout() );

            //----------------------------------------------------------------
            // Dynamic/static field choices
            //----------------------------------------------------------------
            ButtonGroup fieldTypeRBGroup = new ButtonGroup();
            fieldTypeRBGroup.add( dynamicFieldRB );
            fieldTypeRBGroup.add( staticFieldRB );
            JPanel fieldTypePane = new JPanel( new GridBagLayout() );
            GridBagConstraints gbcA = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0, 0 ), 0, 0 );
            fieldTypePane.setBorder( BorderFactory.createTitledBorder( RadioWavesResources.getString( "EmfControlPanel.FieldVectorBorder" ) ) );
            fieldTypePane.add( dynamicFieldRB, gbcA );
            fieldTypePane.add( staticFieldRB, gbcA );
            staticFieldRB.addActionListener( new FieldViewRBActionListener() );
            dynamicFieldRB.addActionListener( new FieldViewRBActionListener() );

            //----------------------------------------------------------------
            // Field representation choices
            //----------------------------------------------------------------
            hideFieldRB.addActionListener( new FieldViewRBActionListener() );
            hideFieldRB.addActionListener( new FieldViewRBActionListener() );
            fullFieldRB.addActionListener( new FieldViewRBActionListener() );
            vectorWCurveRB = new JRadioButton( RadioWavesResources.getString( "EmfControlPanel.CurveVectorsRadioButton" ) );
            vectorWCurveRB.addActionListener( new FieldViewRBActionListener() );
            curveRB = new JRadioButton( RadioWavesResources.getString( "EmfControlPanel.CurveRadioButton" ) );
            curveRB.addActionListener( new FieldViewRBActionListener() );

            fieldDisplayRBGroup = new ButtonGroup();
            fieldDisplayRBGroup.add( curveRB );
            fieldDisplayRBGroup.add( vectorWCurveRB );
            fieldDisplayRBGroup.add( fullFieldRB );
            fieldDisplayRBGroup.add( hideFieldRB );

            // Set the default
            curveRB.setSelected( true );

            // Lay out the radio buttons
            JPanel fieldRepPane = new JPanel( new GridBagLayout() );
            GridBagConstraints gbcB = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0, 0 ), 0, 0 );
            fieldRepPane.setBorder( BorderFactory.createTitledBorder( RadioWavesResources.getString( "EmfControlPanel.FieldDisplayBorder" ) ) );
            fieldRepPane.add( vectorWCurveRB, gbcB );
            fieldRepPane.add( curveRB, gbcB );
            fieldRepPane.add( fullFieldRB, gbcB );
            fieldRepPane.add( hideFieldRB, gbcB );

            //----------------------------------------------------------------
            // Field sense options
            //----------------------------------------------------------------
            ButtonGroup fieldSenseRBGroup = new ButtonGroup();
            JPanel fieldSensePane = new JPanel( new GridBagLayout() );
            GridBagConstraints gbcD = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 0, 10, 0, 0 ), 0, 0 );
            JRadioButton fFieldRB = new JRadioButton( MessageFormatter.format( RadioWavesResources.getString( "EmfControlPanel.ForceFieldRadioButton" ) ) );
            fFieldRB.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    module.setFieldSense( EmfConfig.SHOW_FORCE_ON_ELECTRON );
                }
            } );
            fieldSenseRBGroup.add( fFieldRB );
            fieldSensePane.add( fFieldRB, gbcD );
            JRadioButton eFieldRB = new JRadioButton( RadioWavesResources.getString( "EmfControlPanel.ElectricFieldRadioButton" ) );
            eFieldRB.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    module.setFieldSense( EmfConfig.SHOW_ELECTRIC_FIELD );
                }
            } );
            fieldSenseRBGroup.add( eFieldRB );
            fieldSensePane.add( eFieldRB, gbcD );
            TitledBorder fieldSenseBorder = BorderFactory.createTitledBorder( RadioWavesResources.getString( "EmfControlPanel.FieldSenseBorder" ) );
            fieldSensePane.setBorder( fieldSenseBorder );

            stripChartCB.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    JDialog dlg = module.setStripChartEnabled( stripChartCB.isSelected() );
                    dlg.addComponentListener( new ComponentAdapter() {

                        public void componentHidden( ComponentEvent e ) {
                            stripChartCB.setSelected( false );
                        }
                    } );
                }
            } );

            JButton recenterButton = new JButton( RadioWavesResources.getString( "EmfControlPanel.RecenterButton" ) );
            recenterButton.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    module.recenterElectrons();
                }
            } );

            GridBagConstraints gbcC = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );
            add( fieldTypePane, gbcC );
            add( fieldRepPane, gbcC );
            add( fieldSensePane, gbcC );
            add( fieldTypePane, gbcC );
            gbcC.insets = new Insets( 4, 15, 0, 0 );
            add( stripChartCB, gbcC );

            // Set initial conditions
            vectorWCurveRB.setSelected( true );
            dynamicFieldRB.setSelected( true );
            fFieldRB.setSelected( true );
            setFieldView();
        }

        /**
         * Handles the switch between dynamic and static field views,
         * and various types of representations for the dynamic field
         */
        private void setFieldView() {

            module.displayStaticField( staticFieldRB.isSelected() );
            module.displayDynamicField( dynamicFieldRB.isSelected() );
            new StaticFieldIsEnabledCmd( model, true ).doIt();
            new DynamicFieldIsEnabledCmd( model, true ).doIt();

            // Takes care of turning off field curve if it was showing and
            // we have gone to the static field display
            boolean isEnabled = false;
            if ( staticFieldRB.isSelected() ) {
                isEnabled = false;
                fullFieldRB.setSelected( true );
            }
            else if ( dynamicFieldRB.isSelected() ) {
                isEnabled = true;
            }
            hideFieldRB.setEnabled( isEnabled );
            curveRB.setEnabled( isEnabled );
            vectorWCurveRB.setEnabled( isEnabled );


            int display = EmfPanel.NO_FIELD;
            JRadioButton rb = SwingUtils.getSelection( fieldDisplayRBGroup );
            display = rb == fullFieldRB ? EmfPanel.FULL_FIELD : display;
            if ( fullFieldRB.isSelected() ) {
                display = EmfPanel.FULL_FIELD;
            }
            if ( hideFieldRB.isSelected() ) {
                display = EmfPanel.NO_FIELD;
            }
            if ( curveRB.isSelected() ) {
                display = EmfPanel.CURVE;
            }
            if ( vectorWCurveRB.isSelected() ) {
                display = EmfPanel.CURVE_WITH_VECTORS;
                module.setSingleVectorRowRepresentation( EmfConfig.SINGLE_VECTOR_ROW_PINNED );
            }
            module.setFieldDisplay( display );
        }

        private class FieldViewRBActionListener implements ActionListener {

            public void actionPerformed( ActionEvent e ) {
                setFieldView();
            }
        }
    }

    /**
     * An inner class for the radio buttons that control how the transmitting
     * electrons move
     */
    private class MovementControlPane extends JPanel {

        JRadioButton sineRB = new JRadioButton( RadioWavesResources.getString( "EmfControlPanel.OscillateRadioButton" ) );
        JRadioButton manualRB = new JRadioButton( RadioWavesResources.getString( "EmfControlPanel.ManualRadioButton" ) );
        JCheckBox coordinateFACB = new JCheckBox( MessageFormatter.format( RadioWavesResources.getString( "EmfControlPanel.CoordinateFACheckBox" ) ) );
        ButtonGroup rbGroup = new ButtonGroup();
        int maxFreq = 200;
        JSlider freqSlider = new JSlider( 0, maxFreq, maxFreq / 2 );
        int maxAmplitude = 100;
        JSlider ampSlider = new JSlider( 0, maxAmplitude, maxAmplitude / 2 );
        private boolean coordinateFandA;
        private JLabel freqLabel;
        private JLabel ampLabel;

        MovementControlPane() {
            this.setLayout( new GridBagLayout() );
            rbGroup.add( sineRB );
            rbGroup.add( manualRB );
            EtchedBorder etchedBorder = (EtchedBorder) BorderFactory.createEtchedBorder();
            this.setBorder( etchedBorder );
            this.setBorder( BorderFactory.createTitledBorder( RadioWavesResources.getString( "EmfControlPanel.TransmitterMovementBorder" ) ) );

            sineRB.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    setMovementType();
                }
            } );

            manualRB.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    setMovementType();
                }
            } );

            freqSlider.setPaintTicks( true );
            freqSlider.setPreferredSize( new Dimension( 120, (int) freqSlider.getPreferredSize().getHeight()) );
            freqSlider.addChangeListener( new ChangeListener() {

                public void stateChanged( ChangeEvent e ) {
                    new SetFreqencyCmd( model, (float) freqSlider.getValue() / 5000 ).doIt();

                    // Adjust the amplitude so the vectors don't get too big
                    if ( coordinateFandA ) {
                        double ampPercentage = 1 - ( (double) freqSlider.getValue() ) / freqSlider.getMaximum();
                        ampSlider.setValue( (int) ( maxAmplitude * ampPercentage ) );
                    }
                }
            } );

            ampSlider.setPaintTicks( true );
            ampSlider.setPreferredSize( new Dimension( 120, (int) ampSlider.getPreferredSize().getHeight() ) );
            ampSlider.addChangeListener( new ChangeListener() {

                public void stateChanged( ChangeEvent e ) {
                    new SetAmplitudeCmd( model, (float) ampSlider.getValue() ).doIt();
                }
            } );
            new SetAmplitudeCmd( model, (float) ampSlider.getValue() ).doIt();

            coordinateFACB.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    setCoordinateFandA( coordinateFACB.isSelected() );
                }
            } );
            coordinateFACB.setSelected( false );
            setCoordinateFandA( coordinateFACB.isSelected() );
            GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets( 0, 32, 0, 0 ), 0, 0 );
            add( manualRB, gbc );
            gbc.gridy++;
            add( sineRB, gbc );
            gbc.anchor = GridBagConstraints.CENTER;
            gbc.insets = new Insets( 0, 0, 0, 0 );
            gbc.gridy++;
            freqLabel = new JLabel( RadioWavesResources.getString( "EmfControlPanel.FrequencyLabel" ) );
            add( freqLabel, gbc );
            gbc.gridy++;
            add( freqSlider, gbc );
            ampLabel = new JLabel( RadioWavesResources.getString( "EmfControlPanel.AmplitudeLabel" ) );
            gbc.gridy++;
            add( ampLabel, gbc );
            gbc.gridy++;
            add( ampSlider, gbc );

            // Set initial conditions
            manualRB.setSelected( true );
            setMovementType();
        }

        void setCoordinateFandA( boolean coordinateFandA ) {
            this.coordinateFandA = coordinateFandA;
        }

        void setMovementType() {
            if ( manualRB.isSelected() ) {
                module.setMovementManual();
            }
            if ( sineRB.isSelected() ) {
                module.setMovementSinusoidal();
            }
            module.recenterElectrons();
            module.setAutoscaleEnabled( false );
            freqLabel.setEnabled( sineRB.isSelected() );
            ampLabel.setEnabled( sineRB.isSelected() );
            freqSlider.setEnabled( sineRB.isSelected() );
            ampSlider.setEnabled( sineRB.isSelected() );
        }
    }

    private class Legend extends JPanel {

        Legend() {
            setLayout( new GridBagLayout() );
            this.setBorder( BorderFactory.createTitledBorder( RadioWavesResources.getString( "EmfControlPanel.LegendBorder" ) ) );
            ImageIcon electronImg = null;
            try {
                electronImg = new ImageIcon( ImageLoader.loadBufferedImage( EmfConfig.smallElectronImg ) );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
            int rowIdx = 0;
            try {
                SwingUtils.addGridBagComponent( this, new JLabel( RadioWavesResources.getString( "EmfControlPanel.ElectronLabel" ), electronImg, SwingConstants.LEFT ), 0, rowIdx++, 1, 1, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST );
            }
            catch ( AWTException e ) {
                e.printStackTrace();
            }
        }
    }

}