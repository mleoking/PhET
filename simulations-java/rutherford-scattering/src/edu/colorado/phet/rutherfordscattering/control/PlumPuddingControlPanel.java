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

    private PlumPuddingModule _module;
    private SliderControl _clockStepControl;
    
    public PlumPuddingControlPanel( PlumPuddingModule module ) {
        super( module );
        
        _module = module;
        
        JLabel titleLabel = new JLabel( SimStrings.get( "label.alphaParticleProperties" ) );
        titleLabel.setFont( RSConstants.TITLE_FONT );
        
        // Clock Step control
        {
            double value = RSConstants.DEFAULT_CLOCK_STEP;
            double min = RSConstants.MIN_CLOCK_STEP;
            double max = RSConstants.MAX_CLOCK_STEP;
            double tickSpacing = max - min;
            int tickDecimalPlaces = 0;
            int valueDecimalPlaces = 1;
            String label = SimStrings.get( "label.energy" ); // labeled "Energy" !
            String units = "";
            int columns = 3;
            _clockStepControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
            _clockStepControl.setBorder( BorderFactory.createEtchedBorder() );
            _clockStepControl.setTextFieldEditable( false );
//            _clockStepControl.setTextFieldVisible( false );
            _clockStepControl.setMinMaxLabels( SimStrings.get( "label.minEnergy" ), SimStrings.get( "label.maxEnergy" ) );
            _clockStepControl.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleClockStepChange();
                }
            } );
        }
        
        addVerticalSpace( 20 );
        addControlFullWidth( titleLabel );
        addVerticalSpace( 20 );
        addControlFullWidth( _clockStepControl );
    }
    
    public void setClockStep( double dt ) {
        _clockStepControl.setValue( dt );
    }
    
    private void handleClockStepChange() {
        double dt = _clockStepControl.getValue();
        _module.setClockStep( dt );
        _module.removeAllAlphaParticles();
    }
}
