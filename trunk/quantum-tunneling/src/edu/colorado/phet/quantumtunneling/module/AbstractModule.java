/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.module;

import java.awt.Cursor;

import javax.swing.JFrame;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.quantumtunneling.persistence.QTConfig;


/**
 * AbstractModule is the base class for all modules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    public static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR );
    
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
    }
    
    //----------------------------------------------------------------------------
    // Abstract
    //----------------------------------------------------------------------------

    /**
     * Resets the module to its initial state.
     */
    public abstract void reset();
    
    /**
     * Saves the module's configuration by writing it to a provided configuration object.
     * 
     * @param appConfig
     */
    public abstract void save( QTConfig appConfig );
    
    /**
     * Loads the module's configuration by reading it from a provided configuration object.
     * 
     * @param appConfig
     */
    public abstract void load( QTConfig appConfig );
    
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
     * Turns the wait cursor on and off.
     * 
     * @param enabled true or false
     */
    public void setWaitCursorEnabled( boolean enabled ) {
        if ( enabled ) {
            getSimulationPanel().setCursor( WAIT_CURSOR );
        }
        else {
            getSimulationPanel().setCursor( DEFAULT_CURSOR );
        }
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
