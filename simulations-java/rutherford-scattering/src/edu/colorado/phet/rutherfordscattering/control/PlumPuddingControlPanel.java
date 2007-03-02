/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.control;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.colorado.phet.rutherfordscattering.module.PlumPuddingModule;


public class PlumPuddingControlPanel extends AbstractControlPanel {

    private SliderControl _clockControl;
    
    public PlumPuddingControlPanel( PlumPuddingModule module ) {
        super( module );
        
        JLabel titleLabel = new JLabel( SimStrings.get( "label.alphaParticleProperties" ) );
        titleLabel.setFont( RSConstants.TITLE_FONT );
        
        // Clock (Energy) control
        {
            double value = RSConstants.DEFAULT_CLOCK_STEP;
            double min = RSConstants.MIN_CLOCK_STEP;
            double max = RSConstants.MAX_CLOCK_STEP;
            double tickSpacing = max - min;
            int tickDecimalPlaces = 0;
            int valueDecimalPlaces = 1;
            String label = SimStrings.get( "label.energy" ); // labeled "Energy" !
            String units = "";
            int columns = 1;
            _clockControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
            _clockControl.setBorder( BorderFactory.createEtchedBorder() );
            _clockControl.setTextFieldVisible( false );
            _clockControl.setMinMaxLabels( SimStrings.get( "label.minEnergy" ), SimStrings.get( "label.maxEnergy" ) );
            _clockControl.addChangeListener( new ChangeListener() {

                public void stateChanged( ChangeEvent event ) {
                    handleClockChange();
                }
            } );
        }
        
        addVerticalSpace( 20 );
        addControlFullWidth( titleLabel );
        addVerticalSpace( 20 );
        addControlFullWidth( _clockControl );
    }
    
    private void handleClockChange() {
        double dt = _clockControl.getValue();
        System.out.println( "PlumPuddingControlPanel.handleClockChange dt=" + dt );//XXX
        getModule().setDt( dt );
    }
}
