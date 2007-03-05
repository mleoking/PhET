/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.module;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Dimension2D;

import javax.swing.JComponent;
import javax.swing.JFrame;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.rutherfordscattering.model.RSClock;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.util.PDimension;


/**
 * AbstractModule is the module implementation shared by all modules in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractModule extends PiccoloModule {

    private RSClock _clock;
    private PhetPCanvas _canvas;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     * 
     * @param title
     * @param clock
     * @param startsPaused
     */
    public AbstractModule( String title, RSClock clock, boolean startsPaused ) {
        super( title, clock, startsPaused );
        
        _clock = clock;
        
        // hide the PhET logo
        setLogoPanel( null );
    }
    
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
     * Sets the clock step (dt) for the module.
     * 
     * @param clockStep
     */
    public void setClockStep( double clockStep ) {
        _clock.setDt(  clockStep  );
    }
    
    /**
     * Determines the visible bounds of the canvas in world coordinates.
     */ 
    public Dimension getWorldSize() {
        Dimension2D dim = new PDimension( _canvas.getWidth(), _canvas.getHeight() );
        _canvas.getPhetRootNode().screenToWorld( dim ); // this modifies dim!
        Dimension worldSize = new Dimension( (int) dim.getWidth(), (int) dim.getHeight() );
        return worldSize;
    }
    
    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------
    
    /**
     * Adds a component listener to the simulation panel,
     * so that the canvas layout will be updated when it changes size.
     */
    public void setSimulationPanel( PhetPCanvas canvas ) {
        super.setSimulationPanel( canvas );
        _canvas = canvas;
        _canvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                // update the layout when the canvas is resized
                updateCanvasLayout();
            }
        } );
    }
    
    //----------------------------------------------------------------------------
    // Abstract
    //----------------------------------------------------------------------------
    
    /*
     * Lays out nodes on the canvas.
     * This is called whenever the canvas size changes.
     */
    protected abstract void updateCanvasLayout();

    /**
     * Resets the module to its initial state.
     */
    public abstract void reset();
}
