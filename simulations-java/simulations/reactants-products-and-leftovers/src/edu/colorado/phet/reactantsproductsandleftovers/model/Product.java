/* Copyright 2010, University of Colorado */

package edu.colorado.phet.reactantsproductsandleftovers.model;

import java.awt.Image;

/**
 * Chemical reactions yield one or more products, which have properties different from the reactants.
 * This class is final and cannot be extended in order to support newInstance.
 * This approach is an alternative to the evils of implementing clone.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public final /* yes, final, see javadoc! */ class Product extends Substance {

    public Product( String name, Image image, int coefficient, int quantity ) {
        super( name, image, coefficient, quantity );
    }
    
    /**
     * Copy constructor.
     * @param product
     */
    public Product( Product product ) {
        this( product.getName(), product.getImage(), product.getCoefficient(), product.getQuantity() );
        // listeners are not copied.
    }
    
    /**
     * Factory method to create a new instance.
     * @param product
     * @return
     */
    public static Product newInstance( Product product ) {
        return new Product( product );
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
