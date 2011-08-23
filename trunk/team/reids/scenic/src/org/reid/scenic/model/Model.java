// Copyright 2002-2011, University of Colorado
package org.reid.scenic.model;

import edu.colorado.phet.sugarandsaltsolutions.common.util.ImmutableList;

/**
 * @author Sam Reid
 */
public class Model {
    public final ImmutableList<Atom> atoms;
    public final ButtonModel button1;
    public final ButtonModel button2;

    public Model( ImmutableList<Atom> atoms, ButtonModel button1, ButtonModel button2 ) {
        this.atoms = atoms;
        this.button1 = button1;
        this.button2 = button2;
    }

    public Model atoms( ImmutableList<Atom> atoms ) {
        return new Model( atoms, button1, button2 );
    }

    public Model button1( ButtonModel button1 ) {
        return new Model( atoms, button1, button2 );
    }

    public Model button2( ButtonModel button2 ) {
        return new Model( atoms, button1, button2 );
    }
}