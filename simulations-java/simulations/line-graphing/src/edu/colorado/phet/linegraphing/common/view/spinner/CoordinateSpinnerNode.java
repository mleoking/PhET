// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view.spinner;

import java.awt.Color;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * Spinner for one coordinate of a 2D point.
 * It prevents the point from having the same value as some other point,
 * so that we don't end up with with an undefined line because (x1,y1) == (x2,y2).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CoordinateSpinnerNode extends SpinnerNode {

    /**
     * Constructor
     * @param userComponent used in sim-sharing message
     * @param x1 the coordinate that this spinner changes (not necessarily on the x-axis!)
     * @param y1 the other coordinate of the point that has coordinate x1
     * @param x2 the coordinate in the second point that is on the same axis as x1
     * @param y2 the coordinate in the second point that is on the same axis as y1
     * @param range range of x1
     * @param colors color scheme that will be used for the spinner
     * @param font font for the number in the spinner
     * @param format format of the number in the spinner
     */
    public CoordinateSpinnerNode( IUserComponent userComponent,
                                  final Property<Double> x1,
                                  final Property<Double> y1,
                                  final Property<Double> x2,
                                  final Property<Double> y2,
                                  final Property<DoubleRange> range,
                                  SpinnerStateIndicator<Color> colors, PhetFont font, NumberFormat format ) {
        super( userComponent, x1, range, colors, font, format,
               // "up" function, skips up
               new Function0<Double>() {
                   public Double apply() {
                       double x1New = x1.get() + 1;
                       if ( x1New == x2.get().doubleValue() && y1.get().doubleValue() == y2.get().doubleValue() ) { // will points be the same?
                           x1New++;
                           if ( x1New > range.get().getMax() ) { // did we skip too far?
                               x1New = x1.get();
                           }
                       }
                       return x1New;
                   }
               },
               // "down" function, skips down
               new Function0<Double>() {
                   public Double apply() {
                       double x1New = x1.get() - 1;
                       if ( x1New == x2.get().doubleValue() && y1.get().doubleValue() == y2.get().doubleValue() ) { // will points be the same?
                           x1New--;
                           if ( x1New < range.get().getMin() ) { // did we skip too far?
                               x1New = x1.get();
                           }
                       }
                       return x1New;
                   }
               }
        );
    }
}
