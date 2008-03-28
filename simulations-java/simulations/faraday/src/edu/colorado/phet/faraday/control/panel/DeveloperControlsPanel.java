/* Copyright 2005-2008, University of Colorado */

package edu.colorado.phet.faraday.control.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
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
            final ElectromagnetGraphic electromagnetGraphic ) {

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
        
     // Lightbulb
        if ( lightbulbModel != null ) {
            final LinearValueControl lightbulbControl = new LinearValueControl( MIN_SCALE, MAX_SCALE, "Lightbulb scale:", "0.0", "" );
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

        // Voltmeter
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