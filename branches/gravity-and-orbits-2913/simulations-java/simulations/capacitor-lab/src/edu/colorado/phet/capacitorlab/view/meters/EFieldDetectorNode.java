// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.meter.EFieldDetector;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;

/**
 * The E-Field Detector has a body, 1 probe, and 1 wire connecting the probe to the body.
 * This node is designed to be located at (0,0), so that we don't need to deal with
 * coordinate frame issues when the detector's components move.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EFieldDetectorNode extends PhetPNode {

    // wire is a cubic curve, these are the control point offsets
    private static final Point2D BODY_CONTROL_POINT_OFFSET = new Point2D.Double( -25, 0 );
    private static final Point2D PROBE_CONTROL_POINT_OFFSET = new Point2D.Double( -80, 100 );

    /**
     * Constructor
     *
     * @param detector                 model of the detector
     * @param mvt                      model-view transform
     * @param vectorReferenceMagnitude used for calibrating the initial scale of the vector display
     * @param dev                      if true, enabled developer features
     * @param simplified               if true, disabled some feature of the detector
     */
    public EFieldDetectorNode( final EFieldDetector detector, CLModelViewTransform3D mvt, double vectorReferenceMagnitude, boolean dev, boolean simplified ) {
        EFieldDetectorBodyNode bodyNode = new EFieldDetectorBodyNode( detector, mvt, vectorReferenceMagnitude, simplified );
        EFieldDetectorProbeNode probeNode = new EFieldDetectorProbeNode( detector, mvt, dev );
        ProbeWireNode wireNode = new ProbeWireNode( bodyNode, probeNode, BODY_CONTROL_POINT_OFFSET, PROBE_CONTROL_POINT_OFFSET,
                                                    bodyNode.getConnectionOffset(), probeNode.getConnectionOffset(), CLPaints.EFIELD_DETECTOR_WIRE );

        // rendering order
        addChild( bodyNode );
        addChild( wireNode );
        addChild( probeNode );

        detector.visibleProperty.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( detector.visibleProperty.get() );
            }
        } );
    }
}
