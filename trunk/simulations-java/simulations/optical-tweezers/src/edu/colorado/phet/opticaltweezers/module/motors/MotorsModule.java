/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module.motors;

import edu.colorado.phet.common.phetcommon.view.ClockControlPanelWithTimeDisplay;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.model.MotorsModel;
import edu.colorado.phet.opticaltweezers.model.OTClock;
import edu.colorado.phet.opticaltweezers.module.AbstractModule;
import edu.colorado.phet.opticaltweezers.module.GlobalDefaults;
import edu.colorado.phet.opticaltweezers.persistence.OTConfig;

/**
 * MotorsModule is the "Molecular Motors" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MotorsModule extends AbstractModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private MotorsModel _model;
    private MotorsCanvas _canvas;
    private MotorsControlPanel _controlPanel;
    private ClockControlPanelWithTimeDisplay _clockControlPanel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public MotorsModule() {
        super( OTResources.getString( "title.molecularMotors" ), MotorsDefaults.CLOCK, GlobalDefaults.CLOCK_PAUSED );
        
        _model = new MotorsModel();
        
        _canvas = new MotorsCanvas( _model );
        setSimulationPanel( _canvas );
        
        // Control Panel
        _controlPanel = new MotorsControlPanel( this );
        setControlPanel( _controlPanel );
        
        // Clock controls
        _clockControlPanel = new ClockControlPanelWithTimeDisplay( (OTClock) getClock() );
        setClockControlPanel( _clockControlPanel );

        resetAll();
    }
    
    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------
    
    /**
     * Indicates that this module has help.
     * 
     * @return true
     */
    public boolean hasHelp() {
        return false;
    }
    
    public void deactive() {
        super.deactivate();
        //XXX close dialogs
    }
    
    //----------------------------------------------------------------------------
    // AbstractModule implementation
    //----------------------------------------------------------------------------
    
    public void resetAll() {
        // TODO Auto-generated method stub
    }

    public void save( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }

    public void load( OTConfig appConfig ) {
        // TODO Auto-generated method stub
    }
}
