package edu.colorado.phet.reactantsproductsandleftovers.model;

import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class SandwichFormula {
    
    private final ArrayList<ChangeListener> listeners;
    private int bread, meat, cheese;
    
    public SandwichFormula() {
        this( 2, 1, 1 );
    }
    
    public SandwichFormula( int bread, int meat, int cheese ) {
        this.bread = bread;
        this.meat = meat;
        this.cheese = cheese;
        listeners = new ArrayList<ChangeListener>();
    }
    
    public void setBread( int bread ) {
        if ( bread < 0 ) {
            throw new IllegalArgumentException( "bread must be > 0" );
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
            throw new IllegalArgumentException( "meat must be > 0" );
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
            throw new IllegalArgumentException( "cheese must be > 0" );
        }
        if ( cheese != this.cheese ) {
            this.cheese = cheese;
            fireStateChanged();
        }
    }
    
    public int getCheese() {
        return cheese;
    }
    
    public void addChangeListener( ChangeListener listener ) {
        listeners.add( listener );
    }
    
    public void removeChangeListener( ChangeListener listener ) {
        listeners.remove( listener );
    }
    
    private void fireStateChanged() {
        ChangeEvent e = new ChangeEvent( this );
        for ( ChangeListener listener : listeners ) {
            listener.stateChanged( e );
        }
    }

}
