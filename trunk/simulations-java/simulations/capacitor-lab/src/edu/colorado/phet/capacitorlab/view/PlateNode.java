/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;

/**
 * Visual representation of a capacitor plate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class PlateNode extends BoxNode {
    
    public static enum Polarity { POSITIVE, NEGATIVE };
    
    private final PlateChargeNode plateChargeNode;
    
    public static class TopPlateNode extends PlateNode {
        public TopPlateNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev ) {
            super( circuit, mvt, dev, Polarity.POSITIVE );
        }
    }
    
    public static class BottomPlateNode extends PlateNode {
        public BottomPlateNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev ) {
            super( circuit, mvt, dev, Polarity.NEGATIVE );
        }
    }
    
    public PlateNode( BatteryCapacitorCircuit circuit, ModelViewTransform mvt, boolean dev, Polarity polarity ) {
        super( mvt, CLPaints.PLATE );
        this.plateChargeNode = new PlateChargeNode( circuit, mvt, dev, polarity );
        addChild( plateChargeNode );
    }
    
    public void setChargeVisible( boolean visible ) {
        plateChargeNode.setVisible( visible );
    }
    
    public boolean isChargeVisible() {
        return plateChargeNode.getVisible();
    }
}
