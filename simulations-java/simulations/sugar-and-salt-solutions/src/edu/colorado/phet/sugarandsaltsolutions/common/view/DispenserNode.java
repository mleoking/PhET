// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.RelativeDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Dispenser;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel.BEAKER_HEIGHT;
import static java.lang.Double.POSITIVE_INFINITY;

/**
 * @author Sam Reid
 */
public class DispenserNode<T extends Dispenser> extends PNode {
    private final boolean debug = false;
    protected PImage imageNode;

    public DispenserNode( final ModelViewTransform transform, final T model, BufferedImage image ) {
        //Show the image of the shaker
        imageNode = new PImage( image );
        addChild( imageNode );

        //Update the AffineTransform for the image when the model changes
        new RichSimpleObserver() {
            @Override public void update() {
                //Clear the transform to start over
                imageNode.setTransform( new AffineTransform() );

                //Find the view coordinates of the rotation point of the model (its center)
                Point2D.Double viewPoint = transform.modelToView( model.center.get() ).toPoint2D();

                //Rotate by the correct angle: Note: This angle doesn't get mapped into the right coordinate frame, so could be backwards
                imageNode.rotate( -model.angle.get() );

                //Center on the view point
                imageNode.centerFullBoundsOnPoint( viewPoint.x, viewPoint.y );
            }
        }.observe( model.center, model.angle );

        //Show a rectangle at the rotation point of the shaker
        if ( debug ) {
            addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, 10, 10 ), Color.blue ) {{
                new RichSimpleObserver() {
                    @Override public void update() {
                        setOffset( transform.modelToView( model.center.get() ).toPoint2D() );
                    }
                }.observe( model.center, model.angle );
            }} );
        }

        //Translate the shaker when dragged
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseDragged( PInputEvent event ) {
                model.translate( transform.viewToModelDelta( event.getDeltaRelativeTo( DispenserNode.this.getParent() ) ) );
            }
        } );

        //Allow dragging the shaker, making sure the cursor hand doesn't get away from the node
        // but don't let the shaker be dragged underwater
        addInputEventListener( new RelativeDragHandler( this, transform, model.center, new Function1<Point2D, Point2D>() {
            public Point2D apply( Point2D modelPoint ) {
                return new Point2D.Double( modelPoint.getX(), MathUtil.clamp( BEAKER_HEIGHT * 1.3, modelPoint.getY(), POSITIVE_INFINITY ) );
            }
        } ) );

        //Show a hand cursor when over the dispenser
        addInputEventListener( new CursorHandler() );
    }
}
