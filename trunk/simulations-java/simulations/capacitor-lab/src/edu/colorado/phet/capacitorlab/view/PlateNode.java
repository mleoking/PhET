/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.BatteryCapacitorCircuit;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.view.PlateChargeNode.AirPlateChargeNode;
import edu.colorado.phet.capacitorlab.view.PlateChargeNode.DielectricPlateChargeNode;

/**
 * Visual representation of a capacitor plate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class PlateNode extends BoxNode {
    
    public static enum Polarity { POSITIVE, NEGATIVE };
    
    private final PlateChargeNode dielectricPlateChargeNode; // shows charge on the portion of the plate that contacts the dielectric
    private final PlateChargeNode airPlateChargeNode; // shows charge on the portion of the plate that contacts air
    
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
        
        this.dielectricPlateChargeNode = new DielectricPlateChargeNode( circuit, mvt, dev, polarity );
        addChild( dielectricPlateChargeNode );
        
        this.airPlateChargeNode = new AirPlateChargeNode( circuit, mvt, dev, polarity );
        addChild( airPlateChargeNode );
    }
    
    public void setChargeVisible( boolean visible ) {
        dielectricPlateChargeNode.setVisible( visible );
        airPlateChargeNode.setVisible( visible );
    }
    
    public boolean isChargeVisible() {
        return dielectricPlateChargeNode.getVisible();
    }
}
