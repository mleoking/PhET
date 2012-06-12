// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * A property that ensures that its value will never be an undefined line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WellDefinedLineProperty extends Property<StraightLine> {

    public WellDefinedLineProperty( StraightLine line ) {
        super( line );
    }

    @Override public void set( StraightLine line ) {
        double newRise = line.rise;
        double newRun = line.run;
        // Skip over values that would result in slope=0/0
        if ( line.rise == 0 && line.run == 0 ) {
            if ( get().run != 0 ) {
                // run changed, skip over run = 0
                newRun = ( get().run > 0 ) ? -1 : 1;
            }
            else if ( get().rise != 0 ) {
                // rise changed, skip over rise = 0
                newRise = ( get().rise > 0 ) ? -1 : 1;
            }
            else {
                // rise and run haven't changed, arbitrarily move rise toward origin
                newRise = ( get().y1 > 0 ) ? -1 : 1;
            }
        }
        super.set( new StraightLine( newRise, newRun, line.x1, line.y1, line.color, line.highlightColor ) );
    }
}