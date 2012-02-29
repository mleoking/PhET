// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.equalitylab.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.fractionsintro.intro.view.Representation;

/**
 * Model for the Equality Lab tab
 *
 * @author Sam Reid
 */
public class EqualityLabModel {
    public SettableProperty<Representation> representation = new Property<Representation>( Representation.PIE );

    public void resetAll() {
    }
}