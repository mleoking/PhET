// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactantsproductsandleftovers.model;

import edu.colorado.phet.reactantsproductsandleftovers.RPALStrings;
import edu.colorado.phet.reactantsproductsandleftovers.model.Molecule.*;

/**
 * Collection of two-product chemical reaction subclasses.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TwoProductReactions {
    
    /* not intended for instantiation */
    private TwoProductReactions() {}
    
    // 2C + 2H2O -> CH4 + CO2
    public static class Reaction_2C_2H2O__CH4_CO2 extends ChemicalReaction {
        public Reaction_2C_2H2O__CH4_CO2() {
            super( new Reactant[] { new Reactant( 2, new C() ), new Reactant( 2, new H2O() ) }, 
                    new Product[] { new Product( 1, new CH4() ), new Product( 1, new CO2() ) } );
        }
    }
    
    // CH4 + H2O -> 3H2 + CO
    public static class Reaction_CH4_H2O__3H2_CO extends ChemicalReaction {
        public Reaction_CH4_H2O__3H2_CO() {
            super( new Reactant[] { new Reactant( 1, new CH4() ), new Reactant( 1, new H2O() ) }, 
                    new Product[] { new Product( 3, new H2() ), new Product( 1, new CO() ) } );
        }
    }

    // CH4 + 2 O2 -> CO2 + 2 H2O
    public static class MethaneReaction extends ChemicalReaction {
        public MethaneReaction() {
            super( RPALStrings.RADIO_BUTTON_METHANE, 
                    new Reactant[] { new Reactant( 1, new CH4() ), new Reactant( 2, new O2() ) }, 
                    new Product[] { new Product( 1, new CO2() ), new Product( 2, new H2O() ) } );
        }
    }
    
    // 2C2H6 + 7O2 -> 4CO2 + 6H2O
    public static class Reaction_2C2H6_7O2__4CO2_6H2O extends ChemicalReaction {
        public Reaction_2C2H6_7O2__4CO2_6H2O() {
            super( new Reactant[] { new Reactant( 2, new C2H6() ), new Reactant( 7, new O2() ) }, 
                    new Product[] { new Product( 4, new CO2() ), new Product( 6, new H2O() ) } );
        }
    }
    
    // C2H4 + 3O2 -> 2CO2 + 2H2O
    public static class Reaction_C2H4_3O2__2CO2_2H2O extends ChemicalReaction {
        public Reaction_C2H4_3O2__2CO2_2H2O() {
            super( new Reactant[] { new Reactant( 1, new C2H4() ), new Reactant( 3, new O2() ) }, 
                    new Product[] { new Product( 2, new CO2() ), new Product( 2, new H2O() ) } );
        }
    }
    
    // 2C2H2 + 5O2 -> 4CO2 + 2H2O
    public static class Reaction_2C2H2_5O2__4CO2_2H2O extends ChemicalReaction {
        public Reaction_2C2H2_5O2__4CO2_2H2O() {
            super( new Reactant[] { new Reactant( 2, new C2H2() ), new Reactant( 5, new O2() ) }, 
                    new Product[] { new Product( 4, new CO2() ), new Product( 2, new H2O() ) } );
        }
    }
    
    // C2H5OH + 3O2 -> 2CO2 + 3H2O
    public static class Reaction_C2H5OH_3O2__2CO2_3H2O extends ChemicalReaction {
        public Reaction_C2H5OH_3O2__2CO2_3H2O() {
            super( new Reactant[] { new Reactant( 1, new C2H5OH() ), new Reactant( 3, new O2() ) }, 
                    new Product[] { new Product( 2, new CO2() ), new Product( 3, new H2O() ) } );
        }
    }

    // C2H6 + Cl2 -> C2H5Cl + HCl
    public static class Reaction_C2H6_Cl2__C2H5Cl_HCl extends ChemicalReaction {
        public Reaction_C2H6_Cl2__C2H5Cl_HCl() {
            super( new Reactant[] { new Reactant( 1, new C2H6() ), new Reactant( 1, new Cl2() ) }, 
                    new Product[] { new Product( 1, new C2H5Cl() ), new Product( 1, new HCl() ) } );
        }
    }
    
    // CH4 + 4S -> CS2 + 2H2S
   public static class Reaction_CH4_4S__CS2_2H2S extends ChemicalReaction {
        public Reaction_CH4_4S__CS2_2H2S() {
            super( new Reactant[] { new Reactant( 1, new CH4() ), new Reactant( 4, new S() ) }, 
                    new Product[] { new Product( 1, new CS2() ), new Product( 2, new H2S() ) } );
        }
    }
    
    // CS2 + 3O2 -> CO2 + 2SO2
   public static class Reaction_CS2_3O2__CO2_2SO2 extends ChemicalReaction {
       public Reaction_CS2_3O2__CO2_2SO2() {
           super( new Reactant[] { new Reactant( 1, new CS2() ), new Reactant( 3, new O2() ) }, 
                   new Product[] { new Product( 1, new CO2() ), new Product( 2, new SO2() ) } );
       }
   }

    // 4NH3 + 3O2 -> 2N2 + 6H2O
   public static class Reaction_4NH3_3O2__2N2_6H2O extends ChemicalReaction {
       public Reaction_4NH3_3O2__2N2_6H2O() {
           super( new Reactant[] { new Reactant( 4, new NH3() ), new Reactant( 3, new O2() ) }, 
                   new Product[] { new Product( 2, new N2() ), new Product( 6, new H2O() ) } );
       }
   }
    
    // 4NH3 + 5O2 -> 4NO + 6H2O
   public static class Reaction_4NH3_5O2__4NO_6H2O extends ChemicalReaction {
       public Reaction_4NH3_5O2__4NO_6H2O() {
           super( new Reactant[] { new Reactant( 4, new NH3() ), new Reactant( 5, new O2() ) }, 
                   new Product[] { new Product( 4, new NO() ), new Product( 6, new H2O() ) } );
       }
   }
    
    // 4NH3 + 7O2 -> 4NO2 + 6H2O
   public static class Reaction_4NH3_7O2__4NO2_6H2O extends ChemicalReaction {
       public Reaction_4NH3_7O2__4NO2_6H2O() {
           super( new Reactant[] { new Reactant( 4, new NH3() ), new Reactant( 7, new O2() ) }, 
                   new Product[] { new Product( 4, new NO2() ), new Product( 6, new H2O() ) } );
       }
   }
    
    // 4NH3 + 6NO -> 5N2 + 6H2O
   public static class Reaction_4NH3_6NO__5N2_6H2O extends ChemicalReaction {
       public Reaction_4NH3_6NO__5N2_6H2O() {
           super( new Reactant[] { new Reactant( 4, new NH3() ), new Reactant( 6, new NO() ) }, 
                   new Product[] { new Product( 5, new N2() ), new Product( 6, new H2O() ) } );
       }
   }
    
    // SO2 + 2H2 -> S + 2H2O
   public static class Reaction_SO2_2H2__S_2H2O extends ChemicalReaction {
       public Reaction_SO2_2H2__S_2H2O() {
           super( new Reactant[] { new Reactant( 1, new SO2() ), new Reactant( 2, new H2() ) }, 
                   new Product[] { new Product( 1, new S() ), new Product( 2, new H2O() ) } );
       }
   }
    
    // SO2 + 3H2 -> H2S + 2H2O
   public static class Reaction_SO2_3H2__H2S_2H2O extends ChemicalReaction {
       public Reaction_SO2_3H2__H2S_2H2O() {
           super( new Reactant[] { new Reactant( 1, new SO2() ), new Reactant( 3, new H2() ) }, 
                   new Product[] { new Product( 1, new H2S() ), new Product( 2, new H2O() ) } );
       }
   }

    // 2F2 + H2O -> OF2 + 2HF
   public static class Reaction_2F2_H2O__OF2_2HF extends ChemicalReaction {
       public Reaction_2F2_H2O__OF2_2HF() {
           super( new Reactant[] { new Reactant( 2, new F2() ), new Reactant( 1, new H2O() ) }, 
                   new Product[] { new Product( 1, new OF2() ), new Product( 2, new HF() ) } );
       }
   }
    
    // OF2 + H2O -> O2 + 2HF
   public static class Reaction_OF2_H2O__O2_2HF extends ChemicalReaction {
       public Reaction_OF2_H2O__O2_2HF() {
           super( new Reactant[] { new Reactant( 1, new OF2() ), new Reactant( 1, new H2O() ) }, 
                   new Product[] { new Product( 1, new O2() ), new Product( 2, new HF() ) } );
       }
   }
}
