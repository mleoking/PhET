// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactantsproductsandleftovers.model;

import java.awt.Image;

import edu.colorado.phet.chemistry.molecules.C2H2Node;
import edu.colorado.phet.chemistry.molecules.C2H4Node;
import edu.colorado.phet.chemistry.molecules.C2H5ClNode;
import edu.colorado.phet.chemistry.molecules.C2H5OHNode;
import edu.colorado.phet.chemistry.molecules.C2H6Node;
import edu.colorado.phet.chemistry.molecules.CH2ONode;
import edu.colorado.phet.chemistry.molecules.CH3OHNode;
import edu.colorado.phet.chemistry.molecules.CH4Node;
import edu.colorado.phet.chemistry.molecules.H2ONode;
import edu.colorado.phet.chemistry.molecules.H2SNode;
import edu.colorado.phet.chemistry.molecules.HClNode;
import edu.colorado.phet.chemistry.molecules.HFNode;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.CNode;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.CO2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.CONode;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.CS2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.Cl2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.F2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.H2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.N2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.N2ONode;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.NONode;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.O2Node;
import edu.colorado.phet.chemistry.molecules.HorizontalMoleculeNode.SNode;
import edu.colorado.phet.chemistry.molecules.NH3Node;
import edu.colorado.phet.chemistry.molecules.NO2Node;
import edu.colorado.phet.chemistry.molecules.OF2Node;
import edu.colorado.phet.chemistry.molecules.P4Node;
import edu.colorado.phet.chemistry.molecules.PCl3Node;
import edu.colorado.phet.chemistry.molecules.PCl5Node;
import edu.colorado.phet.chemistry.molecules.PF3Node;
import edu.colorado.phet.chemistry.molecules.PH3Node;
import edu.colorado.phet.chemistry.molecules.SO2Node;
import edu.colorado.phet.chemistry.molecules.SO3Node;
import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.colorado.phet.reactantsproductsandleftovers.RPALSymbols;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for molecules.
 * Inner classes for each specific molecule.
 * <p>
 * For purposes of the Sandwich Shop analogy, sandwiches and their ingredients are 
 * considered to be molecules.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Molecule {
    
    private final String symbol;
    private Image image;  // mutable only for the Sandwich case
    
    public Molecule( String symbol, Image image ) {
        this.symbol = symbol;
        this.image = image;
    }

    protected Molecule( String symbol, PNode node ) {
        this( symbol, node.toImage() );
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setImage( Image image ) {
        this.image = image;
    }
    
    public Image getImage() {
        return image;
    }
    
    /*================================================================================
     * 
     * "Molecules" for sandwich analogy.
     * 
     *================================================================================*/
    
    public static class Bread extends Molecule {
        public Bread() {
            super( RPALSymbols.BREAD, RPALImages.BREAD );
        }
    }
    
    public static class Meat extends Molecule {
        public Meat() {
            super( RPALSymbols.MEAT, RPALImages.MEAT );
        }
    }
    
    public static class Cheese extends Molecule {
        public Cheese() {
            super( RPALSymbols.CHEESE, RPALImages.CHEESE );
        }
    }
    
    public static class Sandwich extends Molecule {
        public Sandwich() {
            super( RPALSymbols.SANDWICH, (Image)null /* image */ ); // image will be dynamically generated
        }
    }
    
    /*================================================================================
     * 
     * Molecules for real reactions.
     * 
     *================================================================================*/
    
    public static class C extends Molecule {
        public C() {
            super( RPALSymbols.C, new CNode() );
        }
    }
    
    public static class C2H2 extends Molecule {
        public C2H2() {
            super( RPALSymbols.C2H2, new C2H2Node() );
        }
    }
    
    public static class C2H4 extends Molecule {
        public C2H4() {
            super( RPALSymbols.C2H4, new C2H4Node() );
        }
    }
    
    public static class C2H5Cl extends Molecule {
        public C2H5Cl() {
            super( RPALSymbols.C2H5Cl, new C2H5ClNode() );
        }
    }
    
    public static class C2H5OH extends Molecule {
        public C2H5OH() {
            super( RPALSymbols.C2H5OH, new C2H5OHNode() );
        }
    }
    
    public static class C2H6 extends Molecule {
        public C2H6() {
            super( RPALSymbols.C2H6, new C2H6Node() );
        }
    }
    
    public static class CH2O extends Molecule {
        public CH2O() {
            super( RPALSymbols.CH2O, new CH2ONode() );
        }
    }
    
    public static class CH3OH extends Molecule {
        public CH3OH() {
            super( RPALSymbols.CH3OH, new CH3OHNode() );
        }
    }
    
    public static class CH4 extends Molecule {
        public CH4() {
            super( RPALSymbols.CH4, new CH4Node() );
        }
    }
    
    public static class Cl2 extends Molecule {
        public Cl2() {
            super( RPALSymbols.Cl2, new Cl2Node() );
        }
    }
    
    public static class CO extends Molecule {
        public CO() {
            super( RPALSymbols.CO, new CONode() );
        }
    }
    
    public static class CO2 extends Molecule {
        public CO2() {
            super( RPALSymbols.CO2, new CO2Node() );
        }
    }
    
    public static class CS2 extends Molecule {
        public CS2() {
            super( RPALSymbols.CS2, new CS2Node() );
        }
    }
    
    public static class F2 extends Molecule {
        public F2() {
            super( RPALSymbols.F2, new F2Node() );
        }
    }
    
    public static class H2 extends Molecule {
        public H2() {
            super( RPALSymbols.H2, new H2Node() );
        }
    }
    
    public static class H2O extends Molecule {
        public H2O() {
            super( RPALSymbols.H2O, new H2ONode() );
        }
    }
    
    public static class H2S extends Molecule {
        public H2S() {
            super( RPALSymbols.H2S, new H2SNode() );
        }
    }
    
    public static class HCl extends Molecule {
        public HCl() {
            super( RPALSymbols.HCl, new HClNode() );
        }
    }
    
    public static class HF extends Molecule {
        public HF() {
            super( RPALSymbols.HF, new HFNode() );
        }
    }
    
    public static class N2 extends Molecule {
        public N2() {
            super( RPALSymbols.N2, new N2Node() );
        }
    }
    
    public static class N2O extends Molecule {
        public N2O() {
            super( RPALSymbols.N2O, new N2ONode() );
        }
    }
    
    public static class NH3 extends Molecule {
        public NH3() {
            super( RPALSymbols.NH3, new NH3Node() );
        }
    }
    
    public static class NO extends Molecule {
        public NO() {
            super( RPALSymbols.NO, new NONode() );
        }
    }
    
    public static class NO2 extends Molecule {
        public NO2() {
            super( RPALSymbols.NO2, new NO2Node() );
        }
    }
    
    public static class O2 extends Molecule {
        public O2() {
            super( RPALSymbols.O2, new O2Node() );
        }
    }
    
    public static class OF2 extends Molecule {
        public OF2() {
            super( RPALSymbols.OF2, new OF2Node() );
        }
    }
    
    public static class P4 extends Molecule {
        public P4() {
            super( RPALSymbols.P4, new P4Node() );
        }
    }
    
    public static class PCl3 extends Molecule {
        public PCl3() {
            super( RPALSymbols.PCl3, new PCl3Node() );
        }
    }
    
    public static class PCl5 extends Molecule {
        public PCl5() {
            super( RPALSymbols.PCl5, new PCl5Node() );
        }
    }
    
    public static class PF3 extends Molecule {
        public PF3() {
            super( RPALSymbols.PF3, new PF3Node() );
        }
    }
    
    public static class PH3 extends Molecule {
        public PH3() {
            super( RPALSymbols.PH3, new PH3Node() );
        }
    }
    
    public static class S extends Molecule {
        public S() {
            super( RPALSymbols.S, new SNode() );
        }
    }
    
    public static class SO2 extends Molecule {
        public SO2() {
            super( RPALSymbols.SO2, new SO2Node() );
        }
    }
    
    public static class SO3 extends Molecule {
        public SO3() {
            super( RPALSymbols.SO3, new SO3Node() );
        }
    }
}
