// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.bendinglight.view.ChartNode;
import edu.colorado.phet.bendinglight.view.WireNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ToolNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.bendinglight.BendingLightApplication.RESOURCES;
import static java.awt.Color.white;

/**
 * @author Sam Reid
 */
public class WaveSensorNode extends ToolNode {
    final Color darkProbeColor = new Color( 88, 89, 91 );//color taken from the image
    final Color lightProbeColor = new Color( 147, 149, 152 );

    private final ModelViewTransform transform;
    private final WaveSensor waveSensor;

    public WaveSensorNode( final ModelViewTransform transform, final WaveSensor waveSensor ) {
        this.transform = transform;
        this.waveSensor = waveSensor;
        final Rectangle titleBounds = new Rectangle( 63, 90, 37, 14 );
        final Rectangle chartArea = new Rectangle( 15, 15, 131, 68 );
        final PImage bodyNode = new PImage( RESOURCES.getImage( "wave_detector_box.png" ) ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override
                public void mouseDragged( PInputEvent event ) {
                    waveSensor.translateBody( transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) ) );
                }
            } );
            waveSensor.bodyPosition.addObserver( new SimpleObserver() {
                public void update() {
                    final Point2D.Double viewPoint = transform.modelToView( waveSensor.bodyPosition.getValue() ).toPoint2D();
                    setOffset( viewPoint.getX() - getFullBounds().getWidth() / 2, viewPoint.getY() - getFullBounds().getHeight() );
                }
            } );
            addChild( new PText( "Time" ) {{
                setFont( new PhetFont( 18 ) );
                setTextPaint( white );
                setOffset( titleBounds.getCenterX() - getFullBounds().getWidth() / 2, titleBounds.getCenterY() - getFullBounds().getHeight() / 2 );
            }} );
            addChild( new ChartNode( waveSensor.clock, chartArea, new ArrayList<ChartNode.Series>() {{
                add( new ChartNode.Series( waveSensor.probe1.series, darkProbeColor ) );
                add( new ChartNode.Series( waveSensor.probe2.series, lightProbeColor ) );
            }} ) );
        }};

        class ProbeNode extends PNode {
            public ProbeNode( final WaveSensor.Probe probe, String imageName ) {
                addChild( new PImage( RESOURCES.getImage( imageName ) ) );
                addInputEventListener( new CursorHandler() );
                addInputEventListener( new PBasicInputEventHandler() {
                    @Override
                    public void mouseDragged( PInputEvent event ) {
                        probe.translate( transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) ) );
                    }
                } );
                probe.position.addObserver( new SimpleObserver() {
                    public void update() {
                        final Point2D.Double viewPoint = transform.modelToView( probe.position.getValue() ).toPoint2D();
                        setOffset( viewPoint.getX() - getFullBounds().getWidth() / 2, viewPoint.getY() - getFullBounds().getHeight() / 2 );
                    }
                } );
            }
        }
        final ProbeNode probe1Node = new ProbeNode( waveSensor.probe1, "wave_detector_probe_dark.png" );
        final ProbeNode probe2Node = new ProbeNode( waveSensor.probe2, "wave_detector_probe_light.png" );
        addChild( new WireNode( probe1Node, bodyNode, darkProbeColor ) );
        addChild( new WireNode( probe2Node, bodyNode, lightProbeColor ) );
        addChild( bodyNode );
        addChild( probe1Node );
        addChild( probe2Node );
    }

    @Override public void dragAll( PDimension delta ) {
        waveSensor.translateAll( new ImmutableVector2D( transform.viewToModelDelta( delta ) ) );
    }
}