/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view.meters;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Electric-field (E-field) detector.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EFieldDetectorNode extends PhetPNode {

    private final BatteryCapacitorCircuit circuit; 
    private final ModelViewTransform mvt;
    private final PImage probeNode;
    
    public EFieldDetectorNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, PNode dragBoundsNode ) {
        
        this.circuit = circuit;
        this.mvt = mvt;
        
        probeNode = new PImage( CLImages.EFIELD_PROBE );
        addChild( probeNode );
        
        //XXX see BarMeterNode for how to use dragBoundsNode
    }
}
