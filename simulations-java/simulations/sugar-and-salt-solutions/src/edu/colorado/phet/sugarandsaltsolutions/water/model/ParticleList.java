// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.model;

import java.util.ArrayList;

import org.jbox2d.dynamics.World;

import edu.colorado.phet.common.phetcommon.model.property.doubleproperty.DoubleProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Abstraction over lists of particles that are used in the WaterModel, to provide access to the number of items in the list and to clear all particles.
 * Here a "particle" could be a molecule.
 *
 * @author Sam Reid
 */
public class ParticleList<T extends Molecule> {
    //Lists of all model objects
    public final ArrayList<T> list = new ArrayList<T>();

    //Listeners that are notified when something enters the model.  Removal listeners are added to the particle itself
    public final ArrayList<VoidFunction1<T>> itemAddedListeners = new ArrayList<VoidFunction1<T>>();

    //The number in the list
    public final DoubleProperty count = new DoubleProperty( 0.0 );

    public void add( T t ) {
        list.add( t );
        updateCount();
        for ( VoidFunction1<T> listener : itemAddedListeners ) {
            listener.apply( t );
        }
    }

    private void updateCount() {
        count.set( list.size() + 0.0 );
    }

    public void clear( World world ) {
        for ( T t : list ) {
            world.destroyBody( t.body );
            t.notifyRemoved();
        }
        list.clear();
        updateCount();
    }
}
