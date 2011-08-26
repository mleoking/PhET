// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a formula ratio for constructive units for crystals.  NaCl is 1Na + 1Cl, NaNO3 is 1Na + 1 NO3.  Sucrose crystals just have a repeating unit of 1 sucrose molecule.
 * CaCl2 is 2 Ca + 2 Cl.
 *
 * @author Sam Reid
 */
public class Formula {
    public final HashMap<Class<? extends Particle>, Integer> map;

    //Convenience constructor for making a formula unit of 1:1
    public Formula( final Class<? extends Particle> a, final Class<? extends Particle> b ) {
        this( a, b, 1 );
    }

    //Convenience constructor for making a formula unit of 1:N, as in CaCl2
    public Formula( final Class<? extends Particle> a, final Class<? extends Particle> b, final int bCount ) {
        this( new HashMap<Class<? extends Particle>, Integer>() {{
            put( a, 1 );
            put( b, bCount );
        }} );
    }

    //Convenience constructor for making a formula unit of 1 such as in Sucrose or Glucose
    public Formula( final Class<? extends Particle> type ) {
        this( new HashMap<Class<? extends Particle>, Integer>() {{
            put( type, 1 );
        }} );
    }

    //Create a formula by giving an explicit map
    public Formula( HashMap<Class<? extends Particle>, Integer> map ) {
        this.map = map;
    }

    //List the different types used in the formula
    public ArrayList<Class<? extends Particle>> getTypes() {
        return new ArrayList<Class<? extends Particle>>( map.keySet() );
    }

    //Determine the count for the building block of the specified type
    public int getFactor( Class type ) {
        return map.get( type );
    }

    //Deterine if this formula contains the specified type of particle
    public boolean contains( Class<? extends Particle> type ) {
        return getTypes().contains( type );
    }

    //Duplicates classes according to the formula counts, to facilitate iteration
    public ArrayList<Class<? extends Particle>> getFormulaUnit() {
        ArrayList<Class<? extends Particle>> list = new ArrayList<Class<? extends Particle>>();
        for ( Class<? extends Particle> type : getTypes() ) {
            for ( int i = 0; i < getFactor( type ); i++ ) {
                list.add( type );
            }
        }
        return list;
    }
}