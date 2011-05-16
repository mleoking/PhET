// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.Polarity;
import edu.colorado.phet.capacitorlab.model.multicaps.ICapacitor;
import edu.colorado.phet.capacitorlab.view.PlateChargeNode.AirPlateChargeNode;
import edu.colorado.phet.capacitorlab.view.PlateChargeNode.DielectricPlateChargeNode;

/**
 * Visual representation of a capacitor plate.
 * For a partially-inserted dielectric, the portion of the plate that contacts the dielectric
 * is charged differently than the portion of the plate that contacts air.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class PlateNode extends BoxNode {

    private final PlateChargeNode dielectricPlateChargeNode; // shows charge on the portion of the plate that contacts the dielectric
    private final PlateChargeNode airPlateChargeNode; // shows charge on the portion of the plate that contacts air

    public static class TopPlateNode extends PlateNode {
        public TopPlateNode( ICapacitor capacitor, CLModelViewTransform3D mvt, double maxPlateCharge ) {
            super( capacitor, mvt, Polarity.POSITIVE, maxPlateCharge );
        }
    }

    public static class BottomPlateNode extends PlateNode {
        public BottomPlateNode( ICapacitor capacitor, CLModelViewTransform3D mvt, double maxPlateCharge ) {
            super( capacitor, mvt, Polarity.NEGATIVE, maxPlateCharge );
        }
    }

    public PlateNode( ICapacitor capacitor, CLModelViewTransform3D mvt, Polarity polarity, double maxPlateCharge ) {
        super( mvt, CLPaints.PLATE, capacitor.getPlateSize() );

        this.dielectricPlateChargeNode = new DielectricPlateChargeNode( capacitor, mvt, polarity, maxPlateCharge );
        addChild( dielectricPlateChargeNode );

        this.airPlateChargeNode = new AirPlateChargeNode( capacitor, mvt, polarity, maxPlateCharge );
        addChild( airPlateChargeNode );
    }

    public void setChargeVisible( boolean visible ) {
        dielectricPlateChargeNode.setVisible( visible );
        airPlateChargeNode.setVisible( visible );
    }
}
