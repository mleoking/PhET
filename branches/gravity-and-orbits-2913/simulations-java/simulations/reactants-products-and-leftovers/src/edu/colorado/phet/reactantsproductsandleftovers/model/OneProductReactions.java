// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactantsproductsandleftovers.model;

import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.model.Molecule.*;

/**
 * Collection of one-product chemical reaction subclasses.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OneProductReactions {
    
    /* not intended for instantiation */
    private OneProductReactions() {}
    
    // 2H2 + O2 -> 2H2O
    public static class WaterReaction extends ChemicalReaction {
        public WaterReaction() {
            super( RPALStrings.RADIO_BUTTON_WATER, 
                   new Reactant[] { new Reactant( 2, new H2() ), new Reactant( 1, new O2() ) },
                    new Product[] { new Product( 2, new H2O() ) } );
        }
    }

    // H2 + F2 -> 2HF
    public static class Reaction_H2_F2__2HF extends ChemicalReaction {
        public Reaction_H2_F2__2HF() {
            super( new Reactant[] { new Reactant( 1, new H2() ), new Reactant( 1, new F2() ) }, 
                    new Product[] { new Product( 2, new HF() ) } );
        }
    }
    
    // H2 + Cl2 -> 2HCl
    public static class Reaction_H2_Cl2__2HCl extends ChemicalReaction {
        public Reaction_H2_Cl2__2HCl() {
            super( new Reactant[] { new Reactant( 1, new H2() ), new Reactant( 1, new Cl2() ) }, 
                    new Product[] { new Product( 2, new HCl() ) } );
        }
    }
    
    // CO + 2H2 -> CH3OH
    public static class Reaction_CO_2H2__CH3OH extends ChemicalReaction {
        public Reaction_CO_2H2__CH3OH() {
            super( new Reactant[] { new Reactant( 1, new CO() ), new Reactant( 2, new H2() ) }, 
                    new Product[] { new Product( 1, new CH3OH() ) } );
        }
    }
    
    // CH2O + H2 -> CH3OH
    public static class Reaction_CH2O_H2__CH3OH extends ChemicalReaction {
        public Reaction_CH2O_H2__CH3OH() {
            super( new Reactant[] { new Reactant( 1, new CH2O() ), new Reactant( 1, new H2() ) }, 
                    new Product[] { new Product( 1, new CH3OH() ) } );
        }
    }
    
    // C2H4 + H2 -> C2H6
    public static class Reaction_C2H4_H2__C2H6 extends ChemicalReaction {
        public Reaction_C2H4_H2__C2H6() {
            super( new Reactant[] { new Reactant( 1, new C2H4() ), new Reactant( 1, new H2() ) }, 
                    new Product[] { new Product( 1, new C2H6() ) } );
        }
    }
    
    // C2H2 + 2H2 -> C2H6
    public static class Reaction_C2H2_2H2__C2H6 extends ChemicalReaction {
        public Reaction_C2H2_2H2__C2H6() {
            super( new Reactant[] { new Reactant( 1, new C2H2() ), new Reactant( 2, new H2() ) }, 
                    new Product[] { new Product( 1, new C2H6() ) } );
        }
    }
    
    // C + O2 -> CO2
    public static class Reaction_C_O2__CO2 extends ChemicalReaction {
        public Reaction_C_O2__CO2() {
            super( new Reactant[] { new Reactant( 1, new C() ), new Reactant( 1, new O2() ) }, 
                    new Product[] { new Product( 1, new CO2() ) } );
        }
    }
    
    // 2C + O2 -> 2CO
    public static class Reaction_2C_O2__2CO extends ChemicalReaction {
        public Reaction_2C_O2__2CO() {
            super( new Reactant[] { new Reactant( 2, new C() ), new Reactant( 1, new O2() ) }, 
                   new Product[] { new Product( 2, new CO() ) } );
        }
    }
    
    // 2CO + O2 -> 2CO2
    public static class Reaction_2CO_O2__2CO2 extends ChemicalReaction {
        public Reaction_2CO_O2__2CO2() {
            super( new Reactant[] { new Reactant( 2, new CO() ), new Reactant( 1, new O2() ) }, 
                    new Product[] { new Product( 2, new CO2() ) } );
        }
    }
    
    // C + CO2 -> 2CO
    public static class Reaction_C_CO2__2CO extends ChemicalReaction {
        public Reaction_C_CO2__2CO() {
            super( new Reactant[] { new Reactant( 1, new C() ), new Reactant( 1, new CO2() ) }, 
                    new Product[] { new Product( 2, new CO() ) } );
        }
    }
    
    // C + 2S -> CS2
    public static class Reaction_C_2S__CS2 extends ChemicalReaction {
        public Reaction_C_2S__CS2() {
            super( new Reactant[] { new Reactant( 1, new C() ), new Reactant( 2, new S() ) }, 
                    new Product[] { new Product( 1, new CS2() ) } );
        }
    }
    
    // N2 + 3H2 -> 2NH3
    public static class AmmoniaReaction extends ChemicalReaction {
        public AmmoniaReaction() {
            super( RPALStrings.RADIO_BUTTON_AMMONIA, 
                    new Reactant[] { new Reactant( 1, new N2() ), new Reactant( 3, new H2() ) }, 
                    new Product[] { new Product( 2, new NH3() ) } );
        }
    }
    
    // N2 + O2 -> 2NO
    public static class Reaction_N2_O2__2NO extends ChemicalReaction {
        public Reaction_N2_O2__2NO() {
            super( new Reactant[] { new Reactant( 1, new N2() ), new Reactant( 1, new O2() ) }, 
                    new Product[] { new Product( 2, new NO() ) } );
        }
    }
    
    // 2NO + O2 -> 2NO2
    public static class Reaction_2NO_O2__2NO2 extends ChemicalReaction {
        public Reaction_2NO_O2__2NO2() {
            super( new Reactant[] { new Reactant( 2, new NO() ), new Reactant( 1, new O2() ) }, 
                    new Product[] { new Product( 2, new NO2() ) } );
        }
    }
    
    // 2N2 + O2 -> 2N2O
    public static class Reaction_2N2_O2__2NO2 extends ChemicalReaction {
        public Reaction_2N2_O2__2NO2() {
            super( new Reactant[] { new Reactant( 2, new N2() ), new Reactant( 1, new O2() ) }, 
                    new Product[] { new Product( 2, new N2O() ) } );
        }
    }
    
    // P4 + 6H2 -> 4PH3
    public static class Reaction_P4_6H2__4PH3 extends ChemicalReaction {
        public Reaction_P4_6H2__4PH3() {
            super( new Reactant[] { new Reactant( 1, new P4() ), new Reactant( 6, new H2() ) }, 
                    new Product[] { new Product( 4, new PH3() ) } );
        }
    }
    
    // P4 + 6F2 -> 4PF3
    public static class Reaction_P4_6F2__4PF3 extends ChemicalReaction {
        public Reaction_P4_6F2__4PF3() {
            super( new Reactant[] { new Reactant( 1, new P4() ), new Reactant( 6, new F2() ) }, 
                    new Product[] { new Product( 4, new PF3() ) } );
        }
    }
    
    // P4 + 6Cl2 -> 4PCl3
    public static class Reaction_P4_6Cl2__4PCl3 extends ChemicalReaction {
        public Reaction_P4_6Cl2__4PCl3() {
            super( new Reactant[] { new Reactant( 1, new P4() ), new Reactant( 6, new Cl2() ) }, 
                    new Product[] { new Product( 4, new PCl3() ) } );
        }
    }
    
    // P4 + 10Cl2 -> 4PCl5
    public static class Reaction_P4_10Cl2__4PCl5 extends ChemicalReaction {
        public Reaction_P4_10Cl2__4PCl5() {
            super( new Reactant[] { new Reactant( 1, new P4() ), new Reactant( 10, new Cl2() ) }, 
                    new Product[] { new Product( 4, new PCl5() ) } );
        }
    }
    
    // PCl3 + Cl2 -> PCl5
    public static class Reaction_PCl3_Cl2__PCl5 extends ChemicalReaction {
        public Reaction_PCl3_Cl2__PCl5() {
            super( new Reactant[] { new Reactant( 1, new PCl3() ), new Reactant( 1, new Cl2() ) }, 
                    new Product[] { new Product( 1, new PCl5() ) } );
        }
    }
    
    // 2SO2 + O2 -> 2SO3
    public static class Reaction_2SO2_O2__2SO3 extends ChemicalReaction {
        public Reaction_2SO2_O2__2SO3() {
            super( new Reactant[] { new Reactant( 2, new SO2() ), new Reactant( 1, new O2() ) }, 
                    new Product[] { new Product( 2, new SO3() ) } );
        }
    }
}
