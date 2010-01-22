package edu.colorado.phet.reactantsproductsandleftovers.model;

import java.awt.Image;

import edu.colorado.phet.reactantsproductsandleftovers.RPALImages;
import edu.colorado.phet.reactantsproductsandleftovers.RPALSymbols;


public abstract class Molecule {
    
    private final String name;
    private final Image image;
    
    public Molecule( String name, Image image ) {
        this.name = name;
        this.image = image;
    }
    
    public String getName() {
        return name;
    }
    
    public Image getImage() {
        return image;
    }
    
    public class CH4 extends Molecule {
        public CH4() {
            super( RPALSymbols.CH4, RPALImages.CH4 );
        }
    }
    
    public class CO2 extends Molecule {
        public CO2() {
            super( RPALSymbols.CO2, RPALImages.CO2 );
        }
    }
    
    public class H2 extends Molecule {
        public H2() {
            super( RPALSymbols.H2, RPALImages.H2 );
        }
    }
    
    public class N2 extends Molecule {
        public N2() {
            super( RPALSymbols.N2, RPALImages.N2 );
        }
    }
    
    public class NH3 extends Molecule {
        public NH3() {
            super( RPALSymbols.NH3, RPALImages.NH3 );
        }
    }
    
    public class O2 extends Molecule {
        public O2() {
            super( RPALSymbols.O2, RPALImages.O2 );
        }
    }

}
