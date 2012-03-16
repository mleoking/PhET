// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.model;

import edu.colorado.phet.buildanatom.model.ImmutableAtom;

/**
 * Abstract superclass for game problems in which the user is asked to predict which element is represented.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public abstract class ToElementProblem extends Problem {
    public ToElementProblem( BuildAnAtomGameModel model, ImmutableAtom atomValue ) {
        super( model, atomValue );
    }
}
