// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.pieset;

import fj.F;

import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Function that creates water glass nodes for PieSetNode.
 *
 * @author Sam Reid
 */
public class WaterGlassNodeFactory extends F<Slice, PNode> {
    @Override public PNode f( final Slice slice ) {
        return new PText( "Hello" ) {{
            setOffset( slice.shape().getBounds2D().getCenterX(), slice.shape().getBounds2D().getCenterY() );
        }};
    }
}