// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Data structure for handling the whole, numerator and denominator parts in a FractionNode.  Parts are null until assigned and null if not attached.
 *
 * @author Sam Reid
 */
public class Box {
    public ShapeContainer box;
    public NumberCardNode cardNode;
    public NumberNode numberNode;
    public PNode parent;

    public void setEnabled( final boolean enabled ) { box.setVisible( enabled ); }

    public boolean isEnabled() { return box.getVisible(); }

    //Extra layer so it can have 2 levels of enabled/disabled
    public static class ShapeContainer extends PNode {
        public final PhetPPath shape;

        public ShapeContainer( PhetPPath shape ) {
            this.shape = shape;
            addChild( shape );
        }
    }
}