// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.model;

import java.awt.Image;

import edu.colorado.phet.balancingchemicalequations.BCEImages;
import edu.colorado.phet.balancingchemicalequations.BCESymbols;

/**
 * Base class for molecules.
 * Inner classes for each specific molecule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Molecule {

    private final String symbol;
    private final Image image;

    public Molecule( String symbol, Image image ) {
        this.symbol = symbol;
        this.image = image;
    }

    public String getSymbol() {
        return symbol;
    }

    public Image getImage() {
        return image;
    }

    public static class CH4 extends Molecule {
        public CH4() {
            super( BCESymbols.CH4, BCEImages.CH4 );
        }
    }

    public static class CO2 extends Molecule {
        public CO2() {
            super( BCESymbols.CO2, BCEImages.CO2 );
        }
    }

    public static class H2 extends Molecule {
        public H2() {
            super( BCESymbols.H2, BCEImages.H2 );
        }
    }

    public static class H2O extends Molecule {
        public H2O() {
            super( BCESymbols.H2O, BCEImages.H2O );
        }
    }

    public static class N2 extends Molecule {
        public N2() {
            super( BCESymbols.N2, BCEImages.N2 );
        }
    }

    public static class NH3 extends Molecule {
        public NH3() {
            super( BCESymbols.NH3, BCEImages.NH3 );
        }
    }

    public static class O2 extends Molecule {
        public O2() {
            super( BCESymbols.O2, BCEImages.O2 );
        }
    }
}
