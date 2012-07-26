// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.model;

import fj.data.List;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;

/**
 * @author Sam Reid
 */
public class Level {

    //Fractions the user has created in the play area, which may match a target
    public final Property<List<Fraction>> createdFractions = new Property<List<Fraction>>( List.<Fraction>nil() );

    public void resetAll() { createdFractions.reset(); }
}