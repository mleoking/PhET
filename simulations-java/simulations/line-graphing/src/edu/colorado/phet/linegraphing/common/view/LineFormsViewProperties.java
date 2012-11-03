// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.common.view;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * Properties that are specific to the view for the "Slope", "Slope-Intercept" and "Point-Slope" tabs.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LineFormsViewProperties implements Resettable {

    // Are lines visible on the graph?
    public final Property<Boolean> linesVisible = new Property<Boolean>( true );

    // Is the interactive line visible on the graph?
    public final Property<Boolean> interactiveLineVisible = new Property<Boolean>( true );

    // Is the equation visible on the interactive line?
    public final Property<Boolean> interactiveEquationVisible = new Property<Boolean>( true );

    // Are the slope (rise/run) brackets visible on the graphed line?
    public final Property<Boolean> slopeVisible = new Property<Boolean>( true );

    public void reset() {
        linesVisible.reset();
        interactiveLineVisible.reset();
        interactiveEquationVisible.reset();
        slopeVisible.reset();
    }
}
