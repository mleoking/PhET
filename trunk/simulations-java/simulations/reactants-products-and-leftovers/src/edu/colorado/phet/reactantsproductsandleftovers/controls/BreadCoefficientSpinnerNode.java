package edu.colorado.phet.reactantsproductsandleftovers.controls;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.model.SandwichFormula;
import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;


public class BreadCoefficientSpinnerNode extends IntegerSpinnerNode {
    
    public BreadCoefficientSpinnerNode( final SandwichFormula formula ) {
        super( SandwichShopDefaults.BREAD_COEFFICIENT_RANGE );
        setValue( formula.getBread() );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                formula.setBread( getValue() );
            }
        });
        formula.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setValue( formula.getBread() );
            }
        });
    }
}
