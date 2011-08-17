// Copyright 2002-2011, University of Colorado
package org.reid.scenic.model;

import edu.colorado.phet.sugarandsaltsolutions.common.util.ImmutableList;

/**
 * @author Sam Reid
 */
public class Model {
    public final ImmutableList<Atom> atoms;

    public Model( ImmutableList<Atom> atoms ) {
        this.atoms = atoms;
    }
}