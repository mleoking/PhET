/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * ClockControlPanelWithTimeDisplay adds a time display and a Restart button.
 * to the standard clock control panel.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ClockControlPanelWithTimeDisplay extends ClockControlPanel {
 
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final String PROPERTY_RESTART = "Common.ClockControlPanel.Restart";
    public static final NumberFormat DEFAULT_TIME_FORMAT = new DecimalFormat( "0" );
    public static final int DEFAULT_TIME_DISPLAY_COLUMNS = 8;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JButton restartButton;
    private JPanel timePanel;
    private JTextField timeTextField;
    private JLabel unitsLabel;
    private NumberFormat timeFormat;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs a control panel with a default time format and no units.
     */
    public ClockControlPanelWithTimeDisplay( IClock clock ) {
        this( clock, "" );
    }
    
    /**
     * Constructs a control panel with a default format for the time display.
     * 
     * @param clock the clock to be controlled
     * @param units time units
     */
    public ClockControlPanelWithTimeDisplay( IClock clock, String units ) {
        this( clock, units, DEFAULT_TIME_FORMAT, DEFAULT_TIME_DISPLAY_COLUMNS );
    }
   
    /**
     * Constructor that specifies the format for the time display.
     * 
     * @param clock the clock to be controlled
     * @param units time units
     * @param timeFormat format of the time display
     * @param timeColumns number of columns in the time display
     */
    public ClockControlPanelWithTimeDisplay( IClock clock, String units, NumberFormat timeFormat, int timeColumns ) {
        super( clock );
        
        // Restart button
        String restartLabel = PhetCommonResources.getInstance().getLocalizedString( PROPERTY_RESTART );
        Icon restartIcon = new ImageIcon( PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_REWIND ) );
        restartButton = new JButton( restartLabel, restartIcon );
        restartButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                getClock().resetSimulationTime();
                updateTimeDisplay();
            }
        } );
        
        // Time display
        this.timeFormat = timeFormat;
        timePanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        {
            Icon clockIcon = new ImageIcon( PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_CLOCK ) );
            JLabel clockLabel = new JLabel( clockIcon );
            timeTextField = new JTextField();
            timeTextField.setColumns( DEFAULT_TIME_DISPLAY_COLUMNS );
            timeTextField.setPreferredSize( timeTextField.getPreferredSize() );
            timeTextField.setText( "0" );
            timeTextField.setEditable( false );
            timeTextField.setHorizontalAlignment( JTextField.RIGHT );
            
            unitsLabel = new JLabel();
            
            timePanel.add( clockLabel );
            timePanel.add( timeTextField );
            timePanel.add( unitsLabel );
        }
        
        addControlToLeft( restartButton );
        addControlToLeft( timePanel );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    /**
     * Changes visibility of Restart button.
     * 
     * @param visible
     */
    public void setRestartVisible( boolean visible ) {
        restartButton.setVisible( visible );
    }
    
    /**
     * Changes visibility of time display.
     * 
     * @param visible
     */
    public void setTimeVisible( boolean visible ) {
       timePanel.setVisible( visible );
       if ( visible ) {
           updateTimeDisplay();
       }
    }
    
    /**
     * Sets the format of the time display.
     * See DecimalFormat for specification of pattern syntax.
     * 
     * @param formatPattern
     */
    public void setTimeFormat( String formatPattern ) {
        setTimeFormat( new DecimalFormat( formatPattern ) );
    }
    
    /**
     * Sets the format of the time display.
     * 
     * @param format
     */
    public void setTimeFormat( NumberFormat format ) {
        timeFormat = format;
        updateTimeDisplay();
    }
    
    /**
     * Sets the font used to display the time.
     * 
     * @param font
     */
    public void setTimeFont( Font font ) {
        timeTextField.setFont( font );
    }
    
    /**
     * Sets the number of columns used to display the time.
     * 
     * @param columns
     */
    public void setTimeColumns( int columns ) {
        timeTextField.setColumns( columns );
    }

    /**
     * Sets the time units.
     * 
     * @param units
     */
    public void setUnits( String units ) {
        unitsLabel.setText( units );
    }
    
    /**
     * Sets the font for the time units.
     * 
     * @param font
     */
    public void setUnitsFont( Font font ) {
        unitsLabel.setFont( font );
    }
    
    //----------------------------------------------------------------------------
    // Updaters
    //----------------------------------------------------------------------------
    
    /*
     * Updates the time display if it's visible.
     */
    private void updateTimeDisplay() {
        if ( timePanel.isVisible() ) {
            double time = getClock().getSimulationTime();
            String sValue = timeFormat.format( time );
            timeTextField.setText( sValue );
        }
    }
    
    //----------------------------------------------------------------------------
    // ClockListener, overrides superclass
    //----------------------------------------------------------------------------
    
    /**
     * Updates the time display when the clock changes.
     */
    public void simulationTimeChanged( ClockEvent event ) {
        super.simulationTimeChanged( event );
        updateTimeDisplay();
    }
    
    /**
     * Updates the time display when the clock is reset.
     */
    public void simulationTimeReset( ClockEvent event ) {
        super.simulationTimeReset( event );
        updateTimeDisplay();
    }
}
