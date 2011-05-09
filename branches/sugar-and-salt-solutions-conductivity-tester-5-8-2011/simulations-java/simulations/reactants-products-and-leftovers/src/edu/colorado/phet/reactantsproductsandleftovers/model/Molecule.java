// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactantsproductsandleftovers.model;

import java.awt.Image;

import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.colorado.phet.reactantsproductsandleftovers.RPALSymbols;

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
            super( RPALSymbols.SANDWICH, null /* image */ ); // image will be dynamically generated
        }
    }
    
    /*================================================================================
     * 
     * Molecules for real reactions.
     * 
     *================================================================================*/
    
    public static class C extends Molecule {
        public C() {
            super( RPALSymbols.C, RPALImages.C );
        }
    }
    
    public static class C2H2 extends Molecule {
        public C2H2() {
            super( RPALSymbols.C2H2, RPALImages.C2H2 );
        }
    }
    
    public static class C2H4 extends Molecule {
        public C2H4() {
            super( RPALSymbols.C2H4, RPALImages.C2H4 );
        }
    }
    
    public static class C2H5Cl extends Molecule {
        public C2H5Cl() {
            super( RPALSymbols.C2H5Cl, RPALImages.C2H5Cl );
        }
    }
    
    public static class C2H5OH extends Molecule {
        public C2H5OH() {
            super( RPALSymbols.C2H5OH, RPALImages.C2H5OH );
        }
    }
    
    public static class C2H6 extends Molecule {
        public C2H6() {
            super( RPALSymbols.C2H6, RPALImages.C2H6 );
        }
    }
    
    public static class CH2O extends Molecule {
        public CH2O() {
            super( RPALSymbols.CH2O, RPALImages.CH2O );
        }
    }
    
    public static class CH3OH extends Molecule {
        public CH3OH() {
            super( RPALSymbols.CH3OH, RPALImages.CH3OH );
        }
    }
    
    public static class CH4 extends Molecule {
        public CH4() {
            super( RPALSymbols.CH4, RPALImages.CH4 );
        }
    }
    
    public static class Cl2 extends Molecule {
        public Cl2() {
            super( RPALSymbols.Cl2, RPALImages.Cl2 );
        }
    }
    
    public static class CO extends Molecule {
        public CO() {
            super( RPALSymbols.CO, RPALImages.CO );
        }
    }
    
    public static class CO2 extends Molecule {
        public CO2() {
            super( RPALSymbols.CO2, RPALImages.CO2 );
        }
    }
    
    public static class CS2 extends Molecule {
        public CS2() {
            super( RPALSymbols.CS2, RPALImages.CS2 );
        }
    }
    
    public static class F2 extends Molecule {
        public F2() {
            super( RPALSymbols.F2, RPALImages.F2 );
        }
    }
    
    public static class H2 extends Molecule {
        public H2() {
            super( RPALSymbols.H2, RPALImages.H2 );
        }
    }
    
    public static class H2O extends Molecule {
        public H2O() {
            super( RPALSymbols.H2O, RPALImages.H2O );
        }
    }
    
    public static class H2S extends Molecule {
        public H2S() {
            super( RPALSymbols.H2S, RPALImages.H2S );
        }
    }
    
    public static class HCl extends Molecule {
        public HCl() {
            super( RPALSymbols.HCl, RPALImages.HCl );
        }
    }
    
    public static class HF extends Molecule {
        public HF() {
            super( RPALSymbols.HF, RPALImages.HF );
        }
    }
    
    public static class N2 extends Molecule {
        public N2() {
            super( RPALSymbols.N2, RPALImages.N2 );
        }
    }
    
    public static class N2O extends Molecule {
        public N2O() {
            super( RPALSymbols.N2O, RPALImages.N2O );
        }
    }
    
    public static class NH3 extends Molecule {
        public NH3() {
            super( RPALSymbols.NH3, RPALImages.NH3 );
        }
    }
    
    public static class NO extends Molecule {
        public NO() {
            super( RPALSymbols.NO, RPALImages.NO );
        }
    }
    
    public static class NO2 extends Molecule {
        public NO2() {
            super( RPALSymbols.NO2, RPALImages.NO2 );
        }
    }
    
    public static class O2 extends Molecule {
        public O2() {
            super( RPALSymbols.O2, RPALImages.O2 );
        }
    }
    
    public static class OF2 extends Molecule {
        public OF2() {
            super( RPALSymbols.OF2, RPALImages.OF2 );
        }
    }
    
    public static class P4 extends Molecule {
        public P4() {
            super( RPALSymbols.P4, RPALImages.P4 );
        }
    }
    
    public static class PCl3 extends Molecule {
        public PCl3() {
            super( RPALSymbols.PCl3, RPALImages.PCl3 );
        }
    }
    
    public static class PCl5 extends Molecule {
        public PCl5() {
            super( RPALSymbols.PCl5, RPALImages.PCl5 );
        }
    }
    
    public static class PF3 extends Molecule {
        public PF3() {
            super( RPALSymbols.PF3, RPALImages.PF3 );
        }
    }
    
    public static class PH3 extends Molecule {
        public PH3() {
            super( RPALSymbols.PH3, RPALImages.PH3 );
        }
    }
    
    public static class S extends Molecule {
        public S() {
            super( RPALSymbols.S, RPALImages.S );
        }
    }
    
    public static class SO2 extends Molecule {
        public SO2() {
            super( RPALSymbols.SO2, RPALImages.SO2 );
        }
    }
    
    public static class SO3 extends Molecule {
        public SO3() {
            super( RPALSymbols.SO3, RPALImages.SO3 );
        }
    }
}
