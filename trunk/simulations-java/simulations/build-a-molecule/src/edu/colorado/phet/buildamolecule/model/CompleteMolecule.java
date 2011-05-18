// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.model;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

import org.jmol.api.JmolViewer;

import edu.colorado.phet.buildamolecule.BuildAMoleculeResources;
import edu.colorado.phet.buildamolecule.model.CompleteMolecule.PubChemAtom;
import edu.colorado.phet.chemistry.model.Atom;
import edu.colorado.phet.chemistry.model.Element;
import edu.colorado.phet.chemistry.molecules.*;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.*;

/**
 * Represents a complete (stable) molecule with a name and structure. Includes 2d and 3d representations,
 * and can generate visuals of both types.
 */
public class CompleteMolecule extends MoleculeStructure<PubChemAtom> {
    private String commonName; // as said by pubchem (or overridden)
    private String molecularFormula; // as said by pubchem

    public int cid; // TODO: if we can, make this final - need to read CID before construction?

    // nodes listed so we can construct them with reflection TODO: auto-construction of nodes like the default case, but tuned?
    private static final Class[] nodeClasses = new Class[] {
            Cl2Node.class, CO2Node.class, CO2Node.class, CS2Node.class, F2Node.class, H2Node.class, N2Node.class, NONode.class, N2ONode.class,
            O2Node.class, C2H2Node.class, C2H4Node.class, C2H5ClNode.class, C2H5OHNode.class, C2H6Node.class, CH2ONode.class, CH3OHNode.class,
            CH4Node.class, H2ONode.class, H2SNode.class, HClNode.class, HFNode.class, NH3Node.class, NO2Node.class, OF2Node.class, P4Node.class,
            PCl3Node.class, PCl5Node.class, PF3Node.class, PH3Node.class, SO2Node.class, SO3Node.class
    };

    private CompleteMolecule( String commonName, String molecularFormula, int atomCount, int bondCount ) {
        super( atomCount, bondCount );
        this.commonName = commonName;
        this.molecularFormula = molecularFormula;
    }

    /**
     * Construct a molecule out of a pipe-separated line.
     *
     * @param line A string that is essentially a serialized molecule
     * @return A CompleteMolecule that is properly constructed
     */
    public static CompleteMolecule fromString( String line ) {
        StringTokenizer t = new StringTokenizer( line, "|" );

        // read common name first
        String commonName = t.nextToken();

        // molecular formula
        String molecularFormula = t.nextToken();

        // # of atoms
        int atomCount = Integer.parseInt( t.nextToken() );

        // # of bonds
        int bondCount = Integer.parseInt( t.nextToken() );
        CompleteMolecule completeMolecule = new CompleteMolecule( commonName, molecularFormula, atomCount, bondCount );

        // for each atom, read its symbol, then 2d coordinates, then 3d coordinates (total of 6 fields)
        for ( int i = 0; i < atomCount; i++ ) {
            String symbol = t.nextToken();
            float x2d = Float.parseFloat( t.nextToken() );
            float y2d = Float.parseFloat( t.nextToken() );
            float x3d = Float.parseFloat( t.nextToken() );
            float y3d = Float.parseFloat( t.nextToken() );
            float z3d = Float.parseFloat( t.nextToken() );
            PubChemAtom atom = new PubChemAtom( Element.getElementBySymbol( symbol ), x2d, y2d, x3d, y3d, z3d );
            completeMolecule.addAtom( atom );
        }

        // for each bond, read atom indices (2 of them, which are 1-indexed), and then the order of the bond (single, double, triple, etc.)
        for ( int i = 0; i < bondCount; i++ ) {
            int a = Integer.parseInt( t.nextToken() );
            int b = Integer.parseInt( t.nextToken() );
            int order = Integer.parseInt( t.nextToken() );
            PubChemBond bond = new PubChemBond( completeMolecule.getAtoms().get( a - 1 ), completeMolecule.getAtoms().get( b - 1 ), order ); // -1 since our format is 1-based
            completeMolecule.addBond( bond );
        }

        completeMolecule.cid = Integer.parseInt( t.nextToken() );

        return completeMolecule;
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

    /**
     * @return An XML CML string for our 3D representation
     */
    public String getCmlData() {
        String ret = "<?xml version=\"1.0\"?>\n" +
                     "<molecule id=\"" + commonName + "\" xmlns=\"http://www.xml-cml.org/schema\">";
        ret += "<name>" + commonName + "</name>";
        ret += "<atomArray>";
        for ( int i = 0; i < this.getAtoms().size(); i++ ) {
            PubChemAtom atom = this.getAtoms().get( i );
            // TODO: include the formal charge possibly later, if Jmol can show it?
            ret += "<atom id=\"a" + i + "\" elementType=\"" + atom.getSymbol() + "\" x3=\"" + atom.x3d + "\" y3=\"" + atom.y3d + "\" z3=\"" + atom.z3d + "\"/>";
        }
        ret += "</atomArray>";
        ret += "<bondArray>";
        for ( Bond<PubChemAtom> bond : this.getBonds() ) {
            PubChemBond bondWrapper = (PubChemBond) bond;
            ret += "<bond atomRefs2=\"a" + this.getAtoms().indexOf( bondWrapper.a ) + " a" + this.getAtoms().indexOf( bondWrapper.b ) + "\" order=\"" + bondWrapper.order + "\"/>";
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
        for ( int i = 0; i < this.getAtoms().size(); i++ ) {
            Color color = this.getAtoms().get( i ).getColor();

            viewer.script( "select a" + i + ";  color [" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "];" );
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
            List<PubChemAtom> wrappers = new ArrayList<PubChemAtom>( CompleteMolecule.this.getAtoms() );

            // sort by Z-depth in 3D
            Collections.sort( wrappers, new Comparator<PubChemAtom>() {
                public int compare( PubChemAtom a, PubChemAtom b ) {
                    return ( new Float( a.z3d ) ).compareTo( b.z3d );
                }
            } );

            for ( final PubChemAtom atomWrapper : wrappers ) {
                addChild( new AtomNode( atomWrapper.getElement() ) {{
                    setOffset( atomWrapper.x2d * 15, atomWrapper.y2d * 15 ); // custom scale for now.
                }} );
            }
        }};
    }

    public static class PubChemAtom extends Atom {
        // 2d coordinates
        public final float x2d;
        public final float y2d;

        // 3d coordinates
        public final float x3d;
        public final float y3d;
        public final float z3d;

        private PubChemAtom( Element element, float x2d, float y2d, float x3d, float y3d, float z3d ) {
            super( element );
            this.x2d = x2d;
            this.y2d = y2d;
            this.x3d = x3d;
            this.y3d = y3d;
            this.z3d = z3d;
        }
    }

    public static class PubChemBond extends Bond<PubChemAtom> {
        public final int order;

        private PubChemBond( PubChemAtom a, PubChemAtom b, int order ) {
            super( a, b );
            this.order = order;
        }
    }
}
