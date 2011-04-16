// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.modesexample.malley;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.umd.cs.piccolo.PCanvas;

/**
 * The "Rectangles" module, has a couple of different modes for dealing with rectangle models.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RectanglesModule extends PiccoloModule {

    private final ArrayList<RectanglesMode> modes; // modes associated with this module
    private final Property<RectanglesMode> currentMode; // mode currently visible

    /**
     * @param frame the main application frame
     */
    public RectanglesModule( final Frame frame ) {
        super( "Rectangles", new ConstantDtClock( 25 ) );

        // hide clock controls
        setClockControlPanel( null );

        /*
         * Set up modes.
         * As in mode-less sims, the module is responsible for creating all canvases and models.
         * Modes act as lightweight containers for the set of things that change when the mode changes.
         */
        {
            modes = new ArrayList<RectanglesMode>();

            // mode with 2 rectangles
            IRectanglesModel twoModel = new IRectanglesModel.TwoRectanglesModel();
            PCanvas twoCanvas = new RectanglesCanvas( twoModel );
            modes.add( new RectanglesMode( "Two Rectangles", twoModel, twoCanvas ) );

            // mode with 3 rectangles
            IRectanglesModel threeModel = new IRectanglesModel.ThreeRectanglesModel();
            PCanvas threeCanvas = new RectanglesCanvas( threeModel );
            modes.add( new RectanglesMode( "Three Rectangles", threeModel, threeCanvas ) );
        }

        // default to the first mode in the list
        currentMode = new Property<RectanglesMode>( modes.get( 0 ) );

        // control panel, with mode controls
        setControlPanel( new RectanglesControlPanel( currentMode, modes, this ) );

        // Set the default canvas.
        // We need to do this here because the currentMode observer does invokeLater,
        // so there may be a period of time where getSimulationPanel returns null.
        setSimulationPanel( currentMode.getValue().canvas );

        // when the mode changes, change the play area
        currentMode.addObserver( new SimpleObserver() {
            public void update() {
                // This bit of code is borrowed from GravityAndOrbitsModule.
                // TODO Need to make canvas switching more transparent to applications.
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        System.out.println( "RectanglesModule switched to mode " + currentMode.getValue().displayName );
                        setSimulationPanel( currentMode.getValue().canvas );
                        if ( frame != null ) {
                            frame.invalidate();
                            frame.validate();
                            frame.doLayout();
                        }
                    }
                } );
            }
        } );
    }

    @Override
    public void reset() {
        super.reset();
        currentMode.reset();
        for ( RectanglesMode mode : modes ) {
            mode.model.reset();
        }
    }
}
