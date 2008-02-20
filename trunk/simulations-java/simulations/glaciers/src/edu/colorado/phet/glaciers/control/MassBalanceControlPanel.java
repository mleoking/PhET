/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
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
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private LinearValueControl _maximumSnowfallControl;
    private LinearValueControl _equilibriumLineAltitudeControl;
    
    private ArrayList _listeners; // list of MassBalanceControlPanelListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MassBalanceControlPanel( 
            DoubleRange equilibriumLineAltitudeRange,
            DoubleRange maximumSnowfallRange ) {
        super();
        
        _listeners = new ArrayList();
        
        // max snowfall
        JLabel maximumSnowfallLabel = new JLabel( GlaciersStrings.SLIDER_MAXIMUM_SNOWFALL );        
        maximumSnowfallLabel.setForeground( CONTROL_COLOR );
        maximumSnowfallLabel.setFont( CONTROL_FONT );
        {
            double min = maximumSnowfallRange.getMin();
            double max = maximumSnowfallRange.getMax();
            String label = "";
            String textfieldPattern = "#0.0";
            String units = GlaciersStrings.UNITS_ACCUMULATION;
            ILayoutStrategy layout = new HorizontalLayoutStrategy();
            _maximumSnowfallControl = new LinearValueControl( min, max, label, textfieldPattern, units, layout );
            _maximumSnowfallControl.setFont( CONTROL_FONT );
            _maximumSnowfallControl.setUpDownArrowDelta( 0.1 );
            _maximumSnowfallControl.addChangeListener( new ChangeListener() { 
                public void stateChanged( ChangeEvent event ) {
                    if ( GlaciersConstants.UPDATE_WHILE_DRAGGING_SLIDERS || !_maximumSnowfallControl.isAdjusting() ) {
                        notifyMaximumSnowfallChanged();
                    }
                }
            } );
            
            // Change the font and color of the tick labels
            Dictionary d = _maximumSnowfallControl.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent )
                    ( (JComponent) o ).setForeground( CONTROL_COLOR );
                    ( (JComponent) o ).setFont( CONTROL_FONT );
            }
        }
        
        // equilibrium line altitude
        JLabel equilibriumLineAltitudeLabel = new JLabel( GlaciersStrings.SLIDER_EQUILIBRIUM_LINE_ALTITUDE );        
        equilibriumLineAltitudeLabel.setForeground( CONTROL_COLOR );
        equilibriumLineAltitudeLabel.setFont( CONTROL_FONT );
        {
            double min = equilibriumLineAltitudeRange.getMin();
            double max = equilibriumLineAltitudeRange.getMax();
            String label = "";
            String textfieldPattern = "###0";
            String units = GlaciersStrings.UNITS_ELEVATION;
            ILayoutStrategy layout = new HorizontalLayoutStrategy();
            _equilibriumLineAltitudeControl = new LinearValueControl( min, max, label, textfieldPattern, units, layout );
            _equilibriumLineAltitudeControl.setFont( CONTROL_FONT );
            _equilibriumLineAltitudeControl.setUpDownArrowDelta( 1 );
//            _equilibriumLineAltitudeControl.addChangeListener( new ChangeListener() { 
//                public void stateChanged( ChangeEvent event ) {
//                    if ( GlaciersConstants.UPDATE_WHILE_DRAGGING_SLIDERS || !_equilibriumLineAltitudeControl.isAdjusting() ) {
//                        notifyEquilibriumLineAltitudeChanged();
//                    }
//                }
//            } );
            _equilibriumLineAltitudeControl.setEnabled( false );//XXX make this a display for now
            
            // Change the font and color of the tick labels
            Dictionary d = _equilibriumLineAltitudeControl.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent )
                    ( (JComponent) o ).setForeground( CONTROL_COLOR );
                    ( (JComponent) o ).setFont( CONTROL_FONT );
            }
        }
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addAnchoredComponent( maximumSnowfallLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _maximumSnowfallControl, row, column++, GridBagConstraints.WEST );
        row++;
        column = 0;
        layout.addAnchoredComponent( equilibriumLineAltitudeLabel, row, column++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _equilibriumLineAltitudeControl, row, column++, GridBagConstraints.WEST );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setEquilibriumLineAltitude( double altitude ) {
        if ( altitude != getEquilibriumLineAltitude() ) {
            _equilibriumLineAltitudeControl.setValue( altitude );
        }
    }
    
    public double getEquilibriumLineAltitude() {
        return _equilibriumLineAltitudeControl.getValue();
    }
    
    public void setMaximumSnowfall( double maximumSnowfall ) {
        if ( maximumSnowfall != getMaximumSnowfall() ) {
            _maximumSnowfallControl.setValue( maximumSnowfall );
        }
    }
    public double getMaximumSnowfall() {
        return _maximumSnowfallControl.getValue();
    }
    
    //----------------------------------------------------------------------------
    // Listener
    //----------------------------------------------------------------------------
    
    public interface MassBalanceControlPanelListener {
        public void maximumSnowfallChanged( double maximumSnowfall );
        public void equilibriumLineAltitudeChanged( double altitude );
    }
    
    public static class MassBalanaceControlPanelAdapter implements MassBalanceControlPanelListener {
        public void maximumSnowfallChanged( double maximumSnowfall ) {}
        public void equilibriumLineAltitudeChanged( double altitude ) {}
    }
    
    public void addMassBalanaceControlPanelListener( MassBalanceControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeMassBalanaceControlPanelListener( MassBalanceControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Notification
    //----------------------------------------------------------------------------
    
    private void notifyMaximumSnowfallChanged() {
        final double value = _maximumSnowfallControl.getValue();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (MassBalanceControlPanelListener) i.next() ).maximumSnowfallChanged( value );
        }
    }
    
    private void notifyEquilibriumLineAltitudeChanged() {
        final double value = _equilibriumLineAltitudeControl.getValue();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (MassBalanceControlPanelListener) i.next() ).equilibriumLineAltitudeChanged( value );
        }
    }
}
