/* Copyright 2010, University of Colorado */

package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.BuildAnAtomModel.Bucket;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 *
 * @author John Blanco
 */
public class BucketNode extends PNode {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private static final Font LABEL_FONT = new PhetFont( 18, true );

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public BucketNode( Bucket bucket, ModelViewTransform2D mvt ) {
        // Create a scaling transform based on the provided MVT, since we only
        // want the scaling portion and we want to avoid any translation.
        AffineTransform scaleTransform = AffineTransform.getScaleInstance( mvt.getAffineTransform().getScaleX(),
                mvt.getAffineTransform().getScaleY() );

        // Create the scaled shapes.
        Shape scaledHoleShape = scaleTransform.createTransformedShape( bucket.getHoleShape() );
        Shape scaledContainerShape = scaleTransform.createTransformedShape( bucket.getContainerShape() );

        // Create and add the container node.
        Paint containerPaint = new GradientPaint(
                new Point2D.Double( scaledHoleShape.getBounds2D().getMinX(), scaledHoleShape.getBounds2D().getCenterY() ),
                ColorUtils.brighterColor( bucket.getBaseColor(), 0.5 ),
                new Point2D.Double( scaledHoleShape.getBounds2D().getMaxX(), scaledHoleShape.getBounds2D().getCenterY() ),
                ColorUtils.darkerColor( bucket.getBaseColor(), 0.5 ) );
        addChild( new PhetPPath( scaledContainerShape, containerPaint ) );

        // Create and add the hole node.
        Paint holePaint = new GradientPaint(
                new Point2D.Double( scaledHoleShape.getBounds2D().getMinX(), scaledHoleShape.getBounds2D().getCenterY() ),
                Color.BLACK,
                new Point2D.Double( scaledHoleShape.getBounds2D().getMaxX(), scaledHoleShape.getBounds2D().getCenterY() ),
                Color.LIGHT_GRAY );
        addChild( new PhetPPath( scaledHoleShape, holePaint, new BasicStroke( 1f ), Color.GRAY ) );

        // Create and add the caption (if provided).
        if ( bucket.getCaptionText() != null ) {
            PText caption = new PText( bucket.getCaptionText() );
            caption.setFont( LABEL_FONT );
            caption.setTextPaint( Color.WHITE );
            if ( caption.getFullBoundsReference().getWidth() > scaledContainerShape.getBounds().getWidth() * 0.8 ) {
                // The caption must be scaled in order to fit on the container.
                caption.scale( scaledContainerShape.getBounds().getWidth() * 0.8 / caption.getFullBoundsReference().getWidth() );
            }
            caption.setOffset(
                    scaledContainerShape.getBounds2D().getCenterX() - caption.getFullBoundsReference().getWidth() / 2,
                    scaledContainerShape.getBounds2D().getCenterY() - caption.getFullBoundsReference().getHeight() / 2 );
            addChild( caption );
        }
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}
