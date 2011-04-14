// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.util.*;

import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.chemistry.utils.ChemUtils;

/**
 * Represents a general molecular structure (without position or instance information).
 * <p/>
 * TODO: add in comparison of molecular structures! (graph/tree, not by formula)
 */
public class MoleculeStructure {
    private Set<Atom> atoms = new HashSet<Atom>();
    private Set<Bond> bonds = new HashSet<Bond>();

    /**
     * Map of atom => all bonds that contain that atom
     */
    private Map<Atom, Set<Bond>> bondMap = new HashMap<Atom, Set<Bond>>();

    public MoleculeStructure() {
    }

    /**
     * @param atom An atom to add
     * @return The atom that was added
     */
    public Atom addAtom( Atom atom ) {
        assert ( !atoms.contains( atom ) );
        atoms.add( atom );
        bondMap.put( atom, new HashSet<Bond>() );
        return atom;
    }

    public void addBond( Bond bond ) {
        assert ( atoms.contains( bond.a ) );
        assert ( atoms.contains( bond.b ) );
        bonds.add( bond );
        bondMap.get( bond.a ).add( bond );
        bondMap.get( bond.b ).add( bond );
    }

    public void addBond( Atom a, Atom b ) {
        addBond( new Bond( a, b ) );
    }

    public String getMolecularFormula() {
        return ChemUtils.hillOrderedSymbol( atoms );
    }

    public Set<Atom> getAtoms() {
        return atoms;
    }

    public Set<Bond> getBonds() {
        return bonds;
    }

    /**
     * @param atom An atom
     * @return All neighboring atoms that are connected by bonds to the passed in atom
     */
    public List<Atom> getNeighbors( Atom atom ) {
        List<Atom> ret = new LinkedList<Atom>();
        for ( Bond bond : bonds ) {
            if ( bond.contains( atom ) ) {
                ret.add( bond.getOtherAtom( atom ) );
            }
        }
        return ret;
    }

    public static MoleculeStructure bondTogether( MoleculeStructure molA, MoleculeStructure molB, Atom a, Atom b ) {
        MoleculeStructure ret = new MoleculeStructure();
        for ( Atom atom : molA.getAtoms() ) {
            ret.addAtom( atom );
        }
        for ( Atom atom : molB.getAtoms() ) {
            ret.addAtom( atom );
        }
        for ( Bond bond : molA.getBonds() ) {
            ret.addBond( bond );
        }
        for ( Bond bond : molB.getBonds() ) {
            ret.addBond( bond );
        }
        ret.addBond( a, b );
        return ret;
    }

    public static class Bond {
        public Atom a;
        public Atom b;

        public Bond( Atom a, Atom b ) {
            this.a = a;
            this.b = b;
        }

        @Override
        public int hashCode() {
            return a.hashCode() + b.hashCode();
        }

        @Override
        public boolean equals( Object ob ) {
            if ( ob instanceof Bond/*, James Bond*/ ) {
                Bond other = (Bond) ob;
                return ( this.a == other.a && this.b == other.b ) || ( this.a == other.b && this.b == other.a );
            }
            else {
                return false;
            }
        }

        public boolean contains( Atom atom ) {
            return atom == a || atom == b;
        }

        public Atom getOtherAtom( Atom atom ) {
            assert ( contains( atom ) );
            return ( a == atom ? b : a );
        }
    }

    public MoleculeStructure getCopy() {
        MoleculeStructure ret = new MoleculeStructure();
        for ( Atom atom : atoms ) {
            ret.addAtom( atom );
        }
        for ( Bond bond : bonds ) {
            ret.addBond( bond );
        }
        return ret;
    }

    public MoleculeStructure getCopyWithAtomRemoved( Atom atomToRemove ) {
        MoleculeStructure ret = new MoleculeStructure();
        for ( Atom atom : atoms ) {
            if ( atom != atomToRemove ) {
                ret.addAtom( atom );
            }
        }
        for ( Bond bond : bonds ) {
            if ( !bond.contains( atomToRemove ) ) {
                ret.addBond( bond );
            }
        }
        return ret;
    }

    /**
     * Check whether the molecular structure is equivalent to another structure. Not terribly efficient, and will
     * probably fail for cyclic graphs.
     * <p/>
     * TODO: add tree comparison testing
     *
     * @param other Another molecular structure
     * @return True, if there is an isomorphism between the two molecular structures
     */
    public boolean isEquivalent( MoleculeStructure other ) {
        if ( this == other ) {
            // same instance
            return true;
        }
        if ( !getMolecularFormula().equals( other.getMolecularFormula() ) ) {
            // different molecular formula
            return false;
        }
        Set<Atom> myVisited = new HashSet<Atom>();
        Set<Atom> otherVisited = new HashSet<Atom>();
        Atom firstAtom = atoms.iterator().next(); // grab the 1st atom
        for ( Atom otherAtom : other.getAtoms() ) {
            if ( checkEquivalency( other, myVisited, otherVisited, firstAtom, otherAtom ) ) {
                // we found an isomorphism with firstAtom => otherAtom
                return true;
            }
        }
        return false;
    }

