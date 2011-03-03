// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.bendinglight.model.VelocitySensor;
import edu.colorado.phet.bendinglight.modules.intro.ToolboxNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.bendinglight.BendingLightApplication.RESOURCES;

/**
 * @author Sam Reid
 */
public class VelocitySensorNode extends ToolboxNode.DoDragNode {
    private final ModelViewTransform transform;
    private final VelocitySensor velocitySensor;

    public VelocitySensorNode( final ModelViewTransform transform, final VelocitySensor velocitySensor ) {
        this.transform = transform;
        this.velocitySensor = velocitySensor;
        final Rectangle readoutBounds = new Rectangle( 18, 38, 96 - 18, 70 - 38 );
        final Rectangle titleBounds = new Rectangle( 18, 10, 96 - 18, 32 - 10 );
        final PImage imageNode = new PImage( RESOURCES.getImage( "velocity_detector.png" ) );
        addChild( imageNode );

        addChild( new PText( "Speed" ) {{
            setFont( new PhetFont( 22 ) );
            setOffset( titleBounds.getCenterX() - getFullBounds().getWidth() / 2, titleBounds.getCenterY() - getFullBounds().getHeight() / 2 );
        }} );
        addChild( new PText() {{
            setFont( new PhetFont( 32 ) );
            velocitySensor.value.addObserver( new SimpleObserver() {
                public void update() {
                    final Option<ImmutableVector2D> value = velocitySensor.value.getValue();
                    setText( ( value.isNone() ) ? "?" :
                             new DecimalFormat( "0.0" ).format( value.get().getMagnitude() / BendingLightModel.SPEED_OF_LIGHT ) + " c" );
                    setOffset( readoutBounds.getCenterX() - getFullBounds().getWidth() / 2, readoutBounds.getCenterY() - getFullBounds().getHeight() / 2 );
                }
            } );
        }} );

        addChild( new ArrowNode( new Point2D.Double(), new Point2D.Double( 100, 100 ), 20, 20, 10 ) {{
            setPaint( Color.blue );
            setStrokePaint( Color.black );
            setStroke( new BasicStroke( 1 ) );
            velocitySensor.value.addObserver( new SimpleObserver() {
                public void update() {
                    final Option<ImmutableVector2D> value = velocitySensor.value.getValue();
                    if ( value.isNone() ) {
                        setVisible( false );
                    }
                    else {
                        ImmutableVector2D v = transform.modelToViewDelta( value.get() ).times( 1.5E-14 );
                        setTipAndTailLocations( imageNode.getFullBounds().getCenterX() + v.getX() / 2, imageNode.getFullBounds().getMaxY() + v.getY() / 2,
                                                imageNode.getFullBounds().getCenterX() - v.getX() / 2, imageNode.getFullBounds().getMaxY() - v.getY() / 2 );
                        setVisible( true );
                    }
//                    setText( Double.isNaN( value ) ? "?" : new DecimalFormat( "0.0" ).format( value / BendingLightModel.SPEED_OF_LIGHT ) + " c" );
//                    setOffset( readoutBounds.getCenterX() - getFullBounds().getWidth() / 2, readoutBounds.getCenterY() - getFullBounds().getHeight() / 2 );
                }
            } );
        }} );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseDragged( PInputEvent event ) {
                doDrag( event );
            }
        } );
        velocitySensor.position.addObserver( new SimpleObserver() {
            public void update() {
                final Point2D.Double viewPoint = transform.modelToView( velocitySensor.position.getValue() ).toPoint2D();
                setOffset( viewPoint.getX() - imageNode.getFullBounds().getWidth() / 2, viewPoint.getY() - imageNode.getFullBounds().getHeight() );
            }
        } );
    }

    @Override
    public void doDrag( PInputEvent event ) {
        velocitySensor.translate( transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) ) );
    }
}
