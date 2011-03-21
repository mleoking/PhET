// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.bendinglight.model.VelocitySensor;
import edu.colorado.phet.bendinglight.modules.intro.ToolNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.ThreeImageNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.bendinglight.BendingLightApplication.RESOURCES;

/**
 * The VelocitySensorNode provides a draggable display of the velocity (speed and direction) of something.
 *
 * @author Sam Reid
 */
public class VelocitySensorNode extends ToolNode {
    private final ModelViewTransform transform;
    private final VelocitySensor velocitySensor;

    public VelocitySensorNode( final ModelViewTransform transform, final VelocitySensor velocitySensor ) {
        this.transform = transform;
        this.velocitySensor = velocitySensor;
        final int titleOffsetY = 7;
        final int readoutOffsetY = 38;

        final ThreeImageNode imageNode = new ThreeImageNode( RESOURCES.getImage( "velocity_left.png" ), RESOURCES.getImage( "velocity_center.png" ), RESOURCES.getImage( "velocity_right.png" ) );
        addChild( imageNode );

        final PText titleNode = new PText( "Speed" ) {{
            setFont( new PhetFont( 22 ) );
            imageNode.addCenterWidthObserver( new SimpleObserver() {
                public void update() {
                    setOffset( imageNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, titleOffsetY );
                }
            } );
        }};
        addChild( titleNode );
        addChild( new PText() {{
            setFont( new PhetFont( 26 ) );
            final SimpleObserver updateTextLocation = new SimpleObserver() {
                public void update() {
                    setOffset( imageNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, readoutOffsetY );
                }
            };
            imageNode.addCenterWidthObserver( updateTextLocation );
            velocitySensor.value.addObserver( new SimpleObserver() {
                public void update() {
                    final Option<ImmutableVector2D> value = velocitySensor.value.getValue();
                    setText( ( value.isNone() ) ? "?" :
                             new DecimalFormat( "0.00" ).format( value.get().getMagnitude() / BendingLightModel.SPEED_OF_LIGHT ) + " c" );
                    imageNode.setCenterWidth( Math.max( titleNode.getFullBounds().getWidth(), getFullBounds().getWidth() ) );
                    updateTextLocation.update();
                }
            } );
        }} );
        final BufferedImage velocityPoint = RESOURCES.getImage( "velocity_point.png" );
        final PImage velocityPointNode = new PImage( velocityPoint ) {{
            final PropertyChangeListener updatePosition = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    setOffset( imageNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, imageNode.getFullBounds().getMaxY() );
                }
            };
            imageNode.addPropertyChangeListener( PROPERTY_FULL_BOUNDS, updatePosition );
            updatePosition.propertyChange( null );
        }};
        addChild( velocityPointNode );

        addChild( new ArrowNode( new Point2D.Double(), new Point2D.Double( 100, 100 ), 20, 20, 10 ) {{
            setPaint( Color.blue );
            setStrokePaint( Color.black );
            setStroke( new BasicStroke( 1 ) );
            final PropertyChangeListener updateArrow = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    final Option<ImmutableVector2D> value = velocitySensor.value.getValue();
                    if ( value.isNone() ) {
                        setVisible( false );
                    }
                    else {
                        ImmutableVector2D v = transform.modelToViewDelta( value.get() ).times( 1.5E-14 );

                        //Show speed vector centered
//                        setTipAndTailLocations( velocityPointNode.getFullBounds().getCenterX() + v.getX() / 2, velocityPointNode.getFullBounds().getMaxY() + v.getY() / 2,
//                                                velocityPointNode.getFullBounds().getCenterX() - v.getX() / 2, velocityPointNode.getFullBounds().getMaxY() - v.getY() / 2 );

                        //Show speed vector at the tail instead of centered
                        setTipAndTailLocations( velocityPointNode.getFullBounds().getCenterX() + v.getX(), velocityPointNode.getFullBounds().getMaxY() + v.getY(),
                                                velocityPointNode.getFullBounds().getCenterX(), velocityPointNode.getFullBounds().getMaxY() );
                        setVisible( true );
                    }
                }
            };
            velocityPointNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, updateArrow );
            velocitySensor.value.addObserver( new SimpleObserver() {
                public void update() {
                    updateArrow.propertyChange( null );
                }
            } );
        }} );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseDragged( PInputEvent event ) {
                dragAll( event.getDeltaRelativeTo( getParent() ) );
            }
        } );
        velocitySensor.position.addObserver( new SimpleObserver() {
            public void update() {
                final Point2D.Double viewPoint = transform.modelToView( velocitySensor.position.getValue() ).toPoint2D();
                setOffset( viewPoint.getX() - imageNode.getFullBounds().getWidth() / 2, viewPoint.getY() - imageNode.getFullBounds().getHeight() - velocityPoint.getHeight() );
            }
        } );
    }

    @Override public void dragAll( PDimension delta ) {
        velocitySensor.translate( transform.viewToModelDelta( delta ) );
    }
}