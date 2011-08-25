// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.ItemList;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

/**
 * A TargetConfiguration indicates target positions and matching crystal lattice sites for each member of a formula unit
 * so that a crystal can be grown by one formula unit at a time so that it will never become unbalanced
 *
 * @author Sam Reid
 */
public class TargetConfiguration<T extends Particle> {
    private final ItemList<CrystallizationMatch<T>> list;
    public final double distance;

    public TargetConfiguration( ItemList<CrystallizationMatch<T>> list ) {
        this.list = list;
        this.distance = list.foldLeft( 0.0, new Function2<CrystallizationMatch<T>, Double, Double>() {
            public Double apply( CrystallizationMatch<T> match, Double runningTotal ) {
                return match.distance + runningTotal;
            }
        } );
    }

    public ItemList<CrystallizationMatch<T>> getMatches() {
        return list;
    }

    @Override public String toString() {
        return list.toString();
    }
}