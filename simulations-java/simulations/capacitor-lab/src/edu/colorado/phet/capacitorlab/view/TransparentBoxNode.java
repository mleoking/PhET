// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.common.phetcommon.math.Dimension3D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * A box that, when made transparent, shows the outline of its back (occluded) faces.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TransparentBoxNode extends BoxNode {

    // properties of the back (occluded) faces
    private static final Stroke BACK_STROKE = new BasicStroke( 1f );
    private static final Color BACK_STROKE_COLOR = Color.GRAY;
    private static final Color BACK_FILL_COLOR = new Color( 0, 0, 0, 0 ); // transparent, we only care about their outlines

    private final PPath bottomNode, backNode, leftSideNode; // back (occluded) faces

    public TransparentBoxNode( CLModelViewTransform3D mvt, Color color, Dimension3D size ) {
        super( mvt, color, size );

        // back faces
        bottomNode = new PhetPPath( getShapeCreator().createBottomFace( size ), BACK_FILL_COLOR, BACK_STROKE, BACK_STROKE_COLOR );
        backNode = new PhetPPath( getShapeCreator().createBackFace( size ), BACK_FILL_COLOR, BACK_STROKE, BACK_STROKE_COLOR );
        leftSideNode = new PhetPPath( getShapeCreator().createLeftSideFace( size ), BACK_FILL_COLOR, BACK_STROKE, BACK_STROKE_COLOR );

        addChild( bottomNode );
        addChild( backNode );
        addChild( leftSideNode );

        bottomNode.moveToBack();
        backNode.moveToBack();
        leftSideNode.moveToBack();
    }

    @Override protected void updateShapes() {
        super.updateShapes();
        bottomNode.setPathTo( getShapeCreator().createBottomFace( getBoxSize() ) );
        backNode.setPathTo( getShapeCreator().createBackFace( getBoxSize() ) );
        leftSideNode.setPathTo( getShapeCreator().createLeftSideFace( getBoxSize() ) );
    }

    public void setTransparent( boolean transparent ) {
        setTransparency( ( transparent ) ? 0.5f : 1f );
    }
}
