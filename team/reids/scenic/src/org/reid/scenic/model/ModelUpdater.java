// Copyright 2002-2011, University of Colorado
package org.reid.scenic.model;

import org.reid.scenic.TestScenicPanel;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

/**
 * @author Sam Reid
 */
public class ModelUpdater implements Function1<Model, Model> {
    public Model apply( Model model ) {
        final double dt = 0.05;
        final ImmutableVector2D force = new ImmutableVector2D( 0, 9.8 );

        return model.atoms( model.atoms.map( new Function1<Atom, Atom>() {
            public Atom apply( Atom atom ) {
                //v = v0 + at, a = f/m, v = v0+ft/m
                final ImmutableVector2D velocity = atom.velocity.plus( force.times( dt / atom.mass ) );
                final ImmutableVector2D position = atom.position.plus( atom.velocity.times( dt ) );
                final ImmutableVector2D newVelocity = atom.position.getY() < TestScenicPanel.MAX_Y ? velocity : new ImmutableVector2D( velocity.getX(), -Math.abs( velocity.getY() ) );
                return new Atom( position, newVelocity, atom.mass );
            }
        } ) );
    }
}