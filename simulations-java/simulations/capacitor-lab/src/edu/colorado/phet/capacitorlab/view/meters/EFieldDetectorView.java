/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.model.EFieldDetector;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.World;
import edu.umd.cs.piccolo.PNode;

/**
 * Electric-field (E-field) detector.
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
    
    public EFieldDetectorView( EFieldDetector detector, World world, ModelViewTransform mvt, PNode dragBoundsNode, boolean dev ) {
        bodyNode = new EFieldDetectorBodyNode( detector, dragBoundsNode );
        probeNode = new EFieldDetectorProbeNode( detector, world, mvt, dev );
        wireNode = new ProbeWireNode( bodyNode, probeNode, BODY_CONTROL_POINT_OFFSET, PROBE_CONTROL_POINT_OFFSET, bodyNode.getConnectionOffset(), probeNode.getConnectionOffset() );
    }
    
    public void setVisible( boolean visible ) {
        bodyNode.setVisible( visible );
        probeNode.setVisible( visible );
        wireNode.setVisible( visible );
    }
    
    public boolean isVisible() {
        return bodyNode.isVisible();
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
    
    public void setShowVectorsPanelVisible( boolean visible ) {
        bodyNode.setShowVectorsPanelVisible( visible );
    }
}
