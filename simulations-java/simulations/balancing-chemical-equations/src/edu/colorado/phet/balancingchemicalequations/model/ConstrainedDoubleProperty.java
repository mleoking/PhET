// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;

/**
 * Double property that is constrained to a range.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConstrainedDoubleProperty extends ConstrainedProperty<Double> {

    private final double min, max;

    public ConstrainedDoubleProperty( DoubleRange range ) {
        this( range.getMin(), range.getMax(), range.getDefault() );
    }

    public ConstrainedDoubleProperty( double min, double max, double value ) {
        super( value );
        this.min = min;
        this.max = max;
    }

    @Override
    protected boolean isValid( Double value ) {
        return ( value >= min && value <= max );
    }
}
