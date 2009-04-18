/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.naturalselection.module.example;

import edu.colorado.phet.naturalselection.defaults.ExampleDefaults;
import edu.colorado.phet.naturalselection.model.ExampleModelElement;
import edu.colorado.phet.naturalselection.model.NaturalSelectionClock;

/**
 * ExampleModel is the model for ExampleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleModel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private final NaturalSelectionClock _clock;
    private final ExampleModelElement _exampleModelElement;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public ExampleModel( NaturalSelectionClock clock ) {
        super();

        _clock = clock;

        _exampleModelElement = new ExampleModelElement(
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_WIDTH,
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_HEIGHT,
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_POSITION,
                ExampleDefaults.EXAMPLE_MODEL_ELEMENT_ORIENTATION );
        _clock.addClockListener( _exampleModelElement );
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------

    public NaturalSelectionClock getClock() {
        return _clock;
    }

    public ExampleModelElement getExampleModelElement() {
        return _exampleModelElement;
    }
}
