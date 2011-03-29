// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view.meters;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.model.PlateChargeMeter;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * Meter that displays charge on the capacitor plates.
 * Origin is at the upper-left corner of the "track" that the bar moves in.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlateChargeMeterNode extends BarMeterNode {

    private static final String VALUE_MANTISSA_PATTERN = "0.00";
    private static final int VALUE_EXPONENT = CLConstants.PLATE_CHARGE_METER_VALUE_EXPONENT;
    private static final String UNITS = CLStrings.COULOMBS;

    public PlateChargeMeterNode( final PlateChargeMeter meter, final CLModelViewTransform3D mvt ) {
        super( meter, mvt, CLPaints.POSITIVE_CHARGE, CLStrings.PLATE_CHARGE_TOP, VALUE_MANTISSA_PATTERN, VALUE_EXPONENT, UNITS );
        meter.addValueObserver( new SimpleObserver() {
            public void update() {
                setBarColor( ( meter.getValue() >= 0 ) ? CLPaints.POSITIVE_CHARGE : CLPaints.NEGATIVE_CHARGE );
            }
        } );
    }

    @Override
    protected void setValue( double value ) {
        super.setValue( Math.abs( value ) );
    }
}
