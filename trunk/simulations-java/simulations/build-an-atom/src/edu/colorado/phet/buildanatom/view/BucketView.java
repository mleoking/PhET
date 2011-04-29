// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.buildanatom.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.buildanatom.model.Bucket;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This is the view representation of a bucket.  It is set up to have a sort
 * of "faux 3D" look, where it is tipped slightly so that the hole at the top
 * can be seen and there is shading on the outer portion.  It does NOT extend
 * a PNode, because it has a front and back that must be placed on different
 * layers in order to allow objects to look as though they are in the buckets,
 * so the API allows users to obtain the front and back layers separately.
 *
 * @author John Blanco
 */
public class BucketView {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    private static final Font LABEL_FONT = new PhetFont( 18, true );

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    // This node maintains two layers and makes those layers available via its
    // API.  This is done so that its parts can be added to different layers,
    // thus making more easy to make things look like they are in the bucket.
    private final PNode holeLayer = new PNode();
    private final PNode containerLayer = new PNode();

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public BucketView( Bucket bucket, ModelViewTransform mvt ) {
        // Create a scaling transform based on the provided MVT, since we only
        // want the scaling portion and we want to avoid any translation.
        AffineTransform scaleTransform = AffineTransform.getScaleInstance( mvt.getTransform().getScaleX(),
                mvt.getTransform().getScaleY() );

        // Create the scaled shapes.
        Shape scaledHoleShape = scaleTransform.createTransformedShape( bucket.getHoleShape() );
        Shape scaledContainerShape = scaleTransform.createTransformedShape( bucket.getContainerShape() );

        // Create and add the container node.
        Paint containerPaint = new GradientPaint(
                new Point2D.Double( scaledHoleShape.getBounds2D().getMinX(), scaledHoleShape.getBounds2D().getCenterY() ),
                ColorUtils.brighterColor( bucket.getBaseColor(), 0.5 ),
                new Point2D.Double( scaledHoleShape.getBounds2D().getMaxX(), scaledHoleShape.getBounds2D().getCenterY() ),
                ColorUtils.darkerColor( bucket.getBaseColor(), 0.5 ) );
        PhetPPath containerNode = new PhetPPath( scaledContainerShape, containerPaint );
        containerLayer.addChild( containerNode );

        // Create and add the hole node.
        Paint holePaint = new GradientPaint(
                new Point2D.Double( scaledHoleShape.getBounds2D().getMinX(), scaledHoleShape.getBounds2D().getCenterY() ),
                Color.BLACK,
                new Point2D.Double( scaledHoleShape.getBounds2D().getMaxX(), scaledHoleShape.getBounds2D().getCenterY() ),
                Color.LIGHT_GRAY );
        PhetPPath holeNode = new PhetPPath( scaledHoleShape, holePaint, new BasicStroke( 1f ), Color.GRAY );
        holeLayer.addChild( holeNode );

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
            containerLayer.addChild( caption );
        }

        holeLayer.setOffset( mvt.modelToView( bucket.getPosition() ) );
        containerLayer.setOffset( mvt.modelToView( bucket.getPosition() ) );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    public PNode getHoleLayer() {
        return holeLayer;
    }

    public PNode getContainerLayer() {
        return containerLayer;
    }

    public void setOffset( double x, double y ) {
        holeLayer.setOffset( x, y );
        containerLayer.setOffset( x, y );
    }

    public void setOffset( Point2D point ) {
        setOffset( point.getX(), point.getY() );
    }

    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------
}
