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
    public static class Reaction_C_O2__CO2 extends ChemicalReaction {
        
        public Reaction_C_O2__CO2() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new C() );
            Reactant r2 = new Reactant( 1, new O2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 1, new CO2() );
            return new Product[] { p1 };
        }
    }
    
    // 2C + O2 -> 2CO
    public static class Reaction_2C_O2__2CO extends ChemicalReaction {
        
        public Reaction_2C_O2__2CO() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 2, new C() );
            Reactant r2 = new Reactant( 1, new O2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 2, new CO() );
            return new Product[] { p1 };
        }
    }
    
    // 2CO + O2 -> 2CO2
    public static class Reaction_2CO_O2__2CO2 extends ChemicalReaction {
        
        public Reaction_2CO_O2__2CO2() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 2, new CO() );
            Reactant r2 = new Reactant( 1, new O2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 2, new CO2() );
            return new Product[] { p1 };
        }
    }
    
    // C + CO2 -> 2CO
    public static class Reaction_C_CO2__2CO extends ChemicalReaction {
        
        public Reaction_C_CO2__2CO() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new C() );
            Reactant r2 = new Reactant( 1, new CO2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 2, new CO() );
            return new Product[] { p1 };
        }
    }
    
    // C + 2S -> CS2
    public static class Reaction_C_2S__CS2 extends ChemicalReaction {
        
        public Reaction_C_2S__CS2() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new C() );
            Reactant r2 = new Reactant( 2, new S() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 1, new CS2() );
            return new Product[] { p1 };
        }
    }
    
    // N2 + 3H2 -> 2NH3
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
    public static class Reaction_N2_O2__2NO extends ChemicalReaction {
        
        public Reaction_N2_O2__2NO() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new N2() );
            Reactant r2 = new Reactant( 1, new O2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 2, new NO() );
            return new Product[] { p1 };
        }
    }
    
    // 2NO + O2 -> 2NO2
    public static class Reaction_2NO_O2__2NO2 extends ChemicalReaction {
        
        public Reaction_2NO_O2__2NO2() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 2, new NO() );
            Reactant r2 = new Reactant( 1, new O2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 2, new NO2() );
            return new Product[] { p1 };
        }
    }
    
    // 2N2 + O2 -> 2N2O
    public static class Reaction_2N2_O2__2NO2 extends ChemicalReaction {
        
        public Reaction_2N2_O2__2NO2() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 2, new N2() );
            Reactant r2 = new Reactant( 1, new O2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 2, new N2O() );
            return new Product[] { p1 };
        }
    }
    
    // P4 + 6H2 -> 4PH3
    public static class Reaction_P4_6H2__4PH3 extends ChemicalReaction {
        
        public Reaction_P4_6H2__4PH3() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new P4() );
            Reactant r2 = new Reactant( 6, new H2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 4, new PH3() );
            return new Product[] { p1 };
        }
    }
    
    // P4 + 6F2 -> 4PF3
    public static class Reaction_P4_6F2__4PF3 extends ChemicalReaction {
        
        public Reaction_P4_6F2__4PF3() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new P4() );
            Reactant r2 = new Reactant( 6, new F2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 4, new PF3() );
            return new Product[] { p1 };
        }
    }
    
    // P4 + 6Cl2 -> 4PCl3
    public static class Reaction_P4_6Cl2__4PCl3 extends ChemicalReaction {
        
        public Reaction_P4_6Cl2__4PCl3() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new P4() );
            Reactant r2 = new Reactant( 6, new Cl2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 4, new PCl3() );
            return new Product[] { p1 };
        }
    }
    
    // P4 + 10Cl2 -> 4PCl5
    public static class Reaction_P4_10Cl2__4PCl5 extends ChemicalReaction {
        
        public Reaction_P4_10Cl2__4PCl5() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new P4() );
            Reactant r2 = new Reactant( 10, new Cl2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 4, new PCl5() );
            return new Product[] { p1 };
        }
    }
    
    // PCl3 + Cl2 -> PCl5
    public static class Reaction_PCl3_Cl2__PCl5 extends ChemicalReaction {
        
        public Reaction_PCl3_Cl2__PCl5() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 1, new PCl3() );
            Reactant r2 = new Reactant( 1, new Cl2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 1, new PCl5() );
            return new Product[] { p1 };
        }
    }
    
    // 2SO2 + O2 -> 2SO3
    public static class Reaction_2SO2_O2__2SO3 extends ChemicalReaction {
        
        public Reaction_2SO2_O2__2SO3() {
            super( createReactants(), createProducts() );
        }
        
        private static Reactant[] createReactants() {
            Reactant r1 = new Reactant( 2, new SO2() );
            Reactant r2 = new Reactant( 1, new O2() );
            return new Reactant[] { r1, r2 };
        }
        
        private static Product[] createProducts() {
            Product p1 = new Product( 2, new SO3() );
            return new Product[] { p1 };
        }
    }
}
