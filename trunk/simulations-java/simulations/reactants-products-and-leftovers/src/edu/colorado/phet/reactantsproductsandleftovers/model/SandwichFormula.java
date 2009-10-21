package edu.colorado.phet.reactantsproductsandleftovers.model;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.reactantsproductsandleftovers.module.sandwichshop.SandwichShopDefaults;


public class SandwichFormula {
    
    private final ArrayList<ChangeListener> listeners;
    private int bread, meat, cheese;
    
    public SandwichFormula() {
        this( SandwichShopDefaults.BREAD_COEFFICIENT_RANGE.getDefault(), SandwichShopDefaults.MEAT_COEFFICIENT_RANGE.getDefault(), SandwichShopDefaults.CHEESE_COEFFICIENT_RANGE.getDefault() );
    }
    
    public SandwichFormula( int bread, int meat, int cheese ) {
        this.bread = bread;
        this.meat = meat;
        this.cheese = cheese;
        listeners = new ArrayList<ChangeListener>();
    }
    
    /**
     * Formula is a reaction if more than one coefficient is non-zero,
     * or if any coefficient is > 1.
     * @return
     */
    public boolean isReaction() {
        boolean isReaction = false;
        if ( bread > 1 || cheese > 1 || meat > 1 ) {
            isReaction = true;
        }
        else {
            int nonZero = 0;
            if ( bread > 0 ) {
                nonZero++;
            }
            if ( meat > 0 ) {
                nonZero++;
            }
            if ( cheese > 0 ) {
                nonZero++;
            }
            isReaction = ( nonZero > 1 );
        }
        return isReaction;
    }
    
    public void setBread( int bread ) {
        if ( bread < 0 ) {
            throw new IllegalArgumentException( "bread must be >= 0" );
        }
        if ( bread != this.bread ) {
            this.bread = bread;
            fireStateChanged();
        }
    }
    
    public int getBread() {
        return bread;
    }
    
    public void setMeat( int meat ) {
        if ( meat < 0 ) {
            throw new IllegalArgumentException( "meat must be >= 0" );
        }
        if ( meat != this.meat ) {
            this.meat = meat;
            fireStateChanged();
        }
    }
    
    public int getMeat() {
        return meat;
    }
    
    public void setCheese( int cheese ) {
        if ( cheese < 0 ) {
            throw new IllegalArgumentException( "cheese must be >= 0" );
        }
        if ( cheese != this.cheese ) {
            this.cheese = cheese;
            fireStateChanged();
        }
    }
    
    public int getCheese() {
        return cheese;
    }
    
    public int getSandwiches() {
        return ( bread == 0 && meat == 0 && cheese == 0 ) ? 0 : 1;
    }
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( listener );
    }
    
    private void fireStateChanged() {
        ChangeEvent e = new ChangeEvent( this );
        ArrayList<ChangeListener> listenersCopy = new ArrayList<ChangeListener>( listeners );
        for ( ChangeListener listener : listenersCopy ) {
            listener.stateChanged( e );
        }
    }

}
