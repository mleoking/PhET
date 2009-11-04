package edu.colorado.phet.reactantsproductsandleftovers.model;

import java.awt.Image;

/**
 * Chemical reactions yield one or more products, which have properties different from the reactants.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Product extends Substance {

    public Product( String name, Image image, int coefficient, int quantity ) {
        super( name, image, coefficient, quantity );
    }
    
    // marker interface
    public interface ProductChangeListener extends SubstanceChangeListener {}
    
    // marker adapter
    public static class ProductChangeAdapter extends SubstanceChangeAdapter implements ProductChangeListener {}
    
    public void addProductChangeListener( ProductChangeListener listener ) {
        addSubstanceChangeListener( listener );
    }
    
    public void removeProductChangeListener( ProductChangeListener listener ) {
        removeSubstanceChangeListener( listener );
    }
}
