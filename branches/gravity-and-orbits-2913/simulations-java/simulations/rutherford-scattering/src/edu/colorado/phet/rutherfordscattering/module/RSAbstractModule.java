// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.module;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JFrame;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.rutherfordscattering.model.RSClock;


/**
 * AbstractModule is the module implementation shared by all modules in this simulation.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class RSAbstractModule extends PiccoloModule {

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
    public RSAbstractModule( String title, RSClock clock, boolean startsPaused ) {
        super( title, clock, startsPaused );

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
        return PhetApplication.getInstance().getPhetFrame();
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

    /**
     * Called whenever the canvas size changes.
     */
    protected abstract void updateCanvasLayout();
}
