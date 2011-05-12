//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.util.*;

import edu.colorado.phet.chemistry.model.Atom;

/**
 * Molecule structure with the hydrogens stripped out (but with the hydrogen count of an atom saved)
 * TODO: potentially move a "stripped" structure into MoleculeStructure for quick comparison!
 */
public class StrippedMolecule {
    public final MoleculeStructure stripped;
    private final Map<Atom, Integer> hydrogenCount = new HashMap<Atom, Integer>();

    public StrippedMolecule( MoleculeStructure original ) {
        stripped = new MoleculeStructure();

        // copy non-hydrogens
        for ( Atom atom : original.getAtoms() ) {
            if ( !atom.isHydrogen() ) {
                stripped.addAtom( atom );

                // initialize bonded hydrogen count to 0
                hydrogenCount.put( atom, 0 );
            }
        }

        // copy non-hydrogen honds, and mark hydrogen bonds
        for ( MoleculeStructure.Bond bond : original.getBonds() ) {
            boolean aIsHydrogen = bond.a.isHydrogen();
            boolean bIsHydrogen = bond.b.isHydrogen();

            // only do something if both aren't hydrogen
            if ( !aIsHydrogen || !bIsHydrogen ) {

                if ( aIsHydrogen || bIsHydrogen ) {
                    // increment hydrogen count of either A or B, if the bond contains hydrogen
                    if ( aIsHydrogen ) {
                        hydrogenCount.put( bond.b, hydrogenCount.get( bond.b ) + 1 );
                    }
                    else {
                        hydrogenCount.put( bond.a, hydrogenCount.get( bond.a ) + 1 );
                    }
                }
                else {
                    // bond doesn't involve hydrogen, so we add it to our stripped version
                    stripped.addBond( bond );
                }
            }
        }
    }

    /**
     * @return MoleculeStructure, where the hydrogen atoms are not the original hydrogen atoms
     */
    public MoleculeStructure toMoleculeStructure() {
        MoleculeStructure result = stripped.getCopy();
        for ( Atom atom : hydrogenCount.keySet() ) {
            int count = hydrogenCount.get( atom );
            for ( int i = 0; i < count; i++ ) {
                Atom.H hydrogenAtom = new Atom.H();
                result.addAtom( hydrogenAtom );
                result.addBond( atom, hydrogenAtom );
            }
        }
        return result;
    }

    public int getHydrogenCount( Atom atom ) {
        return hydrogenCount.get( atom );
    }

    public boolean isEquivalent( StrippedMolecule other ) {
        if ( this == other ) {
            // same instance
            return true;
        }
//        if ( this.original.getAtoms().size() != other.original.getAtoms().size() ) {
//            // must have same number of atoms
//            return false;
//        }
//        if ( !this.original.getHistogram().equals( other.original.getHistogram() ) ) {
//            // different molecular formula
//            return false;
//        }
        if ( this.stripped.getAtoms().size() == 0 && other.stripped.getAtoms().size() == 0 ) {
            return true;
        }
        Set<Atom> myVisited = new HashSet<Atom>();
        Set<Atom> otherVisited = new HashSet<Atom>();
        Atom firstAtom = stripped.getAtoms().iterator().next(); // grab the 1st atom
        for ( Atom otherAtom : other.stripped.getAtoms() ) {
            if ( checkEquivalency( other, myVisited, otherVisited, firstAtom, otherAtom, false ) ) {
                // we found an isomorphism with firstAtom => otherAtom
                return true;
            }
        }
        return false;
    }

    public boolean isHydrogenSubmolecule( StrippedMolecule other ) {
        if ( this == other ) {
            // same instance
            return true;
        }
//        if ( this.original.getAtoms().size() < other.original.getAtoms().size() ) {
//            return false;
//        }
//        if ( !this.original.getHistogram().containsAsSubset( other.original.getHistogram() ) ) {
//            return false;
//        }
        if ( stripped.getAtoms().size() == 0 ) {
            // if we have no heavy atoms
            return other.stripped.getAtoms().size() == 0;
        }
        Set<Atom> myVisited = new HashSet<Atom>();
        Set<Atom> otherVisited = new HashSet<Atom>();
        Atom firstAtom = stripped.getAtoms().iterator().next(); // grab the 1st atom
        for ( Atom otherAtom : other.stripped.getAtoms() ) {
            if ( checkEquivalency( other, myVisited, otherVisited, firstAtom, otherAtom, true ) ) {
                // we found an isomorphism with firstAtom => otherAtom
                return true;
            }
        }
        return false;
    }

    // TODO: separate out common behavior?
    private boolean checkEquivalency( StrippedMolecule other, Set<Atom> myVisited, Set<Atom> otherVisited, Atom myAtom, Atom otherAtom, boolean subCheck ) {
        if ( !myAtom.isSameTypeOfAtom( otherAtom ) ) {
            // if the atoms are of different types, bail. subtrees can't possibly be equivalent
            return false;
        }
        if ( !subCheck ) {
            // if the atoms have different numbers of hydrogen containing them, bail
            if ( getHydrogenCount( myAtom ) != other.getHydrogenCount( otherAtom ) ) {
                return false;
            }
        }
        else {
            // if the other atom has more hydrogens, bail
            if ( getHydrogenCount( myAtom ) < other.getHydrogenCount( otherAtom ) ) {
                return false;
            }
        }
        List<Atom> myUnvisitedNeighbors = stripped.getNeighborsNotInSet( myAtom, myVisited );
        List<Atom> otherUnvisitedNeighbors = other.stripped.getNeighborsNotInSet( otherAtom, otherVisited );
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
          equivalency matrix. each entry is basically whether the subtree in the direction of the "my" atom is
          equivalent to the subtree in the direction of the "other" atom, for all possible my and other atoms
        */
        boolean[][] equivalences = new boolean[size][size];

        // keep track of available indices for the following matrix equivalency check
        List<Integer> availableIndices = new LinkedList<Integer>();

        // for the love of god, this matrix is NOT symmetric. It computes whether each tree branch for A is equivalent to each tree branch for B
        for ( int myIndex = 0; myIndex < size; myIndex++ ) {
            availableIndices.add( myIndex );
            for ( int otherIndex = 0; otherIndex < size; otherIndex++ ) {
                equivalences[myIndex][otherIndex] = checkEquivalency( other, myVisited, otherVisited, myUnvisitedNeighbors.get( myIndex ), otherUnvisitedNeighbors.get( otherIndex ), subCheck );
            }
        }

        // remove the atoms from the visited sets, to hold our contract
        myVisited.remove( myAtom );
        otherVisited.remove( otherAtom );

        // return whether we can find a successful permutation matching from our equivalency matrix
        return MoleculeStructure.checkEquivalencyMatrix( equivalences, 0, availableIndices );
    }

    public StrippedMolecule getCopyWithAtomRemoved( Atom atom ) {
        StrippedMolecule result = new StrippedMolecule( stripped.getCopyWithAtomRemoved( atom ) );
        for ( Atom resultAtom : result.stripped.getAtoms() ) {
            result.hydrogenCount.put( resultAtom, hydrogenCount.get( resultAtom ) );
        }
        return result;
    }
}
