// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.meter.EFieldDetector;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;

/**
 * Electric-field (E-field) detector.
 * A voltmeter has a body, 1 probe, and 1 wire connecting the probe to the body.
 * This is not a node, but it manages the connections and interactivity of a set of related nodes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EFieldDetectorView {

    // wire is a cubic curve, these are the control point offsets
    private static final Point2D BODY_CONTROL_POINT_OFFSET = new Point2D.Double( -25, 0 );
    private static final Point2D PROBE_CONTROL_POINT_OFFSET = new Point2D.Double( -80, 100 );

    private final EFieldDetectorBodyNode bodyNode;
    private final EFieldDetectorProbeNode probeNode;
    private final ProbeWireNode wireNode;

    /**
     * Constructor
     *
     * @param detector                 model of the detector
     * @param mvt                      model-view transform
     * @param vectorReferenceMagnitude used for calibrating the initial scale of the vector display
     * @param dev                      if true, enabled developer features
     * @param simplified               if true, disabled some feature of the detector
     */
    public EFieldDetectorView( final EFieldDetector detector, CLModelViewTransform3D mvt, double vectorReferenceMagnitude, boolean dev, boolean simplified ) {
        bodyNode = new EFieldDetectorBodyNode( detector, mvt, vectorReferenceMagnitude, simplified );
        probeNode = new EFieldDetectorProbeNode( detector, mvt, dev );
        wireNode = new ProbeWireNode( bodyNode, probeNode, BODY_CONTROL_POINT_OFFSET, PROBE_CONTROL_POINT_OFFSET,
                                      bodyNode.getConnectionOffset(), probeNode.getConnectionOffset(), CLPaints.EFIELD_DETECTOR_WIRE );

        detector.visibleProperty.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( detector.visibleProperty.get() );
            }
        } );
    }

    private void setVisible( boolean visible ) {
        bodyNode.setVisible( visible );
        probeNode.setVisible( visible );
        wireNode.setVisible( visible );
    }

    public PNode getBodyNode() {
        return bodyNode;
    }

    public PNode getProbeNode() {
        return probeNode;
    }

    public PNode getWireNode() {
        return wireNode;
    }
}
