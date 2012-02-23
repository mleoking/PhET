// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.pieset;

import fj.F;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0.Null;
import edu.colorado.phet.fractionsintro.intro.view.WaterGlassNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Function that creates water glass nodes for PieSetNode.
 *
 * @author Sam Reid
 */
public class WaterGlassNodeFactory extends F<SliceNodeArgs, PNode> {
    @Override public PNode f( final SliceNodeArgs args ) {
        return new WaterGlassNode( 1, args.denominator, new Null(), new Null() ) {{
            setOffset( args.slice.shape().getBounds2D().getCenterX() - getFullWidth() / 2, args.slice.shape().getBounds2D().getCenterY() - getFullHeight() / 2 );
        }};
    }
}