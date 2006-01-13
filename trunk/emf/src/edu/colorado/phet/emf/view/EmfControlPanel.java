/**
 * Class: EmfControlPanel
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: May 27, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.coreadditions.MessageFormatter;
import edu.colorado.phet.emf.EmfConfig;
import edu.colorado.phet.emf.EmfModule;
import edu.colorado.phet.emf.command.*;
import edu.colorado.phet.emf.model.EmfModel;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

public class EmfControlPanel extends JPanel {

    private EmfModel model;
    private EmfModule module;

    public EmfControlPanel( EmfModel model, EmfModule module ) {
        this.model = model;
        this.module = module;
        createControls();
        this.setPreferredSize( new Dimension( 180, 520 ) );

        this.addContainerListener( new ContainerAdapter() {
            public void componentRemoved( ContainerEvent e ) {
                EmfControlPanel.this.setPreferredSize( EmfControlPanel.this.getSize() );
            }
        } );
        this.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                EmfControlPanel.this.setPreferredSize( EmfControlPanel.this.getSize() );
            }
        } );
    }

    private void createControls() {
        setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.HORIZONTAL,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );
        gbc.gridy = GridBagConstraints.RELATIVE;
        add( new Legend(), gbc );
        add( new MovementControlPane(), gbc );
        add( new OptionControlPane(), gbc );
    }

    //
    // Inner classes
    //

    /**
     * An inner class for the controls that enable, disable or set the values
     * of various options
     */
    private class OptionControlPane extends JPanel {
        private JCheckBox stripChartCB = new JCheckBox( SimStrings.get( "EmfControlPanel.StripChartCheckBox" ) );
        private JRadioButton staticFieldRB = new JRadioButton( SimStrings.get( "EmfControlPanel.StaticFieldRadioButton" ) );
        private JRadioButton dynamicFieldRB = new JRadioButton( SimStrings.get( "EmfControlPanel.RadiatedFieldRadioButton" ) );

        private JRadioButton fullFieldRB = new JRadioButton( SimStrings.get( "EmfControlPanel.FullRadioButton" ) );
        private JRadioButton hideFieldRB = new JRadioButton( SimStrings.get( "EmfControlPanel.NoneRadioButton" ) );
        private ButtonGroup fieldDisplayRBGroup;
        private JRadioButton centeredVectorRB;
        private JRadioButton centeredVectorWCurveRB;
        private JRadioButton curveRB;
//        private JCheckBox curveVisibleCB;

        /**
         * Constructor
         */
        OptionControlPane() {

            this.setLayout( new GridBagLayout() );

            // Dynamic/static field selection
            ButtonGroup fieldTypeRBGroup = new ButtonGroup();
            fieldTypeRBGroup.add( dynamicFieldRB );
            fieldTypeRBGroup.add( staticFieldRB );
            JPanel fieldTypePane = new JPanel( new GridBagLayout() );
            GridBagConstraints gbcA = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                              GridBagConstraints.WEST,
                                                              GridBagConstraints.HORIZONTAL,
                                                              new Insets( 0, 0, 0, 0 ), 0, 0 );
            fieldTypePane.setBorder( BorderFactory.createTitledBorder( SimStrings.get( "EmfControlPanel.FieldVectorBorder" ) ) );
            fieldTypePane.add( dynamicFieldRB, gbcA );
            fieldTypePane.add( staticFieldRB, gbcA );
            staticFieldRB.addActionListener( new FieldViewRBActionListener() );
            dynamicFieldRB.addActionListener( new FieldViewRBActionListener() );

            // Field representation choices
            hideFieldRB.addActionListener( new FieldViewRBActionListener() );
            hideFieldRB.addActionListener( new FieldViewRBActionListener() );
            fullFieldRB.addActionListener( new FieldViewRBActionListener() );
            centeredVectorRB = new JRadioButton( SimStrings.get( "EmfControlPanel.SingleLineOfVectors" ) );
            centeredVectorRB.addActionListener( new FieldViewRBActionListener() );
            centeredVectorWCurveRB = new JRadioButton( SimStrings.get( "EmfControlPanel.CurveVectorsRadioButton" ) );
            centeredVectorWCurveRB.addActionListener( new FieldViewRBActionListener() );
            curveRB = new JRadioButton( SimStrings.get( "EmfControlPanel.CurveRadioButton" ) );
            curveRB.addActionListener( new FieldViewRBActionListener() );

            fieldDisplayRBGroup = new ButtonGroup();
            fieldDisplayRBGroup.add( curveRB );
            fieldDisplayRBGroup.add( centeredVectorWCurveRB );
            fieldDisplayRBGroup.add( centeredVectorRB );
            fieldDisplayRBGroup.add( fullFieldRB );
            fieldDisplayRBGroup.add( hideFieldRB );

            // Set the default
            curveRB.setSelected( true );

            // Lay out the radio buttons
            JPanel fieldRepPane = new JPanel( new GridBagLayout() );
            GridBagConstraints gbcB = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                              GridBagConstraints.WEST,
                                                              GridBagConstraints.HORIZONTAL,
                                                              new Insets( 0, 0, 0, 0 ), 0, 0 );
            fieldRepPane.setBorder( BorderFactory.createTitledBorder( SimStrings.get( "EmfControlPanel.FieldDisplayBorder" ) ) );
            fieldRepPane.add( centeredVectorWCurveRB, gbcB );
            fieldRepPane.add( centeredVectorRB, gbcB );
            fieldRepPane.add( curveRB, gbcB );
            fieldRepPane.add( fullFieldRB, gbcB );
            fieldRepPane.add( hideFieldRB, gbcB );

            // Field sense options
            ButtonGroup fieldSenseRBGroup = new ButtonGroup();
            JPanel fieldSensePane = new JPanel( new GridLayout( 2, 1 ) );
            JRadioButton fFieldRB = new JRadioButton( MessageFormatter.format( SimStrings.get( "EmfControlPanel.ForceFieldRadioButton" ) ) );
            fFieldRB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setFieldSense( FieldLatticeView.FORCE_ON_ELECTRON );
                }
            } );
            fieldSenseRBGroup.add( fFieldRB );
            fieldSensePane.add( fFieldRB );
            JRadioButton eFieldRB = new JRadioButton( SimStrings.get( "EmfControlPanel.ElectricFieldRadioButton" ) );
            eFieldRB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setFieldSense( FieldLatticeView.ELECTRIC_FIELD );
                }
            } );
            fieldSenseRBGroup.add( eFieldRB );
            fieldSensePane.add( eFieldRB );
            TitledBorder fieldSenseBorder = BorderFactory.createTitledBorder( SimStrings.get( "EmfControlPanel.FieldSenseBorder" ) );
            fieldSensePane.setBorder( fieldSenseBorder );

            stripChartCB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setStripChartEnabled( stripChartCB.isSelected() );
                }
            } );

            JButton recenterButton = new JButton( SimStrings.get( "EmfControlPanel.RecenterButton" ) );
            recenterButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.recenterElectrons();
                }
            } );

            try {
                int componentIdx = 0;
                GraphicsUtil.addGridBagComponent( this, fieldTypePane,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( this, fieldRepPane,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( this, fieldSensePane,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( this, stripChartCB,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }

            // Set initial conditions
            centeredVectorWCurveRB.setSelected( true );
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
            if( staticFieldRB.isSelected() ) {
                isEnabled = false;
                fullFieldRB.setSelected( true );
            }
            else if( dynamicFieldRB.isSelected() ) {
                isEnabled = true;
            }
            hideFieldRB.setEnabled( isEnabled );
            centeredVectorRB.setEnabled( isEnabled );
            curveRB.setEnabled( isEnabled );
            centeredVectorWCurveRB.setEnabled( isEnabled );


            int display = EmfPanel.NO_FIELD;
            JRadioButton rb = GraphicsUtil.getSelection( fieldDisplayRBGroup );
            display = rb == fullFieldRB ? EmfPanel.FULL_FIELD : display;
            if( fullFieldRB.isSelected() ) {
                display = EmfPanel.FULL_FIELD;
            }
            if( centeredVectorRB.isSelected() ) {
                display = EmfPanel.VECTORS_CENTERED_ON_X_AXIS;
            }
            if( hideFieldRB.isSelected() ) {
                display = EmfPanel.NO_FIELD;
            }
            if( curveRB.isSelected() ) {
                display = EmfPanel.CURVE;
            }
            if( centeredVectorWCurveRB.isSelected() ) {
                display = EmfPanel.CURVE_WITH_VECTORS;
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

        JRadioButton sineRB = new JRadioButton( SimStrings.get( "EmfControlPanel.OscillateRadioButton" ) );
        JRadioButton manualRB = new JRadioButton( SimStrings.get( "EmfControlPanel.ManualRadioButton" ) );
        JCheckBox coordinateFACB = new JCheckBox( MessageFormatter.format( SimStrings.get( "EmfControlPanel.CoordinateFACheckBox" ) ) );
        ButtonGroup rbGroup = new ButtonGroup();
        JSlider freqSlider = new JSlider( 0, 200, 100 );
        int maxAmplitude = 100;
        JSlider ampSlider = new JSlider( 0, maxAmplitude, maxAmplitude / 2 );
        private boolean coordinateFandA;
        private JLabel freqLabel;
        private JLabel ampLabel;

        MovementControlPane() {
            this.setLayout( new GridBagLayout() );
            rbGroup.add( sineRB );
            rbGroup.add( manualRB );
            EtchedBorder etchedBorder = (EtchedBorder)BorderFactory.createEtchedBorder();
            this.setBorder( etchedBorder );
            this.setBorder( BorderFactory.createTitledBorder( SimStrings.get( "EmfControlPanel.TransmitterMovementBorder" ) ) );

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

            freqSlider.setPreferredSize( new Dimension( 100, 20 ) );
            freqSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    new SetFreqencyCmd( model, (float)freqSlider.getValue() / 5000 ).doIt();

                    // Adjust the amplitude so the vectors don't get too big
                    if( coordinateFandA ) {
                        double ampPercentage = 1 - ( (double)freqSlider.getValue() ) / freqSlider.getMaximum();
                        ampSlider.setValue( (int)( maxAmplitude * ampPercentage ) );
                    }
                }
            } );


            ampSlider.setPreferredSize( new Dimension( 100, 20 ) );
            ampSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    new SetAmplitudeCmd( model, (float)ampSlider.getValue() ).doIt();
                }
            } );
            new SetAmplitudeCmd( model, (float)ampSlider.getValue() ).doIt();

            coordinateFACB.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    setCoordinateFandA( coordinateFACB.isSelected() );
                }
            } );
            coordinateFACB.setSelected( false );
            setCoordinateFandA( coordinateFACB.isSelected() );

            int componentIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, manualRB,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( this, sineRB,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
                freqLabel = new JLabel( SimStrings.get( "EmfControlPanel.FrequencyLabel" ) );
                GraphicsUtil.addGridBagComponent( this, freqLabel,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, freqSlider,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.CENTER );
                ampLabel = new JLabel( SimStrings.get( "EmfControlPanel.AmplitudeLabel" ) );
                GraphicsUtil.addGridBagComponent( this, ampLabel,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, ampSlider,
                                                  0, componentIdx++, 1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }

            // Set initial conditions
            manualRB.setSelected( true );
            setMovementType();
        }

        void setCoordinateFandA( boolean coordinateFandA ) {
            this.coordinateFandA = coordinateFandA;
        }

        void setMovementType() {
            if( manualRB.isSelected() ) {
                module.setMovementManual();
            }
            if( sineRB.isSelected() ) {
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
            this.setBorder( BorderFactory.createTitledBorder( SimStrings.get( "EmfControlPanel.LegendBorder" ) ) );
            ImageIcon electronImg = null;
            try {
                electronImg = new ImageIcon( ImageLoader.loadBufferedImage( EmfConfig.smallElectronImg ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, new JLabel( SimStrings.get( "EmfControlPanel.ElectronLabel" ),
                                                                    electronImg, SwingConstants.LEFT ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }
        }
    }

}