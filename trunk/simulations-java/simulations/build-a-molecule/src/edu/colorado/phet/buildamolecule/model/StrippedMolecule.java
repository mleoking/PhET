//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.util.*;

import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.chemistry.model.Element;

/**
 * Molecule structure with the hydrogens stripped out (but with the hydrogen count of an atom saved)
 * TODO: potentially move a "stripped" structure into MoleculeStructure for quick comparison!
 */
public class StrippedMolecule<AtomT extends Atom> {
    public final MoleculeStructure<AtomT> stripped;

    /**
     * Array indexed the same way as stripped.getAtoms() for efficiency. It's essentially immutable, so this works
     */
    private final int[] hydrogenCount;

    public StrippedMolecule( MoleculeStructure<AtomT> original ) {
        List<AtomT> atomsToAdd = new ArrayList<AtomT>();
        List<Bond<AtomT>> bondsToAdd = new ArrayList<Bond<AtomT>>();

        // copy non-hydrogens
        for ( AtomT atom : original.getAtoms() ) {
            if ( !atom.isHydrogen() ) {
                atomsToAdd.add( atom );
            }
        }

        hydrogenCount = new int[atomsToAdd.size()];

        // copy non-hydrogen honds, and mark hydrogen bonds
        for ( Bond<AtomT> bond : original.getBonds() ) {
            boolean aIsHydrogen = bond.a.isHydrogen();
            boolean bIsHydrogen = bond.b.isHydrogen();

            // only do something if both aren't hydrogen
            if ( !aIsHydrogen || !bIsHydrogen ) {

                if ( aIsHydrogen || bIsHydrogen ) {
                    // increment hydrogen count of either A or B, if the bond contains hydrogen
                    hydrogenCount[atomsToAdd.indexOf( aIsHydrogen ? bond.b : bond.a )]++;
                }
                else {
                    // bond doesn't involve hydrogen, so we add it to our stripped version
                    bondsToAdd.add( bond );
                }
            }
        }

        // construct the stripped structure
        stripped = new MoleculeStructure<AtomT>( atomsToAdd.size(), bondsToAdd.size() );
        for ( AtomT atom : atomsToAdd ) {
            stripped.addAtom( atom );
        }
        for ( Bond<AtomT> bond : bondsToAdd ) {
            stripped.addBond( bond );
        }
    }

    /**
     * @return MoleculeStructure, where the hydrogen atoms are not the original hydrogen atoms
     */
    public MoleculeStructure<Atom> toMoleculeStructure() {
        MoleculeStructure<Atom> result = stripped.getAtomCopy();
        for ( AtomT atom : stripped.getAtoms() ) {
            int count = getHydrogenCount( atom );
            for ( int i = 0; i < count; i++ ) {
                Atom hydrogenAtom = new Atom( Element.H );
                result.addAtom( hydrogenAtom );
                result.addBond( atom, hydrogenAtom );
            }
        }
        return result;
    }

    private int getIndex( AtomT atom ) {
        int index = stripped.getAtoms().indexOf( atom );
        assert ( index != -1 );
        return index;
    }

    public int getHydrogenCount( AtomT atom ) {
        return hydrogenCount[getIndex( atom )];
    }

    public <AtomU extends Atom> boolean isEquivalent( StrippedMolecule<AtomU> other ) {
        if ( this == other ) {
            // same instance
            return true;
        }

        if ( this.stripped.getAtoms().size() == 0 && other.stripped.getAtoms().size() == 0 ) {
            return true;
        }
        Set<AtomT> myVisited = new HashSet<AtomT>();
        Set<AtomU> otherVisited = new HashSet<AtomU>();
        AtomT firstAtom = stripped.getAtoms().iterator().next(); // grab the 1st atom
        for ( AtomU otherAtom : other.stripped.getAtoms() ) {
            if ( checkEquivalency( other, myVisited, otherVisited, firstAtom, otherAtom, false ) ) {
                // we found an isomorphism with firstAtom => otherAtom
                return true;
            }
        }
        return false;
    }

    public <AtomU extends Atom> boolean isHydrogenSubmolecule( StrippedMolecule<AtomU> other ) {
        if ( this == other ) {
            // same instance
            return true;
        }

        if ( stripped.getAtoms().size() == 0 ) {
            // if we have no heavy atoms
            return other.stripped.getAtoms().size() == 0;
        }
        Set<AtomT> myVisited = new HashSet<AtomT>();
        Set<AtomU> otherVisited = new HashSet<AtomU>();
        AtomT firstAtom = stripped.getAtoms().iterator().next(); // grab the 1st atom
        for ( AtomU otherAtom : other.stripped.getAtoms() ) {
            if ( checkEquivalency( other, myVisited, otherVisited, firstAtom, otherAtom, true ) ) {
                // we found an isomorphism with firstAtom => otherAtom
                return true;
            }
        }
        return false;
    }

    // TODO: separate out common behavior?
    private <AtomU extends Atom> boolean checkEquivalency( StrippedMolecule<AtomU> other, Set<AtomT> myVisited, Set<AtomU> otherVisited, AtomT myAtom, AtomU otherAtom, boolean subCheck ) {
        if ( !myAtom.hasSameElement( otherAtom ) ) {
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
        List<AtomT> myUnvisitedNeighbors = stripped.getNeighborsNotInSet( myAtom, myVisited );
        List<AtomU> otherUnvisitedNeighbors = other.stripped.getNeighborsNotInSet( otherAtom, otherVisited );
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

    public StrippedMolecule<AtomT> getCopyWithAtomRemoved( AtomT atom ) {
        StrippedMolecule<AtomT> result = new StrippedMolecule<AtomT>( stripped.getCopyWithAtomRemoved( atom ) );
        for ( AtomT resultAtom : result.stripped.getAtoms() ) {
            result.hydrogenCount[result.getIndex( resultAtom )] = getHydrogenCount( resultAtom );
        }
        return result;
    }
}
