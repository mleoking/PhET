// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.moleculeshapes.math.ImmutableVector3D;

/**
 * Represents a "real" molecule with exact positions, as opposed to a molecule model (which is VSEPR-based
 * and doesn't include other information).
 * <p/>
 * We display these real molecules to the user in 3D
 * TODO: consider making into a generic version for other sims
 */
public class RealMolecule extends Molecule {

    private final String displayName;
    private final int lonePairCount;

    private RealMolecule( String displayName, int lonePairCount ) {
        this.displayName = displayName;
        this.lonePairCount = lonePairCount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int countBondsOfOrder( int order ) {
        int result = 0;
        for ( Bond<Atom3D> bond : getBonds() ) {
            if ( bond.order == order ) {
                result += 1;
            }
        }
        return result;
    }

    public static final RealMolecule WATER = new RealMolecule( "H2O", 2 ) {{
        // units in angstroms?
        Atom3D a = new Atom3D( Element.O, new ImmutableVector3D( 0, 0, 0 ) );
        Atom3D b = new Atom3D( Element.H, new ImmutableVector3D( 0.277400, 0.892900, 0.254400 ) );
        Atom3D c = new Atom3D( Element.H, new ImmutableVector3D( 0.606800, -0.238300, -0.716900 ) );

        addAtom( a );
        addAtom( b );
        addAtom( c );
        addBond( a, b, 1 );
        addBond( a, c, 1 );
    }};

    private static final RealMolecule[] MOLECULES = new RealMolecule[] {
            WATER
    };

    public static List<RealMolecule> getMatchingMolecules( MoleculeModel model ) {
        List<RealMolecule> result = new ArrayList<RealMolecule>();

        int lonePairs = model.getLonePairs().size();
        int singleBonds = 0;
        int doubleBonds = 0;
        int tripleBonds = 0;
        for ( PairGroup group : model.getBondedGroups() ) {
            switch( group.bondOrder ) {
                case 1:
                    singleBonds += 1;
                    break;
                case 2:
                    doubleBonds += 1;
                    break;
                case 3:
                    tripleBonds += 1;
                    break;
            }
        }

        for ( RealMolecule molecule : MOLECULES ) {
            if ( lonePairs == molecule.lonePairCount
                 && singleBonds == molecule.countBondsOfOrder( 1 )
                 && doubleBonds == molecule.countBondsOfOrder( 2 )
                 && tripleBonds == molecule.countBondsOfOrder( 3 ) ) {
                result.add( molecule );
            }
        }
        return result;
    }
}
