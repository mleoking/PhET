// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.moretools;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.bendinglight.view.BoundedDragHandler;
import edu.colorado.phet.bendinglight.view.WireNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ToolNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.bendinglight.BendingLightApplication.RESOURCES;
import static edu.colorado.phet.bendinglight.BendingLightStrings.TIME;
import static java.awt.Color.white;

/**
 * PNode for the wave sensor, which shows 2 sensor probes and a chart area (the body)
 *
 * @author Sam Reid
 */
public class WaveSensorNode extends ToolNode {
    final Color darkProbeColor = new Color( 88, 89, 91 );//color taken from the image
    final Color lightProbeColor = new Color( 147, 149, 152 );

    private final ModelViewTransform transform;
    private final WaveSensor waveSensor;
    public final PImage bodyNode;
    public final PNode probe1Node;
    public final PNode probe2Node;

    public WaveSensorNode( final ModelViewTransform transform, final WaveSensor waveSensor ) {
        this.transform = transform;
        this.waveSensor = waveSensor;

        //Bounds are based on the provided images, and will need to be updated if the image changes
        final Rectangle titleBounds = new Rectangle( 63, 90, 37, 14 );
        final Rectangle chartArea = new Rectangle( 15, 15, 131, 68 );

        //Create the body where the chart is shown
        bodyNode = new PImage( RESOURCES.getImage( "wave_detector_box.png" ) ) {{
            //Add the "time" axis label at the bottom center of the chart
            addChild( new PText( TIME ) {{
                setFont( new PhetFont( 18 ) );
                setTextPaint( white );
                setOffset( titleBounds.getCenterX() - getFullBounds().getWidth() / 2, titleBounds.getCenterY() - getFullBounds().getHeight() / 2 );
            }} );

            //Add the chart inside the body, with one series for each of the dark and light probes
            addChild( new ChartNode( waveSensor.clock, chartArea, new ArrayList<Series>() {{
                add( new Series( waveSensor.probe1.series, darkProbeColor ) );
                add( new Series( waveSensor.probe2.series, lightProbeColor ) );
            }} ) );

            //Synchronize the body position with the model (centered on the model point)
            waveSensor.bodyPosition.addObserver( new SimpleObserver() {
                public void update() {
                    final Point2D.Double viewPoint = transform.modelToView( waveSensor.bodyPosition.getValue() ).toPoint2D();
                    setOffset( viewPoint.getX() - getFullBounds().getWidth() / 2, viewPoint.getY() - getFullBounds().getHeight() );
                }
            } );

            //Add interaction, the body is draggable, but keep it constrained to stay in the play area
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new BoundedDragHandler( WaveSensorNode.this ) {
                @Override protected void dragNode( PDimension delta ) {
                    waveSensor.translateBody( transform.viewToModelDelta( delta ) );
                }
            } );
        }};

        //Create the probes
        probe1Node = new ProbeNode( waveSensor.probe1, "wave_detector_probe_dark.png" );
        probe2Node = new ProbeNode( waveSensor.probe2, "wave_detector_probe_light.png" );

        //Rendering order, including wires
        addChild( new WireNode( probe1Node, bodyNode, darkProbeColor ) );
        addChild( new WireNode( probe2Node, bodyNode, lightProbeColor ) );
        addChild( bodyNode );
        addChild( probe1Node );
        addChild( probe2Node );
    }

    //Called when dragged out of the toolbox, drags all parts together (including body and probes)
    @Override public void dragAll( PDimension delta ) {
        waveSensor.translateAll( new ImmutableVector2D( transform.viewToModelDelta( delta ) ) );
    }

    //When any probe or body is dropped in the toolbox, the whole WaveSensor goes back in the toolbox
    @Override public PNode[] getDroppableComponents() {
        return new PNode[] { bodyNode, probe1Node, probe2Node };
    }

    //Class for rendering a probe that can be used to sense wave values
    class ProbeNode extends PNode {
        public ProbeNode( final WaveSensor.Probe probe, String imageName ) {
            //Draw the probe
            addChild( new PImage( RESOURCES.getImage( imageName ) ) );

            //Interaction: translates when dragged, but keep it bounded within the play area
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new BoundedDragHandler( this ) {
                @Override protected void dragNode( PDimension delta ) {
                    probe.translate( transform.viewToModelDelta( delta ) );
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
}