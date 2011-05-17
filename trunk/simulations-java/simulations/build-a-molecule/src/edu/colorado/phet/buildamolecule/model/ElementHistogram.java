package edu.colorado.phet.buildamolecule.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.colorado.phet.buildamolecule.BuildAMoleculeConstants;
import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.chemistry.model.Element;

/**
 * Histogram of each element in a molecule, and allows fast comparison
 */
public class ElementHistogram {
    public static Set<String> ALLOWED_CHEMICAL_SYMBOLS = new HashSet<String>() {{
        // add in the symbols from our supported elements
        for ( Element element : BuildAMoleculeConstants.SUPPORTED_ELEMENTS ) {
            add( element.getSymbol() );
        }
    }};
    public final Map<String, Integer> quantities = new HashMap<String, Integer>() {{
        for ( String symbol : ALLOWED_CHEMICAL_SYMBOLS ) {
            put( symbol, 0 );
        }
    }};

    public ElementHistogram() {
    }

    public ElementHistogram( MoleculeStructure molecule ) {
        add( molecule );
    }

    public int getQuantity( Element element ) {
        return quantities.get( element.getSymbol() );
    }

    public void add( Element element ) {
        quantities.put( element.getSymbol(), quantities.get( element.getSymbol() ) + 1 );
    }

    public void add( MoleculeStructure molecule ) {
        for ( Atom atom : molecule.getAtoms() ) {
            add( atom.getElement() );
        }
    }

    public boolean containsAsSubset( ElementHistogram otherHistogram ) {
        for ( String symbol : ALLOWED_CHEMICAL_SYMBOLS ) {
            if ( quantities.get( symbol ) < otherHistogram.quantities.get( symbol ) ) {
                return false;
            }
        }
        return true;
    }

    public String getHashString() {
        String hashString = "";
        for ( String symbol : ALLOWED_CHEMICAL_SYMBOLS ) {
            hashString += quantities.get( symbol );
        }
        return hashString;
    }

    @Override
    public int hashCode() {
        return getHashString().hashCode();
    }

    @Override
    public boolean equals( Object obj ) {
        if ( obj instanceof ElementHistogram ) {
            ElementHistogram otherHistogram = (ElementHistogram) obj;
            for ( String symbol : ALLOWED_CHEMICAL_SYMBOLS ) {
                if ( !quantities.get( symbol ).equals( otherHistogram.quantities.get( symbol ) ) ) {
                    return false;
                }
            }
            return true;
        }
        else {
            return false;
        }
    }
}
