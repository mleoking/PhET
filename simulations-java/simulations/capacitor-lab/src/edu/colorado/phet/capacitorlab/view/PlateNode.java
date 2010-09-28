/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;

/**
 * Visual pseudo-3D representation of a capacitor plate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlateNode extends BoxNode {

    public PlateNode( ModelViewTransform mvt ) {
        super( mvt, CLPaints.PLATE );
    }
}
