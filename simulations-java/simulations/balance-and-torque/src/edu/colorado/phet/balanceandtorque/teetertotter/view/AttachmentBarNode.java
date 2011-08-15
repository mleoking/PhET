// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import edu.colorado.phet.balanceandtorque.teetertotter.model.AttachmentBar;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;

/**
 * Graphic for the bar that connects from the pivot point of the fulcrum to the
 * plank.
 *
 * @author John Blanco
 */
public class AttachmentBarNode extends ModelObjectNode {
    private static final Color BASE_COLOR = Color.LIGHT_GRAY;

    public AttachmentBarNode( final ModelViewTransform mvt, final AttachmentBar attachmentBar ) {
        super( mvt, attachmentBar, createGradientPaint( mvt, attachmentBar ) );
        // Add a couple of nodes to create something that looks like a pivot.
        double pivotWidth = mvt.modelToViewDeltaX( attachmentBar.WIDTH * 2 );
        PNode pivotNode = new PhetPPath( new Ellipse2D.Double( -pivotWidth / 2, -pivotWidth / 2, pivotWidth, pivotWidth ), BASE_COLOR, new BasicStroke( 1 ), Color.BLACK );
        pivotNode.setOffset( mvt.modelToView( attachmentBar.getPivotPoint() ) );
        pivotNode.addChild( new PhetPPath( new Ellipse2D.Double( -pivotWidth / 10, -pivotWidth / 10, pivotWidth / 5, pivotWidth / 5 ), Color.BLACK ) );
        addChild( pivotNode );
        // Watch the shape and update the gradient as needed.
        attachmentBar.addShapeObserver( new VoidFunction1<Shape>() {
            public void apply( Shape shape ) {
                setPaint( createGradientPaint( mvt, attachmentBar ) );
            }
        } );
    }

    private static Paint createGradientPaint( ModelViewTransform mvt, AttachmentBar attachmentBar ) {
        Vector2D gradientAdjustmentVector = new Vector2D( AttachmentBar.WIDTH / 2, 0 ).rotate( attachmentBar.getDeflectionAngle() );
        Vector2D pivotPointVector = new Vector2D( attachmentBar.getPivotPoint() );
        return new GradientPaint( mvt.modelToView( new Vector2D( pivotPointVector ).subtract( gradientAdjustmentVector ).toPoint2D() ),
                                  ColorUtils.brighterColor( BASE_COLOR, 0.5 ),
                                  mvt.modelToView( new Vector2D( pivotPointVector ).add( gradientAdjustmentVector ).toPoint2D() ),
                                  ColorUtils.darkerColor( BASE_COLOR, 0.5 ) );
    }
}
