/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;

/**
 * MassBalanceControlPanel is the control panel for controlling climate using a "mass balance" model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MassBalanceControlPanel extends JPanel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color CONTROL_COLOR = GlaciersConstants.INNER_PANEL_CONTROL_COLOR;
    private static final Font CONTROL_FONT = GlaciersConstants.CONTROL_PANEL_CONTROL_FONT;
    
    private static final Dimension SPINNER_SIZE = new Dimension( 100, 20 );//XXX
    
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
            DoubleRange maximumMassBalanceRange ) {
        super();
        
        _listeners = new ArrayList();
        
        JLabel equilibriumLineAltitudeLabel = new JLabel( GlaciersStrings.LABEL_EQUILIBRIUM_LINE_ALTITUDE );
        equilibriumLineAltitudeLabel.setForeground( CONTROL_COLOR );
        equilibriumLineAltitudeLabel.setFont( CONTROL_FONT );
        
        JLabel massBalanceSlopeLabel = new JLabel( GlaciersStrings.LABEL_MASS_BALANCE_SLOPE );
        massBalanceSlopeLabel.setForeground( CONTROL_COLOR );
        massBalanceSlopeLabel.setFont( CONTROL_FONT );
        
        JLabel maximumMassBalanceLabel = new JLabel( GlaciersStrings.LABEL_MAXIMUM_MASS_BALANCE );
        maximumMassBalanceLabel.setForeground( CONTROL_COLOR );
        maximumMassBalanceLabel.setFont( CONTROL_FONT );
        
        JLabel equilibriumLineAltitudeUnits= new JLabel( GlaciersStrings.UNITS_EQUILIBRIUM_LINE_ALTITUDE );
        equilibriumLineAltitudeUnits.setForeground( CONTROL_COLOR );
        equilibriumLineAltitudeUnits.setFont( CONTROL_FONT );
        
        JLabel massBalanceSlopeUnits = new JLabel( GlaciersStrings.UNITS_MASS_BALANCE_SLOPE );
        massBalanceSlopeUnits.setForeground( CONTROL_COLOR );
        massBalanceSlopeUnits.setFont( CONTROL_FONT );
        
        JLabel maximumMassBalanceUnits = new JLabel( GlaciersStrings.UNITS_MAXIMUM_MASS_BALANCE );
        maximumMassBalanceUnits.setForeground( CONTROL_COLOR );
        maximumMassBalanceUnits.setFont( CONTROL_FONT );
        
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
        
        value = maximumMassBalanceRange.getDefault();
        min = maximumMassBalanceRange.getMin();
        max = maximumMassBalanceRange.getMax();
        step = 0.1; //XXX
        format = "0.0";//XXX
        _maximumMassBalanceSpinner = new DoubleSpinner( value, min, max, step, format, SPINNER_SIZE );
        _maximumMassBalanceSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                notifyMaximumMassBalanceChanged();
            }
        });
        
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addAnchoredComponent( equilibriumLineAltitudeLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _equilibriumLineAltitudeSpinner, row, column++, GridBagConstraints.WEST );
        layout.addAnchoredComponent( equilibriumLineAltitudeUnits, row, column++, GridBagConstraints.WEST );
        row++;
        column = 0;
        layout.addAnchoredComponent( massBalanceSlopeLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _massBalanceSlopeSpinner, row, column++, GridBagConstraints.WEST );
        layout.addAnchoredComponent( massBalanceSlopeUnits, row, column++, GridBagConstraints.WEST );
        row++;
        column = 0;
        layout.addAnchoredComponent( maximumMassBalanceLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _maximumMassBalanceSpinner, row, column++, GridBagConstraints.WEST );
        layout.addAnchoredComponent( maximumMassBalanceUnits, row, column++, GridBagConstraints.WEST );
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
