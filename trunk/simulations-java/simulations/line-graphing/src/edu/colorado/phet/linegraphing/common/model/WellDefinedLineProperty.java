// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelComponentTypes;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.linegraphing.common.LGSimSharing.ModelActions;
import edu.colorado.phet.linegraphing.common.LGSimSharing.ModelComponents;
import edu.colorado.phet.linegraphing.common.LGSimSharing.ParameterKeys;

/**
 * A property that ensures that its value will never be an undefined line.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class WellDefinedLineProperty extends Property<StraightLine> {

    public WellDefinedLineProperty( StraightLine line ) {
        super( line );
        set( line ); // in case constructor was not provided with a well-defined line
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

            // Send a model message indicating that we're adjusting the slope.
            SimSharingManager.sendModelMessage( ModelComponents.line, ModelComponentTypes.modelElement, ModelActions.adjustingSlopeToPreventUndefinedLine,
                                                            new ParameterSet().with( ParameterKeys.rise, newRise ).with( ParameterKeys.run, newRun ) );
        }
        super.set( new StraightLine( newRise, newRun, line.x1, line.y1, line.color ) );
    }
}