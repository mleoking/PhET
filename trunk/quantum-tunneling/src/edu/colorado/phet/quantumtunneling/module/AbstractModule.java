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
import java.awt.Frame;

import javax.naming.OperationNotSupportedException;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;


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

    public static final double HELP_LAYER = ApparatusPanel.LAYER_TOP;
    public static final double DEBUG_LAYER = HELP_LAYER - 1;
    
    public static final Cursor DEFAULT_CURSOR = Cursor.getPredefinedCursor( Cursor.DEFAULT_CURSOR );
    public static final Cursor WAIT_CURSOR = Cursor.getPredefinedCursor( Cursor.WAIT_CURSOR );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param title the module title
     * @param clock the simulation clock
     */
    public AbstractModule( String title, AbstractClock clock ) {
        super( title, clock );
    }
    
    //----------------------------------------------------------------------------
    // Abstract
    //----------------------------------------------------------------------------

    /**
     * Resets the module to its initial state.
     */
    public abstract void reset();
    
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
        ApparatusPanel apparatusPanel = getApparatusPanel();
        if ( enabled ) {
            apparatusPanel.setCursor( WAIT_CURSOR );
        }
        else {
            apparatusPanel.setCursor( DEFAULT_CURSOR );
        }
    }
    
    /**
     * Gets the application's Frame.
     * 
     * @return Frame
     */
    public Frame getFrame() {
        return PhetApplication.instance().getPhetFrame();
    }
    
    //----------------------------------------------------------------------------
    // Debugging
    //----------------------------------------------------------------------------
    
    /**
     * Causes the graphic's location and bounds to be rendered.
     * 
     * @param graphic the graphic
     * @throws OperationNotSupportedException
     */
    protected void drawBounds( PhetGraphic graphic ) throws OperationNotSupportedException {
        //XXX this needs to be rewritten for Piccolo
        throw new OperationNotSupportedException();
    }
}
