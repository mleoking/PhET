// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.pressure.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * Function for interpolating values, used for creating color gradients in WaterColor
 *
 * @author Sam Reid
 */
public class PiecewiseFunction {
    private List<Piece> pieces = new ArrayList<Piece>();

    static class Piece {
        final Function1<Double, Boolean> condition;
        final Function1<Double, Double> function;

        Piece( Function1<Double, Boolean> condition, Function1<Double, Double> function ) {
            this.condition = condition;
            this.function = function;
        }
    }

    public PiecewiseFunction( Piece[] pieces ) {
        this.pieces = Arrays.asList( pieces );
    }

    public int evaluate( double x ) {
        for ( Piece entry : pieces ) {
            if ( entry.condition.apply( x ) ) {
                return (int) ( entry.function.apply( x ).doubleValue() );
            }
        }
        throw new RuntimeException( "value out of domain: " + x );
    }
}
