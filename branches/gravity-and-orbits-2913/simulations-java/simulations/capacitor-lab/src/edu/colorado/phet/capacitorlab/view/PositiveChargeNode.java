// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;

/**
 * Visual representation of a positive charge.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PositiveChargeNode extends PlusNode {

    public PositiveChargeNode() {
        super( CLConstants.NEGATIVE_CHARGE_SIZE.width, CLConstants.NEGATIVE_CHARGE_SIZE.height, CLPaints.POSITIVE_CHARGE );
    }
}
