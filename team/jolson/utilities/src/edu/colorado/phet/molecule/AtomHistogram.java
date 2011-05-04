package edu.colorado.phet.molecule;

import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.buildamolecule.model.MoleculeStructure;
import edu.colorado.phet.chemistry.model.Atom;

/**
 * Count of each atom type. TODO consider moving to BAM
 */
public class AtomHistogram {
    public final Map<String, Integer> quantities = new HashMap<String, Integer>() {{
        for ( String symbol : MoleculeSDFCombinedParser.ALLOWED_CHEMICAL_SYMBOLS ) {
            put( symbol, 0 );
        }
    }};

    public void add( Atom atom ) {
        quantities.put( atom.getSymbol(), quantities.get( atom.getSymbol() ) + 1 );
    }

    public void add( MoleculeStructure molecule ) {
        for ( Atom atom : molecule.getAtoms() ) {
            add( atom );
        }
    }

    public boolean containsAsSubset( AtomHistogram otherHistogram ) {
        for ( String symbol : MoleculeSDFCombinedParser.ALLOWED_CHEMICAL_SYMBOLS ) {
            if ( quantities.get( symbol ) < otherHistogram.quantities.get( symbol ) ) {
                return false;
            }
        }
        return true;
    }
}
