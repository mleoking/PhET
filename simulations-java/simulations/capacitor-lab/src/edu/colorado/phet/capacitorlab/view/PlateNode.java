/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Plate;

/**
 * Visual pseudo-3D representation of a capacitor plate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlateNode extends BoxNode {

    private final Plate plate;
    
    public PlateNode( Plate plate, ModelViewTransform mvt ) {
        super( plate, mvt, CLPaints.PLATE_TOP, CLPaints.PLATE_FRONT, CLPaints.PLATE_SIDE );
        this.plate = plate;
    }
}
