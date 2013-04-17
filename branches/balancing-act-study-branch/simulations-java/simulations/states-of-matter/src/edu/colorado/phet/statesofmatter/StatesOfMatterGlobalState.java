// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.statesofmatter;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.statesofmatter.view.TemperatureUnits;

/**
 * Global state information that applies to all modules in the simulation.
 *
 * @author John Blanco
 */
public class StatesOfMatterGlobalState {
    public static final BooleanProperty whiteBackground = new BooleanProperty( false );
    public static final Property<TemperatureUnits> temperatureUnitsProperty = new Property<TemperatureUnits>( TemperatureUnits.KELVIN );

}
