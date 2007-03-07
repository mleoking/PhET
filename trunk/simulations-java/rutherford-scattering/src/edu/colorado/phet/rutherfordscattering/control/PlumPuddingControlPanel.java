/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.control;

import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.colorado.phet.rutherfordscattering.model.Gun;
import edu.colorado.phet.rutherfordscattering.module.PlumPuddingModule;
import edu.colorado.phet.rutherfordscattering.util.DoubleRange;


public class PlumPuddingControlPanel extends AbstractControlPanel implements Observer {

    private PlumPuddingModule _module;
    private Gun _gun;
    private SliderControl _initialSpeedControl;
    private ChangeListener _initialSpeedListener;
    
    public PlumPuddingControlPanel( PlumPuddingModule module ) {
        super( module );
        
        JLabel titleLabel = new JLabel( SimStrings.get( "label.alphaParticleProperties" ) );
        titleLabel.setFont( RSConstants.TITLE_FONT );
        
        _module = module;
        
        _gun = _module.getGun();
        _gun.addObserver( this );
        
        // Initial Speed control (labeled "Energy")
        {
            double value = _gun.getSpeed();
            double min = _gun.getMinSpeed();
            double max = _gun.getMaxSpeed();
            double tickSpacing = max - min;
            int tickDecimalPlaces = 0;
            int valueDecimalPlaces = 1;
            String label = SimStrings.get( "label.energy" );
            String units = "";
            int columns = 3;
            _initialSpeedControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
            _initialSpeedControl.setBorder( BorderFactory.createEtchedBorder() );
            _initialSpeedControl.setTextFieldEditable( false );
//            _clockStepControl.setTextFieldVisible( false );
            _initialSpeedControl.setMinMaxLabels( SimStrings.get( "label.minEnergy" ), SimStrings.get( "label.maxEnergy" ) );
            _initialSpeedListener = new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleInitialSpeedChange();
                }
            };
            _initialSpeedControl.addChangeListener( _initialSpeedListener );
        }
        
        addVerticalSpace( 20 );
        addControlFullWidth( titleLabel );
        addVerticalSpace( 20 );
        addControlFullWidth( _initialSpeedControl );
    }
    
    public void cleanup() {
        _gun.deleteObserver( this );
    }
    
    private void handleInitialSpeedChange() {
        double speed = _initialSpeedControl.getValue();
        _gun.deleteObserver( this );
        _gun.setSpeed( speed );
        _gun.addObserver( this );
        _module.removeAllAlphaParticles();
    }

    public void update( Observable o, Object arg ) {
        if ( o == _gun && arg == Gun.PROPERTY_INITIAL_SPEED ) {
            _initialSpeedControl.removeChangeListener( _initialSpeedListener );
            _initialSpeedControl.setValue( _gun.getSpeed() );
            _initialSpeedControl.addChangeListener( _initialSpeedListener );
        }
    }
}
