// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.event.RelativeDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Dispenser;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Base class for Piccolo nodes for sugar or salt shakers.
 *
 * @author Sam Reid
 */
public class DispenserNode<U extends SugarAndSaltSolutionModel, T extends Dispenser<U>> extends PNode {
    private final boolean debug = false;
    protected final PImage imageNode;
    private final ModelViewTransform transform;
    private final T model;
    private final PNode textLabel;

    public DispenserNode( final ModelViewTransform transform, final T model, Function1<Point2D, Point2D> dragConstraint ) {
        this.transform = transform;
        this.model = model;

        //Show the image of the shaker, with the text label on the side of the dispenser
        imageNode = new PImage();
        addChild( imageNode );

        //Text label that shows "Sugar" or "Salt" along the axis of the dispenser.  It is a child of the image node so it will move and rotate with the image node.
        textLabel = new PNode() {{
            addChild( new HTMLNode( model.name ) {{
                setFont( new PhetFont( 30 ) );
                rotateInPlace( Math.PI / 2 );
            }} );
        }};
        imageNode.addChild( textLabel );

        //Update the AffineTransform for the image when the model changes
        new RichSimpleObserver() {
            @Override public void update() {
                updateTransform();
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
        addInputEventListener( new RelativeDragHandler( this, transform, model.center, dragConstraint ) {
            @Override public void mouseDragged( PInputEvent event ) {

                //Set the model height of the dispenser so the model will be able to emit crystals in the right location (at the output part of the image)
                model.setDispenserHeight( transform.viewToModelDeltaY( imageNode.getFullBounds().getHeight() ) );

                //Handle super drag after setting the dispenser height in case crystals are emitted
                super.mouseDragged( event );

                //The thing you are dragging should always go in front.
                moveToFront();
            }

            //Override the setModelPosition to use a call to translate, since that is the call used to shake out crystals
            @Override protected void setModelPosition( Point2D constrained ) {
                ImmutableVector2D initialPoint = model.center.get();
                ImmutableVector2D finalPoint = new ImmutableVector2D( constrained );
                ImmutableVector2D delta = finalPoint.minus( initialPoint );
                model.translate( delta.toDimension() );
            }
        } );

        //Show a hand cursor when over the dispenser
        addInputEventListener( new CursorHandler() );
    }

    protected void updateTransform() {

        //Clear the transform to start over
        imageNode.setTransform( new AffineTransform() );

        //Find the view coordinates of the rotation point of the model (its center)
        Point2D.Double viewPoint = transform.modelToView( model.center.get() ).toPoint2D();

        //Rotate by the correct angle: Note: This angle doesn't get mapped into the right coordinate frame, so could be backwards
        imageNode.rotate( -model.angle.get() );

        //Center on the view point
        imageNode.centerFullBoundsOnPoint( viewPoint.x, viewPoint.y );

        //Update the location of the text label to remain centered in the image since the image could have changed size
        textLabel.setOffset( imageNode.getWidth() / 2 - textLabel.getFullBounds().getWidth() / 2, imageNode.getHeight() / 2 - textLabel.getFullBounds().getWidth() / 2 );
    }
}