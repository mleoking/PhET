// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.modesexample.malley;

import edu.umd.cs.piccolo.PCanvas;

/**
 * A mode is a lightweight container for things that changes in a Module when the mode changes.
 * In this case, each mode has its own model and canvas.
 * A mode has no responsibilities for creating or interacting with these things.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RectanglesMode {

    public final String displayName;
    public final IRectanglesModel model;
    public final PCanvas canvas;

    public RectanglesMode( String displayName, IRectanglesModel model, PCanvas canvas ) {
        this.displayName = displayName;
        this.model = model;
        this.canvas = canvas;
    }
}
