// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.bendinglight.modules.intro.ToolboxNode;
import edu.colorado.phet.bendinglight.modules.moretools.AmplitudeSensor;
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
import static java.awt.Color.white;

/**
 * @author Sam Reid
 */
public class AmplitudeSensorNode extends ToolboxNode.DoDragNode {
    private final ModelViewTransform transform;
    private final AmplitudeSensor amplitudeSensor;

    public AmplitudeSensorNode( final ModelViewTransform transform, final AmplitudeSensor amplitudeSensor ) {
        this.transform = transform;
        this.amplitudeSensor = amplitudeSensor;
        final Rectangle titleBounds = new Rectangle( 63, 90, 37, 14 );
        final PImage bodyNode = new PImage( RESOURCES.getImage( "wave_detector_box.png" ) ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override
                public void mouseDragged( PInputEvent event ) {
                    doDrag( event );
                }
            } );
            amplitudeSensor.bodyPosition.addObserver( new SimpleObserver() {
                public void update() {
                    final Point2D.Double viewPoint = transform.modelToView( amplitudeSensor.bodyPosition.getValue() ).toPoint2D();
                    setOffset( viewPoint.getX() - getFullBounds().getWidth() / 2, viewPoint.getY() - getFullBounds().getHeight() );
                }
            } );
            addChild( new PText( "Time" ) {{
                setFont( new PhetFont( 18 ) );
                setTextPaint( white );
                setOffset( titleBounds.getCenterX() - getFullBounds().getWidth() / 2, titleBounds.getCenterY() - getFullBounds().getHeight() / 2 );
            }} );
        }};

        class ProbeNode extends PNode {
            public ProbeNode( final AmplitudeSensor.Probe probe, String imageName ) {
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
                        setOffset( viewPoint.getX() - getFullBounds().getWidth() / 2, viewPoint.getY() - getFullBounds().getHeight() );
                    }
                } );
            }
        }
        final ProbeNode probe1Node = new ProbeNode( amplitudeSensor.probe1, "wave_detector_probe_dark.png" );
        final ProbeNode probe2Node = new ProbeNode( amplitudeSensor.probe2, "wave_detector_probe_light.png" );
        addChild( new WireNode( probe1Node, bodyNode, new Color( 92, 93, 95 ) ) );//color taken from the image
        addChild( new WireNode( probe2Node, bodyNode, new Color( 204, 206, 208 ) ) );
        addChild( bodyNode );
        addChild( probe1Node );
        addChild( probe2Node );
    }

    @Override
    public void doDrag( PInputEvent event ) {
        amplitudeSensor.translateBody( transform.viewToModelDelta( event.getDeltaRelativeTo( getParent() ) ) );
    }
}
