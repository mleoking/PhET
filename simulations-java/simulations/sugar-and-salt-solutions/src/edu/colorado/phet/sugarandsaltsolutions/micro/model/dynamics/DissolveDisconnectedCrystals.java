// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import java.util.IdentityHashMap;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.Crystal;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.MicroModel;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

/**
 * Workaround for completely dissolving any crystals that have become disconnected as a result of partial dissolving
 *
 * @author Sam Reid
 */
public class DissolveDisconnectedCrystals {
    private MicroModel model;
    public static final boolean debug = true;

    //Map that keeps track of the number of steps that a crystal has been identified as disconnected.  If it is disconnected too long, it will be completely dissolved.
    private final IdentityHashMap<Crystal, Integer> numberStepsDisconnected = new IdentityHashMap<Crystal, Integer>();

    public DissolveDisconnectedCrystals( MicroModel model ) {
        this.model = model;
    }

    //If any crystal has been disconnected too long, it will be completely dissolved
    public <T extends Particle, U extends Crystal<T>> void apply( ItemList<U> crystalItemList ) {
        for ( Crystal<T> crystal : crystalItemList.toList() ) {

            if ( crystal.isConnected() ) {

                //Clean up the map to prevent memory leak and reset for next time
                numberStepsDisconnected.remove( crystal );
            }
            else {

                //Increment the counts in the map
                final int newCount = numberStepsDisconnected.containsKey( crystal ) ? numberStepsDisconnected.get( crystal ) + 1 : 1;
                numberStepsDisconnected.put( crystal, newCount );

                //If it has been disconnected for >1 second, dissolve it completely
                if ( newCount > 30 ) {
                    if ( debug ) {
                        System.out.println( "Crystal disconnected for " + newCount + " steps, dissolving..." );
                    }
                    new CrystalDissolve<T>( model ).dissolve( crystal, crystal.getConstituents().toList() );
                    crystalItemList.remove( crystal );
                }
            }
        }

        //Prevent memory leak
        if ( numberStepsDisconnected.keySet().size() > 100 ) {
            numberStepsDisconnected.clear();
        }
    }
}