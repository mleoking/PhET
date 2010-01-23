package edu.colorado.phet.reactantsproductsandleftovers.model;

import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.model.Molecule.*;

/**
 * Collection of two-product chemical reactions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoProductReactions {
    
    /* not intended for instantiation */
    private TwoProductReactions() {}
    
    // 2C + 2H2O -> CH4 + CO2
    
    // CH4 + H2O -> 3H2 + CO

    // CH4 + 2 O2 -> CO2 + 2 H2O
    public static class MethaneReaction extends ChemicalReaction {
        
        public MethaneReaction() {
            super( RPALStrings.RADIO_BUTTON_METHANE, createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new CH4() );
            Reactant r2 = new Reactant( 2, new O2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 1, new CO2() );
            Product p2 = new Product( 2, new H2O() );
            return new Product[] { p1, p2 };
        }

    }
    
    // 2C2H6 + 7O2 -> 4CO2 + 6H2O
    public static class Reaction_2C2H6_7O2__4CO2_6H2O extends ChemicalReaction {
        
        public Reaction_2C2H6_7O2__4CO2_6H2O() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 2, new C2H6() );
            Reactant r2 = new Reactant( 7, new O2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 4, new CO2() );
            Product p2 = new Product( 6, new H2O() );
            return new Product[] { p1, p2 };
        }
    }
    
    // C2H4 + 3O2 -> 2CO2 + 2H2O
    public static class Reaction_C2H4_3O2__2CO2_2H2O extends ChemicalReaction {
        
        public Reaction_C2H4_3O2__2CO2_2H2O() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new C2H4() );
            Reactant r2 = new Reactant( 3, new O2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 2, new CO2() );
            Product p2 = new Product( 2, new H2O() );
            return new Product[] { p1, p2 };
        }
    }

    
    // 2C2H2 + 5O2 -> 4CO2 + 2H2O
    public static class Reaction_2C2H2_5O2__4CO2_2H2O extends ChemicalReaction {
        
        public Reaction_2C2H2_5O2__4CO2_2H2O() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 2, new C2H2() );
            Reactant r2 = new Reactant( 5, new O2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 4, new CO2() );
            Product p2 = new Product( 2, new H2O() );
            return new Product[] { p1, p2 };
        }
    }
    
    // C2H5OH + 3O2 -> 2CO2 + 3H2O
    public static class Reaction_C2H5OH_3O2__2CO2_3H2O extends ChemicalReaction {
        
        public Reaction_C2H5OH_3O2__2CO2_3H2O() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new C2H5OH() );
            Reactant r2 = new Reactant( 3, new O2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 2, new CO2() );
            Product p2 = new Product( 3, new H2O() );
            return new Product[] { p1, p2 };
        }
    }

    // C2H6 + Cl2 -> C2H5Cl + HCl
    public static class Reaction_C2H6_Cl2__C2H5Cl_HCl extends ChemicalReaction {
        
        public Reaction_C2H6_Cl2__C2H5Cl_HCl() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new C2H6() );
            Reactant r2 = new Reactant( 1, new Cl2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 1, new C2H5Cl() );
            Product p2 = new Product( 1, new HCl() );
            return new Product[] { p1, p2 };
        }
    }
    
    // CH4 + 4S -> CS2 + 2H2S
    
    // CS2 + 3O2 -> CO2 + 2SO2

    // 4NH3 + 3O2 -> 2N2 + 6H2O
    
    // 4NH3 + 5O2 -> 4NO + 6H2O
    
    // 4NH3 + 7O2 -> 4NO2 + 6H2O
    
    // 4NH3 + 6NO -> 5N2 + 6H2O
    
    // SO2 + 2H2 -> S + 2H2O
    
    // SO2 + 3H2 -> H2S + 2H2O

    // 2F2 + H2O -> OF2 + 2HF
    
    // OF2 + H2O -> O2 + 2HF
}
