// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.slider;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandlerOld;
import edu.umd.cs.piccolo.PNode;

/**
 * Rewrite for SliderNode, should work at different orientations and support tick labels, etc.
 * See #1767
 *
 * @author Sam Reid
 */
public abstract class SliderNode extends PNode {

    public final double min;
    public final double max;
    public final SettableProperty<Double> value;
    public SimSharingDragHandlerOld dragHandler; // set by subclasses

    public SliderNode( final double min, final double max, final SettableProperty<Double> value ) {
        this.min = min;
        this.max = max;
        this.value = value;
    }

    // For sim-sharing, see #3196
    public SimSharingDragHandlerOld getDragHandler() {
        return dragHandler;
    }
}