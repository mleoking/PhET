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

    private Atom3D centralAtom = null;

    private RealMolecule( String displayName, int lonePairCount ) {
        this.displayName = displayName;
        this.lonePairCount = lonePairCount;
    }

    public void addCentralAtom( Atom3D atom ) {
        addAtom( atom );
        centralAtom = atom;
    }

    public void addRadialAtom( Atom3D atom, int bondOrder ) {
        addAtom( atom );
        addBond( atom, centralAtom, bondOrder );
    }

    public void centerOnCentralAtom() {
        translate( centralAtom.position.get().negated() );
    }

    private void translate( ImmutableVector3D offset ) {
        for ( Atom3D atom : getAtoms() ) {
            atom.position.set( atom.position.get().plus( offset ) );
        }
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

    public static final RealMolecule CARBON_MONOXIDE = new RealMolecule( "CO", 1 ) {{
        addCentralAtom( new Atom3D( Element.C, new ImmutableVector3D( -0.528500, 0, 0 ) ) );
        addRadialAtom( new Atom3D( Element.O, new ImmutableVector3D( 0.528500, 0, 0 ) ), 3 );
    }};

    public static final RealMolecule CARBON_DIOXIDE = new RealMolecule( "CO2", 0 ) {{
        addCentralAtom( new Atom3D( Element.C, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( Element.O, new ImmutableVector3D( -1.19700, 0, 0 ) ), 2 );
        addRadialAtom( new Atom3D( Element.O, new ImmutableVector3D( 1.19700, 0, 0 ) ), 2 );
    }};

    public static final RealMolecule BORON_TRIFLUORIDE = new RealMolecule( "BF3", 0 ) {{
        addCentralAtom( new Atom3D( Element.B, new ImmutableVector3D( 2.866, 0.25, 0 ) ) );
        addRadialAtom( new Atom3D( Element.F, new ImmutableVector3D( 3.732, 0.75, 0 ) ), 1 );
        addRadialAtom( new Atom3D( Element.F, new ImmutableVector3D( 2, 0.75, 0 ) ), 1 );
        addRadialAtom( new Atom3D( Element.F, new ImmutableVector3D( 2.866, -0.75, 0 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule NITRATE = new RealMolecule( "NO3-", 0 ) {{
        addCentralAtom( new Atom3D( Element.N, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( Element.O, new ImmutableVector3D( 1.227900, 0.187600, 0 ) ), 1 );
        addRadialAtom( new Atom3D( Element.O, new ImmutableVector3D( -0.451500, -1.157200, 0 ) ), 1 );
        addRadialAtom( new Atom3D( Element.O, new ImmutableVector3D( -0.776400, 0.969600, 0 ) ), 2 );
    }};

    public static final RealMolecule FORMALDEHYDE = new RealMolecule( "CH2O", 0 ) {{
        addCentralAtom( new Atom3D( Element.C, new ImmutableVector3D( 2.5369, 0.06, 0 ) ) );
        addRadialAtom( new Atom3D( Element.O, new ImmutableVector3D( 3.403, 0.56, 0 ) ), 2 );
        addRadialAtom( new Atom3D( Element.H, new ImmutableVector3D( 2, 0.37, 0 ) ), 1 );
        addRadialAtom( new Atom3D( Element.H, new ImmutableVector3D( 2.5369, -0.56, 0 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule WATER = new RealMolecule( "H2O", 2 ) {{
        addCentralAtom( new Atom3D( Element.O, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( Element.H, new ImmutableVector3D( 0.277400, 0.892900, 0.254400 ) ), 1 );
        addRadialAtom( new Atom3D( Element.H, new ImmutableVector3D( 0.606800, -0.238300, -0.716900 ) ), 1 );
    }};

    // TODO: Beryllium Chloride is maybe the best linear example?

    private static final RealMolecule[] MOLECULES = new RealMolecule[] {
            CARBON_MONOXIDE, CARBON_DIOXIDE, WATER, BORON_TRIFLUORIDE, NITRATE, FORMALDEHYDE
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
