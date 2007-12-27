/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanelWithTimeDisplay;
import edu.colorado.phet.common.phetcommon.view.ClockTimePanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.glaciers.GlaciersResources;
import edu.colorado.phet.glaciers.GlaciersStrings;
import edu.colorado.phet.glaciers.model.GlaciersClock;


/**
 * GlaciersClockControlPanel is a custom clock control panel.
 * It has a time display, speed control, and control buttons.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GlaciersClockControlPanel extends JPanel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private GlaciersClock _clock;
    
    private ClockControlPanel _clockControlPanel;
    private ClockTimePanel _timePanel;
    private LinearValueControl _speedControl;
    private JButton _restartButton;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param clock
     */
    public GlaciersClockControlPanel( GlaciersClock clock ) {
        super();
        
        // Clock
        _clock = clock;
        
        // common clock controls
        _clockControlPanel = new ClockControlPanel( clock );
        
        // Restart button
        {
            String restartLabel = GlaciersResources.getCommonString( ClockControlPanelWithTimeDisplay.PROPERTY_RESTART );
            Icon restartIcon = new ImageIcon( GlaciersResources.getCommonImage( PhetCommonResources.IMAGE_REWIND ) );
            _restartButton = new JButton( restartLabel, restartIcon );
            _clockControlPanel.addControlToLeft( _restartButton );
        }

        // Time display
        {
            DecimalFormat format = new DecimalFormat( "0" );
            int columns = 6;
            _timePanel = new ClockTimePanel( clock, GlaciersStrings.UNITS_TIME, format, columns );
        }
        
        // Speed control
        {
            double min = _clock.getDtRange().getMin();
            double max = _clock.getDtRange().getMax();
            String label = "";
            String textFieldPattern = "";
            String units = "";
            _speedControl = new LinearValueControl( min, max, label, textFieldPattern, units, new SliderOnlyLayoutStrategy() );
            _speedControl.setValue( _clock.getDt() );
            _speedControl.setMinorTicksVisible( false );
            
            // Tick labels
            Hashtable labelTable = new Hashtable();
            labelTable.put( new Double( min ), new JLabel( GlaciersStrings.SLIDER_CLOCK_SLOW ) );
            labelTable.put( new Double( max ), new JLabel( GlaciersStrings.SLIDER_CLOCK_FAST ) );
            _speedControl.setTickLabels( labelTable );
            
            // Change font on tick labels
            Dictionary d = _speedControl.getSlider().getLabelTable();
            Enumeration e = d.elements();
            while ( e.hasMoreElements() ) {
                Object o = e.nextElement();
                if ( o instanceof JComponent )
                    ( (JComponent) o ).setFont( new PhetDefaultFont( 10 ) );
            }
            
            // Slider width
            _speedControl.setSliderWidth( 125 );
        }
        
        // Layout
        {
            JPanel innerPanel = new JPanel();
            EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
            innerPanel.setLayout( layout );
            layout.setInsets( new Insets( 0, 0, 0, 0 ) );
            int column = 0;
            layout.addComponent( _timePanel, 0, column++ );
            layout.addComponent( _speedControl, 0, column++ );
            layout.addComponent( _clockControlPanel, 0, column++ );

            FlowLayout flowLayout = new FlowLayout( FlowLayout.LEFT, 0, 0 );
            setLayout( flowLayout );
            add( innerPanel );
        }
        
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
        _clockControlPanel.cleanup();
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
