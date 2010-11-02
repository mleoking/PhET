/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Voltmeter;
import edu.colorado.phet.capacitorlab.model.World;
import edu.colorado.phet.capacitorlab.view.meters.VoltmeterProbeNode.NegativeVoltmeterProbeNode;
import edu.colorado.phet.capacitorlab.view.meters.VoltmeterProbeNode.PositiveVoltmeterProbeNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;

/**
 * Voltmeter view.
 * This is not a node, but it manages the connections and interactivity of a set of related nodes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VoltmeterView {
    
    // wire is a cubic curve, these are the control point offsets
    private static final Point2D BODY_CONTROL_POINT_OFFSET = new Point2D.Double( -25, 0 );
    private static final Point2D PROBE_CONTROL_POINT_OFFSET = new Point2D.Double( -80, 100 );
    
    private final VoltmeterBodyNode bodyNode;
    private final VoltmeterProbeNode positiveProbeNode, negativeProbeNode;
    private final ProbeWireNode positiveWireNode, negativeWireNode;
    
    public VoltmeterView( final Voltmeter voltmeter, World world, ModelViewTransform mvt, PNode dragBoundsNode, boolean dev ) {
        bodyNode = new VoltmeterBodyNode( voltmeter, dragBoundsNode );
        positiveProbeNode = new PositiveVoltmeterProbeNode( voltmeter, world, mvt );
        negativeProbeNode = new NegativeVoltmeterProbeNode( voltmeter, world, mvt );
        positiveWireNode = new ProbeWireNode( bodyNode, positiveProbeNode, BODY_CONTROL_POINT_OFFSET, PROBE_CONTROL_POINT_OFFSET, 
                bodyNode.getPositiveConnectionOffset(), positiveProbeNode.getConnectionOffset(), CLPaints.VOLTMETER_POSITIVE_WIRE );
        negativeWireNode = new ProbeWireNode( bodyNode, negativeProbeNode, BODY_CONTROL_POINT_OFFSET, PROBE_CONTROL_POINT_OFFSET, 
                bodyNode.getNegativeConnectionOffset(), negativeProbeNode.getConnectionOffset(), CLPaints.VOLTMETER_NEGATIVE_WIRE );
        
        voltmeter.addVisibleObserver( new SimpleObserver() {
            public void update() {
                setVisible( voltmeter.isVisible() );
            }
        } );
    }
    
    private void setVisible( boolean visible ) {
        bodyNode.setVisible( visible );
        positiveProbeNode.setVisible( visible );
        negativeProbeNode.setVisible( visible );
        positiveWireNode.setVisible( visible );
        negativeWireNode.setVisible( visible );
    }
    
    public PNode getBodyNode() {
        return bodyNode;
    }
    
    public PNode getPositiveProbeNode() {
        return positiveProbeNode;
    }
    
    public PNode getNegativeProbeNode() {
        return negativeProbeNode;
    }
    
    public PNode getPositiveWireNode() {
        return positiveWireNode;
    }
    
    public PNode getNegativeWireNode() {
        return negativeWireNode;
    }
}
