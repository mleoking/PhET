// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.bendinglight.BendingLightApplication;
import edu.colorado.phet.bendinglight.model.Laser;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.*;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.*;

/**
 * Piccolo node for drawing the laser itself, including an on/off button and ability to rotate/translate.
 *
 * @author Sam Reid
 */
public class LaserNode extends PNode {
    boolean debug = false;

    public LaserNode( final ModelViewTransform transform,
                      final Laser laser,
                      final Property<Boolean> showRotationDragHandles,
                      final Property<Boolean> showTranslationDragHandles,
                      final Function1<Double, Double> clampDragAngle,
                      Function2<Shape, Shape, Shape> translationRegion,//Select from the entire region and front region which should be used for translating the laser.  Signature is (full region, front region)=>selection region
                      Function2<Shape, Shape, Shape> rotationRegion, //Select from the entire region and back region which should be used for rotating the laser.  Signature is (full region,back region)=> selected region
                      String imageName ) {
        //Load the image
        final BufferedImage image = flipY( flipX( BendingLightApplication.RESOURCES.getImage( imageName ) ) );

        //Properties to help identify where the mouse is so that arrows can be show indicating how the laser can be dragged
        final BooleanProperty mouseOverRotationPart = new BooleanProperty( false );
        final BooleanProperty mouseOverTranslationPart = new BooleanProperty( false );
        final BooleanProperty draggingRotation = new BooleanProperty( false );
        final BooleanProperty draggingTranslation = new BooleanProperty( false );

        //Continue to show the rotation arrows even if the mouse is outside of the region if the mouse is currently rotating the laser
        final Or showRotationArrows = mouseOverRotationPart.or( draggingRotation );
        showRotationArrows.addObserver( new SimpleObserver() {
            public void update() {
                showRotationDragHandles.setValue( showRotationArrows.getValue() );
            }
        } );

        //Couldn't use phetcommon Not since this uses nonsettable parent
        class ObservableNot extends ObservableProperty<Boolean> {
            private ObservableProperty<Boolean> parent;

            public ObservableNot( final ObservableProperty<Boolean> parent ) {
                this.parent = parent;
                parent.addObserver( new SimpleObserver() {
                    public void update() {
                        notifyObservers();
                    }
                } );
            }

            public Boolean getValue() {
                return !parent.getValue();
            }
        }

        //Continue to show the translation arrows even if the mouse is outside of the region if the mouse is currently translating the laser
        final Or doShowTranslationArrows = mouseOverTranslationPart.or( draggingTranslation );
        final And a = new And( doShowTranslationArrows, new ObservableNot( showRotationArrows ) );
        a.addObserver( new SimpleObserver() {
            public void update() {
                showTranslationDragHandles.setValue( doShowTranslationArrows.getValue() );
            }
        } );

        //Show the laser image
        addChild( new PImage( image ) );

        //Drag handlers can choose which of these regions to use for drag events
        double fractionBackToRotateHandle = 34.0 / 177.0;//for the rotatable laser, just use the part of the image that looks like a knob be used for rotation
        Rectangle2D.Double frontRectangle = new Rectangle2D.Double( 0, 0, image.getWidth() * ( 1 - fractionBackToRotateHandle ), image.getHeight() );
        Rectangle2D.Double backRectangle = new Rectangle2D.Double( image.getWidth() * ( 1 - fractionBackToRotateHandle ), 0, image.getWidth() * fractionBackToRotateHandle, image.getHeight() );
        Rectangle2D.Double fullRectangle = new Rectangle2D.Double( 0, 0, image.getWidth(), image.getHeight() );

        //A drag region is an invisible shape that can be dragged with the mouse
        class DragRegion extends PhetPPath {
            DragRegion( Shape shape, Paint fill, final VoidFunction1<PInputEvent> eventHandler, final BooleanProperty isMouseOver, final BooleanProperty isDragging ) {
                super( shape, fill );
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new PBasicInputEventHandler() {
                    public void mouseEntered( PInputEvent event ) {
                        isMouseOver.setValue( true );
                    }

                    public void mouseExited( PInputEvent event ) {
                        isMouseOver.setValue( false );
                    }

                    public void mouseReleased( PInputEvent event ) {
                        isDragging.setValue( false );
                    }

                    public void mousePressed( PInputEvent event ) {
                        isDragging.setValue( true );
                    }

                    public void mouseDragged( PInputEvent event ) {
                        eventHandler.apply( event );
                    }
                } );
            }
        }

        //Set up the colors to be invisible (or red and blue for debugging)
        final Color dragRegionColor;
        final Color rotationRegionColor;
        if ( debug ) {
            dragRegionColor = new Color( 255, 0, 0, 128 );
            rotationRegionColor = new Color( 0, 0, 255, 128 );
        }
        else {
            dragRegionColor = new Color( 255, 0, 0, 0 );
            rotationRegionColor = new Color( 0, 0, 255, 0 );
        }

        //Add the drag region for translating the laser
        addChild( new DragRegion( translationRegion.apply( fullRectangle, frontRectangle ), dragRegionColor, new VoidFunction1<PInputEvent>() {
            public void apply( PInputEvent event ) {
                laser.translate( transform.viewToModelDelta( event.getDeltaRelativeTo( getParent().getParent() ) ) );
            }
        }, mouseOverTranslationPart, draggingTranslation ) );

        //Add the drag region of rotating the laser
        addChild( new DragRegion( rotationRegion.apply( fullRectangle, backRectangle ), rotationRegionColor, new VoidFunction1<PInputEvent>() {
            public void apply( PInputEvent event ) {
                ImmutableVector2D modelPoint = new ImmutableVector2D( transform.viewToModel( event.getPositionRelativeTo( getParent().getParent() ) ) );
                ImmutableVector2D vector = modelPoint.minus( laser.pivot.getValue() );
                final double angle = vector.getAngle();
                double after = clampDragAngle.apply( angle );
                laser.setAngle( after );
            }
        }, mouseOverRotationPart, draggingRotation ) );

        //Update the transform of the laser when its model data (pivot or emission point) changes
        new RichSimpleObserver() {
            public void update() {
                Point2D emissionPoint = transform.modelToView( laser.emissionPoint.getValue() ).toPoint2D();
                final double angle = transform.modelToView( ImmutableVector2D.parseAngleAndMagnitude( 1, laser.getAngle() ) ).getAngle();

                final AffineTransform t = new AffineTransform();
                t.translate( emissionPoint.getX(), emissionPoint.getY() );
                t.rotate( angle );
                t.translate( 0, -image.getHeight() / 2 );

                LaserNode.this.setTransform( t );
            }
        }.observe( laser.pivot, laser.emissionPoint );

        //Show the button on the laser that turns it on and off
        final BufferedImage pressed = flipY( flipX( multiScaleToHeight( BendingLightApplication.RESOURCES.getImage( "button_pressed.png" ), 42 ) ) );
        final BufferedImage unpressed = flipY( flipX( multiScaleToHeight( BendingLightApplication.RESOURCES.getImage( "button_unpressed.png" ), 42 ) ) );
        addChild( new PImage( pressed ) {{
            setOffset( -getFullBounds().getWidth() / 2 + image.getWidth() / 2, -getFullBounds().getHeight() / 2 + image.getHeight() / 2 );
            laser.on.addObserver( new SimpleObserver() {
                public void update() {
                    setImage( laser.on.getValue() ? pressed : unpressed );
                }
            } );

            //User interaction: when the user presses the red laser button turn the laser and off
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    laser.on.setValue( !laser.on.getValue() );
                }
            } );
        }} );
    }
}
