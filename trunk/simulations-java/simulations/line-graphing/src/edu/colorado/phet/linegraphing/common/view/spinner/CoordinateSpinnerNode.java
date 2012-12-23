// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view.spinner;

import java.awt.Color;
import java.text.NumberFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.linegraphing.common.view.spinner.SpinnerNode;
import edu.colorado.phet.linegraphing.common.view.spinner.SpinnerStateIndicator;

/**
 * Spinner for one coordinate of a point.
 * It prevents the point from having the same value as some other point,
 * so that we don't end up with with an undefined line because (x1,y1) == (x2,y2).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class CoordinateSpinnerNode extends SpinnerNode {

    //TODO doc
    public CoordinateSpinnerNode( IUserComponent userComponent,
                                  final Property<Double> p1,
                                  final Property<Double> p2,
                                  final Property<Double> q1,
                                  final Property<Double> q2,
                                  final Property<DoubleRange> range,
                                  SpinnerStateIndicator<Color> colors, PhetFont font, NumberFormat format ) {
        super( userComponent, p1, range, colors, font, format,
               // "up" function, skips up
               new Function0<Double>() {
                   public Double apply() {
                       double x1New = p1.get() + 1;
                       if ( x1New == p2.get().doubleValue() && q1.get().doubleValue() == q2.get().doubleValue() ) { // will points be the same?
                           x1New++;
                           if ( x1New > range.get().getMax() ) { // did we skip too far?
                               x1New = p1.get();
                           }
                       }
                       return x1New;
                   }
               },
               // "down" function, skips down
               new Function0<Double>() {
                   public Double apply() {
                       double x1New = p1.get() - 1;
                       if ( x1New == p2.get().doubleValue() && q1.get().doubleValue() == q2.get().doubleValue() ) { // will points be the same?
                           x1New--;
                           if ( x1New < range.get().getMin() ) { // did we skip too far?
                               x1New = p1.get();
                           }
                       }
                       return x1New;
                   }
               }
        );
    }
}
