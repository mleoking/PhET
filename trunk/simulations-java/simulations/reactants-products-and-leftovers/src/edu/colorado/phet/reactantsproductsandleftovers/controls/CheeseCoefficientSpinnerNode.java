package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichFormula;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;


public class CheeseCoefficientSpinnerNode extends IntegerSpinnerNode {
    
    public CheeseCoefficientSpinnerNode( final SandwichFormula formula ) {
        super( SandwichShopDefaults.CHEESE_COEFFICIENT_RANGE );
        setValue( formula.getCheese() );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                formula.setCheese( getValue() );
            }
        });
        formula.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setValue( formula.getCheese() );
            }
        });
    }
}
