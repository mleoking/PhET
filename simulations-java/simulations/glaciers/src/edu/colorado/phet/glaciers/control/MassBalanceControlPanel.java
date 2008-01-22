/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * MassBalanceControlPanel is the control panel for controlling climate using a "mass balance" model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MassBalanceControlPanel extends JPanel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Dimension SPINNER_SIZE = new Dimension( 100, 10 );//XXX
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private DoubleSpinner _equilibriumLineAltitudeSpinner;
    private DoubleSpinner _massBalanceSlopeSpinner;
    private DoubleSpinner _maximumMassBalanceSpinner;
    
    private ArrayList _listeners; // list of MassBalanceControlPanelListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MassBalanceControlPanel( 
            DoubleRange equilibriumLineAltitudeRange,
            DoubleRange massBalanceSlopeRange,
            DoubleRange maximumMassBalanaceRange ) {
        super();
        
        double value = equilibriumLineAltitudeRange.getDefault();
        double min = equilibriumLineAltitudeRange.getMin();
        double max = equilibriumLineAltitudeRange.getMax();
        double step = 1; //XXX
        String format = "0"; //XXX
        _equilibriumLineAltitudeSpinner = new DoubleSpinner( value, min, max, step, format, SPINNER_SIZE );
        _equilibriumLineAltitudeSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyEquilibriumLineAltitudeChanged();
            }
        });
        
        value = massBalanceSlopeRange.getDefault();
        min = massBalanceSlopeRange.getMin();
        max = massBalanceSlopeRange.getMax();
        step = 1; //XXX
        format = "0";//XXX
        _massBalanceSlopeSpinner = new DoubleSpinner( value, min, max, step, format, SPINNER_SIZE );
        _massBalanceSlopeSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyMassBalanceSlopeChanged();
            }
        });
        
        value = maximumMassBalanaceRange.getDefault();
        min = maximumMassBalanaceRange.getMin();
        max = maximumMassBalanaceRange.getMax();
        step = 0.1; //XXX
        format = "0.0";//XXX
        _maximumMassBalanceSpinner = new DoubleSpinner( value, min, max, step, format, SPINNER_SIZE );
        _maximumMassBalanceSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyMaximumMassBalanceChanged();
            }
        });
        
        _listeners = new ArrayList();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setEquilibriumLineAltitude( double altitude ) {
        if ( altitude != getEquilibriumLineAltitude() ) {
            _equilibriumLineAltitudeSpinner.setValue( altitude );
        }
    }
    
    public double getEquilibriumLineAltitude() {
        return _equilibriumLineAltitudeSpinner.getValue();
    }
    
    public void setMassBalanceSlope( double slope ) {
        if ( slope != getMassBalanceSlope() ) {
            _massBalanceSlopeSpinner.setValue( slope );
        }
    }
    
    public double getMassBalanceSlope() {
        return _massBalanceSlopeSpinner.getValue();
    }
    
    public void setMaximumMassBalance( double maximumMassBalance ) {
        if ( maximumMassBalance != getMaximumMassBalance() ) {
            _maximumMassBalanceSpinner.setValue( maximumMassBalance );
        }
    }
    public double getMaximumMassBalance() {
        return _maximumMassBalanceSpinner.getValue();
    }
    
    //----------------------------------------------------------------------------
    // Listener
    //----------------------------------------------------------------------------
    
    public interface MassBalanceControlPanelListener {
        public void equilibriumLineAltitudeChanged( double altitude );
        public void massBalanceSlopeChanged( double slope );
        public void maximumMassBalanceChanged( double maximumMassBalance );
    }
    
    public static class MassBalanaceControlPanelAdapter implements MassBalanceControlPanelListener {
        public void equilibriumLineAltitudeChanged( double altitude ) {}
        public void massBalanceSlopeChanged( double slope ) {}
        public void maximumMassBalanceChanged( double maximumMassBalance ) {}
    }
    
    //----------------------------------------------------------------------------
    // Notification
    //----------------------------------------------------------------------------
    
    private void notifyEquilibriumLineAltitudeChanged() {
        final double value = _equilibriumLineAltitudeSpinner.getValue();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (MassBalanceControlPanelListener) i.next() ).equilibriumLineAltitudeChanged( value );
        }
    }
    
    private void notifyMassBalanceSlopeChanged() {
        final double value = _massBalanceSlopeSpinner.getValue();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (MassBalanceControlPanelListener) i.next() ).massBalanceSlopeChanged( value );
        }
    }
    
    private void notifyMaximumMassBalanceChanged() {
        final double value = _maximumMassBalanceSpinner.getValue();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (MassBalanceControlPanelListener) i.next() ).maximumMassBalanceChanged( value );
        }
    }
}
