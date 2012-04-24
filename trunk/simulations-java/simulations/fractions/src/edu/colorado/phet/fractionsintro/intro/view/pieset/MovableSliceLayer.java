// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.pieset;

import fj.Effect;
import fj.F;
import fj.data.List;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.piccolophet.nodes.BucketView;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that shows movable pieces and clipping region for bottomless bucket.
 *
 * @author Sam Reid
 */
public class MovableSliceLayer extends PNode {
    public MovableSliceLayer( final PieSet state, final F<SliceNodeArgs, PNode> createSliceNode, final SettableProperty<PieSet> model, final PNode rootNode, final BucketView bucketView ) {

        //Show graphics for the movable cells.  Put in a clip so that long bars will look like they sink through a "bottomless" bucket since they are too big at full size
//        PNode movablePiecesLayer = new PClip() {{
//            setStroke( null );

        //Create a clip that is far enough away that it will work for a variety of screen resolutions
        final double far = 10000;

        //For primary representations, cut out the part below the bucket.  This is null for the scaled representation on Equality Lab tab because there are no buckets there
//            setPathTo( bucketView == null ? new Rectangle2D.Double( -far, -far, far * 3, far * 3 ) :
//                       new Area() {{
//                           add( new Area( new Rectangle2D.Double( -far, -far, far * 3, far * 3 ) ) );
//                           final PBounds fullBounds = bucketView.getFrontNode().getFullBounds();
//                           Rectangle2D rect = new Rectangle2D.Double( fullBounds.getX() + 34, fullBounds.getMaxY() - 15, fullBounds.getWidth() - 34 * 2, fullBounds.getMaxY() + 1000 );
//                           subtract( new Area( rect ) );
//                       }} );
        List<MovableSliceNode> nodes = state.slices.map( new F<Slice, MovableSliceNode>() {
            @Override public MovableSliceNode f( Slice slice ) {
                return new MovableSliceNode( createSliceNode.f( new SliceNodeArgs( slice, model.get().denominator, state.isInContainer( slice ) ) ), rootNode, model, slice );
            }
        } );
        nodes.foreach( new Effect<MovableSliceNode>() {
            @Override public void e( MovableSliceNode m ) {
                addChild( m );
            }
        } );
//        }};
//        addChild( movablePiecesLayer );
    }
}