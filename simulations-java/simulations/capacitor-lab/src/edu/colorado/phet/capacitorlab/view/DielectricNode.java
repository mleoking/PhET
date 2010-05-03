/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.CLPaints;

/**
 * Visual pseudo-3D representation of a capacitor dielectric.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricNode extends BoxNode {
    
    public DielectricNode() {
        super( CLPaints.DIELECTRIC_TOP, CLPaints.DIELECTRIC_FRONT, CLPaints.DIELECTRIC_SIDE );
    }
}
