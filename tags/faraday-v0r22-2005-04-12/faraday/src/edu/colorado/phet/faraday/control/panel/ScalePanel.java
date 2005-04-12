/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control.panel;

import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.control.ControlPanelSlider;
import edu.colorado.phet.faraday.model.Lightbulb;
import edu.colorado.phet.faraday.model.Voltmeter;
import edu.colorado.phet.faraday.util.EasyGridBagLayout;
import edu.colorado.phet.faraday.view.ElectromagnetGraphic;
import edu.colorado.phet.faraday.view.PickupCoilGraphic;


/**
 * ScalePanel is a debugging panel for adjusting the scale of various simulation components.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
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
            final ControlPanelSlider slider = new ControlPanelSlider( "Lightbulb: {0} %" );
            slider.setMinimum( 100 );
            slider.setMaximum( 1000 );
            slider.setValue( (int) ( lightbulbModel.getScale() * 100 ) );
            slider.addChangeListener( new ChangeListener() {

                public void stateChanged( ChangeEvent e ) {
                    int value = slider.getValue();
                    lightbulbModel.setScale( value / 100 );
                }
            } );
            layout.addFilledComponent( slider, row++, 0, GridBagConstraints.HORIZONTAL );
        }

        // Voltmeter
        if ( voltmeterModel != null ) {
            final ControlPanelSlider slider = new ControlPanelSlider( "Voltmeter: {0} %" );
            slider.setMinimum( 100 );
            slider.setMaximum( 1000 );
            slider.setValue( (int) ( voltmeterModel.getScale() * 100 ) );
            slider.addChangeListener( new ChangeListener() {

                public void stateChanged( ChangeEvent e ) {
                    int value = slider.getValue();
                    voltmeterModel.setScale( value / 100 );
                }
            } );
            layout.addFilledComponent( slider, row++, 0, GridBagConstraints.HORIZONTAL );
        }
        
        // Electrons in Pickup Coil
        if ( pickupCoilGraphic != null ) {
            final ControlPanelSlider slider = new ControlPanelSlider( "Pickup Electrons: {0} %" );
            slider.setMinimum( 100 );
            slider.setMaximum( 1000 );
            slider.setValue( (int) ( pickupCoilGraphic.getCoilGraphic().getElectronSpeedScale() * 100 ) );
            slider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int value = slider.getValue();
                    pickupCoilGraphic.getCoilGraphic().setElectronSpeedScale( value / 100 );
                }
            } );
            layout.addFilledComponent( slider, row++, 0, GridBagConstraints.HORIZONTAL );
        }
        
        // Electrons in Electromagnet
        if ( electromagnetGraphic != null ) {
            final ControlPanelSlider slider = new ControlPanelSlider( "Electromagnet Electrons: {0} %" );
            slider.setMinimum( 100 );
            slider.setMaximum( 1000 );
            slider.setValue( (int) ( electromagnetGraphic.getCoilGraphic().getElectronSpeedScale() * 100 ) );
            slider.addChangeListener( new ChangeListener() {

                public void stateChanged( ChangeEvent e ) {
                    int value = slider.getValue();
                    electromagnetGraphic.getCoilGraphic().setElectronSpeedScale( value / 100 );
                }
            } );
            layout.addFilledComponent( slider, row++, 0, GridBagConstraints.HORIZONTAL );
        }
    }
}