    /**
     * @param atom         An atom
     * @param exclusionSet A set of atoms that should not be in the return value
     * @return All neighboring atoms that are connected by bonds to the passed in atom AND aren't in the exclusionSet
     */
    private List<Atom> getNeighborsNotInSet( Atom atom, Set<Atom> exclusionSet ) {
        List<Atom> ret = new LinkedList<Atom>();
        for ( Atom otherAtom : getNeighbors( atom ) ) {
            if ( !exclusionSet.contains( otherAtom ) ) {
                ret.add( otherAtom );
            }
        }
        return ret;
    }

    private boolean checkEquivalency( MoleculeStructure other, Set<Atom> myVisited, Set<Atom> otherVisited, Atom myAtom, Atom otherAtom ) {
        if ( !myAtom.isSameTypeOfAtom( otherAtom ) ) {
            // if the atoms are of different types, bail. subtrees can't possibly be equivalent
            return false;
        }
        List<Atom> myUnvisitedNeighbors = getNeighborsNotInSet( myAtom, myVisited );
        List<Atom> otherUnvisitedNeighbors = other.getNeighborsNotInSet( otherAtom, otherVisited );
        if ( myUnvisitedNeighbors.size() != otherUnvisitedNeighbors.size() ) {
            return false;
        }
        if ( myUnvisitedNeighbors.isEmpty() ) {
            // no more unmatched atoms
            return true;
        }
        int size = myUnvisitedNeighbors.size();

        // for now, add visiting atoms to the visited set. we NEED to revert this before returning!
        myVisited.add( myAtom );
        otherVisited.add( otherAtom );

        /*
          symmetric equivalency matrix. each entry is basically whether the subtree in the direction of the "my" atom is
          equivalent to the subtree in the direction of the "other" atom, for all possible my and other atoms
        */
        boolean[][] equivalences = new boolean[size][size];

        // keep track of available indices for the following matrix equivalency check
        List<Integer> availableIndices = new LinkedList<Integer>();

        // compute this for each part of (my,other) atoms. since it is symmetric, we only do approximately half of the heavy-lifting
        for ( int myIndex = 0; myIndex < size; myIndex++ ) {
            availableIndices.add( myIndex );
            for ( int otherIndex = myIndex; otherIndex < size; otherIndex++ ) {
                equivalences[myIndex][otherIndex] = checkEquivalency( other, myVisited, otherVisited, myUnvisitedNeighbors.get( myIndex ), otherUnvisitedNeighbors.get( otherIndex ) );
                equivalences[otherIndex][myIndex] = equivalences[myIndex][otherIndex]; // just for sanity, because this is symmetric
            }
        }

        // remove the atoms from the visited sets, to hold our contract
        myVisited.remove( myAtom );
        otherVisited.remove( otherAtom );

        // return whether we can find a successful permutation matching from our equivalency matrix
        return checkEquivalencyMatrix( equivalences, 0, availableIndices );
    }

    /**
     * Given a symmetric matrix of equivalencies, can we find a permutation of the "other" atoms that are equivalent to
     * their respective "my" atoms?
     *
     * @param equivalences          Equivalence Matrix
     * @param myIndex               Index for the row (index into our atoms). calls with myIndex + 1 to children
     * @param otherRemainingIndices Remaining available "other" indices
     * @return Whether a successful matching permutation was found
     */
    private boolean checkEquivalencyMatrix( boolean[][] equivalences, int myIndex, List<Integer> otherRemainingIndices ) {
        // should be inefficient, but not too bad (computational complexity is not optimal)
        for ( Integer otherIndex : otherRemainingIndices ) { // loop over all remaining others
            if ( equivalences[myIndex][otherIndex] ) { // only follow path if it is true (equivalent)

                // remove the index from consideration for checking the following submatrix
                otherRemainingIndices.remove( otherIndex );

                boolean success = ( myIndex == equivalences.length - 1 ) // there are no more permutations to check
                                  || checkEquivalencyMatrix( equivalences, myIndex + 1, otherRemainingIndices ); // or we can find a good combination of the remaining indices

                // add it back in so the calling function's contract for otherRemainingIndices is satisfied
                otherRemainingIndices.add( otherIndex );

                if ( success ) {
                    return true;
                }
            }
        }
        return false;
    }

}
