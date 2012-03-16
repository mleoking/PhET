// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.reactantsproductsandleftovers.model;


/**
 * Chemical reactions yield one or more products, which have properties different from the reactants.
 * <p>
 * This class is final and cannot be extended in order to support newInstance, which is needed 
 * for creating a Game "guess" for a specific reaction.
 * This approach is an alternative to the evils of implementing clone.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public final /* yes, final, see javadoc! */ class Product extends Substance {

    public Product( int coefficient, Molecule molecule ) {
        this( coefficient, molecule, 0 /* quantity */ );
    }
    
    public Product( int coefficient, Molecule molecule, int quantity ) {
        super( coefficient, molecule, quantity );
    }
    
    /**
     * Copy constructor.
     * @param product
     */
    public Product( Product product ) {
        this( product.getCoefficient(), product.getMolecule(), product.getQuantity() );
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
