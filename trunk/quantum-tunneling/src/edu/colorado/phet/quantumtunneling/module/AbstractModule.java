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

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;

import javax.swing.JFrame;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.ClockListener;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.quantumtunneling.help.HelpPane;
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
    // Instance data
    //----------------------------------------------------------------------------
    
    private Component helpPane;
    private Component restoreGlassPane;
    
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
        if ( hasHelp() ) {
            helpPane = new HelpPane( PhetApplication.instance().getPhetFrame() );
        }
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
     * Sets the canvas.
     */
    public void setCanvas( PhetPCanvas canvas ) {
        setPhetPCanvas( canvas );
    }
    
    /**
     * Turns the wait cursor on and off.
     * 
     * @param enabled true or false
     */
    public void setWaitCursorEnabled( boolean enabled ) {
        if ( enabled ) {
            getPhetPCanvas().setCursor( WAIT_CURSOR );
        }
        else {
            getPhetPCanvas().setCursor( DEFAULT_CURSOR );
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
    
    public void setHelpPane( Component helpPane ) {
        boolean visible = ( this.helpPane.isVisible() );
        this.helpPane = helpPane;
        this.helpPane.setVisible( visible );
        if ( isActive() ) {
            setGlassPane();
        }
    }
    
    public Component getHelpPane() {
        return helpPane;
    }
    
    public HelpPane getDefaultHelpPane() {
        if ( helpPane instanceof HelpPane ) {
            return (HelpPane) helpPane;
        }
        else {
            return null;
        }
    }
    
    public void setHelpEnabled( boolean enabled ) {
        super.setHelpEnabled( enabled );
        if ( helpPane != null ) {
            helpPane.setVisible( enabled );
        }
    }
    
    public void activate() {
        super.activate();
        setGlassPane();
    }
    
    public void deactivate() {
        restoreGlassPane();
        super.deactivate();
    }
    
    private void setGlassPane() {
        if ( helpPane != null ) {
            JFrame frame = getFrame();
            restoreGlassPane = frame.getGlassPane();
            frame.setGlassPane( helpPane );
        }
        else {
            restoreGlassPane = null;
        }
    }
    
    private void restoreGlassPane() {
        if ( restoreGlassPane != null ) {
            JFrame frame = getFrame();
            frame.setGlassPane( restoreGlassPane );
            restoreGlassPane = null;
        }
    }

    /**
     * Gets the module's parent frame.
     * 
     * @return Frame
     */
    public JFrame getFrame() {
        return PhetApplication.instance().getPhetFrame();
    }

}
