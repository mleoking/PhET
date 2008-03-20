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
import edu.colorado.phet.faraday.model.Voltmeter;
import edu.colorado.phet.faraday.view.ElectromagnetGraphic;
import edu.colorado.phet.faraday.view.PickupCoilGraphic;


/**
 * ScalePanel is a debugging panel for adjusting the scale of various simulation components.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ScalePanel extends FaradayPanel {

    /**
     * Sole constructor.
     * Any of the paramaters may be null.
     * 
     * @param lightbulbModel
     * @param voltmeterModel
     * @param pickupCoilGraphic
     * @param electromagnetGraphic
     */
    public ScalePanel( 
            final Lightbulb lightbulbModel,
            final Voltmeter voltmeterModel,
            final PickupCoilGraphic pickupCoilGraphic,
            final ElectromagnetGraphic electromagnetGraphic ) {

        super();
        
        //  Title
        Border lineBorder = BorderFactory.createLineBorder( Color.BLACK, 2 );
        String title = "Scale Debugging";
        TitledBorder titleBorder = BorderFactory.createTitledBorder( lineBorder, title );
        titleBorder.setTitleFont( getTitleFont() );
        setBorder( titleBorder );
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        
        // Lightbulb
        if ( lightbulbModel != null ) {
            final LinearValueControl lightbulbControl = new LinearValueControl( 100, 1000, "Lightbulb:", "0", "%" );
            lightbulbControl.setValue( (int) ( lightbulbModel.getScale() * 100 ) );
            lightbulbControl.setTextFieldEditable( true );
            lightbulbControl.setTextFieldColumns( 3 );
            lightbulbControl.setUpDownArrowDelta( 1 );
            lightbulbControl.setBorder( BorderFactory.createEtchedBorder() );
            lightbulbControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int value = (int)lightbulbControl.getValue();
                    lightbulbModel.setScale( value / 100 );
                }
            } );
            layout.addFilledComponent( lightbulbControl, row++, 0, GridBagConstraints.HORIZONTAL );
        }

        // Voltmeter
        if ( voltmeterModel != null ) {
            final LinearValueControl voltmeterControl = new LinearValueControl( 100, 1000, "Voltmeter:", "0", "%" );
            voltmeterControl.setValue( (int) ( voltmeterModel.getScale() * 100 ) );
            voltmeterControl.setTextFieldEditable( true );
            voltmeterControl.setTextFieldColumns( 3 );
            voltmeterControl.setUpDownArrowDelta( 1 );
            voltmeterControl.setBorder( BorderFactory.createEtchedBorder() );
            voltmeterControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int value = (int)voltmeterControl.getValue();
                    voltmeterModel.setScale( value / 100 );
                }
            } );
            layout.addFilledComponent( voltmeterControl, row++, 0, GridBagConstraints.HORIZONTAL );
        }
        
        // Electrons in Pickup Coil
        if ( pickupCoilGraphic != null ) {
            final LinearValueControl pickupElectronsControl = new LinearValueControl( 100, 1000, "Pickup Electrons:", "0", "%" );
            pickupElectronsControl.setValue( (int) ( pickupCoilGraphic.getCoilGraphic().getElectronSpeedScale() * 100 ) );
            pickupElectronsControl.setTextFieldEditable( true );
            pickupElectronsControl.setTextFieldColumns( 3 );
            pickupElectronsControl.setUpDownArrowDelta( 1 );
            pickupElectronsControl.setBorder( BorderFactory.createEtchedBorder() );
            pickupElectronsControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int value = (int)pickupElectronsControl.getValue();
                    pickupCoilGraphic.getCoilGraphic().setElectronSpeedScale( value / 100 );
                }
            } );
            layout.addFilledComponent( pickupElectronsControl, row++, 0, GridBagConstraints.HORIZONTAL );
        }
        
        // Electrons in Electromagnet
        if ( electromagnetGraphic != null ) {
            final LinearValueControl electromagnetElectronsControl = new LinearValueControl( 100, 1000, "Electromagnet Electrons:", "0", "%" );
            electromagnetElectronsControl.setValue( (int) ( electromagnetGraphic.getCoilGraphic().getElectronSpeedScale() * 100 ) );
            electromagnetElectronsControl.setTextFieldEditable( true );
            electromagnetElectronsControl.setTextFieldColumns( 3 );
            electromagnetElectronsControl.setUpDownArrowDelta( 1 );
            electromagnetElectronsControl.setBorder( BorderFactory.createEtchedBorder() );
            electromagnetElectronsControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int value = (int)electromagnetElectronsControl.getValue();
                    electromagnetGraphic.getCoilGraphic().setElectronSpeedScale( value / 100 );
                }
            } );
            layout.addFilledComponent( electromagnetElectronsControl, row++, 0, GridBagConstraints.HORIZONTAL );
        }
    }
}
