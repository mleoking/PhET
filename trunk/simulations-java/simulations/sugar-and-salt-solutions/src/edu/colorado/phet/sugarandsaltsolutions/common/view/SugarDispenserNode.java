// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarDispenser;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.RESOURCES;

/**
 * Sugar dispenser which can be rotated to pour out an endless supply of sugar.
 *
 * @author Sam Reid
 */
public class SugarDispenserNode extends PNode {
    private final boolean debug = false;

    public SugarDispenserNode( final ModelViewTransform transform, final SugarDispenser model ) {
        //Show the image of the shaker
        final BufferedImage openImage = BufferedImageUtils.multiScaleToHeight( RESOURCES.getImage( "sugar_open.png" ), 250 );
        final BufferedImage closedImage = BufferedImageUtils.multiScaleToHeight( RESOURCES.getImage( "sugar_closed.png" ), 250 );
        final PImage imageNode = new PImage( closedImage );
        addChild( imageNode );

        //Hide the sugar dispenser if it is not enabled (selected by the user)
        model.enabled.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean enabled ) {
                setVisible( enabled );
            }
        } );

        //Choose the image based on the angle.  If it is tipped sideways the opening should flip open.
        model.open.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean open ) {
                imageNode.setImage( open ? openImage : closedImage );
            }
        } );

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
                model.translate( transform.viewToModelDelta( event.getDeltaRelativeTo( SugarDispenserNode.this.getParent() ) ) );

                if ( debug ) {
                    System.out.println( "model.rotationPoint = " + model.center );
                }
            }
        } );

        //Show a hand cursor when over the dispenser
        addInputEventListener( new CursorHandler() );
    }
}