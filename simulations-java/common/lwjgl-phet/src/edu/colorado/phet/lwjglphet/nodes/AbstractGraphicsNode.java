// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.event.ValueNotifier;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * Abstract base class for the nodes that embed Piccolo or Swing graphics, and that may include event forwarding. OrthoPiccoloNode, OrthoSwingNode,
 * PlanarPiccoloNode, PlanarSwingNode and ThreadedPlanarPiccoloNode are examples.
 */
public abstract class AbstractGraphicsNode extends GLNode {
    // notifier for when we resize
    public final ValueNotifier<AbstractGraphicsNode> onResize = new ValueNotifier<AbstractGraphicsNode>( this );

    // our current size
    protected Dimension size = new Dimension();

    public PBounds get2DBounds() {
        return new PBounds( 0, 0, size.width, size.height );
    }

    // width of the underlying graphic
    public int getComponentWidth() {
        return size.width;
    }

    // height of the underlying graphic
    public int getComponentHeight() {
        return size.height;
    }

    protected abstract void rebuildComponentImage();

    // width of the quad displaying this graphic
    public abstract int getWidth();

    // height of the quad displaying this graphic
    public abstract int getHeight();
}
