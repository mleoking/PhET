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
import edu.colorado.phet.rutherfordscattering.module.PlumPuddingAtomModule;
import edu.colorado.phet.rutherfordscattering.util.DoubleRange;

/**
 * PlumPuddingAtomControlPanel is the control panel for the "Plum Pudding Atom" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlumPuddingAtomControlPanel extends AbstractControlPanel implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    // shows the energy value, should be false for production code
    private static final boolean DEBUG_SHOW_ENERGY_VALUE = true;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PlumPuddingAtomModule _module;
    private Gun _gun;
    private SliderControl _energyControl;
    private ChangeListener _energyListener;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     */
    public PlumPuddingAtomControlPanel( PlumPuddingAtomModule module ) {
        super( module );
        
        _module = module;
        
        _gun = _module.getGun();
        _gun.addObserver( this );
        
        // Alpha Particle Properties label
        JLabel titleLabel = new JLabel( SimStrings.get( "label.alphaParticleProperties" ) );
        titleLabel.setFont( RSConstants.TITLE_FONT );
        
        // Energy control
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
            _energyControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
            _energyControl.setBorder( BorderFactory.createEtchedBorder() );
            _energyControl.setTextFieldEditable( true );
            if ( !DEBUG_SHOW_ENERGY_VALUE ) {
                _energyControl.setTextFieldVisible( false );
            }
            _energyControl.setMinMaxLabels( SimStrings.get( "label.minEnergy" ), SimStrings.get( "label.maxEnergy" ) );
            _energyListener = new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    _module.removeAllAlphaParticles();
                    _gun.setRunning( false );
                    if ( !_energyControl.isAdjusting() ) {
                        handleEnergyChange();
                        _gun.setRunning( true );
                    }
                }
            };
            _energyControl.addChangeListener( _energyListener );
        }
        
        // Layout
        addVerticalSpace( 20 );
        addControlFullWidth( titleLabel );
        addVerticalSpace( 20 );
        addControlFullWidth( _energyControl );
        addVerticalSpace( 20 );
        addSeparator();
        addVerticalSpace( 20 );
        addResetButton();
    }
    
    /**
     * Call this before releasing all references to this object.
     */
    public void cleanup() {
        _gun.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    /*
     * Handles changes to the energy control.
     */
    private void handleEnergyChange() {
        
        _module.removeAllAlphaParticles();
        _gun.setRunning( false );
        
        double speed = _energyControl.getValue();
        _gun.deleteObserver( this );
        _gun.setSpeed( speed );
        _gun.addObserver( this );
        
        if ( !_energyControl.isAdjusting() ) {
            // restart the gun when slider is released
            _gun.setRunning( true );
        }
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Updates the controls to match the model.
     * 
     * @param o
     * @param arg
     */
    public void update( Observable o, Object arg ) {
        if ( o == _gun && arg == Gun.PROPERTY_SPEED ) {
            _energyControl.removeChangeListener( _energyListener );
            _energyControl.setValue( _gun.getSpeed() );
            _energyControl.addChangeListener( _energyListener );
        }
    }
}
