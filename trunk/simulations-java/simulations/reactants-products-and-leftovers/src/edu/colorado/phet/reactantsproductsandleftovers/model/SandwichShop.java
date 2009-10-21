package edu.colorado.phet.reactantsproductsandleftovers.model;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class SandwichShop {
    
    private final ArrayList<ChangeListener> listeners;
    private final SandwichFormula formula;
    private int bread, meat, cheese;

    public SandwichShop() {
        listeners = new ArrayList<ChangeListener>();
        formula = new SandwichFormula();
        formula.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireStateChanged();
            }
        } );
    }
    
    public SandwichFormula getFormula() {
        return formula;
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
    
    //XXX need a more general design for determining the limiting reagent and number of products 
    public int getSandwiches() {
        int sandwiches = 0;
        if ( formula.isReaction() ) {
            ArrayList<Integer> possibleValues = new ArrayList<Integer>();
            if ( formula.getBread() != 0 ) {
                possibleValues.add( new Integer( bread / formula.getBread() ) );
            }
            if ( formula.getMeat() != 0 ) {
                possibleValues.add( new Integer( meat / formula.getMeat() ) );
            }
            if ( formula.getCheese() != 0 ) {
                possibleValues.add( new Integer( cheese / formula.getCheese() ) );
            }
            if ( possibleValues.size() > 0 ) {
                Collections.sort( possibleValues );
                sandwiches = possibleValues.get( 0 );
            }
        }
        return sandwiches;
    }
    
    public int getBreadLeftover() {
        return bread - ( getSandwiches() * formula.getBread() );
    }
    
    public int getMeatLeftover() {
        return meat - ( getSandwiches() * formula.getMeat() );
    }
    
    public int getCheeseLeftover() {
        return cheese - ( getSandwiches() * formula.getCheese() );
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
