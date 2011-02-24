// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.bendinglight.BendingLightApplication;
import edu.colorado.phet.bendinglight.model.Laser;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.Or;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.*;

/**
 * @author Sam Reid
 */
public class LaserNode extends PNode {

    public LaserNode( final ModelViewTransform transform, final Laser laser, final Property<Boolean> showDragHandles, final Function1<Double, Double> clampDragAngle ) {
        final BufferedImage image = flipY( flipX( BendingLightApplication.RESOURCES.getImage( "laser.png" ) ) );
        final PNode clockwiseDragArrow = new PNode() {{
            addChild( new ArrowNode( new Point2D.Double( image.getWidth() / 2, image.getHeight() / 2 ), new Point2D.Double( image.getWidth() / 2, image.getHeight() / 2 - 150 ), 20, 20, 10 ) {{
                setPaint( Color.green );
            }} );
            setPickable( false );
            setChildrenPickable( false );
            setVisible( false );
        }};

        addChild( clockwiseDragArrow );
        final BooleanProperty mouseOver = new BooleanProperty( false );
        final BooleanProperty dragging = new BooleanProperty( false );

        final Or doShowArrow = mouseOver.or( dragging );

        doShowArrow.addObserver( new SimpleObserver() {
            public void update() {
                showDragHandles.setValue( doShowArrow.getValue() );
            }
        } );

        addChild( new PImage( image ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseEntered( PInputEvent event ) {
                    mouseOver.setValue( true );
                }

                public void mouseExited( PInputEvent event ) {
                    mouseOver.setValue( false );
                }

                public void mouseReleased( PInputEvent event ) {
                    dragging.setValue( false );
                }

                public void mousePressed( PInputEvent event ) {
                    dragging.setValue( true );
                }

                public void mouseDragged( PInputEvent event ) {
                    Point2D viewPt = event.getPositionRelativeTo( getParent().getParent() );
                    ImmutableVector2D modelPoint = new ImmutableVector2D( transform.viewToModel( viewPt ) );
                    final double angle = modelPoint.getAngle();
                    double after = clampDragAngle.apply( angle );
                    laser.angle.setValue( after );
                }
            } );
            laser.angle.addObserver( new SimpleObserver() {
                public void update() {
                    Point2D emissionPoint = transform.modelToView( laser.getEmissionPoint() ).toPoint2D();
                    final double angle = transform.modelToView( ImmutableVector2D.parseAngleAndMagnitude( 1, laser.angle.getValue() ) ).getAngle();

                    final AffineTransform t = new AffineTransform();
                    t.translate( emissionPoint.getX(), emissionPoint.getY() );
                    t.rotate( angle );
                    t.translate( 0, -image.getHeight() / 2 );

                    LaserNode.this.setTransform( t );
                }
            } );
        }} );

        final BufferedImage pressed = flipY( flipX( multiScaleToHeight( BendingLightApplication.RESOURCES.getImage( "button_pressed.png" ), 42 ) ) );
        final BufferedImage unpressed = flipY( flipX( multiScaleToHeight( BendingLightApplication.RESOURCES.getImage( "button_unpressed.png" ), 42 ) ) );
        addChild( new PImage( pressed ) {{
            setOffset( -getFullBounds().getWidth() / 2 + image.getWidth() / 2, -getFullBounds().getHeight() / 2 + image.getHeight() / 2 );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    laser.on.setValue( !laser.on.getValue() );
                }
            } );
            laser.on.addObserver( new SimpleObserver() {
                public void update() {
                    setImage( laser.on.getValue() ? pressed : unpressed );
                }
            } );
        }} );
    }
}
