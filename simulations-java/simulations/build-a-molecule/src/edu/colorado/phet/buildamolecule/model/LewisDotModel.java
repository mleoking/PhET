// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Option;

/**
 * Represents the lewis-dot directional connections between atoms. Holds information for all atoms within a particular kit, but it is generic
 * enough to handle other situations
 */
public class LewisDotModel {
    private Map<Atom, LewisDotAtom> atomMap = new HashMap<Atom, LewisDotAtom>();

    /*---------------------------------------------------------------------------*
    * directions
    *----------------------------------------------------------------------------*/

    /**
     * Represents a cardinal direction for use in our model. Also includes unit vector version
     */
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

    public void breakBondsOfAtom( Atom atom ) {
        LewisDotAtom dotAtom = getLewisDotAtom( atom );

        // disconnect all of its bonds
        for ( Direction direction : Direction.values() ) {
            if ( dotAtom.hasConnection( direction ) ) {
                LewisDotAtom other = dotAtom.getLewisDotAtom( direction );
                breakBond( dotAtom.getAtom(), other.getAtom() );
            }
        }
    }

    /**
     * Break the bond between A and B (if it exists)
     *
     * @param a A
     * @param b B
     */
    public void breakBond( Atom a, Atom b ) {
        LewisDotAtom dotA = getLewisDotAtom( a );
        LewisDotAtom dotB = getLewisDotAtom( b );
        Direction direction = getBondDirection( a, b );
        dotA.disconnect( direction );
        dotB.disconnect( Direction.opposite( direction ) );
    }

    /**
     * Bond together atoms A and B.
     *
     * @param a       A
     * @param dirAtoB The direction from A to B. So if A is to the left, B is on the right, the direction would be East
     * @param b       B
     */
    public void bond( Atom a, Direction dirAtoB, Atom b ) {
        LewisDotAtom dotA = getLewisDotAtom( a );
        LewisDotAtom dotB = getLewisDotAtom( b );
        dotA.connect( dirAtoB, dotB );
        dotB.connect( Direction.opposite( dirAtoB ), dotA );
    }

    /**
     * @param atom An atom
     * @return All of the directions that are open (not bonded to another) on the atom
     */
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

    /**
     * @param a A
     * @param b B
     * @return The bond direction from A to B. If it doesn't exist, an exception is thrown
     */
    public Direction getBondDirection( Atom a, Atom b ) {
        LewisDotAtom dotA = getLewisDotAtom( a );
        for ( Direction direction : Direction.values() ) {
            if ( dotA.hasConnection( direction ) && dotA.getLewisDotAtom( direction ).atom == b ) {
                return direction;
            }
        }
        throw new RuntimeException( "Bond not found" );
    }

    /**
     * Decide whether this bonding would cause any layout issues. Does NOT detect loops, and will
     * fail if given molecules with loops.
     *
     * @param a         A
     * @param direction Direction from A to B
     * @param b         B
     * @return Whether this bond is considered acceptable
     */
    public boolean willAllowBond( Atom a, Direction direction, Atom b ) {

        /*---------------------------------------------------------------------------*
        * We need to verify that if we bind these two together that no overlaps occur.
        * This can be done by creating a coordinate system where atom A is our origin,
        * and verifying that no atoms share the same coordinates if they are not both
        * hydrogen.
        *----------------------------------------------------------------------------*/

        Map<Point2D, Atom> coordinateMap = new HashMap<Point2D, Atom>();

        // map the molecule on the A side, from the origin
        boolean success = mapMolecule( new ImmutableVector2D(), a, null, coordinateMap );

        // map the molecule on the B side, with the offset from direction
        success = success && mapMolecule( direction.getVector(), b, null, coordinateMap );

        // we would have false if a conflict was found
        return success;
    }

    /*---------------------------------------------------------------------------*
    * implementation details
    *----------------------------------------------------------------------------*/

    /**
     * Add "atom" to our coordinate map, and all of its neighbors EXCEPT for excludedAtom.
     * This allows mapping a molecule without loops quite easily
     *
     * @param coordinates   Coordinates of "atom"
     * @param atom          Atom to add
     * @param excludedAtom  Atom not to
     * @param coordinateMap Coordinate map to which we add the atoms to
     * @return Success. Will return false if any heavy atom overlaps on another atom. If it returns false, the coordinate map may be inconsistent
     */
    private boolean mapMolecule( ImmutableVector2D coordinates, Atom atom, Atom excludedAtom, Map<Point2D, Atom> coordinateMap ) {
        LewisDotAtom dotAtom = getLewisDotAtom( atom );

        // for sanity and equality (negative zero equals zero, so don't worry about that)
        Point2D point = new Point2D.Double( Math.round( coordinates.getX() ), Math.round( coordinates.getY() ) );

        // if we have seen a different atom in this position
        if ( coordinateMap.containsKey( point ) ) {
            // if at least one isn't hydrogen, fail out
            if ( !atom.isHydrogen() || !coordinateMap.get( point ).isHydrogen() ) {
                return false;
            }
            // here, they both must be hydrogen, so we don't need to worry about adding it in
        }
        else {
            coordinateMap.put( point, atom );
        }

        boolean success = true;

        // check all directions so we can explore all other atoms that need to be mapped
        for ( Direction direction : Direction.values() ) {
            if ( dotAtom.hasConnection( direction ) ) {
                LewisDotAtom otherDot = dotAtom.getLewisDotAtom( direction );

                // if this atom isn't excluded
                if ( otherDot.atom != excludedAtom ) {
                    success = mapMolecule( coordinates.plus( direction.getVector() ), otherDot.atom, atom, coordinateMap );

                    // if we had a failure mapping that one, bail out
                    if ( !success ) {
                        return false;
                    }
                }
            }
        }

        // everything worked
        return success;
    }

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
