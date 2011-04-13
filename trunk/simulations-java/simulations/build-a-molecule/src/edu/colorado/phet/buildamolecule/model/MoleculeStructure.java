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

    public void removeAtom( Atom atom ) {
        // TODO: what if this splits the molecule??? return a list of molecules?
        assert ( atoms.contains( atom ) );
        for ( Bond bond : new ArrayList<Bond>( bondMap.get( atom ) ) ) {
            removeBond( bond );
        }
        assert ( bondMap.get( atom ).isEmpty() );
        bondMap.remove( atom );
        atoms.remove( atom );
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

    public void removeBond( Bond bond ) {
        // TODO: what if this splits the molecule??? return a list of molecules?
        assert ( bonds.contains( bond ) );
        bonds.remove( bond );
        bondMap.get( bond.a ).remove( bond );
        bondMap.get( bond.b ).remove( bond );
    }

    public String getMolecularFormula() {
        return ChemUtils.hillOrderedSymbol( atoms );
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
    }
}
