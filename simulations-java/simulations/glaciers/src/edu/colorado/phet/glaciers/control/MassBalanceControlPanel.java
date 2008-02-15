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
    private DoubleSpinner _maximumSnowfallSpinner;
    
    private ArrayList _listeners; // list of MassBalanceControlPanelListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MassBalanceControlPanel( 
            DoubleRange equilibriumLineAltitudeRange,
            DoubleRange maximumSnowfallRange ) {
        super();
        
        _listeners = new ArrayList();
        
        JLabel equilibriumLineAltitudeLabel = new JLabel( GlaciersStrings.LABEL_EQUILIBRIUM_LINE_ALTITUDE );
        equilibriumLineAltitudeLabel.setForeground( CONTROL_COLOR );
        equilibriumLineAltitudeLabel.setFont( CONTROL_FONT );
        
        JLabel maximumSnowfallLabel = new JLabel( GlaciersStrings.LABEL_MAXIMUM_SNOWFALL );
        maximumSnowfallLabel.setForeground( CONTROL_COLOR );
        maximumSnowfallLabel.setFont( CONTROL_FONT );
        
        JLabel equilibriumLineAltitudeUnits= new JLabel( GlaciersStrings.UNITS_EQUILIBRIUM_LINE_ALTITUDE );
        equilibriumLineAltitudeUnits.setForeground( CONTROL_COLOR );
        equilibriumLineAltitudeUnits.setFont( CONTROL_FONT );
        
        JLabel maximumSnowfallUnits = new JLabel( GlaciersStrings.UNITS_ACCUMULATION );
        maximumSnowfallUnits.setForeground( CONTROL_COLOR );
        maximumSnowfallUnits.setFont( CONTROL_FONT );
        
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
        
        value = maximumSnowfallRange.getDefault();
        min = maximumSnowfallRange.getMin();
        max = maximumSnowfallRange.getMax();
        step = 0.1; //XXX
        format = "0.0";//XXX
        _maximumSnowfallSpinner = new DoubleSpinner( value, min, max, step, format, SPINNER_SIZE );
        _maximumSnowfallSpinner.addChangeListener( new ChangeListener() {
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
        layout.addAnchoredComponent( maximumSnowfallLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _maximumSnowfallSpinner, row, column++, GridBagConstraints.WEST );
        layout.addAnchoredComponent( maximumSnowfallUnits, row, column++, GridBagConstraints.WEST );
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
    
    public void setMaximumSnowfall( double maximumSnowfall ) {
        if ( maximumSnowfall != getMaximumSnowfall() ) {
            _maximumSnowfallSpinner.setValue( maximumSnowfall );
        }
    }
    public double getMaximumSnowfall() {
        return _maximumSnowfallSpinner.getValue();
    }
    
    //----------------------------------------------------------------------------
    // Listener
    //----------------------------------------------------------------------------
    
    public interface MassBalanceControlPanelListener {
        public void equilibriumLineAltitudeChanged( double altitude );
        public void maximumSnowfallChanged( double maximumSnowfall );
    }
    
    public static class MassBalanaceControlPanelAdapter implements MassBalanceControlPanelListener {
        public void equilibriumLineAltitudeChanged( double altitude ) {}
        public void maximumSnowfallChanged( double maximumSnowfall ) {}
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
    
    private void notifyMaximumMassBalanceChanged() {
        final double value = _maximumSnowfallSpinner.getValue();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (MassBalanceControlPanelListener) i.next() ).maximumSnowfallChanged( value );
        }
    }
}
