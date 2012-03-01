// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view.pieset;

import fj.Effect;
import fj.F;
import fj.data.List;

import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.fractionsintro.intro.model.pieset.PieSet;
import edu.colorado.phet.fractionsintro.intro.model.pieset.Slice;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * Piccolo node that shows movable pieces and clipping region for bottomless bucket.
 *
 * @author Sam Reid
 */
public class MovablePiecesLayer extends PNode {
    public MovablePiecesLayer( final PieSet state, final F<SliceNodeArgs, PNode> createSliceNode, final SettableProperty<PieSet> model, final PNode rootNode, final double clipY ) {

        //Show graphics for the movable cells.  Put in a clip so that long bars will look like they sink through a "bottomless" bucket since they are too big at full size
        PClip movablePiecesLayer = new PClip() {{
            setStroke( null );

            //Create a clip that is far enough away that it will work for a variety of screen resolutions
            double far = 10000;

            setPathTo( new Rectangle2D.Double( -far, -far, far * 2, far + clipY ) );
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
        }};
        addChild( movablePiecesLayer );
    }
}
