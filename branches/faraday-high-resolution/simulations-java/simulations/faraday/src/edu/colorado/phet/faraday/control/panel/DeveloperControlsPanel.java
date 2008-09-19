/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.control.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.faraday.model.Lightbulb;
import edu.colorado.phet.faraday.model.PickupCoil;
import edu.colorado.phet.faraday.model.Voltmeter;
import edu.colorado.phet.faraday.view.ElectromagnetGraphic;
import edu.colorado.phet.faraday.view.LightbulbGraphic;
import edu.colorado.phet.faraday.view.PickupCoilGraphic;


/**
 * DeveloperControlsPanel contains developer controls.
 * The contents of this panel are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperControlsPanel extends FaradayPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final double MIN_SCALE = 0.1;
    private static final double MAX_SCALE = 30.0;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param pickupCoilModel
     */
    public DeveloperControlsPanel( 
            final PickupCoil pickupCoilModel,
            final Lightbulb lightbulbModel,
            final Voltmeter voltmeterModel,
            final PickupCoilGraphic pickupCoilGraphic,
            final ElectromagnetGraphic electromagnetGraphic,
            final LightbulbGraphic lightbulbGraphic ) {

        super();
        
        //  Title
        Border lineBorder = BorderFactory.createLineBorder( Color.BLACK, 2 );
        TitledBorder titleBorder = BorderFactory.createTitledBorder( lineBorder, "Developer controls" );
//        titleBorder.setTitleFont( getTitleFont() );
        setBorder( titleBorder );
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        
        // Message
        JLabel message = new JLabel( "<html>These controls will NOT be reset<br>when you press the Reset All button." );
        message.setForeground( Color.RED );
        layout.addComponent( message, row++, 0 );
        
        // Electromagnet shape
        if ( electromagnetGraphic != null ) {
            final JCheckBox showElectromagnetShapeCheckBox = new JCheckBox( "Show magnet model shape" );
            showElectromagnetShapeCheckBox.setSelected( electromagnetGraphic.isModelShapeVisible() );
            showElectromagnetShapeCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    boolean visible = showElectromagnetShapeCheckBox.isSelected();
                    electromagnetGraphic.setModelShapeVisible( visible );
                }
            } );
            layout.addComponent( showElectromagnetShapeCheckBox, row++, 0 );
        }
        
        // Pickup Coil sample points
        if ( pickupCoilGraphic != null ) {
            final JCheckBox showSamplePointsCheckBox = new JCheckBox( "Show pickup sample points" );
            showSamplePointsCheckBox.setSelected( pickupCoilGraphic.isSamplePointsVisible() );
            showSamplePointsCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    pickupCoilGraphic.setSamplePointsVisible( showSamplePointsCheckBox.isSelected() );
                }
            } );
            layout.addComponent( showSamplePointsCheckBox, row++, 0 );
        }
        
        // Pickup Coil flux display
        if ( pickupCoilGraphic != null ) {
            final JCheckBox displayFluxCheckBox = new JCheckBox( "Display pickup flux" );
            displayFluxCheckBox.setSelected( pickupCoilGraphic.isFluxDisplayVisible() );
            displayFluxCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    pickupCoilGraphic.setFluxDisplayVisible( displayFluxCheckBox.isSelected() );
                }
            } );
            layout.addComponent(  displayFluxCheckBox, row++, 0 );
        }

        // Pickup coil fudge factor
        if ( pickupCoilModel != null ) {
            double min = 0.1;
            double max = 1;
            final LinearValueControl pickupFudgeFactorControl = new LinearValueControl( min, max, "Pickup fudge factor:", "0.00", "" );
            pickupFudgeFactorControl.setValue( pickupCoilModel.getFudgeFactor() );
            pickupFudgeFactorControl.setTextFieldEditable( true );
            pickupFudgeFactorControl.setTextFieldColumns( 3 );
            pickupFudgeFactorControl.setUpDownArrowDelta( 0.01 );
            pickupFudgeFactorControl.setBorder( BorderFactory.createEtchedBorder() );
            pickupFudgeFactorControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double value = pickupFudgeFactorControl.getValue();
                    pickupCoilModel.setFudgeFactor( value );
                }
            } );
            layout.addFilledComponent( pickupFudgeFactorControl, row++, 0, GridBagConstraints.HORIZONTAL );
        }
        
        // Lightbulb glass minimum alpha
        if ( lightbulbGraphic != null ) {
            final LinearValueControl lightbulbGlassMinAlphaControl = new LinearValueControl( 0, 1, "Lightbulb min alpha:", "0.00", "" );
            lightbulbGlassMinAlphaControl.setValue( lightbulbGraphic.getGlassMinAlpha() );
            lightbulbGlassMinAlphaControl.setTextFieldEditable( true );
            lightbulbGlassMinAlphaControl.setTextFieldColumns( 3 );
            lightbulbGlassMinAlphaControl.setUpDownArrowDelta( 0.01 );
            lightbulbGlassMinAlphaControl.setBorder( BorderFactory.createEtchedBorder() );
            lightbulbGlassMinAlphaControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double value = lightbulbGlassMinAlphaControl.getValue();
                    lightbulbGraphic.setGlassMinAlpha( value );
                }
            } );
            layout.addFilledComponent( lightbulbGlassMinAlphaControl, row++, 0, GridBagConstraints.HORIZONTAL );
        }
        
        // Lightbulb glass glow scale
        if ( lightbulbGraphic != null ) {
            final LinearValueControl lightbulbGlassGlowScaleControl = new LinearValueControl( 0.1, 30, "Lightbulb glow scale:", "0.0", "" );
            lightbulbGlassGlowScaleControl.setValue( lightbulbGraphic.getGlassGlowScale() );
            lightbulbGlassGlowScaleControl.setUpDownArrowDelta( 0.1 );
            lightbulbGlassGlowScaleControl.setBorder( BorderFactory.createEtchedBorder() );
            lightbulbGlassGlowScaleControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double value = lightbulbGlassGlowScaleControl.getValue();
                    lightbulbGraphic.setGlassGlowScale( value );
                }
            } );
            layout.addFilledComponent( lightbulbGlassGlowScaleControl, row++, 0, GridBagConstraints.HORIZONTAL );
        }
        
        // Light rays scale
        if ( lightbulbModel != null ) {
            final LinearValueControl lightbulbControl = new LinearValueControl( MIN_SCALE, MAX_SCALE, "Light rays scale:", "0.0", "" );
            lightbulbControl.setValue( lightbulbModel.getScale() );
            lightbulbControl.setTextFieldEditable( true );
            lightbulbControl.setTextFieldColumns( 3 );
            lightbulbControl.setUpDownArrowDelta( 0.1 );
            lightbulbControl.setBorder( BorderFactory.createEtchedBorder() );
            lightbulbControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double value = lightbulbControl.getValue();
                    lightbulbModel.setScale( value );
                }
            } );
            layout.addFilledComponent( lightbulbControl, row++, 0, GridBagConstraints.HORIZONTAL );
        }

        // Voltmeter scale
        if ( voltmeterModel != null ) {
            final LinearValueControl voltmeterControl = new LinearValueControl( MIN_SCALE, MAX_SCALE, "Voltmeter scale:", "0.0", "" );
            voltmeterControl.setValue( voltmeterModel.getScale() );
            voltmeterControl.setTextFieldEditable( true );
            voltmeterControl.setTextFieldColumns( 3 );
            voltmeterControl.setUpDownArrowDelta( 0.1 );
            voltmeterControl.setBorder( BorderFactory.createEtchedBorder() );
            voltmeterControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double value = voltmeterControl.getValue();
                    voltmeterModel.setScale( value );
                }
            } );
            layout.addFilledComponent( voltmeterControl, row++, 0, GridBagConstraints.HORIZONTAL );
        }
        
        // Electrons in Pickup Coil
        if ( pickupCoilGraphic != null ) {
            final LinearValueControl pickupElectronsControl = new LinearValueControl( MIN_SCALE, MAX_SCALE, "<html>Pickup<br>electrons scale:</html>", "0.0", "" );
            pickupElectronsControl.setValue( pickupCoilGraphic.getCoilGraphic().getElectronSpeedScale() );
            pickupElectronsControl.setTextFieldEditable( true );
            pickupElectronsControl.setTextFieldColumns( 3 );
            pickupElectronsControl.setUpDownArrowDelta( 0.1 );
            pickupElectronsControl.setBorder( BorderFactory.createEtchedBorder() );
            pickupElectronsControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double value = pickupElectronsControl.getValue();
                    pickupCoilGraphic.getCoilGraphic().setElectronSpeedScale( value );
                }
            } );
            layout.addFilledComponent( pickupElectronsControl, row++, 0, GridBagConstraints.HORIZONTAL );
        }
        
        // Electrons in Electromagnet
        if ( electromagnetGraphic != null ) {
            final LinearValueControl electromagnetElectronsControl = new LinearValueControl( MIN_SCALE, MAX_SCALE, "<html>Electromagnet<br>electrons scale:<html>", "0.0", "" );
            electromagnetElectronsControl.setValue( electromagnetGraphic.getCoilGraphic().getElectronSpeedScale() );
            electromagnetElectronsControl.setTextFieldEditable( true );
            electromagnetElectronsControl.setTextFieldColumns( 3 );
            electromagnetElectronsControl.setUpDownArrowDelta( 0.1 );
            electromagnetElectronsControl.setBorder( BorderFactory.createEtchedBorder() );
            electromagnetElectronsControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double value = electromagnetElectronsControl.getValue();
                    electromagnetGraphic.getCoilGraphic().setElectronSpeedScale( value );
                }
            } );
            layout.addFilledComponent( electromagnetElectronsControl, row++, 0, GridBagConstraints.HORIZONTAL );
        }
    }
}