// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.moleculeshapes.math.ImmutableVector3D;

import static edu.colorado.phet.chemistry.model.Element.*;

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
        addCentralAtom( new Atom3D( C, new ImmutableVector3D( -0.528500, 0, 0 ) ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 0.528500, 0, 0 ) ), 3 );
    }};

    public static final RealMolecule CARBON_DIOXIDE = new RealMolecule( "CO2", 0 ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -1.19700, 0, 0 ) ), 2 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 1.19700, 0, 0 ) ), 2 );
    }};

    public static final RealMolecule BORON_TRIFLUORIDE = new RealMolecule( "BF3", 0 ) {{
        addCentralAtom( new Atom3D( B, new ImmutableVector3D( 2.866, 0.25, 0 ) ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 3.732, 0.75, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 2, 0.75, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 2.866, -0.75, 0 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule NITRATE = new RealMolecule( "NO3-", 0 ) {{
        addCentralAtom( new Atom3D( N, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 1.227900, 0.187600, 0 ) ), 1 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.451500, -1.157200, 0 ) ), 1 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.776400, 0.969600, 0 ) ), 2 );
    }};

    public static final RealMolecule FORMALDEHYDE = new RealMolecule( "CH2O", 0 ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D( 2.5369, 0.06, 0 ) ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 3.403, 0.56, 0 ) ), 2 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 2, 0.37, 0 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 2.5369, -0.56, 0 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule WATER = new RealMolecule( "H2O", 2 ) {{
        addCentralAtom( new Atom3D( O, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.277400, 0.892900, 0.254400 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.606800, -0.238300, -0.716900 ) ), 1 );
    }};

    public static final RealMolecule AMMONIA = new RealMolecule( "NH3", 1 ) {{
        addCentralAtom( new Atom3D( N, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( -0.4417, 0.2706, 0.8711 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.7256, 0.6896, -0.1907 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.4875, -0.8701, 0.2089 ) ), 1 );
    }};

    public static final RealMolecule METHANE = new RealMolecule( "CH4", 0 ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.554100, 0.799600, 0.496500 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.683300, -0.813400, -0.253600 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( -0.778200, -0.373500, 0.669200 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( -0.459300, 0.387400, -0.912100 ) ), 1 );
    }};

    public static final RealMolecule DICHLORODIFLUOROMETHANE = new RealMolecule( "CCl2F2", 0 ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D( 0, 0.0728, 0 ) ) );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( -1.459400, -0.920800, 0 ) ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 1.459200, -0.921000, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0, 0.884300, 1.088700 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0, 0.884300, -1.088700 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule HYDROGEN_CYANIDE = new RealMolecule( "HCN", 0 ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D( 0.58, 0, 0 ) ) );
        addRadialAtom( new Atom3D( N, new ImmutableVector3D( -0.58, 0, 0 ) ), 3 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 1.645, 0, 0 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule BERYLLIUM_FLUORIDE = new RealMolecule( "BeF2", 0 ) {{ // TODO: more accurate numbers?
        addCentralAtom( new Atom3D( Be, new ImmutableVector3D( 2.866, 0, 0 ) ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 3.732, 0, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 2.00, 0, 0 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule CARBON_DISULFATE = new RealMolecule( "CS2", 0 ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( S, new ImmutableVector3D( 1.5904, 0, 0 ) ), 2 );
        addRadialAtom( new Atom3D( S, new ImmutableVector3D( -1.5904, 0, 0 ) ), 2 );
    }};

    public static final RealMolecule SULFUR_TRIOXIDE = new RealMolecule( "SO3", 0 ) {{
        addCentralAtom( new Atom3D( S, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.6552, -1.2949, 0 ) ), 2 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.7939, 1.2148, 0 ) ), 2 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 1.4491, 0.0801, 0 ) ), 2 );
    }};

    public static final RealMolecule SILICON_TETRACHLORIDE = new RealMolecule( "SiCl4", 0 ) {{
        addCentralAtom( new Atom3D( Element.Si, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 0.256300, -1.355900, 1.487200 ) ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 1.450400, 1.414200, 0.116900 ) ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( -1.819200, 0.878500, 0.191500 ) ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 0.112700, -0.937200, -1.795700 ) ), 1 );
    }};

    public static final RealMolecule PHOSPHORUS_TRIFLUORIDE = new RealMolecule( "PF3", 1 ) {{
        addCentralAtom( new Atom3D( P, new ImmutableVector3D( 0, 0, 0.6224 ) ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.9872, -0.9041, -0.2075 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.2894, 1.3069, -0.2075 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.2766, -0.4029, -0.2075 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule FLUORINE_MONOXIDE = new RealMolecule( "OF2", 2 ) {{
        addCentralAtom( new Atom3D( O, new ImmutableVector3D( 0, -0.5366, 0 ) ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.1592, 0.2683, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 1.1592, 0.2683, 0 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule PHOSPHORUS_PENTAFLUORIDE = new RealMolecule( "PF5", 0 ) {{
        addCentralAtom( new Atom3D( P, new ImmutableVector3D( -0.532000, 0.324000, 0.000000 ) ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.532000, -1.210000, 0.000000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.797000, 1.091000, 0.000000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.861000, 1.091000, 0.000000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.532000, 0.324000, -1.577000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.532000, 0.324000, 1.577000 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule SULFUR_OXIDE_TETRAFLUORIDE = new RealMolecule( "SOF4", 0 ) {{
        addCentralAtom( new Atom3D( S, new ImmutableVector3D( -0.091000, -0.017700, -0.350800 ) ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.000000, 1.740000, 0.000000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.507000, -0.870000, 0.000000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.000000, 0.000000, 1.740000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.000000, 0.000000, -1.740000 ) ), 1 );

        centerOnCentralAtom();
    }};

//    public static final RealMolecule SULFUR_TETRAFLUORIDE = new RealMolecule( "SF4", 1 ) {{
//        addCentralAtom( new Atom3D( S, new ImmutableVector3D() ) );
//        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.487900, 0.881900, 1.212500 ) ), 1 );
//        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.725700, -1.288900, 0.546000 ) ), 1 );
//        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.004000, 0.833100, -0.885500 ) ), 1 );
//        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 1.241900, -0.426100, -0.873100 ) ), 1 );
//    }};

    public static final RealMolecule XENON_DIOXYDIFLUORIDE = new RealMolecule( "XeO2F2", 1 ) {{
        addCentralAtom( new Atom3D( Element.Xe, new ImmutableVector3D( -1.187300, 24.900400, 1.887500 ) ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.149800, 23.847900, 2.747800 ) ), 2 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -2.481700, 24.586800, 3.242800 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.121100, 24.918100, 0.509900 ) ), 1 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -2.126800, 23.895900, 0.871300 ) ), 2 );
        centerOnCentralAtom();
    }};

//    public static final RealMolecule IODINE_TETRAFLUORIDE = new RealMolecule( "IF4-", 1 ) {{
//        addCentralAtom( new Atom3D( I, new ImmutableVector3D( 3.981300, 1.196400, 0.737100 ) ) );
//        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.166000, 1.854000, -0.066000 ) ), 1 );
//        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.567000, -0.841000, 0.014000 ) ), 1 );
//        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.101000, 0.161000, 1.752000 ) ), 1 );
//        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.076000, 0.043000, -1.765000 ) ), 1 );
//
//        centerOnCentralAtom();
//    }};

    public static final RealMolecule BROMINE_TRIFLUORIDE = new RealMolecule( "BrF3", 2 ) {{
        addCentralAtom( new Atom3D( Br, new ImmutableVector3D( 0.596100, 0.310400, 0.075500 ) ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.017000, -0.284600, 0.124100 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.147700, 1.942000, -0.170900 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 1.103100, -1.408600, 0.328900 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule CHLORINE_TRIFLUORIDE = new RealMolecule( "ClF3", 2 ) {{
        addCentralAtom( new Atom3D( Cl, new ImmutableVector3D( 4.416000, 2.322000, 3.301000 ) ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 4.550000, 3.874000, 4.061000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 3.250000, 1.846000, 4.482000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 4.282000, 0.770000, 2.544000 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule XENON_DIFLUORIDE = new RealMolecule( "XeF2", 3 ) {{
        addCentralAtom( new Atom3D( Xe, new ImmutableVector3D( -0.206000, 0.054000, 0.119000 ) ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.206000, 0.054000, 2.149000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.206000, 0.054000, -1.911000 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule TRIIODIDE = new RealMolecule( "I3", 3 ) {{
        addCentralAtom( new Atom3D( I, new ImmutableVector3D( 0.076000, 0.044000, 0.000000 ) ) );
        addRadialAtom( new Atom3D( I, new ImmutableVector3D( 0.076000, 0.044000, 2.660000 ) ), 1 );
        addRadialAtom( new Atom3D( I, new ImmutableVector3D( 0.076000, 0.044000, -2.660000 ) ), 1 );
        centerOnCentralAtom();
    }};

    private static final RealMolecule[] MOLECULES = new RealMolecule[] {
            CARBON_MONOXIDE, CARBON_DIOXIDE, WATER, BORON_TRIFLUORIDE, NITRATE,
            FORMALDEHYDE, AMMONIA, METHANE, DICHLORODIFLUOROMETHANE, HYDROGEN_CYANIDE,
            BERYLLIUM_FLUORIDE, CARBON_DISULFATE, SULFUR_TRIOXIDE, SILICON_TETRACHLORIDE,
            PHOSPHORUS_TRIFLUORIDE, FLUORINE_MONOXIDE, PHOSPHORUS_PENTAFLUORIDE, SULFUR_OXIDE_TETRAFLUORIDE,
//            SULFUR_TETRAFLUORIDE,
            XENON_DIOXYDIFLUORIDE,
//            IODINE_TETRAFLUORIDE,
            BROMINE_TRIFLUORIDE,
            CHLORINE_TRIFLUORIDE, XENON_DIFLUORIDE, TRIIODIDE
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
