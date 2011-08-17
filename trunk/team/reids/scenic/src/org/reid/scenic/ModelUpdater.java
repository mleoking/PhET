// Copyright 2002-2011, University of Colorado
package org.reid.scenic;

import org.reid.scenic.model.Atom;
import org.reid.scenic.model.Model;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * @author Sam Reid
 */
public class ModelUpdater implements Function1<Model, Model> {
    public Model apply( Model model ) {
        final double dt = 0.05;
        final ImmutableVector2D force = new ImmutableVector2D( 0, 9.8 );

        return new Model( model.atoms.map( new Function1<Atom, Atom>() {
            public Atom apply( Atom atom ) {
                //v = v0 + at, a = f/m, v = v0+ft/m
                ImmutableVector2D velocity = atom.velocity.plus( force.times( dt / atom.mass ) );
                return new Atom( atom.position.plus( atom.velocity.times( dt ) ), atom.position.getY() < TestScenicPanel.MAX_Y ? velocity : new ImmutableVector2D( velocity.getX(), -Math.abs( velocity.getY() ) ), atom.mass );
            }
        } ) );
    }
}