package edu.colorado.phet.reactantsproductsandleftovers.model;

import java.util.ArrayList;
import java.util.Collections;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class SandwichShop {
    
    private final ArrayList<ChangeListener> listeners;
    private final SandwichFormula sandwichFormula;
    private int bread, meat, cheese;

    public SandwichShop() {
        listeners = new ArrayList<ChangeListener>();
        sandwichFormula = new SandwichFormula();
        sandwichFormula.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                fireStateChanged();
            }
        } );
    }
    
    public SandwichFormula getSandwichFormula() {
        return sandwichFormula;
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
        ArrayList<Integer> nonZeroCoefficients = new ArrayList<Integer>();
        ArrayList<Integer> possibleValues = new ArrayList<Integer>();
        if ( sandwichFormula.getBread() != 0 ) {
            nonZeroCoefficients.add( new Integer( sandwichFormula.getBread() ) );
            possibleValues.add( new Integer( bread / sandwichFormula.getBread() ) );
        }
        if ( sandwichFormula.getMeat() != 0 ) {
            nonZeroCoefficients.add( new Integer( sandwichFormula.getMeat() ) );
            possibleValues.add( new Integer( meat / sandwichFormula.getMeat() ) );
        }
        if ( sandwichFormula.getCheese() != 0 ) {
            nonZeroCoefficients.add( new Integer( sandwichFormula.getCheese() ) );
            possibleValues.add( new Integer( cheese / sandwichFormula.getCheese() ) );
        }
        // more than 2 reactants or at least 1 reactant with coefficient >= 2 is required to create a product
        if ( nonZeroCoefficients.size() > 1 || ( nonZeroCoefficients.size() == 1 && nonZeroCoefficients.get( 0 ).intValue() > 1 ) ) {
            Collections.sort( possibleValues );
            sandwiches = possibleValues.get( 0 );
        }
        return sandwiches;
    }
    
    public int getBreadLeftover() {
        return bread - ( getSandwiches() * sandwichFormula.getBread() );
    }
    
    public int getMeatLeftover() {
        return meat - ( getSandwiches() * sandwichFormula.getMeat() );
    }
    
    public int getCheeseLeftover() {
        return cheese - ( getSandwiches() * sandwichFormula.getCheese() );
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
