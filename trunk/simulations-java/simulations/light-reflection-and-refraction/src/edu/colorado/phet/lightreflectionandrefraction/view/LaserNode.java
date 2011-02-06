// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.lightreflectionandrefraction.LightReflectionAndRefractionApplication;
import edu.colorado.phet.lightreflectionandrefraction.model.Laser;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.*;

/**
 * @author Sam Reid
 */
public class LaserNode extends PNode {

    public LaserNode( final ModelViewTransform transform, final Laser laser ) {
        final BufferedImage image = flipY( flipX( LightReflectionAndRefractionApplication.RESOURCES.getImage( "laser.png" ) ) );

        final PNode dragLines = new PNode() {{
            addChild( new ArrowNode( new Point2D.Double( image.getWidth() / 2, image.getHeight() / 2 ), new Point2D.Double( image.getWidth() / 2, image.getHeight() / 2 + 150 ), 20, 20, 10 ) {{
                setPaint( Color.green );
            }} );
            addChild( new ArrowNode( new Point2D.Double( image.getWidth() / 2, image.getHeight() / 2 ), new Point2D.Double( image.getWidth() / 2, image.getHeight() / 2 - 150 ), 20, 20, 10 ) {{
                setPaint( Color.green );
            }} );
            setPickable( false );
            setChildrenPickable( false );
            setVisible( false );
        }};

        addChild( dragLines );
        final BooleanProperty mouseOver = new BooleanProperty( false );
        final BooleanProperty dragging = new BooleanProperty( false );

        mouseOver.or( dragging ).addObserver( new SimpleObserver() {
            public void update() {
                dragLines.setVisible( mouseOver.or( dragging ).getValue() );
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
                    double after = angle;
                    if ( angle < -Math.PI / 2 ) { after = Math.PI; }
                    if ( angle < Math.PI / 2 && angle > 0 ) { after = Math.PI / 2; }
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

        final BufferedImage pressed = flipY( flipX( multiScaleToHeight( LightReflectionAndRefractionApplication.RESOURCES.getImage( "button_pressed.png" ), 42 ) ) );
        final BufferedImage unpressed = flipY( flipX( multiScaleToHeight( LightReflectionAndRefractionApplication.RESOURCES.getImage( "button_unpressed.png" ), 42 ) ) );
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
