// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.intro.model;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Model of a line, using slope-intercept form, y=mx+b.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SlopeInterceptLine {

    public final Property<Integer> rise;
    public final Property<Integer> run;
    public final Property<Integer> intercept;
    public final CompositeProperty<Double> slope; // derived

    public SlopeInterceptLine( int rise, int run, int intercept ) {
        this.rise = new Property<Integer>( rise );
        this.run = new Property<Integer>( run );
        this.intercept = new Property<Integer>( intercept );
        this.slope = new CompositeProperty<Double>( new Function0<Double>() {
            public Double apply() {
                return ( (double) SlopeInterceptLine.this.rise.get() ) / SlopeInterceptLine.this.run.get();
            }
        }, this.rise, this.run );
    }

    // y=mx+b
    public double solve( double x ) {
        return solve( slope.get(), x, intercept.get() );
    }

    // y=mx+b
    public static double solve( double m, double x, double b ) {
        return ( m * x ) + b;
    }
}
