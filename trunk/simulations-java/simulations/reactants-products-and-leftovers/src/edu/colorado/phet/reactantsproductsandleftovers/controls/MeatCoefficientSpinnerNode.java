package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichFormula;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;


public class MeatCoefficientSpinnerNode extends IntegerSpinnerNode {
    
    public MeatCoefficientSpinnerNode( final SandwichFormula formula ) {
        super( SandwichShopDefaults.MEAT_COEFFICIENT_RANGE );
        setValue( formula.getMeat() );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                formula.setMeat( getValue() );
            }
        });
        formula.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setValue( formula.getMeat() );
            }
        });
    }
}
