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
import edu.colorado.phet.faraday.FaradayConstants;
import edu.colorado.phet.faraday.model.AbstractMagnet;
import edu.colorado.phet.faraday.model.Lightbulb;
import edu.colorado.phet.faraday.model.PickupCoil;
import edu.colorado.phet.faraday.model.Voltmeter;
import edu.colorado.phet.faraday.view.*;


/**
 * DeveloperControlsPanel contains developer controls.
 * The contents of this panel are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperControlsPanel extends FaradayPanel {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     * 
     * @param pickupCoilModel
     */
    public DeveloperControlsPanel( 
            final AbstractMagnet magnetModel,
            final PickupCoil pickupCoilModel,
            final Lightbulb lightbulbModel,
            final Voltmeter voltmeterModel,
            final PickupCoilGraphic pickupCoilGraphic,
            final ElectromagnetGraphic electromagnetGraphic,
            final LightbulbGraphic lightbulbGraphic,
            final BFieldInsideGraphic bFieldInsideGraphic, 
            final BFieldOutsideGraphic bFieldOutsideGraphic ) {

        super();
        
        //  Title
        Border lineBorder = BorderFactory.createLineBorder( Color.BLACK, 2 );
        TitledBorder titleBorder = BorderFactory.createTitledBorder( lineBorder, "Developer controls" );
        titleBorder.setTitleFont( getTitleFont() );
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
        
        // B-Field display scale
        if ( bFieldInsideGraphic != null || bFieldOutsideGraphic != null ) {
            double min = FaradayConstants.GRID_INTENSITY_SCALE_MIN;
            double max = FaradayConstants.GRID_INTENSITY_SCALE_MAX;
            double value = ( bFieldInsideGraphic != null ) ? bFieldInsideGraphic.getIntensityScale() : bFieldOutsideGraphic.getIntensityScale();
            final LinearValueControl bFieldScaleControl = new LinearValueControl( min, max, "B-field scale:", "0.0", "" );
            bFieldScaleControl.setValue( value );
            bFieldScaleControl.setTextFieldEditable( true );
            bFieldScaleControl.setTextFieldColumns( 3 );
            bFieldScaleControl.setUpDownArrowDelta( 0.1 );
            bFieldScaleControl.setBorder( BorderFactory.createEtchedBorder() );
            bFieldScaleControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double newValue = bFieldScaleControl.getValue();
                    if ( bFieldInsideGraphic != null ) {
                        bFieldInsideGraphic.setIntensityScale( newValue );
                    }
                    if ( bFieldOutsideGraphic != null ) {
                        bFieldOutsideGraphic.setIntensityScale( newValue );
                    }
                }
            } );
            layout.addFilledComponent( bFieldScaleControl, row++, 0, GridBagConstraints.HORIZONTAL );
        }
        
        // Pickup coil EMF scale
        if ( pickupCoilModel != null ) {
            double min = FaradayConstants.EMF_SCALE_MIN;
            double max = FaradayConstants.EMF_SCALE_MAX;
            final LinearValueControl emfScaleControl = new LinearValueControl( min, max, "EMF scale:", "0.00", "" );
            emfScaleControl.setValue( pickupCoilModel.getEmfScale() );
            emfScaleControl.setTextFieldEditable( true );
            emfScaleControl.setTextFieldColumns( 3 );
            emfScaleControl.setUpDownArrowDelta( 0.01 );
            emfScaleControl.setBorder( BorderFactory.createEtchedBorder() );
            emfScaleControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double value = emfScaleControl.getValue();
                    pickupCoilModel.setEmfScale( value );
                }
            } );
            layout.addFilledComponent( emfScaleControl, row++, 0, GridBagConstraints.HORIZONTAL );
        }

        // Pickup coil fudge factor
        if ( pickupCoilModel != null ) {
            double min = FaradayConstants.PICKUP_TRANSITION_SMOOTHING_SCALE_MIN;
            double max = FaradayConstants.PICKUP_TRANSITION_SMOOTHING_SCALE_MAX;
            final LinearValueControl transitionSmoothingScaleControl = new LinearValueControl( min, max, "<html>Pickup transition<br>smoothing scale:</html>", "0.00", "" );
            transitionSmoothingScaleControl.setToolTipText( "<html>Scaling factor used to smooth out<br>abrupt EMF changes when the magnet<br>transitions between inside & outside<br>the pickup coil.</html>" );
            transitionSmoothingScaleControl.setValue( pickupCoilModel.getTransitionSmoothingScale() );
            transitionSmoothingScaleControl.setTextFieldEditable( true );
            transitionSmoothingScaleControl.setTextFieldColumns( 3 );
            transitionSmoothingScaleControl.setUpDownArrowDelta( 0.01 );
            transitionSmoothingScaleControl.setBorder( BorderFactory.createEtchedBorder() );
            transitionSmoothingScaleControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    double value = transitionSmoothingScaleControl.getValue();
                    pickupCoilModel.setTransitionSmoothingScale( value );
                }
            } );
            layout.addFilledComponent( transitionSmoothingScaleControl, row++, 0, GridBagConstraints.HORIZONTAL );
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
            double min = FaradayConstants.LIGHTBULB_GLASS_GLOW_SCALE_MIN;
            double max = FaradayConstants.LIGHTBULB_GLASS_GLOW_SCALE_MAX;
            final LinearValueControl lightbulbGlassGlowScaleControl = new LinearValueControl( min, max, "Lightbulb glow scale:", "0.0", "" );
            lightbulbGlassGlowScaleControl.setValue( lightbulbGraphic.getGlassGlowScale() );
            lightbulbGlassGlowScaleControl.setTextFieldEditable( true );
            lightbulbGlassGlowScaleControl.setTextFieldColumns( 4 );
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
            double min = FaradayConstants.LIGHTBULB_RAYS_SCALE_MIN;
            double max = FaradayConstants.LIGHTBULB_RAYS_SCALE_MAX;
            final LinearValueControl lightbulbControl = new LinearValueControl( min, max, "Light rays scale:", "0.0", "" );
            lightbulbControl.setValue( lightbulbModel.getScale() );
            lightbulbControl.setTextFieldEditable( true );
            lightbulbControl.setTextFieldColumns( 4 );
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
            double min = FaradayConstants.VOLTMETER_SCALE_MIN;
            double max = FaradayConstants.VOLTMETER_SCALE_MAX;
            final LinearValueControl voltmeterControl = new LinearValueControl( min, max, "Voltmeter scale:", "0.0", "" );
            voltmeterControl.setValue( voltmeterModel.getScale() );
            voltmeterControl.setTextFieldEditable( true );
            voltmeterControl.setTextFieldColumns( 4 );
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
            double min = FaradayConstants.PICKUP_ELECTRONS_SPEED_SCALE_MIN;
            double max = FaradayConstants.PICKUP_ELECTRONS_SPEED_SCALE_MAX;
            final LinearValueControl pickupElectronsControl = new LinearValueControl( min, max, "<html>Pickup<br>electrons scale:</html>", "0.0", "" );
            pickupElectronsControl.setValue( pickupCoilGraphic.getCoilGraphic().getElectronSpeedScale() );
            pickupElectronsControl.setTextFieldEditable( true );
            pickupElectronsControl.setTextFieldColumns( 4 );
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
            double min = FaradayConstants.ELECTROMAGNET_ELECTRONS_SPEED_SCALE_MIN;
            double max = FaradayConstants.ELECTROMAGNET_ELECTRONS_SPEED_SCALE_MAX;
            final LinearValueControl electromagnetElectronsControl = new LinearValueControl( min, max, "<html>Electromagnet<br>electrons scale:<html>", "0.0", "" );
            electromagnetElectronsControl.setValue( electromagnetGraphic.getCoilGraphic().getElectronSpeedScale() );
            electromagnetElectronsControl.setTextFieldEditable( true );
            electromagnetElectronsControl.setTextFieldColumns( 4 );
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