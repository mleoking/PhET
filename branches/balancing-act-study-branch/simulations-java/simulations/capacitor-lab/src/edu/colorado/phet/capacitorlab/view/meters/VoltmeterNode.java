// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.geom.Point2D;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.meter.Voltmeter;
import edu.colorado.phet.capacitorlab.view.meters.VoltmeterProbeNode.NegativeVoltmeterProbeNode;
import edu.colorado.phet.capacitorlab.view.meters.VoltmeterProbeNode.PositiveVoltmeterProbeNode;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;

/**
 * A voltmeter has a body, 2 probes, and 2 wires connecting the probes to the body.
 * This node is designed to be located at (0,0), so that we don't need to deal with
 * coordinate frame issues when the voltmeter's components move.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VoltmeterNode extends PhetPNode {

    // wire is a cubic curve, these are the control point offsets
    private static final Point2D BODY_CONTROL_POINT_OFFSET = new Point2D.Double( -25, 0 );
    private static final Point2D PROBE_CONTROL_POINT_OFFSET = new Point2D.Double( -80, 100 );

    /**
     * Constructor
     *
     * @param voltmeter the voltmeter model
     * @param mvt       model-view transform
     */
    public VoltmeterNode( final Voltmeter voltmeter, CLModelViewTransform3D mvt ) {

        VoltmeterBodyNode bodyNode = new VoltmeterBodyNode( voltmeter, mvt );
        PositiveVoltmeterProbeNode positiveProbeNode = new PositiveVoltmeterProbeNode( voltmeter, mvt );
        NegativeVoltmeterProbeNode negativeProbeNode = new NegativeVoltmeterProbeNode( voltmeter, mvt );
        ProbeWireNode positiveWireNode = new ProbeWireNode( bodyNode, positiveProbeNode, BODY_CONTROL_POINT_OFFSET, PROBE_CONTROL_POINT_OFFSET,
                                                            bodyNode.getPositiveConnectionOffset(), positiveProbeNode.getConnectionOffset(), CLPaints.VOLTMETER_POSITIVE_WIRE );
        ProbeWireNode negativeWireNode = new ProbeWireNode( bodyNode, negativeProbeNode, BODY_CONTROL_POINT_OFFSET, PROBE_CONTROL_POINT_OFFSET,
                                                            bodyNode.getNegativeConnectionOffset(), negativeProbeNode.getConnectionOffset(), CLPaints.VOLTMETER_NEGATIVE_WIRE );

        // rendering order
        addChild( bodyNode );
        addChild( positiveProbeNode );
        addChild( positiveWireNode );
        addChild( negativeProbeNode );
        addChild( negativeWireNode );

        voltmeter.visibleProperty.addObserver( new SimpleObserver() {
            public void update() {
                setVisible( voltmeter.isVisible() );
            }
        } );
    }
}
