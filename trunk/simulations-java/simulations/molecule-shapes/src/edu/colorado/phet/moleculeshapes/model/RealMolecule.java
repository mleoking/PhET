// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;

import static edu.colorado.phet.chemistry.model.Element.*;

/**
 * Represents a "real" molecule with exact positions, as opposed to a molecule model (which is VSEPR-based
 * and doesn't include other information).
 * <p/>
 * We display these real molecules to the user in 3D
 */
public class RealMolecule {

    private final String displayName;
    private List<Atom3D> atoms = new ArrayList<Atom3D>();
    private List<Bond<Atom3D>> bonds = new ArrayList<Bond<Atom3D>>();
    private Atom3D centralAtom = null;

    private RealMolecule( String displayName ) {
        this.displayName = displayName;
    }

    public void addAtom( Atom3D atom ) {
        assert !atoms.contains( atom );
        atoms.add( atom );
    }

    public void addBond( Atom3D a, Atom3D b, int order ) {
        bonds.add( new Bond<Atom3D>( a, b, order ) );
    }

    public Collection<Atom3D> getAtoms() {
        return atoms;
    }

    public Collection<Bond<Atom3D>> getBonds() {
        return bonds;
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

    public void centerOnCentroid() {
        ImmutableVector3D centroid = new ImmutableVector3D();
        for ( Atom3D atom3D : getAtoms() ) {
            centroid = centroid.plus( atom3D.position.get() );
        }
        translate( centroid.times( -1 / (float) getAtoms().size() ) );
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

    public Atom3D getCentralAtom() {
        return centralAtom;
    }

    public int getCentralLonePairCount() {
        return getCentralAtom().lonePairCount;
    }

    @Override public String toString() {
        return displayName;
    }

    public static final RealMolecule CARBON_MONOXIDE = new RealMolecule( "CO" ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D( -0.528500, 0, 0 ), 1 ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 0.528500, 0, 0 ) ), 3 );
        centerOnCentroid();
    }};

    public static final RealMolecule MOLECULAR_OXYGEN = new RealMolecule( "O2" ) {{
        addCentralAtom( new Atom3D( O, new ImmutableVector3D( -0.864000, 0.116000, 0.000000 ), 2 ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 0.292000, 0.477000, 0.000000 ) ), 2 );
        centerOnCentroid();
    }};
    public static final RealMolecule MOLECULAR_HYDROGEN = new RealMolecule( "H2" ) {{
        addCentralAtom( new Atom3D( H, new ImmutableVector3D( -0.390000, 0.178000, -0.444000 ) ) );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.014000, 0.318000, 0.160000 ) ), 1 );

        centerOnCentroid();
    }};

    public static final RealMolecule HYDROGEN_FLUORIDE = new RealMolecule( "HF" ) {{
        addCentralAtom( new Atom3D( F, new ImmutableVector3D( 0.325, 0, 0 ), 3 ) );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 1.242, 0, 0 ) ), 1 );
        centerOnCentroid();
    }};

    public static final RealMolecule HYDROGEN_CHLORIDE = new RealMolecule( "HCl" ) {{
        addCentralAtom( new Atom3D( Cl, new ImmutableVector3D( -0.967000, 0.227000, 0.000000 ), 3 ) );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.246000, 0.620000, 0.000000 ) ), 1 );
        centerOnCentroid();
    }};

    public static final RealMolecule CARBON_DIOXIDE = new RealMolecule( "CO2" ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -1.19700, 0, 0 ) ), 2 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 1.19700, 0, 0 ) ), 2 );
    }};

    public static final RealMolecule BORON_TRIFLUORIDE = new RealMolecule( "BF3" ) {{
        addCentralAtom( new Atom3D( B, new ImmutableVector3D( 2.866, 0.25, 0 ) ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 3.732, 0.75, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 2, 0.75, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 2.866, -0.75, 0 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule NITRATE = new RealMolecule( "NO3<sup>-</sup>" ) {{
        addCentralAtom( new Atom3D( N, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 1.227900, 0.187600, 0 ) ), 1 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.451500, -1.157200, 0 ) ), 1 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.776400, 0.969600, 0 ) ), 2 );
    }};

    public static final RealMolecule FORMALDEHYDE = new RealMolecule( "CH2O" ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D( -0.237000, 1.147000, -0.000000 ) ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.237000, -0.080000, 0.000000 ) ), 2 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.702000, 1.739000, -0.000000 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( -1.176000, 1.739000, 0.000000 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule WATER = new RealMolecule( "H2O" ) {{
        addCentralAtom( new Atom3D( O, new ImmutableVector3D(), 2 ) );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.277400, 0.892900, 0.254400 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.606800, -0.238300, -0.716900 ) ), 1 );
    }};

    public static final RealMolecule AMMONIA = new RealMolecule( "NH3" ) {{
        addCentralAtom( new Atom3D( N, new ImmutableVector3D(), 1 ) );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( -0.4417, 0.2706, 0.8711 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.7256, 0.6896, -0.1907 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.4875, -0.8701, 0.2089 ) ), 1 );
    }};

    public static final RealMolecule METHANE = new RealMolecule( "CH4" ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.554100, 0.799600, 0.496500 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.683300, -0.813400, -0.253600 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( -0.778200, -0.373500, 0.669200 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( -0.459300, 0.387400, -0.912100 ) ), 1 );
    }};

    public static final RealMolecule DICHLORODIFLUOROMETHANE = new RealMolecule( "CCl2F2" ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D( 0, 0.0728, 0 ) ) );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( -1.459400, -0.920800, 0 ) ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 1.459200, -0.921000, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0, 0.884300, 1.088700 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0, 0.884300, -1.088700 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule HYDROGEN_CYANIDE = new RealMolecule( "HCN" ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D( 0.58, 0, 0 ) ) );
        addRadialAtom( new Atom3D( N, new ImmutableVector3D( -0.58, 0, 0 ) ), 3 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 1.645, 0, 0 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule BERYLLIUM_CHLORIDE = new RealMolecule( "BeCl2" ) {{ // TODO: more accurate numbers?
        addCentralAtom( new Atom3D( Be, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 1.220000, 0, 0 ) ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( -1.220000, 0, 0 ) ), 1 );
    }};

    public static final RealMolecule CARBON_DISULFATE = new RealMolecule( "CS2" ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( S, new ImmutableVector3D( 1.5904, 0, 0 ) ), 2 );
        addRadialAtom( new Atom3D( S, new ImmutableVector3D( -1.5904, 0, 0 ) ), 2 );
    }};

    public static final RealMolecule SULFUR_TRIOXIDE = new RealMolecule( "SO3" ) {{
        addCentralAtom( new Atom3D( S, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.6552, -1.2949, 0 ) ), 2 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.7939, 1.2148, 0 ) ), 2 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 1.4491, 0.0801, 0 ) ), 2 );
    }};

    public static final RealMolecule SILICON_TETRACHLORIDE = new RealMolecule( "SiCl4" ) {{
        addCentralAtom( new Atom3D( Element.Si, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 0.256300, -1.355900, 1.487200 ) ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 1.450400, 1.414200, 0.116900 ) ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( -1.819200, 0.878500, 0.191500 ) ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 0.112700, -0.937200, -1.795700 ) ), 1 );
    }};

    public static final RealMolecule PHOSPHORUS_TRIFLUORIDE = new RealMolecule( "PF3" ) {{
        addCentralAtom( new Atom3D( P, new ImmutableVector3D( 0, 0, 0.6224 ), 1 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.9872, -0.9041, -0.2075 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.2894, 1.3069, -0.2075 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.2766, -0.4029, -0.2075 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule FLUORINE_MONOXIDE = new RealMolecule( "OF2" ) {{
        addCentralAtom( new Atom3D( O, new ImmutableVector3D( 0, -0.5366, 0 ), 2 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.1592, 0.2683, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 1.1592, 0.2683, 0 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule PHOSPHORUS_PENTAFLUORIDE = new RealMolecule( "PF5" ) {{
        addCentralAtom( new Atom3D( P, new ImmutableVector3D( -0.532000, 0.324000, 0.000000 ) ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.532000, -1.210000, 0.000000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.797000, 1.091000, 0.000000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.861000, 1.091000, 0.000000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.532000, 0.324000, -1.577000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.532000, 0.324000, 1.577000 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule SULFUR_OXIDE_TETRAFLUORIDE = new RealMolecule( "SOF4" ) {{
        addCentralAtom( new Atom3D( S, new ImmutableVector3D( -0.091000, -0.017700, -0.350800 ) ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.028900, 1.873900, -0.093300 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.153000, -1.909200, -0.608300 ) ), 1 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 0.492500, 0.210200, -2.165700 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.956100, -0.012500, 0.060500 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 1.193800, -0.249600, 1.043400 ) ), 1 );

        centerOnCentralAtom();
    }};

    public static final RealMolecule SULFUR_TETRAFLUORIDE = new RealMolecule( "SF4" ) {{
        addCentralAtom( new Atom3D( S, new ImmutableVector3D( -0.504000, 0.358000, 0.168000 ), 1 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.791000, -1.156000, 0.221000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.913000, 0.980000, 0.235000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.605000, 0.317000, -1.471000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.429000, 0.386000, 1.809000 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule XENON_DIOXYDIFLUORIDE = new RealMolecule( "XeO2F2" ) {{
        addCentralAtom( new Atom3D( Element.Xe, new ImmutableVector3D( -1.187300, 24.900400, 1.887500 ), 1 ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.149800, 23.847900, 2.747800 ) ), 2 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -2.481700, 24.586800, 3.242800 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.121100, 24.918100, 0.509900 ) ), 1 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -2.126800, 23.895900, 0.871300 ) ), 2 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule BROMINE_TRIFLUORIDE = new RealMolecule( "BrF3" ) {{
        addCentralAtom( new Atom3D( Br, new ImmutableVector3D( 0.596100, 0.310400, 0.075500 ), 2 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.017000, -0.284600, 0.124100 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.147700, 1.942000, -0.170900 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 1.103100, -1.408600, 0.328900 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule CHLORINE_TRIFLUORIDE = new RealMolecule( "ClF3" ) {{
        addCentralAtom( new Atom3D( Cl, new ImmutableVector3D( 4.416000, 2.322000, 3.301000 ), 2 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 4.550000, 3.874000, 4.061000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 3.250000, 1.846000, 4.482000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 4.282000, 0.770000, 2.544000 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule XENON_DIFLUORIDE = new RealMolecule( "XeF2" ) {{
        addCentralAtom( new Atom3D( Xe, new ImmutableVector3D( -0.206000, 0.054000, 0.119000 ), 3 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.206000, 0.054000, 2.149000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.206000, 0.054000, -1.911000 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule TRIIODIDE = new RealMolecule( "I3" ) {{
        addCentralAtom( new Atom3D( I, new ImmutableVector3D( 0.076000, 0.044000, 0.000000 ), 3 ) );
        addRadialAtom( new Atom3D( I, new ImmutableVector3D( 0.076000, 0.044000, 2.660000 ) ), 1 );
        addRadialAtom( new Atom3D( I, new ImmutableVector3D( 0.076000, 0.044000, -2.660000 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule SULFUR_HEXAFLUORIDE = new RealMolecule( "SF6" ) {{
        addCentralAtom( new Atom3D( S, new ImmutableVector3D( -1.679000, -0.674000, -1.012000 ) ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.679000, -2.433000, -1.012000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.080000, -0.674000, -1.012000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.679000, -0.674000, -2.771000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.679000, 1.085000, -1.012000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -3.438000, -0.674000, -1.012000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.679000, -0.674000, 0.748000 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule BROMINE_PENTAFLUORIDE = new RealMolecule( "BrF5" ) {{
        addCentralAtom( new Atom3D( B, new ImmutableVector3D( 0.042300, 0.073900, 0.191700 ), 1 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 1.440000, -0.261000, 1.322100 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.168400, -1.700800, -0.222300 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.332400, 0.086700, -1.013900 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.107800, -0.483500, 1.499300 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 1.240300, 0.306100, -1.170200 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule STANNOUS_CHLORIDE = new RealMolecule( "SnCl2" ) {{
        addCentralAtom( new Atom3D( Element.Sn, new ImmutableVector3D( 2.866, -0.25, 0 ), 1 ) );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 3.732, 0.25, 0 ) ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 2, 0.25, 0 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule OZONE = new RealMolecule( "O3" ) {{
        addCentralAtom( new Atom3D( O, new ImmutableVector3D( -0.095000, -0.494300, 0.000000 ), 1 ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 1.148900, 0.215200, 0.000000 ) ), 1 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -1.054000, 0.279100, 0.000000 ) ), 2 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule SULFUR_DIOXIDE = new RealMolecule( "SO2" ) {{
        addCentralAtom( new Atom3D( S, new ImmutableVector3D( 0.000000, -0.577400, 0.000000 ), 1 ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 1.309100, 0.288700, 0.000000 ) ), 2 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -1.309100, 0.288700, 0.000000 ) ), 2 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule THIAZYL_FLUORIDE = new RealMolecule( "NSF" ) {{
        addCentralAtom( new Atom3D( S, new ImmutableVector3D( 0.129500, 0.636500, 0.000000 ), 1 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.180600, -0.262900, 0.000000 ) ), 1 );
        addRadialAtom( new Atom3D( N, new ImmutableVector3D( 1.051100, -0.373700, 0.000000 ) ), 3 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule CHLORITE = new RealMolecule( "ClO2<sup>-</sup>" ) {{
        addCentralAtom( new Atom3D( Cl, new ImmutableVector3D( 2.866, -0.25, 0 ), 2 ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 2, 0.25, 0 ) ), 1 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 3.732, 0.25, 0 ) ), 2 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule PHOSPHORYL_CHLORIDE = new RealMolecule( "POCl3" ) {{
        addCentralAtom( new Atom3D( P, new ImmutableVector3D( 0.000100, 0.000100, -0.172000 ) ) );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 0.428200, 1.785000, 0.615300 ) ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 1.331700, -1.264000, 0.614400 ) ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( -1.760700, -0.521900, 0.613700 ) ), 1 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 0.000700, 0.000800, -1.671400 ) ), 2 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule XENON_TETRAFLUORIDE = new RealMolecule( "XeF4" ) {{
        addCentralAtom( new Atom3D( Xe, new ImmutableVector3D( 2.7071, 0, 0 ), 2 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 3.4142, 0.7071, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 2, -0.7071, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 2, 0.7071, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 3.4142, -0.7071, 0 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule PERCHLORATE = new RealMolecule( "ClO4<sup>-</sup>" ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D( -0.023000, 0.014000, 0.171000 ) ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.023000, -1.334000, -0.305000 ) ), 2 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -1.191000, 0.688000, -0.306000 ) ), 2 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 1.145000, 0.688000, -0.307000 ) ), 2 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.023000, 0.015000, 1.601000 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule METHYL_CHLORIDE = new RealMolecule( "CH3Cl" ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D( -0.178000, -0.689000, 0.000000 ) ) );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( -0.178000, 1.052000, -0.000000 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.878000, -1.038000, -0.000000 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( -0.705000, -1.038000, 0.914000 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( -0.705000, -1.038000, -0.914000 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule XENON_OXYTETRAFLUORIDE = new RealMolecule( "XeOF4" ) {{
        addCentralAtom( new Atom3D( Xe, new ImmutableVector3D( -0.198000, 0.086000, -0.422000 ), 1 ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 0.218000, 0.026000, -2.070000 ) ), 2 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.919000, 0.746000, -0.881000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.854000, -1.694000, -0.523000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.458000, 1.866000, -0.321000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 1.524000, -0.575000, 0.036000 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule PHOSPHATE = new RealMolecule( "PO4<sup>3-</sup>" ) {{
        addCentralAtom( new Atom3D( P, new ImmutableVector3D( 0.000000, -0.000100, 0.000000 ) ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 1.523400, -0.068800, -0.197700 ) ), 1 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.640600, 0.718000, -1.199100 ) ), 1 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.567900, -1.424800, 0.107300 ) ), 1 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.314900, 0.775700, 1.289500 ) ), 2 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule SULFITE = new RealMolecule( "SO3<sup>2-</sup>" ) {{
        addCentralAtom( new Atom3D( S, new ImmutableVector3D( -0.341000, 0.348000, 0.302000 ), 1 ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.303000, -1.160000, 0.237000 ) ), 1 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 1.096000, 0.804000, 0.234000 ) ), 1 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.984000, 0.804000, -0.986000 ) ), 2 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule CHLORATE = new RealMolecule( "ClO3<sup>-</sup>" ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D( -0.633000, 0.293000, -2.002000 ), 1 ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -0.671000, -1.192000, -2.119000 ) ), 1 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 0.796000, 0.702000, -2.104000 ) ), 2 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -1.330000, 0.842000, -3.199000 ) ), 2 );
        centerOnCentralAtom();
    }};

    public static final RealMolecule HYDROXONIUM = new RealMolecule( "H3O<sup>+</sup>" ) {{
        addCentralAtom( new Atom3D( O, new ImmutableVector3D( -9.792000, 10.242000, 1.118000 ), 1 ) );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( -9.792000, 11.292000, 1.118000 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( -8.803000, 9.892000, 1.118000 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( -10.287000, 9.892000, 1.976000 ) ), 1 );
        centerOnCentralAtom();
    }};

    private static final RealMolecule[] MOLECULES = new RealMolecule[] {
            CARBON_MONOXIDE, CARBON_DIOXIDE, WATER, BORON_TRIFLUORIDE, NITRATE,
            FORMALDEHYDE, AMMONIA, METHANE, DICHLORODIFLUOROMETHANE, HYDROGEN_CYANIDE,
            BERYLLIUM_CHLORIDE, CARBON_DISULFATE, SULFUR_TRIOXIDE, SILICON_TETRACHLORIDE,
            PHOSPHORUS_TRIFLUORIDE, FLUORINE_MONOXIDE, PHOSPHORUS_PENTAFLUORIDE, SULFUR_OXIDE_TETRAFLUORIDE,
            SULFUR_TETRAFLUORIDE, XENON_DIOXYDIFLUORIDE, BROMINE_TRIFLUORIDE,
            CHLORINE_TRIFLUORIDE, XENON_DIFLUORIDE, TRIIODIDE, SULFUR_HEXAFLUORIDE,
            BROMINE_PENTAFLUORIDE, STANNOUS_CHLORIDE, OZONE, SULFUR_DIOXIDE, THIAZYL_FLUORIDE,
            CHLORITE, PHOSPHORYL_CHLORIDE, XENON_TETRAFLUORIDE, PERCHLORATE,
            MOLECULAR_OXYGEN, HYDROGEN_FLUORIDE, METHYL_CHLORIDE, HYDROGEN_CHLORIDE,
            XENON_OXYTETRAFLUORIDE, PHOSPHATE, SULFITE, CHLORATE, MOLECULAR_HYDROGEN, HYDROXONIUM
    };

    public static final RealMolecule[] TAB_2_MOLECULES = new RealMolecule[] {
            BERYLLIUM_CHLORIDE,
            BORON_TRIFLUORIDE,
            BROMINE_PENTAFLUORIDE,
            METHANE,
            CHLORINE_TRIFLUORIDE,
            // TODO: add CH2F2
            // TODO: add CF4
            CARBON_DIOXIDE,
            HYDROGEN_FLUORIDE,
            WATER,
            // TODO: add NCl3
            AMMONIA,
            // TODO: add PCl5
            SULFUR_TETRAFLUORIDE,
            SULFUR_HEXAFLUORIDE,
            SULFUR_DIOXIDE,
            XENON_DIFLUORIDE,
            XENON_TETRAFLUORIDE,
            // TODO: add XeOF5-
            PERCHLORATE
    };

    public static List<RealMolecule> getMatchingMolecules( MoleculeModel model ) {
        List<RealMolecule> result = new ArrayList<RealMolecule>();

        int lonePairs = model.getLonePairs().size();
        int singleBonds = 0;
        int doubleBonds = 0;
        int tripleBonds = 0;
        for ( PairGroup group : model.getBondedGroups() ) {

            //SRR Suspicious use of bond order, should this treat bonds equally?
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
            if ( lonePairs == molecule.getCentralLonePairCount()
                 && singleBonds == molecule.countBondsOfOrder( 1 )
                 && doubleBonds == molecule.countBondsOfOrder( 2 )
                 && tripleBonds == molecule.countBondsOfOrder( 3 ) ) {
                result.add( molecule );
            }
        }
        return result;
    }
}
