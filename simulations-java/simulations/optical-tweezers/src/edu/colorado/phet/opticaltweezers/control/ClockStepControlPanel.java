/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.defaults.PhysicsDefaults;
import edu.colorado.phet.opticaltweezers.model.OTClock;

/**
 * ClockStepControlPanel contains controls for setting the simulation clock step.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ClockStepControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private OTClock _clock;
    
    private LogarithmicValueControl _clockStepControl;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param titleFont
     * @param controlFont
     * @param clock
     */
    public ClockStepControlPanel( Font titleFont, Font controlFont, OTClock clock ) {
        super();
        
        _clock = clock;
        
        double min = PhysicsDefaults.CLOCK_DT_RANGE.getMin();
        double max = PhysicsDefaults.CLOCK_DT_RANGE.getMax();
        String label = OTResources.getString( "label.timestep" );
        String valuePattern = PhysicsDefaults.CLOCK_CONTROL_PATTERN;
        String units = OTResources.getString( "units.time" );
        _clockStepControl = new LogarithmicValueControl( min, max, label, valuePattern, units );
        _clockStepControl.setTextFieldColumns( 6 );
        _clockStepControl.setFont( controlFont );
        _clockStepControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                handleClockStepControl();
            }
        });
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        int row = 0;
        int column = 0;
        layout.addComponent( _clockStepControl, row++, column );
        
        // Default state
        _clockStepControl.setValue( _clock.getDt() );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setClockStep( double dt ) {
        _clockStepControl.setValue( dt );
        handleClockStepControl();
    }
    
    public double getClockStep() {
        return _clockStepControl.getValue();
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleClockStepControl() {
        final double dt = _clockStepControl.getValue();
        _clock.setDt( dt );
    }
}
