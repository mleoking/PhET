// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;

/**
 * Represents the lewis-dot directional connections between atoms
 */
public class LewisDotModel {
    private Map<Atom, LewisDotAtom> atomMap = new HashMap<Atom, LewisDotAtom>();

    /*---------------------------------------------------------------------------*
    * directions
    *----------------------------------------------------------------------------*/

    public static enum Direction {
        North( new ImmutableVector2D( 0, 1 ) ),
        East( new ImmutableVector2D( 1, 0 ) ),
        South( new ImmutableVector2D( 0, -1 ) ),
        West( new ImmutableVector2D( -1, 0 ) );

        private ImmutableVector2D vector;

        Direction( ImmutableVector2D vector ) {
            this.vector = vector;
        }

        public ImmutableVector2D getVector() {
            return vector;
        }

        public static Direction opposite( Direction direction ) {
            switch( direction ) {
                case North:
                    return South;
                case East:
                    return West;
                case South:
                    return North;
                case West:
                    return East;
                default:
                    throw new RuntimeException( "Could not find opposite direction" );
            }
        }
    }

    /*---------------------------------------------------------------------------*
    * public methods
    *----------------------------------------------------------------------------*/

    public void addAtom( Atom atom ) {
        LewisDotAtom dotAtom = new LewisDotAtom( atom );
        atomMap.put( atom, dotAtom );
    }

    public void removeAtom( Atom atom ) {
        LewisDotAtom dotAtom = getLewisDotAtom( atom );
        if ( dotAtom == null ) {
            return;
        }

        // disconnect all of its bonds
        for ( Direction direction : Direction.values() ) {
            if ( dotAtom.hasConnection( direction ) ) {
                LewisDotAtom other = dotAtom.getLewisDotAtom( direction );
                dotAtom.disconnect( direction );
                other.disconnect( Direction.opposite( direction ) );
            }
        }

        atomMap.remove( atom );
    }

    public void bond( Atom a, Direction dirAtoB, Atom b ) {
        LewisDotAtom dotA = getLewisDotAtom( a );
        LewisDotAtom dotB = getLewisDotAtom( b );
        dotA.connect( dirAtoB, dotB );
        dotB.connect( Direction.opposite( dirAtoB ), dotA );
    }

    public void unBond( Atom a, Atom b ) {
        LewisDotAtom dotA = getLewisDotAtom( a );
        LewisDotAtom dotB = getLewisDotAtom( b );
        for ( Direction direction : Direction.values() ) {
            if ( dotA.hasConnection( direction ) && dotA.getLewisDotAtom( direction ) == dotB ) {
                dotA.disconnect( direction );
                dotB.disconnect( Direction.opposite( direction ) );
            }
        }
    }

    public List<Direction> getOpenDirections( Atom atom ) {
        List<Direction> ret = new LinkedList<Direction>();
        LewisDotAtom dotAtom = getLewisDotAtom( atom );
        for ( Direction direction : Direction.values() ) {
            if ( !dotAtom.hasConnection( direction ) ) {
                ret.add( direction );
            }
        }
        return ret;
    }

    /*---------------------------------------------------------------------------*
    * implementation details
    *----------------------------------------------------------------------------*/

    private LewisDotAtom getLewisDotAtom( Atom atom ) {
        return atomMap.get( atom );
    }

    private static class LewisDotAtom {
        private Atom atom;
        private Map<Direction, Option<LewisDotAtom>> connections = new HashMap<Direction, Option<LewisDotAtom>>();

        public LewisDotAtom( Atom atom ) {
            this.atom = atom;
            for ( Direction direction : Direction.values() ) {
                connections.put( direction, new Option.None<LewisDotAtom>() );
            }
        }

        public boolean hasConnection( Direction direction ) {
            return connections.get( direction ).isSome();
        }

        public LewisDotAtom getLewisDotAtom( Direction direction ) {
            return connections.get( direction ).get();
        }

        public void connect( Direction direction, LewisDotAtom atom ) {
            connections.put( direction, new Option.Some<LewisDotAtom>( atom ) );
        }

        public void disconnect( Direction direction ) {
            connections.put( direction, new Option.None<LewisDotAtom>() );
        }

        public Atom getAtom() {
            return atom;
        }
    }

}
