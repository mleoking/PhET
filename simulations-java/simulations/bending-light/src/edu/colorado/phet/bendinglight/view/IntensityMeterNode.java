// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.bendinglight.model.IntensityMeter;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.ToolNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.bendinglight.BendingLightApplication.RESOURCES;
import static edu.colorado.phet.bendinglight.BendingLightStrings.INTENSITY;
import static java.awt.Color.gray;

/**
 * Piccolo node for the intensity meter, including its movable sensor and readout region (called the body).
 *
 * @author Sam Reid
 */
public class IntensityMeterNode extends ToolNode {
    private final ModelViewTransform transform;
    private final IntensityMeter intensityMeter;
    public PImage bodyNode;
    public PImage sensorNode;
    private boolean debug = false;

    public IntensityMeterNode( final ModelViewTransform transform, final IntensityMeter intensityMeter ) {
        this.transform = transform;
        this.intensityMeter = intensityMeter;
        intensityMeter.enabled.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( intensityMeter.enabled.getValue() );
            }
        } );

        //Create the probe that can intercept the light
        sensorNode = new PImage( RESOURCES.getImage( "intensity_meter_probe.png" ) ) {{
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

        //Create the body that reads out the intercepted light
        bodyNode = new PImage( RESOURCES.getImage( "intensity_meter_box.png" ) ) {{
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

        //Add a "Intensity" title to the body node
        bodyNode.addChild( new PText( INTENSITY ) {{
            setFont( new PhetFont( 22 ) );
            setTextPaint( Color.white );
            setOffset( bodyNode.getFullBounds().getWidth() / 2 - getFullBounds().getWidth() / 2, bodyNode.getFullBounds().getHeight() * 0.1 );
        }} );

        //Add the reading to the body node
        bodyNode.addChild( new PText( "-" ) {{//dummy string for layout
            setFont( new PhetFont( 30 ) );
            intensityMeter.reading.addObserver( new SimpleObserver() {
                public void update() {
                    setTransform( new AffineTransform() );
                    setText( intensityMeter.reading.getValue().getString() );
                    setOffset( bodyNode.getFullBounds().getWidth() / 2 - getFullBounds().getWidth() / 2, bodyNode.getFullBounds().getHeight() * 0.44 );
                }
            } );
        }} );

        //Add the components
        addChild( new WireNode( sensorNode, bodyNode, gray ) );//Connect the sensor to the body with a gray wire
        addChild( bodyNode );
        addChild( sensorNode );

        if ( debug ) {
            addChild( new PhetPPath( new BasicStroke( 1 ), Color.green ) {{
                intensityMeter.sensorPosition.addObserver( new SimpleObserver() {
                    public void update() {
                        setPathTo( transform.modelToView( intensityMeter.getSensorShape() ) );
                    }
                } );
                setPickable( false );
                setChildrenPickable( false );
            }} );
        }
    }

    //Drag all components, called when dragging from toolbox
    public void dragAll( PDimension delta ) {
        doTranslate( transform.viewToModelDelta( delta ) );
    }

    //Get the components that could be dropped back in the toolbox
    @Override public PNode[] getDroppableComponents() {
        return new PNode[] { bodyNode, sensorNode };
    }

    public void doTranslate( Dimension2D delta ) {
        intensityMeter.translateAll( delta );
    }
}
