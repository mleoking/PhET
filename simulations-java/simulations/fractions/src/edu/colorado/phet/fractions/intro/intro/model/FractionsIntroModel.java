// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractions.intro.common.model.SingleFractionModel;
import edu.colorado.phet.fractions.intro.intro.view.Fill;
import edu.colorado.phet.fractions.intro.intro.view.Visualization;

/**
 * Model for the Fractions Intro sim.
 *
 * @author Sam Reid
 */
public class FractionsIntroModel extends SingleFractionModel {
    public final Property<Fill> fill = new Property<Fill>( Fill.SEQUENTIAL );
    public final Property<Visualization> visualization = new Property<Visualization>( Visualization.NONE );

    public void resetAll() {
        super.resetAll();
        fill.reset();
        visualization.reset();
    }
}