/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.module;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.clock.ClockListener;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.opticaltweezers.persistence.OTConfig;
import edu.colorado.phet.common.piccolophet.PiccoloModule;


/**
 * AbstractModule is the base class for all modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractModule extends PiccoloModule {
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param title the module title
     * @param clock the simulation clock
     * @param startsPaused initial clock state
     */
    public AbstractModule( String title, IClock clock, boolean startsPaused ) {
        super( title, clock, startsPaused );
        setLogoPanel( null );
    }
    
    //----------------------------------------------------------------------------
    // Abstract
    //----------------------------------------------------------------------------

    /**
     * Resets the module to its initial state.
     */
    public abstract void resetAll();
    
    /**
     * Saves the module's configuration by writing it to a provided configuration object.
     * 
     * @param appConfig
     */
    public abstract void save( OTConfig appConfig );
    
    /**
     * Loads the module's configuration by reading it from a provided configuration object.
     * 
     * @param appConfig
     */
    public abstract void load( OTConfig appConfig );
    
    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    /**
     * Gets this module's frame.
     * 
     * @return JFrame
     */
    public JFrame getFrame() {
        return PhetApplication.instance().getPhetFrame();
    }
    
    /**
     * Adds a listener to the module's clock.
     * 
     * @param listener
     */
    public void addClockListener( ClockListener listener ) {
        getClock().addClockListener( listener );
    }
    
    /**
     * Removes a listener from the module's clock.
     * 
     * @param listener
     */
    public void removeClockListener( ClockListener listener ) {
        getClock().removeClockListener( listener );
    }
}
