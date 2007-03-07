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
import edu.colorado.phet.rutherfordscattering.model.RutherfordAtom;
import edu.colorado.phet.rutherfordscattering.module.RutherfordAtomModule;
import edu.colorado.phet.rutherfordscattering.util.DoubleRange;
import edu.colorado.phet.rutherfordscattering.util.IntegerRange;


public class RutherfordAtomControlPanel extends AbstractControlPanel implements Observer {

    private RutherfordAtomModule _module;
    private Gun _gun;
    private RutherfordAtom _atom;
    private SliderControl _initialSpeedControl;
    private ChangeListener _initialSpeedListener;
    private SliderControl _protonsControl;
    private ChangeListener _protonsListener;
    private SliderControl _neutronsControl;
    private ChangeListener _neutronsListener;
    
    public RutherfordAtomControlPanel( RutherfordAtomModule module ) {
        super( module );
        
        _module = module;
        _module = module;
        
        _gun = _module.getGun();
        _gun.addObserver( this );
        
        _atom = _module.getAtom();
        _atom.addObserver( this );
        
        JLabel alphaParticlePropertiesLabel = new JLabel( SimStrings.get( "label.alphaParticleProperties" ) );
        alphaParticlePropertiesLabel.setFont( RSConstants.TITLE_FONT );
        
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
        
        JLabel atomPropertiesLabel = new JLabel( SimStrings.get( "label.atomProperties" ) );
        atomPropertiesLabel.setFont( RSConstants.TITLE_FONT );
        
        // Number of protons
        {
            int value = _atom.getNumberOfProtons();
            int min = _atom.getMinNumberOfProtons();
            int max = _atom.getMaxNumberOfProtons();
            int tickSpacing = max - min;
            int tickDecimalPlaces = 0;
            int valueDecimalPlaces = 0;
            String label = SimStrings.get( "label.numberOfProtons" );
            String units = "";
            int columns = 3;
            _protonsControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
            _protonsControl.setBorder( BorderFactory.createEtchedBorder() );
            _protonsListener = new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleProtonsChange();
                }
            };
            _protonsControl.addChangeListener( _protonsListener );
        }
        
        // Number of neutrons
        {
            int value = _atom.getNumberOfNeutrons();
            int min = _atom.getMinNumberOfNeutrons();
            int max = _atom.getMaxNumberOfNeutrons();
            int tickSpacing = max - min;
            int tickDecimalPlaces = 0;
            int valueDecimalPlaces = 0;
            String label = SimStrings.get( "label.numberOfNeutrons" );
            String units = "";
            int columns = 3;
            _neutronsControl = new SliderControl( value, min, max, tickSpacing, tickDecimalPlaces, valueDecimalPlaces, label, units, columns );
            _neutronsControl.setBorder( BorderFactory.createEtchedBorder() );
            _neutronsListener = new ChangeListener() {
                public void stateChanged( ChangeEvent event ) {
                    handleNeutronsChange();
                }
            };
            _neutronsControl.addChangeListener( _neutronsListener );
        }
        
        addVerticalSpace( 20 );
        addControlFullWidth( alphaParticlePropertiesLabel );
        addVerticalSpace( 20 );
        addControlFullWidth( _initialSpeedControl );
        addVerticalSpace( 20 );
        addSeparator();
        addVerticalSpace( 20 );
        addControlFullWidth( atomPropertiesLabel );
        addVerticalSpace( 20 );
        addControlFullWidth( _protonsControl );
        addControlFullWidth( _neutronsControl );
        addVerticalSpace( 20 );
        addSeparator();
    }
    
    public void cleanup() {
        _gun.deleteObserver( this );
        _atom.deleteObserver( this );
    }
    
    public void setClockStep( double clockStep ) {
        _initialSpeedControl.setValue( clockStep );
    }
    
    public void setNumberOfProtons( int numberOfProtons ) {
        _protonsControl.setValue( numberOfProtons );
    }
    
    public void setNumberOfNeutrons( int numberOfNeutrons ) {
        _neutronsControl.setValue( numberOfNeutrons );
    }

    private void handleInitialSpeedChange() {
        double speed = _initialSpeedControl.getValue();
        _gun.deleteObserver( this );
        _gun.setSpeed( speed );
        _gun.addObserver( this );
        _module.removeAllAlphaParticles();
    }
    
    private void handleProtonsChange() {
        int numberOfProtons = (int) _protonsControl.getValue();
        _atom.deleteObserver( this );
        _atom.setNumberOfProtons( numberOfProtons );
        _atom.addObserver(  this );
        _module.removeAllAlphaParticles();
    }

    private void handleNeutronsChange() {
        int numberOfNeutrons = (int) _neutronsControl.getValue();
        _atom.deleteObserver( this );
        _atom.setNumberOfNeutrons( numberOfNeutrons );
        _atom.addObserver( this );
        _module.removeAllAlphaParticles();
    }

    public void update( Observable o, Object arg ) {
        if ( o == _gun && arg == Gun.PROPERTY_INITIAL_SPEED ) {
            _initialSpeedControl.removeChangeListener( _initialSpeedListener );
            _initialSpeedControl.setValue( _gun.getSpeed() );
            _initialSpeedControl.addChangeListener( _initialSpeedListener );
        }
        else if ( o == _atom ) {
            if ( arg == RutherfordAtom.PROPERTY_NUMBER_OF_PROTONS ) {
                _protonsControl.removeChangeListener( _protonsListener );
                _protonsControl.setValue(  _atom.getNumberOfProtons() );
                _protonsControl.addChangeListener( _protonsListener );
            }
            else if ( arg == RutherfordAtom.PROPERTY_NUMBER_OF_NEUTRONS ) {
                _neutronsControl.removeChangeListener( _neutronsListener );
                _neutronsControl.setValue(  _atom.getNumberOfNeutrons() );
                _neutronsControl.addChangeListener( _neutronsListener );
            }
        }
    }
}
