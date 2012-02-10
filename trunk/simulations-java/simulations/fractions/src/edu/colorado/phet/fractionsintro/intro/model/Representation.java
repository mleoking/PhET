// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import edu.colorado.phet.common.phetcommon.model.property.Property;

/**
 * A representation for a fraction such as "decimal" or "percentage", used to show a list in the toolbox and to enable/disable different representations.
 *
 * @author Sam Reid
 */
public class Representation {
    public final String name;
    public final Property<Boolean> enabled;

    public Representation( String name ) {
        this.name = name;
        this.enabled = new Property<Boolean>( false );
    }
}