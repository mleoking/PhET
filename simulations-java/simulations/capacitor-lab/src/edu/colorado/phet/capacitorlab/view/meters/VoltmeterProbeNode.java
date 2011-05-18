// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.*;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.drag.WorldLocationDragHandler;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.WorldLocationProperty;
import edu.colorado.phet.capacitorlab.model.meter.Voltmeter;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Base class for voltmeter probes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ abstract class VoltmeterProbeNode extends PhetPNode {

    // Positive voltmeter probe.
    public static class PositiveVoltmeterProbeNode extends VoltmeterProbeNode {
        public PositiveVoltmeterProbeNode( final Voltmeter voltmeter, final CLModelViewTransform3D mvt ) {
            super( CLImages.RED_VOLTMETER_PROBE, voltmeter.positiveProbeLocationProperty, mvt );
        }
    }

    // Negative voltmeter probe.
    public static class NegativeVoltmeterProbeNode extends VoltmeterProbeNode {
        public NegativeVoltmeterProbeNode( final Voltmeter voltmeter, final CLModelViewTransform3D mvt ) {
            super( CLImages.BLACK_VOLTMETER_PROBE, voltmeter.negativeProbeLocationProperty, mvt );
        }
    }

    private final Point2D connectionOffset; // offset for connection point of wire that attaches probe to body

    public VoltmeterProbeNode( Image image, final WorldLocationProperty locationProperty, final CLModelViewTransform3D mvt ) {

        PImage imageNode = new PImage( image );
        addChild( imageNode );
        double x = -imageNode.getFullBoundsReference().getWidth() / 2;
        double y = 0;
        imageNode.setOffset( x, y );

        connectionOffset = new Point2D.Double( 0, imageNode.getFullBoundsReference().getHeight() ); // connect wire to bottom center

        // image is vertical, rotate into pseudo-3D perspective after computing the connection offset
        rotate( -mvt.getYaw() );

        // make draggable
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new WorldLocationDragHandler( locationProperty, this, mvt ) );

        // move the node when the model changes
        locationProperty.addObserver( new SimpleObserver() {
            public void update() {
                setOffset( mvt.modelToView( locationProperty.get() ) );
            }
        } );
    }

    // Gets the point, relative to the probe, where the wire connects to the probe.
    public Point2D getConnectionOffset() {
        return new Point2D.Double( connectionOffset.getX(), connectionOffset.getY() );
    }
}
