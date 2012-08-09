// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.view.pieset;

import fj.F;
import fj.data.List;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.fractions.common.view.FNode;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.Slice;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * Piccolo node that shows movable pieces and clipping region for bottomless bucket.
 *
 * @author Sam Reid
 */
public class MovableSliceLayer extends PNode {
    public MovableSliceLayer( final PieSet state, final F<SliceNodeArgs, PNode> createSliceNode, final SettableProperty<PieSet> model, final PNode rootNode, final BucketView bucketView ) {

        final F<Slice, MovableSliceNode> map = new F<Slice, MovableSliceNode>() {
            @Override public MovableSliceNode f( Slice slice ) {
                final SliceNodeArgs args = new SliceNodeArgs( slice, model.get().denominator, state.isInContainer( slice ) );
                return new MovableSliceNode( createSliceNode.f( args ), rootNode, model, slice, args.denominator );
            }
        };
        List<MovableSliceNode> nodes = state.slices.map( map );
        final FNode childNodes = new FNode( nodes );

        //Show graphics for the movable cells.  Put in a clip so that long bars will look like they sink through a "bottomless" bucket since they are too big at full size
        PNode movablePiecesLayer = new PClip() {
            {
                setStroke( null );

                //Create a clip that is far enough away that it will work for a variety of screen resolutions
                final double far = 10000;

                //For primary representations, cut out the part below the bucket.  This is null for the scaled representation on Equality Lab tab because there are no buckets there
                setPathTo( bucketView == null ? new Rectangle2D.Double( -far, -far, far * 3, far * 3 ) :
                           new Area() {{
                               add( new Area( new Rectangle2D.Double( -far, -far, far * 3, far * 3 ) ) );
                               final PBounds fullBounds = bucketView.getFrontNode().getFullBounds();
                               Rectangle2D rect = new Rectangle2D.Double( fullBounds.getX() + 34, fullBounds.getMaxY() - 15, fullBounds.getWidth() - 34 * 2 + 6, fullBounds.getMaxY() + 1000 );
                               subtract( new Area( rect ) );
                           }} );
                addChild( childNodes );
            }

            //Hack workaround to improve performance, see #3314: keep the dirty rectangles small so the painting will be fast.
            //Without this, the entire 10000x10000 rectangle gets flagged as dirty--we just want the moving children nodes marked as dirty and repainted
            public PBounds computeFullBounds( final PBounds dstBounds ) {
                final PBounds result = getUnionOfChildrenBounds( dstBounds );
                localToParent( result );
                return result;
            }
        };
        addChild( movablePiecesLayer );
    }
}