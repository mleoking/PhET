// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view.meters;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.StoredEnergyMeter;

/**
 * Meter that displays stored energy.
 * Origin is at the upper-left corner of the "track" that the bar moves in.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StoredEnergyMeterNode extends BarMeterNode {

    private static final String VALUE_MANTISSA_PATTERN = "0.00";
    private static final int VALUE_EXPONENT = CLConstants.STORED_ENERGY_METER_VALUE_EXPONENT;
    private static final String UNITS = CLStrings.JOULES;

    public StoredEnergyMeterNode( StoredEnergyMeter meter, final CLModelViewTransform3D mvt ) {
        super( meter, mvt, CLPaints.STORED_ENERGY, CLStrings.STORED_ENERGY, VALUE_MANTISSA_PATTERN, VALUE_EXPONENT, UNITS );
    }
}
