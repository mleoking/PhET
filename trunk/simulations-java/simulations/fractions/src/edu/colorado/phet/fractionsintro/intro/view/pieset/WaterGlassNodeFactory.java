// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.pieset;

import fj.F;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Function that creates water glass nodes for PieSetNode.
 *
 * @author Sam Reid
 */
public class WaterGlassNodeFactory extends F<SliceNodeArg, PNode> {
    @Override public PNode f( SliceNodeArg sliceNodeArg ) {
        return new PText( "Hello" );
    }
}