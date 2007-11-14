/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanelWithTimeDisplay;
import edu.colorado.phet.common.phetcommon.view.ClockTimePanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.AbstractValueControl;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.ILayoutStrategy;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.glaciers.GlaciersResources;
import edu.colorado.phet.glaciers.model.GlaciersClock;


/**
 * GlaciersClockControlPanel is a custom clock control panel.
 * It has a time display, speed control, and control buttons.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersClockControlPanel extends ClockControlPanel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GlaciersClock _clock;
    
    private ClockTimePanel _timePanel;
    private LinearValueControl _speedControl;
    private JButton _restartButton;

    public class SliderOnlyLayoutStrategy implements ILayoutStrategy {

        public SliderOnlyLayoutStrategy() {}

        public void doLayout( AbstractValueControl valueControl ) {
            valueControl.add( valueControl.getSlider() );
        }
    }
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock
     */
    public GlaciersClockControlPanel( GlaciersClock clock ) {
        super( clock );
        
        TitledBorder border = new TitledBorder( GlaciersResources.getString( "title.time" ) );
        setBorder( border );
        
        // Clock
        _clock = clock;
        
        // Restart button
        {
            String restartLabel = GlaciersResources.getCommonString( ClockControlPanelWithTimeDisplay.PROPERTY_RESTART );
            Icon restartIcon = new ImageIcon( GlaciersResources.getCommonImage( PhetCommonResources.IMAGE_REWIND ) );
            _restartButton = new JButton( restartLabel, restartIcon );
        }

        // Time display
        {
            String units = clock.getUnits();
            DecimalFormat format = new DecimalFormat( "0" );
            int columns = 6;
            _timePanel = new ClockTimePanel( clock, units, format, columns );
        }
        
        // Speed slider
        {
            double min = _clock.getDtRange().getMin();
            double max = _clock.getDtRange().getMax();
            String label = "";
            String textFieldPattern = "";
            String units = "";
            _speedControl = new LinearValueControl( min, max, label, textFieldPattern, units, new SliderOnlyLayoutStrategy() );
            _speedControl.setValue( _clock.getDt() );
            _speedControl.setMinorTicksVisible( false );
            
            // Label the min and max
            String normalString = GlaciersResources.getString( "label.clockSpeed.slow" );
            String fastString = GlaciersResources.getString( "label.clockSpeed.fast" );
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Double( min ), new JLabel( normalString ) );
            labelTable.put( new Double( max ), new JLabel( fastString ) );
            _speedControl.setTickLabels( labelTable );
        }
        
        // Layout
        addControlToLeft( _restartButton );
        addControlToLeft( _speedControl );
        addControlToLeft( _timePanel );
        
        // Interactivity
        _speedControl.addChangeListener( new ChangeListener() { 
            public void stateChanged( ChangeEvent event ) {
                handleDtChange();
            }
        } );
        _restartButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleRestart();
            }
        } );
    }
    
    /**
     * Call this method before releasing all references to this object.
     */
    public void cleanup() {
        super.cleanup();
        _clock = null;
    }
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets the "Restart" component, used for attaching help items.
     * 
     * @return JComponent
     */
    public JComponent getRestartComponent() {
        return _restartButton;
    }
    
    /**
     * Gets the clock index (speed) component, used for attaching help items.
     * 
     * @return JComponent
     */
    public JComponent getClockIndexComponent() {
        return _speedControl;
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleDtChange() {
        _clock.setDt( _speedControl.getValue() );
    }
    
    private void handleRestart() {
        _clock.resetSimulationTime();
    }
}
