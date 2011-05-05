// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.property4.types;

import edu.colorado.phet.common.phetcommon.property4.*;

/**
 * Computes the sum of a variable number of Double properties.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Sum extends CompositeProperty<Double> {

    public Sum( GettableProperty<Double>... args ) {
        super( new CompositeFunction<Double>( args ) {
            @Override public Double apply() {
                double sum = 0;
                for ( GettableProperty<Double> arg : getArgs() ) {
                    sum += arg.getValue();
                }
                return sum;
            }
        } );
    }

    public static void main( String[] args ) {
        SettableProperty<Double> term1 = new SettableProperty<Double>( 5d );
        SettableProperty<Double> term2 = new SettableProperty<Double>( 10d );
        SettableProperty<Double> term3 = new SettableProperty<Double>( 20d );
        Sum sum = new Sum( term1, term2, term3 ) {{
            addListener( new PropertyChangeListener() {
                public void propertyChanged( PropertyChangeEvent event ) {
                    System.out.println( "sum=" + event.toString() );
                }
            } );
        }};
        assert ( sum.getValue() == term1.getValue() + term2.getValue() + term3.getValue() );
        term1.setValue( 10d );
        assert ( sum.getValue() == term1.getValue() + term2.getValue() + term3.getValue() );
        term2.setValue( 5d );
        assert ( sum.getValue() == term1.getValue() + term2.getValue() + term3.getValue() );
        term3.setValue( 5d );
        assert ( sum.getValue() == term1.getValue() + term2.getValue() + term3.getValue() );
    }
}
