// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Data class that is passed during drag events from bounded drag handlers, providing information about the precomputed allowed
 * delta, and access to the full PInputEvent when necessary.
 *
 * @author Sam Reid
 */
public class DragEvent {
    public final PInputEvent event;
    public final PDimension delta;

    public DragEvent( PInputEvent event, PDimension delta ) {
        this.event = event;
        this.delta = delta;
    }
}
