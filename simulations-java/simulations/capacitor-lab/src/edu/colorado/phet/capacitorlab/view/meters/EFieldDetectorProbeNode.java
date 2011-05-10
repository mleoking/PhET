// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.drag.WorldLocationDragHandler;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.EFieldDetector;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Probe for the E-Field Detector.
 * Origin is in the center of the probe's crosshairs, and the location of the crosshairs
 * is dependent on the image file.  The only way to align the crosshairs and the origin
 * is via visual inspection. Running with -dev will add an additional node that allows
 * you to visually check alignment.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
/* package private */ class EFieldDetectorProbeNode extends PhetPNode {

    private final Point2D connectionOffset; // offset for connection point of wire that attaches probe to body

    public EFieldDetectorProbeNode( final EFieldDetector detector, final CLModelViewTransform3D mvt, boolean dev ) {
        super();

        PImage imageNode = new PImage( CLImages.EFIELD_PROBE );
        addChild( imageNode );
        double x = -imageNode.getFullBoundsReference().getWidth() / 2;
        double y = -14.5; // determined by visual inspection, dependent on where crosshairs appear in image file
        imageNode.setOffset( x, y );

        connectionOffset = new Point2D.Double( 0, imageNode.getFullBoundsReference().getHeight() + y ); // connect wire to bottom center

        // image is vertical, rotate into pseudo-3D perspective after computing the connection offset
        rotate( -mvt.getYaw() );

        // Put a dot at origin to check that probe image is offset properly.
        if ( dev ) {
            double diameter = 4;
            PPath originNode = new PPath( new Ellipse2D.Double( -diameter / 2, -diameter / 2, diameter, diameter ) );
            originNode.setStroke( null );
            originNode.setPaint( Color.RED );
            addChild( originNode );
        }

        // make draggable
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new WorldLocationDragHandler( detector.probeLocationProperty, this, mvt ) );

        // move the node when the model changes
        detector.probeLocationProperty.addObserver( new SimpleObserver() {
            public void update() {
                setOffset( mvt.modelToView( detector.probeLocationProperty.get() ) );
            }
        } );
    }

    // Gets the point, relative to the probe, where the wire connects to the probe.
    public Point2D getConnectionOffset() {
        return new Point2D.Double( connectionOffset.getX(), connectionOffset.getY() );
    }
}


