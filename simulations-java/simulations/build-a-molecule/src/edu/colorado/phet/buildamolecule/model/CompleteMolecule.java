// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

import org.jmol.api.JmolViewer;

import edu.colorado.phet.buildamolecule.BuildAMoleculeResources;
import edu.colorado.phet.chemistry.model.Atomic;
import edu.colorado.phet.chemistry.molecules.*;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.*;

/**
 * Represents a complete (stable) molecule with a name and structure. Includes 2d and 3d representations, and can generate visuals of both types.
 */
public class CompleteMolecule {
    private String commonName; // as said by pubchem (or overridden)
    private String molecularFormula; // as said by pubchem
    private MoleculeStructure moleculeStructure;

    // more advanced molecule support. primarily for 3d display
    private AtomWrapper[] atomWrappers;
    private BondWrapper[] bondWrappers;

    public final int cid;

    // nodes listed so we can construct them with reflection TODO: auto-construction of nodes like the default case, but tuned?
    private static final Class[] nodeClasses = new Class[] {
            Cl2Node.class, CO2Node.class, CO2Node.class, CS2Node.class, F2Node.class, H2Node.class, N2Node.class, NONode.class, N2ONode.class,
            O2Node.class, C2H2Node.class, C2H4Node.class, C2H5ClNode.class, C2H5OHNode.class, C2H6Node.class, CH2ONode.class, CH3OHNode.class,
            CH4Node.class, H2ONode.class, H2SNode.class, HClNode.class, HFNode.class, NH3Node.class, NO2Node.class, OF2Node.class, P4Node.class,
            PCl3Node.class, PCl5Node.class, PF3Node.class, PH3Node.class, SO2Node.class, SO3Node.class
    };

    /**
     * Construct a molecule out of a pipe-separated line.
     *
     * @param line
     */
    public CompleteMolecule( String line ) {
        StringTokenizer t = new StringTokenizer( line, "|" );

        // read common name first
        commonName = t.nextToken();

        // molecular formula
        molecularFormula = t.nextToken();

        // # of atoms
        int atomCount = Integer.parseInt( t.nextToken() );

        // # of bonds
        int bondCount = Integer.parseInt( t.nextToken() );
        moleculeStructure = new MoleculeStructure();

        // for each atom, read its symbol, then 2d coordinates, then 3d coordinates (total of 6 fields)
        atomWrappers = new AtomWrapper[atomCount];
        for ( int i = 0; i < atomCount; i++ ) {
            String symbol = t.nextToken();
            float x2d = Float.parseFloat( t.nextToken() );
            float y2d = Float.parseFloat( t.nextToken() );
            float x3d = Float.parseFloat( t.nextToken() );
            float y3d = Float.parseFloat( t.nextToken() );
            float z3d = Float.parseFloat( t.nextToken() );
            Atomic atom = AtomModel.createAtomBySymbol( symbol );
            moleculeStructure.addAtom( atom );
            atomWrappers[i] = new AtomWrapper( x2d, y2d, x3d, y3d, z3d, atom );
        }

        // for each bond, read atom indices (2 of them, which are 1-indexed), and then the order of the bond (single, double, triple, etc.)
        bondWrappers = new BondWrapper[bondCount];
        for ( int i = 0; i < bondCount; i++ ) {
            int a = Integer.parseInt( t.nextToken() );
            int b = Integer.parseInt( t.nextToken() );
            int order = Integer.parseInt( t.nextToken() );
            MoleculeStructure.Bond bond = new MoleculeStructure.Bond( atomWrappers[a - 1].atom, atomWrappers[b - 1].atom ); // -1 since our format is 1-based
            moleculeStructure.addBond( bond );
            bondWrappers[i] = new BondWrapper( a, b, bond, order );
        }

        cid = Integer.parseInt( t.nextToken() );
    }

    public String getCommonName() {
        String ret = commonName;
        if ( ret.startsWith( "molecular " ) ) {
            ret = ret.substring( "molecular ".length() );
        }
        return capitalize( ret );
    }

    /**
     * @return A translated display name if possible. This does a weird lookup so that we can only list some of the names in the translation, but can
     *         accept an even larger number of translated names in a translation file
     */
    public String getDisplayName() {
        // first check if we have it translated. do NOT warn on missing
        String lookupKey = "molecule." + commonName.replace( ' ', '_' );
        String stringLookup = BuildAMoleculeResources.getResourceLoader().getLocalizedProperties().getString( lookupKey, false );

        // we need to check whether it came back the same as the key due to how getString works.
        if ( stringLookup != null && !stringLookup.equals( lookupKey ) ) {
            return stringLookup;
        }
        else {
            // if we didn't find it, pull it from our English data
            return getCommonName();
        }
    }

