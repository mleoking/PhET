// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view.meters;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.CapacitanceMeter;

/**
 * Meter that displays capacitance.
 * Origin is at the upper-left corner of the "track" that the bar moves in.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CapacitanceMeterNode extends BarMeterNode {

    private static final String VALUE_MANTISSA_PATTERN = "0.00";
    private static final int VALUE_EXPONENT = CLConstants.CAPACITANCE_METER_VALUE_EXPONENT;
    private static final String UNITS = CLStrings.FARADS;

    public CapacitanceMeterNode( CapacitanceMeter meter, final CLModelViewTransform3D mvt ) {
        super( meter, mvt, CLPaints.CAPACITANCE, CLStrings.CAPACITANCE, VALUE_MANTISSA_PATTERN, VALUE_EXPONENT, UNITS );
    }
}
