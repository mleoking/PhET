// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;


/**
 * Visual representation of a negative charge.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NegativeChargeNode extends MinusNode {

    public NegativeChargeNode() {
        super( CLConstants.NEGATIVE_CHARGE_SIZE.width, CLConstants.NEGATIVE_CHARGE_SIZE.height, CLPaints.NEGATIVE_CHARGE );
    }
}
