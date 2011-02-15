// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lightreflectionandrefraction.LightReflectionAndRefractionApplication;
import edu.colorado.phet.lightreflectionandrefraction.model.IntensityMeter;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class IntensityMeterNode extends PNode {
    private final ModelViewTransform transform;
    private final IntensityMeter intensityMeter;
    public PImage bodyNode;
    public PImage sensorNode;

    public IntensityMeterNode( final ModelViewTransform transform, final IntensityMeter intensityMeter ) {
        this.transform = transform;
        this.intensityMeter = intensityMeter;
        intensityMeter.enabled.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( intensityMeter.enabled.getValue() );
            }
        } );

        sensorNode = new PImage( LightReflectionAndRefractionApplication.RESOURCES.getImage( "intensity_meter_probe.png" ) ) {{
            intensityMeter.sensorPosition.addObserver( new SimpleObserver() {
                public void update() {
                    final Point2D.Double sensorViewPoint = transform.modelToView( intensityMeter.sensorPosition.getValue() ).toPoint2D();
                    setOffset( sensorViewPoint.getX() - getFullBounds().getWidth() / 2, sensorViewPoint.getY() - getFullBounds().getHeight() * 0.32 );
                }
            } );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    intensityMeter.translateSensor( transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) ) );
                }
            } );
        }};
        final PhetPPath sensorHotSpotDebugger = new PhetPPath( new BasicStroke( 1 ), Color.green ) {{
            intensityMeter.sensorPosition.addObserver( new SimpleObserver() {
                public void update() {
                    setPathTo( transform.modelToView( intensityMeter.getSensorShape() ) );
                }
            } );
            setPickable( false );
            setChildrenPickable( false );
        }};

        bodyNode = new PImage( LightReflectionAndRefractionApplication.RESOURCES.getImage( "intensity_meter_box.png" ) ) {{
            intensityMeter.bodyPosition.addObserver( new SimpleObserver() {
                public void update() {
                    setOffset( transform.modelToView( intensityMeter.bodyPosition.getValue() ).toPoint2D() );
                }
            } );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    intensityMeter.translateBody( transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) ) );
                }
            } );
        }};
        bodyNode.addChild( new PText( "Intensity" ) {{
            setFont( new PhetFont( 22 ) );
            setTextPaint( Color.white );
            setOffset( bodyNode.getFullBounds().getWidth() / 2 - getFullBounds().getWidth() / 2, bodyNode.getFullBounds().getHeight() * 0.1 );
        }} );

        bodyNode.addChild( new PText( "-" ) {{
            setFont( new PhetFont( 30 ) );
            intensityMeter.reading.addObserver( new SimpleObserver() {
                public void update() {
                    setTransform( new AffineTransform() );
                    setText( intensityMeter.reading.getValue().getString() );
                    setOffset( bodyNode.getFullBounds().getWidth() / 2 - getFullBounds().getWidth() / 2, bodyNode.getFullBounds().getHeight() * 0.44 );
                }
            } );
        }} );
        bodyNode.addChild( new PImage( PhetCommonResources.getImage( PhetCommonResources.IMAGE_CLOSE_BUTTON ) ) {{
            setOffset( bodyNode.getFullBounds().getWidth() - getFullBounds().getWidth() - 10, 10 );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mousePressed( PInputEvent event ) {
                    intensityMeter.enabled.setValue( false );
                }
            } );
        }} );

        addChild( new PhetPPath( new BasicStroke( 8, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 1f ), Color.gray ) {{
            final PropertyChangeListener listener = new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    final GeneralPath path = new GeneralPath();
                    path.moveTo( (float) sensorNode.getFullBounds().getCenterX(), (float) sensorNode.getFullBounds().getMaxY() );

                    path.curveTo( (float) sensorNode.getFullBounds().getCenterX(), (float) sensorNode.getFullBounds().getMaxY() + 60,
                                  (float) bodyNode.getFullBounds().getX() - 60, (float) bodyNode.getFullBounds().getCenterY(),
                                  (float) bodyNode.getFullBounds().getX(), (float) bodyNode.getFullBounds().getCenterY() );

                    setPathTo( path );
                }
            };
            sensorNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
            bodyNode.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, listener );
        }} );

        addChild( bodyNode );
        addChild( sensorNode );
//        addChild( sensorHotSpotDebugger );
    }

    public void doDrag( PInputEvent event ) {
        intensityMeter.translateAll( transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) ) );
    }

    public Rectangle2D getSensorGlobalFullBounds() {
        return sensorNode.getGlobalFullBounds();

    }

    public Rectangle2D getBodyGlobalFullBounds() {
        return bodyNode.getGlobalFullBounds();
    }
}
