/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.module;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.piccolo.PiccoloModule;
import edu.colorado.phet.rutherfordscattering.model.RSClock;


/**
 * AbstractModule is the module implementation shared by all modules in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public abstract class AbstractModule extends PiccoloModule {

    private RSClock _clock;
    
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
     * Sets the clock dt for the module.
     * @param dt
     */
    public void setDt( double dt ) {
        System.out.println( "AbstractModule.setDt dt=" + dt );//XXX
        _clock.setSimulationTimeChange(  dt  );
    }
    
    //----------------------------------------------------------------------------
    // Overrides
    //----------------------------------------------------------------------------
    
    /**
     * Adds a component listener to the simulation panel,
     * so that the canvas layout will be updated when it changes size.
     */
    public void setSimulationPanel( JComponent simulationPanel ) {
        super.setSimulationPanel( simulationPanel );
        simulationPanel.addComponentListener( new ComponentAdapter() {
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