    private String capitalize( String str ) {
        char[] characters = str.toCharArray();
        boolean lastWasSpace = true;
        for ( int i = 0; i < characters.length; i++ ) {
            char character = characters[i];
            if ( Character.isWhitespace( character ) ) {
                lastWasSpace = true;
            }
            else {
                if ( lastWasSpace && Character.isLetter( character ) && Character.isLowerCase( character ) ) {
                    characters[i] = Character.toUpperCase( character );
                }
                lastWasSpace = false;
            }
        }
        return String.valueOf( characters );
    }

    public MoleculeStructure getMoleculeStructure() {
        return moleculeStructure;
    }

    /**
     * @return An XML CML string for our 3D representation
     */
    public String getCmlData() {
        String ret = "<?xml version=\"1.0\"?>\n" +
                     "<molecule id=\"" + commonName + "\" xmlns=\"http://www.xml-cml.org/schema\">";
        ret += "<name>" + commonName + "</name>";
        ret += "<atomArray>";
        for ( int i = 0; i < atomWrappers.length; i++ ) {
            AtomWrapper atomWrapper = atomWrappers[i];
            // TODO: include the formal charge possibly later, if Jmol can show it?
            ret += "<atom id=\"a" + ( i + 1 ) + "\" elementType=\"" + atomWrapper.atom.getSymbol() + "\" x3=\"" + atomWrapper.x3d + "\" y3=\"" + atomWrapper.y3d + "\" z3=\"" + atomWrapper.z3d + "\"/>";
        }
        ret += "</atomArray>";
        ret += "<bondArray>";
        for ( BondWrapper bondWrapper : bondWrappers ) {
            ret += "<bond atomRefs2=\"a" + bondWrapper.a + " a" + bondWrapper.b + "\" order=\"" + bondWrapper.order + "\"/>";
        }
        ret += "</bondArray>";
        ret += "</molecule>";
        return ret;
    }

    /**
     * Coloring scripts based on http://jmol.sourceforge.net/scripting/
     *
     * @param viewer Jmol viewer with molecule initialized from getCmlData()
     */
    public void fixJmolColors( JmolViewer viewer ) {
        for ( int i = 0; i < atomWrappers.length; i++ ) {
            Color color = atomWrappers[i].atom.getColor();

            viewer.script( "select a" + ( i + 1 ) + ";  color [" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "];" );
        }

        // set our selection back to everything
        viewer.script( "select all;" );
    }

    /**
     * @return A node that represents a 2d but quasi-3D version
     */
    public PNode createPseudo3DNode() {
        // if we can find it in the common chemistry nodes, use that
        for ( Class nodeClass : nodeClasses ) {
            if ( nodeClass.getSimpleName().equals( molecularFormula + "Node" ) || ( nodeClass == NH3Node.class && molecularFormula.equals( "H3N" ) ) ) {
                try {
                    return (PNode) nodeClass.getConstructors()[0].newInstance();
                }
                catch ( InstantiationException e ) {
                    e.printStackTrace();
                }
                catch ( IllegalAccessException e ) {
                    e.printStackTrace();
                }
                catch ( InvocationTargetException e ) {
                    e.printStackTrace();
                }
            }
        }

        // otherwise, use our 2d positions to construct a version. we get the correct back-to-front rendering
        return new PNode() {{
            List<AtomWrapper> wrappers = new LinkedList<AtomWrapper>( Arrays.asList( atomWrappers ) );

            // sort by Z-depth in 3D
            Collections.sort( wrappers, new Comparator<AtomWrapper>() {
                public int compare( AtomWrapper a, AtomWrapper b ) {
                    return ( new Float( a.z3d ) ).compareTo( b.z3d );
                }
            } );

            for ( final AtomWrapper atomWrapper : wrappers ) {
                addChild( new AtomNode( atomWrapper.atom ) {{
                    setOffset( atomWrapper.x2d * 15, atomWrapper.y2d * 15 ); // custom scale for now.
                }} );
            }
        }};
    }

    private static class AtomWrapper {
        // 2d coordinates
        public final float x2d;
        public final float y2d;

        // 3d coordinates
        public final float x3d;
        public final float y3d;
        public final float z3d;

        // our atom
        public final Atomic atom;

        private AtomWrapper( float x2d, float y2d, float x3d, float y3d, float z3d, Atomic atom ) {
            this.x2d = x2d;
            this.y2d = y2d;
            this.x3d = x3d;
            this.y3d = y3d;
            this.z3d = z3d;
            this.atom = atom;
        }
    }

    private static class BondWrapper {
        public int a;
        public int b;
        public final MoleculeStructure.Bond bond;
        public final int order;

        private BondWrapper( int a, int b, MoleculeStructure.Bond bond, int order ) {
            this.a = a;
            this.b = b;
            this.bond = bond;
            this.order = order;
        }
    }
}
