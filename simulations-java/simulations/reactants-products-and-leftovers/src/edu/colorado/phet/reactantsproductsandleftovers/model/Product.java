package edu.colorado.phet.reactantsproductsandleftovers.model;

import edu.umd.cs.piccolo.PNode;

/**
 * Chemical reactions yield one or more products, which have properties different from the reactants.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Product extends Substance {

    public Product( String name, PNode node, int coefficient, int quantity ) {
        super( name, node, coefficient, quantity );
    }
    
    public interface ProductChangeListener extends SubstanceChangeListener {}
    
    public static class ProductChangeAdapter extends SubstanceChangeAdapter implements ProductChangeListener {}
    
    public void addProductChangeListener( ProductChangeListener listener ) {
        addSubstanceChangeListener( listener );
    }
    
    public void removeProductChangeListener( ProductChangeListener listener ) {
        removeSubstanceChangeListener( listener );
    }
}
