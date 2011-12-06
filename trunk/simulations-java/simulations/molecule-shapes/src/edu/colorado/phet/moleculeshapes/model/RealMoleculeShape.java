// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector3D;

import static edu.colorado.phet.chemistry.model.Element.*;

/**
 * Represents a "real" molecule with exact positions, as opposed to a molecule model (which is VSEPR-based
 * and doesn't include other information).
 * <p/>
 * We display these real molecules to the user in 3D
 */
public class RealMoleculeShape {

    private final String displayName;
    private List<Atom3D> atoms = new ArrayList<Atom3D>();
    private List<Bond<Atom3D>> bonds = new ArrayList<Bond<Atom3D>>();
    private Atom3D centralAtom = null;

    private RealMoleculeShape( String displayName ) {
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

    public Atom3D getCentralAtom() {
        return centralAtom;
    }

    public int getCentralLonePairCount() {
        return getCentralAtom().lonePairCount;
    }

    @Override public String toString() {
        return displayName;
    }

    public static final RealMoleculeShape HYDROGEN_FLUORIDE = new RealMoleculeShape( "HF" ) {{
        addCentralAtom( new Atom3D( F, new ImmutableVector3D( 0.325, 0, 0 ), 3 ) );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 1.242, 0, 0 ) ), 1 );
        centerOnCentroid();
    }};

    public static final RealMoleculeShape CARBON_DIOXIDE = new RealMoleculeShape( "CO2" ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -1.19700, 0, 0 ) ), 2 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 1.19700, 0, 0 ) ), 2 );
    }};

    public static final RealMoleculeShape BORON_TRIFLUORIDE = new RealMoleculeShape( "BF3" ) {{
        addCentralAtom( new Atom3D( B, new ImmutableVector3D( 2.866, 0.25, 0 ) ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 3.732, 0.75, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 2, 0.75, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 2.866, -0.75, 0 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMoleculeShape WATER = new RealMoleculeShape( "H2O" ) {{
        addCentralAtom( new Atom3D( O, new ImmutableVector3D(), 2 ) );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.277400, 0.892900, 0.254400 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.606800, -0.238300, -0.716900 ) ), 1 );
    }};

    public static final RealMoleculeShape AMMONIA = new RealMoleculeShape( "NH3" ) {{
        addCentralAtom( new Atom3D( N, new ImmutableVector3D(), 1 ) );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( -0.4417, 0.2706, 0.8711 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.7256, 0.6896, -0.1907 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.4875, -0.8701, 0.2089 ) ), 1 );
    }};

    public static final RealMoleculeShape METHANE = new RealMoleculeShape( "CH4" ) {{
        addCentralAtom( new Atom3D( C, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.554100, 0.799600, 0.496500 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( 0.683300, -0.813400, -0.253600 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( -0.778200, -0.373500, 0.669200 ) ), 1 );
        addRadialAtom( new Atom3D( H, new ImmutableVector3D( -0.459300, 0.387400, -0.912100 ) ), 1 );
    }};

    public static final RealMoleculeShape BERYLLIUM_CHLORIDE = new RealMoleculeShape( "BeCl2" ) {{ // TODO: more accurate numbers?
        addCentralAtom( new Atom3D( Be, new ImmutableVector3D() ) );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( 1.220000, 0, 0 ), 3 ), 1 );
        addRadialAtom( new Atom3D( Cl, new ImmutableVector3D( -1.220000, 0, 0 ), 3 ), 1 );
    }};

    public static final RealMoleculeShape SULFUR_TETRAFLUORIDE = new RealMoleculeShape( "SF4" ) {{
        addCentralAtom( new Atom3D( S, new ImmutableVector3D( -0.504000, 0.358000, 0.168000 ), 1 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.791000, -1.156000, 0.221000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.913000, 0.980000, 0.235000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.605000, 0.317000, -1.471000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.429000, 0.386000, 1.809000 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMoleculeShape CHLORINE_TRIFLUORIDE = new RealMoleculeShape( "ClF3" ) {{
        addCentralAtom( new Atom3D( Cl, new ImmutableVector3D( 4.416000, 2.322000, 3.301000 ), 2 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 4.550000, 3.874000, 4.061000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 3.250000, 1.846000, 4.482000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 4.282000, 0.770000, 2.544000 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMoleculeShape XENON_DIFLUORIDE = new RealMoleculeShape( "XeF2" ) {{
        addCentralAtom( new Atom3D( Xe, new ImmutableVector3D( -0.206000, 0.054000, 0.119000 ), 3 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.206000, 0.054000, 2.149000 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -0.206000, 0.054000, -1.911000 ) ), 1 );
        centerOnCentralAtom();
    }};
    public static final RealMoleculeShape SULFUR_HEXAFLUORIDE = new RealMoleculeShape( "SF6" ) {{
        addCentralAtom( new Atom3D( S, new ImmutableVector3D( -1.679000, -0.674000, -1.012000 ) ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.679000, -2.433000, -1.012000 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.080000, -0.674000, -1.012000 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.679000, -0.674000, -2.771000 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.679000, 1.085000, -1.012000 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -3.438000, -0.674000, -1.012000 ), 3 ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.679000, -0.674000, 0.748000 ), 3 ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMoleculeShape BROMINE_PENTAFLUORIDE = new RealMoleculeShape( "BrF5" ) {{
        addCentralAtom( new Atom3D( B, new ImmutableVector3D( 0.042300, 0.073900, 0.191700 ), 1 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 1.440000, -0.261000, 1.322100 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 0.168400, -1.700800, -0.222300 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.332400, 0.086700, -1.013900 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( -1.107800, -0.483500, 1.499300 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 1.240300, 0.306100, -1.170200 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMoleculeShape SULFUR_DIOXIDE = new RealMoleculeShape( "SO2" ) {{
        addCentralAtom( new Atom3D( S, new ImmutableVector3D( 0.000000, -0.577400, 0.000000 ), 1 ) );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( 1.309100, 0.288700, 0.000000 ) ), 2 );
        addRadialAtom( new Atom3D( O, new ImmutableVector3D( -1.309100, 0.288700, 0.000000 ) ), 2 );
        centerOnCentralAtom();
    }};

    public static final RealMoleculeShape XENON_TETRAFLUORIDE = new RealMoleculeShape( "XeF4" ) {{
        addCentralAtom( new Atom3D( Xe, new ImmutableVector3D( 2.7071, 0, 0 ), 2 ) );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 3.4142, 0.7071, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 2, -0.7071, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 2, 0.7071, 0 ) ), 1 );
        addRadialAtom( new Atom3D( F, new ImmutableVector3D( 3.4142, -0.7071, 0 ) ), 1 );
        centerOnCentralAtom();
    }};

    public static final RealMoleculeShape[] TAB_2_MOLECULES = new RealMoleculeShape[] {
            BERYLLIUM_CHLORIDE,
            BORON_TRIFLUORIDE,
            BROMINE_PENTAFLUORIDE,
            METHANE,
            CHLORINE_TRIFLUORIDE,
            CARBON_DIOXIDE,
            HYDROGEN_FLUORIDE,
            WATER,
            AMMONIA,
            // TODO: add PCl5
            SULFUR_TETRAFLUORIDE,
            SULFUR_HEXAFLUORIDE,
            SULFUR_DIOXIDE,
            XENON_DIFLUORIDE,
            XENON_TETRAFLUORIDE
    };
}
