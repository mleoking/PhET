package edu.colorado.phet.reactantsproductsandleftovers.model;

import java.awt.Image;
import java.util.ArrayList;

/**
 * A substance is a participant in a chemical reaction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class Substance {
    
    private final String name;
    private Image image;
    private int coefficient;
    private int quantity;
    private final ArrayList<SubstanceChangeListener> listeners;

    public Substance( String name, Image image, int coefficient, int quantity ) {
        this.name = name;
        this.image = image;
        this.coefficient = coefficient;
        this.quantity = quantity;
        listeners = new ArrayList<SubstanceChangeListener>();
    }
    
    public String getName() {
        return name;
    }
    
    public Image getImage() {
        return image;
    }
    
    protected void setImage( Image image ) {
        if ( image != this.image ) {
            this.image = image;
            fireImageChanged();
        }
    }
    
    public void setCoefficient( int coefficient ) {
        if ( coefficient < 0 ) {
            throw new IllegalArgumentException( "coefficient must be >= 0: " + coefficient );
        }
        if ( coefficient != this.coefficient ) {
            this.coefficient = coefficient;
            fireCoefficientChanged();
        }
    }
    
    public int getCoefficient() {
        return coefficient;
    }
    
    public void setQuantity( int quantity ) {
        if ( quantity < 0 ) {
            throw new IllegalArgumentException( "quantity must be >= 0: " + quantity );
        }
        if ( quantity != this.quantity ) {
            this.quantity = quantity;
            fireQuantityChanged();
        }
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public interface SubstanceChangeListener {
        public void coefficientChanged();
        public void quantityChanged();
        public void imageChanged();
    }
    
    public static class SubstanceChangeAdapter implements SubstanceChangeListener {
        public void coefficientChanged() {}
        public void quantityChanged() {}
        public void imageChanged() {}
    }
    
    public void addSubstanceChangeListener( SubstanceChangeListener listener ) {
        listeners.add( listener );
    }
    
    public void removeSubstanceChangeListener( SubstanceChangeListener listener ) {
        listeners.remove( listener );
    }
    
    private void fireCoefficientChanged() {
        ArrayList<SubstanceChangeListener> listenersCopy = new ArrayList<SubstanceChangeListener>( listeners ); // avoid ConcurrentModificationException
        for ( SubstanceChangeListener listener : listenersCopy ) {
            listener.coefficientChanged();
        }
    }
    
    private void fireQuantityChanged() {
        ArrayList<SubstanceChangeListener> listenersCopy = new ArrayList<SubstanceChangeListener>( listeners ); // avoid ConcurrentModificationException
        for ( SubstanceChangeListener listener : listenersCopy ) {
            listener.quantityChanged();
        }
    }
    
    private void fireImageChanged() {
        ArrayList<SubstanceChangeListener> listenersCopy = new ArrayList<SubstanceChangeListener>( listeners ); // avoid ConcurrentModificationException
        for ( SubstanceChangeListener listener : listenersCopy ) {
            listener.imageChanged();
        }
    }
}
