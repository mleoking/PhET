// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.bendinglight.model.VelocitySensor;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.bendinglight.BendingLightApplication.RESOURCES;

/**
 * @author Sam Reid
 */
public class VelocitySensorNode extends PNode {
    public VelocitySensorNode( final ModelViewTransform transform, final VelocitySensor velocitySensor ) {
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
                    final double value = velocitySensor.value.getValue();
                    setText( Double.isNaN( value ) ? "?" : new DecimalFormat( "0.0" ).format( value / BendingLightModel.SPEED_OF_LIGHT ) + " c" );
                    setOffset( readoutBounds.getCenterX() - getFullBounds().getWidth() / 2, readoutBounds.getCenterY() - getFullBounds().getHeight() / 2 );
                }
            } );
        }} );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override
            public void mouseDragged( PInputEvent event ) {
                velocitySensor.translate( transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) ) );
            }
        } );
        velocitySensor.position.addObserver( new SimpleObserver() {
            public void update() {
                final Point2D.Double viewPoint = transform.modelToView( velocitySensor.position.getValue() ).toPoint2D();
                setOffset( viewPoint.getX() - imageNode.getFullBounds().getWidth() / 2, viewPoint.getY() - imageNode.getFullBounds().getHeight() );
            }
        } );
    }
}
