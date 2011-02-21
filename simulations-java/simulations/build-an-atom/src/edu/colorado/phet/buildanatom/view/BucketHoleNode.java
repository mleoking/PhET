// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.Bucket;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * This Piccolo2D node represents the hole in a bucket.  It exists as a
 * separate node from the front of the bucket because the front and the hold
 * generally need to be placed on different layers in order to be able to
 * support the visual effect of putting things in to the bucket.
 *
 * @author John Blanco
 */
public class BucketHoleNode extends PNode {

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public BucketHoleNode( Bucket bucket, ModelViewTransform2D mvt ) {

        // Create a scaling transform based on the provided MVT, since we only
        // want the scaling portion and we want to avoid any translation.
        AffineTransform scaleTransform = AffineTransform.getScaleInstance( mvt.getAffineTransform().getScaleX(),
                mvt.getAffineTransform().getScaleY() );

        // Create the scaled shape.
        Shape scaledHoleShape = scaleTransform.createTransformedShape( bucket.getHoleShape() );

        // Create and add the hole node.
        Paint holePaint = new GradientPaint(
                new Point2D.Double( scaledHoleShape.getBounds2D().getMinX(), scaledHoleShape.getBounds2D().getCenterY() ),
                Color.BLACK,
                new Point2D.Double( scaledHoleShape.getBounds2D().getMaxX(), scaledHoleShape.getBounds2D().getCenterY() ),
                Color.LIGHT_GRAY );
        PhetPPath holeNode = new PhetPPath( scaledHoleShape, holePaint, new BasicStroke( 1f ), Color.GRAY );
        addChild( holeNode );
    }
}
