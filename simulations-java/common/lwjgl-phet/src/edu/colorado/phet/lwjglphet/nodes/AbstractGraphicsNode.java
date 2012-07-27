// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.model.event.ValueNotifier;

public abstract class AbstractGraphicsNode extends GLNode {
    // notifier for when we resize
    public final ValueNotifier<AbstractGraphicsNode> onResize = new ValueNotifier<AbstractGraphicsNode>( this );

    // our current size
    protected Dimension size = new Dimension();

    // width of the underlying graphic
    public int getComponentWidth() {
        return size.width;
    }

    // height of the underlying graphic
    public int getComponentHeight() {
        return size.height;
    }

    // width of the quad displaying this graphic
    public abstract int getWidth();

    // height of the quad displaying this graphic
    public abstract int getHeight();
}
