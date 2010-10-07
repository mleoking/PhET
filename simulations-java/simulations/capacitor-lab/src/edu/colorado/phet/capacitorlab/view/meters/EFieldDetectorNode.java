/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view.meters;

import java.awt.Color;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.view.PlusNode;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Electric-field (E-field) detector.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EFieldDetectorNode extends PhetPNode {

    private final BatteryCapacitorCircuit circuit; 
    private final ModelViewTransform mvt;
    private final ProbeNode probeNode;
    
    public EFieldDetectorNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, PNode dragBoundsNode, boolean dev ) {
        
        this.circuit = circuit;
        this.mvt = mvt;
        
        probeNode = new ProbeNode( dev );
        addChild( probeNode );
        
        //XXX see BarMeterNode for how to use dragBoundsNode
    }
    
    private static class ProbeNode extends PComposite {
        
        public ProbeNode( boolean dev ) {
            super();
            
            PImage imageNode = new PImage( CLImages.EFIELD_PROBE );
            addChild( imageNode );
            double x = -imageNode.getFullBoundsReference().getWidth() / 2;
            double y = 0.078 * -imageNode.getFullBoundsReference().getHeight(); // multiplier is dependent on image file
            imageNode.setOffset( x, y );
            
            // Put a '+' at origin to check that probe image is offset properly.
            if ( dev ) {
                PlusNode plusNode = new PlusNode( 12, 2, Color.GRAY );
                addChild( plusNode );
            }
        }
    }
}
