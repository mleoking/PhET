// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.util.ArrayList;
import java.util.HashMap;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Calcium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Chloride;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.SphericalParticle.Sodium;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.glucose.Glucose;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sodiumnitrate.Nitrate;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.sucrose.Sucrose;

/**
 * Represents a formula ratio for constructive units for crystals.  NaCl is 1Na + 1Cl, NaNO3 is 1Na + 1 NO3.  Sucrose crystals just have a repeating unit of 1 sucrose molecule.
 * CaCl2 is 2 Ca + 2 Cl.
 *
 * @author Sam Reid
 */
public class Formula {
    public final HashMap<Class<? extends Particle>, Integer> map;

    //Formulae used in Sugar and Salt Solutions
    public static final Formula SODIUM_CHLORIDE = new Formula( Sodium.class, Chloride.class );
    public static final Formula SUCROSE = new Formula( Sucrose.class );
    public static final Formula GLUCOSE = new Formula( Glucose.class );
    public static final Formula SODIUM_NITRATE = new Formula( Sodium.class, Nitrate.class );

    //The formula for calcium chloride must return Calcium first, otherwise the crystal growing procedure can run into too many dead ends
    public static final Formula CALCIUM_CHLORIDE = new Formula( Calcium.class, Chloride.class, 2 ) {
        @Override public ArrayList<Class<? extends Particle>> getTypes() {
            return new ArrayList<Class<? extends Particle>>() {{
                add( Calcium.class );
                add( Chloride.class );
            }};
        }
    };

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

    //Determine if this formula contains the specified type of particle
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

    @Override public String toString() {
        return map.toString();
    }

    //Auto-generated equality test
    @Override public boolean equals( Object o ) {
        if ( this == o ) { return true; }
        if ( o == null || getClass() != o.getClass() ) { return false; }

        Formula formula = (Formula) o;

        if ( !map.equals( formula.map ) ) { return false; }

        return true;
    }

    //Auto-generated hashCode
    @Override public int hashCode() {
        return map.hashCode();
    }
}