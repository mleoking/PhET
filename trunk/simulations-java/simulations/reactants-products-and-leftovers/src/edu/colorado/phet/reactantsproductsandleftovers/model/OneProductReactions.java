package edu.colorado.phet.reactantsproductsandleftovers.model;

import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.model.Molecule.*;

/**
 * Collection of one-product chemical reactions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OneProductReactions {
    
    /* not intended for instantiation */
    private OneProductReactions() {}
    
    // 2H2 + O2 -> 2H2O
    public static class WaterReaction extends ChemicalReaction {
        
        public WaterReaction() {
            super( RPALStrings.RADIO_BUTTON_WATER, createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 2, new H2() );
            Reactant r2 = new Reactant( 1, new O2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 2, new H2O() );
            return new Product[] { p1 };
        }

    }

    // H2 + F2 -> 2HF
    public static class Reaction_H2_F2__2HF extends ChemicalReaction {
        
        public Reaction_H2_F2__2HF() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new H2() );
            Reactant r2 = new Reactant( 1, new F2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 2, new HF() );
            return new Product[] { p1 };
        }
    }
    
    // H2 + Cl2 -> 2HCl
    public static class Reaction_H2_Cl2__2HCl extends ChemicalReaction {
        
        public Reaction_H2_Cl2__2HCl() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new H2() );
            Reactant r2 = new Reactant( 1, new Cl2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 2, new HCl() );
            return new Product[] { p1 };
        }
    }
    
    // CO + 2H2 -> CH3OH
    public static class Reaction_CO_2H2__CH3OH extends ChemicalReaction {
        
        public Reaction_CO_2H2__CH3OH() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new CO() );
            Reactant r2 = new Reactant( 2, new H2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 1, new CH3OH() );
            return new Product[] { p1 };
        }
    }
    
    // CH2O + H2 -> CH3OH
    public static class Reaction_CH2O_H2__CH3OH extends ChemicalReaction {
        
        public Reaction_CH2O_H2__CH3OH() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new CH2O() );
            Reactant r2 = new Reactant( 1, new H2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 1, new CH3OH() );
            return new Product[] { p1 };
        }
    }
    
    // C2H4 + H2 -> C2H6
    public static class Reaction_C2H4_H2__C2H6 extends ChemicalReaction {
        
        public Reaction_C2H4_H2__C2H6() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new C2H4() );
            Reactant r2 = new Reactant( 1, new H2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 1, new C2H6() );
            return new Product[] { p1 };
        }
    }
    
    // C2H2 + 2H2 -> C2H6
    public static class Reaction_C2H2_2H2__C2H6 extends ChemicalReaction {
        
        public Reaction_C2H2_2H2__C2H6() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new C2H2() );
            Reactant r2 = new Reactant( 2, new H2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 1, new C2H6() );
            return new Product[] { p1 };
        }
    }
    
    // C + O2 -> CO2
    
    // 2C + O2 -> 2CO
    
    // 2CO + O2 -> 2CO2
    
    // C + CO2 -> 2CO
    
    // C + 2S -> CS2
    
    // N2 + H2 -> 2NH3
    public static class AmmoniaReaction extends ChemicalReaction {
        
        public AmmoniaReaction() {
            super( RPALStrings.RADIO_BUTTON_AMMONIA, createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new N2() );
            Reactant r2 = new Reactant( 3, new H2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 2, new NH3() );
            return new Product[] { p1 };
        }

    }
    
    // N2 + O2 -> 2NO
    
    // 2NO + O2 -> 2NO2
    
    // 2N2 + O2 -> 2N2O
    
    // P4 + 6H2 -> 4PH3
    
    // P4 + 6F2 -> 4PF3
    
    // P4 + 6Cl2 -> 4PCl3
    
    // P4 + 10Cl2 -> 4PCl5
    
    // PCl3 + Cl2 -> PCl5
    
    // 2SO2 + O2 -> 2SO3
}
