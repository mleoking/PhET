/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.Dielectric;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;

/**
 * Visual pseudo-3D representation of a capacitor dielectric.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricNode extends BoxNode {
    
    private final Dielectric dielectric;
    
    public DielectricNode( Dielectric dielectric, ModelViewTransform mvt ) {
        super( dielectric, mvt, CLPaints.DIELECTRIC_TOP, CLPaints.DIELECTRIC_FRONT, CLPaints.DIELECTRIC_SIDE );
        this.dielectric = dielectric;
    }
}
